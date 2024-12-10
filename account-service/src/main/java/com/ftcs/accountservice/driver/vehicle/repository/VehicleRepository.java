package com.ftcs.accountservice.driver.vehicle.repository;


import com.ftcs.accountservice.driver.vehicle.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findVehicleByAccountId(Integer accountId);
    Optional<Vehicle> findVehicleByVehicleId(Integer vehicleId);
    boolean existsByAccountId(Integer accountId);
}
