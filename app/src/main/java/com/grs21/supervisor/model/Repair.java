package com.grs21.supervisor.model;

public class Repair {

    private String apartmentName;
    private User user;
    private String notes;

    public Repair() {
    }

    public Repair(String apartmentName, User user, String notes) {
        this.apartmentName = apartmentName;
        this.user = user;
        this.notes = notes;
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

    @Override
    public String toString() {
        return "Repair{" +
                "apartmentName='" + apartmentName + '\'' +
                ", user=" + user +
                ", notes='" + notes + '\'' +
                '}';
    }
}
