package cn.wch.blelib.ch9141;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import cn.wch.blelib.ch9141.callback.ConnectStatus;
import cn.wch.blelib.ch9141.callback.ModemStatus;
import cn.wch.blelib.ch9141.callback.NotifyStatus;
import cn.wch.blelib.ch9141.callback.RSSIStatus;
import cn.wch.blelib.ch9141.callback.ScanResult;
import cn.wch.blelib.ch9141.command.AckParseUtil;
import cn.wch.blelib.ch9141.command.CommandUtil;
import cn.wch.blelib.ch9141.command.ModemParse;
import cn.wch.blelib.ch9141.constant.Constant;
import cn.wch.blelib.exception.BLELibException;
import cn.wch.blelib.host.core.BLEHostManager;
import cn.wch.blelib.host.core.ConnRuler;
import cn.wch.blelib.host.core.Connection;
import cn.wch.blelib.host.core.Connector;
import cn.wch.blelib.host.core.callback.ConnectCallback;
import cn.wch.blelib.host.core.callback.MtuCallback;
import cn.wch.blelib.host.core.callback.NotifyDataCallback;
import cn.wch.blelib.host.core.callback.ReadCallback;
import cn.wch.blelib.host.core.callback.RssiCallback;
import cn.wch.blelib.host.scan.BLEScanUtil;
import cn.wch.blelib.host.scan.BLEScanUtil2;
import cn.wch.blelib.host.scan.ScanObserver;
import cn.wch.blelib.host.scan.ScanRuler;
import cn.wch.blelib.utils.LogUtil;

//CH573没有固定广播包，不需要过滤

public class CH9141BluetoothManager {
    private Application application;
    private static CH9141BluetoothManager ch9141BluetoothManager;

    //扫描相关
    private ScanFilter filter;
    private ScanRuler scanRuler;

    //连接以及数据传输
    private BLEHostManager bleHostManager;

    private BluetoothGattCharacteristic mWrite;
    private BluetoothGattCharacteristic mRead;
    private BluetoothGattCharacteristic mNotify;
    private BluetoothGattCharacteristic mConfig;

    private Connection connection;

    //默认可靠传输
    private boolean reliable=true;
    //当可靠传输时，发送前应该检查该标志
    private static volatile boolean flag=true;
    //rssi
    private RSSIStatus rssiStatus;
    //Modem状态通知
    private ModemStatus modemStatus;
    //上报数据通知
    private NotifyStatus readNotify;

    private RssiCallback rssiCallback=new RssiCallback() {
        @Override
        public void onReadRemoteRssi(int rssi, int status) {
            if(rssiStatus!=null){
                rssiStatus.onRSSI(rssi,status);
            }
        }
    };

    private NotifyDataCallback notifyDataCallback=new NotifyDataCallback() {
        @Override
        public void OnError(String mac, Throwable t) {
            LogUtil.d(t.getMessage());
        }

        @Override
        public void OnData(String mac, byte[] data) {
            if(readNotify!=null){
                readNotify.onData(data);
            }
        }
    };

    private boolean stopFlag=false;

    private Handler handler=new Handler(Looper.getMainLooper());

    /**
     * 用于创建实例
     * @return 返回全局唯一单例
     */
    public static CH9141BluetoothManager getInstance() {
        if(ch9141BluetoothManager ==null){
            synchronized (CH9141BluetoothManager.class){
                ch9141BluetoothManager =new CH9141BluetoothManager();
            }
        }
        return ch9141BluetoothManager;
    }

    /**
     * 初始化上下文，在自定义Application中使用
     * @param application 全局上下文
     */
    public void init(@NonNull Application application) throws BLELibException {
        this.application=application;
        //在使用BLEScanUtil2$startScan()方法是需要使用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            filter = new ScanFilter.Builder().setManufacturerData(0xAB57, new byte[]{0x07, 0x01, 0x43, 0x48, 0x39, 0x31, 0x34, 0x31}).build();
            ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
            BLEScanUtil2.getInstance(application).initScanPolicy(scanSettings, filter);
        }

        bleHostManager = BLEHostManager.getInstance(application);
        bleHostManager.init(application);
    }

    private void forceReInitScanPolicy(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //CH573没有固定广播包，不需要过滤
            filter = new ScanFilter.Builder().setManufacturerData(0xAB57, new byte[]{0x07, 0x01, 0x43, 0x48, 0x39, 0x31, 0x34, 0x31}).build();
            ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
            BLEScanUtil2.getInstance(application).initScanPolicy(scanSettings, filter);
        }
    }


    /**
     * 开始枚举附近设备
     * @param enumResult 枚举结果
     * @throws BLELibException
     */
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION})
    public synchronized void startScan(@NonNull final ScanResult enumResult)throws BLELibException{
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            startScan(new ScanObserver() {
                @Override
                public void OnScanDevice(BluetoothDevice bluetoothDevice, int rssi, byte[] broadcastRecord) {
                    if(enumResult !=null){
                        enumResult.onResult(bluetoothDevice,rssi,broadcastRecord);
                    }
                }
            });
        }else {
            startScan(new ScanObserver() {
                @Override
                public void OnScanDevice(BluetoothDevice bluetoothDevice, int rssi, byte[] broadcastRecord) {
                    if(enumResult !=null){
                        enumResult.onResult(bluetoothDevice,rssi,broadcastRecord);
                    }
                }
            });
            /*startScan(new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
                    if(enumResult !=null){
                        enumResult.onResult(result.getDevice(),result.getRssi(),result.getScanRecord()==null ? null:result.getScanRecord().getBytes());
                    }
                }
            });*/
        }
    }

    /**
     *开始扫描(适用于Android 5.0及以上)
     * @param scanCallback 扫描回调
     */


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION})
    private void startScan(@NonNull ScanCallback scanCallback)throws BLELibException {
        forceReInitScanPolicy();
        if(application==null){
            throw new BLELibException("Application is null, do you invoke method CH9141BluetoothManager$init() first?");
        }
        BLEScanUtil2.getInstance(application).startLeScan(scanCallback);
    }

    /**
     * 开始扫描(适用于Android 4.4及以上)
     * @param scanObserver 扫描回调
     */
    @RequiresApi(api =Build.VERSION_CODES.KITKAT)
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION})
    private void startScan(ScanObserver scanObserver) throws BLELibException {
        ScanRuler scanRuler=new ScanRuler.Builder().ScanRecord(new byte[]{0x57, (byte) 0xab,0x07,0x01,0x43,0x48,0x39,0x31,0x34,0x31}).union(true).build();
        //ScanRuler scanRuler=new ScanRuler.Builder().union(true).build();
        BLEScanUtil.getInstance().startScan(scanRuler, scanObserver);
    }


    /**
     * 停止扫描
     */
    public  void stopScan() throws BLELibException{
        if(application==null){
            throw new BLELibException("Application is null, do you invoke method CH9141BluetoothManager$init() first?");
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            /*BLEScanUtil2.getInstance(application).stopLeScan();*/
            BLEScanUtil.getInstance().stopScan();
        }else {
            BLEScanUtil.getInstance().stopScan();
        }
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
            throw new BLELibException("BleHostManager is null, do you invoke method CH9141BluetoothManager$init() first?");
        }
        ConnRuler ruler = new ConnRuler.Builder().MAC(mac).ConnectTimeout(timeout).build();
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
                    if(isHardwareOld()){
                        //如果该硬件是老版本，不支持可靠传输,连接后置为false
                        setTransmissionReliability(false);
                    }else {
                        //如果该硬件是新版本，可以支持可靠传输,连接后默认为true
                        setTransmissionReliability(true);
                    }
                    //缓存区标志置true
                    flag=true;
                    openRSSINotify();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            openModemNotify();
                        }
                    });
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
            throw new BLELibException("BleHostManager is null, do you invoke method CH9143BluetoothManager$init() first?");
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
        //发送间隔 如果数据长度大于最大包长，自动分包，此为每一包间的发送间隔。如果不需要可以直接填0
        long packetInterval=0;
        //等待缓冲区标志次数 当处于可靠传输模式时该值有效，需要发送前等待缓冲区状态标志位置true，当前线程每隔1ms检查一次
        int waitFlagCount=5000;
        stopFlag=false;
        LogUtil.d("发送前将发送标志置false-->");
        if(connection==null){
            return -1;
        }
        Connector connector = connection.getConnector();
        if(connector==null || mWrite==null || packetInterval<0 || length<0){
            return -1;
        }
        if((mWrite.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE)==0 && (mWrite.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)==0){
            return -2;
        }
        if(data.length==0 || length==0){
            return 0;
        }
        int packetLen=connector.getMax_packet();
        LogUtil.d("当前最大包长："+packetLen);
        int fullCount=Math.min(length,data.length)/packetLen;
        for(int i=0;i<fullCount;i++){
            byte[] tmp=new byte[packetLen];
            System.arraycopy(data,i*packetLen,tmp,0,packetLen);
            if(!syncWriteCharacteristic( connector,mWrite,tmp,waitFlagCount)) {
                return total;
            }
            total+=tmp.length;
            if(i==(fullCount-1) && data.length%packetLen==0){
                break;
            }

        }
        byte[] tmp=new byte[Math.min(length,data.length)%packetLen];
        if(tmp.length!=0) {
            System.arraycopy(data, fullCount * packetLen, tmp, 0, tmp.length);
            if (!syncWriteCharacteristic(connector,mWrite, tmp,waitFlagCount)) {
                return total;
            }
            LogUtil.d("final write "+tmp.length);
            total+=tmp.length;
        }
        return total;
    }

    /**
     * 取消发送
     */
    public void stopWrite(){
        stopFlag=true;
        LogUtil.d("stopWrite置位true-->");
    }

    /**
     *此函数是单包发送的函数
     * @return 表示发送成功或者失败
     */
    private boolean syncWriteCharacteristic(Connector connector,BluetoothGattCharacteristic characteristic, byte[] tmp,long waitIdle){
        LogUtil.d("单包发送");
        int count=0;
        while (count<=waitIdle){
            if(stopFlag){
                LogUtil.d("stopFlag当前为true,取消发送！");
                return false;
            }
            if(connector==null){
                return false;
            }
            /*if(reliable){
                if(!flag){
                    //LogUtil.d("等待标志位："+count);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count++;
                    continue;
                }else {
                    LogUtil.d("标志位有效，开始执行");
                    flag=false;
                }
            }*/
            if(!flag){
                //LogUtil.d("等待标志位："+count);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
                continue;
            }else {
                LogUtil.d("标志位有效，开始执行");
                if(reliable){
                    flag=false;
                }
            }
            if(!reliable){
                int interval=0;
                try {
                    int mtu = getMTU();
                    interval=mtu/100;
                } catch (BLELibException e) {
                    e.printStackTrace();
                }
                if(interval>0){
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return connector.syncWriteCharacteristic(characteristic, tmp);
        }
        //超时仍然没有等待到标志位，将flag置true,返回false,
        flag=true;
        LogUtil.d("flag一直为false，本次发送失败，且flag强制置true");
        return false;
    }

    /**
     * 获取当前MTU
     * @return 返回当前MTU
     * @throws BLELibException
     */
    private int getMTU()throws BLELibException{
        if(connection==null){
            throw new BLELibException("Connection is null,BT is disconnected");
        }
        return connection.getMtu();
    }


    /**
     * 注册串口状态以及Modem状态的通知
     * @param modemNotify 通知回调,为null可以取消回调
     */
    public void registerSerialModemNotify(ModemStatus modemNotify){
        this.modemStatus=modemNotify;
    }

    /**
     * 释放资源
     */
    private void close(){
        closeRSSINotify();
        readNotify=null;
        modemStatus=null;
        connection=null;
        mWrite=null;
        mRead=null;
        mNotify=null;
        mConfig=null;
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
        mNotify=null;
        mRead=null;
        mWrite=null;
        mConfig=null;
        for (BluetoothGattService service : list) {
            if (service.getUuid().toString().equalsIgnoreCase(Constant.ServiceUUID)) {
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    String s = characteristic.getUuid().toString();
                    if (s.equalsIgnoreCase(Constant.RWCharacterUUID)) {
                        mConfig=characteristic;

                        //characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

                    }else if (s.equalsIgnoreCase(Constant.ReadCharacterUUID)) {
                        mRead=characteristic;
                        mNotify=characteristic;

                    }else if (s.equalsIgnoreCase(Constant.WriteCharacterUUID)) {
                        mWrite=characteristic;
                        //characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    }
                }
            }
        }
        LogUtil.d("getCharacteristic end");
        return mNotify!=null && mRead!=null && mWrite!=null && mConfig!=null;
    }


    /**
     * 设置波特率等参数
     * @param baudRate 波特率 例如9600，115200
     * @param dataBit 数据位 5-8
     * @param stopBit 停止位 1-2
     * @param parity 校验位 0：无校验；1：奇校验；2：偶校验；3：标志位；4：空白位
     * @return 操作成功或者失败
     */
    public boolean setSerialBaud(int baudRate,int dataBit,int stopBit,int parity){
        if(connection==null || mConfig==null){
            return false;
        }
        byte[] bytes = connection.spWRCharacteristic(mConfig, CommandUtil.getBaudRateCommand(baudRate, dataBit, stopBit, parity), mConfig,0);

        return  AckParseUtil.isValidBaudRateAck(bytes);
    }

    /**
     *设置硬件流控开关以及Modem状态
     * @param flow 硬件流控开关 true 开；false 关
     * @param DTR 1 有效，低电平；0 无效，高电平
     * @param RTS 1 有效，低电平；0 无效，高电平
     * @return 操作成功或者失败
     */
    public boolean setSerialModem(boolean flow,int DTR,int RTS){
        if(connection==null || mConfig==null){
            return false;
        }
        byte[] bytes = connection.spWRCharacteristic(mConfig, CommandUtil.getFlowCommand(flow, DTR, RTS), mConfig,0);

        return  AckParseUtil.isValidFlowAck(bytes);
    }

    /**
     * 设置传输模式可靠性
     * 设置传输模式 该模式是用来保证数据传输的可靠性。如果选择可靠传输模式，发送前会检查缓冲区的状态，如果选择不可靠传输模式，会直接发送 。该设置即时生效，应当在发送前设置。如果当前正在发送数据，应当努力避免调用该函数。
     * @param reliable true 可靠传输模式；false 不可靠传输模式
     */
    public void setTransmissionReliability(boolean reliable){
        if(mWrite!=null){
            mWrite.setWriteType(reliable? BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT:BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        }
        this.reliable=reliable;
    }

    /**
     * 返回当前传输模式是否是可靠传输模式
     * @return true 可靠传输模式；false 不可靠传输模式
     */
    public boolean isTransmissionReliable() {
        return reliable;
    }

    /**
     * 检查蓝牙设备是否正在被连接
     * @param mac 蓝牙MAC地址
     * @return true 被连接；false 未被连接；
     */
    public boolean isConnected(String mac){

        return connection!=null && mWrite!=null && mConfig!=null && mRead!=null && mNotify!=null && bleHostManager!=null && bleHostManager.isConnected(mac);
    }

    /**
     * 检查蓝牙设备是否正在被连接
     * @param mac 蓝牙MAC地址
     * @return true 被连接；false 未被连接；
     */

    /**
     * 用于连接设备后，主动读取RSSI值。需配合registerRSSINotify(RSSIStatus rssiStatus)函数使用
     * @return true 读取成功;false 读取失败
     */
    public boolean readRSSI(){
        if(bleHostManager==null || connection==null){
            LogUtil.d("设备未连接");
            return false;
        }
        return connection.readRssi();
    }
    /**
     * 注册RSSI信号强度的通知,只有先调用readRSSI()函数，回调才会返回结果
     * @param rssiStatus RSSI状态回调,为null可以取消回调
     */
    public void registerRSSINotify(RSSIStatus rssiStatus){
        this.rssiStatus=rssiStatus;
    }

    /**
     * 打开RSSI上报通知
     */
    private void openRSSINotify(){
        if(connection!=null){
            connection.setRSSICallback(rssiCallback);
        }
    }
    /**
     * 关闭RSSI上报通知
     */
    private void closeRSSINotify(){
        if(connection!=null){
            connection.setRSSICallback(null);
        }
    }


    /**
     * 注册数据上报的通知
     * @param readNotify 通知回调,为null可以取消回调
     */
    public void registerReadNotify(NotifyStatus readNotify){
        this.readNotify=readNotify;
    }

    /**
     * 用于连接成功后调用,区分硬件新旧版本
     * @return true 旧版本硬件;false 新版本硬件
     */
    public boolean isHardwareOld(){

        if(mConfig==null){
            return false;
        }

        return (mConfig.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY)==0;
    }

    /**
     * 获取当前通知状态
     * @return true 通知已经打开；false 通知已经关闭
     */
    public boolean getNotifyState(){
        if(connection==null || mNotify==null){
            return false;
        }
        return connection.getNotifyState(mNotify);
    }

    /**
     * 打开Modem状态上报通知
     */
    private void openModemNotify(){
        //强制打开fff3的通知，接收串口状态以及Modem状态
        //旧版本的硬件fff3无Notify属性，但对程序无影响

        LogUtil.d("当前线程id："+Thread.currentThread().getId());
        boolean b=connection.setNotifyListener(mConfig, new NotifyDataCallback() {
            @Override
            public void OnError(String mac, Throwable t) {
                LogUtil.d("fff3上报错误：" + t.getMessage());
            }

            @Override
            public void OnData(String mac, byte[] data) {

                LogUtil.d("串口状态包");
                if (data == null || data.length != 7) {
                    return;
                }

                //更新缓冲区标志位
                boolean bit0= ModemParse.getStatus(data);
                boolean bit1=ModemParse.getStatusModem(data);
                boolean bit2=ModemParse.getStatusNoResponse(data);


                if(!reliable){
                    if(bit2){
                        flag=false;
                        LogUtil.d("串口位无效");
                    }
                }

                if(bit0){
                    flag=true;
                    LogUtil.d("串口位有效");
                }

                if(bit1){
                    if(modemStatus!=null){
                        LogUtil.d("更新Modem状态");
                        modemStatus.onNotify(ModemParse.isDCDValid(data[5]), ModemParse.isRIValid(data[5]),
                                ModemParse.isDSRValid(data[5]),
                                ModemParse.isCTSValid(data[5]));
                    }
                }
            }
        }, true);
        if(!b){
            LogUtil.d("打开Modem状态通知失败");
        }
    }


    /**
     * 打开或者关闭数据通知
     * @param enable true 打开；false 关闭
     * @return 返回操作结果
     */
    public boolean setNotify(boolean enable){
        if(connection==null || mNotify==null){
            return false;
        }
        if(!enable){
            return connection.setNotifyListener(mNotify,null,false) && connection.enableNotify(false,mNotify);
        }else {
            return connection.setNotifyListener(mNotify,notifyDataCallback,false )  && connection.enableNotify(true,mNotify);
        }
    }

    /**
     * 主动单次读取数据
     * @return 读取的数据
     */
    public byte[] read(){
        if(connection==null || mRead==null){
            return null;
        }
        return connection.read(mRead,true);
    }

    /**
     * 主动单次读取数据
     * @param readCallback 读取回调
     * @param readCallback 读取回调
     */
    public void read(ReadCallback readCallback){
        if(connection==null || mRead==null){
            return;
        }
        connection.read(mRead,readCallback);
    }

}
