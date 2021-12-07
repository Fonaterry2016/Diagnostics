package cn.wch.blelib.ch9141.config.command;


public class GPIOAckParse {
    public static boolean isGPIOSetSuccess(byte[] data){
        if(data==null ||data.length!=6){
            return false;
        }
        if(!( data[0] == (byte) 0x85 && data[1] == 0x00 && data[2] == 0x03 && data[3] == 0x01)){
            return false;
        }
        return data[4] == 0x00;
    }

    public static int getGPIOReadResult(byte[] data){
        if(data==null ||data.length!=7){
            return -1;
        }
        if(!( data[0] == (byte) 0x85 && data[1] == 0x00 && data[2] == 0x04 && data[3] == 0x02)){
            return -1;
        }
        return data[5] & 0xff;
    }

    public static boolean isGPIOWriteSuccess(byte[] data){
        if(data==null ||data.length!=6){
            return false;
        }
        if(!( data[0] == (byte) 0x85 && data[1] == 0x00 && data[2] == 0x03 && data[3] == 0x03)){
            return false;
        }
        return data[4] == 0x00;
    }

    public static boolean isGPIOSyncSuccess(byte[] data){
        if(data==null ||data.length!=7){
            return false;
        }
        if(!( data[0] == (byte) 0x85 && data[1] == 0x00 && data[2] == 0x04 && data[3] == 0x04)){
            return false;
        }
        return true;
    }

    public static double getADC(byte[] data){
        if(data==null ||data.length!=7){
            return -1;
        }
        if(!( data[0] == (byte) 0x85 && data[1] == 0x00 && data[2] == 0x04 && data[3] == 0x05)
                 ){
            return -1;
        }
        double adc = (double)((data[4] & 0xff) * 256 + (data[5] & 0xff)) / 2048 * 1.05;

        //String.format(Locale.US,"%.2f", adc);
        return adc;
    }
}
