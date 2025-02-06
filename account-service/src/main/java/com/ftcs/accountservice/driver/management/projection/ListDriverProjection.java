package com.ftcs.accountservice.driver.management.projection;

import java.time.LocalDateTime;

public interface ListDriverProjection {
    Integer getAccountId();
    String getUsername();
    String getEmail();
    String getPhone();
    String getRole();
    String getProfilePicture();
    LocalDateTime getLastLogin();
    String getAccountStatus();

    Integer getDriverIdentificationId();
    String getDriverIDNumber();
    String getDriverFullName();
    String getDriverGender();
    LocalDateTime getDriverBirthday();
    String getDriverCountry();
    String getPermanentAddress();
    String getTemporaryAddress();
    LocalDateTime getDriverIDIssueDate();
    LocalDateTime getDriverIDExpiryDate();
    String getDriverIDIssuedBy();
    Boolean getDriverIDVerified();
    String getDriverIDFrontView();
    String getDriverIDBackView();
    String getDriverIDStatus();



    String getLicenseNumber();
    String getLicenseType();
    LocalDateTime getLicenseIssuedDate();
    LocalDateTime getLicenseExpiryDate();
    String getIssuingAuthority();
    String getLicenseStatus();
    Integer getLicenseId();
    String getLicenseFrontView();
    String getLicenseBackView();

    Integer getVehicleId();
    String getLicensePlate();
    String getVehicleType();
    String getVehicleMake();
    String getVehicleModel();
    Integer getVehicleYear();
    Integer getVehicleCapacity();
    String getVehicleDimensions();
    String getVehicleStatus();
    Boolean getVehicleVerified();
    String getVehicleFrontView();
    String getVehicleBackView();


    Integer getAddressDriverId();
    String getStreetAddress();
    String getWardName();
    String getDistrictName();
    Integer getProvinceId();
    String getAddressType();
    String getAddressNotes();

    LocalDateTime getAccountCreatedAt();
    LocalDateTime getAccountUpdatedAt();
}
