package com.ftcs.transportation.schelude.dto;

import com.ftcs.transportation.schelude.constant.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusScheduleRequestDTO {
    private ScheduleStatus status;
}
