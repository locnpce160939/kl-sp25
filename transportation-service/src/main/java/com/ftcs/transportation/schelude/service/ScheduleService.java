package com.ftcs.transportation.schelude.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.transportation.schelude.dto.FindScheduleByTimePeriodRequestDTO;
import com.ftcs.transportation.schelude.dto.ScheduleRequestDTO;
import com.ftcs.transportation.schelude.dto.UpdateStatusScheduleRequestDTO;
import com.ftcs.transportation.schelude.model.Schedule;
import com.ftcs.transportation.schelude.repository.ScheduleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public Schedule createSchedule(ScheduleRequestDTO requestDTO, Integer accountId) {
        validateScheduleDates(requestDTO.getStartDate(), requestDTO.getEndDate());
        List<Schedule> schedules = scheduleRepository.findAllByAccountId(accountId);

        if (!schedules.isEmpty()) {
            Schedule latestSchedule = schedules.stream()
                    .max((s1, s2) -> s1.getEndDate().compareTo(s2.getEndDate()))
                    .orElseThrow(() -> new BadRequestException("Unable to find latest schedule."));
            if (!requestDTO.getStartDate().isAfter(latestSchedule.getEndDate().plusDays(1))) {
                throw new BadRequestException("The new schedule must start at least one day after the last schedule's end date.");
            }
        }
        Schedule schedule = new Schedule();
        schedule.setAccountId(accountId);
        mapScheduleRequestToEntity(requestDTO, schedule);
        schedule.setStatus("Waiting for delivery");
        return scheduleRepository.save(schedule);
    }


    public void updateSchedule(ScheduleRequestDTO requestDTO, Integer scheduleId) {
        Schedule schedule = getScheduleById(scheduleId);
        validateScheduleDates(requestDTO.getStartDate(), requestDTO.getEndDate());
        mapScheduleRequestToEntity(requestDTO, schedule);
        schedule.setUpdateAt(LocalDateTime.now());
        scheduleRepository.save(schedule);
    }

    public void updateStatusSchedule(UpdateStatusScheduleRequestDTO requestDTO, Integer scheduleId) {
        Schedule schedule = getScheduleById(scheduleId);
        schedule.setStatus(requestDTO.getStatus());
        schedule.setUpdateAt(LocalDateTime.now());
        scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedulesByAccountId(Integer accountId) {
        List<Schedule> schedules = scheduleRepository.findAllByAccountId(accountId);
        if (schedules == null || schedules.isEmpty()) {
            throw new BadRequestException("No schedules found for the specified account.");
        }
        return schedules;
    }

    public Schedule getScheduleById(Integer scheduleId) {
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
    }

    private void validateScheduleDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new BadRequestException("End date must be after start date.");
        }
    }
}
