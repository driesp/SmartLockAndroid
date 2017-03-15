package com.github.driesp.smartlockandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.UUID;

/**
 * Created by Dries Peeters on 7/03/2017.
 */
public class BLEScan{
    private final static String TAG = "SmartLock";
    private mainActivity PMA;
    private BluetoothAdapter PBTAdapter;
    private boolean PScanning;
    private Handler PHandler;

    private long PScanPeriod;
    private int PSignalStrength;

    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public BLEScan(mainActivity ma, long scanPeriod, int signalStrength )
    {
        this.PMA =  ma;
        this.PScanPeriod = scanPeriod;
        this.PSignalStrength = signalStrength;
        this.PHandler = new Handler();

        final BluetoothManager bluetoothmanager = (BluetoothManager) PMA.getSystemService(Context.BLUETOOTH_SERVICE);
        this.PBTAdapter = bluetoothmanager.getAdapter();

    }
    public boolean isScanning()
    {
        return PScanning;
    }

    public void start()
    {
        if(!Utils.checkBluetooth(PBTAdapter))
        {
            Utils.requestUserBluetooth(PMA);
            PMA.stopScan();
        }
        else
        {
            scanBLE(true);
        }
    }

    public void stop()
    {
        scanBLE(false);
    }

    public void scanBLE(final boolean enable)
    {
        if(enable && !PScanning)
        {
            Utils.toast(PMA.getApplication(), "Starting Scan!");
            PHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utils.toast(PMA.getApplication(), "Stopping Scan!");
                    PScanning = false;

                    PBTAdapter.stopLeScan(BLEScanCallback);

                    PMA.stopScan();
                }
            }, PScanPeriod);

            PScanning = true;
            PBTAdapter.startLeScan(BLEScanCallback);
        }
    }
    private BluetoothAdapter.LeScanCallback BLEScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi , byte[] scanRecord)
        {
            final int new_rssi = rssi;
            if(rssi > PSignalStrength)
            {
                PHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        PMA.addDevice(device, new_rssi);
                    }
                });
            }

        }
    };

    public boolean connect(final String address) {
        if (PBTAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        final BluetoothDevice device = PBTAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(PMA, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothGatt.discoverServices();
        mConnectionState = STATE_CONNECTING;
        return true;
    }
    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (PBTAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }
    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
        }
    };

    public boolean writeCharacteristic(byte[] data){
        boolean sent = false;
        boolean status = false;

            if (mBluetoothGatt == null) {
                Log.e(TAG, "lost connection");
                Utils.toast(PMA.getApplication(), "Connection Lost With Device");
                sent = false;
            }
            BluetoothGattService Service = mBluetoothGatt.getService(UUID.fromString("713d0000-503e-4c75-ba94-3148f18d941e"));
            if (Service == null) {
                Log.e(TAG, "service not found!");
                sent = false;
            }
            else
            {
                BluetoothGattCharacteristic charac = Service
                        .getCharacteristic(UUID.fromString("713d0003-503e-4c75-ba94-3148f18d941e"));
                if (charac == null) {
                    Log.e(TAG, "char not found!");
                    sent = false;
                }
                else
                {
                    sent = true;
                    charac.setValue(data);
                    status = mBluetoothGatt.writeCharacteristic(charac);
                }
            }

        return status;
    }

}
