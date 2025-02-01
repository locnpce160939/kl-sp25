package com.ftcs.accountservice.driver.identification.dto;

import com.ftcs.accountservice.driver.shared.StatusDocumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusDriverIdentificationRequestDTO {
    private StatusDocumentType status;
}
