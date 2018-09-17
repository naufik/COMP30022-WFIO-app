package com.navigation.wfio_dlyw.navigation;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ConnectAdapter extends RecyclerView.Adapter<ConnectAdapter.ExampleViewHolder> {
    private ArrayList<ElderItem> mElders;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView1;
        public TextView mTextView2;
        public ExampleViewHolder(View itemView){
            super(itemView);
            mTextView1 = itemView.findViewById(R.id.fullnameEI);
            mTextView2 = itemView.findViewById(R.id.usernameEI);

        }
    }

    public ConnectAdapter(ArrayList<ElderItem> elders){
        mElders = elders;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.elder_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        ElderItem currentItem = mElders.get(position);

        holder.mTextView1.setText(currentItem.getmText1());
        holder.mTextView2.setText(currentItem.getmText2());
    }

    @Override
    public int getItemCount() {
        return mElders.size();
    }
}
