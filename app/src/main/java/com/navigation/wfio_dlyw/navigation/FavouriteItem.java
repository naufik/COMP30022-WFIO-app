package com.navigation.wfio_dlyw.navigation;

import android.location.Location;

public class FavouriteItem {
    private Location location;

    public FavouriteItem(Location location){
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
