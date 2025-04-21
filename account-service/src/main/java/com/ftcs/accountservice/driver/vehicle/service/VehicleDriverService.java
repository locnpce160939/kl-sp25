package com.ftcs.accountservice.driver.vehicle.service;

import com.ftcs.accountservice.driver.shared.StatusDocumentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.accountservice.driver.vehicle.dto.UpdateStatusVehicleRequestDTO;
import com.ftcs.accountservice.driver.vehicle.dto.VehicleRequestDTO;
import com.ftcs.accountservice.driver.vehicle.model.Vehicle;
import com.ftcs.accountservice.driver.vehicle.repository.VehicleRepository;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.upload.FileService;
import com.ftcs.common.upload.FolderEnum;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
@Log4j2
public class VehicleDriverService {

    private final VehicleRepository vehicleRepository;
    private final FileService fileService;

    public void createNewVehicle(VehicleRequestDTO requestDTO, Integer accountId, MultipartFile frontFile,
                                 MultipartFile backFile) {
        validate(requestDTO);
        Vehicle vehicle = Vehicle.builder()
                .accountId(accountId)
                .licensePlate(requestDTO.getLicensePlate())
                .vehicleType(requestDTO.getVehicleType())
                .make(requestDTO.getMake())
                .model(requestDTO.getModel())
                .year(requestDTO.getYear())
                .capacity(requestDTO.getCapacity())
                .dimensions(requestDTO.getDimensions())
                .status(StatusDocumentType.NEW)
                .insuranceStatus(requestDTO.getInsuranceStatus())
                .registrationExpiryDate(requestDTO.getRegistrationExpiryDate())
                .build();

        if (frontFile != null) {
            handleFileUpload(frontFile, vehicle::setFrontView);
        }

        if (backFile != null) {
            handleFileUpload(backFile, vehicle::setBackView);
        }

        vehicleRepository.save(vehicle);
    }

    public void updateVehicle(VehicleRequestDTO requestDTO, Integer accountId,
                              MultipartFile frontFile, MultipartFile backFile) {

        Vehicle vehicle = findVehicleByVehicleId(requestDTO.getVehicleId());
        validate(requestDTO);
        validateAccountOwnership(accountId, vehicle);

        if (frontFile != null) {
            handleFileUpload(frontFile, vehicle::setFrontView);
            handleFileDelete(vehicle.getFrontView());
        }

        if (backFile != null) {
            handleFileUpload(backFile, vehicle::setBackView);
            handleFileDelete(vehicle.getBackView());
        }

        updateVehicleDetails(vehicle, requestDTO);
        vehicleRepository.save(vehicle);
    }

    public void updateStatus(Integer vehicleId, UpdateStatusVehicleRequestDTO requestDTO){
        Vehicle vehicle = findVehicleByVehicleId(vehicleId);
        vehicle.setStatus(requestDTO.getStatus());
        vehicleRepository.save(vehicle);
    }

    private void handleFileUpload(MultipartFile file, Consumer<String> callback) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new BadRequestException("Invalid file name");
        }

        CompletableFuture<Void> uploadTask = fileService.processFileAsync(
                file,
                originalFileName,
                FolderEnum.VEHICLE_DRIVER,
                callback
        );

        uploadTask.join();
    }

    private void handleFileDelete(String fileName) {
        fileService.processDeleteFile(fileName, FolderEnum.VEHICLE_DRIVER);
    }

    public List<Vehicle> findVehiclesByAccountId(Integer accountId) {
        List<Vehicle> vehicles = vehicleRepository.findVehiclesByAccountId(accountId);
        if (vehicles.isEmpty()) {
            throw new BadRequestException("No vehicles found for this account.");
        }
        return vehicles;
    }

    public Vehicle findVehicleByVehicleId(Integer vehicleId) {
        return vehicleRepository.findVehicleByVehicleId(vehicleId)
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
        vehicle.setStatus(StatusDocumentType.NEW);
    }

    public void validateAccountOwnership(Integer accountId, Vehicle vehicle) {
        if (!vehicle.getAccountId().equals(accountId)) {
            throw new BadRequestException("This vehicle does not belong to the specified account.");
        }
    }

    public List<Vehicle> getVehicleApproved(Integer accountId){
        return vehicleRepository.findByAccountIdAndStatus(accountId, StatusDocumentType.APPROVED);
    }

    private void validate(VehicleRequestDTO requestDTO) {
        if(requestDTO.getRegistrationExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Registration expiry date must not be in the past.");
        }
    }
}
