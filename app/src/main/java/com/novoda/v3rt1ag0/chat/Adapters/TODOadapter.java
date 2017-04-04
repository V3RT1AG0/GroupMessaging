package com.novoda.v3rt1ag0.chat.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.chat.MillisToDateTimeStringFormat;
import com.novoda.v3rt1ag0.chat.Model.TODO;

import java.util.List;

/**
 * Created by v3rt1ag0 on 3/22/17.
 */

public class TODOadapter extends RecyclerView.Adapter<TODOadapter.MyViewHolder>
{
    List<TODO> info;
    Context context;
    String channelname;

    public TODOadapter(List<TODO> info,String channelname)
    {
        this.info = info;
        this.channelname=channelname;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_todo, parent, false);
        context = parent.getContext();
        MyViewHolder pvh = new MyViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        TODO in = info.get(position);
        holder.message.setText(in.getContent());
        holder.username.setText(in.getEditedby());
        holder.date.setText(MillisToDateTimeStringFormat.formattedTimeFrom(in.getTimestamp()));
        holder.checkBox.setChecked(in.getChecked());
    }

    @Override
    public int getItemCount()
    {
        return info.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView username, date, message;
        CheckBox checkBox;
        LinearLayout hiddenlinearlayout;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
            message = (TextView) itemView.findViewById(R.id.message);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            hiddenlinearlayout= (LinearLayout) itemView.findViewById(R.id.fadelinearlayout);
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
                            database.child("TODO").child(channelname).addValueEventListener(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {

                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                    {
                                        Log.d("hello",getAdapterPosition()+"");
                                        Log.d("tag","helloasd"+postSnapshot.child("timestamp").getValue()+",,"+info.get(getAdapterPosition()).getTimestamp());
                                        if (postSnapshot.child("timestamp").getValue()==info.get(getAdapterPosition()).getTimestamp())
                                        {
                                            database.child("TODO").child(channelname).removeEventListener(this);
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
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    FirebaseDatabase.getInstance().getReference().child("TODO").child(channelname).child(info.get(getAdapterPosition()).getKey()).child("checked").setValue(isChecked);
                    if(isChecked)
                    {
                        message.setPaintFlags(message.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }
            });
        }

        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.cardview:
                    if(hiddenlinearlayout.getVisibility()==View.GONE)
                    {
                        Animation fadein = AnimationUtils.loadAnimation(view.getContext(),
                                R.anim.fade_in);
                        hiddenlinearlayout.startAnimation(fadein);
                        hiddenlinearlayout.setVisibility(View.VISIBLE);
                        //view.setOnClickListener(null);
                    }
                    else
                    {
                        Animation fadeout = AnimationUtils.loadAnimation(view.getContext(),
                                R.anim.fade_out);
                        hiddenlinearlayout.startAnimation(fadeout);
                        hiddenlinearlayout.setVisibility(View.GONE);
                    }
            }
        }
    }
}

