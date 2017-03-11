package com.novoda.v3rt1ag0.chat.view;

import android.support.v7.widget.RecyclerView;

import com.novoda.v3rt1ag0.chat.data.model.Message;

class MessageViewHolder extends RecyclerView.ViewHolder {

    private final MessageView messageView;

    public MessageViewHolder(MessageView messageView) {
        super(messageView);
        this.messageView = messageView;
    }

    public void bind(Message message) {
        messageView.display(message);
    }
}
