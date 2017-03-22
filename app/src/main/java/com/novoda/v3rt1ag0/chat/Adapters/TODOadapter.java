package com.novoda.v3rt1ag0.chat.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
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

        public MyViewHolder(View itemView)
        {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
            message = (TextView) itemView.findViewById(R.id.message);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    FirebaseDatabase.getInstance().getReference().child("TODO").child(channelname).child(info.get(getAdapterPosition()).getKey()).child("checked").setValue(isChecked);
                }
            });
        }

        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {

            }
        }
    }
}

