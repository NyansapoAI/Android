package com.example.edward.nyansapo.db.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Student2 {

    public String local_id;
    public String cloud_id;
    public String firstname;
    public String lastname;
    public String age;
    public String gender;
    public String notes;
    public String timestamp;
    public String instructor_id;
    public String learning_level;
    public String std_class;


    // contructor

    public Student2() {
        //local_id = UUID.randomUUID().toString();
        //timestamp = new Date(System.currentTimeMillis()).toString();
    }





    // getters
    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getNotes() {
        return notes;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getInstructor_id() {
        return instructor_id;
    }

    public String getLearning_level() {
        return learning_level;
    }

    // setters

    public String getStd_class() {
        return std_class;
    }

    public void setStd_class(String std_class) {
        this.std_class = std_class;
    }


    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setInstructor_id(String instructor_id) {
        this.instructor_id = instructor_id;
    }

    public void setLearning_level(String learning_level) {
        this.learning_level = learning_level;
    }


    // id

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




}
