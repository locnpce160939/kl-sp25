package com.ftcs.authservice.features.account;

import com.ftcs.authservice.features.account.contacts.RoleType;
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
    boolean existsByEmailAndAccountIdNot(String email, Integer accountId);
    boolean existsByUsernameAndAccountIdNot(String username, Integer accountId);
    List<Account> findAllByStatusNot(String status);
    List<Account> findAllByRoleAndStatusNot(RoleType role, String status);
}
