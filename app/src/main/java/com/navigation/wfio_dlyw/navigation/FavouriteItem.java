package com.navigation.wfio_dlyw.navigation;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class FavouriteItem implements Parcelable{
    private Location location;
    private String name;
    private double longitude;
    private double latitude;

    public FavouriteItem(Location location){
        this.location = location;
        this.name = location.getProvider();
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
    }

    protected FavouriteItem(Parcel in){
        name = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<FavouriteItem> CREATOR = new Creator<FavouriteItem>() {
        @Override
        public FavouriteItem createFromParcel(Parcel in) {
            return new FavouriteItem(in);
        }
        @Override
        public FavouriteItem[] newArray(int size) {
            return new FavouriteItem[size];
        }
    };


    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
    }

    public Location getLocation() { return location; }

    public String getName() { return name; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }
}
