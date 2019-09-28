package de.vms.vmsapp.Models;

public class RoomEquipment {
    private int id;
    private Room room;
    private Equipment equipment;

    public RoomEquipment() {
        //
    }

    public RoomEquipment(int id, Room room, Equipment equipment) {
        this.setId(id);
        this.setRoom(room);
        this.setEquipment(equipment);
    }

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
}
