package com.ftcs.transportation.trip_matching.repository;

import com.ftcs.transportation.trip_matching.model.TripAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripAgreementRepository extends JpaRepository<TripAgreement, Integer> {
}
