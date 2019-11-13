package de.vms.vmsapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Company implements Parcelable {
    private int id;
    private String name;

    public Company() {
        //
    }

    public Company(int id, String name) {
        this.setId(id);
        this.setName(name);
    }

    protected Company(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<Company> CREATOR = new Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
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
