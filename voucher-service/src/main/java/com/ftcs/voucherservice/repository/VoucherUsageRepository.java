package com.ftcs.voucherservice.repository;

import com.ftcs.voucherservice.model.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long> {
    Optional<VoucherUsage> findByAccountIdAndVoucherId(Integer accountId, Long voucherId);
    boolean existsByAccountIdAndVoucherId(Integer accountId, Long voucherId);
    List<VoucherUsage> findByAccountId(Integer accountId);
}
