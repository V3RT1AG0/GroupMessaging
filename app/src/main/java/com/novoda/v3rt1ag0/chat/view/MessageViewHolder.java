package com.novoda.v3rt1ag0.chat.view;

import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.chat.Model.StarredMessage;
import com.novoda.v3rt1ag0.chat.data.model.Chat;
import com.novoda.v3rt1ag0.chat.data.model.Message;

import java.util.Random;

public class MessageViewHolder extends RecyclerView.ViewHolder
{

    private final MessageView messageView;
    public static String channelname;

    public MessageViewHolder(final MessageView messageView, final Chat chat)
    {
        super(messageView);
        this.messageView = messageView;
        messageView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.popup_dialog);
                TextView text = (TextView) dialog.findViewById(R.id.star);
                text.setText("Star");
                text.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        Message message=chat.get(getAdapterPosition());
                        String starredmessage =message.getBody();
                        long date=message.getTimestamp();
                        String author=message.getAuthor().getName();
                        StarredMessage starredMessage=new StarredMessage(starredmessage,Long.toString(date),author);
                        //database.child("StarredMessage").child(channelname).child(String.valueOf(date)).setValue(starredmessage+"\n"+author);
                        // Toast.makeText(messageView.getContext(),chat.get(getAdapterPosition()).getBody(),Toast.LENGTH_LONG).show();
                        database.child("StarredMessage").child(channelname).push().setValue(starredMessage);
                        dialog.dismiss();
                    }
                });
                dialog.show();

                return true;
            }
        });
    }

    public void bind(Message message)
    {
        messageView.display(message);
    }
}
