package com.ftcs.balanceservice.withdraw;

import com.ftcs.balanceservice.BalanceURL;
import com.ftcs.balanceservice.withdraw.constant.WithdrawStatus;
import com.ftcs.balanceservice.withdraw.dto.BatchUpdateRequestDTO;
import com.ftcs.balanceservice.withdraw.dto.WithdrawExportDTO;
import com.ftcs.balanceservice.withdraw.dto.WithdrawTotalExportDTO;
import com.ftcs.balanceservice.withdraw.service.ExcelExportService;
import com.ftcs.common.dto.ApiResponse;
import com.ftcs.balanceservice.withdraw.dto.WithdrawRequestDTO;
import com.ftcs.balanceservice.withdraw.model.Withdraw;
import com.ftcs.balanceservice.withdraw.service.WithdrawService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(BalanceURL.WITH_DRAW)
public class WithdrawController {
    private final WithdrawService withdrawService;
    private final ExcelExportService excelExportService;

    @GetMapping()
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<Page<Withdraw>> getAllWithdraws(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(withdrawService.getAll(page, size));
    }

    @GetMapping("/{withdrawId}")
    public ApiResponse<Withdraw> getWithdrawById(@PathVariable("withdrawId") Long withdrawId) {
        return new ApiResponse<>(withdrawService.findWithdrawById(withdrawId));
    }

    @PostMapping()
    public ApiResponse<String> createWithdraw(@RequestAttribute("accountId") Integer accountId,
                                              @RequestBody @Valid WithdrawRequestDTO requestDTO) {
        withdrawService.createWithdraw(requestDTO, accountId);
        return new ApiResponse<>("Withdraw request created successfully");
    }

    @PutMapping("/{withdrawId}")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<String> updateStatusWithdraw(@PathVariable("withdrawId") Long withdrawId,
                                                    @RequestBody @Valid WithdrawRequestDTO requestDTO) {
        withdrawService.updateStatusWithdraw(requestDTO, withdrawId);
        return new ApiResponse<>("Withdraw status updated successfully");
    }

    @DeleteMapping("/{withdrawId}")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<String> deleteWithdraw(@PathVariable("withdrawId") Long withdrawId) {
        withdrawService.deleteWithdraw(withdrawId);
        return new ApiResponse<>("Withdraw request deleted successfully");
    }

    @GetMapping("/account")
    public ApiResponse<List<Withdraw>> getAllWithdrawsByAccount(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(withdrawService.getAllByAccountId(accountId));
    }

    @GetMapping("/management/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<List<Withdraw>> getAllWithdrawsManagement(@PathVariable("accountId") Integer accountId) {
        return new ApiResponse<>(withdrawService.getAllByAccountIdManagement(accountId));
    }

    @PutMapping("/driver/{withdrawId}")
    public ApiResponse<?> updateForDriver(@PathVariable("withdrawId") Long withdrawId,
                                          @RequestBody @Valid WithdrawRequestDTO requestDTO,
                                          @RequestAttribute("accountId") Integer accountId) {
        withdrawService.updateForDriver(requestDTO, withdrawId, accountId);
        return new ApiResponse<>("Withdraw request updated successfully");
    }

    @GetMapping("/status")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<Page<Withdraw>> getAllWithdrawsByStatus(@Valid @RequestParam("status") WithdrawStatus status,
                                                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(withdrawService.getAllByStatusManagement(status, page, size));
    }

    @GetMapping("/requestDate")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<Page<Withdraw>> getAllWithdrawsByRequestDate(
            @RequestParam("requestDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate requestDate,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(withdrawService.getAllByRequestDateManagement(requestDate, page, size));
    }

//    @GetMapping("/export/{withdrawId}")
//    public ApiResponse<WithdrawExportDTO> export(@PathVariable("withdrawId") Long withdrawId) {
//        return new ApiResponse<>(withdrawService.exportWithdraw(withdrawId));
//    }
//
//    @GetMapping("/export/total/{accountId}")
//    public ApiResponse<WithdrawTotalExportDTO> exportTotal(@PathVariable("accountId") Integer accountId) {
//        return new ApiResponse<>(withdrawService.exportTotalWithdrawByAccountId(accountId));
//    }
//
//    @GetMapping("/export/total")
//    public ApiResponse<WithdrawTotalExportDTO> exportTotalRequest(@RequestAttribute("accountId") Integer accountId) {
//        return new ApiResponse<>(withdrawService.exportTotalWithdrawByAccountId(accountId));
//    }



    @GetMapping("/export/excel/{withdrawId}")
    public ResponseEntity<byte[]> exportWithdrawToExcel(@PathVariable("withdrawId") Long withdrawId) throws IOException {
        byte[] excelBytes = excelExportService.exportSingleWithdrawToExcel(withdrawId);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=withdraw_" + withdrawId + "_" + getCurrentTimestamp() + ".xlsx");
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelBytes);
    }

    /**
     * Export all withdrawals for a specific account to Excel
     */
    @GetMapping("/export/excel/account")
    public ResponseEntity<byte[]> exportAllByAccountIdToExcel(@RequestAttribute("accountId") Integer accountId) throws IOException {
        byte[] excelBytes = excelExportService.exportAccountWithdrawalsToExcel(accountId);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=account_withdrawals_" + accountId + "_" + getCurrentTimestamp() + ".xlsx");
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelBytes);
    }

    @GetMapping("/export/excel/account/{accountId}")
    public ResponseEntity<byte[]> exportAllByAccountIdToExcelManagement(@PathVariable("accountId") Integer accountId) throws IOException {
        byte[] excelBytes = excelExportService.exportAccountWithdrawalsToExcel(accountId);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=account_withdrawals_" + accountId + "_" + getCurrentTimestamp() + ".xlsx");
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelBytes);
    }

    /**
     * Export all withdrawals across all accounts to Excel (Admin only)
     */
    @GetMapping("/export/excel/all")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ResponseEntity<byte[]> exportAllWithdrawalsToExcel() throws IOException {
        byte[] excelBytes = excelExportService.exportAllWithdrawalsToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=all_withdrawals_" + getCurrentTimestamp() + ".xlsx");
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelBytes);
    }

    @PostMapping("/export/status")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ResponseEntity<byte[]> exportByStatus(@Valid @RequestBody WithdrawExportDTO requestDTO) throws IOException {
        byte[] excelBytes = excelExportService.exportWithdrawalsByStatusToExcel(requestDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=all_withdrawals_" + getCurrentTimestamp() + ".xlsx");
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelBytes);
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    @PutMapping("/batch-update")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<?> batchUpdate(@RequestBody BatchUpdateRequestDTO requestDTO) {
        int updatedCount = withdrawService.batchUpdateWithdrawStatus(
                requestDTO.getWithdrawIds(),
                requestDTO.getStatus()
        );
        return new ApiResponse<>("Successfully updated " + updatedCount + " withdrawals");
    }
}

