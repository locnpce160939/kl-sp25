package com.ftcs.registerdriver.repository;

import com.ftcs.registerdriver.model.License;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, Integer> {
    Optional<License> findLicenseByAccountId(Integer accountId);
    Optional<License> findLicenseByLicenseId(Integer licenseId);
    boolean existsByAccountId(Integer accountId);
}
