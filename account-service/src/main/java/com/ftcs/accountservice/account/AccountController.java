package com.ftcs.accountservice.account;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.account.dto.*;
import com.ftcs.accountservice.account.dto.register.RegisterConfirmRequestDTO;
import com.ftcs.accountservice.account.dto.register.RegisterRequestDTO;
import com.ftcs.accountservice.account.serivce.AccountService;
import com.ftcs.accountservice.account.serivce.ExportService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.ACCOUNT)
public class AccountController {

    private final AccountService accountService;
    private final ExportService exportService;

    @PostMapping("/register/send")
    public ApiResponse<?> registerSendUser(@Valid @RequestBody RegisterRequestDTO requestDTO, HttpServletRequest request) {
        accountService.registerSendUser(requestDTO, request);
        return new ApiResponse<>("Send mail success");
    }

    @PostMapping("/register/confirm")
    public ApiResponse<?> registerConfirmUser(@Valid @RequestBody RegisterConfirmRequestDTO requestDTO, HttpServletRequest request) {
        return new ApiResponse<>(accountService.registerConfirmUser(requestDTO, request));
    }

    @PutMapping("/isDisable/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<?> deleteAccount(@Valid @PathVariable("accountId") Integer accountId) {
        accountService.deleteAccount(accountId);
        return new ApiResponse<>("Delete account success");
    }

    @PostMapping("/createAccount")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'AREA_MANAGEMENT')")
    public ApiResponse<?> createNewAccount(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        return new ApiResponse<>(accountService.createNewAccount(requestDTO));
    }

    @GetMapping("/profile")
    public ApiResponse<?> getProfile(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(accountService.profile(accountId));
    }

    @PutMapping("/editProfile")
    public ApiResponse<?> editProfile(@Valid @RequestBody UpdateProfileRequestDTO requestDTO, @RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(accountService.updateProfile(requestDTO, accountId));
    }

    @GetMapping("/getAllAccount")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<?> getAllAccount(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(accountService.getAllAccounts(page, size));
    }

    @GetMapping("/getAccount/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<?> getAccount(@PathVariable("accountId") Integer accountId) {
        return new ApiResponse<>(accountService.getAccountById(accountId));
    }

    @PostMapping("/forgotSend")
    public ApiResponse<?> forgotSend(@Valid @RequestBody ForgotPasswordAccountRequestDTO requestDTO, HttpServletRequest request) {
        accountService.forgotPasswordAccountSend(requestDTO, request);
        return new ApiResponse<>("Send mail success");
    }

    @PostMapping("/forgotConfirm")
    public ApiResponse<?> forgotConfirm(@Valid @RequestBody ForgotPasswordAccountRequestDTO requestDTO, HttpServletRequest request) {
        accountService.forgotPasswordAccountConfirm(requestDTO, request);
        return new ApiResponse<>("Change password success");
    }

    @PostMapping("/changePassword")
    public ApiResponse<?> changePassword(@Valid @RequestBody ChangePasswordRequestDTO requestDTO, @RequestAttribute("accountId") Integer accountId) {
        accountService.changePasswordAccount(requestDTO, accountId);
        return new ApiResponse<>("Change password success");
    }

    @PostMapping("/findByRole")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<?> findByRole(@Valid @RequestBody FilterRequestDTO requestDTO,
                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(accountService.findAllByRole(requestDTO.getRole(), page, size));
    }

    @PutMapping("/editAccount/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<?> editAccount(@Valid @RequestBody RegisterRequestDTO requestDTO,
                                      @PathVariable("accountId") Integer accountId) {
        return new ApiResponse<>(accountService.updateAccount(accountId, requestDTO));
    }

    @GetMapping("/export-excel/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ResponseEntity<byte[]> exportAccountToExcel(@PathVariable("accountId") Integer accountId) {
        try {
            byte[] excelBytes = exportService.exportAccountToExcel(accountId);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "account_" + accountId + "_" + timestamp + ".xlsx";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
