package cn.wch.ch9141tool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.wch.bleconfig.ConfigActivity;
import cn.wch.blelib.ch9141.CH9141BluetoothManager;
import cn.wch.blelib.ch9141.callback.ScanResult;
import cn.wch.blelib.ch9141.constant.Constant;
import cn.wch.blelib.exception.BLELibException;
import cn.wch.blelib.utils.Location;
import cn.wch.blelib.utils.LogUtil;
import cn.wch.ch9141tool.ui.DeviceAdapter;
import cn.wch.ch9141tool.ui.ScanBaseActivity;
import cn.wch.otaupdate.MainActivity;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.touchmcu.ui.DialogUtil;

public class ScanActivity extends ScanBaseActivity {


    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private boolean isScanning=false;
    private Handler handler=new Handler(Looper.getMainLooper());

    @Override
    public void onCreateView() {
        setContentView(R.layout.activity_scan);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Searching CH9141 Dev");
        setSupportActionBar(toolbar);

    }

    @Override
    public void initWidget() {
        recyclerView=findViewById(R.id.rvDevice);

        adapter=new DeviceAdapter(this, new DeviceAdapter.Listener() {
            @Override
            public void onClick(BluetoothDevice device) {
                showSnackBar(device.getAddress());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void autoRun() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                startScan();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main,menu);
        menu.findItem(R.id.startScan).setVisible(!isScanning);
        menu.findItem(R.id.indicator).setVisible(isScanning);
        menu.findItem(R.id.indicator).setActionView(R.layout.menu_progress);
        menu.findItem(R.id.stopScan).setVisible(isScanning);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();
        if(itemId==R.id.startScan){
            startScan();
        }else if(itemId==R.id.stopScan){
            stopScan();
        }else if(itemId==android.R.id.home){
            onBackPressed();
            return true;
        }
        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        stopScan();
        finish();
    }

    private void startScan(){
        adapter.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //大于安卓10，需要检查定位服务
            LogUtil.d("位置服务打开：" + Location.isLocationEnable(this));
            if (!Location.isLocationEnable(this)) {
                DialogUtil.getInstance().showSimpleDialog(this, "蓝牙扫描功能需要开启位置信息服务", new DialogUtil.IResult() {
                    @Override
                    public void onContinue() {
                        Location.requestLocationService(ScanActivity.this);
                    }

                    @Override
                    public void onCancel() {

                    }
                });

                return;
            }
        }
        if (!isBluetoothAdapterOpened()) {
            showToast("Open Blue Tooth First");
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
            showToast("定位权限未开启,无法扫描");
            return;
        }
        isScanning=true;
        try {

            CH9141BluetoothManager.getInstance().startScan(new ScanResult() {
                @Override
                public void onResult(BluetoothDevice device, int rssi, byte[] broadcastRecord) {
                    if(adapter!=null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.update(device,rssi);
                            }
                        });
                    }
                }
            });
        } catch (BLELibException e) {
            e.printStackTrace();
            LogUtil.d(e.getMessage());
        }
        invalidateOptionsMenu();
    }

    void stopScan(){
        isScanning=false;
        LogUtil.d("停止扫描");
        try {
            CH9141BluetoothManager.getInstance().stopScan();
        } catch (BLELibException e) {
            e.printStackTrace();
            LogUtil.d(e.getMessage());
        }
        invalidateOptionsMenu();
    }

    protected void showToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSnackBar(String mac) {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(getContext(), R.layout.item_snack, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        dialog.findViewById(R.id.tv_ble_comm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                toAnotherActivity(mac, cn.wch.blecomm.MainActivity.class);
            }
        });
        dialog.findViewById(R.id.tv_ble_config).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                toAnotherActivity(mac, ConfigActivity.class);
            }
        });
        dialog.findViewById(R.id.tv_ble_ota).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                toAnotherActivity(mac, MainActivity.class);
            }
        });
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void toAnotherActivity(String mac, Class<? extends AppCompatActivity> c){
        stopScan();
        LogUtil.d(mac);
        Intent intent=new Intent(getContext(),c);
        intent.putExtra(Constant.INTENT_KEY_ADDRESS,mac);
        startActivity(intent);
    }


}