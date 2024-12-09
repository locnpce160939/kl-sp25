package com.ftcs.authservice.features.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findAccountByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByAccountId(Integer accountId);
    boolean existsByEmail(String email);
    Optional<Account> findAccountByAccountId(Integer accountId);
    Account findAccountByEmail(String email);

}
