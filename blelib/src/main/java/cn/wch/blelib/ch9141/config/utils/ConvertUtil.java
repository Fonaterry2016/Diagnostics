package cn.wch.blelib.ch9141.config.utils;

public class ConvertUtil {

    public static String toHexString(byte[] arg,int offset, int length) {
        String result = new String();
        if (arg != null) {
            for (int i = offset; i < offset+length; i++) {
                result = result
                        + (Integer.toHexString(
                        arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])
                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])) + " ";
            }
            return result;
        }
        return "";
    }
    //convert from hex string to byte array
    public static byte[] toByteArray(String arg) {
        if (arg != null) {
            /* 1.先去除String中的' '，然后将String转换为char数组 */
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }
            /* 将char数组中的值转成一个实际的十进制数组 */
            int EvenLength = (length % 2 == 0) ? length : length + 1;
            if (EvenLength != 0) {
                int[] data = new int[EvenLength];
                data[EvenLength - 1] = 0;
                for (int i = 0; i < length; i++) {
                    if (NewArray[i] >= '0' && NewArray[i] <= '9') {
                        data[i] = NewArray[i] - '0';
                    } else if (NewArray[i] >= 'a' && NewArray[i] <= 'f') {
                        data[i] = NewArray[i] - 'a' + 10;
                    } else if (NewArray[i] >= 'A' && NewArray[i] <= 'F') {
                        data[i] = NewArray[i] - 'A' + 10;
                    }
                }
                /* 将 每个char的值每两个组成一个16进制数据 */
                byte[] byteArray = new byte[EvenLength / 2];
                for (int i = 0; i < EvenLength / 2; i++) {
                    byteArray[i] = (byte) (data[i * 2] * 16 + data[i * 2 + 1]);
                }
                return byteArray;
            }
        }
        return new byte[] {};
    }

    //invert hex string
    public static String invertHexString(String srcStr) {
        int len = srcStr.length();
        StringBuilder strBuilder = new StringBuilder(len);
        for (int i = len; i > 0; i -= 3)
            strBuilder.append(srcStr.substring(i-3, i));
        return strBuilder.toString();
    }
    //invert byte array
    public static byte[] invertByteArray(byte[] srcArray) {
        if(srcArray==null){
            return null;
        }
        int len = srcArray.length;
        byte[] dstArray = new byte[len];
        for (int i = 0; i < len; i++)
            dstArray[len - 1 - i] = srcArray[i];
        return dstArray;
    }
    //convert byte array to string
    public static String byteToString(byte[] byteArray,int offset,int len) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = offset; i < offset+len; i++)
            if (byteArray[i] >= 48 && byteArray[i] <=57 || byteArray[i] >= 65 && byteArray[i] <= 90 ||
                    byteArray[i] >= 97 && byteArray[i] <= 122 || byteArray[i] == 32)
                strBuilder.append(String.format("%c", byteArray[i]));
        return strBuilder.toString();
    }

    public static String toBinaryString(int number) {
        StringBuilder strBuilder = new StringBuilder();
        if (number >255)
            return "";
        strBuilder.append(String.format("%d", (number / 128)));
        number = number % 128;
        strBuilder.append(String.format("%d", (number / 64)));
        number = number % 64;
        strBuilder.append(String.format("%d", (number / 32)));
        number = number % 32;
        strBuilder.append(String.format("%d", (number / 16)));
        number = number % 16;
        strBuilder.append(String.format("%d", (number / 8)));
        number = number % 8;
        strBuilder.append(String.format("%d", (number / 4)));
        number = number % 4;
        strBuilder.append(String.format("%d", (number / 2)));
        number = number % 2;
        strBuilder.append(String.format("%d", (number / 1)));
        return strBuilder.toString();

    }
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv+" ");
        }
        return stringBuilder.toString();
    }
    public static String bytesToHexString(byte src){
        StringBuilder stringBuilder = new StringBuilder("");
        int v = src & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() < 2) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hv);
        return stringBuilder.toString();
    }
    public static byte[] myParseHexToBytes(final String hex) {
        if(hex.equals("") || hex.length()<2){
            return new byte[]{0x00};
        }
        String tmp = hex.substring(0).replaceAll("[^[0-9][a-f]]", "");
        byte[] bytes = new byte[tmp.length() / 2]; // every two letters in the string are one byte finally

        String part = "";

        for(int i = 0; i < bytes.length; ++i) {
            part = "0x" + tmp.substring(i*2, i*2+2);
            bytes[i] = Long.decode(part).byteValue();
        }
        return bytes;
    }
}
