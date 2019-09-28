package de.vms.vmsapp.Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Meeting {
    private int id;
    private User user;
    private Room room;
    private Date date;
    private int duration;

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

}
