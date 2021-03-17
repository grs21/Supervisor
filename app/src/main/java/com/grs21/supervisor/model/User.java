package com.grs21.supervisor.model;

import java.io.Serializable;

public class User implements Serializable {

        private String fullName;
        private String id;
        private String userName;
        private String password;
        private String accessLevel;
        private String company;
        private String phoneID;

    public User(String fullName, String id, String userName, String password, String accessLevel, String company
    ,String phoneID) {
        this.fullName = fullName;
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.accessLevel = accessLevel;
        this.company = company;
        this.phoneID=phoneID;
    }

    public String getPhoneID() {
        return phoneID;
    }

    public void setPhoneID(String phoneID) {
        this.phoneID = phoneID;
    }

    public User() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
