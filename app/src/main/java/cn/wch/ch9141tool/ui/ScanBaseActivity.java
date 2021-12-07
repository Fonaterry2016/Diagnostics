package cn.wch.ch9141tool.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.wch.blelib.ch9141.ota.CH9141OTAManager;
import io.reactivex.functions.Consumer;

public abstract class ScanBaseActivity extends AppCompatActivity {
    private int BT_CODE=111;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        onCreateView();
        initWidget();
        initBle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BT_CODE && resultCode == RESULT_OK) {
            autoRun();
            return;
        } else if (requestCode == BT_CODE) {
            showToast("请允许打开蓝牙");
            return;
        }

    }

    private void initBle(){
        if(!isSupportBle(context)){
            showToast("该设备不支持BLE");
            return;
        }
        RxPermissions permissions=new RxPermissions(this);
        permissions.requestEachCombined(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {//全部同意后调用
                            createOTAFolder();
                            checkAdapter();
                        } else if (permission.shouldShowRequestPermissionRationale) {//只要有一个选择：禁止，但没有选择“以后不再询问”，以后申请权限，会继续弹出提示
                            showToast("请允许权限,否则APP不能正常运行");
                        } else {//只要有一个选择：禁止，但选择“以后不再询问”，以后申请权限，不会继续弹出提示
                            showToast("请到设置中打开权限");
                        }
                    }
                });
    }

    private void checkAdapter(){
        if(BluetoothAdapter.getDefaultAdapter()==null){
            return;
        }
        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i,BT_CODE);
        }else {
            autoRun();
        }
    }

    protected boolean isBluetoothAdapterOpened(){
        return BluetoothAdapter.getDefaultAdapter()!=null && BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    private static boolean isSupportBle(Context context){
        PackageManager packageManager=context.getPackageManager();
        return BluetoothAdapter.getDefaultAdapter()!=null && packageManager!=null && packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    protected void showToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void autoRun(){

    }

    protected Context getContext(){
        return context;
    }
    public abstract void onCreateView();
    public abstract void initWidget();

    public void createOTAFolder(){
        if(!CH9141OTAManager.getInstance().createFolder()){
            showToast("创建文件夹失败");
        }
    }
}
