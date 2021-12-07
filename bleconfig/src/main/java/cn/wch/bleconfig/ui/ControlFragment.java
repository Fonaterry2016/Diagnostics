package cn.wch.bleconfig.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TableRow;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
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
import cn.wch.bleconfig.util.ConvertUtil;
import cn.wch.blelib.ch9141.config.entry.ControlInfo;
import cn.wch.blelib.utils.FormatUtil;
import cn.wch.blelib.utils.LogUtil;


public class ControlFragment extends Fragment {
    @BindView(R2.id.getControlInfo)
    Button getControlInfo;
    @BindView(R2.id.setControlInfo)
    Button setControlInfo;
    @BindView(R2.id.et_image_position)
    EditText etImagePosition;
    @BindView(R2.id.et_version)
    EditText etVersion;
    @BindView(R2.id.sp_enable_config)
    Spinner spEnableConfig;
    @BindView(R2.id.sp_adv_channel)
    Spinner spAdvChannel;
    @BindView(R2.id.sp_slave_voltage)
    Spinner spSlaveVoltage;
    @BindView(R2.id.tr_slave_voltage)
    TableRow trSlaveVoltage;

    @BindView(R2.id.rc_method)
    Spinner spRcMethod;
    @BindView(R2.id.rf_method)
    Spinner spRFMethod;
    @BindView(R2.id.rc_temperature)
    EditText etRcTemperature;
    @BindView(R2.id.rf_temperature)
    EditText etRfTemperature;
    @BindView(R2.id.rc_time)
    EditText etRcTime;
    @BindView(R2.id.rf_time)
    EditText etRfTime;

    private Context mContext;
    private Unbinder unbinder;
    private PageViewModel pageViewModel;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ICommand iCommand;

    private byte imageflag;
    private byte[] reserved = new byte[3];
    private byte para_flag;
    private byte[] versionInfo = new byte[2];

    private boolean parameterValid = false;

    private byte gpio_en;
    private byte tnow_en;
    private byte ble_sta__en;

    public static ControlFragment newInstance() {
        ControlFragment fragment = new ControlFragment();
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
        View inflate = inflater.inflate(R.layout.fragment_control, container, false);
        unbinder = ButterKnife.bind(this, inflate);
        init(inflate);
        return inflate;
    }

    @Override
    public String toString() {
        return "控制参数";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void register(ICommand iCommand) {
        this.iCommand = iCommand;
    }

    private void init(View inflate) {
        pageViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(PageViewModel.class);
        pageViewModel.getControlInfo().observe(this, new Observer<ControlInfo>() {
            @Override
            public void onChanged(ControlInfo controlInfo) {
                updateUi(controlInfo);
            }
        });
        initWidget(inflate);
    }

    private void initWidget(View v) {
        initSpinner();
        getControlInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCommand != null) {
                    iCommand.getControlInfo();
                }
            }
        });
        setControlInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spEnableConfig.getSelectedItemPosition()==0){
                    showContinueDialog();
                }else {
                    setConfig();
                }
            }
        });
    }

    private void setConfig(){
        if (iCommand != null) {
            ControlInfo newControlInfo = getNewControlInfo();
            if (newControlInfo != null) {
                iCommand.setControlInfo(newControlInfo);
            }
        }
    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter_1 = ArrayAdapter.createFromResource(mContext,
                R.array.cfg_en, R.layout.item_spinner);
        adapter_1.setDropDownViewResource(R.layout.item_spinner);
        spEnableConfig.setAdapter(adapter_1);
        spEnableConfig.setGravity(0x10);
        spEnableConfig.setSelection(0);
        spEnableConfig.setOnItemSelectedListener(new ConfigEnableListener());

        ArrayAdapter<CharSequence> adapter_2 = ArrayAdapter.createFromResource(mContext,
                R.array.adv_channel, R.layout.item_spinner);
        adapter_2.setDropDownViewResource(R.layout.item_spinner);
        spAdvChannel.setAdapter(adapter_2);
        spAdvChannel.setGravity(0x10);
        spAdvChannel.setSelection(0);
        spAdvChannel.setOnItemSelectedListener(new AdvChannelListener());

        ArrayAdapter<CharSequence> adapter_3 = ArrayAdapter.createFromResource(mContext,
                R.array.voltage_show, R.layout.item_spinner);
        adapter_3.setDropDownViewResource(R.layout.item_spinner);
        spSlaveVoltage.setAdapter(adapter_3);
        spSlaveVoltage.setGravity(0x10);
        spSlaveVoltage.setSelection(0);
        spSlaveVoltage.setOnItemSelectedListener(new VoltageShowListener());
    }

    private class ConfigEnableListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class AdvChannelListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class VoltageShowListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    private ControlInfo getNewControlInfo() {
        if (!checkValid()) {
            return null;
        }
        return getControlInfo();
    }

    private ControlInfo getControlInfo() {

        ControlInfo controlInfo=new ControlInfo();
        controlInfo.setImageFlag(imageflag);
        controlInfo.setRevd(new byte[]{0x00,0x00,0x00});
        controlInfo.setExd_para_flag(para_flag);
        controlInfo.setExd_para_ver(versionInfo);
        controlInfo.setBle_cfg_enable((byte) spEnableConfig.getSelectedItemPosition());

        int ch=0;
        switch (spAdvChannel.getSelectedItemPosition()) {
            case 0:
                ch = 0x01;
                break;
            case 1:
                ch = 0x02;
                break;
            case 2:
                ch = 0x04;
                break;
            case 3:
                ch = 0x07;
                break;
            default:
                ch = 0x01;
                break;
        }
        controlInfo.setBroadcast_ch_cfg((byte) ch);

        byte[] b = new byte[3];
        Arrays.fill(b, (byte) 0);
        if (parameterValid) {
            b[0] = (byte) (b[0] | (spEnableConfig.getSelectedItemPosition() == 0 ? 0x00 : 0x40));
        } else {
            b[0] = (byte) (b[0] | 0x80);
        }

        controlInfo.setBit_switch_cfg(b);

        controlInfo.setRc_32k_cali_method((byte) spRcMethod.getSelectedItemPosition());
        controlInfo.setBle_rf_cali_method((byte) spRFMethod.getSelectedItemPosition());

        if(TextUtils.isEmpty(etRcTemperature.getText().toString()) || TextUtils.isEmpty(etRfTemperature.getText().toString())){
            showToast("温度不能为空");
            return null;
        }

        int tc=Integer.parseInt(etRcTemperature.getText().toString());
        int tf=Integer.parseInt(etRfTemperature.getText().toString());
        if(tc<1 || tc>10 || tf<1 || tf>10){
            showToast("温度边界范围为1-10");
            return null;
        }
        controlInfo.setRc_32k_cali_temperature((byte) tc);
        controlInfo.setBle_rf_cali_temperature((byte) tf);
        if(TextUtils.isEmpty(etRcTime.getText().toString()) || TextUtils.isEmpty(etRfTime.getText().toString())){
            showToast("时间不能为空");
            return null;
        }
        long c=  Long.parseLong(etRcTime.getText().toString());
        long f= Long.parseLong(etRfTime.getText().toString());
        if(c>Integer.MAX_VALUE || f>Integer.MAX_VALUE){
            showToast("校准时间超出界限");
            return null;
        }
        controlInfo.setRc_32k_cali_time((int) c);
        controlInfo.setBle_rf_cali_time((int) f);

        controlInfo.setGpio_en(gpio_en);
        controlInfo.setTnow_en(tnow_en);
        controlInfo.setBle_sta_en(ble_sta__en);

        return controlInfo;
    }

    private boolean checkValid() {
        return true;
    }


    private void updateUi(ControlInfo controlInfo) {
        if (controlInfo == null) {
            showSetButton(false);
            LogUtil.d("对象为空，不更新");
            return;
        }
        showSetButton(true);
        imageflag = controlInfo.getImageFlag();
        reserved = controlInfo.getRevd();
        para_flag = controlInfo.getExd_para_flag();
        versionInfo = controlInfo.getExd_para_ver();
        etVersion.setText(FormatUtil.bytesToHexString(ConvertUtil.inverseByteArray(versionInfo)));
        spEnableConfig.setSelection(controlInfo.getBle_cfg_enable() == 0 ? 0 : 1);
        byte broadcast_ch_cfg = controlInfo.getBroadcast_ch_cfg();
        switch (broadcast_ch_cfg) {
            case 0x01:
                spAdvChannel.setSelection(0);
                break;
            case 0x02:
                spAdvChannel.setSelection(1);
                break;
            case 0x04:
                spAdvChannel.setSelection(2);
                break;
            case 0x07:
                spAdvChannel.setSelection(3);
                break;
            default:
                break;
        }
        if ((controlInfo.getBit_switch_cfg()[0] & 0x80) == 0) {
            //有效
            showConfigParameter(true);
            parameterValid = true;
            spSlaveVoltage.setSelection((controlInfo.getBit_switch_cfg()[0] & 0x40) == 0 ? 0 : 1);
        } else {
            //无效
            parameterValid = false;
            //隐藏从机电压选项
            showConfigParameter(false);
        }

        spRcMethod.setSelection(controlInfo.getRc_32k_cali_method() & 0xff);
        spRFMethod.setSelection(controlInfo.getBle_rf_cali_method() & 0xff);
        etRcTemperature.setText((controlInfo.getRc_32k_cali_temperature()&0xff) +"");
        etRfTemperature.setText((controlInfo.getBle_rf_cali_temperature()&0xff) +"");
        etRcTime.setText((controlInfo.getRc_32k_cali_time()& 0xffffffff) +"");
        etRfTime.setText((controlInfo.getBle_rf_cali_time()&0xffffffff) +"");

        gpio_en=controlInfo.getGpio_en();
        tnow_en=controlInfo.getTnow_en();
        ble_sta__en=controlInfo.getBle_sta_en();
    }

    void showSetButton(boolean enable) {
        setControlInfo.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    void showConfigParameter(boolean enable) {
        trSlaveVoltage.setVisibility(enable? View.VISIBLE:View.GONE);
    }

    void showContinueDialog(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(mContext)
                .setMessage("关闭蓝牙配置后，手机将无法重新配置，只能通过串口命令重新打开")
                .setCancelable(false)
                .setPositiveButton("继续", null)
                .setNeutralButton("取消", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConfig();
            }
        });
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
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
