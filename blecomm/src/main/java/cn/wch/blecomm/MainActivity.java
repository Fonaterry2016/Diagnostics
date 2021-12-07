package cn.wch.blecomm;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.suke.widget.SwitchButton;
import com.touchmcu.ui.DialogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import cn.wch.blecomm.constant.Constant;
import cn.wch.blecomm.serial.ModemParse;
import cn.wch.blecomm.storage.ConfigSaveUtil;
import cn.wch.blecomm.storage.SaveBean;
import cn.wch.blecomm.storage.SaveType;
import cn.wch.blecomm.storage.SendBean;
import cn.wch.blecomm.storage.SerialBaudBean;
import cn.wch.blecomm.storage.SerialModemBean;
import cn.wch.blecomm.task.ATInterface;
import cn.wch.blecomm.task.BytesTaskBean;
import cn.wch.blecomm.task.FileTak;
import cn.wch.blecomm.task.FileTaskBean;
import cn.wch.blecomm.task.SendType;
import cn.wch.blecomm.task.SingleTask;
import cn.wch.blecomm.task.TimingTask;
import cn.wch.blecomm.ui.MtuDialog;
import cn.wch.blecomm.ui.ReceiveConfigDialog;
import cn.wch.blecomm.ui.SendConfigDialog;
import cn.wch.blecomm.ui.SendInterruptConfigDialog;
import cn.wch.blecomm.ui.SerialConfigDialog;
import cn.wch.blecomm.utils.FileUtil;
import cn.wch.blelib.ch9141.CH9141BluetoothManager;
import cn.wch.blelib.ch9141.callback.ModemStatus;
import cn.wch.blelib.ch9141.callback.NotifyStatus;
import cn.wch.blelib.ch9141.callback.RSSIStatus;
import cn.wch.blelib.exception.BLELibException;
import cn.wch.blelib.utils.AppUtil;
import cn.wch.blelib.utils.BLEUtil;
import cn.wch.blelib.utils.FormatUtil;
import cn.wch.blelib.utils.Location;
import cn.wch.blelib.utils.LogUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends BLEBaseActivity implements ATInterface {

    Button write;
    LinearLayout llSend;

    EditText sendData;
    SwitchButton sendHex;
    TextView sendSpeed;
    Button clearWrite;
    TextView sendCount;
    SwitchButton charDetailsNotificationSwitcher;
    SwitchButton receiveHex;
    TextView receiveData;
    TextView receiveCount;
    TextView receiveSpeed;
    Button clearReceive;
    Button receiveSet;
    Button btnSetSerial;
    Button read;
    Button sendSet;
    TextView receiveDesc;
    Button receiveShare;
    LinearLayout llReceiveSave;
    TextView sendDesc;
    ProgressBar progress;
    TextView progressValue;
    LinearLayout llProgress;
    TextView serialInfo;
    Button receiveShareData;
    CheckBox cbDcd;
    CheckBox cbDsr;
    CheckBox cbCts;
    CheckBox cbRi;
    LinearLayout llContentSerial;

    /*
    TextView TV_ChgMosFault;
    TextView TV_DisChgMosFault;
    TextView TV_Voltage;
    TextView TV_Current;
     */

    //ModbusVcRealtimeInfo RecRealTimeInfo = new ModbusVcRealtimeInfo(this);
    ViewModeWithLiveData RecRealTimeInfo;





    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isConnected = false;
    private int CODE_CHOOSE_FILE = 110;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_main);
        initSpecialWidget();
    }

    @Override
    protected void initWidget() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("BLE Communication");
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        setScheduleSpeedMonitor(timeRunnable);
        initSpecialWidget();
        initUi();

        NavController navController = Navigation.findNavController(this, R.id.nav_register_frag);
        Navigation.findNavController(this, R.id.nav_register_frag);
        NavigationUI.setupActionBarWithNavController(this,navController);

        RecRealTimeInfo  = new ViewModelProvider(this).get(ViewModeWithLiveData.class);
        RecRealTimeInfo.ModbusVcRealtimeInfo(this);

    }

    private void initSpecialWidget(){

        write=findViewById(R.id.write);
        llSend=findViewById(R.id.ll_send);
        sendData=findViewById(R.id.send_data);
        sendHex=findViewById(R.id.send_hex);
        //sendSpeed=findViewById(R.id.send_speed);
        clearWrite=findViewById(R.id.clear_write);
        //sendCount=findViewById(R.id.send_count);
        //charDetailsNotificationSwitcher=findViewById(R.id.char_details_notification_switcher);
        receiveHex=findViewById(R.id.receive_hex);
        receiveData=findViewById(R.id.receive_data);
        //receiveCount=findViewById(R.id.receive_count);
        //receiveSpeed=findViewById(R.id.receive_speed);

        clearReceive=findViewById(R.id.clear_receive);
        receiveSet=findViewById(R.id.receive_set);
        btnSetSerial=findViewById(R.id.btn_set_serial);

        read=findViewById(R.id.read);
        sendSet=findViewById(R.id.send_set);
        receiveDesc=findViewById(R.id.receive_desc);
        receiveShare=findViewById(R.id.receive_share);
        llReceiveSave=findViewById(R.id.ll_receive_save);

        //delete by terry for removing show new sending configuration
        //sendDesc=findViewById(R.id.send_desc);

        //progress=findViewById(R.id.progress);
        progressValue=findViewById(R.id.progress_value);
        llProgress=findViewById(R.id.ll_progress);
        serialInfo=findViewById(R.id.serial_info);
        receiveShareData=findViewById(R.id.receive_share_data);
        cbDcd=findViewById(R.id.cb_dcd);

        cbDsr=findViewById(R.id.cb_dsr);
        cbCts=findViewById(R.id.cb_cts);
        cbRi=findViewById(R.id.cb_ri);
        llContentSerial=findViewById(R.id.ll_content_serial);

        /*
        TV_ChgMosFault = findViewById(R.id.ChgMosFault);
        TV_DisChgMosFault = findViewById(R.id.DisChgMosFault);
        TV_Voltage = findViewById(R.id.Voltage);
        TV_Current = findViewById(R.id.Current);
         */



    }


    @Override
    protected void startTask() {
        if(getIntent()!=null){
            address=getIntent().getStringExtra(Constant.INTENT_KET);
            connect(address);
        }

    }

    @Override
    protected void onActivitySomeResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == CODE_CHOOSE_FILE) {
            List<String> list = data.getStringArrayListExtra("paths");
            LogUtil.d(list.get(0));
            if (sendConfigDialog != null && sendConfigDialog.isVisible()) {
                sendConfigDialog.setFileResult(new File(list.get(0)));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.ble_connect).setVisible( !isConnected);
        menu.findItem(R.id.ble_disconnect).setVisible(isConnected);
        menu.findItem(R.id.ble_set_mtu).setVisible(isConnected);
        menu.findItem(R.id.view_debug).setVisible(!isDebugModel());
        menu.findItem(R.id.view_monitor).setVisible(isDebugModel());
        //旧版本,菜单隐藏"可靠传输模式"
        menu.findItem(R.id.send_interrupt).setVisible(!isHardwareOld);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.ble_connect) {
            connect(address);
        } else if (itemId == R.id.ble_disconnect) {
            try {
                CH9141BluetoothManager.getInstance().disconnect(address,false);
            } catch (BLELibException e) {
                e.printStackTrace();
            }
        } else if (itemId == R.id.ble_set_mtu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setMtu();
            } else {
                showToast("当前安卓版本的手机不支持设置mtu");
            }
        } else if (itemId == R.id.view_debug) {
            setDebugModel();
        } else if (itemId == R.id.view_monitor) {
            setMonitorModel();
        } else if (itemId == R.id.send_interrupt) {
            showSendInterruptDialog();
        } else if (itemId == R.id.about) {
            toOfficialWebsite();
        }else if(itemId==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (checkConnected(address)) {
            showDisconnectDialog();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        closeReceive();
        try {
            CH9141BluetoothManager.getInstance().disconnect(address,true);
        } catch (BLELibException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private boolean checkBtEnabled() {
        boolean enabled = BluetoothAdapter.getDefaultAdapter().isEnabled();
        if (!enabled) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_BLUETOOTH_CODE);
        }
        return enabled;
    }


    /******************************************connect**********************************************/


    private void connect(String mac) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //大于安卓10，需要检查定位服务
            LogUtil.d("位置服务打开：" + Location.isLocationEnable(this));
            if (!Location.isLocationEnable(this)) {
                DialogUtil.getInstance().showSimpleDialog(this, "蓝牙扫描需要开启位置信息服务", new DialogUtil.IResult() {
                    @Override
                    public void onContinue() {
                        Location.requestLocationService(MainActivity.this);
                    }

                    @Override
                    public void onCancel() {

                    }
                });

                return;
            }
        }
        if (!checkBtEnabled()) {
            showToast("请先打开蓝牙");
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            showToast("定位权限未开启");
            return;
        }
        if(!BLEUtil.isValidMac(mac)){
            showToast("MAC地址不合法");
            return;
        }
        connectBLE(mac);
    }

    @Override
    protected void onConnecting() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isConnected = false;
                DialogUtil.getInstance().showLoadingDialog(MainActivity.this, "Connecting...");
            }
        });

    }

    @Override
    protected void onConnectSuccess() {
        isHardwareOld=CH9141BluetoothManager.getInstance().isHardwareOld();
        hideNewFunctions(isHardwareOld);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isConnected = true;
                DialogUtil.getInstance().hideLoadingDialog();
                reDraw();
                enableButtons(true);
            }
        });
        initWriteData();
        initReadData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            syncMtu();
        }
        initNotifyCallback();

        //如果是老版本硬件，不需要同步参数，提醒用户
        //modified by terry for keeping the GUI part to old hardware version
        /*
        if(!isHardwareOld){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //清空状态
                    ConfigSaveUtil.getInstance().setSerialStatusBean(null);
                    syncSerialStatus(ConfigSaveUtil.getInstance().getSerialStatusBean().getSerialBaudBean(),
                            ConfigSaveUtil.getInstance().getSerialStatusBean().getSerialModemBean());
                }
            }, 700);
        }else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    DialogUtil.getInstance().showSimpleDialog(MainActivity.this, getResources().getString(R.string.old_version),"OK", new DialogUtil.IResult() {
                        @Override
                        public void onContinue() {

                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            });
        }

         */

        //modified by terry for GUI part begin
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    DialogUtil.getInstance().showSimpleDialog(MainActivity.this, getResources().getString(R.string.old_version),"OK", new DialogUtil.IResult() {
                        @Override
                        public void onContinue() {

                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            });
        }
        //modified by terry for GUI part end


    }

    @Override
    protected void onConnectError(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isConnected = false;
                DialogUtil.getInstance().hideLoadingDialog();
            }
        });
    }

    @Override
    protected void onDisconnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isConnected = false;
                reDraw();
                enableButtons(false);
                DialogUtil.getInstance().hideLoadingDialog();
            }
        });

    }


    /********************************************ui************************************************/


    //如果连接到旧版的CH9141，需要隐藏部分新版本功能
    void hideNewFunctions(final boolean isOld) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //界面上串口以及Modem状态相关界面隐藏
                llContentSerial.setVisibility(isOld? View.GONE:View.VISIBLE);

            }
        });
    }

    /**
     * 重绘
     */
    void reDraw() {
        invalidateOptionsMenu();
    }

    /**
     *
     */
    boolean isDebugModel() {
        return llSend.getVisibility() == View.VISIBLE;
    }

    /**
     * 进入监听模式，隐藏发送区域
     */
    void setMonitorModel() {

        llSend.setVisibility(View.GONE);



        //added by terry for 2 modes begin
        ViewGroup.LayoutParams lp;
        lp = findViewById(R.id.linerLayout_textView).getLayoutParams();
        lp.height = 1800;
        findViewById(R.id.linerLayout_textView).setLayoutParams(lp);

        lp = findViewById(R.id.receive_data).getLayoutParams();
        lp.height = 1800;
        findViewById(R.id.receive_data).setLayoutParams(lp);

        reDraw();
    }

    /**
     * 进入调试模式，显示送区域
     */
    void setDebugModel() {
        llSend.setVisibility(View.VISIBLE);

        //added by terry for 2 modes begin

        ViewGroup.LayoutParams lp;
        lp = findViewById(R.id.linerLayout_textView).getLayoutParams();
        lp.height = 2;
        findViewById(R.id.linerLayout_textView).setLayoutParams(lp);

        lp = findViewById(R.id.receive_data).getLayoutParams();
        lp.height = 2;
        findViewById(R.id.receive_data).setLayoutParams(lp);

        //added for test by terry
        /*
        ((TextView)findViewById(R.id.DisChgMosFault)).setBackgroundColor(Color.RED);
        ((TextView)findViewById(R.id.DisChgMosFault)).setTextColor(Color.WHITE);

        ((TextView)findViewById(R.id.ChgCellOverVol)).setBackgroundColor(Color.RED);
        ((TextView)findViewById(R.id.ChgCellOverVol)).setTextColor(Color.WHITE);

        ((TextView)findViewById(R.id.ChgOverTemp)).setBackgroundColor(Color.RED);
        ((TextView)findViewById(R.id.ChgOverTemp)).setTextColor(Color.WHITE);

        ((TextView)findViewById(R.id.SystemStatus)).setBackgroundColor(Color.RED);
        ((TextView)findViewById(R.id.SystemStatus)).setText("Fault");
        ((TextView)findViewById(R.id.SystemStatus)).setTextColor(Color.WHITE);

        ((TextView)findViewById(R.id.Cell4)).setBackgroundColor(Color.rgb(255,248,220));
        ((TextView)findViewById(R.id.Cell4)).setText("2550");
        ((TextView)findViewById(R.id.Cell4)).setTextColor(Color.WHITE);

        ((TextView)findViewById(R.id.Cell10)).setBackgroundColor(Color.RED);
        ((TextView)findViewById(R.id.Cell10)).setText("4310");
        ((TextView)findViewById(R.id.Cell10)).setTextColor(Color.WHITE);


         */


        reDraw();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void setMtu() {

        MtuDialog dialog = MtuDialog.newInstance();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "Mtu");
        dialog.setListener(new MtuDialog.ClickListener() {
            @Override
            public void onResult(int mtu) {
                setMtu(mtu);
            }
        });
    }

    private void initUi() {
        receiveData.setMovementMethod(ScrollingMovementMethod.getInstance());
        write.setText(Constant.SEND);
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBtEnabled()) {
                    showToast("请先打开蓝牙");
                    return;
                }
                byte[] bytes = new byte[0];
                if (write.getText().toString().equalsIgnoreCase(Constant.SEND)) {
                    try {
                        if (sendHex.isChecked()) {
                            String s=sendData.getText().toString();
                            if(!s.matches("([0-9|a-f|A-F]{2})*")){
                                showToast("发送内容不符合HEX规范");
                                return;
                            }

                            bytes = FormatUtil.hexStringToBytes(sendData.getText().toString());
                        } else {
                            bytes = sendData.getText().toString().getBytes("utf-8");
                        }
                        //分析发送状态,默认是单次发送

                        //added by terry for send modbus command to BMS begin
                        //sendData.setText("0103C80000507B96");//this is the modbus command to read the realtime information about
                        //sendData.setText("0103C8000051BA56");
                        sendData.setText(R.string.RequestRealTimeData);

                        //Liuyang-Platform

                        bytes = FormatUtil.hexStringToBytes(sendData.getText().toString());
                        RecRealTimeInfo.ResetModbusVcRealtimeInfo();
                        //added by terry for send modbus command to BMS end
                        SendBean sendStatus = ConfigSaveUtil.getInstance().getSendStatus();
                        if (sendStatus != null) {
                            if (sendStatus.getType() == SendType.TYPE_SINGLE) {
                                write(SendType.TYPE_SINGLE, bytes, 0);
                            } else if (sendStatus.getType() == SendType.TYPE_TIMING) {
                                write(SendType.TYPE_TIMING, bytes, sendStatus.getInterval());
                            } else if (sendStatus.getType() == SendType.TYPE_CYCLIC) {
                                write(SendType.TYPE_CYCLIC, bytes, sendStatus.getInterval());
                            } else if (sendStatus.getType() == SendType.TYPE_FILE) {
                                write(sendStatus.getFile());
                            }
                        } else {
                            write(SendType.TYPE_SINGLE, bytes, 0);
                        }


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else if (write.getText().toString().equalsIgnoreCase(Constant.STOP)) {
                    cancel();
                }

            }
        });
        clearWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearWrite();
            }
        });
        clearReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRead();
            }
        });

        receiveSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.Bundle_Mac, address);
                ReceiveConfigDialog dialog = ReceiveConfigDialog.newInstance(ConfigSaveUtil.getInstance().getSaveStatus(), bundle);
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "ReceiveConfigDialog");

                dialog.setOnListener(new ReceiveConfigDialog.OnResult() {
                    @Override
                    public void onChanged() {
                        reModifySaveStatus();
                    }
                });
            }
        });
        receiveShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                share("Share function needs exclusive using the file, switch to real time show mode, continue？");
            }
        });
        receiveShareData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareData("Share Data Function will save the received data in the Receive View to File and Share, Continue?？");
            }
        });

        sendSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendConfigDialog = SendConfigDialog.newInstance(ConfigSaveUtil.getInstance().getSendStatus());
                sendConfigDialog.setCancelable(false);
                sendConfigDialog.show(getSupportFragmentManager(), "SendConfigDialog");
                sendConfigDialog.setOnClickListener(new SendConfigDialog.OnClickListener() {
                    @Override
                    public void onChooseFile() {
                        pickFile();
                    }

                    @Override
                    public void onConfirm() {
                        reModifySendStatus();
                    }
                });
            }
        });

        btnSetSerial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SerialUtil.setBaudRate(myConnection, mConfig, 115200, 8, 1, 0);
                SerialConfigDialog dialog = SerialConfigDialog.newInstance(ConfigSaveUtil.getInstance().getSerialStatusBean());
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "SerialConfigDialog");
                dialog.setListener(new SerialConfigDialog.onClickListener() {
                    @Override
                    public void onSetBaud(SerialBaudBean data) {
                        if (!checkBtEnabled()) {
                            showToast("请先打开蓝牙");
                            return;
                        }
                        setSerial(data);
                    }

                    @Override
                    public void onSetModem(SerialModemBean data) {
                        setModem( data);
                    }
                });
            }
        });
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //只通过通知接收数据
            }
        });

         //added by terry for fault text view button click action
        /*


        TV_ChgMosFault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                String MsgName = (String) TV_ChgMosFault.getText();
                builder.setTitle(MsgName);
                builder.setMessage("There is no Fault happened now");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("点了确定");
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("点了取消");
                    }
                });
                //一样要show
                builder.show();
            }
        });

        TV_DisChgMosFault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd: HH:mm:ss",Locale.getDefault());
                String MsgName = (String) TV_DisChgMosFault.getText();
                builder.setTitle(MsgName);
                long timegettime = System.currentTimeMillis();
                String time = sdf.format(timegettime);
                builder.setMessage("Fault Happened time: " + time);



                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("点了确定");
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("点了取消");
                    }
                });
                //一样要show
                builder.show();
            }
        });

         */

    }

    /**
     * 根据连接状态，将按钮disable
     *
     * @param isConnected 蓝牙连接状态
     */
    void enableButtons(boolean isConnected) {
        btnSetSerial.setEnabled(isConnected);
        receiveSet.setEnabled(isConnected);
        read.setEnabled(isConnected);
        sendSet.setEnabled(isConnected);
        write.setEnabled(isConnected);
        clearReceive.setEnabled(isConnected);
        clearWrite.setEnabled(isConnected);
        receiveShareData.setEnabled(isConnected);

    }

    void updateModem(final boolean DCD, final boolean RI, final boolean DSR, final boolean CTS){
        handler.post(new Runnable() {
            @Override
            public void run() {
                cbDcd.setChecked(DCD);
                cbCts.setChecked(CTS);
                cbDsr.setChecked(DSR);
                cbRi.setChecked(RI);
            }
        });
    }

    void updateModem(final byte[] data) {
        if (data == null || data.length != 7) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("更新modem状态");
                cbDcd.setChecked(ModemParse.isDCDValid(data[5]));
                cbCts.setChecked(ModemParse.isCTSValid(data[5]));
                cbDsr.setChecked(ModemParse.isDSRValid(data[5]));
                cbRi.setChecked(ModemParse.isRIValid(data[5]));
            }
        });
    }


    /***********************************************通信相关*************************************/
    //////////////////////////////////////////发送数据/////////////////////////

    private SingleTask singleTask;
    private FileTak fileTask;
    private TimingTask timingTask;

    private long count_W = 0;
    private long tempCount_W = 0;
    private long speed_W = 0;

    public volatile boolean flag = false;

    private SendConfigDialog sendConfigDialog;

    /**
     * 默认是单次发送
     */
    private SendType type = SendType.TYPE_SINGLE;

    /**
     * 定时器用来计数
     */
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {

            speed_R = count_R - tempCount_R;
            tempCount_R = count_R;
            speed_W = count_W - tempCount_W;
            tempCount_W = count_W;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //if (sendSpeed != null) {
                    //    sendSpeed.setText(String.format(Locale.US, "%d 字节/秒", speed_W));
                    //}
                    //if (receiveSpeed != null) {
                    //    receiveSpeed.setText(String.format(Locale.US, "%d 字节/秒", speed_R));
                    //}
                }
            });
        }
    };

    /**
     * 发送数据
     *
     * @param type
     * @param data
     */
    void write(SendType type, @NonNull byte[] data, long interval) {

        if (!checkValid()) {
            showToast("请检查蓝牙连接状态");
            LogUtil.d("Write Invalid");
            return;
        }
        if (data.length == 0) {
            showToast("发送内容为空");
            return;
        }
        if (type == SendType.TYPE_SINGLE) {
            LogUtil.d("singleWrite");
            singleTask = new SingleTask(this);
            singleTask.execute(new BytesTaskBean(data));
        } else if (type == SendType.TYPE_CYCLIC || type == SendType.TYPE_TIMING) {
            timingTask = new TimingTask(this);
            timingTask.execute(new BytesTaskBean(data, interval, RecRealTimeInfo));
        } else {
            LogUtil.d("unknown write type");
        }
    }

    /**
     * 发送文件
     *
     * @param file
     */
    void write(@NonNull File file) {
        if (!checkValid()) {
            showToast("请检查蓝牙连接状态");
            LogUtil.d("Write Invalid");
            return;
        }
        if (!file.exists()) {
            showToast("待发送的文件不存在");
            LogUtil.d("待发送的文件不存在");
            return;
        }
        fileTask = new FileTak(this);
        fileTask.execute(new FileTaskBean(file));
    }

    /**
     * 取消发送
     */
    void cancel() {
        //写取消
        CH9141BluetoothManager.getInstance().stopWrite();
        if (singleTask != null && singleTask.getStatus() == AsyncTask.Status.RUNNING) {
            singleTask.cancel(true);
        } else if (fileTask != null && fileTask.getStatus() == AsyncTask.Status.RUNNING) {
            fileTask.cancel(true);
        } else if (timingTask != null && timingTask.getStatus() == AsyncTask.Status.RUNNING) {
            timingTask.cancel(true);
        }
    }

    private boolean isRunningSendTask() {
        if (singleTask != null && singleTask.getStatus() == AsyncTask.Status.RUNNING) {
            return true;
        } else if (fileTask != null && fileTask.getStatus() == AsyncTask.Status.RUNNING) {
            return true;
        } else if (timingTask != null && timingTask.getStatus() == AsyncTask.Status.RUNNING) {
            return true;
        }
        return true;
    }

    private void clearWrite() {

        count_W = 0;
        tempCount_W = 0;
        speed_W = 0;
        sendData.setText("");
        //sendCount.setText(count_W + " 字节");
    }

    private void initWriteData() {
        flag = false;
        count_W = 0;
        tempCount_W = 0;
        speed_W = 0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                //sendCount.setText(count_W + " 字节");
                //sendSpeed.setText(speed_W + " 字节/秒");
            }
        });
    }

    void pickFile() {
        new LFilePicker()
                .withActivity(MainActivity.this)
                .withRequestCode(CODE_CHOOSE_FILE)
                .withStartPath(Environment.getExternalStorageDirectory().getAbsolutePath())//指定初始显示路径
                .withMaxNum(1)
                .withNotFoundBooks("请选择一个文件")
                .start();
    }

    void reModifySendStatus() {
        SendBean sendStatus = ConfigSaveUtil.getInstance().getSendStatus();
        if (sendStatus != null) {
            //delete by terry for removing show new sending configuration
            //sendDesc.setText(sendStatus.toString());
            if (sendStatus.getType() == SendType.TYPE_FILE) {
                llProgress.setVisibility(View.GONE);
            } else {
                llProgress.setVisibility(View.GONE);
            }
        } else {
            LogUtil.d("获取到的发生状态信息为空");
        }
    }

    void updateValue(final int newCount) {
        count_W += newCount;
        handler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("发送总计："+count_W);
                //sendCount.setText(count_W + " 字节");
            }
        });
    }

    void showSendInterruptDialog() {
        SendInterruptConfigDialog interruptConfigDialog = SendInterruptConfigDialog.newInstance(CH9141BluetoothManager.getInstance().isTransmissionReliable());
        interruptConfigDialog.setCancelable(false);
        interruptConfigDialog.show(getSupportFragmentManager(), "SendInterruptConfigDialog");
    }

    void toOfficialWebsite() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://chn.tws.com/"));
        startActivity(intent);
    }

    @Override
    public void onPreExecute(SendType type) {
        /*if (type == SendType.TYPE_SINGLE) {
            write.setEnabled(false);
        } else {
            //长时间发送
            write.setText(Constant.STOP);
            if (type == SendType.TYPE_FILE) {
                progressValue.setText("0%");
                progress.setProgress(0);
            }
        }*/

        write.setText(Constant.STOP);
        if (type == SendType.TYPE_FILE) {
            progressValue.setText("0%");
            progress.setProgress(0);
        }

    }

    @Override
    public void onCount(int count) {
        updateValue(count);
    }

    //只用于文件
    @Override
    public void onProgress(long current, long total) {
        //updateProgress(current,total);

    }

    void updateProgress(long current, long total) {
        final int i = (int) (current / total * 100);
        handler.post(new Runnable() {
            @Override
            public void run() {
                progress.setProgress(i);
                progressValue.setText(String.format(Locale.US, "%d %%", i));
            }
        });
    }

    @Override
    public void onCancel(SendType type) {
        /*if (type == SendType.TYPE_SINGLE) {
            write.setEnabled(true);
        } else {
            //长时间发送
            write.setText(Constant.SEND);
        }*/
        write.setText(Constant.SEND);
    }

    //只用于文件和单次发送
    @Override
    public void onResult(SendType type, boolean result) {
        LogUtil.d("发送结果：" + result);
        /*if (!result) {
            showToast("发送失败");
        }
        if (type == SendType.TYPE_SINGLE) {
            write.setEnabled(true);
        } else {
            //长时间发送
            write.setText(Constant.SEND);
        }*/
        if(type==SendType.TYPE_SINGLE || type==SendType.TYPE_FILE){
            if (!result) {
                showToast("发送未完成");
            }
            write.setText(Constant.SEND);
        }else if(type==SendType.TYPE_TIMING ) {
            write.setText(Constant.SEND);
        }
    }
    /////////////////////////////////////////接收数据////////////////////////////////////////

    private long count_R = 0;
    private long tempCount_R = 0;
    private long speed_R = 0;

    private File file = null;
    private FileOutputStream fos = null;

    /**
     * 初始化两个通知，即接收数据的通知和状态的通知
     */
    private void initNotifyCallback() {

        //added by terry for test
        boolean isCheck = true;
        CH9141BluetoothManager.getInstance().setNotify(isCheck);
        //added by terry for test

        CH9141BluetoothManager.getInstance().registerSerialModemNotify(new ModemStatus() {
            @Override
            public void onNotify(boolean DCD, boolean RI, boolean DSR, boolean CTS) {
                updateModem(DCD, RI, DSR, CTS);
            }
        });
        CH9141BluetoothManager.getInstance().registerReadNotify(new NotifyStatus() {
            @Override
            public void onData(final byte[] data) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateValueTextView(data);
                    }
                });
            }
        });

        //数据通知相关
        handler.post(new Runnable() {
            @Override
            public void run() {
                //charDetailsNotificationSwitcher.setOnCheckedChangeListener(null);
                //charDetailsNotificationSwitcher.setChecked(CH9141BluetoothManager.getInstance().getNotifyState());
                //charDetailsNotificationSwitcher.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                 //   @Override
                //    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                 //       LogUtil.d("NotificationSwitcher: " + isChecked);
                 //       CH9141BluetoothManager.getInstance().setNotify(isChecked);
                 //   }
                //});

            }
        });
    }


    private void updateValueTextView(final byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        count_R += data.length;
        LogUtil.d("共接收到："+count_R);
        //added by terry for receiving data begin
        if (RecRealTimeInfo.bytelength == 0)
        {
            for (int i = 0; i < 3; i++)
            {
                receiveData.append("\n");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd: HH:mm:ss",Locale.getDefault());
            long timegettime = System.currentTimeMillis();
            String time = sdf.format(timegettime);
            receiveData.append(time + ": ");
            receiveData.append("\n");
        }
        System.arraycopy(data,0, RecRealTimeInfo.RecBytes, RecRealTimeInfo.bytelength, data.length);
        RecRealTimeInfo.bytelength = RecRealTimeInfo.bytelength + data.length;

        //TV_Voltage.setText("rec" + count_R + ":"+ Integer.toHexString(data[0] & 0xFF) + Integer.toHexString(data[1] & 0xFF) + Integer.toHexString(data[2] & 0xFF) + Integer.toHexString(data[3] & 0xFF));

        //added by terry for receiving data end
        if (file != null && fos != null) {
            //保存至文件
            try {
                fos.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //receiveCount.setText(count_R + " 字节");
                }
            });
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                //receiveCount.setText(count_R + " 字节");
                //This part is used to record the data length, which can be shown in the textview
                if (receiveData.getText().toString().length() >= 15000) {

                    //added by terry for test, to delete these 2 operations, in order to let the receive data to always receive data.
                    //receiveData.setText("");
                    //receiveData.scrollTo(0, 0);
                }

                /* //delete by terry, the UI update show is in ModbusVcRealtimeInfo class
                if (receiveHex.isChecked()) {
                    receiveData.append(FormatUtil.bytesToHexString(data));
                } else {
                    receiveData.append(new String(data));
                }

                int offset = receiveData.getLineCount() * receiveData.getLineHeight();
                //int maxHeight = usbReadValue.getMaxHeight();
                int height = receiveData.getHeight();
                //USBLog.d("offset: "+offset+"  maxHeight: "+maxHeight+" height: "+height);
                if (offset > height) {
                    //USBLog.d("scroll: "+(offset - usbReadValue.getHeight() + usbReadValue.getLineHeight()));
                    receiveData.scrollTo(0, offset - receiveData.getHeight() + receiveData.getLineHeight());
                }
                */
            }
        });

    }

    void clearRead() {
        count_R = 0;
        tempCount_R = 0;
        speed_R = 0;
        receiveData.setText("");
        //receiveCount.setText(count_R + " 字节");
        receiveData.scrollTo(0, 0);
    }

    private void initReadData() {
        count_R = 0;
        tempCount_R = 0;
        speed_R = 0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                //receiveCount.setText(count_R + " 字节");
                //receiveSpeed.setText(speed_R + " 字节/秒");

                //清空modem状态
                cbDcd.setChecked(false);
                cbDsr.setChecked(false);
                cbCts.setChecked(false);
                cbRi.setChecked(false);
            }
        });
    }

    private void saveToFile(@NonNull File f) {
        LogUtil.d("try saveToFile: " + f.getAbsolutePath());
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //检查当前是否正处于保存至文件的状态
        //true
        if (file != null && fos != null) {

            //检查是否是同一个文件
            if (file.getAbsolutePath().equals(f.getAbsolutePath())) {
                //true
                LogUtil.d("同一个文件，无需更改");
                return;
            } else {
                //false,关闭当前文件
                LogUtil.d("不同一个文件，需先关闭");
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos = new FileOutputStream(f);
                    file = f;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            LogUtil.d("直接打开");
            file = f;
            try {
                fos = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //ui更新
        llReceiveSave.setVisibility(View.VISIBLE);
        receiveDesc.setText("正保存至:" + f.getName());
    }

    private void showOnScreen() {
        llReceiveSave.setVisibility(View.GONE);
        receiveDesc.setText("");
        if (fos != null) {
            try {
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fos = null;
        file = null;
    }

    void reModifySaveStatus() {
        SaveBean saveStatus = ConfigSaveUtil.getInstance().getSaveStatus();
        if (saveStatus != null) {
            if (saveStatus.getType() == SaveType.SHOW_SCREEN) {
                showOnScreen();
            } else if (saveStatus.getType() == SaveType.SAVE_FILE) {
                if (saveStatus.getFile() == null) {
                    LogUtil.d("不合法的字段，File不能为空");
                    return;
                }
                saveToFile(saveStatus.getFile());
            } else {
                LogUtil.d("未定义的接收类型");
            }
        }
    }

    /**
     * 防止fos未关闭
     */
    private void closeReceive() {
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fos = null;
        file = null;
    }

    private void share(String message) {
        LogUtil.d("-------->尝试分享");
        if (file == null || fos == null) {
            return;
        }
        LogUtil.d("-------->开始分享");
        DialogUtil.getInstance().showSimpleDialog(this, message, new DialogUtil.IResult() {
            @Override
            public void onContinue() {
                //切换为实时显示
                llReceiveSave.setVisibility(View.GONE);
                receiveDesc.setText("");
                String p = file.getAbsolutePath();
                closeReceive();
                //底部菜单，选择通过微信或邮件
                showSnack(p);
            }

            @Override
            public void onCancel() {
                LogUtil.d("-------->取消分享");
            }
        });
    }

    /**
     * 分享当前接收数据区的数据
     */
    private void shareData(String message) {
        LogUtil.d("-------->尝试分享");
        final String s = receiveData.getText().toString();
        if (TextUtils.isEmpty(s)) {
            showToast("Currently Receive 0 length data");
            return;
        }
        LogUtil.d("-------->开始分享");
        DialogUtil.getInstance().showSimpleDialog(this, message, new DialogUtil.IResult() {
            @Override
            public void onContinue() {
                //将数据保存至文件
                File dir = MainActivity.this.getExternalFilesDir(Constant.Folder);
                File log = new File(dir, FileUtil.getRandomName(address) + "_Screen.log");
                try {
                    if (!log.exists()) {
                        log.createNewFile();
                    }
                    FileUtil.writeDataToFile(s.getBytes(), log);
                    //底部菜单，选择通过微信或邮件
                    showSnack(log.getAbsolutePath());
                } catch (Exception e) {
                    showToast("分享失败");
                }
            }

            @Override
            public void onCancel() {
                LogUtil.d("-------->取消分享");
            }
        });
    }

    private void showSnack(final String filePath) {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this, R.layout.dialog_snack, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        dialog.findViewById(R.id.sb_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                AppUtil.sendTxt(MainActivity.this, filePath, "蓝牙文件");
            }
        });
        dialog.findViewById(R.id.sb_sns).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                AppUtil.shareTxt(MainActivity.this, filePath);
            }
        });
        dialog.findViewById(R.id.sb_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 同步软件与硬件配置，包括波特率和Modem流控，用于连接蓝牙后同步
     */
    public void syncSerialStatus(final SerialBaudBean baudBean, final SerialModemBean modemBean) {
        LogUtil.d("同步参数");
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                boolean b = CH9141BluetoothManager.getInstance().setSerialBaud(baudBean.getBaud(), baudBean.getData(), baudBean.getStop(), baudBean.getParity());

                if(!b){
                    emitter.onError(new Throwable("set baud fail"));
                    return;
                }
                boolean b1 = CH9141BluetoothManager.getInstance().setSerialModem(modemBean.isFlow(), modemBean.getDTR(), modemBean.getRTS());
                if(!b1){
                    emitter.onError(new Throwable("set flow and modem fail"));
                    return;
                }

                emitter.onComplete();

            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(MainActivity.this, "正在同步参数");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(e.getMessage());
                        showToast("同步参数失败");
                        DialogUtil.getInstance().hideLoadingDialog();
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("同步参数成功");
                        serialInfo.setText(baudBean.toString());
                    }
                });
    }

    /**
     * 设置Modem状态
     */
    public void setModem(final SerialModemBean modemBean) {
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                boolean b1 = CH9141BluetoothManager.getInstance().setSerialModem(modemBean.isFlow(), modemBean.getDTR(), modemBean.getRTS());
                if(!b1){
                    emitter.onError(new Throwable("set flow and modem fail"));
                    return;
                }
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(MainActivity.this, "设置参数");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(e.getMessage());
                        showToast("设置失败");
                        DialogUtil.getInstance().hideLoadingDialog();
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("设置成功");
                        ConfigSaveUtil.getInstance().getSerialStatusBean().setSerialModemBean(modemBean);
                    }
                });
    }

    /**
     * 设置串口波特率相关
     *

     * @param data
     */
    public void setSerial(final SerialBaudBean data) {
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                boolean b = CH9141BluetoothManager.getInstance().setSerialBaud(data.getBaud(), data.getData(), data.getStop(), data.getParity());

                if(!b){
                    emitter.onError(new Throwable("set baud fail"));
                    return;
                }
                emitter.onComplete();

            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(MainActivity.this, "设置参数");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(e.getMessage());
                        showToast("设置失败");
                        DialogUtil.getInstance().hideLoadingDialog();
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("设置成功");
                        serialInfo.setText(data.toString());
                        ConfigSaveUtil.getInstance().getSerialStatusBean().setSerialBaudBean(data);
                    }
                });

    }
    //初次连接后将MTU置为最大
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void syncMtu(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                setMtu(247);
            }
        });
    }

    //用作测试RSSI读取，定时器需要取消
    void testRSSI(){
        CH9141BluetoothManager.getInstance().registerRSSINotify(new RSSIStatus() {
            @Override
            public void onRSSI(int rssi, int status) {
                LogUtil.d("RSSI: "+rssi);
            }
        });
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("read :"+ CH9141BluetoothManager.getInstance().readRSSI());

            }
        },200,1000, TimeUnit.MILLISECONDS);
    }
}