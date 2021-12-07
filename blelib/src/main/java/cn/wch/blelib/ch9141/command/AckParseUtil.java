package cn.wch.blelib.ch9141.command;

public class AckParseUtil {

    /**
     * 判断设置波特率命令的ACK是否成功
     * @param source
     * @return
     */
    public static boolean isValidBaudRateAck(byte[] source){
        if(source==null || source.length!=12){
            return false;
        }
        return source[0]==(byte)0x86;
    }

    /**
     * 判断设置流控以及Modem状态的命令的ACK是否成功
     * @param source
     * @return
     */
    public static boolean isValidFlowAck(byte[] source){
        if(source==null || source.length!=8){
            return false;
        }
        return source[0]==(byte)0x87;
    }
}
