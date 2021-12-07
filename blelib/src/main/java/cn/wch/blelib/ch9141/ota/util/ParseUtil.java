package cn.wch.blelib.ch9141.ota.util;

import java.util.Locale;

import cn.wch.blelib.ch9141.ota.entry.CurrentImageInfo;
import cn.wch.blelib.ch9141.ota.entry.ImageType;

public class ParseUtil {
    public static CurrentImageInfo parseImageFromResponse(byte[] response){
        if(response==null || response.length!=20){
            return null;
        }
        CurrentImageInfo imageInfo=new CurrentImageInfo();
        if(response[0]==(byte) 0x01){
            imageInfo.setType(ImageType.A);
        }else if(response[0]==(byte)0x02 ){
            imageInfo.setType(ImageType.B);
        }else if(response[0]==(byte)0xA3){
            imageInfo.setType(ImageType.C);
        }else {
            imageInfo.setType(ImageType.UNKNOWN);
        }
        imageInfo.setVersion(String.format(Locale.US,"%02X",response[7]));
        imageInfo.setOffset(FormatUtil.bytesToIntLittleEndian(response,1));
        return imageInfo;
    }

    public static boolean parseEraseResponse(byte[] response){
        return response!=null && response.length!=0 && response[0]==0x00;
    }

    public static boolean parseVerifyResponse(byte[] response){
        return response!=null && response.length!=0 && response[0]==0x00;
    }
}
