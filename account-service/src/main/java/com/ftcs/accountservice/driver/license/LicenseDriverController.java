package com.ftcs.accountservice.driver.license;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.driver.license.dto.LicenseRequestDTO;
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

    private final LicenseDriverService licenseDriverService;

    @PostMapping("/createNewLicense")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> createNewLicense(@Valid @RequestPart("requestDTO") String requestDTOJson,
                                           @RequestAttribute("accountId") Integer accountId,
                                           @RequestParam("frontFile") MultipartFile frontFile,
                                           @RequestParam("backFile") MultipartFile backFile,
                                           @RequestParam("folder") FolderEnum folderEnum) {
        licenseDriverService.createNewLicense(requestDTOJson, accountId, frontFile, backFile, folderEnum);
        return new ApiResponse<>("Created license successfully");
    }

    @PutMapping("/updateLicense/{licenseId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateLicense(@Valid @RequestBody LicenseRequestDTO requestDTO,
                                        @PathVariable("licenseId") Integer licenseId,
                                        @RequestAttribute("accountId") Integer accountId) {
        licenseDriverService.updateLicense(accountId, requestDTO, licenseId);
        return new ApiResponse<>("Updated license successfully");
    }

    @PutMapping("/updateLicense")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateLicenseByAccountId(@RequestPart("requestDTO") String requestDTOJson,
                                                   @RequestAttribute("accountId") Integer accountId,
                                                   @RequestParam(value = "frontFile", required = false) MultipartFile frontFile,
                                                   @RequestParam(value = "backFile", required = false) MultipartFile backFile,
                                                   @RequestParam(value = "folder", required = false) FolderEnum folderEnum) {

        licenseDriverService.updateLicenseByAccountId(accountId, requestDTOJson, frontFile, backFile, folderEnum);
        return new ApiResponse<>("Updated license successfully");
    }



    @GetMapping("/license/getById/{licenseId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getLicenseById(@PathVariable("licenseId") Integer licenseId) {
        return new ApiResponse<>(licenseDriverService.findLicenseByLicenseId(licenseId));
    }

    @GetMapping("/license/getByAccountId")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getLicenseByAccountId(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(licenseDriverService.findLicenseByAccountId(accountId));
    }

}