package com.ftcs.accountservice.driver.license.repository;


import com.ftcs.accountservice.driver.license.model.License;
import com.ftcs.accountservice.driver.shared.StatusDocumentType;
import jdk.jshell.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, Integer> {
    Optional<License> findLicenseByAccountId(Integer accountId);
    Optional<License> findLicenseByLicenseId(Integer licenseId);
    boolean existsByAccountIdAndStatus(Integer accountId, StatusDocumentType statusDocumentType);
    boolean existsByAccountId(Integer accountId);

}
