package cn.wch.blelib.ch9141.callback;

/**
 * Modem状态回调接口
 */
public interface ModemStatus {
    void onNotify(boolean DCD, boolean RI, boolean DSR, boolean CTS);
}
