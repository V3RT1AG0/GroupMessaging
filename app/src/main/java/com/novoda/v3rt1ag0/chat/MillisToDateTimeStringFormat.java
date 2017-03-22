package com.novoda.v3rt1ag0.chat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by v3rt1ag0 on 3/22/17.
 */

public class MillisToDateTimeStringFormat
{

    public static String formattedTimeFrom(String timestamp)
    {
        Date date = new Date();
        DateFormat timeFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        date.setTime(Long.parseLong(timestamp));
        return timeFormat.format(date);
    }

}
