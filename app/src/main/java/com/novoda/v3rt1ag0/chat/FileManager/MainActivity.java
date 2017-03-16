package com.novoda.v3rt1ag0.chat.FileManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.chat.Model.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v3rt1ag0 on 3/13/17.
 */

public class MainActivity extends AppCompatActivity
{
    List<Files> list;
    MyAdapter myAdapter;
    RecyclerView recyclerView;
    DatabaseReference database;
    String  user_id, channel_name, dbref;
    Toolbar toolbar;
    Uri filePath;
    private static int REQUEST_CODE = 42;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_filemanagement);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.inflateMenu(R.menu.filemanager_menu);
        toolbar.setOnMenuItemClickListener(menuItemClickListener);
        list = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.my_gamelist_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity.this.finish();
            }
        });
        user_id = getIntent().getStringExtra("uid");
        channel_name = getIntent().getStringExtra("channel");
        if (getIntent().hasExtra("directorypath"))
        {

            dbref = getIntent().getStringExtra("directorypath");
            Log.d("TAG",channel_name);
            database = FirebaseDatabase.getInstance().getReference(dbref);
            retriveallfiles();
        }

    }

    private void retriveallfiles()
    {
        database.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Log.d("TAG", "executed");
                    if(postSnapshot.getKey().equals("directory")||postSnapshot.getKey().equals("filename"))
                        continue;
                    Files file = postSnapshot.getValue(Files.class);
                    Log.d("TAG", file.getFilename());
                    list.add(new Files(file.getDirectory(), file.getDownloadpath(), file.getFilename()));
                }
                myAdapter = new MyAdapter(list,dbref,channel_name,user_id);
                recyclerView.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }

    private Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.file_upload:
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CODE);

                    return true;
                case R.id.new_folder:
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.edittextpopup);
                    final EditText text = (EditText) dialog.findViewById(R.id.edit_text);
                    Button button = (Button) dialog.findViewById(R.id.post_button);
                    button.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            createfolder(text.getText().toString());
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            filePath = data.getData();
            uploadFile();
        }
    }



    private  void createfolder(String foldername)
    {
        Files directory = new Files(true,foldername);
        database.child(foldername+"dir").setValue(directory);
    }
    private void uploadFile()
    {
        //if there is a file to upload
        if (filePath != null)
        {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            FirebaseStorage storageReference = FirebaseStorage.getInstance();
            Log.d("tag", channel_name + user_id+ getFileName(filePath));
            StorageReference riversRef = storageReference.getReference().child(channel_name).
                    child(user_id).   //username not working
                    child(getFileName(filePath));
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Log.d("path", filePath.toString());
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            reporttodatabase(taskSnapshot.getDownloadUrl().toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception exception)
                        {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else
        {
            //you can display an error toast
        }
    }


    private void reporttodatabase(String downloadurl)
    {
        String filename = getFileName(filePath);
        Files file = new Files(false, downloadurl, filename);
        database.push().setValue(file);
    }


    public String getFileName(Uri uri)
    {
        String result = null;
        if (uri.getScheme().equals("content"))
        {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try
            {
                if (cursor != null && cursor.moveToFirst())
                {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally
            {
                cursor.close();
            }
        }
        if (result == null)
        {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1)
            {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}
