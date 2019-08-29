package Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoomEquipment {
    private int id;
    private Room room;
    private Equipment equipment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    /**
     * Extracting data from database record and return model
     *
     * @param rs database record
     * @return model
     * @throws SQLException not handled in static method
     */
    public static RoomEquipment parseModel(ResultSet rs) throws SQLException {
        // extracting data
        // equipment model for record
        Equipment e = new Equipment();
        e.setId(rs.getInt("equipment_id"));
        e.setName(rs.getString("equipment_name"));

        // room model for record
        Room r = new Room();
        r.setId(rs.getInt("room_id"));
        r.setName(rs.getString("room_name"));

        // build room equipment model
        RoomEquipment room_equipment = new RoomEquipment();
        room_equipment.setId(rs.getInt("id"));
        room_equipment.setRoom(r);
        room_equipment.setEquipment(e);

        return room_equipment;
    }
}
