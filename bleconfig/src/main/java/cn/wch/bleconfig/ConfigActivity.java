package cn.wch.bleconfig;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.touchmcu.ui.DialogUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import cn.wch.bleconfig.ui.ControlFragment;

import cn.wch.bleconfig.ui.DeviceFragment;
import cn.wch.bleconfig.ui.GPIOFragment;
import cn.wch.bleconfig.ui.ModuleFragment;
import cn.wch.bleconfig.ui.main.PageViewModel;
import cn.wch.bleconfig.ui.main.SectionsPagerAdapter;

import cn.wch.blelib.ch9141.config.CH9141ConfigManager;
import cn.wch.blelib.ch9141.config.callback.ConnectStatus;
import cn.wch.blelib.ch9141.config.entry.ControlInfo;
import cn.wch.blelib.ch9141.config.entry.DeviceInfo;
import cn.wch.blelib.ch9141.config.entry.ModuleInfo;
import cn.wch.blelib.ch9141.config.entry.gpio.GPIOReadResult;
import cn.wch.blelib.ch9141.config.entry.gpio.GPIOSetResult;
import cn.wch.blelib.exception.BLELibException;
import cn.wch.blelib.host.core.ConnRuler;
import cn.wch.blelib.host.core.Connection;
import cn.wch.blelib.utils.LogUtil;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static cn.wch.bleconfig.Constant.Constant.INTENT_KEY;

public class ConfigActivity extends AppCompatActivity implements ICommand{

    private ModuleFragment moduleFragment;
    private DeviceFragment deviceFragment;
    private GPIOFragment gpioFragment;
    private PageViewModel pageViewModel;
    private ControlFragment controlFragment;

    private Context mContext;
    private String address;
    private Handler handler=new Handler(Looper.getMainLooper());
    private boolean isConnected=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        mContext=this;
        address=getIntent().getStringExtra(INTENT_KEY);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("????????????");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_config,menu);
        menu.findItem(R.id.ble_connect).setVisible(!isConnected);
        menu.findItem(R.id.reset_config).setVisible(isConnected);
        menu.findItem(R.id.reset_info).setVisible(isConnected);
        menu.findItem(R.id.reset_chip).setVisible(isConnected);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==android.R.id.home){
            onBackPressed();
        }else if(itemId==R.id.ble_connect){
            connectBLE(address);
        }else if(itemId==R.id.reset_config){
            resetModule();
        }else if(itemId==R.id.reset_info){
            resetDevice();
        }else if(itemId==R.id.reset_chip){
            resetChip();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        LogUtil.d("onBackPressed: "+address);
        if(CH9141ConfigManager.getInstance().isConnected(address)){
            DialogUtil.getInstance().showSimpleDialog(ConfigActivity.this, "????????????????????????????????????", "??????", "??????", new DialogUtil.IResult() {
                @Override
                public void onContinue() {
                    try {
                        CH9141ConfigManager.getInstance().disconnect(address,false);
                    } catch (BLELibException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancel() {

                }
            });
            return;
        }else {
            LogUtil.d("?????????");
            finish();
        }

    }

    private void init() {
        moduleFragment=ModuleFragment.newInstance();
        deviceFragment=DeviceFragment.newInstance();
        gpioFragment=GPIOFragment.newInstance();
        controlFragment= ControlFragment.newInstance();
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(moduleFragment);
        fragments.add(deviceFragment);
        fragments.add(gpioFragment);
        fragments.add(controlFragment);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(),fragments);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //????????????
        moduleFragment.register(this);
        deviceFragment.register(this);
        controlFragment.register(this);
        gpioFragment.register(this);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connectBLE(address);
            }
        },1000);

    }

    void showToasty(final int type,final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(type==0){
                    Toasty.success(mContext,msg, Toasty.LENGTH_SHORT).show();
                }else if(type==1){
                    Toasty.info(mContext,msg,Toasty.LENGTH_SHORT).show();
                }else if(type==2){
                    Toasty.error(mContext,msg,Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void connectBLE(String mac) {
        if (TextUtils.isEmpty(mac) ) {
            LogUtil.d("MAC is null or bleService is null");
            showToasty(2, "MAC is null");
            return ;
        }
        LogUtil.d("????????????");
        ConnRuler ruler = new ConnRuler.Builder().MAC(mac).ConnectTimeout(15000).readTimeout(1000).writeTimeout(1000).build();
        try {
            CH9141ConfigManager.getInstance().connect(address,15000,connectStatus);
        } catch (BLELibException e) {
            e.printStackTrace();
            LogUtil.d(e.getMessage());
        }
        return ;
    }

    ConnectStatus connectStatus=new ConnectStatus() {
        @Override
        public void OnError(Throwable t) {
            showToast(t.getMessage());
        }

        @Override
        public void OnConnecting() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this, "Connecting");
                }
            });
        }

        @Override
        public void OnConnectSuccess(String mac) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtil.getInstance().hideLoadingDialog();
                    refreshMenu(true);
                }
            });
            showToast("????????????");
        }

        @Override
        public void onInvalidDevice(String mac) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtil.getInstance().hideLoadingDialog();
                    showToast("???????????????????????????CH9141");
                    try {
                        CH9141ConfigManager.getInstance().disconnect(address, false);
                    } catch (BLELibException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void OnConnectTimeout(String mac) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtil.getInstance().hideLoadingDialog();
                    refreshMenu(false);
                }
            });
            showToast("????????????");
        }

        @Override
        public void OnDisconnect(String mac, int status) {
            showToast("?????????????????????");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshMenu(false);
                }
            });
        }
    };

    private void refreshMenu(boolean connected){
        isConnected=connected;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                invalidateOptionsMenu();
            }
        });
    }

    /**********************************************?????????UI***********************************************/

    protected void showToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void getModuleInfo() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                ModuleInfo moduleInfo = CH9141ConfigManager.getInstance().getModuleInfo();
                if(moduleInfo==null){
                    emitter.onError(new Throwable("????????????"));
                    return;
                }
                //??????Module
                pageViewModel.setModuleInfo(moduleInfo);
                //??????GPIO
                pageViewModel.setGPIOInfo(moduleInfo);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"??????????????????");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }

    @Override
    public void setModuleInfo(final ModuleInfo moduleInfo) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                boolean b = CH9141ConfigManager.getInstance().setModuleInfo(moduleInfo);
                if(!b){
                    emitter.onError(new Throwable("????????????"));
                    return;
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"??????????????????");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }

    @Override
    public void getDeviceInfo() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                DeviceInfo deviceInfo = CH9141ConfigManager.getInstance().getDeviceInfo();
                if(deviceInfo==null){
                    emitter.onError(new Throwable("????????????"));
                    return;
                }
                pageViewModel.setDeviceInfo(deviceInfo);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"??????????????????");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }

    @Override
    public void setDeviceInfo(final DeviceInfo deviceInfo) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                boolean b = CH9141ConfigManager.getInstance().setDeviceInfo(deviceInfo);
                if(!b){
                    emitter.onError(new Throwable("????????????"));
                    return;
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"??????????????????");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }

    @Override
    public void getControlInfo() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                ControlInfo controlInfo = CH9141ConfigManager.getInstance().getControlInfo();
                if(controlInfo==null){
                    emitter.onError(new Throwable("????????????"));
                    return;
                }
                pageViewModel.setControlInfo(controlInfo);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"??????????????????");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }

    @Override
    public void setControlInfo(final ControlInfo controlInfo) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                boolean b = CH9141ConfigManager.getInstance().setControlInfo(controlInfo);
                if(!b){
                    emitter.onError(new Throwable("????????????"));
                    return;
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"??????????????????");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }

    @Override
    public void setGPIO(final int num, final int dir) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                boolean b = CH9141ConfigManager.getInstance().setGPIO(num, dir);
                if(!b){
                    emitter.onError(new Throwable("??????GPIO??????"));
                    return;
                }
                GPIOSetResult result=new GPIOSetResult();
                result.setSuccess(true);
                result.setDir(dir);
                result.setNum(num);
                pageViewModel.setGPIOSetResult(result);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"??????GPIO");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }

    @Override
    public void readGPIO(final int num) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                int i= CH9141ConfigManager.getInstance().readGPIO(num);
                if(i<0){
                    emitter.onError(new Throwable("????????????"));
                    return;
                }
                GPIOReadResult readResult=new GPIOReadResult();
                readResult.setSuccess(true);
                readResult.setNum(num);
                readResult.setValue(i);
                pageViewModel.setGPIOReadResult(readResult);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"???GPIO");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }

    @Override
    public void writeGPIO(final int num, final int value) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                boolean b = CH9141ConfigManager.getInstance().writeGPIOValue(num, value);
                if(!b){
                    emitter.onError(new Throwable("????????????"));
                    return;
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"???GPIO");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }

    @Override
    public void syncGPIO() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                boolean b = CH9141ConfigManager.getInstance().syncGPIO();
                if(!b){
                    emitter.onError(new Throwable("????????????"));
                    return;
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"??????GPIO");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }

    @Override
    public void readADC() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                double b = CH9141ConfigManager.getInstance().readGPIOAdc();
                if(b<0){
                    emitter.onError(new Throwable("????????????"));
                    return;
                }
                pageViewModel.setGPIOAdc(b);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"???ADC");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }

    @Override
    public void resetModule() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                boolean b = CH9141ConfigManager.getInstance().resetModuleInfo();
                if(!b){
                    emitter.onError(new Throwable("????????????"));
                    return;
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"??????????????????");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }

    @Override
    public void resetDevice() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                boolean b = CH9141ConfigManager.getInstance().resetDeviceInfo();
                if(!b){
                    emitter.onError(new Throwable("????????????"));
                    return;
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"??????????????????");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }

    @Override
    public void resetChip() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                boolean b = CH9141ConfigManager.getInstance().resetChip();
                if(!b){
                    emitter.onError(new Throwable("????????????"));
                    return;
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(ConfigActivity.this,"????????????");
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                        showToast("????????????");
                    }
                });
    }
}