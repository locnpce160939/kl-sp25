package com.ftcs.balanceservice.withdraw.service;

import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.balanceservice.withdraw.constant.WithdrawStatus;
import com.ftcs.balanceservice.withdraw.dto.WithdrawExportDTO;
import com.ftcs.balanceservice.withdraw.dto.WithdrawTotalExportDTO;
import com.ftcs.balanceservice.withdraw.model.Withdraw;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelExportService {
    private final WithdrawService withdrawService;
    private final AccountRepository accountRepository;

    /**
     * Export a single withdrawal to Excel
     */
    public byte[] exportSingleWithdrawToExcel(Long withdrawId) throws IOException {
        WithdrawExportDTO withdrawData = withdrawService.exportWithdraw(withdrawId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Withdrawal Details");

            // Create header row style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Create header row
            Row headerRow = sheet.createRow(0);
            createHeaderCell(headerRow, 0, "Field", headerStyle);
            createHeaderCell(headerRow, 1, "Value", headerStyle);

            // Add data rows
            int rowNum = 1;
            createDataRow(sheet, rowNum++, "Withdrawal ID", withdrawData.getWithdrawId().toString());
            createDataRow(sheet, rowNum++, "Account ID", withdrawData.getAccountId().toString());
            createDataRow(sheet, rowNum++, "Username", withdrawData.getUsername());
            createDataRow(sheet, rowNum++, "Amount", withdrawData.getAmount().toString());
            createDataRow(sheet, rowNum++, "Bank Name", String.valueOf(withdrawData.getBankName()));
            createDataRow(sheet, rowNum++, "Bank Account Number", withdrawData.getBankAccountNumber());
            createDataRow(sheet, rowNum++, "Status", withdrawData.getStatus().toString());
            createDataRow(sheet, rowNum++, "Request Date", withdrawData.getRequestDate());

            if (withdrawData.getProcessedDate() != null) {
                createDataRow(sheet, rowNum++, "Processed Date", withdrawData.getProcessedDate());
            }

            // Auto-size columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            return writeWorkbookToByteArray(workbook);
        }
    }

    /**
     * Export total withdrawals for an account to Excel
     */
    public byte[] exportTotalWithdrawToExcel(Integer accountId) throws IOException {
        WithdrawTotalExportDTO totalData = withdrawService.exportTotalWithdrawByAccountId(accountId);
        List<Withdraw> allWithdraws = withdrawService.getAllByAccountId(accountId);

        try (Workbook workbook = new XSSFWorkbook()) {
            // Create summary sheet
            Sheet summarySheet = workbook.createSheet("Summary");

            // Create header and data styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            // Create summary section
            Row titleRow = summarySheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Withdrawal Summary Report");
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);

            // Add account information
            int rowNum = 2;
            createDataRow(summarySheet, rowNum++, "Account ID", totalData.getAccountId().toString());
            createDataRow(summarySheet, rowNum++, "Username", totalData.getUsername());

            // Skip a row
            rowNum++;

            // Add financial summary with currency formatting
            Row balanceRow = summarySheet.createRow(rowNum++);
            balanceRow.createCell(0).setCellValue("Current Balance");
            Cell balanceCell = balanceRow.createCell(1);
            balanceCell.setCellValue(totalData.getCurrentBalance());
            balanceCell.setCellStyle(currencyStyle);

            Row totalAmountRow = summarySheet.createRow(rowNum++);
            totalAmountRow.createCell(0).setCellValue("Total Withdrawal Amount");
            Cell totalAmountCell = totalAmountRow.createCell(1);
            totalAmountCell.setCellValue(totalData.getTotalWithdrawAmount());
            totalAmountCell.setCellStyle(currencyStyle);

            // Skip a row
            rowNum++;

            // Add statistics
            createDataRow(summarySheet, rowNum++, "Total Withdrawals", totalData.getTotalWithdrawals().toString());
            createDataRow(summarySheet, rowNum++, "Approved Withdrawals", totalData.getTotalApproved().toString());
            createDataRow(summarySheet, rowNum++, "Pending Withdrawals", totalData.getTotalPending().toString());
            createDataRow(summarySheet, rowNum++, "Rejected Withdrawals", totalData.getTotalRejected().toString());

            // Skip a row
            rowNum++;

            createDataRow(summarySheet, rowNum++, "Export Date", totalData.getExportDate());

            // Auto-size columns
            summarySheet.autoSizeColumn(0);
            summarySheet.autoSizeColumn(1);

            // Create details sheet with all withdrawals
            Sheet detailsSheet = workbook.createSheet("Withdrawal Details");

            // Create header row
            Row headerRow = detailsSheet.createRow(0);
            String[] headers = {"ID", "Amount", "Bank Name", "Bank Account", "Status", "Request Date", "Processed Date"};
            for (int i = 0; i < headers.length; i++) {
                createHeaderCell(headerRow, i, headers[i], headerStyle);
            }

            // Add data rows
            rowNum = 1;
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));

            for (Withdraw withdraw : allWithdraws) {
                Row row = detailsSheet.createRow(rowNum++);
                row.createCell(0).setCellValue(withdraw.getWithdrawId());

                Cell amountCell = row.createCell(1);
                amountCell.setCellValue(withdraw.getAmount());
                amountCell.setCellStyle(currencyStyle);

                row.createCell(2).setCellValue(String.valueOf(withdraw.getBankName()));
                row.createCell(3).setCellValue(withdraw.getBankAccountNumber());
                row.createCell(4).setCellValue(withdraw.getStatus().toString());
                row.createCell(5).setCellValue(
                        withdraw.getRequestDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                if (withdraw.getProcessedDate() != null) {
                    row.createCell(6).setCellValue(
                            withdraw.getProcessedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                detailsSheet.autoSizeColumn(i);
            }

            return writeWorkbookToByteArray(workbook);
        }
    }

    /**
     * Helper method to create header cells with styling
     */
    private void createHeaderCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /**
     * Helper method to create data rows in key-value format
     */
    private void createDataRow(Sheet sheet, int rowNum, String key, String value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(key);
        row.createCell(1).setCellValue(value);
    }

    /**
     * Create header cell style
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    /**
     * Create currency cell style
     */
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
        return style;
    }

    /**
     * Helper method to write workbook to byte array
     */
    private byte[] writeWorkbookToByteArray(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
            return outputStream.toByteArray();
        }
    }

    public byte[] exportAccountWithdrawalsToExcel(Integer accountId) throws IOException {
        List<Withdraw> withdrawals = withdrawService.getAllByAccountId(accountId);
        Optional<Account> accountOpt = accountRepository.findAccountByAccountId(accountId);

        if (accountOpt.isEmpty()) {
            throw new RuntimeException("Account not found with ID: " + accountId);
        }

        Account account = accountOpt.get();

        try (Workbook workbook = new XSSFWorkbook()) {
            // Create summary sheet
            Sheet summarySheet = workbook.createSheet("Account Summary");

            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            // Add account information and title
            Row titleRow = summarySheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Account Withdrawal Report");
            CellStyle titleStyle = createTitleStyle(workbook);
            titleCell.setCellStyle(titleStyle);

            int rowNum = 2;
            createDataRow(summarySheet, rowNum++, "Account ID", accountId.toString());
            createDataRow(summarySheet, rowNum++, "Username", account.getUsername());
            createDataRow(summarySheet, rowNum++, "Current Balance", formatCurrency(account.getBalance()));

            // Calculate summary statistics
            double totalRequested = withdrawals.stream()
                    .mapToDouble(Withdraw::getAmount)
                    .sum();

            double totalApproved = withdrawals.stream()
                    .filter(w -> w.getStatus() == WithdrawStatus.APPROVED)
                    .mapToDouble(Withdraw::getAmount)
                    .sum();

            int countPending = (int) withdrawals.stream()
                    .filter(w -> w.getStatus() == WithdrawStatus.PENDING)
                    .count();

            int countApproved = (int) withdrawals.stream()
                    .filter(w -> w.getStatus() == WithdrawStatus.APPROVED)
                    .count();

            int countRejected = (int) withdrawals.stream()
                    .filter(w -> w.getStatus() == WithdrawStatus.REJECTED)
                    .count();

            // Skip a row
            rowNum++;

            // Add withdrawal statistics
            Row statsTitleRow = summarySheet.createRow(rowNum++);
            Cell statsTitleCell = statsTitleRow.createCell(0);
            statsTitleCell.setCellValue("Withdrawal Statistics");
            Font statsTitleFont = workbook.createFont();
            statsTitleFont.setBold(true);
            CellStyle statsTitleStyle = workbook.createCellStyle();
            statsTitleStyle.setFont(statsTitleFont);
            statsTitleCell.setCellStyle(statsTitleStyle);

            rowNum++;

            createDataRow(summarySheet, rowNum++, "Total Withdrawals", Integer.toString(withdrawals.size()));

            Row totalReqRow = summarySheet.createRow(rowNum++);
            totalReqRow.createCell(0).setCellValue("Total Amount Requested");
            Cell totalReqCell = totalReqRow.createCell(1);
            totalReqCell.setCellValue(totalRequested);
            totalReqCell.setCellStyle(currencyStyle);

            Row totalAppRow = summarySheet.createRow(rowNum++);
            totalAppRow.createCell(0).setCellValue("Total Amount Approved");
            Cell totalAppCell = totalAppRow.createCell(1);
            totalAppCell.setCellValue(totalApproved);
            totalAppCell.setCellStyle(currencyStyle);

            createDataRow(summarySheet, rowNum++, "Pending Withdrawals", Integer.toString(countPending));
            createDataRow(summarySheet, rowNum++, "Approved Withdrawals", Integer.toString(countApproved));
            createDataRow(summarySheet, rowNum++, "Rejected Withdrawals", Integer.toString(countRejected));

            // Add generation timestamp
            rowNum += 2;
            createDataRow(summarySheet, rowNum, "Report Generated",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // Auto-size columns
            summarySheet.autoSizeColumn(0);
            summarySheet.autoSizeColumn(1);

            // Create details sheet
            createWithdrawalsDetailSheet(workbook, withdrawals, "Withdrawal Details");

            return writeWorkbookToByteArray(workbook);
        }
    }

    /**
     * Export all withdrawals across all accounts to Excel (Admin only)
     */
    public byte[] exportAllWithdrawalsToExcel() throws IOException {
        List<Withdraw> allWithdrawals = withdrawService.getAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            // Create summary sheet
            Sheet summarySheet = workbook.createSheet("Summary");

            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            // Add title
            Row titleRow = summarySheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("System-wide Withdrawal Report");
            CellStyle titleStyle = createTitleStyle(workbook);
            titleCell.setCellStyle(titleStyle);

            // Calculate overall statistics
            double totalRequested = allWithdrawals.stream()
                    .mapToDouble(Withdraw::getAmount)
                    .sum();

            double totalApproved = allWithdrawals.stream()
                    .filter(w -> w.getStatus() == WithdrawStatus.APPROVED)
                    .mapToDouble(Withdraw::getAmount)
                    .sum();

            int countPending = (int) allWithdrawals.stream()
                    .filter(w -> w.getStatus() == WithdrawStatus.PENDING)
                    .count();

            int countApproved = (int) allWithdrawals.stream()
                    .filter(w -> w.getStatus() == WithdrawStatus.APPROVED)
                    .count();

            int countRejected = (int) allWithdrawals.stream()
                    .filter(w -> w.getStatus() == WithdrawStatus.REJECTED)
                    .count();

            // Add overall statistics section
            int rowNum = 2;
            Row statsTitleRow = summarySheet.createRow(rowNum++);
            Cell statsTitleCell = statsTitleRow.createCell(0);
            statsTitleCell.setCellValue("Overall Statistics");
            Font statsTitleFont = workbook.createFont();
            statsTitleFont.setBold(true);
            CellStyle statsTitleStyle = workbook.createCellStyle();
            statsTitleStyle.setFont(statsTitleFont);
            statsTitleCell.setCellStyle(statsTitleStyle);

            rowNum++;

            createDataRow(summarySheet, rowNum++, "Total Withdrawals", Integer.toString(allWithdrawals.size()));

            Row totalReqRow = summarySheet.createRow(rowNum++);
            totalReqRow.createCell(0).setCellValue("Total Amount Requested");
            Cell totalReqCell = totalReqRow.createCell(1);
            totalReqCell.setCellValue(totalRequested);
            totalReqCell.setCellStyle(currencyStyle);

            Row totalAppRow = summarySheet.createRow(rowNum++);
            totalAppRow.createCell(0).setCellValue("Total Amount Approved");
            Cell totalAppCell = totalAppRow.createCell(1);
            totalAppCell.setCellValue(totalApproved);
            totalAppCell.setCellStyle(currencyStyle);

            createDataRow(summarySheet, rowNum++, "Pending Withdrawals", Integer.toString(countPending));
            createDataRow(summarySheet, rowNum++, "Approved Withdrawals", Integer.toString(countApproved));
            createDataRow(summarySheet, rowNum++, "Rejected Withdrawals", Integer.toString(countRejected));

            // Add statistics by account
            rowNum += 2;
            Row accountStatsTitleRow = summarySheet.createRow(rowNum++);
            Cell accountStatsTitleCell = accountStatsTitleRow.createCell(0);
            accountStatsTitleCell.setCellValue("Withdrawal Statistics by Account");
            accountStatsTitleCell.setCellStyle(statsTitleStyle);

            rowNum++;

            // Create header for account statistics table
            Row accountStatsHeaderRow = summarySheet.createRow(rowNum++);
            createHeaderCell(accountStatsHeaderRow, 0, "Account ID", headerStyle);
            createHeaderCell(accountStatsHeaderRow, 1, "Username", headerStyle);
            createHeaderCell(accountStatsHeaderRow, 2, "Total Withdrawals", headerStyle);
            createHeaderCell(accountStatsHeaderRow, 3, "Total Amount", headerStyle);
            createHeaderCell(accountStatsHeaderRow, 4, "Approved Amount", headerStyle);

            // Group withdrawals by account
            Map<Integer, List<Withdraw>> withdrawalsByAccount = allWithdrawals.stream()
                    .collect(Collectors.groupingBy(Withdraw::getAccountId));

            // For each account, add a row with statistics
            for (Map.Entry<Integer, List<Withdraw>> entry : withdrawalsByAccount.entrySet()) {
                Integer accId = entry.getKey();
                List<Withdraw> accWithdrawals = entry.getValue();

                Optional<Account> accountOpt = accountRepository.findAccountByAccountId(accId);
                String username = accountOpt.isPresent() ? accountOpt.get().getUsername() : "Unknown";

                double accTotalAmount = accWithdrawals.stream().mapToDouble(Withdraw::getAmount).sum();
                double accApprovedAmount = accWithdrawals.stream()
                        .filter(w -> w.getStatus() == WithdrawStatus.APPROVED)
                        .mapToDouble(Withdraw::getAmount)
                        .sum();

                Row accountRow = summarySheet.createRow(rowNum++);
                accountRow.createCell(0).setCellValue(accId);
                accountRow.createCell(1).setCellValue(username);
                accountRow.createCell(2).setCellValue(accWithdrawals.size());

                Cell accTotalCell = accountRow.createCell(3);
                accTotalCell.setCellValue(accTotalAmount);
                accTotalCell.setCellStyle(currencyStyle);

                Cell accApprovedCell = accountRow.createCell(4);
                accApprovedCell.setCellValue(accApprovedAmount);
                accApprovedCell.setCellStyle(currencyStyle);
            }

            // Add generation timestamp
            rowNum += 2;
            createDataRow(summarySheet, rowNum, "Report Generated",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // Auto-size columns
            for (int i = 0; i < 5; i++) {
                summarySheet.autoSizeColumn(i);
            }

            // Create details sheet with all withdrawals
            createWithdrawalsDetailSheet(workbook, allWithdrawals, "All Withdrawals");

            return writeWorkbookToByteArray(workbook);
        }
    }

    /**
     * Helper method to create a detail sheet with withdrawals
     */
    private void createWithdrawalsDetailSheet(Workbook workbook, List<Withdraw> withdrawals, String sheetName) {
        Sheet detailsSheet = workbook.createSheet(sheetName);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);

        // Create header row
        Row headerRow = detailsSheet.createRow(0);
        String[] headers = {"Withdraw ID", "Account ID", "Amount", "Bank Name", "Bank Account",
                "Status", "Request Date", "Processed Date"};

        for (int i = 0; i < headers.length; i++) {
            createHeaderCell(headerRow, i, headers[i], headerStyle);
        }

        // Add data rows
        int rowNum = 1;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Withdraw withdraw : withdrawals) {
            Row row = detailsSheet.createRow(rowNum++);

            row.createCell(0).setCellValue(withdraw.getWithdrawId());
            row.createCell(1).setCellValue(withdraw.getAccountId());

            Cell amountCell = row.createCell(2);
            amountCell.setCellValue(withdraw.getAmount());
            amountCell.setCellStyle(currencyStyle);

            row.createCell(3).setCellValue(String.valueOf(withdraw.getBankName()));
            row.createCell(4).setCellValue(withdraw.getBankAccountNumber());
            row.createCell(5).setCellValue(withdraw.getStatus().toString());

            if (withdraw.getRequestDate() != null) {
                row.createCell(6).setCellValue(withdraw.getRequestDate().format(dateFormatter));
            }

            if (withdraw.getProcessedDate() != null) {
                row.createCell(7).setCellValue(withdraw.getProcessedDate().format(dateFormatter));
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            detailsSheet.autoSizeColumn(i);
        }
    }

    /**
     * Create title style
     */
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    /**
     * Format currency value as string
     */
    private String formatCurrency(double amount) {
        return String.format("%,.2f", amount);
    }
}