package com.novoda.v3rt1ag0.chat.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.chat.MillisToDateTimeStringFormat;
import com.novoda.v3rt1ag0.chat.Model.Note;

import java.util.List;

/**
 * Created by v3rt1ag0 on 3/22/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder>
{
    List<Note> info;
    Context context;
    String channelname;

    public NotesAdapter(List<Note> info,String channelname)
    {
        this.info = info;
        this.channelname=channelname;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_starred_message, parent, false);
        context = parent.getContext();
        MyViewHolder pvh = new MyViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        Note in = info.get(position);
        holder.message.setText(in.getContent());
        holder.username.setText(in.getEditedby());
        holder.date.setText(MillisToDateTimeStringFormat.formattedTimeFrom(in.getTimestamp()));
    }

    @Override
    public int getItemCount()
    {
        return info.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView username, date, message;
        LinearLayout hiddenlinearlayout;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
            message = (TextView) itemView.findViewById(R.id.message);
            hiddenlinearlayout = (LinearLayout) itemView.findViewById(R.id.fadelinearlayout);
            CardView cardView= (CardView) itemView.findViewById(R.id.cardview);
            cardView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    Log.d("Tag","hello");
                    final Dialog dialog = new Dialog(v.getContext());
                    dialog.setContentView(R.layout.popup_dialog);
                    TextView text = (TextView) dialog.findViewById(R.id.star);
                    text.setText("Delete");
                    text.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                            database.child("Notes").child(channelname).addValueEventListener(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {

                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                    {
                                        //Log.d("hello",getAdapterPosition()+"");
                                        //Log.d("tag","helloasd"+postSnapshot.child("timestamp").getValue()+",,"+info.get(getAdapterPosition()).getTimestamp());
                                        if (postSnapshot.child("timestamp").getValue()==info.get(getAdapterPosition()).getTimestamp())
                                        {
                                            database.child("Notes").child(channelname).removeEventListener(this);
                                            postSnapshot.getRef().removeValue();
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError)
                                {

                                }
                            });
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                    return true;
                }});
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.cardview:
                    Animation fadein = AnimationUtils.loadAnimation(view.getContext(),
                            R.anim.fade_in);
                    hiddenlinearlayout.startAnimation(fadein);
                    hiddenlinearlayout.setVisibility(View.VISIBLE);
                    view.setOnClickListener(null);
            }
        }
    }
}


