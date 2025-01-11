package com.ftcs.accountservice.driver.vehicle.service;

import com.ftcs.accountservice.driver.vehicle.dto.VehicleRequestDTO;
import com.ftcs.accountservice.driver.vehicle.model.Vehicle;
import com.ftcs.accountservice.driver.vehicle.repository.VehicleRepository;
import com.ftcs.common.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class VehicleDriverService {

    private final VehicleRepository vehicleRepository;

    public void createNewVehicle(VehicleRequestDTO requestDTO, Integer accountId) {
        if (vehicleRepository.existsByAccountId(accountId)) {
            throw new BadRequestException("Account already has a vehicle.");
        }
        Vehicle newVehicle = Vehicle.builder()
                .accountId(accountId)
                .licensePlate(requestDTO.getLicensePlate())
                .vehicleType(requestDTO.getVehicleType())
                .make(requestDTO.getMake())
                .model(requestDTO.getModel())
                .year(requestDTO.getYear())
                .capacity(requestDTO.getCapacity())
                .dimensions(requestDTO.getDimensions())
                .status("Pending")
                .isVerified(false)
                .insuranceStatus(requestDTO.getInsuranceStatus())
                .registrationExpiryDate(requestDTO.getRegistrationExpiryDate())
                .build();
        vehicleRepository.save(newVehicle);
    }

    public void updateVehicle(Integer accountId, VehicleRequestDTO requestDTO, Integer vehicleId) {
        Vehicle vehicle = findVehicleByVehicleId(vehicleId);
        validateAccountOwnership(accountId, vehicle);
        updateVehicleDetails(vehicle, requestDTO);
        vehicleRepository.save(vehicle);
    }

    public void updateVehicleByAccountId(Integer accountId, VehicleRequestDTO requestDTO) {
        Vehicle vehicle = findVehicleByAccountId(accountId);
        validateAccountOwnership(accountId, vehicle);
        updateVehicleDetails(vehicle, requestDTO);
        vehicleRepository.save(vehicle);
    }

    public Vehicle findVehicleByVehicleId(Integer vehicleId) {
        return vehicleRepository.findVehicleByVehicleId(vehicleId)
                .orElseThrow(() -> new BadRequestException("Vehicle not found"));
    }

    public Vehicle findVehicleByAccountId(Integer accountId) {
        return vehicleRepository.findVehicleByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Vehicle not found"));
    }

    private void updateVehicleDetails(Vehicle vehicle, VehicleRequestDTO requestDTO) {
        vehicle.setLicensePlate(requestDTO.getLicensePlate());
        vehicle.setVehicleType(requestDTO.getVehicleType());
        vehicle.setMake(requestDTO.getMake());
        vehicle.setModel(requestDTO.getModel());
        vehicle.setYear(requestDTO.getYear());
        vehicle.setCapacity(requestDTO.getCapacity());
        vehicle.setDimensions(requestDTO.getDimensions());
        vehicle.setInsuranceStatus(requestDTO.getInsuranceStatus());
        vehicle.setRegistrationExpiryDate(requestDTO.getRegistrationExpiryDate());
        vehicle.setUpdateAt(LocalDateTime.now());
    }

    public void validateAccountOwnership(Integer accountId, Vehicle vehicle) {
        if (!vehicle.getAccountId().equals(accountId)) {
            throw new BadRequestException("This vehicle does not belong to the specified account.");
        }
    }

}