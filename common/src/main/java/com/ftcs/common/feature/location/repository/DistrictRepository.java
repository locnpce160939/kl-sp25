package com.ftcs.common.feature.location.repository;

import com.ftcs.common.feature.location.model.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Integer> {
    List<District> findByProvinceCode(Integer provinceCode);
    boolean existsByCodeAndProvinceCode(Integer districtCode, Integer provinceCode);
}
