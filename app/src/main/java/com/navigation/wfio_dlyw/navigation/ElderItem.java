package com.navigation.wfio_dlyw.navigation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A class which holds limited information about an elder user
 * used mainly to be displayed by connectAdapter
 */
public class ElderItem implements Parcelable{
    private String mText1;
    private String mText2;
    private int mId;

    public ElderItem(String fullname, String username, int id){
        mText1 = fullname;
        mText2 = username;
        mId = id;
    }

    protected ElderItem(Parcel in) {
        mText1 = in.readString();
        mText2 = in.readString();
        mId = in.readInt();
    }

    public static final Creator<ElderItem> CREATOR = new Creator<ElderItem>() {
        @Override
        public ElderItem createFromParcel(Parcel in) {
            return new ElderItem(in);
        }

        @Override
        public ElderItem[] newArray(int size) {
            return new ElderItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mText1);
        parcel.writeString(mText2);
        parcel.writeInt(mId);
    }

    public String getmText1() {
        return mText1;
    }

    public void changeText1(String text){
        mText1 = text;
    }

    public String getmText2() {
        return mText2;
    }

    public int getmId() { return mId;}
}
