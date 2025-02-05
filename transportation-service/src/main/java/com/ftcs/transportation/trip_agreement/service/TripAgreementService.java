package com.ftcs.transportation.trip_agreement.service;

import com.ftcs.transportation.trip_agreement.model.TripAgreement;
import com.ftcs.transportation.trip_agreement.repository.TripAgreementRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TripAgreementService {
    private final TripAgreementRepository tripAgreementRepository;

    public List<TripAgreement> getAllTripAgreements() {
        return tripAgreementRepository.findAll();
    }

    public List<TripAgreement> getAllTripAgreementsOfDriver(Integer accountId) {
        return tripAgreementRepository.findAllByDriverId(accountId);
    }

    public List<TripAgreement> getAllTripAgreementsOfCustomer(Integer accountId) {
        return tripAgreementRepository.findAllByCustomerId(accountId);
    }
}
