package com.example.eli.bledebug.ui.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eli.bledebug.R;
import com.example.eli.bledebug.adapter.ExpandableListViewaAdapter;
import com.example.eli.bledebug.adapter.GridInputAdapter;
import com.example.eli.bledebug.adapter.InputHistoryAdapter;
import com.example.eli.bledebug.presenter.MainPresenter;
import com.example.eli.bledebug.server.BluetoothLeService;
import com.example.eli.bledebug.ui.base.BaseMvpActivity;
import com.example.eli.bledebug.ui.base.BasePresenter;
import com.example.eli.bledebug.utils.BLEUtils;
import com.example.eli.bledebug.utils.Constans;
import com.example.eli.bledebug.utils.FileUtils;
import com.example.eli.bledebug.utils.SampleGattAttributes;
import com.example.eli.bledebug.utils.StringUtils;
import com.example.eli.bledebug.utils.Tools;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseMvpActivity implements InputHistoryAdapter.Callback {
    public static final String TAG = "MainActivity";
    private static final String FILENAME = "/data.txt";
    @BindView(R.id.grid_input)
    GridView gridInput;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    public static final int READ = 1;
    public static final int WRITE = 2;
    @BindView(R.id.txt_blue_name)
    TextView txtBlueName;
    @BindView(R.id.txt_blue_connect)
    TextView txtBlueConnect;
    @BindView(R.id.txt_output)
    TextView txtOutput;
    @BindView(R.id.txt_write)
    TextView txtWrite;
    @BindView(R.id.txt_read)
    TextView txtRead;
    @BindView(R.id.txt_more)
    ImageView txtMore;
    @BindView(R.id.edit_input)
    EditText editInput;

    ExpandableListView expandableListView;
    @BindView(R.id.sv_show)
    ScrollView svShow;
    private ExpandableListViewaAdapter expandableListViewaAdapter;

    ListView listHistory;
    private InputHistoryAdapter inputHistoryAdapter;

    List<String> listInputHistory;
    List<String> listGridInput;
    BluetoothGattCharacteristic characteristicWrite;
    BluetoothGattCharacteristic characteristicRead;
    private String mDeviceName = "";
    private String mDeviceAddress;

    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private static final int AUTO_CONNECT_PERIOD = 10;
    private Handler handler = new Handler();

    ArrayList<HashMap<String, String>> gattServiceData;
    ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!mConnected) {
                mBluetoothLeService.connect(mDeviceAddress);
            }
            handler.postDelayed(this, AUTO_CONNECT_PERIOD);
        }
    };

    @Override
    protected int initLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * 获取EditText光标所在的位置
     */
    private int getEditTextCursorIndex() {
        return editInput.getSelectionStart();
    }

    /**
     * 向EditText指定光标位置插入字符串
     */
    private void insertText(String mText) {
        editInput.getText().insert(getEditTextCursorIndex(), mText);
    }

    /**
     * 向EditText指定光标位置删除字符串
     */
    private void deleteText() {
        if (!StringUtils.isEmpty(editInput.getText().toString()) && getEditTextCursorIndex() > 0) {
            editInput.getText().delete(getEditTextCursorIndex() - 1, getEditTextCursorIndex());
        }
    }

    // 隐藏系统键盘
    public void hideSoftInputMethod(EditText ed) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        int currentVersion = Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            ed.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(ed, false);
            } catch (NoSuchMethodException e) {
                ed.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void initView() {
        //Toolbar上显示menu菜单
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        //增加返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //结果显示textview自己滚动
        //scroll2Bottom(svShow, txtOutput);
        //隐藏系统键盘
        hideSoftInputMethod(editInput);
        txtBlueName.setText(mDeviceName.trim() + "");
        txtOutput.setMovementMethod(ScrollingMovementMethod.getInstance());
        gridInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        insertText("1");
                        break;
                    case 1:
                        insertText("2");
                        break;
                    case 2:
                        insertText("3");
                        break;
                    case 3:
                        insertText("4");
                        break;
                    case 4:
                        insertText("5");
                        break;
                    case 5:
                        insertText("6");
                        break;
                    case 6:
                        insertText("7");
                        break;
                    case 7:
                        insertText("8");
                        break;
                    case 8:
                        insertText("9");
                        break;
                    case 9:
                        insertText("0");
                        break;
                    case 10:
                        insertText("A");
                        break;
                    case 11:
                        deleteText();
                        break;
                    case 12:
                        insertText("B");
                        break;
                    case 13:
                        insertText("C");
                        break;
                    case 14:
                        insertText("D");
                        break;
                    case 15:
                        insertText("E");
                        break;
                    case 16:
                        insertText("F");
                        break;
                    case 17:
                        send();
                        break;
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);

            BluetoothGattCharacteristic characteristic =
                    mBluetoothLeService.getSpecialGattCharacteristic(
                            SampleGattAttributes.SIMPLE_PROFILE_SERVICE,
                            SampleGattAttributes.CUSTOM_NOTIFICATION);

            if (characteristic != null) {
                mBluetoothLeService.setCharacteristicNotification(
                        characteristic, true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_read:
                creatDialog(READ);
                break;
            case R.id.menu_write:
                creatDialog(WRITE);
                break;
            case R.id.menu_about:
                toAbout();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        this.startActivity(intent);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(Constans.EXTRAS_DEVICE_ADDRESS);
        mDeviceName = intent.getStringExtra(Constans.EXTRAS_DEVICE_NAME);

        listInputHistory = new ArrayList<String>();
        List<String> history = FileUtils.readFileToList(Environment.getExternalStorageDirectory() + FILENAME, "123");
        if (history != null) {
            listInputHistory.addAll(history);
        }

        listGridInput = new ArrayList<String>();
        listGridInput.add("1");
        listGridInput.add("2");
        listGridInput.add("3");
        listGridInput.add("4");
        listGridInput.add("5");
        listGridInput.add("6");
        listGridInput.add("7");
        listGridInput.add("8");
        listGridInput.add("9");
        listGridInput.add("0");
        listGridInput.add("A");
        listGridInput.add("删除");
        listGridInput.add("B");
        listGridInput.add("C");
        listGridInput.add("D");
        listGridInput.add("E");
        listGridInput.add("F");
        listGridInput.add("发送");
        GridInputAdapter gridInputAdapter = new GridInputAdapter(this, listGridInput);
        gridInput.setAdapter(gridInputAdapter);

        //GridView组件去除四周的空隙
        //gridInput.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        //gridInput.setSelector(new ColorDrawable(Color.TRANSPARENT));
        bindService();
    }

    public  void scroll2Bottom(final ScrollView scroll, final View inner) {
        Handler handler = new Handler();
        handler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (scroll == null || inner == null) {
                    return;
                }
                // 内层高度超过外层
                int offset = inner.getMeasuredHeight()
                        - scroll.getMeasuredHeight();
                if (offset < 0) {
                    System.out.println("定位...");
                    offset = 0;
                }else{
                    fab.setVisibility(View.VISIBLE);
                }
                scroll.scrollTo(0, offset);
            }
        });
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.e(TAG, "onServiceConnected---");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.e(TAG, "ACTION_GATT_CONNECTED----");
                mConnected = true;
                invalidateOptionsMenu();
                handler.removeCallbacks(runnable);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

                mConnected = false;

                Log.e(TAG, "State : " + " DisConnected");
                txtBlueConnect.setText("状态 : " + " 已断开");
                Toast.makeText(MainActivity.this, mDeviceName.trim() + "" +
                        " : 已断开", Toast.LENGTH_SHORT).show();
                handler.postDelayed(runnable, AUTO_CONNECT_PERIOD);

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                Log.e(TAG, "ACTION_GATT_SERVICES_DISCOVERED----");
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                txtBlueConnect.setText("状态 : " + " 已连接");
                Toast.makeText(MainActivity.this, mDeviceName.trim() + "" + " : 已连接", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.e(TAG, "ACTION_DATA_AVAILABLE----");
                displayData(intent);
            } else if (BluetoothLeService.ACTION_GATT_NOTIFICATION.equals(action)) {
                Log.e(TAG, "ACTION_GATT_NOTIFICATION----");
//                dataDecode(intent);
                displayData(intent);
            }
        }
    };

    private void displayData(Intent intent) {
        byte[] src = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
        Log.e(TAG, "src--data-" + Tools.bytes2HexString(src));
        txtOutput.setText(txtOutput.getText().toString() + "\n" + Tools.bytes2HexString(src));
        scroll2Bottom(svShow, txtOutput);
    }

    private void SendValToBluetooth(byte[] val) {
        Log.e("---send---", Tools.bytes2HexString(val) + "");
        if (characteristicRead == null) {
            characteristicRead =
                    mBluetoothLeService.getSpecialGattCharacteristic(
                            SampleGattAttributes.SIMPLE_PROFILE_SERVICE,
                            SampleGattAttributes.CUSTOM_DATA_TRANSFER);

            if (characteristicRead != null) {
                characteristicRead.setValue(val);

                mBluetoothLeService.writeCharacteristic(characteristicRead);
            } else {
                Toast.makeText(MainActivity.this, "写服务为空,请设置!", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void displayGattServices() {
        if (characteristicWrite == null) {
            characteristicWrite =
                    mBluetoothLeService.getSpecialGattCharacteristic(
                            SampleGattAttributes.SIMPLE_PROFILE_SERVICE,
                            SampleGattAttributes.CUSTOM_NOTIFICATION);
        }

        if (characteristicWrite != null) {
            mBluetoothLeService.setCharacteristicNotification(
                    characteristicWrite, true);
            //mBluetoothLeService.readCharacteristic();
        } else {
            Toast.makeText(MainActivity.this, "未连接到服务!", Toast.LENGTH_SHORT).show();
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_NOTIFICATION);
        return intentFilter;
    }

    private void bindService() {
        Intent gattServiceIntent = new Intent(MainActivity.this, BluetoothLeService.class);
        boolean bind = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        Log.e(TAG, "bind---" + bind);
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().
                getString(R.string.unknown_service);
        String unknownCharaString = getResources().
                getString(R.string.unknown_characteristic);
        gattServiceData =
                new ArrayList<HashMap<String, String>>();
        gattCharacteristicData =
                new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics =
                new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            HashMap<String, String> currentServiceData =
                    new HashMap<String, String>();
            uuid = gattService.getUuid().toString();

            Log.e(TAG, "----" + uuid);
            Log.e(TAG, "----" + SampleGattAttributes.
                    lookup(uuid, unknownServiceString));
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.
                            lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            //Log.e(TAG, "gattService.getIncludedServices().size();------------" +gattService.getIncludedServices().size());
            Log.e(TAG, "gattCharacteristics.size()------------" + gattCharacteristics.size());
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic :
                    gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData =
                        new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                //int permission = gattCharacteristic.getPermissions();
                int properties = gattCharacteristic.getProperties();
                String permission = BLEUtils.getCharPropertie(properties);
                Log.e(TAG, "---->char permission-------" + properties + "------------" + BLEUtils.getCharPropertie(properties));
                Log.e(TAG, "------------" + uuid);
                Log.e(TAG, "------------" + SampleGattAttributes.lookup(uuid,
                        unknownCharaString));
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid,
                                unknownCharaString));

                String newMessageInfo = "<small><font color='#FF0000'>" + permission
                        + "</small></font>";
                currentCharaData.put(LIST_UUID, uuid + "    " + newMessageInfo);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
        displayGattServices();
        expandableListViewaAdapter = new ExpandableListViewaAdapter(this, gattServiceData, gattCharacteristicData);
        //gattServiceData  所有的service uuid和名字
        //gattCharacteristicData 所有的Characteristic uuid和名字
        //mGattCharacteristics  所有的Characteristic
    }

    public void creatDialog(final int index) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog,
                (ViewGroup) findViewById(R.id.dialog));
        expandableListView = (ExpandableListView) layout.findViewById(R.id.expand_service);
        if (expandableListViewaAdapter != null) {
            expandableListView.setAdapter(expandableListViewaAdapter);
        }
        final AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择服务").setView(layout)
                .show();
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (index == READ) {
                    characteristicRead =
                            mGattCharacteristics.get(groupPosition).get(childPosition);
                } else if (index == WRITE) {
                    characteristicWrite =
                            mGattCharacteristics.get(groupPosition).get(childPosition);
                }
                displayGattServices();
                dialog.dismiss();
                return true;
            }
        });

    }

    public void creatHistoryDialog() {
        if (listInputHistory == null || listInputHistory.size() == 0) {
            Toast.makeText(this, "没有输入记录!", Toast.LENGTH_SHORT).show();
            return;
        }
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_input_history,
                (ViewGroup) findViewById(R.id.dialog_history));
        listHistory = (ListView) layout.findViewById(R.id.list_history);
        inputHistoryAdapter = new InputHistoryAdapter(this, listInputHistory, this);
        listHistory.setAdapter(inputHistoryAdapter);

        final AlertDialog dialog = new AlertDialog.Builder(this).setTitle("历史输入记录").setView(layout)
                .show();
        listHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editInput.setText(listInputHistory.get(position));
                dialog.dismiss();
            }
        });

    }


    @OnClick({R.id.txt_write, R.id.txt_read, R.id.txt_more, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_write:
                creatDialog(WRITE);
                break;
            case R.id.txt_read:
                creatDialog(READ);
                break;
            case R.id.txt_more:
                creatHistoryDialog();
                break;
            case R.id.fab:
                txtOutput.setText("");
                break;
        }
    }

    private void send() {
        try {
            String strInput = editInput.getText().toString().replace(" ", "");
            SendValToBluetooth(Tools.hexStringToByteArray(strInput));
            if (listInputHistory != null) {
                if (!listInputHistory.contains(strInput) && strInput.length() > 0) {
                    listInputHistory.add(strInput);
                }
                if (inputHistoryAdapter != null) {
                    inputHistoryAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "输入有误！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void fetchData() {

    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        BluetoothGattCharacteristic characteristic =
                mBluetoothLeService.getSpecialGattCharacteristic(
                        SampleGattAttributes.SIMPLE_PROFILE_SERVICE,
                        SampleGattAttributes.CUSTOM_NOTIFICATION);

        if (characteristic != null) {
            mBluetoothLeService.setCharacteristicNotification(
                    characteristic, false);
        }

        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        FileUtils.writeFile(Environment.getExternalStorageDirectory() + FILENAME, listInputHistory);
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    //历史删除点击回调
    @Override
    public void click(View v, int position) {
        listInputHistory.remove(position);
        inputHistoryAdapter.notifyDataSetChanged();
    }

}
