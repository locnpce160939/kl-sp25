package com.ftcs.accountservice.driver.verification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusDocumentsDTO {
        private Boolean license;
        private Boolean vehicle;
        private Boolean identification;
}
