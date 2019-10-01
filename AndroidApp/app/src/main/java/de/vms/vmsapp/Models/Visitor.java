package de.vms.vmsapp.Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Visitor {
    private int id;
    private String name;
    private String email;
    private String tel;
    private Company company;
    private Meeting meeting;
    private Date check_in;
    private Date check_out;

    public Visitor() {
        //
    }

    public Visitor(int id, String name, String email, String tel, Company company, Meeting meeting, String check_in, String check_out) {
        this.setId(id);
        this.setName(name);
        this.setEmail(email);
        this.setTel(tel);
        this.setCompany(company);
        this.setMeeting(meeting);
        this.setCheck_in(check_in);
        this.setCheck_out(check_out);
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
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

    public Date getCheck_in() {
        return check_in;
    }

    public void setCheck_in(String check_in) {
        Date parsedDate = null;
        // set format for date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // parse date
            parsedDate = dateFormat.parse(check_in);
            this.check_in = parsedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getCheck_out() {
        return check_out;
    }

    public void setCheck_out(String check_out) {
        Date parsedDate = null;
        // set format for date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // parse
            parsedDate = dateFormat.parse(check_out);
            this.check_out = parsedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if visitor is checked in
     *  true if checked in
     *  false if not
     *
     * @return boolean
     */
    public boolean isChecked_In() {
        return this.check_in != null;
    }

    /**
     * Check if user is checked out
     *  true if checked out
     *  false if not
     *
     * @return boolean
     */
    public boolean isChecked_Out() {
        return this.check_out != null;
    }
}
