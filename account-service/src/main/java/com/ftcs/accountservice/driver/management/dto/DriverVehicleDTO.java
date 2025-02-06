package com.ftcs.accountservice.driver.management.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DriverVehicleDTO {
    private Integer vehicleId;
    private String licensePlate;
    private String vehicleType;
    private String vehicleMake;
    private String vehicleModel;
    private Integer vehicleYear;
    private Integer vehicleCapacity;
    private String vehicleDimensions;
    private String vehicleStatus;
    private Boolean vehicleVerified;
    private String vehicleFrontView;
    private String vehicleBackView;
}