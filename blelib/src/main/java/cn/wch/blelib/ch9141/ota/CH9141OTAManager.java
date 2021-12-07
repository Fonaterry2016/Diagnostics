package cn.wch.blelib.ch9141.ota;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import cn.wch.blelib.ch9141.constant.Constant;
import cn.wch.blelib.ch9141.ota.callback.ConnectStatus;
import cn.wch.blelib.ch9141.ota.callback.IProgress;
import cn.wch.blelib.ch9141.ota.command.CommandUtil;
import cn.wch.blelib.ch9141.ota.entry.CurrentImageInfo;
import cn.wch.blelib.ch9141.ota.entry.ImageType;
import cn.wch.blelib.ch9141.ota.exception.CH9141OTAException;
import cn.wch.blelib.ch9141.ota.util.FileParseUtil;
import cn.wch.blelib.ch9141.ota.util.ParseUtil;
import cn.wch.blelib.exception.BLELibException;
import cn.wch.blelib.host.core.BLEHostManager;
import cn.wch.blelib.host.core.ConnRuler;
import cn.wch.blelib.host.core.Connection;
import cn.wch.blelib.host.core.Connector;
import cn.wch.blelib.host.core.callback.ConnectCallback;
import cn.wch.blelib.host.core.callback.MtuCallback;
import cn.wch.blelib.utils.LogUtil;


/**
 * CH914X升级只支持加密的BIN文件
 * 判断文件数组第17字节是否与当前image类型相对应
 */
public class CH9141OTAManager {
    private Application application;
    private static CH9141OTAManager ch9141OTAManager;

    //连接以及数据传输
    private BLEHostManager bleHostManager;

    private BluetoothGattCharacteristic mConfig;

    private Connection connection;

    //OTA
    private static final int OTA_BLOCK_SIZE = 512;

    private boolean stopFlag=false;
    private IProgress progress;

    public static CH9141OTAManager getInstance(){
        if(ch9141OTAManager ==null){
            synchronized (CH9141OTAManager.class){
                ch9141OTAManager =new CH9141OTAManager();
            }
        }
        return ch9141OTAManager;
    }

    public void init(@NonNull Application application) throws BLELibException {
        this.application=application;
        bleHostManager = BLEHostManager.getInstance(application);
        bleHostManager.init(application);
    }

    /**
     * 创建文件夹，放置image升级文件
     */
    public boolean createFolder() {
        File otaDir = application.getExternalFilesDir(Constant.OTA_FOLDER);
        File imageA=new File(otaDir, Constant.OTA_FOLDER_IMAGE_A);
        File imageB=new File(otaDir, Constant.OTA_FOLDER_IMAGE_B);
        if(!imageA.exists()){
            imageA.mkdirs();
        }
        if(!imageB.exists()){
            imageB.mkdirs();
        }
        return imageA.exists() && imageB.exists();
    }


    /**
     * 根据MAC地址连接蓝牙
     * @param mac MAC地址
     * @param timeout 连接超时时间，单位为ms
     * @param connectStatus 连接状态回调
     */
    public synchronized void connect(String mac, long timeout, final ConnectStatus connectStatus) throws BLELibException{

        if(TextUtils.isEmpty(mac) || !BluetoothAdapter.checkBluetoothAddress(mac)){
            throw new BLELibException("MAC address is invalid");
        }
        if(timeout<=0){
            throw new BLELibException("Timeout should more than 0 ");
        }
        if(connectStatus==null){
            throw new BLELibException("connectStatus is null");
        }
        if(bleHostManager==null){
            throw new BLELibException("BleHostManager is null, do you invoke method CH9141OTAManager$init() first?");
        }
        ConnRuler ruler = new ConnRuler.Builder().MAC(mac).ConnectTimeout(timeout).writeTimeout(2000).readTimeout(2000).build();
        bleHostManager.asyncConnect(ruler, new ConnectCallback() {
            @Override
            public void OnError(String mac, Throwable t) {
                connectStatus.OnError(t);
            }

            @Override
            public void OnConnecting(String mac) {
                connectStatus.OnConnecting();
            }

            @Override
            public void OnConnectSuccess(String mac, Connection conn) {
                connection=conn;
            }

            @Override
            public void OnDiscoverService(String mac, List<BluetoothGattService> list) {
                if(getCharacteristic(list)){
                    //一些初始化操作
                    connectStatus.OnConnectSuccess(mac);

                }else {
                    LogUtil.d("不是目标设备");
                    connectStatus.onInvalidDevice(mac);
                }
            }

            @Override
            public void OnConnectTimeout(String mac) {
                try {
                    disconnect(mac,true);
                } catch (BLELibException e) {
                    e.printStackTrace();
                }
                connectStatus.OnConnectTimeout(mac);

            }

            @Override
            public void OnDisconnect(String mac, BluetoothDevice bluetoothDevice, int status) {
                connection=null;
                close();
                connectStatus.OnDisconnect(mac,status);
            }
        });
    }

    /**
     * 主动断开蓝牙连接
     * @param force 是否强制断开：true 强制断开释放资源，可能不会有连接状态(ConnectStatus)断开的回调；false 正常断开，一般会产生回调
     */
    public synchronized void disconnect(String mac,boolean force) throws BLELibException {
        if(TextUtils.isEmpty(mac) || !BluetoothAdapter.checkBluetoothAddress(mac)){
            throw new BLELibException("MAC address is invalid");
        }
        if(bleHostManager==null){
            throw new BLELibException("BleHostManager is null, do you invoke method CH9141BluetoothManager$init() first?");
        }
        bleHostManager.disconnect(mac);
        if(force){
            bleHostManager.close(mac);
            close();
        }

    }
    /**
     * 通过蓝牙写数据
     * （这是一个耗时操作，避免在主线程执行）
     * @param data 数据数组
     * @param length  写数组的长度，从data[0]开始
     * @return 该值为负 发送出错；不为负 发送成功的数据长度
     */
    public synchronized int write(@NonNull byte[] data, int length){
        int total=0;
        long packetInterval=0;
        if(connection==null){
            return -1;
        }
        Connector connector = connection.getConnector();
        if(connector==null || mConfig==null || packetInterval<0){
            return -1;
        }
        if((mConfig.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE)==0 && (mConfig.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)==0){
            return -2;
        }
        if(data.length==0 || length==0){
            return 0;
        }
        int packetLen=connector.getMax_packet();
        int fullCount=Math.min(length,data.length)/packetLen;
        for(int i=0;i<fullCount;i++){
            byte[] tmp=new byte[packetLen];
            System.arraycopy(data,i*packetLen,tmp,0,packetLen);
            if(!syncWriteCharacteristic( connector,mConfig,tmp)) {
                return total;
            }
            total+=tmp.length;
            if(i==(fullCount-1) && data.length%packetLen==0){
                break;
            }
            try {
                Thread.sleep(packetInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        byte[] tmp=new byte[Math.min(length,data.length)%packetLen];
        if(tmp.length!=0) {
            System.arraycopy(data, fullCount * packetLen, tmp, 0, tmp.length);
            if (!syncWriteCharacteristic(connector,mConfig, tmp)) {
                return total;
            }
            LogUtil.d("final write"+tmp.length);
            total+=tmp.length;
            try {
                Thread.sleep(packetInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    /**
     *此函数是单包发送的函数
     * @return 表示发送成功或者失败
     */
    private boolean syncWriteCharacteristic(Connector connector,BluetoothGattCharacteristic characteristic, byte[] tmp){
        LogUtil.d("单包发送");
        //DebugUtil.getInstance().write(tmp);
        return connector.syncWriteCharacteristic(characteristic, tmp);
    }

    /**
     *用于蓝牙配置，写完数据后，读取一定长度的数据
     * @param writeData
     * @return
     */
    public byte[] spWRCharacteristic(@NonNull byte[] writeData,int readNum){
        if(connection==null){
            return null;
        }
        return connection.spWRCharacteristic(mConfig, writeData, mConfig, readNum,0);
    }

    /**
     *用于蓝牙配置，写完数据后,读取一包数据
     * @param writeData
     * @return
     */
    public byte[] spWRCharacteristic(@NonNull byte[] writeData){
        if(connection==null){
            return null;
        }
        //DebugUtil.getInstance().write(writeData);
        //2021/03/16修改1000->5000
        return connection.spWRCharacteristic(mConfig, writeData, mConfig,5000);
    }

    private byte[] readResponse(){
        if(connection==null || mConfig==null){
            return null;
        }
        return connection.read(mConfig,false);
    }
    /**
     * 释放资源
     */
    private void close(){
        connection=null;
        mConfig=null;
    }

    public boolean isConnected(String mac){
        return connection!=null  && mConfig!=null && bleHostManager!=null && bleHostManager.isConnected(mac);
    }

    /**
     * 设置MTU
     * @param mtu 希望设置的MTU大小
     * @throws BLELibException
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setMtu(int mtu, MtuCallback mtuCallback)throws BLELibException{
        if(connection==null){
            throw new BLELibException("Connection is null,BT is disconnected");
        }
        if(mtu<23){
            throw new BLELibException("MTU should more than 23");
        }
        if(mtuCallback==null){
            throw new BLELibException("MtuCallback is null");
        }
        connection.setMtu(mtu,mtuCallback);
    }

    /**
     * 根据特征值列表，枚举目标特征值
     * @param list 特征值列表
     * @return true 枚举到全部特征值；false 未枚举到全部特征值
     */
    private boolean getCharacteristic(List<BluetoothGattService> list) {
        mConfig=null;
        for (BluetoothGattService service : list) {
            if (service.getUuid().toString().equalsIgnoreCase(Constant.OTA_ServiceUUID)) {
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    String s = characteristic.getUuid().toString();
                    if (s.equalsIgnoreCase(Constant.OTA_CharacterUUID)) {
                        mConfig=characteristic;
                        mConfig.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    }
                }
            }
        }
        LogUtil.d("getCharacteristic end");
        return mConfig!=null;
    }


    /****************************************OTA*******************************************/
    /**
     * 获取当前硬件image信息
     * @return 当前image信息
     */
    public CurrentImageInfo getCurrentImageInfo(){
        LogUtil.d("try-->getCurrentImageInfo");
        if(connection==null || mConfig==null){
            return null;
        }
        byte[] response = connection.spWRCharacteristic(mConfig, CommandUtil.getImageInfoCommand(), mConfig,0);
        return ParseUtil.parseImageFromResponse(response);
    }

    @RequiresPermission(anyOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    public void start(@NonNull File file, @NonNull CurrentImageInfo currentImageInfo, IProgress progress) throws IOException, CH9141OTAException {
        stopFlag=false;
        this.progress=progress;
        //image信息
        LogUtil.d("开始升级固件： "+file.getAbsolutePath());
        int startAddr=0;
        if(currentImageInfo.getType()== ImageType.A){
            startAddr=currentImageInfo.getOffset();
        }else if(currentImageInfo.getType()== ImageType.B){
            startAddr=0;
        }else if(currentImageInfo.getType()== ImageType.C){
            startAddr=0;
        }else {
            throw new CH9141OTAException("CurrentImageInfo illegal");
        }

        LogUtil.d("image type "+currentImageInfo.getType().toString());
        //读取文件
        LogUtil.d("读取文件");
        ByteBuffer byteBuffer=null;
        if(file.getName().endsWith(".bin") || file.getName().endsWith(".BIN")){
            byteBuffer= FileParseUtil.parseBinFile(file);
        }else if(file.getName().endsWith(".hex") || file.getName().endsWith(".HEX")){
            throw new CH9141OTAException("CH914X don't support hex image file");
        }else {
            throw new CH9141OTAException("CH914X only support  bin image file");
        }



        if(byteBuffer==null){
            throw new CH9141OTAException("parse file fail!");
        }
        LogUtil.d("byteBuffer  capacity: "+byteBuffer.capacity());
        int total=  byteBuffer.capacity();
        LogUtil.d("total size: "+total);
        //读取文件的offset
        LogUtil.d("解析文件");

        //检查Image合法性
        if(!checkImagelegal(currentImageInfo,byteBuffer)){
            throw new CH9141OTAException("image file is illegal!");
        }
        //block 需要减去前面512字节
        int nBlocks = (((total-OTA_BLOCK_SIZE)/ OTA_BLOCK_SIZE +1));
        LogUtil.d("image nBlocks: "+(nBlocks & 0xffff));

        if(progress!=null){
            progress.onEraseStart();
        }
        //开始擦除
        LogUtil.d("start erase... ");
        LogUtil.d("startAddr: "+startAddr);
        LogUtil.d("nBlocks: "+nBlocks);
        byte[] bytes = spWRCharacteristic(CommandUtil.getEraseCommand(startAddr, nBlocks));
        if(!ParseUtil.parseEraseResponse(bytes)){
            LogUtil.d("erase fail!");
            if(progress!=null){
                progress.onError("erase fail!");
            }
            return;
        }else {
            LogUtil.d("erase success!");
            if(progress!=null){
                progress.onEraseFinish();
            }
        }
        if(progress!=null){
            progress.onProgramStart();
        }
        byte[] realBuffer = byteBuffer.array();
        //开始编程
        LogUtil.d("start program... ");
        //加密部分，去除头部512字节无用信息
        int offset=512;
        while (offset<realBuffer.length){
            if(checkStopFlag()){
                return;
            }
            //有效数据的长度
            int programmeLength = CommandUtil.getProgrammeLength(realBuffer, offset);
            byte[] programmeCommand = CommandUtil.getProgrammeCommand(offset + startAddr-512, realBuffer, offset);
            if(write(programmeCommand,programmeCommand.length)!=programmeCommand.length){
                if(progress!=null){
                    progress.onError("program fail!");
                }
                return;
            }
            offset+=programmeLength;
            LogUtil.d("progress: "+offset+"/"+realBuffer.length);
            if(progress!=null){
                progress.onProgramProgress(offset,realBuffer.length);
            }
        }
        LogUtil.d("program complete! ");
        if(progress!=null){
            progress.onProgramFinish();
        }
        //开始校验
        if(progress!=null){
            progress.onVerifyStart();
        }
        LogUtil.d("start verify... ");
        int vIndex=512;
        while (vIndex<realBuffer.length){
            if(checkStopFlag()){
                return;
            }
            int verifyLength = CommandUtil.getVerifyLength(realBuffer, vIndex);
            byte[] verifyCommand = CommandUtil.getVerifyCommand(vIndex + startAddr-512, realBuffer, vIndex);
            if(write(verifyCommand,verifyCommand.length)!=verifyCommand.length){
                if(progress!=null){
                    progress.onError("verify fail!");
                }
                return;
            }
            vIndex+=verifyLength;
            LogUtil.d("progress: "+vIndex+"/"+realBuffer.length);
            if(progress!=null){
                progress.onVerifyProgress(vIndex,realBuffer.length);
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] bytes1 = readResponse();
        if(!ParseUtil.parseVerifyResponse(bytes1)){
            if(progress!=null){
                progress.onError("verify fail!");
            }
            return;
        }
        LogUtil.d("verify complete! ");
        if(progress!=null){
            progress.onVerifyFinish();
        }
        //结束
        LogUtil.d("start ending... ");
        byte[] endCommand = CommandUtil.getEndCommand();

        if(endCommand.length!=write(endCommand, endCommand.length)){
            if(progress!=null){
                progress.onError("ending fail!");
            }
            return;
        }else {
            LogUtil.d("ending success!");
            if(progress!=null){
                progress.onEnd();
            }
        }
    }

    public static  boolean checkImagelegal(CurrentImageInfo imageInfo, ByteBuffer byteBuffer){
        if(byteBuffer.capacity()<(17+512)){
            return false;
        }
        byte b=byteBuffer.get(16);
        if((imageInfo.getType()== ImageType.A && b==(byte)0x42) || (imageInfo.getType()== ImageType.B && b==(byte)0x41)
            || (imageInfo.getType()==ImageType.C && b==(byte)0x43)){
            return true;
        }
        return false;
    }

    public void cancel(){
        stopFlag=true;
    }

    private boolean checkStopFlag(){
        if(stopFlag){
            if(progress!=null){
                progress.onCancel();
            }
            return true;
        }
        return false;
    }

}
