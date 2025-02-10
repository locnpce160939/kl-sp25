package com.ftcs.common.feature.location.repository;

import com.ftcs.common.feature.location.model.GeocodingData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeocodingDataRepository extends JpaRepository<GeocodingData, Long> {
    Optional<GeocodingData> findByAddress(String address);
}
