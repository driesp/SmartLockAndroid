package com.github.driesp.smartlockandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.renderscript.ScriptGroup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Dries Peeters on 15/02/2017.
 */
public class BackgroundWorker extends AsyncTask<String, Void, String> {

    Context context;
    AlertDialog alertDialog;
    BackgroundWorker(Context ctx)
    {
        context = ctx;
    }
    String currentType;

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];

        currentType = type;
        if(type.equals("login"))
        {
            String login_url ="https://smartlock.tk/android/login";
            try
            {
                String userN = params[1];
                String passW = params[2];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream output = httpURLConnection.getOutputStream();
                BufferedWriter bufW = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
                String post_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(userN,"UTF-8")+"&"+
                                    URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(passW,"UTF-8");

                bufW.write(post_data);
                bufW.flush();
                bufW.close();
                output.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufR = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

                String result = "";
                String line = "";

                while((line = bufR.readLine()) != null)
                {
                    result +=line;
                }

                bufR.close();
                inputStream.close();
                return result;

            }
            catch(MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        else if(type.equals("requestLocks"))
        {
            String request_url = context.getString(R.string.app_url) + "/android/data";
            try
            {
                String idN = params[1];
                URL url = new URL(request_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream output = httpURLConnection.getOutputStream();
                BufferedWriter bufW = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
                String post_data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(idN,"UTF-8");

                bufW.write(post_data);
                bufW.flush();
                bufW.close();
                output.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufR = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

                String result = "";
                String line = "";

                while((line = bufR.readLine()) != null)
                {
                    result +=line;
                }

                bufR.close();
                inputStream.close();
                return result;

            }
            catch(MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
       alertDialog =  new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {

        if(currentType.equals("login"))
        {
            if(result != null)
            {
                String[] parts = result.split(":");
                if(parts[0].equals("success"))
                {
                    User user = new User(Integer.parseInt(parts[1]),parts[2], parts[3]);
                    String message = "Welcome " + user.name();

                    Intent intent = new Intent(context , mainActivity.class);
                    intent.putExtra("User", user);
                    context.startActivity(intent);

                }
                else
                {
                    alertDialog.setMessage(parts[0]);
                    alertDialog.show();
                }
            }
            else
            {

                alertDialog.setMessage("Error Communication with server!");
                alertDialog.show();

            }
        }
        else if(currentType.equals("requestLocks"))
        {
            if(result != null)
            {
                Intent intent = new Intent();
                intent.putExtra("data", result);
                intent.setAction("com.github.driesp.dataSent");
                context.sendBroadcast(intent);
            }
            else
            {
                alertDialog.setMessage("Error Communicating with server!");
                alertDialog.show();
            }
        }



    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}

