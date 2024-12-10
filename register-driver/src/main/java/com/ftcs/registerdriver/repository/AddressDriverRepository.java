package com.ftcs.registerdriver.repository;

import com.ftcs.registerdriver.model.AddressDriver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressDriverRepository extends JpaRepository<AddressDriver, Integer> {
    
}
