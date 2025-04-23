package com.ftcs.transportation.schedule.repository;

import com.ftcs.transportation.schedule.constant.ScheduleStatus;
import com.ftcs.transportation.schedule.model.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findScheduleByScheduleId(Long scheduleId);

    List<Schedule> findAllByAccountId(Integer accountId);

    Optional<Schedule> findScheduleByAccountId(Integer accountId);

    List<Schedule> findAllByStartDateBetweenAndStatus(LocalDateTime startDate, LocalDateTime startDate2, ScheduleStatus status);

    List<Schedule> findAllByStatus(ScheduleStatus status);

    List<Schedule> findAllByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Schedule> findAllByStartDateBetweenAndStatusIsNull(LocalDateTime startDate, LocalDateTime endDate);

    Page<Schedule> findAllByAccountId(Integer accountId, Pageable pageable);
}
