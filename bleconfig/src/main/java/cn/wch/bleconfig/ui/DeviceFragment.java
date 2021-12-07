package cn.wch.bleconfig.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
import cn.wch.blelib.ch9141.config.entry.DeviceInfo;
import cn.wch.blelib.utils.LogUtil;


import static cn.wch.bleconfig.util.ConvertUtil.bytesToHexString;
import static cn.wch.bleconfig.util.ConvertUtil.myParseHexToBytes;

public class DeviceFragment extends Fragment {

    @BindView(R2.id.value_devinfo_sysid1)
    EditText valueDevinfoSysid1;
    @BindView(R2.id.value_devinfo_sysid2)
    EditText valueDevinfoSysid2;
    @BindView(R2.id.value_devinfo_sysid3)
    EditText valueDevinfoSysid3;
    @BindView(R2.id.value_devinfo_sysid4)
    EditText valueDevinfoSysid4;
    @BindView(R2.id.value_devinfo_sysid5)
    EditText valueDevinfoSysid5;
    @BindView(R2.id.value_devinfo_sysid6)
    EditText valueDevinfoSysid6;
    @BindView(R2.id.value_devinfo_sysid7)
    EditText valueDevinfoSysid7;
    @BindView(R2.id.value_devinfo_sysid8)
    EditText valueDevinfoSysid8;
    @BindView(R2.id.value_devinfo_modname)
    EditText valueDevinfoModname;
    @BindView(R2.id.value_devinfo_sn)
    EditText valueDevinfoSn;
    @BindView(R2.id.value_devinfo_firmver)
    EditText valueDevinfoFirmver;
    @BindView(R2.id.value_devinfo_hardver)
    EditText valueDevinfoHardver;
    @BindView(R2.id.value_devinfo_softver)
    EditText valueDevinfoSoftver;
    @BindView(R2.id.value_devinfo_manu)
    EditText valueDevinfoManu;
    @BindView(R2.id.value_devinfo_pnpid1)
    EditText valueDevinfoPnpid1;
    @BindView(R2.id.value_devinfo_pnpid2)
    EditText valueDevinfoPnpid2;
    @BindView(R2.id.value_devinfo_pnpid3)
    EditText valueDevinfoPnpid3;
    @BindView(R2.id.value_devinfo_pnpid4)
    EditText valueDevinfoPnpid4;
    @BindView(R2.id.value_devinfo_pnpid5)
    EditText valueDevinfoPnpid5;
    @BindView(R2.id.value_devinfo_pnpid6)
    EditText valueDevinfoPnpid6;
    @BindView(R2.id.value_devinfo_pnpid7)
    EditText valueDevinfoPnpid7;
    @BindView(R2.id.getDeviceInfo)
    Button getDeviceInfo;
    @BindView(R2.id.setDeviceInfo)
    Button setDeviceInfo;

    Unbinder unbinder;

    private PageViewModel pageViewModel;
    private Context mContext;
    private ICommand iCommand;
    private Handler handler=new Handler(Looper.getMainLooper());


    public static DeviceFragment newInstance() {
        DeviceFragment fragment = new DeviceFragment();
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
        View inflate = inflater.inflate(R.layout.fragment_device, container, false);
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
        return "设备信息";
    }

    public void register(ICommand iCommand) {
        this.iCommand = iCommand;
    }

    void init(View v) {
        pageViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(PageViewModel.class);
        pageViewModel.getDeviceInfo().observe(this, new Observer<DeviceInfo>() {
            @Override
            public void onChanged(DeviceInfo deviceInfo) {
                updateUi(deviceInfo);
            }
        });
        initWidget(v);
    }

    private void initWidget(View v) {
        getDeviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCommand != null) {
                    iCommand.getDeviceInfo();
                }
            }
        });
        setDeviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCommand != null) {
                    DeviceInfo newDeviceInfo = getNewDeviceInfo();
                    if(newDeviceInfo!=null){
                        iCommand.setDeviceInfo(newDeviceInfo);
                    }
                }
            }
        });
    }

    private DeviceInfo getNewDeviceInfo() {
        if(!checkValid()){
            return null;
        }
        return getDeviceInfo();
    }

    private boolean checkValid() {
        if (TextUtils.isEmpty(valueDevinfoModname.getText().toString()) ||
                TextUtils.isEmpty(valueDevinfoSn.getText().toString()) || TextUtils.isEmpty(valueDevinfoFirmver.getText().toString()) ||
                TextUtils.isEmpty(valueDevinfoSoftver.getText().toString()) || TextUtils.isEmpty(valueDevinfoHardver.getText().toString()) ||
                TextUtils.isEmpty(valueDevinfoManu.getText().toString())) {
            showToast("参数不完整");
            return false;
        }
        return true;
    }

    private DeviceInfo getDeviceInfo(){
        DeviceInfo deviceInfo=new DeviceInfo();
        deviceInfo.setFlag((byte) 0xaa);

        byte[] mp=new byte[8];
        //sys id
        mp[0]=myParseHexToBytes(valueDevinfoSysid1.getText().toString())[0];
        mp[1]=myParseHexToBytes(valueDevinfoSysid2.getText().toString())[0];
        mp[2]=myParseHexToBytes(valueDevinfoSysid3.getText().toString())[0];
        mp[3]=myParseHexToBytes(valueDevinfoSysid4.getText().toString())[0];
        mp[4]=myParseHexToBytes(valueDevinfoSysid5.getText().toString())[0];
        mp[5]=myParseHexToBytes(valueDevinfoSysid6.getText().toString())[0];
        mp[6]=myParseHexToBytes(valueDevinfoSysid7.getText().toString())[0];
        mp[7]=myParseHexToBytes(valueDevinfoSysid8.getText().toString())[0];

        deviceInfo.setSystemID(mp);
        deviceInfo.setModelName(valueDevinfoModname.getText().toString());
        deviceInfo.setSerialNumber(valueDevinfoSn.getText().toString());
        deviceInfo.setFirmwareRevision(valueDevinfoFirmver.getText().toString());
        deviceInfo.setHardwareRevision(valueDevinfoHardver.getText().toString());
        deviceInfo.setSoftwareRevision(valueDevinfoSoftver.getText().toString());
        deviceInfo.setManufacturerName(valueDevinfoManu.getText().toString());



        //PNP id
        byte[] pnp=new byte[7];
        pnp[0]=myParseHexToBytes(valueDevinfoPnpid1.getText().toString())[0];
        pnp[1]=myParseHexToBytes(valueDevinfoPnpid2.getText().toString())[0];
        pnp[2]=myParseHexToBytes(valueDevinfoPnpid3.getText().toString())[0];
        pnp[3]=myParseHexToBytes(valueDevinfoPnpid4.getText().toString())[0];
        pnp[4]=myParseHexToBytes(valueDevinfoPnpid5.getText().toString())[0];
        pnp[5]=myParseHexToBytes(valueDevinfoPnpid6.getText().toString())[0];
        pnp[6]=myParseHexToBytes(valueDevinfoPnpid7.getText().toString())[0];

        deviceInfo.setPnPID(pnp);
        return deviceInfo;
    }


    private void updateUi(DeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            LogUtil.d("对象为空，不更新");
            return;
        }
        byte[] temp = deviceInfo.getSystemID();
        valueDevinfoSysid1.setText(bytesToHexString(temp[0]));
        valueDevinfoSysid2.setText(bytesToHexString(temp[1]));
        valueDevinfoSysid3.setText(bytesToHexString(temp[2]));
        valueDevinfoSysid4.setText(bytesToHexString(temp[3]));
        valueDevinfoSysid5.setText(bytesToHexString(temp[4]));
        valueDevinfoSysid6.setText(bytesToHexString(temp[5]));
        valueDevinfoSysid7.setText(bytesToHexString(temp[6]));
        valueDevinfoSysid8.setText(bytesToHexString(temp[7]));

        valueDevinfoModname.setText(deviceInfo.getModelName());
        valueDevinfoSn.setText(deviceInfo.getSerialNumber());
        valueDevinfoFirmver.setText(deviceInfo.getFirmwareRevision());
        valueDevinfoHardver.setText(deviceInfo.getHardwareRevision());
        valueDevinfoSoftver.setText(deviceInfo.getSoftwareRevision());
        valueDevinfoManu.setText(deviceInfo.getManufacturerName());

        temp=deviceInfo.getPnPID();
        valueDevinfoPnpid1.setText(bytesToHexString(temp[0]));
        valueDevinfoPnpid2.setText(bytesToHexString(temp[1]));
        valueDevinfoPnpid3.setText(bytesToHexString(temp[2]));
        valueDevinfoPnpid4.setText(bytesToHexString(temp[3]));
        valueDevinfoPnpid5.setText(bytesToHexString(temp[4]));
        valueDevinfoPnpid6.setText(bytesToHexString(temp[5]));
        valueDevinfoPnpid7.setText(bytesToHexString(temp[6]));
    }

    private void showToast(final String message){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
            }
        });
    }


}
