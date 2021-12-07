package cn.wch.bleconfig.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import cn.wch.blelib.ch9141.config.entry.ControlInfo;
import cn.wch.blelib.ch9141.config.entry.DeviceInfo;
import cn.wch.blelib.ch9141.config.entry.ModuleInfo;
import cn.wch.blelib.ch9141.config.entry.gpio.GPIOReadResult;
import cn.wch.blelib.ch9141.config.entry.gpio.GPIOSetResult;
import cn.wch.blelib.ch9141.config.entry.gpio.GPIOWriteResult;

public class PageViewModel extends ViewModel {

    private MutableLiveData<ModuleInfo> mModuleInfo = new MutableLiveData<>();
    private MutableLiveData<DeviceInfo> mDeviceInfo = new MutableLiveData<>();
    private MutableLiveData<ControlInfo> mControlInfo = new MutableLiveData<>();


    private MutableLiveData<ModuleInfo> mGPIOInfo=new MutableLiveData<>();
    private MutableLiveData<Double> mAdcInfo = new MutableLiveData<>();
    private MutableLiveData<GPIOReadResult> mGPIOReadResult= new MutableLiveData<>();
    private MutableLiveData<GPIOSetResult> mGPIOSetResult= new MutableLiveData<>();


    public void setModuleInfo(ModuleInfo moduleInfo) {
        mModuleInfo.postValue(moduleInfo);
    }

    public LiveData<ModuleInfo> getModuleInfo() {
        return mModuleInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        mDeviceInfo.postValue(deviceInfo);
    }

    public LiveData<DeviceInfo> getDeviceInfo() {
        return mDeviceInfo;
    }

    public void setControlInfo(ControlInfo controlInfo) {
        mControlInfo.postValue(controlInfo);
    }

    public LiveData<ControlInfo> getControlInfo() {
        return mControlInfo;
    }

    public void setGPIOAdc(Double adc) {
        mAdcInfo.postValue(adc);
    }

    public LiveData<Double> getGPIOAdc() {
        return mAdcInfo;
    }

    public void setGPIOInfo(ModuleInfo moduleInfo){
        mGPIOInfo.postValue(moduleInfo);
    }

    public LiveData<ModuleInfo> getGPIOModuleInfo(){
        return mGPIOInfo;
    }

    public void setGPIOReadResult(GPIOReadResult gpioReadResult){
        mGPIOReadResult.postValue(gpioReadResult);
    }

    public LiveData<GPIOReadResult> getGPIOReadResult(){
        return mGPIOReadResult;
    }


    public void setGPIOSetResult(GPIOSetResult gpioSetResult){
        mGPIOSetResult.postValue(gpioSetResult);
    }

    public LiveData<GPIOSetResult> getGPIOSetResult(){
        return mGPIOSetResult;
    }
}