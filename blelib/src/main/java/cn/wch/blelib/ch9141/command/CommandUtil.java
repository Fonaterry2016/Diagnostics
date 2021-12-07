package cn.wch.blelib.ch9141.command;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CommandUtil {
    /**
     * 根据需要设置波特率以及其他参数，获取需要发送的命令
     * @param baudRate 波特率 例如9600，115200
     * @param dataBit 数据位 5-8
     * @param stopBit 停止位 1-2
     * @param parity 校验位 0：无校验；1：奇校验；2：偶校验；3：标志位；4：空白位
     * @return 需要发送的命令
     */
    public static byte[] getBaudRateCommand(int baudRate,int dataBit,int stopBit,int parity){
        ByteBuffer byteBuffer=ByteBuffer.allocate(11).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(CommandCode.SET_BAUD);
        byteBuffer.put(new byte[]{0x00,0x09});
        byteBuffer.put((byte) 0x00);
        byteBuffer.putInt(baudRate);
        byteBuffer.put((byte)dataBit);
        byteBuffer.put((byte)stopBit);
        byteBuffer.put((byte)parity);
        byte[] array = byteBuffer.array();
        return computeSum(array);
    }

    /**
     *根据硬件流控以及Modem状态，获取需要发送的命令
     * @param flow 硬件流控开关 true 开；false 关
     * @param DTR 1 有效，低电平；0 无效，高电平
     * @param RTS 1 有效，低电平；0 无效，高电平
     * @return 需要发送的命令
     */
    public static byte[] getFlowCommand(boolean flow,int DTR,int RTS){
        ByteBuffer byteBuffer=ByteBuffer.allocate(7).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(CommandCode.SET_FLOW);
        byteBuffer.put(new byte[]{0x00,0x05});
        byteBuffer.put((byte) 0x00);
        byteBuffer.put(flow ? (byte)0x01 : (byte)0x00);
        byteBuffer.put((byte) DTR);
        byteBuffer.put((byte) RTS);
        byte[] array = byteBuffer.array();
        return computeSum(array);
    }


    /**
     * 计算校验和
     * @return 校验和
     */
    private static  byte[] computeSum(byte[] source){
        int sum=0;
        for (int i = 3; i < source.length; i++) {
            sum+=(source[i] & 0xff);
        }
        ByteBuffer byteBuffer=ByteBuffer.allocate(source.length+1);
        byteBuffer.put(source);
        byteBuffer.put((byte)sum);
        return byteBuffer.array();
    }
}
