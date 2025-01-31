package com.ftcs.transportation.trip_matching.config;

import com.ftcs.transportation.trip_matching.dto.MatchResult;
import com.ftcs.transportation.trip_matching.service.DirectionsService;
import com.ftcs.transportation.trip_matching.service.strategy.MatchPointsStrategy;
import com.ftcs.transportation.trip_matching.service.strategy.DirectionStrategy;
import com.ftcs.transportation.trip_matching.service.strategy.MatchingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TripMatchingStrategyConfig {

    @Bean
    public MatchingStrategy<MatchResult> commonPointsStrategy(DirectionsService directionsService) {
        return new MatchPointsStrategy(directionsService);
    }

    @Bean
    public MatchingStrategy<Boolean> directionStrategy() {
        return new DirectionStrategy();
    }
}