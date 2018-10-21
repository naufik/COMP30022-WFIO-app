package com.navigation.wfio_dlyw.navigation;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter to display all elders connected with the current user (which should be a carer)
 */
public class ConnectAdapter extends RecyclerView.Adapter<ConnectAdapter.ExampleViewHolder> {
    private ArrayList<ElderItem> mElders;
    private OnItemClickListener mListener;

    /***
     * Delete item on certain position
     */
    public interface OnItemClickListener{
        void onDeleteClick(int position);
    }

    /***
     * Set listener for this adapt3er
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    /***
     * Create a holder for elderItem to be displayed
     */
    public static class ExampleViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView1;
        public TextView mTextView2;
        public ImageView mDeleteImage;


        private ExampleViewHolder(View itemView, OnItemClickListener listener){
            super(itemView);
            mTextView1 = itemView.findViewById(R.id.fullnameEI);
            mTextView2 = itemView.findViewById(R.id.usernameEI);
            mDeleteImage = itemView.findViewById(R.id.image_delete);


            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener !=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    /***
     * Initialize connect adapter
     * @param elders the current list of elders to be displayed
     */
    public ConnectAdapter(ArrayList<ElderItem> elders){
        mElders = elders;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.elder_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
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
