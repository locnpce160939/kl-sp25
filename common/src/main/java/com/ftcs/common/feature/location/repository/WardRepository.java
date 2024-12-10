package com.ftcs.common.feature.location.repository;

import com.ftcs.common.feature.location.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WardRepository extends JpaRepository<Ward, Integer> {
    List<Ward> findByDistrictCode(Integer districtCode);
    boolean existsByCodeAndDistrictCode(Integer wardCode, Integer districtCode);
}
