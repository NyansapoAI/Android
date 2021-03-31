package com.nyansapo;

public class Instructor {
    public String local_id;
    public String cloud_id;
    public String firstname;
    public String lastname;
    public String timestamp;
    public String email;
    public String password;


    // constructor
    public Instructor() {
    }

    public Instructor(String local_id, String cloud_id, String firstname, String lastname, String timestamp, String email, String password) {
        this.local_id = local_id;
        this.cloud_id = cloud_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.timestamp = timestamp;
        this.email = email;
        this.password = password;
    }

    // getters and setters
    public String getLocal_id() {
        return local_id;
    }

    public void setLocal_id(String local_id) {
        this.local_id = local_id;
    }

    public String getCloud_id() {
        return cloud_id;
    }

    public void setCloud_id(String cloud_id) {
        this.cloud_id = cloud_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



}
