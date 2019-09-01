package main.java.Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Visitor {
    private int id;
    private String name;
    private String email;
    private Company company;
    private Meeting meeting;
    private Timestamp check_in;
    private Timestamp check_out;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public Timestamp getCheck_in() {
        return check_in;
    }

    public void setCheck_in(Timestamp check_in) {
        this.check_in = check_in;
    }

    public Timestamp getCheck_out() {
        return check_out;
    }

    public void setCheck_out(Timestamp check_out) {
        this.check_out = check_out;
    }

    /**
     * Extracting data from database record and return model
     *
     * @param rs database record
     * @return model
     * @throws SQLException not handled in static method
     */
    public static Visitor parseModel(ResultSet rs) throws SQLException {
        // extracting data
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        Timestamp check_in = rs.getTimestamp("check_in");
        Timestamp check_out = rs.getTimestamp("check_out");

        // sub company model
        Company company = new Company();
        company.setId(rs.getInt("company_id"));
        company.setName(rs.getString("company_name"));
        // sub meeting model
        Meeting meeting = new Meeting();
        meeting.setId(rs.getInt("meeting_id"));
        meeting.setDate(rs.getTimestamp("meeting_date"));

        // sub room model for meeting
        Room meeting_room = new Room();
        meeting_room.setId(rs.getInt("meeting_id"));
        meeting_room.setName(rs.getString("room_name"));
        meeting.setRoom(meeting_room);
        // sub user model for meeting
        User meeting_host = new User();
        meeting_host.setId(rs.getInt("user_id"));
        meeting_host.setName(rs.getString("user_name"));
        meeting_host.setEmail(rs.getString("user_email"));
        meeting_host.setRole(rs.getInt("role"));
        meeting_host.setToken(rs.getInt("token"));
        meeting_host.setPassword(rs.getString("password"));
        meeting.setUser(meeting_host);

        Visitor visitor = new Visitor();
        visitor.setId(id);
        visitor.setName(name);
        visitor.setEmail(email);
        visitor.setCheck_in(check_in);
        visitor.setCheck_out(check_out);
        visitor.setMeeting(meeting);
        visitor.setCompany(company);

        return visitor;
    }
}
