package cn.wch.bleconfig.Constant;

public class Constant {

    public static final String INTENT_KEY="address";
    public static final String ServiceUUID="0000fff0-0000-1000-8000-00805f9b34fb";
    public static final String RWCharacterUUID="0000fff3-0000-1000-8000-00805f9b34fb";

    public static final byte[] SET_MODULE_ACK=new byte[]{(byte) 0x82,0x00,0x02,0x01,0x01};
    public static final byte[] SET_DEVICE_ACK=new byte[]{(byte) 0x82,0x00,0x02,0x02,0x02};
    public static final byte[] SET_CONTROL_ACK=new byte[]{(byte) 0x82,0x00,0x02,0x03,0x03};
    public static final byte[] RESET_MODULE_ACK=new byte[]{(byte) 0x83,0x00,0x02,0x01,0x01};
    public static final byte[] RESET_DEVICE_ACK=new byte[]{(byte) 0x83,0x00,0x02,0x02,0x02};
}
