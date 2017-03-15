package com.github.driesp.smartlockandroid;

/**
 * Created by Dries Peeters on 16/02/2017.
 */
public class Device {

    private String Pname;
    private String Paddress;
    private int Prssi;
    private Boolean Pconnected;


    public Device(String name, String address, Boolean connected, int rssi)
    {
        this.Pname = name;
        this.Paddress = address;
        this.Pconnected = connected;
        this.Prssi = rssi;
    }

    public void setName(String name)
    {
        this.Pname = name;
    }
    public void setAddress(String address)
    {
        this.Paddress = address;
    }
    public void setConnected(Boolean connected)
    {
        this.Pconnected = connected;
    }
    public void setRssi(int rssi)
    {
        this.Prssi = rssi;
    }

    public String name()
    {
        return this.Pname;
    }
    public String address()
    {
        return this.Paddress;
    }
    public Boolean connected()
    {
        return this.Pconnected;
    }
    public int rssi()
    {
        return this.Prssi;
    }

}
