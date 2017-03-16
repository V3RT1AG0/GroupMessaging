package com.novoda.v3rt1ag0.chat.Model;

/**
 * Created by v3rt1ag0 on 3/13/17.
 */

public class Files
{
    Boolean directory;
    String downloadpath;
    String filename;

    public Files()
    {

    }

    public Files(Boolean directory, String downloadpath, String filename)
    {
        this.directory = directory;
        this.downloadpath = downloadpath;
        this.filename = filename;
    }

    public Files(Boolean directory, String filename)
    {
        this.directory = directory;
        this.filename = filename;
    }

    public Boolean getDirectory()
    {
        return directory;
    }

    public String getDownloadpath()
    {
        return downloadpath;
    }

    public String getFilename()
    {
        return filename;
    }
}
