package cn.wch.blelib.ch9141.config.command;

import java.util.Locale;

import cn.wch.blelib.ch9141.config.entry.ControlInfo;
import cn.wch.blelib.ch9141.config.entry.DeviceInfo;
import cn.wch.blelib.ch9141.config.entry.ModuleInfo;
import cn.wch.blelib.ch9141.config.utils.ConvertUtil;
import cn.wch.blelib.utils.LogUtil;

import static cn.wch.blelib.ch9141.config.utils.ConvertUtil.invertHexString;

public class AckParse {
    public static final int ACK_MODULE_LENGTH=178;
    public static final int ACK_DEVICE_LENGTH=141;
    public static final int ACK_CONTROL_LENGTH=517;

    public static boolean isAckError(byte[] data){
        if(data!=null && data.length>0){
            return data[0]==(byte) 0x8f;
        }
        return false;
    }


    public static ModuleInfo parseModuleFomAckByteArray(byte[] data){
        if(data==null || data.length!=ACK_MODULE_LENGTH){
            return null;
        }
        //不检查校验位
        if(data[0]!=(byte)0x81 || data[1]!=(byte)0x00 || data[2]!=(byte)0xaf || data[3]!=(byte)0x01){
            return null;
        }
        byte[] source=new byte[ACK_MODULE_LENGTH-5];
        System.arraycopy(data,4,source,0,source.length);
        return parseModuleFromByteArray(source);
    }

    public static DeviceInfo parseDeviceFomAckByteArray(byte[] data){
        if(data==null || data.length!=ACK_DEVICE_LENGTH){
            return null;
        }
        //不检查校验位
        if(data[0]!=(byte)0x81 || data[1]!=(byte)0x00 || data[2]!=(byte)0x8a || data[3]!=(byte)0x02){
            return null;
        }
        byte[] source=new byte[ACK_DEVICE_LENGTH-5];
        System.arraycopy(data,4,source,0,source.length);
        return parseDeviceFromByteArray(source);
    }

    public static ControlInfo parseControlFomAckByteArray(byte[] data){
        if(data==null || data.length!=ACK_CONTROL_LENGTH){
            return null;
        }
        //不检查校验位
        if(data[0]!=(byte)0x81 || data[1]!=(byte)0x02 || data[2]!=(byte)0x02|| data[3]!=(byte)0x03){
            return null;
        }
        byte[] source=new byte[ACK_CONTROL_LENGTH-5];
        System.arraycopy(data,4,source,0,source.length);
        return parseControlFromByteArray(source);
    }

    public static boolean isRightSetModuleAck(byte[] data){
        if(data==null || data.length==0){
            return false;
        }
        return bytesToHexString(data).equalsIgnoreCase(bytesToHexString(new byte[]{(byte) 0x82,0x00,0x02,0x01,0x01}));
    }

    public static boolean isRightSetDeviceAck(byte[] data){
        if(data==null || data.length==0){
            return false;
        }
        return bytesToHexString(data).equalsIgnoreCase(bytesToHexString(new byte[]{(byte) 0x82,0x00,0x02,0x02,0x02}));
    }

    public static boolean isRightSetControlAck(byte[] data){
        if(data==null || data.length==0){
            return false;
        }
        return bytesToHexString(data).equalsIgnoreCase(bytesToHexString(new byte[]{(byte) 0x82,0x00,0x02,0x03,0x03}));
    }

    public static boolean isRightResetModuleAck(byte[] data){
        if(data==null || data.length==0){
            return false;
        }
        return bytesToHexString(data).equalsIgnoreCase(bytesToHexString(new byte[]{(byte) 0x83,0x00,0x02,0x01,0x01}));
    }

    public static boolean isRightResetDeviceAck(byte[] data){
        if(data==null || data.length==0){
            return false;
        }
        return bytesToHexString(data).equalsIgnoreCase(bytesToHexString(new byte[]{(byte) 0x83,0x00,0x02,0x02,0x02}));
    }



    private static String bytesToHexString(byte[] bArr) {
        if (bArr == null || bArr.length==0)
            return "";
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;
        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase() + " ");
        }
        return sb.toString();
    }

    private static  ModuleInfo parseModuleFromByteArray(byte[] data){
        // 蓝牙配置信息从偏移位置0开始
        int offset=0;
        ModuleInfo moduleInfo=new ModuleInfo();
        //flag
        moduleInfo.setFlag(data[offset]);
        offset+=1;
        //module name
        moduleInfo.setModuleName(ConvertUtil.byteToString(data,offset,20));
        offset+=20;
        //module mac
        String modMac = invertHexString(ConvertUtil.toHexString(data, offset,6));
        modMac = modMac.substring(0, modMac.length()-1).replace(' ', ':');
        moduleInfo.setModuleAddress(modMac);
        offset+=6;
        //connect mac
        String connMac = invertHexString(ConvertUtil.toHexString(data, offset,6));
        connMac = connMac.substring(0, connMac.length()-1).replace(' ', ':');
        moduleInfo.setConnectAddress(connMac);
        offset+=6;
        //module version
        String modVer = ConvertUtil.toHexString(data, offset,2);
        moduleInfo.setVersion(modVer);
        offset+=2;
        //hello
        String hello = ConvertUtil.byteToString(data,offset,30);
        moduleInfo.setHello(hello);
        offset+=30;
        //baud rate
        int val = 0;
        for (int i = offset; i < offset+4; i++)
            val += (data[i] & 0xff) * (int)(Math.pow(256, i-offset));
        moduleInfo.setSerialBaudRate(val);
        offset+=4;
        //data bits
        moduleInfo.setSerialDataBits(data[offset]);
        offset+=1;
        //parity
        moduleInfo.setSerialParity(data[offset]);
        offset+=1;
        //stop bits
        moduleInfo.setSerialStopBits(data[offset]);
        offset+=1;
        //timeout
        moduleInfo.setSerialTimeout((short) ((data[offset] & 0xff) + (data[offset+1] & 0xff) * 256));
        offset+=2;

        //低功耗睡眠时间,保留参数
        offset+=4;
        //低功耗模式
        LogUtil.d("低功耗模式:"+((data[offset] & 0xff)));
        moduleInfo.setLowPowerMode((data[offset] ));
        offset+=1;

        //芯片工作模式
        moduleInfo.setChipWorkMode(data[offset] );
        offset+=1;
        //芯片发送功率
        moduleInfo.setChipTransmitPower(data[offset] );
        offset+=1;
        //broadcast mode parameter
        //广播使能
        moduleInfo.setAdvEnable(data[offset]);
        offset+=1;

        //广播模式
        offset+=1;
        //广播时间
        moduleInfo.setAdvInterval((short) ((data[offset] & 0xff) + (data[offset+1] & 0xff) * 256));
        offset+=2;

        //device mode parameter
        //最小连接间隔
        moduleInfo.setPeripheralMinInterval((short) ((data[offset] & 0xff) + (data[offset+1] & 0xff) * 256));
        offset+=2;
        //最大连接间隔
        moduleInfo.setPeripheralMaxInterval((short) ((data[offset] & 0xff) + (data[offset+1] & 0xff) * 256));
        offset+=2;
        //超时时间
        moduleInfo.setPeripheralTimeout((short) ((data[offset] & 0xff) + (data[offset+1] & 0xff) * 256));
        offset+=2;
        //设备名称
        String periName = ConvertUtil.byteToString(data,offset,20);
        moduleInfo.setPeripheralName(periName);
        offset+=20;
        //密码使能
        moduleInfo.setPeripheralPasswordEnable(data[offset]);
        offset+=1;
        //密码长度
        moduleInfo.setPeripheralPasswordLength(data[offset]);
        offset+=1;
        //密码

        if ( moduleInfo.getPeripheralPasswordLength()<=6) {
            String s="";
            for (int i=0;i<moduleInfo.getPeripheralPasswordLength();i++){
                s+=String.format("%d",data[i+offset] & 0xff);
            }
            moduleInfo.setPeripheralPassword(s);
        }
        offset+=6;

        //主机参数-->默认连接的 MAC 标志
        moduleInfo.setHostConnectFlag_1(data[offset]);
        moduleInfo.setHostConnectFlag_2(data[offset+1]);
        moduleInfo.setHostConnectFlag_3(data[offset+2]);
        moduleInfo.setHostConnectFlag_4(data[offset+3]);
        offset+=4;
        //主机参数-->默认连接的 MAC
        String connAdd1 = invertHexString(ConvertUtil.toHexString(data, offset,6));
        connAdd1 = connAdd1.substring(0, connAdd1.length()-1).replace(' ', ':');
        moduleInfo.setHostConnectAddress_1(connAdd1);

        String connAdd2 = invertHexString(ConvertUtil.toHexString(data, offset+6,6));
        connAdd2 = connAdd2.substring(0, connAdd2.length()-1).replace(' ', ':');
        moduleInfo.setHostConnectAddress_2(connAdd2);

        String connAdd3 = invertHexString(ConvertUtil.toHexString(data, offset+12,6));
        connAdd3 = connAdd3.substring(0, connAdd3.length()-1).replace(' ', ':');
        moduleInfo.setHostConnectAddress_3(connAdd3);

        String connAdd4 = invertHexString(ConvertUtil.toHexString(data, offset+18,6));
        connAdd4 = connAdd4.substring(0, connAdd4.length()-1).replace(' ', ':');
        moduleInfo.setHostConnectAddress_4(connAdd4);

        offset+=24;

        //主机参数-->默认连接的MAC的密码
        String connAddPw1 = getHostPassword(data,offset);
        moduleInfo.setHostPassword_1(connAddPw1);

        String connAddPw2 = getHostPassword(data,offset+6);
        moduleInfo.setHostPassword_2(connAddPw2);

        String connAddPw3 = getHostPassword(data,offset+12);
        moduleInfo.setHostPassword_3(connAddPw3);

        String connAddPw4 = getHostPassword(data,offset+18);
        moduleInfo.setHostPassword_4(connAddPw4);
        offset+=24;
        //GPIO 模式设置
        moduleInfo.setGpioMode(data[offset]);
        moduleInfo.setGpioMode_0( (byte)(data[offset] & 0x01) );
        moduleInfo.setGpioMode_1( (byte)((data[offset] & 0x02) >>1) );
        moduleInfo.setGpioMode_2( (byte)((data[offset] & 0x04) >>2));
        moduleInfo.setGpioMode_3( (byte)((data[offset] & 0x08) >>3));
        moduleInfo.setGpioMode_4( (byte)((data[offset] & 0x10) >>4));
        moduleInfo.setGpioMode_5( (byte)((data[offset] & 0x20) >>5));
        moduleInfo.setGpioMode_6( (byte)((data[offset] & 0x40) >>6));
        moduleInfo.setGpioMode_7( (byte)((data[offset] & 0x80) >>7));
        offset+=1;
        //GPIO 电平值设置
        moduleInfo.setGpioValue(data[offset]);
        moduleInfo.setGpioValue_0((byte)((data[offset] & 0x01)));
        moduleInfo.setGpioValue_1( (byte)((data[offset] & 0x02) >>1));
        moduleInfo.setGpioValue_2( (byte)((data[offset] & 0x04) >>2));
        moduleInfo.setGpioValue_3( (byte)((data[offset] & 0x08) >>3));
        moduleInfo.setGpioValue_4( (byte)((data[offset] & 0x10) >>4));
        moduleInfo.setGpioValue_5( (byte)((data[offset] & 0x20) >>5));
        moduleInfo.setGpioValue_6( (byte)((data[offset] & 0x40) >>6));
        moduleInfo.setGpioValue_7( (byte)((data[offset] & 0x80) >>7));
        offset+=1;
        LogUtil.d("当前offset: "+offset);
        return moduleInfo;
    }

    private static DeviceInfo parseDeviceFromByteArray(byte[] data){
    // 蓝牙配置信息从偏移位置4开始
        int offset=0;
        DeviceInfo deviceInfo=new DeviceInfo();
        //参数标志
        deviceInfo.setFlag(data[offset]);
        offset+=1;
        //System ID
        byte[] s=new byte[8];
        System.arraycopy(data,offset,s,0,s.length);
        deviceInfo.setSystemID(s);
        offset+=8;
        //Model Number String
        deviceInfo.setModelName(new String(data,offset,20).trim());
        offset+=20;
        //Serial Number String
        deviceInfo.setSerialNumber(new String(data,offset,20).trim());
        offset+=20;
        //Firmware Revision String
        deviceInfo.setFirmwareRevision(new String(data,offset,20).trim());
        offset+=20;
        //Hardware Revision String
        deviceInfo.setHardwareRevision(new String(data,offset,20).trim());
        offset+=20;
        //Software Revision String
        deviceInfo.setSoftwareRevision(new String(data,offset,20).trim());
        offset+=20;
        //Manufacturer Name String
        deviceInfo.setManufacturerName(new String(data,offset,20).trim());
        offset+=20;
        //PnP ID
        byte[] p=new byte[7];
        System.arraycopy(data,offset,p,0,p.length);
        deviceInfo.setPnPID(p);
        offset+=7;
        LogUtil.d("当前offset: "+offset);
        return deviceInfo;
    }

    private static ControlInfo parseControlFromByteArray(byte[] data){
        // 蓝牙配置信息从偏移位置4开始
        int offset=0;
        ControlInfo controlInfo=new ControlInfo();
        //image标志
        controlInfo.setImageFlag(data[offset]);
        offset+=1;
        //Revd
        byte[] revd=new byte[3];
        System.arraycopy(data,offset,revd,0,revd.length);
        controlInfo.setRevd(revd);
        offset+=3;
        //参数标志
        controlInfo.setExd_para_flag(data[offset]);
        offset+=1;
        //版本信息
        byte[] ver=new byte[2];
        System.arraycopy(data,offset,ver,0,ver.length);
        controlInfo.setExd_para_ver(ver);
        offset+=2;
        //蓝牙接口配置使能
        controlInfo.setBle_cfg_enable(data[offset]);
        offset+=1;
        //广播配置使能
        controlInfo.setBroadcast_ch_cfg(data[offset]);
        offset+=1;
        //按位定义配置项
        byte[] cfg=new byte[3];
        System.arraycopy(data,offset,cfg,0,cfg.length);
        controlInfo.setBit_switch_cfg(cfg);
        offset+=3;
        //
        controlInfo.setRc_32k_cali_method(data[offset]);
        offset+=1;
        //
        controlInfo.setBle_rf_cali_method(data[offset]);
        offset+=1;
        //
        controlInfo.setRc_32k_cali_temperature(data[offset]);
        offset+=1;
        //
        controlInfo.setBle_rf_cali_temperature(data[offset]);
        offset+=1;
        //
        LogUtil.d("RC时间: "+ConvertUtil.toHexString(data,offset,4));
        int val = 0;
        for (int i = offset; i < offset+4; i++)
            val += (data[i] & 0xff) * (int)(Math.pow(256, i-offset));
        controlInfo.setRc_32k_cali_time(val);
        offset+=4;
        //
        LogUtil.d("RF时间: "+ConvertUtil.toHexString(data,offset,4));
        int v = 0;
        for (int i = offset; i < offset+4; i++)
            v += (data[i] & 0xff) * (int)(Math.pow(256, i-offset));
        controlInfo.setBle_rf_cali_time(v);
        offset+=4;
        //
        controlInfo.setGpio_en(data[offset]);
        offset+=1;
        //
        controlInfo.setTnow_en(data[offset]);
        offset+=1;
        //
        controlInfo.setBle_sta_en(data[offset]);
        offset+=1;
        //
        controlInfo.setRevd1(data[offset]);
        offset+=1;
        //ls_dcdc_adc_func暂时不写
        LogUtil.d("当前offset: "+offset);

        return controlInfo;
    }

    private static String getHostPassword(byte[] data,int offset){
        //固定6字节
        StringBuilder builder=new StringBuilder();
        for (int i=0;i<6;i++){
            builder.append(String.format(Locale.US,"%d",data[offset+i] & 0xff));
        }
        return builder.toString();
    }
}

