package com.ftcs.balanceservice.payment.service;
import com.ftcs.balanceservice.payment.constant.PaymentStatus;
import com.ftcs.balanceservice.payment.model.Payment;
import com.ftcs.balanceservice.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentExcelExportService {

    private final PaymentRepository paymentRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Export payment information by payment ID
     */
    public byte[] exportPaymentById(Long paymentId) throws IOException {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

        if (paymentOptional.isEmpty()) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }

        Payment payment = paymentOptional.get();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Payment Details");

            // Create header row
            createPaymentHeaderRow(workbook, sheet);

            // Create data row
            createPaymentDataRow(sheet, payment, 1);

            // Auto-size columns
            for (int i = 0; i < 9; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * Export payments by payment status
     */
    public byte[] exportPaymentsByStatus(PaymentStatus status) throws IOException {
        List<Payment> payments = paymentRepository.findPaymentByPaymentStatus(status);

        if (payments.isEmpty()) {
            throw new RuntimeException("No payments found with status: " + status);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Payments - " + status);

            // Create header row
            createPaymentHeaderRow(workbook, sheet);

            // Create data rows
            int rowNum = 1;
            for (Payment payment : payments) {
                createPaymentDataRow(sheet, payment, rowNum++);
            }

            // Create summary sheet
            createPaymentSummarySheet(workbook, payments, status.toString());

            // Auto-size columns
            for (int i = 0; i < 9; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * Export payments by account ID
     */
    public byte[] exportPaymentsByAccountId(Integer accountId) throws IOException {
        List<Payment> payments = paymentRepository.findPaymentByAccountId(accountId);

        if (payments.isEmpty()) {
            throw new RuntimeException("No payments found for account ID: " + accountId);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Payments - Account " + accountId);

            // Create header row
            createPaymentHeaderRow(workbook, sheet);

            // Create data rows
            int rowNum = 1;
            for (Payment payment : payments) {
                createPaymentDataRow(sheet, payment, rowNum++);
            }

            // Create summary sheet
            createPaymentSummarySheet(workbook, payments, "Account " + accountId);

            // Auto-size columns
            for (int i = 0; i < 9; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * Creates a summary sheet for multiple payments
     */
    private void createPaymentSummarySheet(Workbook workbook, List<Payment> payments, String filterCriteria) {
        Sheet summarySheet = workbook.createSheet("Summary");

        // Calculate summary data
        double totalAmount = payments.stream().mapToDouble(Payment::getAmount).sum();
        int totalPayments = payments.size();
        double averageAmount = totalAmount / totalPayments;
        LocalDateTime oldestPayment = payments.stream()
                .map(Payment::getPaymentDate)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        LocalDateTime newestPayment = payments.stream()
                .map(Payment::getPaymentDate)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        // Create header style
        CellStyle headerStyle = createHeaderStyle(workbook);

        // Create summary rows
        Row titleRow = summarySheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Payment Summary Report - " + filterCriteria);
        titleCell.setCellStyle(headerStyle);

        Row totalRow = summarySheet.createRow(2);
        totalRow.createCell(0).setCellValue("Total Number of Payments:");
        totalRow.createCell(1).setCellValue(totalPayments);

        Row amountRow = summarySheet.createRow(3);
        amountRow.createCell(0).setCellValue("Total Amount:");
        amountRow.createCell(1).setCellValue(totalAmount);

        Row avgRow = summarySheet.createRow(4);
        avgRow.createCell(0).setCellValue("Average Payment Amount:");
        avgRow.createCell(1).setCellValue(averageAmount);

        Row dateRangeRow = summarySheet.createRow(5);
        dateRangeRow.createCell(0).setCellValue("Date Range:");
        dateRangeRow.createCell(1).setCellValue(
                formatLocalDateTime(oldestPayment) + " to " + formatLocalDateTime(newestPayment));

        Row generatedRow = summarySheet.createRow(7);
        generatedRow.createCell(0).setCellValue("Report Generated:");
        generatedRow.createCell(1).setCellValue(formatLocalDateTime(LocalDateTime.now()));

        // Auto-size columns
        summarySheet.autoSizeColumn(0);
        summarySheet.autoSizeColumn(1);
    }

    /**
     * Creates the header row for payment sheets
     */
    private void createPaymentHeaderRow(Workbook workbook, Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Payment ID", "Booking ID", "Account ID", "Amount", "Status",
                "Payment Date", "Transaction ID", "Created At"
        };

        CellStyle headerStyle = createHeaderStyle(workbook);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Creates a data row for a payment
     */
    private void createPaymentDataRow(Sheet sheet, Payment payment, int rowNum) {
        Row dataRow = sheet.createRow(rowNum);

        dataRow.createCell(0).setCellValue(payment.getPaymentId());
        dataRow.createCell(1).setCellValue(payment.getBookingId());
        dataRow.createCell(2).setCellValue(payment.getAccountId());
        dataRow.createCell(3).setCellValue(payment.getAmount());
        dataRow.createCell(4).setCellValue(payment.getPaymentStatus().toString());
        dataRow.createCell(5).setCellValue(formatLocalDateTime(payment.getPaymentDate()));
        dataRow.createCell(6).setCellValue(payment.getTransactionId() != null ? payment.getTransactionId().toString() : "");
        dataRow.createCell(7).setCellValue(formatLocalDateTime(payment.getCreateAt()));
    }

    /**
     * Creates a header style with light blue background and white, bold text
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);

        return headerStyle;
    }

    /**
     * Format LocalDateTime to string
     */
    private String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : "";
    }
}