package com.ftcs.accountservice.areamanagement.repository;

import com.ftcs.accountservice.areamanagement.model.AreaManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AreaManagementRepository extends JpaRepository<AreaManagement, Integer> {
    Optional<AreaManagement> findByAccountIdAndProvinceId(Integer accountId, Integer provinceId);
    List<AreaManagement> findByAccountId(Integer accountId);
}
