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
 * Adapter to display all favorite places of the current user (which should be an elder)
 */
public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouriteViewHolder> {
    private ArrayList<FavouriteItem> mFavourites;
    private OnItemClickListener mListener;

    /***
     * Delete item on certain position
     */
    public interface OnItemClickListener{
        void onDeleteClick(int position);
        void onMapClick(int position);
    }

    /***
     * Set listener for this adapt3er
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener){ mListener = listener;}

    /***
     * Create a holder for elderItem to be displayed
     */
    public static class FavouriteViewHolder extends RecyclerView.ViewHolder{
        public TextView mFavourite;
        public ImageView mDeleteImage;
        public ImageView mOpenMap;

        public FavouriteViewHolder(View itemView, OnItemClickListener listener){
            super(itemView);
            mFavourite = itemView.findViewById(R.id.favouritePlace);
            mDeleteImage = itemView.findViewById(R.id.removeFavourite);
            mOpenMap = itemView.findViewById(R.id.favouriteLocation);

            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
            mOpenMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener !=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onMapClick(position);
                        }
                    }
                }
            });
        }

    }

    public FavouritesAdapter(ArrayList<FavouriteItem> favourites){mFavourites = favourites;}

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_item, parent, false);
        FavouriteViewHolder fvh = new FavouriteViewHolder(v, mListener);
        return fvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        FavouriteItem currentItem = mFavourites.get(position);
        //provider is the name
        holder.mFavourite.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {return mFavourites.size();}
}
