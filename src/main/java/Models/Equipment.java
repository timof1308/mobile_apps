package main.java.Models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Equipment {
    private int id;
    private String name;

    public Equipment() {
        //
    }

    public Equipment(int id, String name) {
        this.setId(id);
        this.setName(name);
    }

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
    public static Equipment parseModel(ResultSet rs) throws SQLException {
        // extracting data
        int id = rs.getInt("id");
        String name = rs.getString("name");

        // create custom model
        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setName(name);

        return equipment;
    }
}
