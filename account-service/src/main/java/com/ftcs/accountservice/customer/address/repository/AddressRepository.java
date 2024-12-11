package com.ftcs.accountservice.customer.address.repository;

import com.ftcs.accountservice.customer.address.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    Optional<Address> findAddressByAddressId(Integer addressId);
    List<Address> findAddressByAccountId(Integer accountId);
    boolean existsAddressByAccountId(Integer accountId);
}
