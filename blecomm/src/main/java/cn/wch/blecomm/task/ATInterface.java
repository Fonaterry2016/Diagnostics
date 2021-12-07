package cn.wch.blecomm.task;

public interface ATInterface {
    void onPreExecute(SendType type);
    void onCount(int count);
    void onProgress(long current, long total);
    void onCancel(SendType type);
    void onResult(SendType type, boolean result);
}
