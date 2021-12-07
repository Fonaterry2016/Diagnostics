package cn.wch.bleconfig;

import cn.wch.blelib.ch9141.config.entry.ControlInfo;
import cn.wch.blelib.ch9141.config.entry.DeviceInfo;
import cn.wch.blelib.ch9141.config.entry.ModuleInfo;

public interface ICommand {
    void getModuleInfo();
    void setModuleInfo(ModuleInfo moduleInfo);
    void getDeviceInfo();
    void setDeviceInfo(DeviceInfo deviceInfo);
    void getControlInfo();
    void setControlInfo(ControlInfo controlInfo);
    void setGPIO(int num,int dir);
    void readGPIO(int num);
    void writeGPIO(int num,int value);
    void syncGPIO();
    void readADC();
    void resetModule();
    void resetDevice();
    void resetChip();
}
