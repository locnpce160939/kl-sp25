package com.ftcs.transportation.schedule.repository;

import com.ftcs.transportation.schedule.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findScheduleByScheduleId(Long scheduleId);

    List<Schedule> findAllByAccountId(Integer accountId);

    Optional<Schedule> findScheduleByAccountId(Integer accountId);

    List<Schedule> findAllByStartDateBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, String status);

    List<Schedule> findAllByStatus(String status);

    List<Schedule> findAllByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Schedule> findAllByStartDateBetweenAndStatusIsNull(LocalDateTime startDate, LocalDateTime endDate);
}
