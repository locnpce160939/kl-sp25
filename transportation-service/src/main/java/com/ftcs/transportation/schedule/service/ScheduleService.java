package com.ftcs.transportation.schedule.service;

import com.ftcs.accountservice.account.serivce.AccountService;
import com.ftcs.accountservice.driver.shared.StatusDocumentType;
import com.ftcs.accountservice.driver.vehicle.model.Vehicle;
import com.ftcs.accountservice.driver.vehicle.repository.VehicleRepository;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.contacts.StatusAccount;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.transportation.schedule.constant.ScheduleStatus;
import com.ftcs.transportation.schedule.dto.FindScheduleByTimePeriodRequestDTO;
import com.ftcs.transportation.schedule.dto.ScheduleRequestDTO;
import com.ftcs.transportation.schedule.dto.UpdateStatusScheduleRequestDTO;
import com.ftcs.transportation.schedule.model.Schedule;
import com.ftcs.transportation.schedule.repository.ScheduleRepository;
import com.ftcs.transportation.trip_agreement.model.TripAgreement;
import com.ftcs.transportation.trip_agreement.repository.TripAgreementRepository;
import com.ftcs.transportation.trip_booking.constant.TripBookingStatus;
import com.ftcs.transportation.trip_booking.repository.TripBookingsRepository;
import com.ftcs.transportation.trip_matching.service.TripAcceptanceService;
import com.ftcs.transportation.trip_matching.service.TripMatchingService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class ScheduleService {
    private final TripMatchingService tripMatchingService;
    private final ScheduleRepository scheduleRepository;
    private final VehicleRepository vehicleRepository;
    private final AccountService accountService;
    private final TripAcceptanceService tripAcceptanceService;
    private final TripAgreementRepository tripAgreementRepository;
    private final TripBookingsRepository tripBookingsRepository;

    public Schedule createSchedule(ScheduleRequestDTO requestDTO, Integer accountId) {
        validateStatusAccount(accountId);
        getValidatedVehicle(requestDTO.getVehicleId(), accountId);
        Schedule schedule = buildNewSchedule(requestDTO, accountId);
        scheduleRepository.save(schedule);
        tripMatchingService.matchTripsForAll();
        return schedule;
    }

    private void getValidatedVehicle(Integer vehicleId, Integer accountId) {
        Vehicle vehicle = vehicleRepository.findVehicleByVehicleId(vehicleId)
                .orElseThrow(() -> new BadRequestException("Vehicle not found"));

        if (!vehicle.getStatus().equals(StatusDocumentType.APPROVED)) {
            throw new BadRequestException("Vehicle is not approved");
        }

        boolean isOwnedByAccount = vehicleRepository.findVehiclesByAccountId(accountId)
                .stream()
                .anyMatch(v -> v.getVehicleId().equals(vehicleId));

        if (!isOwnedByAccount) {
            throw new BadRequestException("Vehicle does not belong to this account");
        }
    }

    private Schedule buildNewSchedule(ScheduleRequestDTO requestDTO, Integer accountId) {
        Schedule schedule = new Schedule();
        schedule.setAccountId(accountId);
        schedule.setVehicleId(requestDTO.getVehicleId());
        mapScheduleRequestToEntity(requestDTO, schedule);
        schedule.setStatus(ScheduleStatus.WAITING_FOR_DELIVERY);
        return schedule;
    }

    public void updateSchedule(ScheduleRequestDTO requestDTO, Long scheduleId) {
        Schedule schedule = getScheduleById(scheduleId);
        mapScheduleRequestToEntity(requestDTO, schedule);
        schedule.setUpdateAt(LocalDateTime.now());
        scheduleRepository.save(schedule);
    }

    public void updateStatusSchedule(UpdateStatusScheduleRequestDTO requestDTO, Long scheduleId) {
        Schedule schedule = getScheduleById(scheduleId);
        schedule.setStatus(requestDTO.getStatus());
        schedule.setUpdateAt(LocalDateTime.now());
        scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedulesByAccountId(Integer accountId) {
        return scheduleRepository.findAllByAccountId(accountId);
    }

    public Page<Schedule> getSchedulesByAccountIdWithPagination(Integer accountId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return scheduleRepository.findAllByAccountId(accountId, pageable);
    }

    public List<Schedule> getAllSchedulesByAccountIdOfDriver(Integer accountId) {
        return scheduleRepository.findAllByAccountId(accountId);
    }

    public Schedule getScheduleById(Long scheduleId) {
        return scheduleRepository.findScheduleByScheduleId(scheduleId)
                .orElseThrow(() -> new BadRequestException("Schedule does not exist"));
    }

    public void deleteScheduleById(Long scheduleId) {
        List<TripAgreement> tripAgreements = tripAgreementRepository.findAllByScheduleId(scheduleId);
        boolean hasIncompleteBooking = tripAgreements.stream()
            .map(agreement -> tripBookingsRepository.findTripBookingsByBookingId(agreement.getBookingId())
                .orElseThrow(() -> new BadRequestException("Trip booking not found")))
            .anyMatch(booking -> booking.getStatus() != TripBookingStatus.ORDER_COMPLETED);
            
        if (hasIncompleteBooking) {
            throw new BadRequestException("Cannot delete schedule because there are incomplete trip bookings");
        }
        
        scheduleRepository.deleteById(scheduleId);
    }

    public Page<Schedule> getAllSchedules(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return scheduleRepository.findAll(pageable);
    }

    public List<Schedule> getSchedulesByTimeRangeAndStatus(FindScheduleByTimePeriodRequestDTO requestDTO) {
        boolean hasDateRange = requestDTO.getStartDate() != null && requestDTO.getEndDate() != null;
        boolean hasStatus = requestDTO.getStatus() != null;
        List<Schedule> schedules;

        if (hasDateRange && hasStatus) {
            schedules = scheduleRepository.findAllByStartDateBetweenAndStatus(
                    requestDTO.getStartDate(), requestDTO.getEndDate(), requestDTO.getStatus());
        } else if (hasDateRange) {
            schedules = scheduleRepository.findAllByStartDateBetween(
                    requestDTO.getStartDate(), requestDTO.getEndDate());
        } else if (hasStatus) {
            schedules = scheduleRepository.findAllByStatus(requestDTO.getStatus());
        } else {
            schedules = scheduleRepository.findAll();
        }

        if (schedules.isEmpty()) {
            throw new BadRequestException("No schedules found with the given criteria.");
        }
        return schedules;
    }

    private void mapScheduleRequestToEntity(ScheduleRequestDTO requestDTO, Schedule schedule) {
        schedule.setStartLocation(requestDTO.getStartLocation());
        schedule.setEndLocation(requestDTO.getEndLocation());
        schedule.setStartDate(requestDTO.getStartDate());
        schedule.setAvailableCapacity(requestDTO.getAvailableCapacity());
        schedule.setStartLocationAddress(requestDTO.getStartLocationAddress());
        schedule.setEndLocationAddress(requestDTO.getEndLocationAddress());
    }

    private void validateStatusAccount(Integer accountId) {
        Account account = accountService.getAccountById(accountId);
        if(account.getStatus() == StatusAccount.PENDING){
            throw new BadRequestException("Account is already pending.");
        }
    }
}
