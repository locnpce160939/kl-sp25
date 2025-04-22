package com.ftcs.accountservice.driver.identification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.driver.identification.dto.DriverIdentificationRequestDTO;
import com.ftcs.accountservice.driver.identification.dto.UpdateStatusDriverIdentificationRequestDTO;
import com.ftcs.accountservice.driver.identification.service.DriverIdentificationService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.DRIVER_REGISTER)
public class IdentificationDriverController {
    private final ObjectMapper objectMapper;
    private final DriverIdentificationService driverIdentificationService;

    @PostMapping("/identification")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> createDriverIdentification(@Valid @RequestPart("requestDTO") String requestDTO,
                                                     @RequestPart("frontFile") MultipartFile frontFile,
                                                     @RequestPart("backFile") MultipartFile backFile,
                                                     @RequestAttribute("accountId") Integer accountId) throws JsonProcessingException {
        driverIdentificationService.addDriverIdentification(objectMapper.readValue(requestDTO, DriverIdentificationRequestDTO.class), accountId, frontFile, backFile);
        return new ApiResponse<>("Created driver identification successfully");
    }

    @PutMapping("/identification")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateDriverIdentificationByAccountId(@Valid @RequestPart("requestDTO") String requestDTO,
                                                                @RequestPart(value = "frontFile", required = false) MultipartFile frontFile,
                                                                @RequestPart(value = "backFile", required = false) MultipartFile backFile,
                                                                @RequestAttribute("accountId") Integer accountId) throws JsonProcessingException {
        driverIdentificationService.updateDriverIdentification(objectMapper.readValue(requestDTO, DriverIdentificationRequestDTO.class), accountId, frontFile, backFile);
        return new ApiResponse<>("Updated driver identification successfully");
    }

    @GetMapping("/identification/{driverIdentificationId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getById(@PathVariable("driverIdentificationId") Integer driverIdentificationId) {
        return new ApiResponse<>(driverIdentificationService.findDriverIdentificationByDriverIdentificationId(driverIdentificationId));
    }

    @GetMapping("/identification")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getByAccountId(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(driverIdentificationService.findDriverIdentification(accountId));
    }

    @PutMapping("/identification/status/{driverIdentificationId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'AREA_MANAGEMENT')")
    public ApiResponse<?> updateStatus(@Valid @RequestBody UpdateStatusDriverIdentificationRequestDTO requestDTO,
                                       @PathVariable("driverIdentificationId") Integer driverIdentificationId ) {
        driverIdentificationService.updateStatus(driverIdentificationId, requestDTO);
        return new ApiResponse<>("Updated driver identification successfully");
    }
}