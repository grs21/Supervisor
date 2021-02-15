package com.grs21.supervisor.model;

public class Service {

    private String well;
    private String elevatorUp;
    private String machineRoom;
    private String date;
    private String employee;

    public Service(String well, String elevatorUp, String machineRoom, String date, String employee) {
        this.well = well;
        this.elevatorUp = elevatorUp;
        this.machineRoom = machineRoom;
        this.date = date;
        this.employee = employee;
    }

    public Service() {
    }

    public String getWell() {
        return well;
    }

    public void setWell(String well) {
        this.well = well;
    }

    public String getElevatorUp() {
        return elevatorUp;
    }

    public void setElevatorUp(String elevatorUp) {
        this.elevatorUp = elevatorUp;
    }

    public String getMachineRoom() {
        return machineRoom;
    }

    public void setMachineRoom(String machineRoom) {
        this.machineRoom = machineRoom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }
}
