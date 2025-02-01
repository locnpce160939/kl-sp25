package com.ftcs.transportation.trip_matching.repository;

import com.ftcs.transportation.trip_matching.projection.ScheduleBookingProjection;
import com.ftcs.transportation.trip_matching.model.DirectionsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectionsDataRepository extends JpaRepository<DirectionsData, Long> {
    DirectionsData findByStartLocationLatAndStartLocationLngAndEndLocationLatAndEndLocationLng(String startLocationLat, String startLocationLng, String endLocationLat, String endLocationLng);

    @Query(value = "EXEC dbo.GetUnmatchedSchedulesAndBookings", nativeQuery = true)
    List<ScheduleBookingProjection> findUnmatchedSchedulesAndBookings();
}
