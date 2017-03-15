package com.github.driesp.smartlockandroid;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.Vector;

public class mainActivity extends AppCompatActivity {


    public static final int REQUEST_ENABLE_BT = 1 ;
    private BLEScan BLEScanner;

    AlertDialog alertDialog;
    Vector<Lock> locks = new Vector<Lock>(1);
    public static int REQUEST_BLUETOOTH = 1;

    BluetoothAdapter BTAdapter;
    Vector<Device> devices = new Vector<Device>(1);


    public int PAGES = 0;
    public int LOOPS = 1;

    public customPagerAdapter adapter;
    public ViewPager pager;

    private BroadcastReceiver gcmReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // Update view
                    String data = intent.getStringExtra("data");
                    readData(data);

                }
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loginActivity.getInstance().finish();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BLEScanner = new BLEScan(this, 1000, -75);
        startScan();

        alertDialog =  new AlertDialog.Builder(this).create();
        alertDialog.setTitle("BT Status");


        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("User");
        String type = "requestLocks";
        BackgroundWorker backgroundworker = new BackgroundWorker(this);
        backgroundworker.execute(type, String.valueOf(user.id()));


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateLocks();
            }
        },1500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                displayLocks();
            }
        },2000);

    }

    public void onResume() {
        super.onResume();
        registerReceiver(gcmReceiver,
                new IntentFilter("com.github.driesp.dataSent"));
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(gcmReceiver);
    }

    public void readData(String data)
    {
        String[] listSplits = data.split("#");
        for(String split : listSplits )
        {

            String temp = split.substring(1, split.length()-1);
            String[] filler = temp.split(";");

            Lock lock = new Lock(filler[0], filler[1], filler[2], false);
            locks.add(lock);
        }
    }

    private void displayLocks()
    {
        pager = (ViewPager) findViewById(R.id.myviewpager);
        pager.removeAllViews();
        PAGES = locks.size();
        Log.d("Counts", String.valueOf(PAGES));
        adapter = new customPagerAdapter(this, this.getSupportFragmentManager(), 1 , PAGES, LOOPS, locks);
        pager.setAdapter(adapter);
        pager.setPageTransformer(false, adapter);
        pager.setCurrentItem(0);
        pager.setOffscreenPageLimit(3);
        pager.setPageMargin(-200);

    }

    public void updateLocks()
    {
        for(Device device: devices)
        {
            for(Lock lock: locks )
            {
                if(lock.address().equals(device.address()))
                {
                    lock.setAccessible(true);
                }
            }
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void startScan()
    {
        devices.clear();
        BLEScanner.start();
    }

    public void stopScan() {
        BLEScanner.stop();
    }

    public void addDevice(BluetoothDevice device, int new_rssi)
    {
        boolean found = true;
        Device tempDevice = new Device(device.getName(), device.getAddress(), true, new_rssi);
        found = false;
        Log.w("Device", tempDevice.address());
        devices.add(tempDevice);
    }

    public void sendData(final int position)
    {

        BLEScanner.connect(locks.elementAt(position).address());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                byte[] b = "0123456789ABCDE".getBytes(Charset.forName("UTF-8"));
                BLEScanner.writeCharacteristic(b);
                closeConnection();
            }
        },1250);




    }
    public void closeConnection()
    {
        BLEScanner.stop();
        BLEScanner.close();
    }
}

