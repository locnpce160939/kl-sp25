package com.ftcs.transportation.trip_matching.repository;

import com.ftcs.transportation.trip_matching.model.TripMatchingCache;
import com.ftcs.transportation.trip_matching.model.TripMatchingFinal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TripMatchingFinalRepository extends JpaRepository<TripMatchingFinal, Long> {
}
