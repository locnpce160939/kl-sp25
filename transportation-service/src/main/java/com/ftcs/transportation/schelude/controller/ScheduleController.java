package com.ftcs.transportation.schelude.controller;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.transportation.TransportationURL;
import com.ftcs.transportation.schelude.dto.FindScheduleByTimePeriodRequestDTO;
import com.ftcs.transportation.schelude.dto.ScheduleRequestDTO;
import com.ftcs.transportation.schelude.dto.UpdateStatusScheduleRequestDTO;
import com.ftcs.transportation.schelude.model.Schedule;
import com.ftcs.transportation.schelude.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(TransportationURL.SCHEDULE)
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping("/create")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<Schedule> createSchedule(@Valid @RequestBody ScheduleRequestDTO requestDTO,
                                                @RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(scheduleService.createSchedule(requestDTO, accountId));
    }

    @PutMapping("/update/{scheduleId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<String> updateSchedule(@Valid @RequestBody ScheduleRequestDTO requestDTO,
                                              @PathVariable("scheduleId") Integer scheduleId) {
        scheduleService.updateSchedule(requestDTO, scheduleId);
        return new ApiResponse<>("Schedule updated successfully");
    }

    @PutMapping("/updateStatus/{scheduleId}")
    public ApiResponse<String> updateStatusSchedule(@Valid @RequestBody UpdateStatusScheduleRequestDTO requestDTO,
                                                    @PathVariable("scheduleId") Integer scheduleId) {
        scheduleService.updateStatusSchedule(requestDTO, scheduleId);
        return new ApiResponse<>("Schedule status updated successfully");
    }

    @GetMapping("/all/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<List<Schedule>> getAllSchedulesByAccountId(@PathVariable("accountId") Integer accountId) {
        List<Schedule> schedules = scheduleService.getAllSchedulesByAccountId(accountId);
        return new ApiResponse<>(schedules);
    }

    @GetMapping("/all")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return new ApiResponse<>(schedules);
    }

    @PostMapping("/filter")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
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
