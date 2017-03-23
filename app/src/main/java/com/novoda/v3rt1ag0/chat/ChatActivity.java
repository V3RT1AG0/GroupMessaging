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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.novoda.v3rt1ag0.BaseActivity;
import com.novoda.v3rt1ag0.Dependencies;
import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.channel.data.model.Channel;
import com.novoda.v3rt1ag0.chat.Adapters.NotesAdapter;
import com.novoda.v3rt1ag0.chat.Adapters.NotificationAdapter;
import com.novoda.v3rt1ag0.chat.Adapters.StarredMessageAdapter;
import com.novoda.v3rt1ag0.chat.Adapters.TODOadapter;
import com.novoda.v3rt1ag0.chat.FileManager.MainActivity;
import com.novoda.v3rt1ag0.chat.FileManager.MyAdapter;
import com.novoda.v3rt1ag0.chat.Model.AdminMode;
import com.novoda.v3rt1ag0.chat.Model.Files;
import com.novoda.v3rt1ag0.chat.Model.Note;
import com.novoda.v3rt1ag0.chat.Model.Notification;
import com.novoda.v3rt1ag0.chat.Model.StarredMessage;
import com.novoda.v3rt1ag0.chat.Model.TODO;
import com.novoda.v3rt1ag0.chat.displayer.ChatDisplayer;
import com.novoda.v3rt1ag0.chat.presenter.ChatPresenter;
import com.novoda.v3rt1ag0.navigation.AndroidNavigator;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.math.BigInteger;


public class ChatActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener
{

    private static final String NAME_EXTRA = "channel_name";
    private static final String ACCESS_EXTRA = "channel_access";
    private static final int REQUEST_CODE = 22;
    String channelname;
    ChatDisplayer chatDisplayer;
    Switch admin_switch;
    RecyclerView starredmessagerecycler, noteslstviewrecycler, todolistviewrecycler, notificationrecycler, filemanagerrecycler;
    LinearLayout starLL, noteLL, TODOLL, FileLL, NotificationLL;
    List<StarredMessage> starredMessageList;
    List<Note> notesList;
    List<TODO> todoList;
    List<Files> fileslist;
    int day = 0, month = 0, year = 0;
    List<Notification> notificationList;
    ImageView todoimage, notesimage, cancel_button, add_notification;
    FloatingActionButton show_hide_toggle;
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
        Log.d("tag", "" + enabled);
        adminMode.setAdminmode(enabled);
        adminMode.setChannelname(channelname);
        database.child("adminCheck").child(channelname).setValue(adminMode);
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
        starredmessagerecycler = (RecyclerView) findViewById(R.id.starredRecycler);
        noteslstviewrecycler = (RecyclerView) findViewById(R.id.noteslistview);
        todolistviewrecycler = (RecyclerView) findViewById(R.id.todolistview);
        notificationrecycler = (RecyclerView) findViewById(R.id.notificationlistview);
        filemanagerrecycler = (RecyclerView) findViewById(R.id.filelist);
        todoimage = (ImageView) findViewById(R.id.TODOImage);
        notesimage = (ImageView) findViewById(R.id.NotesImage);
        popup_linear_layout = (LinearLayout) findViewById(R.id.popup_linearlayout);
        show_hide_toggle = (FloatingActionButton) findViewById(R.id.show_hide_image);
        cancel_button = (ImageView) findViewById(R.id.cancel_button);
        add_notification = (ImageView) findViewById(R.id.AddNotification);
        chatDisplayer = (ChatDisplayer) findViewById(R.id.chat_view);
        starLL = (LinearLayout) findViewById(R.id.LLstar);
        noteLL = (LinearLayout) findViewById(R.id.LLnote);
        TODOLL = (LinearLayout) findViewById(R.id.LLtodo);
        FileLL = (LinearLayout) findViewById(R.id.LLfile);
        NotificationLL = (LinearLayout) findViewById(R.id.LLnoti);
        starredMessageList = new ArrayList<>();
        notesList = new ArrayList<>();
        todoList = new ArrayList<>();
        fileslist = new ArrayList<>();
        notificationList = new ArrayList<>();

        ImageView file_chooser = (ImageView) findViewById(R.id.uploadfile);
        ImageView imageView = (ImageView) findViewById(R.id.open_all_files);

        //Log.d("time",System.currentTimeMillis()/1000+"");
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
                show_hide_toggle.setVisibility(View.VISIBLE);
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
                show_hide_toggle.setVisibility(View.GONE);
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
                        SecureRandom random = new SecureRandom();
                        String key = new BigInteger(130, random).toString(32);
                        TODO todo = new TODO(Long.toString(System.currentTimeMillis()), text.getText().toString(), username, false, key);
                        database.child("TODO").child(channelname).child(key).setValue(todo);
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
                        Note note = new Note(Long.toString(System.currentTimeMillis()), text.getText().toString(), username);
                        database.child("Notes").child(channelname).push().setValue(note);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


        add_notification.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ChatActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );

                dpd.show(getFragmentManager(), "Datepickerdialog");

            }
        });


        admin_switch.setOnCheckedChangeListener(this);

        getUsername();
        checkAdminEnabled();
        setListViewForStarred();
        setListViewForNotes();
        setListViewForTODO();
        setListViewForNotifications();
        setListViewForFiles();


        // Log.d("customlog", userid);

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


        database.child("Admin").child(channelname).addValueEventListener(new ValueEventListener()
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

    private void setListViewForFiles()
    {

        database.child("FileStorage").child(channelname).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                fileslist.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {

                    Log.d("TAG", "executed");
                    if (postSnapshot.getKey().equals("directory") || postSnapshot.getKey().equals("filename"))
                        continue;
                    Files file = postSnapshot.getValue(Files.class);
                    Log.d("TAG", file.getFilename());
                    fileslist.add(new Files(file.getDirectory(), file.getDownloadpath(), file.getFilename()));
                }
                if(!fileslist.isEmpty())
                    FileLL.setVisibility(View.VISIBLE);
                MyAdapter myAdapter = new MyAdapter(fileslist, "FileStorage/" + channelname, channelname, userid);
                filemanagerrecycler.setHasFixedSize(true);
                filemanagerrecycler.setNestedScrollingEnabled(false);
                filemanagerrecycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                filemanagerrecycler.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
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
                NotesAdapter adapter;
                notesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Note notes = postSnapshot.getValue(Note.class);
                    notesList.add(notes);
                }
                if(!notesList.isEmpty())
                    noteLL.setVisibility(View.VISIBLE);
                adapter = new NotesAdapter(notesList,channelname);
                noteslstviewrecycler.setHasFixedSize(true);
                noteslstviewrecycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                noteslstviewrecycler.setAdapter(adapter);
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
                TODOadapter adapter;
                todoList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    TODO todo = postSnapshot.getValue(TODO.class);
                    todoList.add(todo);
                }
                if(!todoList.isEmpty())
                    TODOLL.setVisibility(View.VISIBLE);
                adapter = new TODOadapter(todoList, channelname);
                todolistviewrecycler.setHasFixedSize(true);
                todolistviewrecycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                todolistviewrecycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void setListViewForNotifications()
    {
        database.child("Notification").child(channelname).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                NotificationAdapter adapter;
                notificationList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Notification notification = postSnapshot.getValue(Notification.class);
                    notificationList.add(notification);
                }
                if(!notificationList.isEmpty())
                    NotificationLL.setVisibility(View.VISIBLE);
                adapter = new NotificationAdapter(notificationList);
                notificationrecycler.setHasFixedSize(true);
                notificationrecycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                notificationrecycler.setAdapter(adapter);
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
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                StarredMessageAdapter adapter;
                starredMessageList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    StarredMessage starredMessage = postSnapshot.getValue(StarredMessage.class);
                    starredMessageList.add(starredMessage);
                }
                if(!starredMessageList.isEmpty())
                   starLL.setVisibility(View.VISIBLE);
                adapter = new StarredMessageAdapter(starredMessageList,channelname);
                starredmessagerecycler.setHasFixedSize(true);
                starredmessagerecycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                starredmessagerecycler.setAdapter(adapter);
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
                            chatDisplayer.showAddMembersButton();
                            Log.d("custom", userid + " " + postSnapshot.getKey());
                            break;
                        } else
                        {
                            chatDisplayer.hideAddMembersButton();
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
    {
        this.day = dayOfMonth;
        this.month = monthOfYear;
        this.year = year;
        Calendar now = Calendar.getInstance();
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog dpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                ChatActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true

        );
        dpd.show(getFragmentManager(), "TimePickerDialog");
    }

    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, final int hourOfDay, final int minute, int second)
    {

        final Dialog dialog = new Dialog(ChatActivity.this);
        dialog.setContentView(R.layout.edittextpopup);
        final EditText text = (EditText) dialog.findViewById(R.id.edit_text);
        Button button = (Button) dialog.findViewById(R.id.post_button);

        String myDate = year + "/" + (month + 1) + "/" + day + " " + hourOfDay + ":" + minute + ":" + 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        try
        {
            date = sdf.parse(myDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        final long millis = date.getTime();

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Notification notification = new Notification(text.getText().toString(), hourOfDay, minute, day, month, year, Long.toString(millis), username);
                String key=database.child("Notification").child(channelname).push().getKey();
                database.child("Notification").child(channelname).child(key).setValue(notification);
                dialog.dismiss();
                SendNotificationToHttpServer(text.getText().toString(),Long.toString(millis/1000),key);
            }
        });
        dialog.show();
    }

    private void SendNotificationToHttpServer(String content, String time,String key)
    {
        AsyncHttpPost post = new AsyncHttpPost("http://f8ct.com/gm/scheduled_notification.php");
        MultipartFormDataBody body = new MultipartFormDataBody();
        body.addStringPart("content", content);
        body.addStringPart("time", time);
        body.addStringPart("key", key);
        body.addStringPart("action", "notification");
        post.setBody(body);
        AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback(){
            @Override
            public void onCompleted(Exception ex, AsyncHttpResponse source, String result) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                System.out.println("Server says: " + result);
            }
        });
    }


    //TODO retrive firebase ids of users
    private List retrivefirebaseidofusers(String channelname)
    {
        final List<String> list=new ArrayList<String>();
        database.child("owners").child(channelname).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {

                    database.child("users").child(postSnapshot.getKey()).child("firebasetoken").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            list.add(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
        return list;
    }
}
