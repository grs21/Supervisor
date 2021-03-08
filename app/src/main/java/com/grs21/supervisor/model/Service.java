package com.grs21.supervisor.model;

import java.util.function.BinaryOperator;

public class Service {

    private boolean well;
    private boolean elevatorUp;
    private boolean machineRoom;
    private String date;
    private String employee;

    public Service(boolean well, boolean elevatorUp, boolean machineRoom, String date, String employee) {
        this.well = well;
        this.elevatorUp = elevatorUp;
        this.machineRoom = machineRoom;
        this.date = date;
        this.employee = employee;
    }

    public Service() {
    }

    public boolean getWell() {
        return well;
    }

    public void setWell(boolean well) {
        this.well = well;
    }

    public boolean getElevatorUp() {
        return elevatorUp;
    }

    public void setElevatorUp(boolean elevatorUp) {
        this.elevatorUp = elevatorUp;
    }

    public boolean getMachineRoom() {
        return machineRoom;
    }

    public void setMachineRoom(boolean machineRoom) {
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

    @Override
    public String toString() {
        return date;
    }
}
