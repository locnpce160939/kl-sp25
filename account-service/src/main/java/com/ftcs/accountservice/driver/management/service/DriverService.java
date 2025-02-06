package com.ftcs.accountservice.driver.management.service;

import com.ftcs.accountservice.driver.management.dto.DriverVehicleDTO;
import com.ftcs.accountservice.driver.management.dto.ListDriverDTO;
import com.ftcs.accountservice.driver.management.projection.ListDriverProjection;
import com.ftcs.accountservice.driver.management.repository.DriverRepository;
import com.ftcs.accountservice.driver.verification.service.VerificationDriverService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ftcs.accountservice.driver.management.mapper.DriverMapper.mapToListDriverDTO;

@Service
@AllArgsConstructor
public class DriverService {
    private DriverRepository driverRepository;
    private final VerificationDriverService verificationDriverService;

    public List<ListDriverDTO> getAllDriverDetails() {
        return mapToListDriverDTO(driverRepository.getAllDrivers(), verificationDriverService);
    }
}
