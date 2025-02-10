package com.ftcs.transportation.schedule.service;

import com.ftcs.accountservice.driver.vehicle.model.Vehicle;
import com.ftcs.accountservice.driver.vehicle.repository.VehicleRepository;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.transportation.schedule.constant.ScheduleStatus;
import com.ftcs.transportation.schedule.dto.FindScheduleByTimePeriodRequestDTO;
import com.ftcs.transportation.schedule.dto.ScheduleRequestDTO;
import com.ftcs.transportation.schedule.dto.UpdateStatusScheduleRequestDTO;
import com.ftcs.transportation.schedule.model.Schedule;
import com.ftcs.transportation.schedule.repository.ScheduleRepository;
import com.ftcs.transportation.trip_matching.service.TripMatchingService;
import lombok.AllArgsConstructor;
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

    public Schedule createSchedule(ScheduleRequestDTO requestDTO, Integer accountId) {
        Vehicle vehicle = vehicleRepository.findVehicleByVehicleId(requestDTO.getVehicleId()).
                orElseThrow(() -> new BadRequestException("Vehicle not found"));
        validateScheduleDates(requestDTO.getStartDate(), requestDTO.getEndDate());
        List<Schedule> schedules = scheduleRepository.findAllByAccountId(accountId);

        if (!schedules.isEmpty()) {
            Schedule latestSchedule = schedules.stream()
                    .max(Comparator.comparing(Schedule::getEndDate))
                    .orElseThrow(() -> new BadRequestException("Unable to find latest schedule."));
            if (!requestDTO.getStartDate().isAfter(latestSchedule.getEndDate().plusDays(1))) {
                throw new BadRequestException("The new schedule must start at least one day after the last schedule's end date.");
            }
        }
        Schedule schedule = new Schedule();
        schedule.setAccountId(accountId);
        schedule.setVehicleId(requestDTO.getVehicleId());
        mapScheduleRequestToEntity(requestDTO, schedule);
        schedule.setStatus(ScheduleStatus.WAITING_FOR_DELIVERY);
        scheduleRepository.save(schedule);
        tripMatchingService.matchTripsForAll();
        return schedule;
    }



    public void updateSchedule(ScheduleRequestDTO requestDTO, Long scheduleId) {
        Schedule schedule = getScheduleById(scheduleId);
        validateScheduleDates(requestDTO.getStartDate(), requestDTO.getEndDate());
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

    public List<Schedule> getAllSchedulesByAccountIdOfDriver(Integer accountId) {
        return  scheduleRepository.findAllByAccountId(accountId);
    }

    public Schedule getScheduleById(Long scheduleId) {
        return scheduleRepository.findScheduleByScheduleId(scheduleId)
                .orElseThrow(() -> new BadRequestException("Schedule does not exist"));
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public List<Schedule> getSchedulesByTimeRangeAndStatus(FindScheduleByTimePeriodRequestDTO requestDTO) {
        boolean hasDateRange = requestDTO.getStartDate() != null && requestDTO.getEndDate() != null;
        boolean hasStatus = requestDTO.getStatus() != null && !requestDTO.getStatus().isEmpty();
        List<Schedule> schedules;
        if (hasDateRange && hasStatus) {
            schedules = scheduleRepository.findAllByStartDateBetweenAndStatus(
                    requestDTO.getStartDate(),
                    requestDTO.getEndDate(),
                    requestDTO.getStatus()
            );
        } else if (hasDateRange) {
            schedules = scheduleRepository.findAllByStartDateBetween(
                    requestDTO.getStartDate(),
                    requestDTO.getEndDate()
            );
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
        schedule.setEndDate(requestDTO.getEndDate());
        schedule.setAvailableCapacity(requestDTO.getAvailableCapacity());
        schedule.setStartLocationAddress(requestDTO.getStartLocationAddress());
        schedule.setEndLocationAddress(requestDTO.getEndLocationAddress());

    }

    private void validateScheduleDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new BadRequestException("End date must be after start date.");
        }
    }
}
