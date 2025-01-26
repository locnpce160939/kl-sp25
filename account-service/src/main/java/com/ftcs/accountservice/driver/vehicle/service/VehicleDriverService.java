package com.ftcs.accountservice.driver.vehicle.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Service
@AllArgsConstructor
@Log4j2
public class VehicleDriverService {

    private final VehicleRepository vehicleRepository;
    private final FileService fileService;

    public void createNewVehicle(String requestDTOJson, Integer accountId, MultipartFile frontFile,
                                 MultipartFile backFile, FolderEnum folderEnum) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        VehicleRequestDTO requestDTO;
        try {
            requestDTO = objectMapper.readValue(requestDTOJson, VehicleRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid JSON in requestDTO");
        }

        String frontOriginalFileName = frontFile.getOriginalFilename();
        if (frontOriginalFileName == null) {
            throw new BadRequestException("Invalid front file name");
        }

        String backOriginalFileName = backFile.getOriginalFilename();
        if (backOriginalFileName == null) {
            throw new BadRequestException("Invalid back file name");
        }

        CompletableFuture<Void> frontUploadTask = fileService.processFileAsync(
                frontFile,
                frontOriginalFileName,
                folderEnum,
                frontUrl -> log.info("Front file uploaded successfully: {}", frontUrl)
        );

        CompletableFuture<Void> backUploadTask = fileService.processFileAsync(
                backFile,
                backOriginalFileName,
                folderEnum,
                backUrl -> log.info("Back file uploaded successfully: {}", backUrl)
        );

        CompletableFuture.allOf(frontUploadTask, backUploadTask).join();

        String frontViewUrl = fileService.getInternalMinio().getUrlFile();
        String backViewUrl = fileService.getInternalMinio().getUrlFile();

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
                .insuranceStatus(requestDTO.getInsuranceStatus())
                .registrationExpiryDate(requestDTO.getRegistrationExpiryDate())
                .frontView(frontViewUrl)
                .backView(backViewUrl)
                .build();
        vehicleRepository.save(newVehicle);
        log.info("Vehicle created successfully for accountId: {}, frontView: {}, backView: {}",
                accountId, frontViewUrl, backViewUrl);
    }

    public void updateVehicle(Integer accountId, String requestDTOJson, Integer vehicleId,
                              MultipartFile frontFile, MultipartFile backFile, FolderEnum folderEnum) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        VehicleRequestDTO requestDTO;
        try {
            requestDTO = objectMapper.readValue(requestDTOJson, VehicleRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid JSON in requestDTO");
        }

        Vehicle vehicle = findVehicleByVehicleId(vehicleId);
        validateAccountOwnership(accountId, vehicle);
        String[] frontViewUrl = new String[1];
        String[] backViewUrl = new String[1];

        if (frontFile != null) {
            String frontOriginalFileName = frontFile.getOriginalFilename();
            if (frontOriginalFileName == null) {
                throw new BadRequestException("Invalid front file name");
            }

            CompletableFuture<Void> frontUploadTask = fileService.processFileAsync(
                    frontFile,
                    frontOriginalFileName,
                    folderEnum,
                    frontUrl -> {
                        log.info("Front file uploaded successfully: {}", frontUrl);
                        frontViewUrl[0] = frontUrl;  // Assign value to array
                    }
            );

            frontUploadTask.join();
        }

        if (backFile != null) {
            String backOriginalFileName = backFile.getOriginalFilename();
            if (backOriginalFileName == null) {
                throw new BadRequestException("Invalid back file name");
            }

            CompletableFuture<Void> backUploadTask = fileService.processFileAsync(
                    backFile,
                    backOriginalFileName,
                    folderEnum,
                    backUrl -> {
                        log.info("Back file uploaded successfully: {}", backUrl);
                        backViewUrl[0] = backUrl;  // Assign value to array
                    }
            );

            backUploadTask.join();
        }

        updateVehicleDetails(vehicle, requestDTO);
        vehicle.setFrontView(frontViewUrl[0]);
        vehicle.setBackView(backViewUrl[0]);

        vehicleRepository.save(vehicle);
        log.info("Vehicle updated successfully for accountId: {}, frontView: {}, backView: {}",
                accountId, frontViewUrl[0], backViewUrl[0]);
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

    public void updateVehiclesByAccountId(Integer accountId, List<VehicleRequestDTO> requestDTOs) {
        List<Vehicle> vehicles = findVehiclesByAccountId(accountId);
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            VehicleRequestDTO requestDTO = requestDTOs.get(i);
            updateVehicleDetails(vehicle, requestDTO);
        }
        vehicleRepository.saveAll(vehicles);
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
