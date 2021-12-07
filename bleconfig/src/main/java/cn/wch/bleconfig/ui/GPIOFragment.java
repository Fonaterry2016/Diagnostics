package cn.wch.bleconfig.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import cn.wch.bleconfig.ICommand;
import cn.wch.bleconfig.R;
import cn.wch.bleconfig.ui.main.PageViewModel;
import cn.wch.bleconfig.util.ConvertUtil;
import cn.wch.blelib.ch9141.config.entry.ModuleInfo;
import cn.wch.blelib.ch9141.config.entry.gpio.GPIOReadResult;
import cn.wch.blelib.ch9141.config.entry.gpio.GPIOSetResult;

public class GPIOFragment extends Fragment {

    private Context mContext;
    private ICommand iCommand;
    private PageViewModel pageViewModel;

    private Button gpSync;
    private Button gpAdcRd;
    private Button gpCfg_4;
    private Button gpCfg_5;
    private Button gpCfg_6;
    private Button gpCfg_7;
    private Button gpRd_4;
    private Button gpRd_5;
    private Button gpRd_6;
    private Button gpRd_7;
    private Button gpWr_4;
    private Button gpWr_5;
    private Button gpWr_6;
    private Button gpWr_7;
    private TextView gpAdcVal;
    private Spinner GPIODir_4;
    private Spinner GPIODir_5;
    private Spinner GPIODir_6;
    private Spinner GPIODir_7;
    private Spinner GPIOVal_4;
    private Spinner GPIOVal_5;
    private Spinner GPIOVal_6;
    private Spinner GPIOVal_7;

    private Button getGPIOInfo;

    public static GPIOFragment newInstance() {
        GPIOFragment fragment = new GPIOFragment();
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
        View inflate = inflater.inflate(R.layout.fragment_gpio, container, false);
        initWidget(inflate);
        return inflate;
    }

    @Override
    public String toString() {
        return "GPIO";
    }

    public void register(ICommand iCommand) {
        this.iCommand = iCommand;
    }


    private void initWidget(View inflate) {
        gpSync = (Button) inflate.findViewById(R.id.gpio_sync);
        gpAdcRd = (Button) inflate.findViewById(R.id.gpio_adc_read);
        gpCfg_4 = (Button) inflate.findViewById(R.id.gpio_config_4);
        gpCfg_5 = (Button) inflate.findViewById(R.id.gpio_config_5);
        gpCfg_6 = (Button) inflate.findViewById(R.id.gpio_config_6);
        gpCfg_7 = (Button) inflate.findViewById(R.id.gpio_config_7);
        gpRd_4 = (Button) inflate.findViewById(R.id.gpio_read_4);
        gpRd_5 = (Button) inflate.findViewById(R.id.gpio_read_5);
        gpRd_6 = (Button) inflate.findViewById(R.id.gpio_read_6);
        gpRd_7 = (Button) inflate.findViewById(R.id.gpio_read_7);
        gpWr_4 = (Button) inflate.findViewById(R.id.gpio_write_4);
        gpWr_5 = (Button) inflate.findViewById(R.id.gpio_write_5);
        gpWr_6 = (Button) inflate.findViewById(R.id.gpio_write_6);
        gpWr_7 = (Button) inflate.findViewById(R.id.gpio_write_7);
        gpAdcVal = (TextView) inflate.findViewById(R.id.gpio_adc_value);

        GPIODir_4 = (Spinner) inflate.findViewById(R.id.gpio_dir_4);
        GPIODir_5 = (Spinner) inflate.findViewById(R.id.gpio_dir_5);
        GPIODir_6 = (Spinner) inflate.findViewById(R.id.gpio_dir_6);
        GPIODir_7 = (Spinner) inflate.findViewById(R.id.gpio_dir_7);

        GPIOVal_4 = (Spinner) inflate.findViewById(R.id.gpio_val_4);
        GPIOVal_5 = (Spinner) inflate.findViewById(R.id.gpio_val_5);
        GPIOVal_6 = (Spinner) inflate.findViewById(R.id.gpio_val_6);
        GPIOVal_7 = (Spinner) inflate.findViewById(R.id.gpio_val_7);

        getGPIOInfo = (Button) inflate.findViewById(R.id.getGPIOInfo);

        getGPIOInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iCommand!=null){
                    iCommand.getModuleInfo();
                }
            }
        });

        pageViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(PageViewModel.class);


        pageViewModel.getGPIOModuleInfo().observe(this, new Observer<ModuleInfo>() {
            @Override
            public void onChanged(ModuleInfo moduleInfo) {
                if(moduleInfo==null){
                    return;
                }
                updateWidget();
                updateSpinner((byte) moduleInfo.getGpioMode(),(byte)moduleInfo.getGpioValue());
            }
        });

        pageViewModel.getGPIOReadResult().observe(this, new Observer<GPIOReadResult>() {
            @Override
            public void onChanged(GPIOReadResult gpioReadResult) {
                if(gpioReadResult==null){
                    return;
                }
                int num = gpioReadResult.getNum();
                int value = gpioReadResult.getValue();
                switch (num) {
                    case 4:
                        GPIOVal_4.setSelection(value);
                        break;
                    case 5:
                        GPIOVal_5.setSelection(value);
                        break;
                    case 6:
                        GPIOVal_6.setSelection(value);
                        break;
                    case 7:
                        GPIOVal_7.setSelection(value);
                        break;
                }
            }
        });

        pageViewModel.getGPIOAdc().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double d) {
                if(d<0){
                    return;
                }
                gpAdcVal.setText(String.format(Locale.US,"%.2f",d));
            }
        });

        pageViewModel.getGPIOSetResult().observe(this, new Observer<GPIOSetResult>() {
            @Override
            public void onChanged(GPIOSetResult gpioSetResult) {
                if(gpioSetResult==null){
                    return;
                }
                int gpioNum = gpioSetResult.getNum();
                int gpioDir = gpioSetResult.getDir();
                switch (gpioNum) {
                    case 4:
                        if (gpioDir == 0) {
                            gpRd_4.setVisibility(View.VISIBLE);
                            gpWr_4.setVisibility(View.INVISIBLE);
                        } else {
                            gpRd_4.setVisibility(View.INVISIBLE);
                            gpWr_4.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 5:
                        if (gpioDir == 0) {
                            gpRd_5.setVisibility(View.VISIBLE);
                            gpWr_5.setVisibility(View.INVISIBLE);
                        } else {
                            gpRd_5.setVisibility(View.INVISIBLE);
                            gpWr_5.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 6:
                        if (gpioDir == 0) {
                            gpRd_6.setVisibility(View.VISIBLE);
                            gpWr_6.setVisibility(View.INVISIBLE);
                        } else {
                            gpRd_6.setVisibility(View.INVISIBLE);
                            gpWr_6.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 7:
                        if (gpioDir == 0) {
                            gpRd_7.setVisibility(View.VISIBLE);
                            gpWr_7.setVisibility(View.INVISIBLE);
                        } else {
                            gpRd_7.setVisibility(View.INVISIBLE);
                            gpWr_7.setVisibility(View.VISIBLE);
                        }
                        break;
                }

            }
        });

    }

    private void updateWidget(){
        gpCfg_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setGPIO(4, GPIODir_4.getSelectedItemPosition());
            }
        });
        gpCfg_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setGPIO( 5, GPIODir_5.getSelectedItemPosition());
            }
        });
        gpCfg_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setGPIO( 6, GPIODir_6.getSelectedItemPosition());
            }
        });
        gpCfg_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setGPIO(7, GPIODir_7.getSelectedItemPosition());
            }
        });
        gpRd_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                readGPIO(4);
            }
        });
        gpRd_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                readGPIO(5);
            }
        });
        gpRd_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                readGPIO(6);
            }
        });
        gpRd_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                readGPIO(7);
            }
        });
        gpWr_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                writeGPIO( 4, GPIOVal_4.getSelectedItemPosition());
            }
        });
        gpWr_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                writeGPIO( 5, GPIOVal_5.getSelectedItemPosition());
            }
        });
        gpWr_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                writeGPIO( 6, GPIOVal_6.getSelectedItemPosition());
            }
        });
        gpWr_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                writeGPIO( 7, GPIOVal_7.getSelectedItemPosition());
            }
        });

        gpSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                syncGPIO();
            }
        });
        gpAdcRd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                readGPIOAdc();
            }
        });
    }

    private void updateSpinner(byte gpioModel,byte gpioValue){
        String gpioStr = ConvertUtil.toBinaryString(gpioModel & 0xff);
        Log.d("TAG", gpioStr);

        ArrayAdapter<CharSequence> adapter_1 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_dir, R.layout.item_spinner);
        adapter_1.setDropDownViewResource(R.layout.item_spinner);
        GPIODir_4.setAdapter(adapter_1);
        GPIODir_4.setGravity(0x10);
        int posi = gpioStr.charAt(3) - '0';
        GPIODir_4.setSelection(posi);
        if (posi == 0) {
            gpRd_4.setVisibility(View.VISIBLE);
            gpWr_4.setVisibility(View.INVISIBLE);
        } else {
            gpRd_4.setVisibility(View.INVISIBLE);
            gpWr_4.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<CharSequence> adapter_3 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_dir, R.layout.item_spinner);
        adapter_3.setDropDownViewResource(R.layout.item_spinner);
        GPIODir_5.setAdapter(adapter_3);
        GPIODir_5.setGravity(0x10);
        posi = gpioStr.charAt(2) - '0';
        GPIODir_5.setSelection(posi);
        if (posi == 0) {
            gpRd_5.setVisibility(View.VISIBLE);
            gpWr_5.setVisibility(View.INVISIBLE);
        } else {
            gpRd_5.setVisibility(View.INVISIBLE);
            gpWr_5.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<CharSequence> adapter_5 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_dir, R.layout.item_spinner);
        adapter_5.setDropDownViewResource(R.layout.item_spinner);
        GPIODir_6.setAdapter(adapter_5);
        GPIODir_6.setGravity(0x10);
        posi = gpioStr.charAt(1) - '0';
        GPIODir_6.setSelection(posi);
        if (posi == 0) {
            gpRd_6.setVisibility(View.VISIBLE);
            gpWr_6.setVisibility(View.INVISIBLE);
        } else {
            gpRd_6.setVisibility(View.INVISIBLE);
            gpWr_6.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<CharSequence> adapter_7 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_dir, R.layout.item_spinner);
        adapter_7.setDropDownViewResource(R.layout.item_spinner);
        GPIODir_7.setAdapter(adapter_7);
        GPIODir_7.setGravity(0x10);
        posi = gpioStr.charAt(0) - '0';
        GPIODir_7.setSelection(posi);
        if (posi == 0) {
            gpRd_7.setVisibility(View.VISIBLE);
            gpWr_7.setVisibility(View.INVISIBLE);
        } else {
            gpRd_7.setVisibility(View.INVISIBLE);
            gpWr_7.setVisibility(View.VISIBLE);
        }

        //GPIO value spinner 4~7
        gpioStr = ConvertUtil.toBinaryString(gpioValue & 0xff);

        ArrayAdapter<CharSequence> adapter_2 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_val, R.layout.item_spinner);
        adapter_2.setDropDownViewResource(R.layout.item_spinner);
        GPIOVal_4.setAdapter(adapter_2);
        GPIOVal_4.setGravity(0x10);
        GPIOVal_4.setSelection(gpioStr.charAt(3) - '0');

        ArrayAdapter<CharSequence> adapter_4 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_val, R.layout.item_spinner);
        adapter_4.setDropDownViewResource(R.layout.item_spinner);
        GPIOVal_5.setAdapter(adapter_4);
        GPIOVal_5.setGravity(0x10);
        GPIOVal_5.setSelection(gpioStr.charAt(2) - '0');

        ArrayAdapter<CharSequence> adapter_6 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_val, R.layout.item_spinner);
        adapter_6.setDropDownViewResource(R.layout.item_spinner);
        GPIOVal_6.setAdapter(adapter_6);
        GPIOVal_6.setGravity(0x10);
        GPIOVal_6.setSelection(gpioStr.charAt(1) - '0');

        ArrayAdapter<CharSequence> adapter_8 = ArrayAdapter.createFromResource(mContext,
                R.array.gpio_val, R.layout.item_spinner);
        adapter_8.setDropDownViewResource(R.layout.item_spinner);
        GPIOVal_7.setAdapter(adapter_8);
        GPIOVal_7.setGravity(0x10);
        GPIOVal_7.setSelection(gpioStr.charAt(0) - '0');
    }
    /*****************************************组包*************************************************/
    void setGPIO(int num,int dir){
        if(iCommand!=null){
            iCommand.setGPIO(num, dir);
        }
    }

    void readGPIO(int num){
        if(iCommand!=null){
            iCommand.readGPIO(num);
        }
    }

    void writeGPIO(int num,int val){
        if(iCommand!=null){
            iCommand.writeGPIO(num, val);
        }
    }

    public void syncGPIO() {
        if(iCommand!=null){
            iCommand.syncGPIO();
        }
    }

    public void readGPIOAdc() {
        if(iCommand!=null){
            iCommand.readADC();
        }
    }
}
