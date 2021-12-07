package cn.wch.bleconfig.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.wch.bleconfig.ICommand;
import cn.wch.bleconfig.R;
import cn.wch.bleconfig.R2;
import cn.wch.bleconfig.ui.main.PageViewModel;
import cn.wch.blelib.ch9141.config.entry.ModuleInfo;
import cn.wch.blelib.utils.BLEUtil;
import cn.wch.blelib.utils.LogUtil;


import static cn.wch.bleconfig.util.ConvertUtil.invertByteArray;
import static cn.wch.bleconfig.util.ConvertUtil.toByteArray;

public class ModuleFragment extends Fragment {

    @BindView(R2.id.value_module_name)
    EditText valueModuleName;
    @BindView(R2.id.value_module_mac)
    EditText valueModuleMac;
    @BindView(R2.id.value_conn_mac)
    EditText valueConnMac;
    @BindView(R2.id.value_module_ver)
    EditText valueModuleVer;
    @BindView(R2.id.value_hello)
    EditText valueHello;
    @BindView(R2.id.value_baud_rate)
    EditText valueBaudRate;
    @BindView(R2.id.value_data_bit)
    EditText valueDataBit;
    @BindView(R2.id.value_pari_bit)
    Spinner valueParityBit;
    @BindView(R2.id.value_stop_bit)
    EditText valueStopBit;
    @BindView(R2.id.value_timeout)
    EditText valueTimeout;
    @BindView(R2.id.value_module_mode)
    Spinner valueModuleMode;
    @BindView(R2.id.value_module_tpl)
    Spinner valueModuleTpl;
    @BindView(R2.id.value_adv_en)
    Spinner valueAdvEn;
    @BindView(R2.id.value_conn_interval_min)
    EditText valueConnIntervalMin;
    @BindView(R2.id.value_conn_interval_max)
    EditText valueConnIntervalMax;
    @BindView(R2.id.value_conn_timeout)
    EditText valueConnTimeout;
    @BindView(R2.id.value_peri_name)
    EditText valuePeriName;
    @BindView(R2.id.value_pw_en)
    Spinner valuePwEn;
    @BindView(R2.id.value_pw_len)
    EditText valuePwLen;
    @BindView(R2.id.value_pw)
    EditText valuePw;
    @BindView(R2.id.value_conn_add_flag_1)
    EditText valueConnAddFlag1;
    @BindView(R2.id.value_conn_add_1)
    EditText valueConnAdd1;
    @BindView(R2.id.value_conn_pw_1)
    EditText valueConnPw1;
    @BindView(R2.id.value_conn_add_flag_2)
    EditText valueConnAddFlag2;
    @BindView(R2.id.value_conn_add_2)
    EditText valueConnAdd2;
    @BindView(R2.id.value_conn_pw_2)
    EditText valueConnPw2;
    @BindView(R2.id.value_conn_add_flag_3)
    EditText valueConnAddFlag3;
    @BindView(R2.id.value_conn_add_3)
    EditText valueConnAdd3;
    @BindView(R2.id.value_conn_pw_3)
    EditText valueConnPw3;
    @BindView(R2.id.value_conn_add_flag_4)
    EditText valueConnAddFlag4;
    @BindView(R2.id.value_conn_add_4)
    EditText valueConnAdd4;
    @BindView(R2.id.value_conn_pw_4)
    EditText valueConnPw4;
    @BindView(R2.id.value_gpio_mode_7)
    Spinner valueGpioMode7;
    @BindView(R2.id.value_gpio_mode_6)
    Spinner valueGpioMode6;
    @BindView(R2.id.value_gpio_mode_5)
    Spinner valueGpioMode5;
    @BindView(R2.id.value_gpio_mode_4)
    Spinner valueGpioMode4;
    @BindView(R2.id.value_gpio_mode_3)
    TextView valueGpioMode3;
    @BindView(R2.id.value_gpio_mode_2)
    TextView valueGpioMode2;
    @BindView(R2.id.value_gpio_mode_1)
    TextView valueGpioMode1;
    @BindView(R2.id.value_gpio_mode_0)
    TextView valueGpioMode0;
    @BindView(R2.id.value_gpio_value_7)
    Spinner valueGpioValue7;
    @BindView(R2.id.value_gpio_value_6)
    Spinner valueGpioValue6;
    @BindView(R2.id.value_gpio_value_5)
    Spinner valueGpioValue5;
    @BindView(R2.id.value_gpio_value_4)
    Spinner valueGpioValue4;
    @BindView(R2.id.value_gpio_value_3)
    Spinner valueGpioValue3;
    @BindView(R2.id.value_gpio_value_2)
    Spinner valueGpioValue2;
    @BindView(R2.id.value_gpio_value_1)
    TextView valueGpioValue1;
    @BindView(R2.id.value_gpio_value_0)
    TextView valueGpioValue0;

    Unbinder unbinder;
    @BindView(R2.id.value_adv_interval)
    EditText valueAdvInterval;


    @BindView(R2.id.value_low_power_mode)
    Spinner ValueLowPowerModel;

    private PageViewModel pageViewModel;
    private Context mContext;
    private ICommand iCommand;
    private Handler handler = new Handler(Looper.getMainLooper());

    private int valGPIOMode_4, valGPIOMode_5, valGPIOMode_6, valGPIOMode_7;
    private int valGPIOValue_2, valGPIOValue_3, valGPIOValue_4, valGPIOValue_5, valGPIOValue_6, valGPIOValue_7;
    private int lowPowerModel;

    public static ModuleFragment newInstance() {
        ModuleFragment fragment = new ModuleFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_module, container, false);
        unbinder = ButterKnife.bind(this, inflate);
        init(inflate);
        return inflate;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public String toString() {
        return "配置参数";
    }

    public void register(ICommand iCommand) {
        this.iCommand = iCommand;
    }

    private void init(View v) {
        initSpinner();
        initWidget(v);
        pageViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(PageViewModel.class);
        pageViewModel.getModuleInfo().observe(this, new Observer<ModuleInfo>() {
            @Override
            public void onChanged(ModuleInfo moduleInfo) {
                updateUi(moduleInfo);
            }
        });
    }


    private void initWidget(View v) {
        Button getModule = v.findViewById(R.id.getModule);
        Button setModule = v.findViewById(R.id.setModule);

        getModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCommand != null) {
                    iCommand.getModuleInfo();
                }
            }
        });
        setModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCommand != null) {
                    ModuleInfo newDeviceInfo = getNewModuleInfo();
                    if(newDeviceInfo!=null){
                        iCommand.setModuleInfo(newDeviceInfo);
                    }

                }
            }
        });
    }


    private void initSpinner() {
        //spinner adapter of parity bits
        ArrayAdapter<CharSequence> adapter_1 = ArrayAdapter.createFromResource(mContext,
                R.array.pari_bits, R.layout.item_spinner);
        adapter_1.setDropDownViewResource(R.layout.item_spinner);
        valueParityBit.setAdapter(adapter_1);
        valueParityBit.setGravity(0x10);
        valueParityBit.setSelection(0);
        valueParityBit.setOnItemSelectedListener(new onParityBitsSelectedListener());

        //spinner adapter of module mode
        ArrayAdapter<CharSequence> adapter_2 = ArrayAdapter.createFromResource(mContext,
                R.array.mod_mode, R.layout.item_spinner);
        adapter_2.setDropDownViewResource(R.layout.item_spinner);
        valueModuleMode.setAdapter(adapter_2);
        valueModuleMode.setGravity(0x10);
        valueModuleMode.setSelection(0);
        valueModuleMode.setOnItemSelectedListener(new onModuleModeSelectedListener());

        //spinner adapter: module TPL
        ArrayAdapter<CharSequence> adapter_3 = ArrayAdapter.createFromResource(mContext,
                R.array.mod_tpl, R.layout.item_spinner);
        adapter_3.setDropDownViewResource(R.layout.item_spinner);
        valueModuleTpl.setAdapter(adapter_3);
        valueModuleTpl.setGravity(0x10);
        valueModuleTpl.setSelection(0);
        valueModuleTpl.setOnItemSelectedListener(new onModuleTPLSelectedListener());

        //spinner advertise enable adapter
        ArrayAdapter<CharSequence> adapter_4 = ArrayAdapter.createFromResource(mContext,
                R.array.adv_en, R.layout.item_spinner);
        adapter_4.setDropDownViewResource(R.layout.item_spinner);
        valueAdvEn.setAdapter(adapter_4);
        valueAdvEn.setGravity(0x10);
        valueAdvEn.setSelection(0);
        valueAdvEn.setOnItemSelectedListener(new onAdvertiseEnableSelectedListener());

        //spinner password enable adapter
        ArrayAdapter<CharSequence> adapter_5 = ArrayAdapter.createFromResource(mContext,
                R.array.pw_en, R.layout.item_spinner);
        adapter_5.setDropDownViewResource(R.layout.item_spinner);
        valuePwEn.setAdapter(adapter_5);
        valuePwEn.setGravity(0x10);
        valuePwEn.setSelection(0);
        valuePwEn.setOnItemSelectedListener(new onPasswordEnableSelectedListener());

        //8 spinners of gpio mode
        ArrayAdapter<CharSequence> adapter_6 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_spinner, R.layout.item_spinner);
        adapter_6.setDropDownViewResource(R.layout.item_spinner);
        valueGpioMode4.setAdapter(adapter_6);
        valueGpioMode4.setGravity(0x10);
        valueGpioMode4.setSelection(0);
        valueGpioMode4.setOnItemSelectedListener(new onGPIOModeSelectedListener_4());

        ArrayAdapter<CharSequence> adapter_7 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_spinner, R.layout.item_spinner);
        adapter_7.setDropDownViewResource(R.layout.item_spinner);
        valueGpioMode5.setAdapter(adapter_7);
        valueGpioMode5.setGravity(0x10);
        valueGpioMode5.setSelection(0);
        valueGpioMode5.setOnItemSelectedListener(new onGPIOModeSelectedListener_5());

        ArrayAdapter<CharSequence> adapter_8 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_spinner, R.layout.item_spinner);
        adapter_8.setDropDownViewResource(R.layout.item_spinner);
        valueGpioMode6.setAdapter(adapter_8);
        valueGpioMode6.setGravity(0x10);
        valueGpioMode6.setSelection(0);
        valueGpioMode6.setOnItemSelectedListener(new onGPIOModeSelectedListener_6());

        ArrayAdapter<CharSequence> adapter_9 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_spinner, R.layout.item_spinner);
        adapter_9.setDropDownViewResource(R.layout.item_spinner);
        valueGpioMode7.setAdapter(adapter_9);
        valueGpioMode7.setGravity(0x10);
        valueGpioMode7.setSelection(0);
        valueGpioMode7.setOnItemSelectedListener(new onGPIOModeSelectedListener_7());

        ArrayAdapter<CharSequence> adapter_10 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_spinner, R.layout.item_spinner);
        adapter_10.setDropDownViewResource(R.layout.item_spinner);
        valueGpioValue2.setAdapter(adapter_10);
        valueGpioValue2.setGravity(0x10);
        valueGpioValue2.setSelection(0);
        valueGpioValue2.setOnItemSelectedListener(new onGPIOValueSelectedListener_2());

        ArrayAdapter<CharSequence> adapter_11 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_spinner, R.layout.item_spinner);
        adapter_11.setDropDownViewResource(R.layout.item_spinner);
        valueGpioValue3.setAdapter(adapter_11);
        valueGpioValue3.setGravity(0x10);
        valueGpioValue3.setSelection(0);
        valueGpioValue3.setOnItemSelectedListener(new onGPIOValueSelectedListener_3());

        ArrayAdapter<CharSequence> adapter_12 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_spinner, R.layout.item_spinner);
        adapter_12.setDropDownViewResource(R.layout.item_spinner);
        valueGpioValue4.setAdapter(adapter_12);
        valueGpioValue4.setGravity(0x10);
        valueGpioValue4.setSelection(0);
        valueGpioValue4.setOnItemSelectedListener(new onGPIOValueSelectedListener_4());

        ArrayAdapter<CharSequence> adapter_13 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_spinner, R.layout.item_spinner);
        adapter_13.setDropDownViewResource(R.layout.item_spinner);
        valueGpioValue5.setAdapter(adapter_13);
        valueGpioValue5.setGravity(0x10);
        valueGpioValue5.setSelection(0);
        valueGpioValue5.setOnItemSelectedListener(new onGPIOValueSelectedListener_5());

        ArrayAdapter<CharSequence> adapter_14 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_spinner, R.layout.item_spinner);
        adapter_14.setDropDownViewResource(R.layout.item_spinner);
        valueGpioValue6.setAdapter(adapter_14);
        valueGpioValue6.setGravity(0x10);
        valueGpioValue6.setSelection(0);
        valueGpioValue6.setOnItemSelectedListener(new onGPIOValueSelectedListener_6());

        ArrayAdapter<CharSequence> adapter_15 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_spinner, R.layout.item_spinner);
        adapter_15.setDropDownViewResource(R.layout.item_spinner);
        valueGpioValue7.setAdapter(adapter_15);
        valueGpioValue7.setGravity(0x10);
        valueGpioValue7.setSelection(0);
        valueGpioValue7.setOnItemSelectedListener(new onGPIOValueSelectedListener_7());

        //新增
        //spinner advertise enable adapter
        ArrayAdapter<CharSequence> adapter_16 = ArrayAdapter.createFromResource(mContext,
                R.array.low_power, R.layout.item_spinner);
        adapter_16.setDropDownViewResource(R.layout.item_spinner);
        ValueLowPowerModel.setAdapter(adapter_16);
        ValueLowPowerModel.setGravity(0x10);
        ValueLowPowerModel.setSelection(0);
        ValueLowPowerModel.setOnItemSelectedListener(new onLowPowerEnableSelectedListener());

    }

    /**************************************Spinner Listener****************************************************/
    private class onParityBitsSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //module mode value spinner listener
    private class onModuleModeSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //module TPL value spinner listener
    private class onModuleTPLSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //module advertise enable spinner listener
    private class onAdvertiseEnableSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
    //module low power enable spinner listener
    private class onLowPowerEnableSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            lowPowerModel = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //module password enable spinner listener
    private class onPasswordEnableSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }

    //gpio mode bit 4 spinner listener
    private class onGPIOModeSelectedListener_4 implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            valGPIOMode_4 = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //gpio mode bit 5 spinner listener
    private class onGPIOModeSelectedListener_5 implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            valGPIOMode_5 = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //gpio mode bit 6 spinner listener
    private class onGPIOModeSelectedListener_6 implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            valGPIOMode_6 = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //gpio mode bit 7 spinner listener
    private class onGPIOModeSelectedListener_7 implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            valGPIOMode_7 = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //gpio value bit 2 spinner listener
    private class onGPIOValueSelectedListener_2 implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            valGPIOValue_2 = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //gpio value bit 3 spinner listener
    private class onGPIOValueSelectedListener_3 implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            valGPIOValue_3 = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //gpio value bit 4 spinner listener
    private class onGPIOValueSelectedListener_4 implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            valGPIOValue_4 = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //gpio value bit 5 spinner listener
    private class onGPIOValueSelectedListener_5 implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            valGPIOValue_5 = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //gpio value bit 6 spinner listener
    private class onGPIOValueSelectedListener_6 implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            valGPIOValue_6 = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //gpio value bit 7 spinner listener
    private class onGPIOValueSelectedListener_7 implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            valGPIOValue_7 = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }


    private ModuleInfo getNewModuleInfo() {
        if (!checkValid()) {
            return null;
        }
        return getModuleConfig();
    }

    private boolean checkValid() {
        if (TextUtils.isEmpty(valueModuleName.getText().toString()) || TextUtils.isEmpty(valueModuleMac.getText().toString()) ||
                TextUtils.isEmpty(valueConnMac.getText().toString()) || TextUtils.isEmpty(valueModuleVer.getText().toString()) ||
                TextUtils.isEmpty(valueHello.getText().toString()) || TextUtils.isEmpty(valueBaudRate.getText().toString()) ||
                TextUtils.isEmpty(valueDataBit.getText().toString()) || TextUtils.isEmpty(valueStopBit.getText().toString()) ||
                TextUtils.isEmpty(valueTimeout.getText().toString()) || TextUtils.isEmpty(valueConnIntervalMax.getText().toString()) ||
                TextUtils.isEmpty(valueConnIntervalMin.getText().toString()) || TextUtils.isEmpty(valueConnTimeout.getText().toString()) ||
                TextUtils.isEmpty(valuePeriName.getText().toString())
                || TextUtils.isEmpty(valueConnAddFlag1.getText().toString()) || TextUtils.isEmpty(valueConnAddFlag2.getText().toString())
                || TextUtils.isEmpty(valueConnAddFlag3.getText().toString()) || TextUtils.isEmpty(valueConnAddFlag4.getText().toString())
                || TextUtils.isEmpty(valueAdvInterval.getText().toString())) {
            showToast("参数不完整");
            return false;
        }
        return true;
    }

    private ModuleInfo getModuleConfig() {

        ModuleInfo moduleInfo=new ModuleInfo();
        if(TextUtils.isEmpty(valueModuleName.getText().toString())){
            showToast("模块名称不能为空");
            return null;
        }
        moduleInfo.setModuleName( valueModuleName.getText().toString());
        if(!BLEUtil.isValidMac(valueModuleMac.getText().toString())){
            showToast("模块地址不规范");
            return null;
        }

        moduleInfo.setModuleAddress( valueModuleMac.getText().toString());

        if(!BLEUtil.isValidMac(valueConnMac.getText().toString())){
            showToast("连接地址不规范");
            return null;
        }
        moduleInfo.setConnectAddress( valueConnMac.getText().toString());
        if(TextUtils.isEmpty(valueModuleVer.getText().toString())){
            showToast("版本号不能为空");
            return null;
        }
        moduleInfo.setVersion(valueModuleVer.getText().toString());

        moduleInfo.setHello(valueHello.getText().toString());

        if(TextUtils.isEmpty(valueBaudRate.getText().toString())){
            showToast("波特率不能为空");
            return null;
        }
        long t=Long.parseLong(valueBaudRate.getText().toString());
        if(t>Integer.MAX_VALUE){
            showToast("波特率超过最大值");
            return null;
        }
        moduleInfo.setSerialBaudRate(Integer.parseInt(valueBaudRate.getText().toString()));

        if(TextUtils.isEmpty(valueDataBit.getText().toString())){
            showToast("数据位不能为空");
            return null;
        }
        moduleInfo.setSerialDataBits((byte) Integer.parseInt(valueDataBit.getText().toString()));
        if(TextUtils.isEmpty(valueStopBit.getText().toString())){
            showToast("停止位不能为空");
            return null;
        }
        moduleInfo.setSerialStopBits((byte) Integer.parseInt(valueStopBit.getText().toString()));
        moduleInfo.setSerialParity((byte) valueParityBit.getSelectedItemPosition());
        if(TextUtils.isEmpty(valueTimeout.getText().toString())){
            showToast("串口超时时间不能为空");
            return null;
        }
        int i=Integer.parseInt(valueTimeout.getText().toString());
        if(i>Short.MAX_VALUE){
            showToast("串口超时时间超过最大值");
            return null;
        }
        moduleInfo.setSerialTimeout((short) Integer.parseInt(valueTimeout.getText().toString()));
        //低功耗睡眠时间目前不设置
        moduleInfo.setLowPowerSleepTime(0);
        moduleInfo.setLowPowerMode((byte) (ValueLowPowerModel.getSelectedItemPosition()));

        moduleInfo.setChipWorkMode((byte) (valueModuleMode.getSelectedItemPosition()));
        moduleInfo.setChipTransmitPower((byte) valueModuleTpl.getSelectedItemPosition());

        moduleInfo.setAdvEnable((byte) valueAdvEn.getSelectedItemPosition());
        //广播模式默认为0
        moduleInfo.setAdvMode((byte) 0x00);

        if(TextUtils.isEmpty(valueAdvInterval.getText().toString())){
            showToast("广播时间不能为空");
            return null;
        }
        int t1=Integer.parseInt(valueAdvInterval.getText().toString());
        if(t1>Short.MAX_VALUE){
            showToast("广播时间超过最大值");
            return null;
        }
        moduleInfo.setAdvInterval((short) Integer.parseInt(valueAdvInterval.getText().toString()));

        if(TextUtils.isEmpty(valueConnIntervalMin.getText().toString())){
            showToast("最小连接间隔不能为空");
            return null;
        }
        int t2=Integer.parseInt(valueConnIntervalMin.getText().toString());
        if(t2>Short.MAX_VALUE){
            showToast("最小连接间隔超过最大值");
            return null;
        }
        moduleInfo.setPeripheralMinInterval((short) Integer.parseInt(valueConnIntervalMin.getText().toString()));
        if(TextUtils.isEmpty(valueConnIntervalMax.getText().toString())){
            showToast("最大连接间隔不能为空");
            return null;
        }
        int t3=Integer.parseInt(valueConnIntervalMax.getText().toString());
        if(t3>Short.MAX_VALUE){
            showToast("最大连接间隔超过最大值");
            return null;
        }
        moduleInfo.setPeripheralMaxInterval((short) Integer.parseInt(valueConnIntervalMax.getText().toString()));
        if(TextUtils.isEmpty(valueConnTimeout.getText().toString())){
            showToast("超时时间不能为空");
            return null;
        }
        int t4=Integer.parseInt(valueConnTimeout.getText().toString());
        if(t4>Short.MAX_VALUE){
            showToast("超时时间超过最大值");
            return null;
        }
        moduleInfo.setPeripheralTimeout((short) Integer.parseInt(valueConnTimeout.getText().toString()));
        if(TextUtils.isEmpty(valuePeriName.getText().toString())){
            showToast("设备名称为空");
            return null;
        }
        moduleInfo.setPeripheralName(valuePeriName.getText().toString());
        moduleInfo.setPeripheralPasswordEnable((byte) valuePwEn.getSelectedItemPosition());
        LogUtil.d("密码使能："+(valuePwEn.getSelectedItemPosition()!=0));
        //密码长度固定位6
        moduleInfo.setPeripheralPasswordLength((byte) Integer.parseInt(valuePwLen.getText().toString()));
        if(TextUtils.isEmpty(valuePw.getText().toString()) || valuePw.getText().toString().length()!=6){
            showToast("密码长度必须为6");
            return null;
        }
        moduleInfo.setPeripheralPassword(valuePw.getText().toString());

        if(TextUtils.isEmpty(valueConnAddFlag1.getText().toString())
                || TextUtils.isEmpty(valueConnAddFlag2.getText().toString())
                || TextUtils.isEmpty(valueConnAddFlag3.getText().toString())
                || TextUtils.isEmpty(valueConnAddFlag4.getText().toString())){
            showToast("默认连接标志不能为空");
            return null;
        }

        byte[] temp = invertByteArray(toByteArray(valueConnAddFlag1.getText().toString()));
        moduleInfo.setHostConnectFlag_1((byte) temp[0]);
        temp = invertByteArray(toByteArray(valueConnAddFlag2.getText().toString()));
        moduleInfo.setHostConnectFlag_2((byte) temp[0]);
        temp = invertByteArray(toByteArray(valueConnAddFlag3.getText().toString()));
        moduleInfo.setHostConnectFlag_3((byte) temp[0]);
        temp = invertByteArray(toByteArray(valueConnAddFlag4.getText().toString()));
        moduleInfo.setHostConnectFlag_4((byte) temp[0]);

        if(!BLEUtil.isValidMac(valueConnAdd1.getText().toString())
                || !BLEUtil.isValidMac(valueConnAdd2.getText().toString())
                || !BLEUtil.isValidMac(valueConnAdd3.getText().toString())
                || !BLEUtil.isValidMac(valueConnAdd4.getText().toString())){
            showToast("默认连接地址不规范");
            return null;
        }
        moduleInfo.setHostConnectAddress_1(valueConnAdd1.getText().toString());
        moduleInfo.setHostConnectAddress_2(valueConnAdd2.getText().toString());
        moduleInfo.setHostConnectAddress_3(valueConnAdd3.getText().toString());
        moduleInfo.setHostConnectAddress_4(valueConnAdd4.getText().toString());

        moduleInfo.setHostPassword_1(valueConnPw1.getText().toString());
        moduleInfo.setHostPassword_2(valueConnPw2.getText().toString());
        moduleInfo.setHostPassword_3(valueConnPw3.getText().toString());
        moduleInfo.setHostPassword_4(valueConnPw4.getText().toString());

        moduleInfo.setGpioMode_7(valGPIOMode_7);
        moduleInfo.setGpioMode_6(valGPIOMode_6);
        moduleInfo.setGpioMode_5(valGPIOMode_5);
        moduleInfo.setGpioMode_4(valGPIOMode_4);


        moduleInfo.setGpioValue_7(valGPIOValue_7);
        moduleInfo.setGpioValue_6(valGPIOValue_6);
        moduleInfo.setGpioValue_5(valGPIOValue_5);
        moduleInfo.setGpioValue_4(valGPIOValue_4);
        moduleInfo.setGpioValue_3(valGPIOValue_3);
        moduleInfo.setGpioValue_2(valGPIOValue_2);
        moduleInfo.setGpioValue_1(0);
        moduleInfo.setGpioValue_0(0);

        return moduleInfo;
    }


    private void updateUi(ModuleInfo moduleInfo) {
        if (moduleInfo == null) {
            LogUtil.d("对象为空，不更新UI");
            return;
        }
        valueModuleName.setText(moduleInfo.getModuleName());
        valueModuleMac.setText(moduleInfo.getModuleAddress());
        valueConnMac.setText(moduleInfo.getConnectAddress());
        valueModuleVer.setText(moduleInfo.getVersion());
        valueHello.setText(moduleInfo.getHello());


        valueBaudRate.setText((moduleInfo.getSerialBaudRate()) + "");
        valueDataBit.setText((moduleInfo.getSerialDataBits()& 0xff) + "");
        valueStopBit.setText((moduleInfo.getSerialStopBits()& 0xff) + "");
        valueParityBit.setSelection(moduleInfo.getSerialParity() & 0xff);
        valueTimeout.setText((moduleInfo.getSerialTimeout() & 0xffff) + "");

        //忽略低功耗睡眠时间
        ValueLowPowerModel.setSelection(moduleInfo.getLowPowerMode() & 0xff);

        valueModuleMode.setSelection(moduleInfo.getChipWorkMode() & 0xff);
        valueModuleTpl.setSelection(moduleInfo.getChipTransmitPower() & 0xff);

        valueAdvEn.setSelection(moduleInfo.getAdvEnable() & 0xff);
        valueAdvInterval.setText((moduleInfo.getAdvInterval() & 0xffff)+"");

        valueConnIntervalMin.setText((moduleInfo.getPeripheralMinInterval() & 0xffff)+ "");
        valueConnIntervalMax.setText((moduleInfo.getPeripheralMaxInterval() & 0xffff)+ "");
        valueConnTimeout.setText((moduleInfo.getPeripheralTimeout()& 0xffff )+ "");
        valuePeriName.setText(moduleInfo.getPeripheralName());
        valuePwEn.setSelection(moduleInfo.getPeripheralPasswordEnable() & 0xff);
        valuePwLen.setText(moduleInfo.getPeripheralPasswordLength() + "");
        valuePw.setText(moduleInfo.getPeripheralPassword());

        valueConnAddFlag1.setText(String.format(Locale.US, "%2x", moduleInfo.getHostConnectFlag_1()));
        valueConnAdd1.setText(moduleInfo.getHostConnectAddress_1());
        valueConnPw1.setText(moduleInfo.getHostPassword_1());

        valueConnAddFlag2.setText(String.format(Locale.US, "%2x", moduleInfo.getHostConnectFlag_2()));
        valueConnAdd2.setText(moduleInfo.getHostConnectAddress_2());
        valueConnPw2.setText(moduleInfo.getHostPassword_2());

        valueConnAddFlag3.setText(String.format(Locale.US, "%2x", moduleInfo.getHostConnectFlag_3()));
        valueConnAdd3.setText(moduleInfo.getHostConnectAddress_3());
        valueConnPw3.setText(moduleInfo.getHostPassword_3());

        valueConnAddFlag4.setText(String.format(Locale.US, "%2x", moduleInfo.getHostConnectFlag_4()));
        valueConnAdd4.setText(moduleInfo.getHostConnectAddress_4());
        valueConnPw4.setText(moduleInfo.getHostPassword_4());

        valueGpioMode7.setSelection(moduleInfo.getGpioMode_7() == 0 ? 0 : 1);
        valueGpioMode6.setSelection(moduleInfo.getGpioMode_6() == 0 ? 0 : 1);
        valueGpioMode5.setSelection(moduleInfo.getGpioMode_5() == 0 ? 0 : 1);
        valueGpioMode4.setSelection(moduleInfo.getGpioMode_4() == 0 ? 0 : 1);
        valueGpioMode3.setText(moduleInfo.getGpioMode_3() + "");
        valueGpioMode2.setText(moduleInfo.getGpioMode_2() + "");
        valueGpioMode1.setText(moduleInfo.getGpioMode_1() + "");
        valueGpioMode0.setText(moduleInfo.getGpioMode_0() + "");

        valueGpioValue7.setSelection(moduleInfo.getGpioValue_7() == 0 ? 0 : 1);
        valueGpioValue6.setSelection(moduleInfo.getGpioValue_6() == 0 ? 0 : 1);
        valueGpioValue5.setSelection(moduleInfo.getGpioValue_5() == 0 ? 0 : 1);
        valueGpioValue4.setSelection(moduleInfo.getGpioValue_4() == 0 ? 0 : 1);
        valueGpioValue3.setSelection(moduleInfo.getGpioValue_3() == 0 ? 0 : 1);
        valueGpioValue2.setSelection(moduleInfo.getGpioValue_2() == 0 ? 0 : 1);
        valueGpioValue1.setText(moduleInfo.getGpioValue_1() + "");
        valueGpioValue0.setText(moduleInfo.getGpioValue_0() + "");

    }

    private void showToast(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
