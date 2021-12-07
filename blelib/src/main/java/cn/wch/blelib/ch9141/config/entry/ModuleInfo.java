package cn.wch.blelib.ch9141.config.entry;

public class ModuleInfo {

    //参数标志
    byte flag;
    //模块参数
    String moduleName;
    String moduleAddress;
    String connectAddress;
    String version;
    String hello;

    //串口参数
    int serialBaudRate;
    byte serialDataBits;
    byte serialParity;
    byte serialStopBits;
    short serialTimeout;

    //低功耗睡眠时间
    int lowPowerSleepTime;

    //低功耗模式
    byte lowPowerMode;

    //工作模式和发射功率
    byte chipWorkMode;
    byte chipTransmitPower;

    //广播模式参数
    byte advEnable;
    byte advMode;
    short advInterval;

    //设备模式参数
    short peripheralMinInterval;
    short peripheralMaxInterval;
    short peripheralTimeout;
    String peripheralName;
    byte peripheralPasswordEnable;
    byte peripheralPasswordLength;
    String peripheralPassword;

    //主机模式参数1
    byte hostConnectFlag_1;
    String hostConnectAddress_1;
    String hostPassword_1;

    //主机模式参数2
    byte hostConnectFlag_2;
    String hostConnectAddress_2;
    String hostPassword_2;

    //主机模式参数3
    byte hostConnectFlag_3;
    String hostConnectAddress_3;
    String hostPassword_3;

    //主机模式参数4
    byte hostConnectFlag_4;
    String hostConnectAddress_4;
    String hostPassword_4;

    //GPIO参数
    byte gpioMode;
    int gpioMode_0;
    int gpioMode_1;
    int gpioMode_2;
    int gpioMode_3;
    int gpioMode_4;
    int gpioMode_5;
    int gpioMode_6;
    int gpioMode_7;


    byte gpioValue;
    int gpioValue_0;
    int gpioValue_1;
    int gpioValue_2;
    int gpioValue_3;
    int gpioValue_4;
    int gpioValue_5;
    int gpioValue_6;
    int gpioValue_7;

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleAddress() {
        return moduleAddress;
    }

    public void setModuleAddress(String moduleAddress) {
        this.moduleAddress = moduleAddress;
    }

    public String getConnectAddress() {
        return connectAddress;
    }

    public void setConnectAddress(String connectAddress) {
        this.connectAddress = connectAddress;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }

    public int getSerialBaudRate() {
        return serialBaudRate;
    }

    public void setSerialBaudRate(int serialBaudRate) {
        this.serialBaudRate = serialBaudRate;
    }

    public byte getSerialDataBits() {
        return serialDataBits;
    }

    public void setSerialDataBits(byte serialDataBits) {
        this.serialDataBits = serialDataBits;
    }

    public byte getSerialParity() {
        return serialParity;
    }

    public void setSerialParity(byte serialParity) {
        this.serialParity = serialParity;
    }

    public byte getSerialStopBits() {
        return serialStopBits;
    }

    public void setSerialStopBits(byte serialStopBits) {
        this.serialStopBits = serialStopBits;
    }

    public short getSerialTimeout() {
        return serialTimeout;
    }

    public void setSerialTimeout(short serialTimeout) {
        this.serialTimeout = serialTimeout;
    }

    public int getLowPowerSleepTime() {
        return lowPowerSleepTime;
    }

    public void setLowPowerSleepTime(int lowPowerSleepTime) {
        this.lowPowerSleepTime = lowPowerSleepTime;
    }

    public byte getLowPowerMode() {
        return lowPowerMode;
    }

    public void setLowPowerMode(byte lowPowerMode) {
        this.lowPowerMode = lowPowerMode;
    }

    public byte getChipWorkMode() {
        return chipWorkMode;
    }

    public void setChipWorkMode(byte chipWorkMode) {
        this.chipWorkMode = chipWorkMode;
    }

    public byte getChipTransmitPower() {
        return chipTransmitPower;
    }

    public void setChipTransmitPower(byte chipTransmitPower) {
        this.chipTransmitPower = chipTransmitPower;
    }

    public byte getAdvEnable() {
        return advEnable;
    }

    public void setAdvEnable(byte advEnable) {
        this.advEnable = advEnable;
    }

    public byte getAdvMode() {
        return advMode;
    }

    public void setAdvMode(byte advMode) {
        this.advMode = advMode;
    }

    public short getAdvInterval() {
        return advInterval;
    }

    public void setAdvInterval(short advInterval) {
        this.advInterval = advInterval;
    }

    public short getPeripheralMinInterval() {
        return peripheralMinInterval;
    }

    public void setPeripheralMinInterval(short peripheralMinInterval) {
        this.peripheralMinInterval = peripheralMinInterval;
    }

    public short getPeripheralMaxInterval() {
        return peripheralMaxInterval;
    }

    public void setPeripheralMaxInterval(short peripheralMaxInterval) {
        this.peripheralMaxInterval = peripheralMaxInterval;
    }

    public short getPeripheralTimeout() {
        return peripheralTimeout;
    }

    public void setPeripheralTimeout(short peripheralTimeout) {
        this.peripheralTimeout = peripheralTimeout;
    }

    public String getPeripheralName() {
        return peripheralName;
    }

    public void setPeripheralName(String peripheralName) {
        this.peripheralName = peripheralName;
    }

    public byte getPeripheralPasswordEnable() {
        return peripheralPasswordEnable;
    }

    public void setPeripheralPasswordEnable(byte peripheralPasswordEnable) {
        this.peripheralPasswordEnable = peripheralPasswordEnable;
    }

    public byte getPeripheralPasswordLength() {
        return peripheralPasswordLength;
    }

    public void setPeripheralPasswordLength(byte peripheralPasswordLength) {
        this.peripheralPasswordLength = peripheralPasswordLength;
    }

    public String getPeripheralPassword() {
        return peripheralPassword;
    }

    public void setPeripheralPassword(String peripheralPassword) {
        this.peripheralPassword = peripheralPassword;
    }

    public byte getHostConnectFlag_1() {
        return hostConnectFlag_1;
    }

    public void setHostConnectFlag_1(byte hostConnectFlag_1) {
        this.hostConnectFlag_1 = hostConnectFlag_1;
    }

    public String getHostConnectAddress_1() {
        return hostConnectAddress_1;
    }

    public void setHostConnectAddress_1(String hostConnectAddress_1) {
        this.hostConnectAddress_1 = hostConnectAddress_1;
    }

    public String getHostPassword_1() {
        return hostPassword_1;
    }

    public void setHostPassword_1(String hostPassword_1) {
        this.hostPassword_1 = hostPassword_1;
    }

    public byte getHostConnectFlag_2() {
        return hostConnectFlag_2;
    }

    public void setHostConnectFlag_2(byte hostConnectFlag_2) {
        this.hostConnectFlag_2 = hostConnectFlag_2;
    }

    public String getHostConnectAddress_2() {
        return hostConnectAddress_2;
    }

    public void setHostConnectAddress_2(String hostConnectAddress_2) {
        this.hostConnectAddress_2 = hostConnectAddress_2;
    }

    public String getHostPassword_2() {
        return hostPassword_2;
    }

    public void setHostPassword_2(String hostPassword_2) {
        this.hostPassword_2 = hostPassword_2;
    }

    public byte getHostConnectFlag_3() {
        return hostConnectFlag_3;
    }

    public void setHostConnectFlag_3(byte hostConnectFlag_3) {
        this.hostConnectFlag_3 = hostConnectFlag_3;
    }

    public String getHostConnectAddress_3() {
        return hostConnectAddress_3;
    }

    public void setHostConnectAddress_3(String hostConnectAddress_3) {
        this.hostConnectAddress_3 = hostConnectAddress_3;
    }

    public String getHostPassword_3() {
        return hostPassword_3;
    }

    public void setHostPassword_3(String hostPassword_3) {
        this.hostPassword_3 = hostPassword_3;
    }

    public byte getHostConnectFlag_4() {
        return hostConnectFlag_4;
    }

    public void setHostConnectFlag_4(byte hostConnectFlag_4) {
        this.hostConnectFlag_4 = hostConnectFlag_4;
    }

    public String getHostConnectAddress_4() {
        return hostConnectAddress_4;
    }

    public void setHostConnectAddress_4(String hostConnectAddress_4) {
        this.hostConnectAddress_4 = hostConnectAddress_4;
    }

    public String getHostPassword_4() {
        return hostPassword_4;
    }

    public void setHostPassword_4(String hostPassword_4) {
        this.hostPassword_4 = hostPassword_4;
    }

    public byte getGpioMode() {
        return gpioMode;
    }

    public void setGpioMode(byte gpioMode) {
        this.gpioMode = gpioMode;
    }

    public int getGpioMode_0() {
        return gpioMode_0;
    }

    public void setGpioMode_0(int gpioMode_0) {
        this.gpioMode_0 = gpioMode_0;
    }

    public int getGpioMode_1() {
        return gpioMode_1;
    }

    public void setGpioMode_1(int gpioMode_1) {
        this.gpioMode_1 = gpioMode_1;
    }

    public int getGpioMode_2() {
        return gpioMode_2;
    }

    public void setGpioMode_2(int gpioMode_2) {
        this.gpioMode_2 = gpioMode_2;
    }

    public int getGpioMode_3() {
        return gpioMode_3;
    }

    public void setGpioMode_3(int gpioMode_3) {
        this.gpioMode_3 = gpioMode_3;
    }

    public int getGpioMode_4() {
        return gpioMode_4;
    }

    public void setGpioMode_4(int gpioMode_4) {
        this.gpioMode_4 = gpioMode_4;
    }

    public int getGpioMode_5() {
        return gpioMode_5;
    }

    public void setGpioMode_5(int gpioMode_5) {
        this.gpioMode_5 = gpioMode_5;
    }

    public int getGpioMode_6() {
        return gpioMode_6;
    }

    public void setGpioMode_6(int gpioMode_6) {
        this.gpioMode_6 = gpioMode_6;
    }

    public int getGpioMode_7() {
        return gpioMode_7;
    }

    public void setGpioMode_7(int gpioMode_7) {
        this.gpioMode_7 = gpioMode_7;
    }

    public byte getGpioValue() {
        return gpioValue;
    }

    public void setGpioValue(byte gpioValue) {
        this.gpioValue = gpioValue;
    }

    public int getGpioValue_0() {
        return gpioValue_0;
    }

    public void setGpioValue_0(int gpioValue_0) {
        this.gpioValue_0 = gpioValue_0;
    }

    public int getGpioValue_1() {
        return gpioValue_1;
    }

    public void setGpioValue_1(int gpioValue_1) {
        this.gpioValue_1 = gpioValue_1;
    }

    public int getGpioValue_2() {
        return gpioValue_2;
    }

    public void setGpioValue_2(int gpioValue_2) {
        this.gpioValue_2 = gpioValue_2;
    }

    public int getGpioValue_3() {
        return gpioValue_3;
    }

    public void setGpioValue_3(int gpioValue_3) {
        this.gpioValue_3 = gpioValue_3;
    }

    public int getGpioValue_4() {
        return gpioValue_4;
    }

    public void setGpioValue_4(int gpioValue_4) {
        this.gpioValue_4 = gpioValue_4;
    }

    public int getGpioValue_5() {
        return gpioValue_5;
    }

    public void setGpioValue_5(int gpioValue_5) {
        this.gpioValue_5 = gpioValue_5;
    }

    public int getGpioValue_6() {
        return gpioValue_6;
    }

    public void setGpioValue_6(int gpioValue_6) {
        this.gpioValue_6 = gpioValue_6;
    }

    public int getGpioValue_7() {
        return gpioValue_7;
    }

    public void setGpioValue_7(int gpioValue_7) {
        this.gpioValue_7 = gpioValue_7;
    }
}
