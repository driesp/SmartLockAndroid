package com.github.driesp.smartlockandroid;

/**
 * Created by Dries Peeters on 16/02/2017.
 */
public class Lock {

    private String Proom;
    private String Ppassword;
    private String Paddress;
    private Boolean Paccessible;

    public Lock(String room, String password, String address, Boolean accessible)
    {
        this.Proom = room;
        this.Ppassword = password;
        this.Paddress = address;
        this.Paccessible = accessible;
    }

    public void setRoom(String room)
    {
        this.Proom = room;
    }
    public void setPassword(String password)
    {
        this.Ppassword = password;
    }
    public void setAddress(String address)
    {
        this.Paddress = address;
    }

    public void setAccessible(Boolean accessible)
    {
        this.Paccessible = accessible;
    }

    public String room()
    {
        return this.Proom;
    }

    public String password()
    {
        return this.Ppassword;
    }

    public String address()
    {
        return this.Paddress;
    }

    public Boolean accessible()
    {
        return this.Paccessible;
    }
}
