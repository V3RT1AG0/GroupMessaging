package com.novoda.v3rt1ag0.chat.Model;

/**
 * Created by v3rt1ag0 on 3/22/17.
 */

public class StarredMessage
{
    String message;
    String timeinmillis;
    String nameofuser;

    StarredMessage()
    {

    }

    public StarredMessage(String message, String timeinmillis, String nameofuser)
    {
        this.message = message;
        this.timeinmillis = timeinmillis;
        this.nameofuser = nameofuser;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setTimeinmillis(String timeinmillis)
    {
        this.timeinmillis = timeinmillis;
    }

    public void setNameofuser(String nameofuser)
    {
        this.nameofuser = nameofuser;
    }

    public String getMessage()
    {
        return message;
    }

    public String getTimeinmillis()
    {
        return timeinmillis;
    }

    public String getNameofuser()
    {
        return nameofuser;
    }
}
