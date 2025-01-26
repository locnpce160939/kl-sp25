package com.ftcs.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteResponse {
    private List<Path> paths;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Path {
        private double distance;
        private double time;
    }
}
