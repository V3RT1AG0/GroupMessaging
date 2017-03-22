package com.novoda.v3rt1ag0.chat.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.chat.MillisToDateTimeStringFormat;
import com.novoda.v3rt1ag0.chat.Model.StarredMessage;

import java.util.List;

/**
 * Created by v3rt1ag0 on 3/22/17.
 */

public class StarredMessageAdapter extends RecyclerView.Adapter<StarredMessageAdapter.MyViewHolder>
{
    List<StarredMessage> info;
    Context context;

    public StarredMessageAdapter(List<StarredMessage> info)
    {
        this.info=info;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_starred_message, parent, false);
        context=parent.getContext();
        MyViewHolder pvh = new MyViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        StarredMessage in=info.get(position);
        holder.message.setText(in.getMessage());
        holder.username.setText(in.getNameofuser());
        holder.date.setText(MillisToDateTimeStringFormat.formattedTimeFrom(in.getTimeinmillis()));
    }

    @Override
    public int getItemCount()
    {
        return info.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView username,date,message;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            username= (TextView) itemView.findViewById(R.id.name);
            date= (TextView) itemView.findViewById(R.id.date);
            message= (TextView) itemView.findViewById(R.id.message);
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



