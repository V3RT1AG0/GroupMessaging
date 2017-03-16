package com.novoda.v3rt1ag0.chat.FileManager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.novoda.v3rt1ag0.Manifest;
import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.chat.ChatActivity;
import com.novoda.v3rt1ag0.chat.Model.Files;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by v3rt1ag0 on 3/13/17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
{
    List<Files> info;
    Context context;
    String channelname, userid;
    String directorypath;

    MyAdapter(List<Files> info, String directorypath, String channelname, String userid)
    {
        this.info = info;
        this.directorypath = directorypath;
        this.channelname = channelname;
        this.userid = userid;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_card, parent, false);
        context = parent.getContext();
        MyViewHolder pvh = new MyViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        Files file = info.get(position);
        holder.filename.setText(file.getFilename());
        if (file.getDirectory())
            Glide.with(context).load("").placeholder(R.drawable.ic_arrow_back).into(holder.fileimage);
        else
            Glide.with(context).load("").placeholder(R.drawable.ic_person_add).into(holder.fileimage);

    }

    @Override
    public int getItemCount()
    {
        return info.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView filename;
        ImageView fileimage;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            fileimage = (ImageView) itemView.findViewById(R.id.file_image);
            filename = (TextView) itemView.findViewById(R.id.filename);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.card:
                    if (info.get(getAdapterPosition()).getDirectory())
                    {
                        view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class).putExtra("channel", channelname).putExtra("uid", userid).putExtra("directorypath", directorypath + "/" + info.get(getAdapterPosition()).getFilename() + "dir"));
                    } else
                    {
                        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        //StrictMode.setThreadPolicy(policy);
                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                                Log.d("TAG", fileExt(info.get(getAdapterPosition()).getDownloadpath()));
                                String mimeType = myMime.getMimeTypeFromExtension(fileExt(info.get(getAdapterPosition()).getDownloadpath()));
                                Log.d("TAG", mimeType + "");
                                //File myFile = new File(info.get(getAdapterPosition()).getDownloadpath());
                                File myFile = DownloadFile(info.get(getAdapterPosition()).getDownloadpath(), info.get(getAdapterPosition()).getFilename());
                                Log.d("TAG", myFile.getAbsolutePath());


                                newIntent.setDataAndType(Uri.fromFile(myFile), mimeType);


                                // Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",myFile);
                                //newIntent.setDataAndType(photoURI,mimeType);


                                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                try
                                {
                                    context.startActivity(newIntent);
                                } catch (ActivityNotFoundException e)
                                {
                                    Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                                }
                            }

                        }).start();

                        break;
                    }
            }
        }


        private String fileExt(String url)
        {
            if (url.indexOf("?") > -1)
            {
                url = url.substring(0, url.indexOf("?"));
            }
            if (url.lastIndexOf(".") == -1)
            {
                return null;
            } else
            {
                String ext = url.substring(url.lastIndexOf(".") + 1);
                if (ext.indexOf("%") > -1)
                {
                    ext = ext.substring(0, ext.indexOf("%"));
                }
                if (ext.indexOf("/") > -1)
                {
                    ext = ext.substring(0, ext.indexOf("/"));
                }
                return ext.toLowerCase();

            }
        }


    }


    public File DownloadFile(String fileURL, String filename)
    {
        File file = null;
        try
        {

            String extStorageDirectory = Environment.getExternalStorageDirectory()
                    .toString();
            File folder = new File(extStorageDirectory, "demo");
            folder.mkdir();
            file = new File(folder, filename);
            URL url = new URL(fileURL);
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            OutputStream output = new FileOutputStream(file);

            byte data[] = new byte[1024];

            long total = 0;
            int count=0;

            while ((count = input.read(data)) != -1) {
                Log.d("count", String.valueOf(total));
                total += count;
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return file;
    }


}

