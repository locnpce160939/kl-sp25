package com.ftcs.insuranceservice.insurance_claim.service;

import com.ftcs.insuranceservice.booking_insurance.model.BookingInsurance;
import com.ftcs.insuranceservice.booking_insurance.service.BookingInsuranceService;
import com.ftcs.insuranceservice.insurance_claim.model.InsuranceClaim;
import com.ftcs.insuranceservice.insurance_claim.repository.InsuranceClaimRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class InsuranceClaimExportService {

    private final InsuranceClaimRepository insuranceClaimRepository;
    private final BookingInsuranceService bookingInsuranceService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public byte[] exportClaimById(Long claimId) throws IOException {
        InsuranceClaim claim = insuranceClaimRepository.findInsuranceClaimById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found with ID: " + claimId));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Claim Details");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            int rowNum = createClaimHeader(sheet, headerStyle);
            rowNum = addClaimDetails(sheet, claim, rowNum);
            rowNum = addInsuranceDetails(sheet, claim, headerStyle, currencyStyle, rowNum);
            addEvidenceImages(sheet, claim, headerStyle, rowNum);

            autoSizeColumns(sheet);
            return writeWorkbookToByteArray(workbook);
        }
    }

    private int createClaimHeader(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        createHeaderCell(headerRow, 0, "Insurance Claim Details", headerStyle);
        return 1;
    }

    private int addClaimDetails(Sheet sheet, InsuranceClaim claim, int rowNum) {
        createDataRow(sheet, rowNum++, "Claim ID", claim.getId().toString());
        createDataRow(sheet, rowNum++, "Booking ID", claim.getBookingId().toString());
        createDataRow(sheet, rowNum++, "Status", claim.getClaimStatus().toString());
        createDataRow(sheet, rowNum++, "Claim Date", formatDateTime(claim.getClaimDate()));
        createDataRow(sheet, rowNum++, "Description", claim.getClaimDescription());
        return rowNum + 1; // Add empty row for spacing
    }

    private int addInsuranceDetails(Sheet sheet, InsuranceClaim claim, CellStyle headerStyle, CellStyle currencyStyle, int rowNum) {
        BookingInsurance insurance = bookingInsuranceService.getBookingInsuranceById(claim.getBookingInsuranceId());
        
        createHeaderCell(sheet.createRow(rowNum++), 0, "Insurance Details", headerStyle);
        createDataRow(sheet, rowNum++, "Insurance ID", insurance.getId().toString());
        
        // Premium Amount
        Row premiumRow = sheet.createRow(rowNum++);
        createCell(premiumRow, 0, "Premium Amount");
        createCurrencyCell(premiumRow, 1, insurance.getCalculatedPremium(), currencyStyle);

        // Compensation Amount
        Row compensationRow = sheet.createRow(rowNum++);
        createCell(compensationRow, 0, "Compensation Amount");
        createCurrencyCell(compensationRow, 1, insurance.getCalculateCompensation(), currencyStyle);

        return rowNum + 1; // Add empty row for spacing
    }

    private void addEvidenceImages(Sheet sheet, InsuranceClaim claim, CellStyle headerStyle, int rowNum) {
        createHeaderCell(sheet.createRow(rowNum++), 0, "Evidence Images", headerStyle);
        int imageIndex = 1;
        for (String image : claim.getEvidenceImageList()) {
            createDataRow(sheet, rowNum++, "Image " + imageIndex++, image);
        }
    }

    private void autoSizeColumns(Sheet sheet) {
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        return style;
    }

    private void createHeaderCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createDataRow(Sheet sheet, int rowNum, String label, String value) {
        Row row = sheet.createRow(rowNum);
        createCell(row, 0, label);
        createCell(row, 1, value);
    }

    private void createCell(Row row, int column, String value) {
        row.createCell(column).setCellValue(value);
    }

    private void createCurrencyCell(Row row, int column, double value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : "";
    }

    private byte[] writeWorkbookToByteArray(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
} 