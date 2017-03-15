package com.github.driesp.smartlockandroid;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class loginActivity extends AppCompatActivity {
    EditText usernameInput, passwordInput;
    CheckBox rememberMe;
    static loginActivity fa;

    public static final String PREFS_NAME = "passwordFile";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fa = this;
        setContentView(R.layout.activity_login);

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        rememberMe = (CheckBox) findViewById(R.id.rememberMe);

        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String username = pref.getString(PREF_EMAIL,null);
        String password = pref.getString(PREF_PASSWORD,null);

        usernameInput.setText(username);
        passwordInput.setText(password);

        if(username != null)
        {
            rememberMe.setChecked(true);
        }
    }

    public static loginActivity getInstance(){
        return   fa;
    }

    public void onLoginClick(View view)
    {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(rememberMe.isChecked())
        {
            getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                    .edit()
                    .putString(PREF_EMAIL, username)
                    .putString(PREF_PASSWORD, password)
                    .apply();
        }
        else
        {
            getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                    .edit()
                    .putString(PREF_EMAIL, "")
                    .putString(PREF_PASSWORD, "")
                    .apply();
        }


        String type = "login";

        BackgroundWorker backgroundworker = new BackgroundWorker(this);
        backgroundworker.execute(type, username, password);

    }

}
