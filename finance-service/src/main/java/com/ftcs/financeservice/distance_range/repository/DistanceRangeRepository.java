package com.ftcs.financeservice.distance_range.repository;

import com.ftcs.financeservice.distance_range.model.DistanceRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DistanceRangeRepository extends JpaRepository<DistanceRange, Integer> {
    Optional<DistanceRange> findByDistanceRangeId(int distanceRangeId);
    @Query("SELECT COUNT(d) > 0 FROM DistanceRange d " +
            "WHERE NOT (:maxKm < d.minKm OR :minKm > d.maxKm)")
    boolean existsByOverlappingRange(@Param("minKm") Double minKm, @Param("maxKm") Double maxKm);
    @Query("SELECT COUNT(d) > 0 FROM DistanceRange d " +
            "WHERE NOT (:maxKm < d.minKm OR :minKm > d.maxKm) " +
            "AND d.distanceRangeId <> :distanceRangeId")
    boolean existsByOverlappingRangeExcludingId(@Param("minKm") Double minKm,
                                                @Param("maxKm") Double maxKm,
                                                @Param("distanceRangeId") Integer distanceRangeId);
}
