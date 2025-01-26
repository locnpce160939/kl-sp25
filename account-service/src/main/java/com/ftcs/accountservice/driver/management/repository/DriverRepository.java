package com.ftcs.accountservice.driver.management.repository;

import com.ftcs.accountservice.driver.management.model.Driver;
import com.ftcs.accountservice.driver.management.projection.ListDriverProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Integer> {
    @Query(value = """
            EXEC  [dbo].[GetAllDriverDetails]
            """, nativeQuery = true)
    List<ListDriverProjection> getAllDrivers();

    @Query(value = """
        EXEC [dbo].[GetAllDriverByProvinces] @ProvinceCodes = :provinceCodes
        """, nativeQuery = true)
    List<ListDriverProjection> getAllDriversByProvinces(@Param("provinceCodes") String provinceCodes);
}
