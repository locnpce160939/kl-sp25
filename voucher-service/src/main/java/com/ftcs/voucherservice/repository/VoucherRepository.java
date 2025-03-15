package com.ftcs.voucherservice.repository;

import com.ftcs.voucherservice.constant.UserType;
import com.ftcs.voucherservice.constant.VoucherStatus;
import com.ftcs.voucherservice.constant.VoucherType;
import com.ftcs.voucherservice.model.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Boolean existsByCode(String code);
    List<Voucher> findAllByStatus(VoucherStatus status);
    Page<Voucher> findAllByStatus(VoucherStatus status, Pageable pageable);
    Optional<Voucher> findByCode(String code);
    List<Voucher> findAllByStatusAndUserType(VoucherStatus status, UserType userType);
    List<Voucher> findAllByVoucherType(VoucherType voucherType);
    List<Voucher> findAllByStatusAndVoucherType(VoucherStatus status, VoucherType voucherType);
    Page<Voucher> findByStatusNot(VoucherStatus status, Pageable pageable);
    Page<Voucher> findByPointsRequiredGreaterThan(Integer pointsThreshold, Pageable pageable);
    @Query("SELECT v FROM Voucher v WHERE v.pointsRequired IS NULL OR v.pointsRequired = 0")
    Page<Voucher> findSystemVouchers(Pageable pageable);



}
