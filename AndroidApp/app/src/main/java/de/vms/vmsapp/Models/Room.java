package de.vms.vmsapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {
    private int id;
    private String name;

    public Room() {
        //
    }

    public Room(int id, String name) {
        this.setId(id);
        this.setName(name);
    }

    protected Room(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}
