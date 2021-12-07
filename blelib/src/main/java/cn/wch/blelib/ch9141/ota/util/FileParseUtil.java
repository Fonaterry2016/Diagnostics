package cn.wch.blelib.ch9141.ota.util;

import android.Manifest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import cn.wch.blelib.ch9141.ota.exception.CH9141OTAException;

public class FileParseUtil {

    @RequiresPermission(anyOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    public static ByteBuffer parseBinFile(@NonNull File file) throws CH9141OTAException, IOException {
        if(file==null || !file.exists() || !file.isFile() ||(!file.getName().endsWith("bin") && !file.getName().endsWith("BIN"))){
            throw new CH9141OTAException("this file don't exist or is invalid");
        }
        long length = file.length();
        if(length>Integer.MAX_VALUE){
            throw new CH9141OTAException("this file is too big");
        }
        //读取文件
        FileInputStream stream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(stream);
        int readLen=0;
        int arraySize=1024;
        byte[] arrayBuffer=new byte[arraySize];
        ByteBuffer byteBuffer=ByteBuffer.allocate(bufferedInputStream.available());
        while ((readLen=bufferedInputStream.read(arrayBuffer))!=-1){
            byteBuffer.put(arrayBuffer,0,readLen);
        }
        bufferedInputStream.close();
        stream.close();
        byteBuffer.flip();
        return byteBuffer;
    }

    private static boolean hexStringValid(String s){
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(('0'<=chars[i] && chars[i]<='9') || ('a'<=chars[i] && chars[i]<='f') || ('A'<=chars[i] && chars[i]<='F')){

            }else {
                return false;
            }
        }
        return true;
    }

    public static byte[] hexString2ByteArray(String hexString) {
        try {
            if (hexString == null || hexString.equals("")) {
                return null;
            }
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];
            for (int i = 0; i < length; i++) {
                int pos = i * 2;
                d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public static String bytesToHexString(byte[] bArr) {
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

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    static class FileStruct{
        private String start = ":";    //每一条Hex记录的起始字符“:”
        private int length = 0x00;    //数据的字节数量
        private int address = 0x0000;    //数据存放的地址
        private int type = 0xFF;    //HEX记录的类型
        private byte[] data;//一行最多有16个字节的数据
        private int check = 0xAA;    //校验和
        private int offset = 0x0000;    //偏移量
        private int format = 0x00;    //数据行所从属的记录类型

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getAddress() {
            return address;
        }

        public void setAddress(int address) {
            this.address = address;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public int getCheck() {
            return check;
        }

        public void setCheck(int check) {
            this.check = check;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getFormat() {
            return format;
        }

        public void setFormat(int format) {
            this.format = format;
        }
    }

    static class FileElement{
        private int addr;
        private int len;

        private byte[] data;

        public int getAddr() {
            return addr;
        }

        public void setAddr(int addr) {
            this.addr = addr;
        }

        public int getLen() {
            return len;
        }

        public void setLen(int len) {
            this.len = len;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }
}
