package com.ftcs.balanceservice.balance_history.repository;

import com.ftcs.balanceservice.balance_history.model.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, Long> {
    List<BalanceHistory> findByAccountId(Integer accountId);
}
