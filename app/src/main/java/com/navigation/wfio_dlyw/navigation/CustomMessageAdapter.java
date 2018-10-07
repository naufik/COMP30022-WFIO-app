package com.navigation.wfio_dlyw.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.navigation.wfio_dlyw.comms.Token;

import java.util.ArrayList;

public class CustomMessageAdapter extends ArrayAdapter<Message> {

    public CustomMessageAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Token token = Token.getInstance();
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            if (token.getType().equals("CARER")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_message, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.their_message, parent, false);
            }

        }

        // Get the data item for this position
        Message message = getItem(position);

        // Lookup view for data population

        TextView tvMessage = (TextView) convertView.findViewById(R.id.message_body);
        tvMessage.setText(message.getText());
        // Populate the data into the template view using the data object
        if (token.getType().equals("ELDER")) {
            TextView tvName = (TextView) convertView.findViewById(R.id.name);
            tvName.setText(message.getUsername());
        }

        // Return the completed view to render on screen
        return convertView;
    }
}