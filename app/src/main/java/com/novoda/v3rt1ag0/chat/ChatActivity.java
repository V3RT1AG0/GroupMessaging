package com.novoda.v3rt1ag0.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.v3rt1ag0.BaseActivity;
import com.novoda.v3rt1ag0.Dependencies;
import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.channel.data.model.Channel;
import com.novoda.v3rt1ag0.chat.Model.AdminMode;
import com.novoda.v3rt1ag0.chat.displayer.ChatDisplayer;
import com.novoda.v3rt1ag0.chat.presenter.ChatPresenter;
import com.novoda.v3rt1ag0.navigation.AndroidNavigator;


public class ChatActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener
{

    private static final String NAME_EXTRA = "channel_name";
    private static final String ACCESS_EXTRA = "channel_access";
    String channelname;
    Switch admin_switch;
    DatabaseReference database;
    AdminMode adminMode;
    private ChatPresenter presenter;
    LinearLayout messagelayout;
    public static String userid;

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
                ;
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
        messagelayout= (LinearLayout) findViewById(R.id.message_layout);
        admin_switch.setOnCheckedChangeListener(this);
        checkAdminEnabled();
        Log.d("customlog",userid);
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
                }
                catch (NullPointerException e)
                {
                    admin_switch.setChecked(false);
                    Log.d("disabled", "disabled");
                }

                if(dataSnapshot.child("owner").exists())
                {
                    //while (iterator.hasNext())
                   // Log.d("hello", String.valueOf(dataSnapshot.child("owner").getChildren().iterator().next().getKey()));
                    for (DataSnapshot postSnapshot: dataSnapshot.child("owner").getChildren()) {
                        if(postSnapshot.getKey().equals(userid))
                        {
                            admin_switch.setEnabled(true);
                            messagelayout.setVisibility(View.VISIBLE);
                            break;
                        }
                        else
                        {
                            admin_switch.setEnabled(false);
                            break;
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

}
