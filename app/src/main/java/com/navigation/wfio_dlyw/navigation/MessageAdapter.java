package com.navigation.wfio_dlyw.navigation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<Message>();
    Context context;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Message message = messages.get(i);
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(message instanceof VoiceClip){
            ClipViewHolder holder = new ClipViewHolder();

            if (message.isBelongsToCurrentUser()) {
                convertView = messageInflater.inflate(R.layout.my_clip, null);
                holder.clip = (Button) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
                holder.clip = ((VoiceClip) message).getClip();
            } else{
                convertView = messageInflater.inflate(R.layout.their_clip, null);
                holder.clip = (Button) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
                holder.clip = ((VoiceClip) message).getClip();
            }
        } else{
            MessageViewHolder holder = new MessageViewHolder();

            if (message.isBelongsToCurrentUser()) { // this message was sent by us so let's create a basic chat bubble on the right
                convertView = messageInflater.inflate(R.layout.my_message, null);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
                holder.messageBody.setText(message.getText());
            }else{ // this message was sent by someone else so let's create an advanced chat bubble on the left
                convertView = messageInflater.inflate(R.layout.their_message, null);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.name.setText(message.getUsername());
                convertView.setTag(holder);
                holder.messageBody.setText(message.getText());
                //set the username of the sender here
            }
        }
        return convertView;
    }

}

class MessageViewHolder {
    public View avatar;
    public TextView name;
    public TextView messageBody;
}

class ClipViewHolder {
    public Button clip;
}