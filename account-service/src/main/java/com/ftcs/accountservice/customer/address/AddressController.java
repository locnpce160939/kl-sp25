package com.ftcs.accountservice.customer.address;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.customer.address.dto.AddressRequestDTO;
import com.ftcs.accountservice.customer.address.model.Address;
import com.ftcs.accountservice.customer.address.service.AddressService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.ADDRESS)
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/createAddress")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<?> createAddress(@Valid @RequestBody AddressRequestDTO requestDTO,
                                        @RequestAttribute("accountId") Integer accountId) {
        addressService.createAddressCustomer(requestDTO, accountId);
        return new ApiResponse<>("Created address successfully");
    }

    @PutMapping("/updateAddress/{addressId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<?> updateAddress(@Valid @RequestBody AddressRequestDTO requestDTO,
                                        @PathVariable("addressId") Integer addressId,
                                        @RequestAttribute("accountId") Integer accountId) {
        addressService.updateAddressCustomer(addressId, requestDTO, accountId);
        return new ApiResponse<>("Updated address successfully");
    }

    @DeleteMapping("/deleteAddress/{addressId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<?> deleteAddress(@PathVariable("addressId") Integer addressId,
                                        @RequestAttribute("accountId") Integer accountId) {
        addressService.deleteAddressCustomer(addressId, accountId);
        return new ApiResponse<>("Deleted address successfully");
    }

    @PutMapping("/setDefaultAddress/{addressId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<?> setDefaultAddress(@PathVariable("addressId") Integer addressId,
                                            @RequestAttribute("accountId") Integer accountId) {
        addressService.setDefaultAddress(addressId, accountId);
        return new ApiResponse<>("Set address as default successfully");
    }

    @GetMapping("/getAllAddresses")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<List<Address>> getAllAddresses(@RequestAttribute("accountId") Integer accountId) {
        List<Address> addresses = addressService.getAllAddressByAccountId(accountId);
        return new ApiResponse<>("Fetched addresses successfully", addresses);
    }
}