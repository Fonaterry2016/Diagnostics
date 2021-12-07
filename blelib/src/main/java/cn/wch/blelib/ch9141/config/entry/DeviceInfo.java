package cn.wch.blelib.ch9141.config.entry;

public class DeviceInfo {

    //参数标志
    byte flag;
    //System ID
    byte[] systemID;
    //Model Number String
    String modelName;
    //Serial Number String
    String serialNumber;
    //Firmware Revision String
    String firmwareRevision;
    //Hardware Revision String
    String hardwareRevision;
    //Software Revision String
    String softwareRevision;
    //Manufacturer Name String
    String manufacturerName;
    //PnP ID
    byte[] PnPID;

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public byte[] getSystemID() {
        return systemID;
    }

    public void setSystemID(byte[] systemID) {
        this.systemID = systemID;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getFirmwareRevision() {
        return firmwareRevision;
    }

    public void setFirmwareRevision(String firmwareRevision) {
        this.firmwareRevision = firmwareRevision;
    }

    public String getHardwareRevision() {
        return hardwareRevision;
    }

    public void setHardwareRevision(String hardwareRevision) {
        this.hardwareRevision = hardwareRevision;
    }

    public String getSoftwareRevision() {
        return softwareRevision;
    }

    public void setSoftwareRevision(String softwareRevision) {
        this.softwareRevision = softwareRevision;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public byte[] getPnPID() {
        return PnPID;
    }

    public void setPnPID(byte[] pnPID) {
        PnPID = pnPID;
    }
}
