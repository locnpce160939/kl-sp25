package com.ftcs.accountservice.driver.identification.repository;


import com.ftcs.accountservice.driver.identification.model.AddressDriver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressDriverRepository extends JpaRepository<AddressDriver, Integer> {
    
}
