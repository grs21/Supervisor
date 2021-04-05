package com.grs21.supervisor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Apartment implements Serializable {
    private String uuid;
    private String apartmentName;
    private String apartmentAddress;
    private String cost;
    private String managerName;
    private String managerNumber;
    private String managerAddress;
    private String employeeName;
    private String employeeNumber;
    private String contractDate;
    private ArrayList<String> buildQrCodes;
    private String well;
    private String elevatorUp;
    private String machineRoom;
    private ArrayList<HashMap> serviceArrayList=new ArrayList<>();
    public Apartment(String uuid, String apartmentName, String apartmentAddress, String cost
            , String managerName, String managerNumber, String managerAddress, String employeeName
            , String employeeNumber, String contractDate, String well, String elevatorUp
            , String machineRoom,ArrayList<HashMap>services,ArrayList<String >buildQrCodes) {
        this.uuid = uuid;
        this.apartmentName = apartmentName;
        this.apartmentAddress = apartmentAddress;
        this.cost = cost;
        this.managerName = managerName;
        this.managerNumber = managerNumber;
        this.managerAddress = managerAddress;
        this.employeeName = employeeName;
        this.employeeNumber = employeeNumber;
        this.contractDate = contractDate;
        this.well = well;
        this.elevatorUp = elevatorUp;
        this.machineRoom = machineRoom;
        this.serviceArrayList=services;
        this.buildQrCodes=buildQrCodes;

    }
    public ArrayList<String> getBuildQrCodes() {
        return buildQrCodes;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getApartmentName() {
        return apartmentName;
    }

    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public String getApartmentAddress() {
        return apartmentAddress;
    }

    public void setApartmentAddress(String apartmentAddress) {
        this.apartmentAddress = apartmentAddress;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerNumber() {
        return managerNumber;
    }

    public void setManagerNumber(String managerNumber) {
        this.managerNumber = managerNumber;
    }

    public String getManagerAddress() {
        return managerAddress;
    }

    public void setManagerAddress(String managerAddress) {
        this.managerAddress = managerAddress;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getContractDate() {
        return contractDate;
    }

    public void setContractDate(String contractDate) {
        this.contractDate = contractDate;
    }

    public String getElevatorUp() {
        return elevatorUp;
    }


    public String getWell() {
        return well;
    }


    public String getMachineRoom() {
        return machineRoom;
    }


    public ArrayList<HashMap> getServiceArrayList() {
        return serviceArrayList;
    }

    @Override
    public String toString() {
        return apartmentName ;
    }
}
