package com.ftcs.accountservice.driver.vehicle.dto;

import com.ftcs.accountservice.driver.shared.StatusDocumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusVehicleRequestDTO {
    private StatusDocumentType status;
}
