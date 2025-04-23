package com.ftcs.transportation.schedule.controller;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.transportation.TransportationURL;
import com.ftcs.transportation.schedule.dto.FindScheduleByTimePeriodRequestDTO;
import com.ftcs.transportation.schedule.dto.ScheduleRequestDTO;
import com.ftcs.transportation.schedule.dto.UpdateStatusScheduleRequestDTO;
import com.ftcs.transportation.schedule.model.Schedule;
import com.ftcs.transportation.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
                                              @PathVariable("scheduleId") Long scheduleId) {
        scheduleService.updateSchedule(requestDTO, scheduleId);
        return new ApiResponse<>("Schedule updated successfully");
    }

    @PutMapping("/updateStatus/{scheduleId}")
    public ApiResponse<String> updateStatusSchedule(@Valid @RequestBody UpdateStatusScheduleRequestDTO requestDTO,
                                                    @PathVariable("scheduleId") Long scheduleId) {
        scheduleService.updateStatusSchedule(requestDTO, scheduleId);
        return new ApiResponse<>("Schedule status updated successfully");
    }

    @GetMapping("/all/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<List<Schedule>> getAllSchedulesByAccountId(@PathVariable("accountId") Integer accountId) {
        List<Schedule> schedules = scheduleService.getAllSchedulesByAccountId(accountId);
        return new ApiResponse<>(schedules);
    }

    @GetMapping("/getScheduleByToken")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<List<Schedule>> getAllSchedulesByAccountIdOfDriver(@RequestAttribute("accountId") Integer accountId) {
        List<Schedule> schedules = scheduleService.getAllSchedulesByAccountIdOfDriver(accountId);
        return new ApiResponse<>(schedules);
    }

    @GetMapping("/all")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<Page<Schedule>> getAllSchedules(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<Schedule> schedules = scheduleService.getAllSchedules(page, size);
        return new ApiResponse<>(schedules);
    }

    @PostMapping("/filter")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<List<Schedule>> getSchedulesByTimeRangeAndStatus(@Valid @RequestBody FindScheduleByTimePeriodRequestDTO requestDTO) {
        List<Schedule> schedules = scheduleService.getSchedulesByTimeRangeAndStatus(requestDTO);
        return new ApiResponse<>(schedules);
    }

    @GetMapping("/{scheduleId}")
    public ApiResponse<Schedule> getScheduleById(@PathVariable("scheduleId") Long scheduleId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return new ApiResponse<>(schedule);
    }

    @GetMapping("/listSchedule/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<Page<Schedule>> getAllSchedulesByAccountId(@PathVariable("accountId") Integer accountId,
                                                                  @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                  @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<Schedule> schedules = scheduleService.getSchedulesByAccountIdWithPagination(accountId, page, size);
        return new ApiResponse<>(schedules);
    }

    @GetMapping("/account/paginated")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<Page<Schedule>> getSchedulesByAccountIdWithPagination(
            @RequestAttribute("accountId") Integer accountId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<Schedule> schedules = scheduleService.getSchedulesByAccountIdWithPagination(accountId, page, size);
        return new ApiResponse<>(schedules);
    }

    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<String> deleteSchedule(@PathVariable("scheduleId") Long scheduleId) {
        scheduleService.deleteScheduleById(scheduleId);
        return new ApiResponse<>("Schedule deleted successfully");
    }

}
