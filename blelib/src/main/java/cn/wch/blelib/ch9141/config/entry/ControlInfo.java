package cn.wch.blelib.ch9141.config.entry;

public class ControlInfo {
    //记录当前image标志
    byte imageFlag;
    byte[] revd;
    /*V1.03*/
    //参数标志
    byte exd_para_flag;
    //版本信息
    byte[] exd_para_ver;
    //蓝牙接口配置使能
    byte ble_cfg_enable;
    /*V1.04*/
    //广播通道设置
    byte broadcast_ch_cfg;
    //按位定义配置项
    byte[] bit_switch_cfg;

    byte rc_32k_cali_method;//内部RC校准方式：定时or温度
    byte ble_rf_cali_method;//蓝牙RF校准方式：定时or温度
    byte rc_32k_cali_temperature;//设置变化的温度边界
    byte ble_rf_cali_temperature;//设置变化的温度边界
    int rc_32k_cali_time;//内部32K校准时间，选择温度时则定时查温度，单位：ms
    int ble_rf_cali_time;//蓝牙RF校准时间，选择温度时则定时查温度，单位：ms

    byte gpio_en;
    byte tnow_en;
    byte ble_sta_en;
    byte revd1;
    int ls_dcdc_adc_func;

    public byte getImageFlag() {
        return imageFlag;
    }

    public void setImageFlag(byte imageFlag) {
        this.imageFlag = imageFlag;
    }

    public byte[] getRevd() {
        return revd;
    }

    public void setRevd(byte[] revd) {
        this.revd = revd;
    }

    public byte getExd_para_flag() {
        return exd_para_flag;
    }

    public void setExd_para_flag(byte exd_para_flag) {
        this.exd_para_flag = exd_para_flag;
    }

    public byte[] getExd_para_ver() {
        return exd_para_ver;
    }

    public void setExd_para_ver(byte[] exd_para_ver) {
        this.exd_para_ver = exd_para_ver;
    }

    public byte getBle_cfg_enable() {
        return ble_cfg_enable;
    }

    public void setBle_cfg_enable(byte ble_cfg_enable) {
        this.ble_cfg_enable = ble_cfg_enable;
    }

    public byte getBroadcast_ch_cfg() {
        return broadcast_ch_cfg;
    }

    public void setBroadcast_ch_cfg(byte broadcast_ch_cfg) {
        this.broadcast_ch_cfg = broadcast_ch_cfg;
    }

    public byte[] getBit_switch_cfg() {
        return bit_switch_cfg;
    }

    public void setBit_switch_cfg(byte[] bit_switch_cfg) {
        this.bit_switch_cfg = bit_switch_cfg;
    }

    public byte getRc_32k_cali_method() {
        return rc_32k_cali_method;
    }

    public void setRc_32k_cali_method(byte rc_32k_cali_method) {
        this.rc_32k_cali_method = rc_32k_cali_method;
    }

    public byte getBle_rf_cali_method() {
        return ble_rf_cali_method;
    }

    public void setBle_rf_cali_method(byte ble_rf_cali_method) {
        this.ble_rf_cali_method = ble_rf_cali_method;
    }

    public byte getRc_32k_cali_temperature() {
        return rc_32k_cali_temperature;
    }

    public void setRc_32k_cali_temperature(byte rc_32k_cali_temperature) {
        this.rc_32k_cali_temperature = rc_32k_cali_temperature;
    }

    public byte getBle_rf_cali_temperature() {
        return ble_rf_cali_temperature;
    }

    public void setBle_rf_cali_temperature(byte ble_rf_cali_temperature) {
        this.ble_rf_cali_temperature = ble_rf_cali_temperature;
    }

    public int getRc_32k_cali_time() {
        return rc_32k_cali_time;
    }

    public void setRc_32k_cali_time(int rc_32k_cali_time) {
        this.rc_32k_cali_time = rc_32k_cali_time;
    }

    public int getBle_rf_cali_time() {
        return ble_rf_cali_time;
    }

    public void setBle_rf_cali_time(int ble_rf_cali_time) {
        this.ble_rf_cali_time = ble_rf_cali_time;
    }

    public byte getGpio_en() {
        return gpio_en;
    }

    public void setGpio_en(byte gpio_en) {
        this.gpio_en = gpio_en;
    }

    public byte getTnow_en() {
        return tnow_en;
    }

    public void setTnow_en(byte tnow_en) {
        this.tnow_en = tnow_en;
    }

    public byte getBle_sta_en() {
        return ble_sta_en;
    }

    public void setBle_sta_en(byte ble_sta_en) {
        this.ble_sta_en = ble_sta_en;
    }

    public byte getRevd1() {
        return revd1;
    }

    public void setRevd1(byte revd1) {
        this.revd1 = revd1;
    }

    public int getLs_dcdc_adc_func() {
        return ls_dcdc_adc_func;
    }

    public void setLs_dcdc_adc_func(int ls_dcdc_adc_func) {
        this.ls_dcdc_adc_func = ls_dcdc_adc_func;
    }
}
