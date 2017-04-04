package com.novoda.v3rt1ag0.chat.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.chat.MillisToDateTimeStringFormat;
import com.novoda.v3rt1ag0.chat.Model.Notification;

import java.util.List;

/**
 * Created by v3rt1ag0 on 3/22/17.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>
{
    List<Notification> info;
    Context context;

    public NotificationAdapter(List<Notification> info)
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
        Notification in=info.get(position);
        holder.message.setText(in.getContent());
        holder.username.setText(in.getName());
        holder.date.setText(MillisToDateTimeStringFormat.formattedTimeFrom(in.getTimestamp()));
    }

    @Override
    public int getItemCount()
    {
        return info.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView username,date,message;
        LinearLayout hiddenlinearlayout;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            username= (TextView) itemView.findViewById(R.id.name);
            date= (TextView) itemView.findViewById(R.id.date);
            message= (TextView) itemView.findViewById(R.id.message);
            hiddenlinearlayout= (LinearLayout) itemView.findViewById(R.id.fadelinearlayout);
            message.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.message:
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
