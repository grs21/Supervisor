package com.grs21.supervisor.model;

public class Repair {

    private String id;
    private String apartmentName;
    private User user;
    private String notes;
    private String date;

    public Repair(String apartmentName, User user, String notes,String date) {

        this.date=date;
        this.apartmentName = apartmentName;
        this.user = user;
        this.notes = notes;
    }

    public Repair() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getApartmentName() {
        return apartmentName;
    }

    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Repair{" +
                "id='" + id + '\'' +
                ", apartmentName='" + apartmentName + '\'' +
                ", user=" + user +
                ", notes='" + notes + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
