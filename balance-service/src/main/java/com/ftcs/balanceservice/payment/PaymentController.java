package com.ftcs.balanceservice.payment;

import com.ftcs.balanceservice.BalanceURL;
import com.ftcs.balanceservice.payment.constant.PaymentStatus;
import com.ftcs.balanceservice.payment.service.PaymentExcelExportService;
import com.ftcs.balanceservice.payment.service.PaymentVerificationService;
import com.ftcs.common.dto.ApiResponse;
import com.ftcs.balanceservice.payment.model.Payment;
import com.ftcs.balanceservice.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(BalanceURL.PAYMENT)
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentVerificationService paymentVerificationService;
    private final PaymentExcelExportService paymentExcelExportService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<Page<Payment>> getAllPayments(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(paymentService.getAllPayments(page, size));
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<Payment> getPaymentById(@PathVariable("paymentId") Long paymentId) {
        return new ApiResponse<>(paymentService.getPaymentById(paymentId));
    }

    @GetMapping("/tripBooking/{tripBookingId}")
    public ApiResponse<Payment> getPaymentByTripBooking(@PathVariable("tripBookingId") Long tripBookingId) {
        return new ApiResponse<>(paymentService.getPaymentByTripBookingId(tripBookingId));
    }

    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<byte[]> exportPaymentsByAccountId(@PathVariable("accountId") Integer accountId) {
        try {
            byte[] excelFile = paymentExcelExportService.exportPaymentsByAccountId(accountId);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "account_" + accountId + "_" + timestamp + ".xlsx";

            return createExcelResponse(excelFile, filename);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/export-excel/{paymentId}")
    public ResponseEntity<byte[]> exportPaymentById(@PathVariable("paymentId") Long paymentId) {
        try {
            byte[] excelBytes = paymentExcelExportService.exportPaymentById(paymentId);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "payment_" + paymentId + "_" + timestamp + ".xlsx";

            return createExcelResponse(excelBytes, filename);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Export payments by status
     *
     * @param status Payment status to filter by
     * @return Excel file as byte array
     */
    @GetMapping("/export-excel/status/{status}")
    public ResponseEntity<byte[]> exportPaymentsByStatus(@PathVariable("status") String status) {
        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            byte[] excelBytes = paymentExcelExportService.exportPaymentsByStatus(paymentStatus);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "payments_status_" + status + "_" + timestamp + ".xlsx";

            return createExcelResponse(excelBytes, filename);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Helper method to create Excel response with proper headers
     */
    private ResponseEntity<byte[]> createExcelResponse(byte[] excelBytes, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }


}
