package com.github.driesp.smartlockandroid;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Dries Peeters on 7/03/2017.
 */
public class Utils {

    public static boolean checkBluetooth(BluetoothAdapter bluetoothAdapter)
    {
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
        {
            return false;
        }
        else
        {
            return true;
        }

    }
    public static void requestUserBluetooth(Activity activity)
    {
        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBTIntent, mainActivity.REQUEST_ENABLE_BT);
    }

    public static void toast(Context context, String string)
    {
        Toast toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0 , 0);
        toast.show();
    }

}
