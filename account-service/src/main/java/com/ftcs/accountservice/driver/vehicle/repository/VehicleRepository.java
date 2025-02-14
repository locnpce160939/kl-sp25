package com.ftcs.accountservice.driver.vehicle.repository;


import com.ftcs.accountservice.driver.shared.StatusDocumentType;
import com.ftcs.accountservice.driver.vehicle.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    List<Vehicle> findVehiclesByAccountId(Integer accountId);
    Optional<Vehicle> findVehicleByVehicleId(Integer vehicleId);
    boolean existsByAccountIdAndStatus(Integer accountId, StatusDocumentType statusDocumentType);
    List<Vehicle> findByAccountIdAndStatus(Integer accountId, StatusDocumentType status);

}
