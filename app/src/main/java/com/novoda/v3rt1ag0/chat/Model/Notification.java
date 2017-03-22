package com.novoda.v3rt1ag0.chat.Model;

/**
 * Created by v3rt1ag0 on 3/16/17.
 */

public class Notification
{

    String content;
    String timestamp;
    String name;
    int hour,minute,year,month,day;

    public Notification()
    {

    }

    public Notification(String content,int hour,int minute,int day,int month,int year,String timestamp,String name)
    {
        this.content=content;
        this.hour=hour;
        this.minute=minute;
        this.year=year;
        this.month=month;
        this.day=day;
        this.timestamp=timestamp;
        this.name=name;
    }

    public int getHour()
    {
        return hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public String getContent()
    {
        return content;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public String getName()
    {
        return name;
    }

    public void setDay(int day)
    {
        this.day = day;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public void setMonth(int month)
    {
        this.month = month;
    }
}
