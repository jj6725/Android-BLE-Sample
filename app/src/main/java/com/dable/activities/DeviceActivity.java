package com.dable.activities;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.dable.bluetooth.*;
import com.dable.glrender.*;
import com.dable.cubes.*;

import com.dable.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BleService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceActivity extends Activity {

    private final static String TAG = DeviceActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    public BluetoothGattCharacteristic characteristic;

    private TextView mConnectionState;
    private TextView mDataField;
    private TextView generate_report;

    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BleService mBleService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics
            = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattDescriptor descriptor;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private MyGLSurfaceView mGLView;

    private int counter =0;

    String [] li = new String[4];

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBleService = ((BleService.LocalBinder) service).getService();
            if (!mBleService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBleService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BleService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                Log.w(TAG, "CONNECTED");
                invalidateOptionsMenu();
            } else if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                Log.w(TAG, "DISCONNECTED");
                invalidateOptionsMenu();
                generate_report.append("DATA ANALYSIS");
                generate_report.append("\n");
                generate_report.append("Total data collected: ");
                generate_report.append(mBleService.broadcastTotalData());
                generate_report.append("\n");
                generate_report.append("Average angle: ");
                generate_report.append(mBleService.broadcastAngleAverage());
                generate_report.append("\n");
                generate_report.append("Median angle: ");
                generate_report.append(mBleService.broadcastAngleMedian());
                generate_report.append("\n");
                generate_report.append("Max angle value: ");
                generate_report.append(mBleService.broadcastMax());
                generate_report.append("\n");
                generate_report.append("Min angle value: ");
                generate_report.append(mBleService.broadcastMin());
                generate_report.append("\n");
                generate_report.append("\n");
                generate_report.append("\n");
                generate_report.append("DATA COLLECTED");
                generate_report.append("\n");
                generate_report.append("ByteChar[ ]");
                generate_report.append("                                            " +
                        "                       ");
                generate_report.append("Angles");
                generate_report.append("\n");
                generate_report.append(mBleService.broadcastReport());
                generate_report.setVisibility(View.VISIBLE);
                clearUI();
            } else if (BleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBleService.getSupportedGattServices());
            }

            //WHERE DATA IS BEING STREAMED
            else if (BleService.ACTION_DATA_AVAILABLE.equals(action)) {

                String data = intent.getStringExtra(BleService.EXTRA_DATA);
                displayData(intent.getStringExtra(BleService.EXTRA_DATA));

                li = data.split(":");

                float w = Float.parseFloat(li[0]);
                float x = Float.parseFloat(li[1]);
                float y = Float.parseFloat(li[2]);
                float z = Float.parseFloat(li[3]);
                System.out.println("@data:"+w+":"+x+":"+y+":"+z+"\n");
                Quant4d qua = new Quant4d(w, x, y, z);
                axisAngle ax = qua.transQToAx();
                ax.setAngle(Math.toDegrees(ax.getAngle()));

                float ang = (float) ax.getAngle();
                float mx = (float) ax.getX();
                float my = (float) ax.getY();
                float mz = (float) ax.getZ();

                if(counter==10){
                    mGLView.setx(mx);
                    mGLView.sety(my);
                    mGLView.setz(mz);
                    mGLView.setAngle(ang);

                    mGLView.getRender().setAngle(ang);
                    mGLView.getRender().setx(mx);
                    mGLView.getRender().sety(my);
                    mGLView.getRender().setz(mz);

                    mGLView.requestRender();
                    counter=0;
                }else{
                    counter++;
                }
                //Log.w(TAG, "WRITE DATA");
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        Log.w(TAG, String.valueOf(charaProp));
                        Log.w(TAG, String.valueOf(BluetoothGattCharacteristic.PROPERTY_NOTIFY));
                        if (((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0)) {
                            Log.w(TAG, "PROPERTY READ");
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBleService.setCharacteristicNotification(mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            Log.w(TAG, "NOTIFICATION CLEARED");
                            mBleService.readCharacteristic(characteristic);
                            Log.w(TAG, "CHARACTERISTIC READ");
                        }
                        if (((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)) {
                            Log.w(TAG, "PROPERTY NOTIFY");
                            mNotifyCharacteristic = characteristic;
                            mBleService.setCharacteristicNotification(characteristic, true);
                            if (mBleService.getDescriptor(characteristic) != null) {
                                Log.w(TAG, "DESCRIPTOR READ");
                            }
                        }
                        return true;
                    }
                    return false;
                }
            };

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);
        generate_report = (TextView) findViewById(R.id.generate_report);
        generate_report.setVisibility(View.INVISIBLE);

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BleService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


        mGLView = (MyGLSurfaceView)findViewById(R.id.surfaceviewclass);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView = (MyGLSurfaceView)findViewById(R.id.surfaceviewclass);
        mGLView.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBleService != null) {
            final boolean result = mBleService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView = (MyGLSurfaceView)findViewById(R.id.surfaceviewclass);
        mGLView.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBleService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBleService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBleService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(final String data) {
        mDataField.setText(data);

    }
    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, GattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, GattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this, gattServiceData, android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }



}
