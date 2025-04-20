package com.ftcs.accountservice.driver.identification.service;

import com.ftcs.accountservice.driver.identification.dto.*;
import com.ftcs.accountservice.driver.identification.model.DriverIdentification;
import com.ftcs.accountservice.driver.identification.repository.DriverIdentificationRepository;
import com.ftcs.accountservice.driver.shared.AddressType;
import com.ftcs.accountservice.driver.shared.StatusDocumentType;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.upload.FileService;
import com.ftcs.common.upload.FolderEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
public class DriverIdentificationService {

    private final DriverIdentificationRepository driverIdentificationRepository;
    private final AddressIdentificationService addressIdentificationService;
    private final FileService fileService;

    public void addDriverIdentification(DriverIdentificationRequestDTO requestDTO, Integer accountId, MultipartFile frontFile,
                                        MultipartFile backFile) {
        if (driverIdentificationRepository.existsByAccountId(accountId)) {
            throw new BadRequestException("Account already has a driver identification");
        }
        Integer permanentAddressId = addressIdentificationService.addAddressDriver(createAddressDriverRequestDTO(requestDTO, AddressType.PERMANENT, true));
        Integer temporaryAddressId = addressIdentificationService.addAddressDriver(createAddressDriverRequestDTO(requestDTO, AddressType.TEMPORARY, false));

        DriverIdentification identification = createNewDriverIdentification(accountId);

        if (frontFile != null) {
            handleFileUpload(frontFile, identification::setFrontView);
        }

        if (backFile != null) {
            handleFileUpload(backFile, identification::setBackView);
        }

        updateDriverIdentificationDetails(identification, requestDTO, permanentAddressId, temporaryAddressId);
        driverIdentificationRepository.save(identification);
    }

    public void updateDriverIdentification(DriverIdentificationRequestDTO requestDTO, Integer accountId, MultipartFile frontFile,
                                           MultipartFile backFile) {
        DriverIdentification identification = findDriverIdentificationByAccountIdOptional(accountId);
        validateAccountOwnership(accountId, identification);

        Integer permanentAddressId = identification.getPermanentAddress();
        Integer temporaryAddressId = identification.getTemporaryAddress();

        if (permanentAddressId != null) {
            addressIdentificationService.updateAddressDriver(permanentAddressId, createAddressDriverRequestDTO(requestDTO, AddressType.PERMANENT, true));
        }
        if (temporaryAddressId != null) {
            addressIdentificationService.updateAddressDriver(temporaryAddressId, createAddressDriverRequestDTO(requestDTO, AddressType.TEMPORARY, false));
        }

        if (frontFile != null) {
            handleFileUpload(frontFile, identification::setFrontView);
            handleFileDelete(identification.getFrontView());
        }

        if (backFile != null) {
            handleFileUpload(backFile, identification::setBackView);
            handleFileDelete(identification.getBackView());
        }

        updateDriverIdentificationDetails(identification, requestDTO, permanentAddressId, temporaryAddressId);
        driverIdentificationRepository.save(identification);
    }

    private DriverIdentification createNewDriverIdentification(Integer accountId) {
        return DriverIdentification.builder()
                .accountId(accountId)
                .status(StatusDocumentType.NEW)
                .build();
    }

    private void updateDriverIdentificationDetails(DriverIdentification identification, DriverIdentificationRequestDTO requestDTO, Integer permanentAddressId, Integer temporaryAddressId) {
        identification.setIdNumber(requestDTO.getIdNumber());
        identification.setFullName(requestDTO.getFullName());
        identification.setGender(requestDTO.getGender());
        identification.setBirthday(requestDTO.getBirthday());
        identification.setCountry(requestDTO.getCountry());
        identification.setPermanentAddress(permanentAddressId);
        identification.setTemporaryAddress(temporaryAddressId);
        identification.setIssueDate(requestDTO.getIssueDate());
        identification.setExpiryDate(requestDTO.getExpiryDate());
        identification.setIssuedBy(requestDTO.getIssuedBy());
        identification.setUpdateAt(LocalDateTime.now());
        identification.setStatus(StatusDocumentType.NEW);
    }

    private AddressDriverRequestDTO createAddressDriverRequestDTO(DriverIdentificationRequestDTO requestDTO, AddressType addressType, boolean isPermanent) {
        return new AddressDriverRequestDTO(
                isPermanent ? requestDTO.getPermanentAddressWard() : requestDTO.getTemporaryAddressWard(),
                isPermanent ? requestDTO.getPermanentAddressDistrict() : requestDTO.getTemporaryAddressDistrict(),
                isPermanent ? requestDTO.getPermanentAddressProvince() : requestDTO.getTemporaryAddressProvince(),
                isPermanent ? requestDTO.getPermanentStreetAddress() : requestDTO.getTemporaryStreetAddress(),
                addressType
        );
    }

    public DriverIdentification findDriverIdentificationByDriverIdentificationId(Integer driverIdentificationId) {
        return driverIdentificationRepository.findDriverIdentificationByDriverIdentificationId(driverIdentificationId)
                .orElseThrow(() -> new BadRequestException("Driver Identification not found"));
    }

    public DriverIdentification findDriverIdentificationByAccountIdOptional(Integer accountId) {
        return driverIdentificationRepository.findDriverIdentificationByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Driver Identification not found"));
    }

    public DriverIdentificationResponseDTO findDriverIdentification(Integer accountId) {
        DriverIdentification identification = findDriverIdentificationByAccountIdOptional(accountId);

        if (identification == null) {
            return new DriverIdentificationResponseDTO();
        }

        AddressDriverResponseDTO permanentAddressDTO = null;
        AddressDriverResponseDTO temporaryAddressDTO = null;

        if (identification.getPermanentAddress() != null) {
            permanentAddressDTO = addressIdentificationService.mapToAddressDriverResponseDTOWithName(
                    addressIdentificationService.getAddressDriverById(identification.getPermanentAddress())
            );
        }

        if (identification.getTemporaryAddress() != null) {
            temporaryAddressDTO = addressIdentificationService.mapToAddressDriverResponseDTOWithName(
                    addressIdentificationService.getAddressDriverById(identification.getTemporaryAddress())
            );
        }

        return DriverIdentificationResponseDTO.builder()
                .driverIdentificationId(identification.getDriverIdentificationId())
                .fullName(identification.getFullName())
                .gender(identification.getGender())
                .birthday(identification.getBirthday())
                .country(identification.getCountry())
                .accountId(identification.getAccountId())
                .idNumber(identification.getIdNumber())
                .status(identification.getStatus())
                .issueDate(identification.getIssueDate())
                .expiryDate(identification.getExpiryDate())
                .issuedBy(identification.getIssuedBy())
                .permanentAddress(permanentAddressDTO)
                .temporaryAddress(temporaryAddressDTO)
                .frontView(identification.getFrontView())
                .backView(identification.getBackView())
                .build();
    }

    public void updateStatus(Integer driverIdentificationId, UpdateStatusDriverIdentificationRequestDTO requestDTO) {
        DriverIdentification driverIdentification = findDriverIdentificationByDriverIdentificationId(driverIdentificationId);
        driverIdentification.setStatus(requestDTO.getStatus());
        driverIdentificationRepository.save(driverIdentification);
    }

    private void handleFileUpload(MultipartFile file, Consumer<String> callback) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new BadRequestException("Invalid file name");
        }

        CompletableFuture<Void> uploadTask = fileService.processFileAsync(
                file,
                originalFileName,
                FolderEnum.IDENTIFICATION_DRIVER,
                callback
        );

        uploadTask.join();
    }

    private void handleFileDelete(String fileName) {
        fileService.processDeleteFile(fileName, FolderEnum.IDENTIFICATION_DRIVER);
    }

    private void validateAccountOwnership(Integer accountId, DriverIdentification identification) {
        if (!identification.getAccountId().equals(accountId)) {
            throw new BadRequestException("This driver identification does not belong to the specified account.");
        }
    }
}