package de.vms.vmsapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Meeting implements Parcelable {
    private int id;
    private User user;
    private Room room;
    private Date date;
    private int duration;
    private ArrayList<Visitor> visitors;

    public Meeting() {
        //
    }

    public Meeting(int id, User user, Room room, String date, int duration) {
        this.setId(id);
        this.setUser(user);
        this.setRoom(room);
        this.setDate(date);
        this.setDuration(duration);
    }

    public Meeting(int id, User user, Room room, String date, int duration, ArrayList<Visitor> visitors) {
        this.setId(id);
        this.setUser(user);
        this.setRoom(room);
        this.setDate(date);
        this.setDuration(duration);
        this.setVisitors(visitors);
    }

    protected Meeting(Parcel in) {
        id = in.readInt();
        user = in.readParcelable(User.class.getClassLoader());
        room = in.readParcelable(Room.class.getClassLoader());
        duration = in.readInt();
        visitors = in.createTypedArrayList(Visitor.CREATOR);
    }

    public static final Creator<Meeting> CREATOR = new Creator<Meeting>() {
        @Override
        public Meeting createFromParcel(Parcel in) {
            return new Meeting(in);
        }

        @Override
        public Meeting[] newArray(int size) {
            return new Meeting[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String date) {
        Date parsedDate = null;
        // set format for date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // parse date
            parsedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            // set date or null
            this.date = parsedDate;
        }
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ArrayList<Visitor> getVisitors() {
        return visitors;
    }

    public void setVisitors(ArrayList<Visitor> visitors) {
        this.visitors = visitors;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(id);
        dest.writeParcelable(user, i);
        dest.writeParcelable(room, i);
        dest.writeSerializable(date);
        dest.writeParcelableList(visitors, i);
    }
}
