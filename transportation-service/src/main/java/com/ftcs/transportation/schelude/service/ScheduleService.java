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

    public Schedule createSchedule(ScheduleRequestDTO scheduleRequestDTO, Integer accountId) {
        validateScheduleDates(scheduleRequestDTO.getStartDate(), scheduleRequestDTO.getEndDate());
        Schedule schedule = new Schedule();
        schedule.setAccountId(accountId);
        mapScheduleRequestToEntity(scheduleRequestDTO, schedule);
        schedule.setStatus("Waiting for delivery");
        return scheduleRepository.save(schedule);
    }

    public void updateSchedule(ScheduleRequestDTO scheduleRequestDTO, Integer scheduleId) {
        Schedule schedule = getScheduleById(scheduleId);
        validateScheduleDates(scheduleRequestDTO.getStartDate(), scheduleRequestDTO.getEndDate());
        mapScheduleRequestToEntity(scheduleRequestDTO, schedule);
        schedule.setUpdateAt(LocalDateTime.now());
        scheduleRepository.save(schedule);
    }

    public void updateStatusSchedule(UpdateStatusScheduleRequestDTO updateStatusScheduleRequestDTO, Integer scheduleId) {
        Schedule schedule = getScheduleById(scheduleId);
        schedule.setStatus(updateStatusScheduleRequestDTO.getStatus());
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

    public List<Schedule> getSchedulesByTimeRangeAndStatus(FindScheduleByTimePeriodRequestDTO findScheduleByTimePeriodRequestDTO) {
        List<Schedule> schedules;
        if (findScheduleByTimePeriodRequestDTO.getStartDate() != null && findScheduleByTimePeriodRequestDTO.getEndDate() != null) {
            if (findScheduleByTimePeriodRequestDTO.getStatus() != null && !findScheduleByTimePeriodRequestDTO.getStatus().isEmpty()) {
                schedules = scheduleRepository.findAllByStartDateBetweenAndStatus(
                        findScheduleByTimePeriodRequestDTO.getStartDate(),
                        findScheduleByTimePeriodRequestDTO.getEndDate(),
                        findScheduleByTimePeriodRequestDTO.getStatus());
            } else {
                schedules = scheduleRepository.findAllByStartDateBetween(
                        findScheduleByTimePeriodRequestDTO.getStartDate(),
                        findScheduleByTimePeriodRequestDTO.getEndDate());
            }
        } else {
            if (findScheduleByTimePeriodRequestDTO.getStatus() != null && !findScheduleByTimePeriodRequestDTO.getStatus().isEmpty()) {
                schedules = scheduleRepository.findAllByStatus(findScheduleByTimePeriodRequestDTO.getStatus());
            } else {
                schedules = scheduleRepository.findAll();
            }
        }
        if (schedules.isEmpty()) {
            throw new BadRequestException("No schedules found with the given criteria.");
        }
        return schedules;
    }


    private void mapScheduleRequestToEntity(ScheduleRequestDTO scheduleRequestDTO, Schedule schedule) {
        schedule.setStartLocation(scheduleRequestDTO.getStartLocation());
        schedule.setEndLocation(scheduleRequestDTO.getEndLocation());
        schedule.setStartDate(scheduleRequestDTO.getStartDate());
        schedule.setEndDate(scheduleRequestDTO.getEndDate());
        schedule.setAvailableCapacity(scheduleRequestDTO.getAvailableCapacity());
    }

    private void validateScheduleDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new BadRequestException("End date must be after start date.");
        }
    }
}
