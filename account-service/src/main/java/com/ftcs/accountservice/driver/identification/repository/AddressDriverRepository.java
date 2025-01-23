package com.ftcs.accountservice.driver.identification.repository;


import com.ftcs.accountservice.driver.identification.model.AddressDriver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressDriverRepository extends JpaRepository<AddressDriver, Integer> {
    List<AddressDriver> findByProvinceIdIn(List<Integer> provinceIds);
}
