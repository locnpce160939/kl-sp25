package com.ftcs.accountservice.driver.identification.repository;


import com.ftcs.accountservice.driver.identification.model.DriverIdentification;
import com.ftcs.accountservice.driver.shared.StatusDocumentType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DriverIdentificationRepository extends JpaRepository<DriverIdentification, Integer> {
    //Optional<DriverIdentification> findDriverIdentificationByAccountId(Integer accountId);

    Optional<DriverIdentification> findDriverIdentificationByDriverIdentificationId(Integer driverIdentificationId);
    Optional<DriverIdentification> findByAccountId(Integer accountId);

    boolean existsByAccountIdAndStatus(Integer accountId, StatusDocumentType statusDocumentType);

    boolean existsByAccountId(Integer accountId);

    List<DriverIdentification> findByPermanentAddressInOrTemporaryAddressIn(List<Integer> permanentAddressDriverIds, List<Integer> temporaryAddressDriverIds);

    @EntityGraph(attributePaths = {"permanentAddress", "temporaryAddress"})
    Optional<DriverIdentification> findDriverIdentificationByAccountId(Integer accountId);
}
