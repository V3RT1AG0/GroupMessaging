package com.novoda.v3rt1ag0.channel.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.channel.data.model.Channel;
import com.novoda.notils.caster.Views;

public class ChannelView extends FrameLayout {

    private TextView title;
    private View lockIcon;

    public ChannelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_channel_item_view, this);
        title = Views.findById(this, R.id.channel_title);
        lockIcon = Views.findById(this, R.id.lock_icon);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void display(Channel channel) {
        title.setText(channel.getName());
        lockIcon.setVisibility(GONE);
    }
}
