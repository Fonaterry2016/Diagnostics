package cn.wch.blelib.ch9141.config.command;

import android.text.TextUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import androidx.annotation.NonNull;
import cn.wch.blelib.ch9141.config.entry.ControlInfo;
import cn.wch.blelib.ch9141.config.entry.DeviceInfo;
import cn.wch.blelib.ch9141.config.entry.ModuleInfo;
import cn.wch.blelib.ch9141.config.exception.CH9141ConfigException;
import cn.wch.blelib.utils.LogUtil;

import static cn.wch.blelib.ch9141.config.utils.ConvertUtil.invertByteArray;
import static cn.wch.blelib.ch9141.config.utils.ConvertUtil.toByteArray;

public class CommandGenerateUtil {
    private static byte[] generateModuleCommandData(@NonNull ModuleInfo moduleInfo)throws CH9141ConfigException {
        byte[] buffer = new byte[173];
        int offset = 0;
        //flag
        buffer[offset] = (byte) 0xaa;
        offset++;
        //module name
        byte[] temp = moduleInfo.getModuleName().getBytes();
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 20));
        offset += 20;
        //module mac
        if(!moduleInfo.getModuleAddress().matches("([A-Fa-f0-9]{2}[-,:]){5}[A-Fa-f0-9]{2}")){
            throw new CH9141ConfigException("module MAC is invalid");
        }
        temp = invertByteArray(toByteArray(moduleInfo.getModuleAddress().replace(":", "")));
        if (temp == null || temp.length != 6) {
            throw new CH9141ConfigException("convert module MAC to byte array");
        } else {
            System.arraycopy(temp, 0, buffer, offset, 6);
        }
        offset += 6;
        //connect mac
        if(!moduleInfo.getConnectAddress().matches("([A-Fa-f0-9]{2}[-,:]){5}[A-Fa-f0-9]{2}")){
            throw new CH9141ConfigException("connect MAC is invalid");
        }
        temp = invertByteArray(toByteArray(moduleInfo.getConnectAddress().replace(":", "")));
        if (temp == null || temp.length != 6) {
            throw new CH9141ConfigException("convert connect MAC to byte array fail");
        } else {
            System.arraycopy(temp, 0, buffer, offset, 6);
        }
        offset += 6;
        //module version
        temp = toByteArray(moduleInfo.getVersion());
        if (temp == null || temp.length == 0) {
            throw new CH9141ConfigException("convert version fail");
        }
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 2));
        offset += 2;
        //module hello
        temp = moduleInfo.getHello().getBytes();
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 30));
        offset += 30;
        //module baud rate
        temp = invertByteArray(toByteArray(String.format("%08x", moduleInfo.getSerialBaudRate())));
        if(temp == null ){
            throw new CH9141ConfigException("convert serial baud rate fail");
        }
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 4));
        offset += 4;
        //data bits
        buffer[offset] = (byte) moduleInfo.getSerialDataBits();
        offset += 1;
        //parity bits
        buffer[offset] = (byte) moduleInfo.getSerialParity();
        offset += 1;
        //stop bits
        buffer[offset] = (byte) moduleInfo.getSerialStopBits();
        offset += 1;
        //time out
        temp = invertByteArray(toByteArray(String.format("%04x", moduleInfo.getSerialTimeout())));
        if(temp == null ){
            throw new CH9141ConfigException("convert serial timeout fail");
        }
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 2));
        offset += 2;
        //低功耗睡眠时间,忽略
        offset += 4;
        //低功耗模式
        buffer[offset] = (byte) moduleInfo.getLowPowerMode();
        LogUtil.d("低功耗模式："+moduleInfo.getLowPowerMode()+" offset"+offset);
        offset += 1;
        //芯片工作模式
        buffer[offset] = (byte) moduleInfo.getChipWorkMode();
        offset += 1;
        //芯片发送功率
        buffer[offset] = (byte) moduleInfo.getChipTransmitPower();
        offset += 1;
        //广播使能
        buffer[offset] = moduleInfo.getAdvEnable();
        LogUtil.d("广播使能："+(moduleInfo.getAdvEnable() & 0xff));
        offset += 1;
        //广播模式
        buffer[offset] = (byte) 0x00;
        offset += 1;
        //广播时间
        short advInterval = moduleInfo.getAdvInterval();
        if(advInterval<20){
            throw new CH9141ConfigException("advertise interval is invalid");
        }
        temp = invertByteArray(toByteArray(String.format("%04x",advInterval & 0xffff)));
        if(temp==null){
            throw new CH9141ConfigException("convert advertise interval fail");
        }
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 2));
        offset += 2;
        //最小连接间隔
        temp = invertByteArray(toByteArray(String.format("%04x", moduleInfo.getPeripheralMinInterval() &0xffff)));
        if(temp==null){
            throw new CH9141ConfigException("convert min connect interval fail");
        }
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 2));
        offset += 2;

        //最大连接间隔
        temp = invertByteArray(toByteArray(String.format("%04x", moduleInfo.getPeripheralMaxInterval() & 0xffff)));
        if(temp==null){
            throw new CH9141ConfigException("convert max connect interval fail");
        }
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 2));
        offset += 2;
        //超时时间
        temp = invertByteArray(toByteArray(String.format("%04x", moduleInfo.getPeripheralTimeout()&0xffff)));
        if(temp==null){
            throw new CH9141ConfigException("convert connect time fail");
        }
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 2));
        offset += 2;
        //设备名称，在General Access里显示
        temp = moduleInfo.getPeripheralName().getBytes();
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 20));
        offset += 20;
        //密码使能
        buffer[offset] = (byte) (moduleInfo.getPeripheralPasswordEnable());
        offset += 1;
        //密码长度
        int peripheralPasswordLength = moduleInfo.getPeripheralPasswordLength();
        if(peripheralPasswordLength<0){
            throw new CH9141ConfigException("peripheral password length is negative");
        }
        buffer[offset] = (byte) peripheralPasswordLength;
        offset++;
        //密码
        String peripheralPassword = moduleInfo.getPeripheralPassword();
        if(peripheralPassword.length()!=peripheralPasswordLength || peripheralPassword.length()!=6){
            throw new CH9141ConfigException("password length must be 6");
        }
        if(!peripheralPassword.matches("[0-9]{6}")){
            throw new CH9141ConfigException("password must be from 0 to 9");
        }
        temp = new byte[6];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = (byte) (peripheralPassword.charAt(i) - '0');
        }
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 6));
        offset += 6;
        //默认连接的MAC标志
        buffer[offset]=moduleInfo.getHostConnectFlag_1();
        buffer[offset+1]=moduleInfo.getHostConnectFlag_2();
        buffer[offset+2]=moduleInfo.getHostConnectFlag_3();
        buffer[offset+3]=moduleInfo.getHostConnectFlag_4();
        offset += 4;
        //默认的连接MAC

        temp = invertByteArray(toByteArray(moduleInfo.getHostConnectAddress_1().replace(":", "")));
        if(temp==null){
            throw new CH9141ConfigException("convert host connect address1 fail");
        }
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 6));

        temp = invertByteArray(toByteArray(moduleInfo.getHostConnectAddress_2().replace(":", "")));
        if(temp==null){
            throw new CH9141ConfigException("convert host connect address2 fail");
        }
        System.arraycopy(temp, 0, buffer, offset+6, Math.min(temp.length, 6));

        temp = invertByteArray(toByteArray(moduleInfo.getHostConnectAddress_3().replace(":", "")));
        if(temp==null){
            throw new CH9141ConfigException("convert host connect address3 fail");
        }
        System.arraycopy(temp, 0, buffer, offset+12, Math.min(temp.length, 6));

        temp = invertByteArray(toByteArray(moduleInfo.getHostConnectAddress_4().replace(":", "")));
        if(temp==null){
            throw new CH9141ConfigException("convert host connect address4 fail");
        }
        System.arraycopy(temp, 0, buffer, offset+18, Math.min(temp.length, 6));
        offset += 24;

        //默认连接的MAC密码
        temp=getHostPasswordByteArray(moduleInfo.getHostPassword_1());
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 6));

        temp=getHostPasswordByteArray(moduleInfo.getHostPassword_2());
        System.arraycopy(temp, 0, buffer, offset+6, Math.min(temp.length, 6));

        temp=getHostPasswordByteArray(moduleInfo.getHostPassword_3());
        System.arraycopy(temp, 0, buffer, offset+12, Math.min(temp.length, 6));

        temp=getHostPasswordByteArray(moduleInfo.getHostPassword_4());
        System.arraycopy(temp, 0, buffer, offset+18, Math.min(temp.length, 6));
        offset += 24;
        //GPIO模式设置

        buffer[offset] = (byte) (moduleInfo.getGpioMode_7() * 128 + moduleInfo.getGpioMode_6() * 64 +
                moduleInfo.getGpioMode_5() * 32 + moduleInfo.getGpioMode_4() * 16 +
                1 * 8 + 1 * 4 + 0 * 2 + 0 * 1);
        offset++;
        //GPIO value
        buffer[offset] = (byte) (moduleInfo.getGpioValue_7() * 128 + moduleInfo.getGpioValue_6() * 64 +
                moduleInfo.getGpioValue_5() * 32 + moduleInfo.getGpioValue_4() * 16 +
                moduleInfo.getGpioValue_3() * 8 + moduleInfo.getGpioValue_2() * 4 +
                0 * 2 + 0 * 1);
        offset++;
        return buffer;
    }

    private static byte[] generateDeviceCommandData(@NonNull DeviceInfo deviceInfo) throws CH9141ConfigException {
        byte[] buffer=new byte[136];
        int offset=0;
        //flag
        buffer[offset]=(byte)0xaa;
        offset++;
        //sys id
        byte[] systemID = deviceInfo.getSystemID();
        if(systemID==null || systemID.length!=8){
            throw new CH9141ConfigException("systemID length must be 8");
        }
        System.arraycopy(systemID, 0, buffer, offset,systemID.length);
        offset+=8;

        //module name
        byte[] temp = deviceInfo.getModelName().getBytes();

        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length,20));
        offset+=20;
        //serial number
        temp = deviceInfo.getSerialNumber().getBytes();
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length,20));
        offset+=20;
        //firmware version
        temp = deviceInfo.getFirmwareRevision().getBytes();
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length,20));
        offset+=20;
        //hardware version

        temp = deviceInfo.getHardwareRevision().getBytes();
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length,20));
        offset+=20;
        //software version
        temp = deviceInfo.getSoftwareRevision().getBytes();
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length,20));
        offset+=20;
        //manufacturer name
        temp = deviceInfo.getManufacturerName().getBytes();
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length,20));
        offset+=20;
        //PNP id
        byte[] pnPID = deviceInfo.getPnPID();
        if(pnPID==null || pnPID.length!=7){
            throw new CH9141ConfigException("PnPID length must be 7");
        }

        System.arraycopy(pnPID, 0, buffer, offset, pnPID.length);
        return buffer;
    }

    private static byte[] generateControlCommandData(@NonNull ControlInfo controlInfo)throws CH9141ConfigException{
        byte[] buffer = new byte[512];
        int offset = 0;
        //image位置
        buffer[offset] = controlInfo.getImageFlag();
        offset += 1;
        //reserved
        byte[] revd = controlInfo.getRevd();
        if(revd==null || revd.length!=3){
            throw new CH9141ConfigException("reserve byte array length must be 3");
        }
        System.arraycopy(revd, 0, buffer, offset, revd.length);
        offset += 3;
        //参数标志
        buffer[offset] = controlInfo.getExd_para_flag();
        offset += 1;
        //版本信息
        byte[] exd_para_ver = controlInfo.getExd_para_ver();
        if(exd_para_ver==null || exd_para_ver.length!=2){
            throw new CH9141ConfigException("version byte array length must be 2");
        }
        System.arraycopy(exd_para_ver, 0, buffer, offset, exd_para_ver.length);
        offset += 2;
        //配置使能
        buffer[offset] = (byte) controlInfo.getBle_cfg_enable();
        offset += 1;
        //广播通道
        buffer[offset] = (byte) controlInfo.getBroadcast_ch_cfg();
        offset += 1;
        //按位定义配置
        byte[] bit_switch_cfg = controlInfo.getBit_switch_cfg();
        if(bit_switch_cfg==null || bit_switch_cfg.length!=3){
            throw new CH9141ConfigException("Bit byte array length must be 3");
        }
        System.arraycopy(bit_switch_cfg, 0, buffer, offset, bit_switch_cfg.length);
        offset += 3;
        //
        buffer[offset]=controlInfo.getRc_32k_cali_method();
        offset+=1;
        //
        buffer[offset]=controlInfo.getBle_rf_cali_method();
        offset+=1;
        //
        buffer[offset]=controlInfo.getRc_32k_cali_temperature();
        offset+=1;
        //
        buffer[offset]=controlInfo.getBle_rf_cali_temperature();
        offset+=1;
        //内部RC校准时间
        byte[] temp = invertByteArray(toByteArray(String.format("%08x", controlInfo.getRc_32k_cali_time() &0xffffffff)));
        if(temp==null){
            throw new CH9141ConfigException("convert rc time fail");
        }
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 4));
        offset+=4;
        //蓝牙RF校准时间
        temp = invertByteArray(toByteArray(String.format("%08x", controlInfo.getBle_rf_cali_time() &0xffffffff)));
        if(temp==null){
            throw new CH9141ConfigException("convert rf time interval fail");
        }
        System.arraycopy(temp, 0, buffer, offset, Math.min(temp.length, 4));
        offset+=4;

        //
        buffer[offset]=controlInfo.getGpio_en();
        offset+=1;
        //
        buffer[offset]=controlInfo.getTnow_en();
        offset+=1;
        //
        buffer[offset]=controlInfo.getBle_sta_en();
        offset+=1;
        //
        buffer[offset]=controlInfo.getRevd1();
        offset+=1;
        //ls_dcdc_adc_func

        LogUtil.d("当前offset: "+offset);
        return buffer;
    }
    public static byte[] generateSetModuleCommand(@NonNull ModuleInfo moduleInfo) throws CH9141ConfigException {
        byte[] bytes = generateModuleCommandData(moduleInfo);
        ByteBuffer byteBuffer=ByteBuffer.allocate(bytes.length+5).order(ByteOrder.BIG_ENDIAN);
        byteBuffer.put((byte) 0x02);
        byteBuffer.putShort((short) (bytes.length+2));
        byteBuffer.put((byte) 0x01);
        byteBuffer.put(bytes);
        //校验值包含状态码
        byteBuffer.put((byte) (calculateSum(bytes)+0x01));
        return byteBuffer.array();
    }

    public static byte[] generateGetModuleCommand(){
        return new byte[]{0x01,0x00,0x02,0x01,0x01};
    }

    public static byte[] generateSetDeviceCommand(@NonNull DeviceInfo deviceInfo) throws CH9141ConfigException {
        byte[] bytes = generateDeviceCommandData(deviceInfo);
        ByteBuffer byteBuffer=ByteBuffer.allocate(bytes.length+5).order(ByteOrder.BIG_ENDIAN);
        byteBuffer.put((byte) 0x02);
        byteBuffer.putShort((short) (bytes.length+2));
        byteBuffer.put((byte) 0x02);
        byteBuffer.put(bytes);
        //校验值包含状态码
        byteBuffer.put((byte) (calculateSum(bytes)+0x02));
        return byteBuffer.array();

    }

    public static byte[] generateGetDeviceCommand(){
        return new byte[]{0x01,0x00,0x02,0x02,0x02};
    }

    public static byte[] generateSetControlCommand(@NonNull ControlInfo controlInfo) throws CH9141ConfigException {
        byte[] bytes = generateControlCommandData(controlInfo);
        ByteBuffer byteBuffer=ByteBuffer.allocate(bytes.length+5).order(ByteOrder.BIG_ENDIAN);
        byteBuffer.put((byte) 0x02);
        byteBuffer.putShort((short) (bytes.length+2));
        byteBuffer.put((byte) 0x03);
        byteBuffer.put(bytes);
        //校验值包含状态码
        byteBuffer.put((byte) (calculateSum(bytes)+0x03));
        return byteBuffer.array();
    }

    public static byte[] generateGetControlCommand(){
        return new byte[]{0x01,0x00,0x02,0x03,0x03};
    }

    public static byte[] generateResetModuleCommand(){
        return new byte[]{0x03,0x00,0x02,0x01,0x01};
    }

    public static byte[] generateResetDeviceCommand(){
        return new byte[]{0x03,0x00,0x02,0x02,0x02};
    }

    public static byte[] generateResetChipCommand(){
        return new byte[]{0x04,0x00,0x02,0x00,0x00};
    }

    public static byte[] generateSetGPIOCommand(int num,int dir){
        byte[] dataToWrite = new byte[7];
        dataToWrite[0] = 0x05;
        dataToWrite[1] = 0x00;
        dataToWrite[2] = 0x04;
        dataToWrite[3] = 0x01;
        dataToWrite[4] = (byte) num;
        dataToWrite[5] = (byte) dir;
        dataToWrite[6] = (byte) ((dataToWrite[3] & 0xff + dataToWrite[4] & 0xff + dataToWrite[5] & 0xff) & 0xff);
        return dataToWrite;
    }

    public static byte[] generateReadGPIOCommand(int num){
        byte[] dataToWrite = new byte[6];
        dataToWrite[0] = 0x05;
        dataToWrite[1] = 0x00;
        dataToWrite[2] = 0x03;
        dataToWrite[3] = 0x02;
        dataToWrite[4] = (byte) num;
        dataToWrite[5] = (byte) ((dataToWrite[3] & 0xff + dataToWrite[4] & 0xff) & 0xff);
        return dataToWrite;
    }

    public static byte[] generateWriteGPIOCommand(int num,int val){
        byte[] dataToWrite = new byte[7];
        dataToWrite[0] = 0x05;
        dataToWrite[1] = 0x00;
        dataToWrite[2] = 0x04;
        dataToWrite[3] = 0x03;

        dataToWrite[4] = (byte) num;
        dataToWrite[5] = (byte)val;
        dataToWrite[6] = (byte) ((dataToWrite[3] & 0xff + dataToWrite[4] & 0xff + dataToWrite[5] & 0xff) & 0xff);
        return dataToWrite;
    }

    public static byte[] generateSyncGPIOCommand() {
        byte[] dataToWrite = new byte[7];
        dataToWrite[0] = 0x05;
        dataToWrite[1] = 0x00;
        dataToWrite[2] = 0x04;
        dataToWrite[3] = 0x04;
        dataToWrite[4] = 0x00;
        dataToWrite[5] = 0x00;
        dataToWrite[6] = 0x04;
        return dataToWrite;
    }

    public static byte[] generateReadGPIOAdcCommand() {
        byte[] dataToWrite = new byte[5];
        dataToWrite[0] = 0x05;
        dataToWrite[1] = 0x00;
        dataToWrite[2] = 0x02;
        dataToWrite[3] = 0x05;
        dataToWrite[4] = 0x05;
        return dataToWrite;
    }



    private static int calculateSum(byte[] bytes){
        int check = 0;
        for (int i = 0; i < bytes.length; i++) {
            check += (bytes[i] & 0xff);
        }
        return check;
    }

    private static byte[] getHostPasswordByteArray(String password)throws CH9141ConfigException{
        if(password==null || password.length()>6){
            throw new CH9141ConfigException("host address length must be 6");
        }
        byte[] temp=new byte[6];
        Arrays.fill(temp, (byte) 0x00);
        int offset=temp.length-password.length();
        for (int i=0;i<password.length();i++){
            temp[offset+i]= (byte) (password.charAt(i)-'0');
        }
        return temp ;
    }

}
