package com.novoda.v3rt1ag0.chat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
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
import com.novoda.v3rt1ag0.BaseActivity;
import com.novoda.v3rt1ag0.Dependencies;
import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.channel.data.model.Channel;
import com.novoda.v3rt1ag0.chat.FileManager.MainActivity;
import com.novoda.v3rt1ag0.chat.Model.AdminMode;
import com.novoda.v3rt1ag0.chat.Model.Files;
import com.novoda.v3rt1ag0.chat.Model.Note;
import com.novoda.v3rt1ag0.chat.Model.TODO;
import com.novoda.v3rt1ag0.chat.displayer.ChatDisplayer;
import com.novoda.v3rt1ag0.chat.presenter.ChatPresenter;
import com.novoda.v3rt1ag0.navigation.AndroidNavigator;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ChatActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener
{

    private static final String NAME_EXTRA = "channel_name";
    private static final String ACCESS_EXTRA = "channel_access";
    private static final int REQUEST_CODE = 22;
    ;
    String channelname;
    Switch admin_switch;
    ListView starredmessagelist, noteslstview, todolistview;
    ImageView todoimage, notesimage, show_hide_toggle, cancel_button;
    DatabaseReference database;
    AdminMode adminMode;
    private ChatPresenter presenter;
    LinearLayout messagelayout, popup_linear_layout;
    public static String userid;
    private String username;
    private Uri filePath;

    public static Intent createIntentFor(Context context, Channel channel)
    {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(NAME_EXTRA, channel.getName());
        intent.putExtra(ACCESS_EXTRA, channel.getAccess().name());
        return intent;
    }


    public void adminmode(int enabled)
    {
        adminMode.setAdminmode(enabled);
        adminMode.setChannelname(channelname);
        database.child("adminCheck").child(channelname).setValue(adminMode);
        database.child("owners").child(channelname).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                adminMode.setOwner(dataSnapshot.getValue());
                database.child("adminCheck").child(channelname).setValue(adminMode);
                //  database.child("adminCheck").push().setValue(adminMode);
            }

            @Override
            public void onCancelled(DatabaseError error)
            {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        database = FirebaseDatabase.getInstance().getReference();
        adminMode = new AdminMode();
        channelname = getIntent().getStringExtra(NAME_EXTRA);
        admin_switch = (Switch) findViewById(R.id.admin_switch);
        messagelayout = (LinearLayout) findViewById(R.id.message_layout);
        starredmessagelist = (ListView) findViewById(R.id.starredRecycler);
        noteslstview = (ListView) findViewById(R.id.noteslistview);
        todolistview = (ListView) findViewById(R.id.todolistview);
        todoimage = (ImageView) findViewById(R.id.TODOImage);
        notesimage = (ImageView) findViewById(R.id.NotesImage);
        popup_linear_layout = (LinearLayout) findViewById(R.id.popup_linearlayout);
        show_hide_toggle = (ImageView) findViewById(R.id.show_hide_image);
        cancel_button = (ImageView) findViewById(R.id.cancel_button);
        ImageView file_chooser = (ImageView) findViewById(R.id.uploadfile);
        ImageView imageView = (ImageView) findViewById(R.id.open_all_files);


        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {


            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);


        }


        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(ChatActivity.this, MainActivity.class).putExtra("channel", channelname).putExtra("uid", userid).putExtra("directorypath", "FileStorage/" + channelname));//.putExtra("directorypath","FileStorage/"+channelname+"/Media"
            }
        });


        file_chooser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("username", username + userid + "hello");
                Intent intent = new Intent();
                intent.setType("*/*");
                // intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                Log.d("username", username + userid + "hello2");
                startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CODE);
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Animation bottom_down = AnimationUtils.loadAnimation(v.getContext(),
                        R.anim.bottom_down);
                popup_linear_layout.startAnimation(bottom_down);
                popup_linear_layout.setVisibility(View.GONE);
            }
        });

        show_hide_toggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Animation bottomUp = AnimationUtils.loadAnimation(v.getContext(),
                        R.anim.bottom_up);
                popup_linear_layout.startAnimation(bottomUp);
                popup_linear_layout.setVisibility(View.VISIBLE);
            }
        });

        todoimage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.edittextpopup);
                final EditText text = (EditText) dialog.findViewById(R.id.edit_text);
                Button button = (Button) dialog.findViewById(R.id.post_button);
                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        TODO todo = new TODO(System.currentTimeMillis(), text.getText().toString(), username);
                        database.child("TODO").child(channelname).push().setValue(todo);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        notesimage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.edittextpopup);
                final EditText text = (EditText) dialog.findViewById(R.id.edit_text);
                Button button = (Button) dialog.findViewById(R.id.post_button);
                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Note note = new Note(System.currentTimeMillis(), text.getText().toString(), username);
                        database.child("Notes").child(channelname).push().setValue(note);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        admin_switch.setOnCheckedChangeListener(this);
        getUsername();
        checkAdminEnabled();
        setListViewForStarred();
        setListViewForNotes();
        setListViewForTODO();


        // Log.d("customlog", userid);
        ChatDisplayer chatDisplayer = (ChatDisplayer) findViewById(R.id.chat_view);
        Channel channel = new Channel(channelname,
                Channel.Access.valueOf(getIntent().getStringExtra(ACCESS_EXTRA)));
        presenter = new ChatPresenter(
                Dependencies.INSTANCE.getLoginService(),
                Dependencies.INSTANCE.getChatService(),
                chatDisplayer,
                channel,
                Dependencies.INSTANCE.getAnalytics(),
                new AndroidNavigator(this),
                Dependencies.INSTANCE.getErrorLogger()
        );
    }

    private void getUsername()
    {
        database.child("users").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    if (postSnapshot.getKey().equals(userid))
                    {
                        username = postSnapshot.child("name").getValue().toString();
                        Log.d("username", username);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            filePath = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        if (requestCode == REQUEST_CODE)
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

            }
        return;
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
            Log.d("tag", channelname + username + getFileName(filePath));
            StorageReference riversRef = storageReference.getReference().child(channelname).
                    child(userid).   //username not working
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

    private void reporttodatabase(final String downloadurl)
    {

        final String filename = getFileName(filePath);
        database.child("FileStorage").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild(channelname))
                {
                    for (DataSnapshot childSnapshot : dataSnapshot.child(channelname).getChildren())
                    {
                        if (childSnapshot.getKey().equals("Mediadir"))
                        {
                            Files file = new Files(false, downloadurl, filename);
                            database.child("FileStorage").child(channelname).child("Mediadir").push().setValue(file);
                        } else
                        {
                            Files directory = new Files(true, "Media");
                            database.child("FileStorage").child(channelname).child("Mediadir").setValue(directory);
                            Files file = new Files(false, downloadurl, filename);
                            database.child("FileStorage").child(channelname).child("Mediadir").push().setValue(file);
                        }
                    }

                } else
                {
                    Files directory = new Files(true, "Media");
                    database.child("FileStorage").child(channelname).child("Mediadir").setValue(directory);
                    Files file = new Files(false, downloadurl, filename);
                    database.child("FileStorage").child(channelname).child("Mediadir").push().setValue(file);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }

        });

/*
        Files directory = new Files(true, "Media");
        database.child("FileStorage").child(channelname).child("Media").setValue(directory);
        Files file = new Files(false, downloadurl, filename);
        String ref = database.child("FileStorage").child(channelname).child("Media").push().getKey();
        database.child("FileStorage").child(channelname).child("Media").child(ref).setValue(file);*/

    }


    private void setListViewForNotes()
    {
        database.child("Notes").child(channelname).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Note note = postSnapshot.getValue(Note.class);
                    String content = note.getContent();
                    String editedby = note.getEditedby();
                    String date = formattedTimeFrom(String.valueOf(note.getTimestamp()));
                    Log.d("note", note.getContent());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void setListViewForTODO()
    {
        database.child("TODO").child(channelname).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    TODO todo = postSnapshot.getValue(TODO.class);
                    String content = todo.getContent();
                    String editedby = todo.getEditedby();
                    String date = formattedTimeFrom(String.valueOf(todo.getTimestamp()));
                    Log.d("TODO", date);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void setListViewForStarred()
    {

        database.child("StarredMessage").child(channelname).addValueEventListener(new ValueEventListener()
        {
            String[] values;


            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                int i = 0;
                values = new String[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Log.d("hello", postSnapshot.getValue().toString());
                    values[i] = postSnapshot.getValue().toString() + "\n" + formattedTimeFrom(postSnapshot.getKey());
                    i++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChatActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, values);
                starredmessagelist.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


    }

    @Override
    protected void onStart()
    {
        super.onStart();
        presenter.startPresenting();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        presenter.stopPresenting();
    }

    private void checkAdminEnabled()
    {
        database.child("adminCheck").child(channelname).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d("hello", channelname);
                try
                {

                    if (Integer.parseInt(dataSnapshot.child("adminmode").getValue().toString()) == 1)
                    {
                        admin_switch.setChecked(true);
                        messagelayout.setVisibility(View.GONE);

                        Log.d("enabled", "enabled");
                    } else
                    {
                        admin_switch.setChecked(false);
                        messagelayout.setVisibility(View.VISIBLE);
                        Log.d("disabled", "disabled");
                    }
                } catch (NullPointerException e)
                {
                    admin_switch.setChecked(false);
                    Log.d("disabled", "disabled");
                }

                if (dataSnapshot.child("owner").exists())
                {
                    //while (iterator.hasNext())
                    // Log.d("hello", String.valueOf(dataSnapshot.child("owner").getChildren().iterator().next().getKey()));
                    for (DataSnapshot postSnapshot : dataSnapshot.child("owner").getChildren())
                    {
                        if (postSnapshot.getKey().equals(userid))
                        {
                            admin_switch.setEnabled(true);
                            messagelayout.setVisibility(View.VISIBLE);
                            Log.d("custom", userid);
                            break;
                        } else
                        {
                            admin_switch.setEnabled(false);
                            Log.d("custom2", userid);
                        }
                        // Log.d("hello",postSnapshot.getKey());
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError error)
            {

            }
        });
    }

    private String formattedTimeFrom(String timestamp)
    {
        Date date = new Date();
        DateFormat timeFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        date.setTime(Long.parseLong(timestamp));
        return timeFormat.format(date);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (isChecked)
        {
            // The toggle is enabled
            adminmode(1);

        } else
        {
            // The toggle is disabled
            adminmode(0);
        }
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
