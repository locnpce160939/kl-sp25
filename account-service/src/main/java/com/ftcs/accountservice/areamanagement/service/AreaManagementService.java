package com.ftcs.accountservice.areamanagement.service;

import com.ftcs.accountservice.areamanagement.dto.AreaManagementRequestDTO;
import com.ftcs.accountservice.areamanagement.model.AreaManagement;
import com.ftcs.accountservice.areamanagement.repository.AreaManagementRepository;
import com.ftcs.accountservice.driver.identification.repository.AddressDriverRepository;
import com.ftcs.accountservice.driver.identification.repository.DriverIdentificationRepository;
import com.ftcs.accountservice.driver.management.dto.ListDriverDTO;
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
        List<Integer> provinceIds = requestDTO.getProvinceIds();

        if (provinceIds == null || provinceIds.isEmpty()) {
            throw new BadRequestException("Province IDs must be provided.");
        }

        for (Integer provinceId : provinceIds) {
            Province province = getProvinceById(provinceId);

            if (areaExists(accountId, provinceId)) {
                throw new BadRequestException("Area already exists for account ID: " + accountId + " and province ID: " + provinceId);
            }

            saveNewArea(accountId, provinceId);
        }
    }

    public void editArea(Integer accountId, Integer provinceId, AreaManagementRequestDTO updatedRequestDTO) {
        getProvinceById(provinceId);

        AreaManagement existingArea = getExistingArea(accountId, provinceId);

        Integer newProvinceId = getUpdatedProvinceId(updatedRequestDTO);

        if (newProvinceId != null) {
            Province newProvince = getProvinceById(newProvinceId);
        }

        if (areaExists(accountId, newProvinceId) && !existingArea.getProvinceId().equals(newProvinceId)) {
            throw new BadRequestException("Area with the new ProvinceId already exists for this account.");
        }

        updateArea(existingArea, newProvinceId);
    }

    private Province getProvinceById(Integer provinceId) {
        return provinceRepository.findById(provinceId)
                .orElseThrow(() -> new BadRequestException("Province not found for ID: " + provinceId));
    }

    private boolean areaExists(Integer accountId, Integer provinceId) {
        return areaManagementRepository.findByAccountIdAndProvinceId(accountId, provinceId).isPresent();
    }

    private void saveNewArea(Integer accountId, Integer provinceId) {
        AreaManagement areaManagement = AreaManagement.builder()
                .accountId(accountId)
                .provinceId(provinceId)
                .build();
        areaManagementRepository.save(areaManagement);
    }

    private AreaManagement getExistingArea(Integer accountId, Integer provinceId) {
        return areaManagementRepository.findByAccountIdAndProvinceId(accountId, provinceId)
                .orElseThrow(() -> new BadRequestException("Area not found for account ID: " + accountId + " and province ID: " + provinceId));
    }

    private Integer getUpdatedProvinceId(AreaManagementRequestDTO updatedRequestDTO) {
        if (updatedRequestDTO.getProvinceIds().size() != 1) {
            throw new BadRequestException("Invalid update request. Only one ProvinceId can be updated at a time.");
        }
        return updatedRequestDTO.getProvinceIds().get(0);
    }

    private void updateArea(AreaManagement existingArea, Integer newProvinceId) {
        existingArea.setProvinceId(newProvinceId);
        areaManagementRepository.save(existingArea);
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
        AreaManagement existingArea = getExistingArea(accountId, provinceId);
        areaManagementRepository.delete(existingArea);
    }

    public List<ListDriverDTO> getAllDriverDetails(Integer accountId) {
        List<Integer> provinceIds = getProvincesByAccountId(accountId);
        String provinceString = provinceIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return mapToListDriverDTO(driverRepository.getAllDriversByProvinces(provinceString), verificationDriverService);
    }
}
