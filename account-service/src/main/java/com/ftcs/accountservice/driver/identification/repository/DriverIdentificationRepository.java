package com.ftcs.accountservice.driver.identification.repository;


import com.ftcs.accountservice.driver.identification.model.DriverIdentification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverIdentificationRepository extends JpaRepository<DriverIdentification, Integer> {
    Optional<DriverIdentification> findDriverIdentificationByAccountId(Integer accountId);
    Optional<DriverIdentification> findDriverIdentificationByDriverIdentificationId(Integer driverIdentificationId);
    boolean existsByAccountId(Integer accountId);
}
