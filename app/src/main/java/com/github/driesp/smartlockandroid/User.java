package com.github.driesp.smartlockandroid;

import java.io.Serializable;

/**
 * Created by Dries Peeters on 16/02/2017.
 */
public class User implements Serializable {

    private String Pname;
    private String Pemail;
    private int Pid;

    public User( int id,String name, String email)
    {
        Pid = id;
        Pname = name;
        Pemail = email;
    }

    public void setId(int id)
    {
        Pid = id;
    }

    public void setName(String name)
    {
        Pname = name;
    }

    public void setEmail(String email)
    {
        Pemail = email;
    }

    public int id()
    {
        return Pid;
    }

    public String name()
    {
        return Pname;
    }

    public String email()
    {
        return Pemail;
    }


}
