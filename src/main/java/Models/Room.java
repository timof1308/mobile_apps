package main.java.Models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Room {
    private int id;
    private String name;

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

    /**
     * Extracting data from database record and return model
     *
     * @param rs database record
     * @return model
     * @throws SQLException not handled in static method
     */
    public static Room parseModel(ResultSet rs) throws SQLException {
        // extracting data
        int id = rs.getInt("id");
        String name = rs.getString("name");

        // create model
        Room room = new Room();
        room.setId(id);
        room.setName(name);

        return room;
    }
}
