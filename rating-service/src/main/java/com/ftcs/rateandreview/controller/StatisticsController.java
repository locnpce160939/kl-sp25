package com.ftcs.rateandreview.controller;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.rateandreview.dto.MainStatisticsDTO;
import com.ftcs.rateandreview.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ftcs.rateandreview.ReviewURL.OVERVIEW_SYSTEM;

@RestController
@RequestMapping(OVERVIEW_SYSTEM)
@AllArgsConstructor
public class StatisticsController {
    private final ReviewService reviewService;

    @GetMapping()
    public ApiResponse<List<MainStatisticsDTO>> getMainStatistics() {
        return new ApiResponse<>(reviewService.getMainStatistics());
    }
} 