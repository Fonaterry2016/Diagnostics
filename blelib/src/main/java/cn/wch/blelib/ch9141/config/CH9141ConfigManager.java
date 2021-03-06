package cn.wch.blelib.ch9141.config;

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
import android.text.TextUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import cn.wch.blelib.ch9141.CH9141BluetoothManager;
import cn.wch.blelib.ch9141.callback.NotifyStatus;
import cn.wch.blelib.ch9141.callback.RSSIStatus;
import cn.wch.blelib.ch9141.config.callback.ConnectStatus;
import cn.wch.blelib.ch9141.config.command.AckParse;
import cn.wch.blelib.ch9141.config.command.CommandGenerateUtil;
import cn.wch.blelib.ch9141.config.command.GPIOAckParse;
import cn.wch.blelib.ch9141.config.entry.ControlInfo;
import cn.wch.blelib.ch9141.config.entry.DeviceInfo;
import cn.wch.blelib.ch9141.config.entry.ModuleInfo;
import cn.wch.blelib.ch9141.config.exception.CH9141ConfigException;
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

public class CH9141ConfigManager {
    private Application application;
    private static CH9141ConfigManager ch9141ConfigManager;

    //????????????
    private ScanFilter filter;
    private ScanRuler scanRuler;

    //????????????????????????
    private BLEHostManager bleHostManager;

    private BluetoothGattCharacteristic mWrite;
    private BluetoothGattCharacteristic mRead;
    private BluetoothGattCharacteristic mNotify;
    private BluetoothGattCharacteristic mConfig;

    private Connection connection;


    /**
     * ??????????????????
     * @return ????????????????????????
     */
    public static CH9141ConfigManager getInstance() {
        if(ch9141ConfigManager ==null){
            synchronized (CH9141BluetoothManager.class){
                ch9141ConfigManager =new CH9141ConfigManager();
            }
        }
        return ch9141ConfigManager;
    }

    /**
     * ?????????????????????????????????Application?????????
     * @param application ???????????????
     */
    public void init(@NonNull Application application) throws BLELibException {
        this.application=application;
        bleHostManager = BLEHostManager.getInstance(application);
        bleHostManager.init(application);
    }
    /**
     * ??????MAC??????????????????
     * @param mac MAC??????
     * @param timeout ??????????????????????????????ms
     * @param connectStatus ??????????????????
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
                    //?????????????????????
                    connectStatus.OnConnectSuccess(mac);

                }else {
                    LogUtil.d("??????????????????");
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
     * ????????????????????????
     * @param force ?????????????????????true ??????????????????????????????????????????????????????(ConnectStatus)??????????????????false ????????????????????????????????????
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
     * ?????????????????????
     * ?????????????????????????????????????????????????????????
     *
     * @param data ????????????
     * @param length  ????????????????????????data[0]??????
     * @return ???????????? ???????????????????????? ???????????????????????????
     */
    public synchronized int write( @NonNull byte[] data, int length){
        int total=0;

        LogUtil.d("???????????????????????????false-->");
        if(connection==null){
            return -1;
        }
        Connector connector = connection.getConnector();
        if(connector==null || mConfig==null || length<0){
            return -1;
        }
        if((mConfig.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE)==0 && (mConfig.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)==0){
            return -2;
        }
        if(data.length==0 || length==0){
            return 0;
        }
        int packetLen=connector.getMax_packet();
        LogUtil.d("?????????????????????"+packetLen);
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

        }
        byte[] tmp=new byte[Math.min(length,data.length)%packetLen];
        if(tmp.length!=0) {
            System.arraycopy(data, fullCount * packetLen, tmp, 0, tmp.length);
            if (!syncWriteCharacteristic(connector,mConfig, tmp)) {
                return total;
            }
            LogUtil.d("final write "+tmp.length);
            total+=tmp.length;
        }
        return total;
    }
    /**
     *?????????????????????????????????
     * @return ??????????????????????????????
     */
    private boolean syncWriteCharacteristic(Connector connector,BluetoothGattCharacteristic characteristic, byte[] tmp){
        LogUtil.d("????????????");

        return connector.syncWriteCharacteristic(characteristic, tmp);
    }

    /**
     *??????????????????????????????????????????????????????????????????
     * @param writeData
     * @return
     */
    public byte[] spWRCharacteristic(@NonNull byte[] writeData,int readNum,long  waitTimeBeforeRead){
        if(connection==null){
            return null;
        }
        return connection.spWRCharacteristic(mConfig, writeData, mConfig, readNum,waitTimeBeforeRead);
    }

    /**
     *????????????????????????????????????,??????????????????
     * @param writeData
     * @return
     */
    public byte[] spWRCharacteristic(@NonNull byte[] writeData,long  waitTimeBeforeRead){
        if(connection==null){
            return null;
        }
        return connection.spWRCharacteristic(mConfig, writeData, mConfig,waitTimeBeforeRead);
    }

    public int resetChip(@NonNull byte[] writeData,int length){
        if(connection==null){
            return -1;
        }
        return connection.write(mConfig, writeData, length);
    }
    /**
     * ????????????
     */
    private void close(){
        connection=null;
        mWrite=null;
        mRead=null;
        mNotify=null;
        mConfig=null;
    }

    /**
     * ??????MTU
     * @param mtu ???????????????MTU??????
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
     * ?????????????????????????????????????????????
     * @param list ???????????????
     * @return true ???????????????????????????false ???????????????????????????
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

                        mConfig.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

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

    public boolean isConnected(String mac){
        return connection!=null  && mConfig!=null && bleHostManager!=null && bleHostManager.isConnected(mac);
    }


    public boolean setModuleInfo(@NonNull ModuleInfo moduleInfo) throws CH9141ConfigException {
        byte[] bytes = CommandGenerateUtil.generateSetModuleCommand(moduleInfo);
        byte[] ack = spWRCharacteristic(bytes, 200);
        if(ack==null){
            return false;
        }
        return AckParse.isRightSetModuleAck(ack);
    }

    public boolean setDeviceInfo(@NonNull DeviceInfo deviceInfo) throws CH9141ConfigException {
        byte[] bytes = CommandGenerateUtil.generateSetDeviceCommand(deviceInfo);
        byte[] ack = spWRCharacteristic(bytes, 200);
        if(ack==null){
            return false;
        }
        return AckParse.isRightSetDeviceAck(ack);
    }

    public boolean setControlInfo(@NonNull ControlInfo controlInfo) throws CH9141ConfigException {
        byte[] bytes = CommandGenerateUtil.generateSetControlCommand(controlInfo);
        byte[] ack = spWRCharacteristic(bytes, 200);
        if(ack==null){
            return false;
        }
        return AckParse.isRightSetControlAck(ack);
    }

    public ModuleInfo getModuleInfo(){
        byte[] bytes = CommandGenerateUtil.generateGetModuleCommand();
        byte[] ack = spWRCharacteristic(bytes, AckParse.ACK_MODULE_LENGTH,200);
        if(ack==null){
            return null;
        }
        return AckParse.parseModuleFomAckByteArray(ack);
    }

    public DeviceInfo getDeviceInfo(){
        byte[] bytes = CommandGenerateUtil.generateGetDeviceCommand();
        byte[] ack = spWRCharacteristic(bytes, AckParse.ACK_DEVICE_LENGTH,200);
        if(ack==null){
            return null;
        }
        return AckParse.parseDeviceFomAckByteArray(ack);
    }

    public ControlInfo getControlInfo(){
        byte[] bytes = CommandGenerateUtil.generateGetControlCommand();
        byte[] ack = spWRCharacteristic(bytes, AckParse.ACK_CONTROL_LENGTH,200);
        if(ack==null){
            return null;
        }
        return AckParse.parseControlFomAckByteArray(ack);
    }

    public boolean resetChip(){
        byte[] bytes = CommandGenerateUtil.generateResetChipCommand();
        int write = resetChip(bytes, bytes.length);
        return write==bytes.length;
    }

    public boolean resetModuleInfo(){
        byte[] bytes = CommandGenerateUtil.generateResetModuleCommand();
        byte[] bytes1 = spWRCharacteristic(bytes, 200);
        return AckParse.isRightResetModuleAck(bytes1);
    }

    public boolean resetDeviceInfo(){
        byte[] bytes = CommandGenerateUtil.generateResetDeviceCommand();
        byte[] bytes1 = spWRCharacteristic(bytes, 200);
        return AckParse.isRightResetDeviceAck(bytes1);
    }

    public boolean setGPIO(int num,int dir){
        byte[] bytes = CommandGenerateUtil.generateSetGPIOCommand(num, dir);
        byte[] bytes1 = spWRCharacteristic(bytes, 200);
        return GPIOAckParse.isGPIOSetSuccess(bytes1);
    }

    public int readGPIO(int num){
        byte[] bytes = CommandGenerateUtil.generateReadGPIOCommand(num);
        byte[] bytes1 = spWRCharacteristic(bytes, 200);
        return GPIOAckParse.getGPIOReadResult(bytes1);
    }

    public boolean writeGPIOValue(int num,int val){
        byte[] bytes = CommandGenerateUtil.generateWriteGPIOCommand(num, val);
        byte[] bytes1 = spWRCharacteristic(bytes, 200);
        return GPIOAckParse.isGPIOWriteSuccess(bytes1);
    }

    public boolean syncGPIO(){
        byte[] bytes = CommandGenerateUtil.generateSyncGPIOCommand();
        byte[] bytes1 = spWRCharacteristic(bytes, 200);
        return GPIOAckParse.isGPIOSyncSuccess(bytes1);
    }

    public double readGPIOAdc(){
        byte[] bytes = CommandGenerateUtil.generateReadGPIOAdcCommand();
        byte[] bytes1 = spWRCharacteristic(bytes, 200);
        return GPIOAckParse.getADC(bytes1);
    }
}
