package com.novoda.v3rt1ag0.chat.Model;

/**
 * Created by v3rt1ag0 on 3/12/17.
 */

public class TODO
{

    String timestamp;
    Boolean checked;
    String content;
    String editedby;
    String key;

    TODO()
    {

    }

    public TODO(String timestamp, String content, String editedby,Boolean checked,String key)
    {
        this.timestamp=timestamp;
        this.content=content;
        this.editedby=editedby;
        this.checked=checked;
        this.key=key;
    }

    public String getContent()
    {
        return content;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public String getEditedby()
    {
        return editedby;
    }

    public Boolean getChecked()
    {
        return checked;
    }

    public String getKey()
    {
        return key;
    }
}
