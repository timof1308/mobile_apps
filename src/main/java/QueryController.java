package main.java;

public class QueryController {

    // ********************
    // * USERS
    // ********************
    public final static String selectUsers = "SELECT * FROM users;";
    public final static String selectUserById = "SELECT * FROM users WHERE id = ?;";
    public final static String insertUser = "INSERT INTO users (name, email, password, role, token) VALUES (?, ?, ?, ?, ?);";
    public final static String updateUserById = "UPDATE users SET name = ?, email = ?, password = ?, role = ?, token = ? WHERE id = ?;";
    public final static String deleteUserById = "DELETE FROM users WHERE id = ?;";

    // ********************
    // * COMPANIES
    // ********************
    public final static String selectCompanies = "SELECT * FROM companies;";
    public final static String selectCompanyById = "SELECT * FROM companies WHERE id = ?;";
    public final static String insertCompany = "INSERT INTO companies (name) VALUES (?);";
    public final static String updateCompanyById = "UPDATE companies SET name = ? WHERE id = ?;";
    public final static String deleteCompanyById = "DELETE FROM companies WHERE id = ?;";

    // ********************
    // * ROOMS
    // ********************
    public final static String selectRooms = "SELECT * FROM rooms;";
    public final static String selectRoomById = "SELECT * FROM rooms WHERE id = ?;";
    public final static String insertRoom = "INSERT INTO rooms (name) VALUES (?);";
    public final static String updateRoomById = "UPDATE rooms SET name = ? WHERE id = ?;";
    public final static String deleteRoomById = "DELETE FROM rooms WHERE id = ?;";

    // ********************
    // * MEETINGS
    // ********************
    public final static String selectMeetings = "SELECT m.id, m.date, m.duration,\n" +
            "       u.id as user_id, u.name as user_name, u.email,  u.password, u.role, u.token,\n" +
            "       r.id as room_id,  r.name as room_name\n" +
            "FROM meetings m\n" +
            "         JOIN users u on m.user_id = u.id\n" +
            "         JOIN rooms r on m.room_id = r.id;";
    public final static String selectMeetingById = "SELECT m.id, m.date, m.duration,\n" +
            "       u.id as user_id, u.name as user_name, u.email,  u.password, u.role, u.token,\n" +
            "       r.id as room_id,  r.name as room_name\n" +
            "FROM meetings m\n" +
            "         JOIN users u on m.user_id = u.id\n" +
            "         JOIN rooms r on m.room_id = r.id\n" +
            "WHERE r.id = ?";
    public final static String insertMeeting = "INSERT INTO meetings (user_id, room_id, date, duration) VALUES (?, ?, ?, ?);";
    public final static String updateMeetingById = "UPDATE meetings SET user_id = ?, room_id = ?, date = ?, duration = ? WHERE id = ?;";
    public final static String deleteMeetingById = "DELETE FROM meetings WHERE id = ?;";


    // ********************
    // * VISITORS
    // ********************
    public final static String selectVisitors = "SELECT v.id, v.name, v.email, v.tel, v.check_in, v.check_out,\n" +
            "       c.id as company_id, c.name as company_name,\n" +
            "       m.id as meeting_id, m.date, m.duration,\n" +
            "       u.id as user_id, u.name as user_name, u.email as user_email,  u.password, u.role, u.token,\n" +
            "       r.id as room_id,  r.name as room_name\n" +
            "FROM visitors v\n" +
            "    JOIN companies c on v.company_id = c.id\n" +
            "    JOIN meetings m on v.meeting_id = m.id\n" +
            "    JOIN users u on m.user_id = u.id\n" +
            "    JOIN rooms r on m.room_id = r.id;";
    public final static String selectVisitorById = "SELECT v.id, v.name, v.email, v.tel, v.check_in, v.check_out,\n" +
            "       c.id as company_id, c.name as company_name,\n" +
            "       m.id as meeting_id, m.date, m.duration,\n" +
            "       u.id as user_id, u.name as user_name, u.email as user_email,  u.password, u.role, u.token,\n" +
            "       r.id as room_id,  r.name as room_name\n" +
            "FROM visitors v\n" +
            "    JOIN companies c on v.company_id = c.id\n" +
            "    JOIN meetings m on v.meeting_id = m.id\n" +
            "    JOIN users u on m.user_id = u.id\n" +
            "    JOIN rooms r on m.room_id = r.id\n" +
            "WHERE v.id = ?;";
    public final static String insertVisitor = "INSERT INTO visitors (name, email, tel, company_id, meeting_id, check_in, check_out) VALUES (?, ?, ?, ?, ?, ?, ?);";
    public final static String updateVisitorById = "UPDATE meetings SET name = ?, email = ?, tel = ?, company_id = ?, meeting_id = ?, check_in = ?, check_out = ? WHERE id = ?;";
    public final static String deleteVisitorById = "DELETE FROM visitors WHERE id = ?;";

    // ********************
    // * EQUIPMENT
    // ********************
    public final static String selectEquipment = "SELECT * FROM equipment;";
    public final static String selectEquipmentById = "SELECT * FROM equipment WHERE id = ?;";
    public final static String selectEquipmentByRoom = "SELECT re.id, r.id as room_id, r.name as room_name, e.id as equipment_id, e.name as equipment_name\n" +
            "FROM room_equipment re\n" +
            "         JOIN rooms r on re.room_id = r.id\n" +
            "         JOIN equipment e on re.equipment_id = e.id\n" +
            "WHERE r.id = ?;";
    public final static String insertEquipment = "INSERT INTO equipment (name) VALUES (?);";
    public final static String updateEquipmentById = "UPDATE equipment SET name = ? WHERE id = ?;";
    public final static String deleteEquipmentById = "DELETE FROM equipment WHERE id = ?";
    public final static String insertRoomEquipment = "INSERT INTO room_equipment (room_id, equipment_id) VALUES (?, ?);";
    public final static String updateRoomEquipmentById = "UPDATE room_equipment SET room_id = ?, equipment_id = ? WHERE id = ?;";
    public final static String deleteRoomEquipmentById = "DELETE FROM room_equipment WHERE id = ?";
}
