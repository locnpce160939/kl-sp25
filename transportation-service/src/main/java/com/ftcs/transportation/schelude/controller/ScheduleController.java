package com.ftcs.transportation.schelude.controller;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.transportation.schelude.ScheduleURL;
import com.ftcs.transportation.schelude.dto.FindScheduleByTimePeriodRequestDTO;
import com.ftcs.transportation.schelude.dto.ScheduleRequestDTO;
import com.ftcs.transportation.schelude.dto.UpdateStatusScheduleRequestDTO;
import com.ftcs.transportation.schelude.model.Schedule;
import com.ftcs.transportation.schelude.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ScheduleURL.SCHEDULE)
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping("/create")
    public ApiResponse<Schedule> createSchedule(@Valid @RequestBody ScheduleRequestDTO scheduleRequestDTO,
                                                @RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(scheduleService.createSchedule(scheduleRequestDTO, accountId));
    }

    @PutMapping("/update/{scheduleId}")
    public ApiResponse<String> updateSchedule(@Valid @RequestBody ScheduleRequestDTO scheduleRequestDTO,
                                              @PathVariable("scheduleId") Integer scheduleId) {
        scheduleService.updateSchedule(scheduleRequestDTO, scheduleId);
        return new ApiResponse<>("Schedule updated successfully");
    }

    @PutMapping("/updateStatus/{scheduleId}")
    public ApiResponse<String> updateStatusSchedule(@Valid @RequestBody UpdateStatusScheduleRequestDTO updateStatusScheduleRequestDTO,
                                                    @PathVariable("scheduleId") Integer scheduleId) {
        scheduleService.updateStatusSchedule(updateStatusScheduleRequestDTO, scheduleId);
        return new ApiResponse<>("Schedule status updated successfully");
    }

    @GetMapping("/all/{accountId}")
    public ApiResponse<List<Schedule>> getAllSchedulesByAccountId(@PathVariable("accountId") Integer accountId) {
        List<Schedule> schedules = scheduleService.getAllSchedulesByAccountId(accountId);
        return new ApiResponse<>(schedules);
    }

    @GetMapping("/all")
    public ApiResponse<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return new ApiResponse<>(schedules);
    }

    @PostMapping("/filter")
    public ApiResponse<List<Schedule>> getSchedulesByTimeRangeAndStatus(@Valid @RequestBody FindScheduleByTimePeriodRequestDTO requestDTO) {
        List<Schedule> schedules = scheduleService.getSchedulesByTimeRangeAndStatus(requestDTO);
        return new ApiResponse<>(schedules);
    }

    @GetMapping("/{scheduleId}")
    public ApiResponse<Schedule> getScheduleById(@PathVariable("scheduleId") Integer scheduleId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return new ApiResponse<>(schedule);
    }
}
