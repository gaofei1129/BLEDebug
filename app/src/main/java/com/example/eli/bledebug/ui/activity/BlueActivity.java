package com.example.eli.bledebug.ui.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eli.bledebug.R;
import com.example.eli.bledebug.adapter.BlueListAdapter;
import com.example.eli.bledebug.ui.base.BaseActivity;
import com.example.eli.bledebug.ui.weiget.RefreshableView;
import com.example.eli.bledebug.utils.Constans;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Eli on 2017/2/14.
 */

public class BlueActivity extends BaseActivity {
    public static final String TAG = "BlueActivity";
    BlueListAdapter blueAdapter;
    List<BluetoothDevice> lstDevices = new ArrayList<BluetoothDevice>();

    private long mExitTime;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // stop scan after 10 second
    private static final long SCAN_PERIOD = 2000;

    @BindView(R.id.list_bluetooh)
    ListView lvBTDevices;
    @BindView(R.id.refreshable_view)
    RefreshableView refreshableView;

    @Override
    protected int initLayoutId() {
        return R.layout.activity_enter;
    }

    @Override
    protected void initView() {
        //Toast.makeText(this, "下拉刷新!", Toast.LENGTH_SHORT).show();
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    protected void initData() {
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_ENABLE_BT);
                this.finish();
            }
        }

        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            this.finish();
        }
        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            this.finish();
            //return;
        }

        mHandler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        initBlueTooth();
        scanLeDevice(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ENABLE_BT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "yes==");
                } else {
                    Log.e(TAG, "no===");
                }
            }
        }
    }

    private void initBlueTooth() {

        lvBTDevices = (ListView) findViewById(R.id.list_bluetooh);
        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        blueAdapter = new BlueListAdapter(this, lstDevices);
        lvBTDevices.setAdapter(blueAdapter);

        lvBTDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice device = blueAdapter.getDevice(position);
                if (device == null) return;

                final Intent intent = new Intent(BlueActivity.this, MainActivity.class);
                intent.putExtra(Constans.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(Constans.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                //Log.e(TAG, "name--" + device.getName() + "---address--" + device.getAddress());
                if (mScanning) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                startActivity(intent);
            }
        });

        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
                refreshableView.finishRefreshing();
            }
        }, 1);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //刷新
            scanLeDevice(true);
            return false;
        }
    });

    private void scanLeDevice(final boolean enable) {
        lstDevices.clear();
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            Log.e(TAG, "----scanLeDevice");
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            BlueActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //String struuid = bytes2HexString(reverseBytes(scanRecord)).replace("-", "").toLowerCase();
                    if (!lstDevices.contains(device)) {
                        lstDevices.add(device);
                    }
                    blueAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();

            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
