import Models.*;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseClient {

    /**
     * Database credentials
     */
    private final String DB_HOST = "localhost:5432";
    private final String DB_NAME = "mobile_apps";
    private final String DB_USER = "postgres";
    private final String DB_PASSWORD = "postgres";

    /**
     * Connectivity variables
     */
    private Connection c = null;
    private Statement stmt = null;

    public DatabaseClient() {
        this.conn();
    }

    public static void main(String[] args) {
        DatabaseClient db = new DatabaseClient();

        Company tmp_company = new Company();
        tmp_company.setName("NTT");
        //db.insertCompany(tmp_company);

        ArrayList<Company> companies = db.getCompanies();

        System.out.println("COMPANIES:");
        for (Company company : companies) {
            System.out.println(company.getId() + ": " + company.getName());
        }
    }

    /**
     * Connect to PostgreSQL Database
     */
    private void conn() {
        try {
            Class.forName("org.postgresql.Driver");
            this.c = DriverManager.getConnection("jdbc:postgresql://" + this.DB_HOST + "/" + this.DB_NAME, this.DB_USER, this.DB_PASSWORD);

            // --> connection successful
            System.out.println("Opened database successfully");

            // prepare connection statement
            this.stmt = c.createStatement();

            // this.stmt.close();
            // this.c.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Close database connection
     */
    public void close() {
        try {
            this.c.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * get all users and it's data that are stored at the database
     *
     * @return user's list from database
     */
    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<User>();

        try {
            ResultSet rs = this.stmt.executeQuery(QueryController.selectUsers);
            // for all records
            while (rs.next()) {
                // create model
                User user = User.parseModel(rs);

                users.add(user);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return users;
    }

    /**
     * get all companies that are stored at the database
     *
     * @return arraylist of all companies
     */
    public ArrayList<Company> getCompanies() {
        ArrayList<Company> companies = new ArrayList<Company>();

        try {
            ResultSet rs = this.stmt.executeQuery(QueryController.selectCompanies);
            // for all records
            while (rs.next()) {
                // create model
                Company company = Company.parseModel(rs);

                companies.add(company);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return companies;
    }

    /**
     * get all rooms that are stored at the database
     *
     * @return arraylist of all rooms
     */
    public ArrayList<Room> getRooms() {
        ArrayList<Room> rooms = new ArrayList<Room>();

        try {
            ResultSet rs = this.stmt.executeQuery(QueryController.selectRooms);
            // for all records
            while (rs.next()) {
                // creating model
                Room room = Room.parseModel(rs);

                rooms.add(room);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return rooms;
    }

    /**
     * get all meetings that are stored at the database
     * Creating sub models for room and user / host
     *
     * @return arraylist of meetings
     */
    public ArrayList<Meeting> getMeetings() {
        ArrayList<Meeting> meetings = new ArrayList<Meeting>();

        try {
            ResultSet rs = this.stmt.executeQuery(QueryController.selectMeetings);
            // for all records
            while (rs.next()) {
                // creating model
                Meeting meeting = Meeting.parseModel(rs);

                meetings.add(meeting);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return meetings;
    }

    /**
     * get all equipment that are stored at the database
     *
     * @return arraylist of all equipment
     */
    public ArrayList<Equipment> getAllEquipment() {
        ArrayList<Equipment> equipment = new ArrayList<Equipment>();

        try {
            ResultSet rs = this.stmt.executeQuery(QueryController.selectEquipment);
            // for all records
            while (rs.next()) {
                // creating model
                Equipment eq = Equipment.parseModel(rs);

                equipment.add(eq);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return equipment;
    }

    /**
     * get all equipment that are stored at the database
     * creating sub room and equipment model
     *
     * @param room_id to get equipment for
     * @return arraylist of room equipment
     */
    public ArrayList<RoomEquipment> getRoomEquipment(int room_id) {
        ArrayList<RoomEquipment> room_equipment = new ArrayList<RoomEquipment>();

        try {
            // prepare statement
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.selectEquipmentByRoom);
            // fill statement
            pstmt.setInt(1, room_id);
            // run query
            ResultSet rs = pstmt.executeQuery();

            // get all records left
            while (rs.next()) {
                // creating model
                RoomEquipment re = RoomEquipment.parseModel(rs);

                room_equipment.add(re);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return room_equipment;
    }

    public ArrayList<Visitor> getVisitors() {
        ArrayList<Visitor> visitors = new ArrayList<Visitor>();

        try {
            ResultSet rs = this.stmt.executeQuery(QueryController.selectVisitors);

            // get all records left
            while (rs.next()) {
                // creating model
                Visitor visitor = Visitor.parseModel(rs);

                visitors.add(visitor);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return visitors;
    }

    /**
     * Find user with matching id
     *
     * @param id as parameter to get user with primary key
     * @return user model or null
     */
    public User getUser(int id) {
        User user = null;

        try {
            // prepare
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.selectUserById);
            // fill
            pstmt.setInt(1, id);
            // execute
            ResultSet rs = pstmt.executeQuery();

            // check if record exists
            if (rs.next()) {
                // create model
                user = User.parseModel(rs);
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return user;
    }

    /**
     * get company with matching id
     *
     * @param id to search company record
     * @return Company model or null
     */
    public Company getCompany(int id) {
        Company company = null;

        try {
            // prepare
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.selectCompanyById);
            // fill
            pstmt.setInt(1, id);
            // execute
            ResultSet rs = pstmt.executeQuery();

            // check if record exists
            if (rs.next()) {
                // create model
                company = Company.parseModel(rs);
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return company;
    }

    /**
     * get equipment with matching id
     *
     * @param id to search equipment record
     * @return equipment model or null
     */
    public Equipment getEquipment(int id) {
        Equipment equipment = null;

        try {
            // prepare
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.selectEquipmentById);
            // fill
            pstmt.setInt(1, id);
            // execute
            ResultSet rs = pstmt.executeQuery();

            // check if record exists
            if (rs.next()) {
                // create model
                equipment = Equipment.parseModel(rs);
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return equipment;
    }

    /**
     * Get meeting with matching id
     *
     * @param id to search meeting record
     * @return meeting model or null
     */
    public Meeting getMeeting(int id) {
        Meeting meeting = null;

        try {
            // prepare
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.selectMeetingById);
            // fill
            pstmt.setInt(1, id);
            // execute
            ResultSet rs = pstmt.executeQuery();

            // check if record exists
            if (rs.next()) {
                // create model
                meeting = Meeting.parseModel(rs);
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return meeting;
    }

    /**
     * Get room with matching id
     *
     * @param id to search room record
     * @return room model or null
     */
    public Room getRoom(int id) {
        Room room = null;

        try {
            // prepare
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.selectRoomById);
            // fill
            pstmt.setInt(1, id);
            // execute
            ResultSet rs = pstmt.executeQuery();

            // check if record exists
            if (rs.next()) {
                // create model
                room = Room.parseModel(rs);
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return room;
    }

    /**
     * Get visitor with matching id
     *
     * @param id to search visitor record
     * @return visitor model or null
     */
    public Visitor getVisitor(int id) {
        Visitor visitor = null;

        try {
            // prepare
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.selectVisitorById);
            // fill
            pstmt.setInt(1, id);
            // execute
            ResultSet rs = pstmt.executeQuery();

            // check if record exists
            if (rs.next()) {
                // create model
                visitor = Visitor.parseModel(rs);
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return visitor;
    }

    /**
     * Create new user record in database
     *
     * @param user model to insert
     */
    public void insertUser(User user) {
        try {
            // insert user - id will be set automatically by auto increment
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.insertUser, Statement.RETURN_GENERATED_KEYS);
            // fill
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, PasswordController.generateHash(user.getPassword()));
            pstmt.setInt(4, user.getRole());
            pstmt.setInt(5, user.getToken());
            // run
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Insert new company record into database
     *
     * @param company model to insert
     */
    public void insertCompany(Company company) {
        try {
            // insert company - id will be set automatically by auto increment
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.insertCompany, Statement.RETURN_GENERATED_KEYS);
            // fill
            pstmt.setString(1, company.getName());
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Insert new room record into database
     *
     * @param room room to insert
     */
    public void insertRoom(Room room) {
        try {
            // insert room - id will be set automatically by auto increment
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.insertRoom, Statement.RETURN_GENERATED_KEYS);
            // fill
            pstmt.setString(1, room.getName());
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Insert new meeting record into database
     *
     * @param meeting meeting to insert
     */
    public void insertMeeting(Meeting meeting) {
        try {
            // insert room - id will be set automatically by auto increment
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.insertMeeting, Statement.RETURN_GENERATED_KEYS);
            // fill
            pstmt.setInt(1, meeting.getUser().getId());
            pstmt.setInt(2, meeting.getRoom().getId());
            pstmt.setTimestamp(3, meeting.getDate());
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Insert new visitor record into database
     *
     * @param visitor visitor to insert
     */
    public void insertVisitor(Visitor visitor) {
        try {
            // insert room - id will be set automatically by auto increment
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.insertVisitor, Statement.RETURN_GENERATED_KEYS);
            // fill
            pstmt.setString(1, visitor.getName());
            pstmt.setString(2, visitor.getEmail());
            pstmt.setInt(3, visitor.getCompany().getId());
            pstmt.setInt(4, visitor.getMeeting().getId());
            pstmt.setTimestamp(5, visitor.getCheck_in());
            pstmt.setTimestamp(6, visitor.getCheck_out());
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Insert new equipment record into database
     *
     * @param equipment equipment to insert
     */
    public void insertEquipment(Equipment equipment) {
        try {
            // insert room - id will be set automatically by auto increment
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.insertEquipment, Statement.RETURN_GENERATED_KEYS);
            // fill
            pstmt.setString(1, equipment.getName());
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Insert new equipment_room record into database
     *
     * @param equipment_room equipment_room to insert
     */
    public void insertRoomEquipment(RoomEquipment equipment_room) {
        try {
            // insert room - id will be set automatically by auto increment
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.insertRoomEquipment, Statement.RETURN_GENERATED_KEYS);
            // fill
            pstmt.setInt(1, equipment_room.getRoom().getId());
            pstmt.setInt(2, equipment_room.getEquipment().getId());
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Update existing user record
     *
     * @param user to update and get data from
     * @return user model
     */
    public User updateUser(User user) {

        try {
            // update user record and all it's values with matching id
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.updateUserById);
            // fill
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setInt(4, user.getRole());
            pstmt.setInt(5, user.getToken());
            pstmt.setInt(6, user.getId());
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return user;
    }

    /**
     * Update existing company record
     *
     * @param company tp update and get data from
     * @return company model
     */
    public Company updateCompany(Company company) {
        try {
            // update record with company id
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.updateCompanyById);
            // fill
            pstmt.setString(1, company.getName());
            pstmt.setInt(2, company.getId());
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return company;
    }

    /**
     * Update room record at database
     *
     * @param room room to update
     */
    public void udpateRoom(Room room) {
        try {
            // prepare
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.updateRoomById);
            // fill
            pstmt.setString(1, room.getName());
            pstmt.setInt(2, room.getId());
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Update meeting record at database
     *
     * @param meeting meeting to update
     */
    public void updateMeeting(Meeting meeting) {
        try {
            // prepare
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.updateMeetingById);
            // fill
            pstmt.setInt(1, meeting.getUser().getId());
            pstmt.setInt(2, meeting.getRoom().getId());
            pstmt.setTimestamp(3, meeting.getDate());
            pstmt.setInt(4, meeting.getId());
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Update visitor record at database
     *
     * @param visitor visitor to update
     */
    public void updateVisitor(Visitor visitor) {
        try {
            // prepare
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.updateVisitorById);
            // fill
            pstmt.setString(1, visitor.getName());
            pstmt.setString(2, visitor.getEmail());
            pstmt.setInt(3, visitor.getCompany().getId());
            pstmt.setInt(4, visitor.getMeeting().getId());
            pstmt.setTimestamp(5, visitor.getCheck_in());
            pstmt.setTimestamp(6, visitor.getCheck_out());
            pstmt.setInt(7, visitor.getId());
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Update equipment record at database
     *
     * @param equipment equipment to update
     */
    public void updateEquipment(Equipment equipment) {
        try {
            // prepare
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.updateEquipmentById);
            // fill
            pstmt.setString(1, equipment.getName());
            pstmt.setInt(2, equipment.getId());
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Update equipment_room record at database
     *
     * @param equipment_room equipment_room to update
     */
    public void updateRoomEquipment(RoomEquipment equipment_room) {
        try {
            // prepare
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.updateRoomEquipmentById);
            // fill
            pstmt.setInt(1, equipment_room.getRoom().getId());
            pstmt.setInt(2, equipment_room.getEquipment().getId());
            pstmt.setInt(3, equipment_room.getId());
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Delete user record from database with matching id
     *
     * @param id user's primary key
     */
    public void deleteUser(int id) {
        try {
            // delete user with id
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.deleteUserById);
            // fill
            pstmt.setInt(1, id);
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Delete user record from database with matching id
     *
     * @param id company's primary key
     */
    public void deleteCompany(int id) {
        try {
            // delete company with id
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.deleteCompanyById);
            // fill
            pstmt.setInt(1, id);
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Delete room record from database with matching id
     *
     * @param id room's primary key
     */
    public void deleteRoom(int id) {
        try {
            // delete room with id
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.deleteRoomById);
            // fill
            pstmt.setInt(1, id);
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Delete meeting record from database with matching id
     *
     * @param id meetings's primary key
     */
    public void deleteMeeting(int id) {
        try {
            // delete meeting with id
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.deleteMeetingById);
            // fill
            pstmt.setInt(1, id);
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Delete visitor record from database with matching id
     *
     * @param id visitor's primary key
     */
    public void deleteVisitor(int id) {
        try {
            // delete visitor with id
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.deleteVisitorById);
            // fill
            pstmt.setInt(1, id);
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Delete equipment record from database with matching id
     *
     * @param id equipment's primary key
     */
    public void deleteEquipment(int id) {
        try {
            // delete visitor with id
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.deleteEquipmentById);
            // fill
            pstmt.setInt(1, id);
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Delete room_equipment record from database with matching id
     *
     * @param id room_equipment's primary key
     */
    public void deleteRoomEquipment(int id) {
        try {
            // delete visitor with id
            PreparedStatement pstmt = this.c.prepareStatement(QueryController.deleteRoomEquipmentById);
            // fill
            pstmt.setInt(1, id);
            // run
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
