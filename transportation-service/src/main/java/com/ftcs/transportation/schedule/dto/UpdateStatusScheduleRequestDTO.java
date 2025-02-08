package com.ftcs.transportation.schedule.dto;

import com.ftcs.transportation.schedule.constant.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusScheduleRequestDTO {
    private ScheduleStatus status;
}
