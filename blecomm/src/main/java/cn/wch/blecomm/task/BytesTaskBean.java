package cn.wch.blecomm.task;


import cn.wch.blecomm.ModbusVcRealtimeInfo;
import cn.wch.blecomm.ViewModeWithLiveData;

public class BytesTaskBean {


    private byte[] data;
    private long interval;
    //added by terry for send modbus command to BMS
    private ViewModeWithLiveData RecRealTimeInfo;

    public BytesTaskBean( byte[] data) {

        this.data = data;
    }

    public BytesTaskBean(byte[] data, long interval, ViewModeWithLiveData TempRecRealTimeInfo) {
        this.RecRealTimeInfo = TempRecRealTimeInfo;
        this.data = data;
        this.interval = interval;
    }


    public byte[] getData() {
        return data;
    }

    public long getInterval() {
        return interval;
    }

    //added by terry for send modbus command to BMS
    public ViewModeWithLiveData getModbusVcRealtimeInfo()
    {
        return RecRealTimeInfo;
    }
}
