package com.ftcs.accountservice.driver.license;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.driver.identification.dto.DriverIdentificationRequestDTO;
import com.ftcs.accountservice.driver.license.dto.LicenseRequestDTO;
import com.ftcs.accountservice.driver.license.dto.UpdateStatusLicenseRequestDTO;
import com.ftcs.accountservice.driver.license.service.LicenseDriverService;
import com.ftcs.common.dto.ApiResponse;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.upload.FolderEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.DRIVER_REGISTER)
public class LicenseDriverController {
    private final ObjectMapper objectMapper;
    private final LicenseDriverService licenseDriverService;

    @PostMapping("/license")
    public ApiResponse<?> createNewLicense(
            @Valid @RequestPart("requestDTO") String licenseRequestDTO,
            @RequestAttribute("accountId") Integer accountId,
            @RequestPart("frontFile") MultipartFile frontFile,
            @RequestPart("backFile") MultipartFile backFile) throws JsonProcessingException {
        licenseDriverService.createNewLicense(objectMapper.readValue(licenseRequestDTO, LicenseRequestDTO.class), accountId, frontFile, backFile);
        return new ApiResponse<>("Created license successfully");
    }

    @PutMapping("/license/{licenseId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateLicense(@Valid @RequestBody LicenseRequestDTO requestDTO,
                                        @PathVariable("licenseId") Integer licenseId,
                                        @RequestAttribute("accountId") Integer accountId) {
        licenseDriverService.updateLicense(accountId, requestDTO, licenseId);
        return new ApiResponse<>("Updated license successfully");
    }

    @PutMapping("/license")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateLicenseByAccountId(@RequestPart("requestDTO") String licenseRequestDTO,
                                                   @RequestAttribute("accountId") Integer accountId,
                                                   @RequestPart(value = "frontFile", required = false) MultipartFile frontFile,
                                                   @RequestPart(value = "backFile", required = false) MultipartFile backFile) throws JsonProcessingException {

        licenseDriverService.updateLicenseByAccountId(objectMapper.readValue(licenseRequestDTO, LicenseRequestDTO.class), accountId , frontFile, backFile);
        return new ApiResponse<>("Updated license successfully");
    }

    @GetMapping("/license/{licenseId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getLicenseById(@PathVariable("licenseId") Integer licenseId) {
        return new ApiResponse<>(licenseDriverService.findLicenseByLicenseId(licenseId));
    }

    @GetMapping("/license")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getLicenseByAccountId(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(licenseDriverService.findLicenseByAccountId(accountId));
    }

    @PutMapping("/license/status/{licenseId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'AREA_MANAGEMENT')")
    public ApiResponse<?> updateStatus(@Valid @RequestBody UpdateStatusLicenseRequestDTO requestDTO,
                                       @PathVariable("licenseId") Integer licenseId){
        licenseDriverService.updateStatus(licenseId, requestDTO);
        return new ApiResponse<>("Updated license successfully");
    }

}