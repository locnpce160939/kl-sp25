package com.ftcs.balanceservice.withdraw.repository;

import com.ftcs.balanceservice.withdraw.model.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {
    Optional<Withdraw> findByWithdrawId(Long withdrawId);
    List<Withdraw> findAllByAccountId(Integer accountId);
}
