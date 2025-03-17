package com.ftcs.balanceservice.balance_history.repository;

import com.ftcs.balanceservice.balance_history.model.BalanceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, Long> {
    Page<BalanceHistory> findByAccountId(Integer accountId, Pageable pageable);
    List<BalanceHistory> findByAccountId(Integer accountId);
}
