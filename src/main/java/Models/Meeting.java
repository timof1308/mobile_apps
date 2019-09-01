package main.java.Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Meeting {
    private int id;
    private User user;
    private Room room;
    private Timestamp date;

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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    /**
     * Extracting data from database record and return model
     *
     * @param rs database record
     * @return model
     * @throws SQLException not handled in static method
     */
    public static Meeting parseModel(ResultSet rs) throws SQLException {
        // extracting data
        int id = rs.getInt("id");
        Timestamp date = rs.getTimestamp("date");

        // creating models for joins
        User host = new User();
        host.setId(rs.getInt("user_id"));
        host.setName(rs.getString("user_name"));
        host.setPassword(rs.getString("password"));
        host.setRole(rs.getInt("role"));
        host.setToken(rs.getInt("token"));

        Room room = new Room();
        room.setId(rs.getInt("room_id"));
        room.setName(rs.getString("room_name"));

        // create custom model
        Meeting meeting = new Meeting();
        meeting.setId(id);
        meeting.setDate(date);
        meeting.setUser(host);
        meeting.setRoom(room);

        return meeting;
    }
}
