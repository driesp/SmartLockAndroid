package com.github.driesp.smartlockandroid;

import android.media.Image;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Dries Peeters on 23/02/2017.
 */
public class customFragment extends Fragment{

    static private mainActivity ma;
    public static Fragment newInstance(mainActivity context,int position, String room,Boolean accessible, float scale) {
        Bundle b = new Bundle();
        b.putString("room", room);
        b.putInt("position", position);
        b.putBoolean("accessible", accessible);
        b.putFloat("scale", scale);
        ma = context;
        return Fragment.instantiate(context, customFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        LinearLayout l = (LinearLayout)
                inflater.inflate(R.layout.mf, container, false);

        String room = this.getArguments().getString("room");
        TextView tv = (TextView) l.findViewById(R.id.lockRoom);
        tv.setText(room);

        ImageButton ib = (ImageButton) l.findViewById(R.id.connectButton);
        ib.setTag(this.getArguments().getInt("position"));
        Boolean accessible = this.getArguments().getBoolean("accessible");
        if(accessible == true)
        {
            ib.setImageResource(R.drawable.lock_active);
            ib.setOnClickListener(new ImageButton.OnClickListener()
            {
                public void onClick(View v)
                {

                    final ImageButton btn = (ImageButton)v;
                    btn.setImageResource(R.drawable.lock_open);
                    int id = Integer.parseInt(v.getTag().toString());
                    ma.sendData(id);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn.setImageResource(R.drawable.lock_active);
                        }
                    }, 5000);
                }
            });
        }
        else
        {
            ib.setImageResource(R.drawable.lock_inactive);
            ib.setOnClickListener(new ImageButton.OnClickListener()
            {
                public void onClick(View v)
                {
                    Utils.toast(ma.getApplication(), "Lock Not Accessible");
                }
            });
        }

        customLinearLayout root = (customLinearLayout) l.findViewById(R.id.root);
        float scale = this.getArguments().getFloat("scale");
        root.setScaleBoth(scale);

        return l;
    }
}
