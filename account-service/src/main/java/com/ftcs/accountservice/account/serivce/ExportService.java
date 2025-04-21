package com.ftcs.accountservice.account.serivce;

import com.ftcs.accountservice.driver.identification.model.AddressDriver;
import com.ftcs.accountservice.driver.identification.model.DriverIdentification;
import com.ftcs.accountservice.driver.identification.repository.AddressDriverRepository;
import com.ftcs.accountservice.driver.identification.repository.DriverIdentificationRepository;
import com.ftcs.accountservice.driver.license.model.License;
import com.ftcs.accountservice.driver.license.repository.LicenseRepository;
import com.ftcs.accountservice.driver.vehicle.model.Vehicle;
import com.ftcs.accountservice.driver.vehicle.repository.VehicleRepository;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.authservice.features.account.contacts.RoleType;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ExportService {

    private final AccountRepository accountRepository;
    private final DriverIdentificationRepository driverIdentificationRepository;
    private final AddressDriverRepository addressDriverRepository;
    private final LicenseRepository licenseRepository;
    private final VehicleRepository vehicleRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


    public byte[] exportAccountToExcel(Integer accountId) throws IOException {
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (accountOptional.isEmpty()) {
            throw new RuntimeException("Account not found with ID: " + accountId);
        }

        Account account = accountOptional.get();

        try (Workbook workbook = new XSSFWorkbook()) {
            // Create Account sheet for all account types
            createAccountSheet(workbook, account);

            // If the account is a driver, add additional sheets
            if (account.getRole() == RoleType.DRIVER) {
                // Get driver related information
                Optional<DriverIdentification> driverIdentificationOptional =
                        driverIdentificationRepository.findByAccountId(accountId);

                if (driverIdentificationOptional.isPresent()) {
                    DriverIdentification driverIdentification = driverIdentificationOptional.get();

                    // Create Driver Identification sheet
                    createDriverIdentificationSheet(workbook, driverIdentification);

                    // Get and create address sheets
                    if (driverIdentification.getPermanentAddress() != null) {
                        Optional<AddressDriver> permanentAddressOptional =
                                addressDriverRepository.findById(driverIdentification.getPermanentAddress());
                        if (permanentAddressOptional.isPresent()) {
                            createAddressSheet(workbook, permanentAddressOptional.get(), "Permanent Address");
                        }
                    }

                    if (driverIdentification.getTemporaryAddress() != null) {
                        Optional<AddressDriver> temporaryAddressOptional =
                                addressDriverRepository.findById(driverIdentification.getTemporaryAddress());
                        if (temporaryAddressOptional.isPresent()) {
                            createAddressSheet(workbook, temporaryAddressOptional.get(), "Temporary Address");
                        }
                    }
                }

                // Get and create license sheet
                Optional<License> licenseOptional = licenseRepository.findLicenseByAccountId(accountId);
                if (licenseOptional.isPresent()) {
                    createLicenseSheet(workbook, licenseOptional.get());
                }

                // Get and create vehicle sheets (a driver can have multiple vehicles)
                List<Vehicle> vehicles = vehicleRepository.findVehiclesByAccountId(accountId);
                if (!vehicles.isEmpty()) {
                    createVehiclesSheet(workbook, vehicles);
                }
            }

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void createAccountSheet(Workbook workbook, Account account) {
        Sheet sheet = workbook.createSheet("Account Information");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] accountHeaders = {
                "Account ID", "Username", "Full Name", "Email", "Phone",
                "Role", "Status", "Balance", "Last Login", "Created At", "Updated At"
        };

        CellStyle headerStyle = createHeaderStyle(workbook);

        for (int i = 0; i < accountHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(accountHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data row
        Row dataRow = sheet.createRow(1);

        dataRow.createCell(0).setCellValue(account.getAccountId());
        dataRow.createCell(1).setCellValue(account.getUsername());
        dataRow.createCell(2).setCellValue(account.getFullName());
        dataRow.createCell(3).setCellValue(account.getEmail());
        dataRow.createCell(4).setCellValue(account.getPhone());
        dataRow.createCell(5).setCellValue(account.getRole().toString());
        dataRow.createCell(6).setCellValue(account.getStatus() != null ? account.getStatus().toString() : "");
        dataRow.createCell(7).setCellValue(account.getBalance());
        dataRow.createCell(8).setCellValue(formatLocalDateTime(account.getLastLogin()));
        dataRow.createCell(9).setCellValue(formatLocalDateTime(account.getCreateAt()));
        dataRow.createCell(10).setCellValue(formatLocalDateTime(account.getUpdateAt()));

        // Auto-size columns
        for (int i = 0; i < accountHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDriverIdentificationSheet(Workbook workbook, DriverIdentification driverIdentification) {
        Sheet sheet = workbook.createSheet("Driver Identification");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] identificationHeaders = {
                "ID", "Account ID", "ID Number", "Full Name", "Gender", "Birthday",
                "Country", "Status", "Issue Date", "Expiry Date", "Issued By",
                "Created At", "Updated At"
        };

        CellStyle headerStyle = createHeaderStyle(workbook);

        for (int i = 0; i < identificationHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(identificationHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data row
        Row dataRow = sheet.createRow(1);

        dataRow.createCell(0).setCellValue(driverIdentification.getDriverIdentificationId());
        dataRow.createCell(1).setCellValue(driverIdentification.getAccountId());
        dataRow.createCell(2).setCellValue(driverIdentification.getIdNumber());
        dataRow.createCell(3).setCellValue(driverIdentification.getFullName());
        dataRow.createCell(4).setCellValue(driverIdentification.getGender());
        dataRow.createCell(5).setCellValue(formatLocalDateTime(driverIdentification.getBirthday()));
        dataRow.createCell(6).setCellValue(driverIdentification.getCountry());
        dataRow.createCell(7).setCellValue(driverIdentification.getStatus().toString());
        dataRow.createCell(8).setCellValue(formatLocalDateTime(driverIdentification.getIssueDate()));
        dataRow.createCell(9).setCellValue(formatLocalDateTime(driverIdentification.getExpiryDate()));
        dataRow.createCell(10).setCellValue(driverIdentification.getIssuedBy());
        dataRow.createCell(11).setCellValue(formatLocalDateTime(driverIdentification.getCreateAt()));
        dataRow.createCell(12).setCellValue(formatLocalDateTime(driverIdentification.getUpdateAt()));

        // Auto-size columns
        for (int i = 0; i < identificationHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createAddressSheet(Workbook workbook, AddressDriver addressDriver, String sheetName) {
        Sheet sheet = workbook.createSheet(sheetName);

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] addressHeaders = {
                "Address ID", "Street Address", "Ward ID", "District ID", "Province ID",
                "Address Type", "Notes", "Created At", "Updated At"
        };

        CellStyle headerStyle = createHeaderStyle(workbook);

        for (int i = 0; i < addressHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(addressHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data row
        Row dataRow = sheet.createRow(1);

        dataRow.createCell(0).setCellValue(addressDriver.getAddressDriverId());
        dataRow.createCell(1).setCellValue(addressDriver.getStreetAddress());
        dataRow.createCell(2).setCellValue(addressDriver.getWardId());
        dataRow.createCell(3).setCellValue(addressDriver.getDistrictId());
        dataRow.createCell(4).setCellValue(addressDriver.getProvinceId());
        dataRow.createCell(5).setCellValue(addressDriver.getAddressType().toString());
        dataRow.createCell(6).setCellValue(addressDriver.getNotes());
        dataRow.createCell(7).setCellValue(formatLocalDateTime(addressDriver.getCreateAt()));
        dataRow.createCell(8).setCellValue(formatLocalDateTime(addressDriver.getUpdateAt()));

        // Auto-size columns
        for (int i = 0; i < addressHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createLicenseSheet(Workbook workbook, License license) {
        Sheet sheet = workbook.createSheet("Driver License");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] licenseHeaders = {
                "License ID", "Account ID", "License Number", "License Type",
                "Issued Date", "Expiry Date", "Issuing Authority", "Status",
                "Notes", "Created At", "Updated At"
        };

        CellStyle headerStyle = createHeaderStyle(workbook);

        for (int i = 0; i < licenseHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(licenseHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data row
        Row dataRow = sheet.createRow(1);

        dataRow.createCell(0).setCellValue(license.getLicenseId());
        dataRow.createCell(1).setCellValue(license.getAccountId());
        dataRow.createCell(2).setCellValue(license.getLicenseNumber());
        dataRow.createCell(3).setCellValue(license.getLicenseType());
        dataRow.createCell(4).setCellValue(formatLocalDateTime(license.getIssuedDate()));
        dataRow.createCell(5).setCellValue(formatLocalDateTime(license.getExpiryDate()));
        dataRow.createCell(6).setCellValue(license.getIssuingAuthority());
        dataRow.createCell(7).setCellValue(license.getStatus().toString());
        dataRow.createCell(8).setCellValue(license.getNotes());
        dataRow.createCell(9).setCellValue(formatLocalDateTime(license.getCreateAt()));
        dataRow.createCell(10).setCellValue(formatLocalDateTime(license.getUpdateAt()));

        // Auto-size columns
        for (int i = 0; i < licenseHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createVehiclesSheet(Workbook workbook, List<Vehicle> vehicles) {
        Sheet sheet = workbook.createSheet("Driver Vehicles");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] vehicleHeaders = {
                "Vehicle ID", "Account ID", "License Plate", "Vehicle Type", "Make", "Model",
                "Year", "Capacity", "Dimensions", "Status", "Insurance Status",
                "Registration Expiry Date", "Notes", "Created At", "Updated At"
        };

        CellStyle headerStyle = createHeaderStyle(workbook);

        for (int i = 0; i < vehicleHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(vehicleHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows for each vehicle
        int rowNum = 1;
        for (Vehicle vehicle : vehicles) {
            Row dataRow = sheet.createRow(rowNum++);

            dataRow.createCell(0).setCellValue(vehicle.getVehicleId());
            dataRow.createCell(1).setCellValue(vehicle.getAccountId());
            dataRow.createCell(2).setCellValue(vehicle.getLicensePlate());
            dataRow.createCell(3).setCellValue(vehicle.getVehicleType());
            dataRow.createCell(4).setCellValue(vehicle.getMake());
            dataRow.createCell(5).setCellValue(vehicle.getModel());
            dataRow.createCell(6).setCellValue(vehicle.getYear());
            dataRow.createCell(7).setCellValue(vehicle.getCapacity());
            dataRow.createCell(8).setCellValue(vehicle.getDimensions());
            dataRow.createCell(9).setCellValue(vehicle.getStatus().toString());
            dataRow.createCell(10).setCellValue(vehicle.getInsuranceStatus());
            dataRow.createCell(11).setCellValue(formatLocalDateTime(vehicle.getRegistrationExpiryDate()));
            dataRow.createCell(12).setCellValue(vehicle.getNotes());
            dataRow.createCell(13).setCellValue(formatLocalDateTime(vehicle.getCreateAt()));
            dataRow.createCell(14).setCellValue(formatLocalDateTime(vehicle.getUpdateAt()));
        }

        // Auto-size columns
        for (int i = 0; i < vehicleHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

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

    private String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : "";
    }
}