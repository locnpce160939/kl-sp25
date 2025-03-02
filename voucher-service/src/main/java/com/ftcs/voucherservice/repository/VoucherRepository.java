package com.ftcs.voucherservice.repository;

import com.ftcs.voucherservice.constant.VoucherStatus;
import com.ftcs.voucherservice.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Boolean existsByCode(String code);
    List<Voucher> findAllByStatus(VoucherStatus status);
    Optional<Voucher> findByCode(String code);

}
