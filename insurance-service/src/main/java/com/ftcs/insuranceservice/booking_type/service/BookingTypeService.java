package com.ftcs.insuranceservice.booking_type.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.insuranceservice.booking_type.dto.BookingTypeRequestDTO;
import com.ftcs.insuranceservice.booking_type.model.BookingType;
import com.ftcs.insuranceservice.booking_type.repository.BookingTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class BookingTypeService {
    private final BookingTypeRepository bookingTypeRepository;

    public BookingType createBookingType(BookingTypeRequestDTO requestDTO){
        validateBookingType(requestDTO);
        BookingType bookingType = BookingType.builder()
                .bookingTypeName(requestDTO.getBookingTypeName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        bookingTypeRepository.save(bookingType);
        return bookingType;
    }

    public Page<BookingType> getAllBookingTypes(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size);
        return bookingTypeRepository.findAll(pageable);
    }

    public BookingType updateBookingType(BookingTypeRequestDTO requestDTO, Long bookingTypeId){
        validateBookingType(requestDTO);
        BookingType bookingType = getBookingType(bookingTypeId);
        bookingType.setBookingTypeName(requestDTO.getBookingTypeName());
        bookingType.setUpdatedAt(LocalDateTime.now());
        bookingTypeRepository.save(bookingType);
        return bookingType;
    }

    public void deleteBookingType(Long bookingTypeId){
        bookingTypeRepository.deleteById(bookingTypeId);
    }

    public BookingType getBookingType(Long bookingTypeId){
        return bookingTypeRepository.findByBookingTypeId(bookingTypeId).
                orElseThrow(() -> new BadRequestException("Booking type not found"));
    }

    private void validateBookingType(BookingTypeRequestDTO requestDTO){
        if(bookingTypeRepository.existsByBookingTypeName(requestDTO.getBookingTypeName())){
            throw new BadRequestException("Booking type name is already exits!");
        }
    }
}
