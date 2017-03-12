package com.novoda.v3rt1ag0.chat.Model;

/**
 * Created by v3rt1ag0 on 3/12/17.
 */

public class TODO
{

    Long timestamp;
    String content;
    String editedby;

    TODO()
    {

    }

    public TODO(Long timestamp, String content, String editedby)
    {
        this.timestamp=timestamp;
        this.content=content;
        this.editedby=editedby;
    }

    public String getContent()
    {
        return content;
    }

    public Long getTimestamp()
    {
        return timestamp;
    }

    public String getEditedby()
    {
        return editedby;
    }
}
