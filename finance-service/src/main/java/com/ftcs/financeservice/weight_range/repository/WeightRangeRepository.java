package com.ftcs.financeservice.weight_range.repository;

import com.ftcs.financeservice.weight_range.model.WeightRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WeightRangeRepository extends JpaRepository<WeightRange, Integer> {
    Optional<WeightRange> findByWeightRangeId(Integer weightRangeId);
    @Query("SELECT COUNT(w) > 0 FROM WeightRange w " +
            "WHERE NOT (:maxWeight < w.minWeight OR :minWeight > w.maxWeight)")
    boolean existsByOverlappingRange(@Param("minWeight") Double minWeight,
                                     @Param("maxWeight") Double maxWeight);

    @Query("SELECT COUNT(w) > 0 FROM WeightRange w " +
            "WHERE NOT (:maxWeight < w.minWeight OR :minWeight > w.maxWeight) " +
            "AND w.weightRangeId <> :weightRangeId")
    boolean existsByOverlappingRangeExcludingId(@Param("minWeight") Double minWeight,
                                                @Param("maxWeight") Double maxWeight,
                                                @Param("weightRangeId") Integer weightRangeId);
}