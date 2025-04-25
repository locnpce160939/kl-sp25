package com.ftcs.accountservice.areamanagement.service;

import com.ftcs.accountservice.areamanagement.dto.AreaManagementRequestDTO;
import com.ftcs.accountservice.areamanagement.model.AreaManagement;
import com.ftcs.accountservice.areamanagement.repository.AreaManagementRepository;
import com.ftcs.accountservice.driver.identification.repository.AddressDriverRepository;
import com.ftcs.accountservice.driver.identification.repository.DriverIdentificationRepository;
import com.ftcs.accountservice.driver.management.dto.ListDriverDTO;
import com.ftcs.accountservice.driver.management.projection.ListDriverProjection;
import com.ftcs.accountservice.driver.management.repository.DriverRepository;
import com.ftcs.accountservice.driver.vehicle.repository.VehicleRepository;
import com.ftcs.accountservice.driver.verification.service.VerificationDriverService;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.feature.location.repository.ProvinceRepository;
import com.ftcs.common.feature.location.model.Province;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ftcs.accountservice.driver.management.mapper.DriverMapper.mapToListDriverDTO;

@Service
@AllArgsConstructor
public class AreaManagementService {
    private final AreaManagementRepository areaManagementRepository;
    private final ProvinceRepository provinceRepository;
    private final DriverRepository driverRepository;
    private final VerificationDriverService verificationDriverService;

    public void addNewArea(Integer accountId, AreaManagementRequestDTO requestDTO) {
        List<Integer> newProvinceIds = requestDTO.getProvinceIds();

        if (newProvinceIds == null || newProvinceIds.isEmpty()) {
            throw new BadRequestException("Province IDs must be provided.");
        }

        // Validate all province IDs exist
        newProvinceIds.forEach(this::validateProvinceExists);

        // Get current areas for the account
        List<AreaManagement> currentAreas = areaManagementRepository.findByAccountId(accountId);
        List<Integer> currentProvinceIds = currentAreas.stream()
                .map(AreaManagement::getProvinceId)
                .collect(Collectors.toList());

        // Add new areas that don't exist yet
        List<AreaManagement> areasToAdd = newProvinceIds.stream()
                .filter(provinceId -> !currentProvinceIds.contains(provinceId))
                .map(provinceId -> AreaManagement.builder()
                        .accountId(accountId)
                        .provinceId(provinceId)
                        .build())
                .collect(Collectors.toList());

        if (!areasToAdd.isEmpty()) {
            areaManagementRepository.saveAll(areasToAdd);
        }
    }

    public void updateAreas(Integer accountId, AreaManagementRequestDTO requestDTO) {
        // Initialize empty list if null
        List<Integer> newProvinceIds = requestDTO.getProvinceIds() != null ? 
            requestDTO.getProvinceIds() : new ArrayList<>();

        // Validate province IDs if any exist
        if (!newProvinceIds.isEmpty()) {
            newProvinceIds.forEach(this::validateProvinceExists);
        }

        // Get current areas
        List<AreaManagement> currentAreas = areaManagementRepository.findByAccountId(accountId);
        
        if (newProvinceIds.isEmpty()) {
            // If new list is empty, remove all existing areas
            if (!currentAreas.isEmpty()) {
                areaManagementRepository.deleteAll(currentAreas);
            }
            return;
        }

        List<Integer> currentProvinceIds = currentAreas.stream()
                .map(AreaManagement::getProvinceId)
                .collect(Collectors.toList());

        List<AreaManagement> areasToRemove = currentAreas.stream()
                .filter(area -> !newProvinceIds.contains(area.getProvinceId()))
                .collect(Collectors.toList());

        // Areas to add - provinces that exist in new list but not in current
        List<AreaManagement> areasToAdd = newProvinceIds.stream()
                .filter(provinceId -> !currentProvinceIds.contains(provinceId))
                .map(provinceId -> AreaManagement.builder()
                        .accountId(accountId)
                        .provinceId(provinceId)
                        .build())
                .collect(Collectors.toList());

        // Perform the updates
        if (!areasToRemove.isEmpty()) {
            areaManagementRepository.deleteAll(areasToRemove);
        }
        if (!areasToAdd.isEmpty()) {
            areaManagementRepository.saveAll(areasToAdd);
        }
    }

    private void validateProvinceExists(Integer provinceId) {
        if (!provinceRepository.existsById(provinceId)) {
            throw new BadRequestException("Province not found for ID: " + provinceId);
        }
    }

    public List<Integer> getProvincesByAccountId(Integer accountId) {
        List<AreaManagement> areaManagementList = areaManagementRepository.findByAccountId(accountId);
        if (areaManagementList.isEmpty()) {
            return new ArrayList<>();
        }

        return areaManagementList.stream()
                .map(AreaManagement::getProvinceId)
                .collect(Collectors.toList());
    }

    public void deleteArea(Integer accountId, Integer provinceId) {
        AreaManagement existingArea = areaManagementRepository.findByAccountIdAndProvinceId(accountId, provinceId)
                .orElseThrow(() -> new BadRequestException("Area not found for account ID: " + accountId + " and province ID: " + provinceId));
        areaManagementRepository.delete(existingArea);
    }

    public List<ListDriverDTO> getAllDriverDetails(Integer accountId) {
        List<Integer> provinceIds = getProvincesByAccountId(accountId);
        String provinceString = provinceIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        List<ListDriverProjection> provinces = driverRepository.getAllDriversByProvinces(provinceString);
        return mapToListDriverDTO(provinces, verificationDriverService);
    }

    public void deleteAreas(Integer accountId, List<Integer> provinceIds) {
        List<AreaManagement> areasToDelete = provinceIds.stream()
                .map(provinceId -> areaManagementRepository.findByAccountIdAndProvinceId(accountId, provinceId)
                        .orElseThrow(() -> new BadRequestException("Area not found for account ID: " + accountId + " and province ID: " + provinceId)))
                .collect(Collectors.toList());
        areaManagementRepository.deleteAll(areasToDelete);
    }
}
