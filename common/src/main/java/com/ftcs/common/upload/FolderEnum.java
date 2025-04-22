package com.ftcs.common.upload;

import lombok.Getter;

@Getter
public enum FolderEnum {
    LICENSE_DRIVER("-license", "./uploads/driver/license"),
    IDENTIFICATION_DRIVER("-identification", "./uploads/driver/identification"),
    VEHICLE_DRIVER("-vehicle", "./uploads/driver/vehicle"),
    INSURANCE_CLAIM("-insurance", "./uploads/insurance"),
    ;

    private final String folderName;
    private final String localPath;

    FolderEnum(String folderName, String localPath) {
        this.folderName = folderName;
        this.localPath = localPath;
    }
}
