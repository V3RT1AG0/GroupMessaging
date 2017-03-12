package com.novoda.v3rt1ag0.chat.Model;

/**
 * Created by v3rt1ag0 on 3/12/17.
 */

public class AdminMode
{
    public String channelname;
    public int adminmode;
    public Object owner;

    public AdminMode()
    {
    }

    public AdminMode(String name, int adminmode,Object owner)
    {
        this.channelname = name;
        this.adminmode = adminmode;
        this.owner = owner;
    }

    public int getAdminmode()
    {
        return adminmode;
    }

    public void setAdminmode(int adminmode)
    {
        this.adminmode = adminmode;
    }

    public void setChannelname(String channelname)
    {
        this.channelname = channelname;
    }

    public void setOwner(Object owner)
    {
        this.owner = owner;
    }
}

