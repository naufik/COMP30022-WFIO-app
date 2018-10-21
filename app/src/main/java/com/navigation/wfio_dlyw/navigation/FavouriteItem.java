package com.navigation.wfio_dlyw.navigation;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/***
 * A class which holds information about a favourite place
 * used mainly to be displayed by Favourites adapter
 */
public class FavouriteItem implements Parcelable{
    private Location location;
    private String name;
    private double longitude;
    private double latitude;

    /***
     * Create a new favouriteItem object from a location
     * @param location Location to be added as a favorite
     */
    public FavouriteItem(Location location){
        this.location = location;
        this.name = location.getProvider();
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
    }

    // Read favorite from parcel
    protected FavouriteItem(Parcel in){
        name = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    /***
     * generate a creator of favorite items
     */
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

    /***
     * @return the location
     */
    public Location getLocation() { return location; }

    /***
     * @return the name of the location
     */
    public String getName() { return name; }

    /***
     * @return the latitude of the location
     */
    public double getLatitude() { return latitude; }

    /***
     * @return the longitude of the location
     */
    public double getLongitude() { return longitude; }
}
