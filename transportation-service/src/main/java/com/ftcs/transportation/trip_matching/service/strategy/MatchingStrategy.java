package com.ftcs.transportation.trip_matching.service.strategy;

public interface MatchingStrategy<T> {
    T evaluateMatch(MatchingContext context);
}
