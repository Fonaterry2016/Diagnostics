package cn.wch.ch9141tool;

import android.app.Application;

import cn.wch.blelib.ch9141.CH9141BluetoothManager;
import cn.wch.blelib.ch9141.config.CH9141ConfigManager;
import cn.wch.blelib.ch9141.ota.CH9141OTAManager;
import cn.wch.blelib.exception.BLELibException;
import cn.wch.blelib.utils.LogUtil;

public class CH9141Application extends Application {
    private   static CH9141Application CH9141Application;
    @Override
    public void onCreate() {
        super.onCreate();
        CH9141Application =this;
        try {
            CH9141BluetoothManager.getInstance().init(this);
            CH9141OTAManager.getInstance().init(this);
            CH9141ConfigManager.getInstance().init(this);
        } catch (BLELibException e) {
            e.printStackTrace();
            LogUtil.d(e.getMessage());
        }
    }

    public static CH9141Application getContext(){
        return CH9141Application;
    }
}
