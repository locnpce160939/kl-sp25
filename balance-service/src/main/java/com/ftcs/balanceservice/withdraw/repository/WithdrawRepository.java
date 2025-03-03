package com.ftcs.balanceservice.withdraw.repository;

import com.ftcs.balanceservice.withdraw.constant.WithdrawStatus;
import com.ftcs.balanceservice.withdraw.model.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {
    Optional<Withdraw> findByWithdrawId(Long withdrawId);
    List<Withdraw> findAllByAccountId(Integer accountId);
    List<Withdraw> findAllByStatus(WithdrawStatus status);
    List<Withdraw> findAllByRequestDateBetween(LocalDateTime start, LocalDateTime end);



}
