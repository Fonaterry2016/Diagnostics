package cn.wch.blelib.ch9141.config.entry.gpio;

public class GPIOWriteResult {

    boolean success=false;
    int num;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
