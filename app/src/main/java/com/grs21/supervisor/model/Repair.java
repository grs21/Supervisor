package com.grs21.supervisor.model;

public class Repair {

    private Apartment apartment;
    private User user;
    private String notes;


    public Repair(Apartment apartment, User user, String notes) {
        this.apartment = apartment;
        this.user = user;
        this.notes = notes;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
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
}
