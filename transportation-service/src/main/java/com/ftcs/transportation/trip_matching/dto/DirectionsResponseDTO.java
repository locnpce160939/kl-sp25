package com.ftcs.transportation.trip_matching.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectionsResponseDTO {

    @JsonProperty("geocoded_waypoints")
    private List<GeocodedWaypointDto> geocodedWaypoints;

    @JsonProperty("routes")
    private List<RouteDto> routes;

    @Getter
    @Setter
    public static class GeocodedWaypointDto {

        @JsonProperty("geocoder_status")
        private String geocoderStatus;

        @JsonProperty("place_id")
        private String placeId;
    }

    @Getter
    @Setter
    public static class RouteDto {

        @JsonProperty("bounds")
        private BoundsDto bounds;

        @JsonProperty("legs")
        private List<LegDto> legs;

        @JsonProperty("overview_polyline")
        private PolylineDto overviewPolyline;

        @JsonProperty("summary")
        private String summary;

        @JsonProperty("warnings")
        private List<String> warnings;

        @JsonProperty("waypoint_order")
        private List<String> waypointOrder;
    }

    @Getter
    @Setter
    public static class BoundsDto {

        @JsonProperty("northeast")
        private LocationDto northeast;

        @JsonProperty("southwest")
        private LocationDto southwest;
    }

    @Getter
    @Setter
    public static class LocationDto {

        @JsonProperty("lat")
        private String lat;

        @JsonProperty("lng")
        private String lng;
    }

    @Getter
    @Setter
    public static class LegDto {

        @JsonProperty("distance")
        private DistanceDto distance;

        @JsonProperty("duration")
        private DurationDto duration;

        @JsonProperty("end_address")
        private String endAddress;

        @JsonProperty("end_location")
        private LocationDto endLocation;

        @JsonProperty("start_address")
        private String startAddress;

        @JsonProperty("start_location")
        private LocationDto startLocation;

        @JsonProperty("steps")
        private List<StepDto> steps;
    }

    @Getter
    @Setter
    public static class DistanceDto {

        @JsonProperty("text")
        private String text;

        @JsonProperty("value")
        private int value;
    }

    @Getter
    @Setter
    public static class DurationDto {

        @JsonProperty("text")
        private String text;

        @JsonProperty("value")
        private int value;
    }

    @Getter
    @Setter
    public static class StepDto {

        @JsonProperty("distance")
        private DistanceDto distance;

        @JsonProperty("duration")
        private DurationDto duration;

        @JsonProperty("end_location")
        private LocationDto endLocation;

        @JsonProperty("html_instructions")
        private String htmlInstructions;

        @JsonProperty("maneuver")
        private String maneuver;

        @JsonProperty("polyline")
        private PolylineDto polyline;

        @JsonProperty("start_location")
        private LocationDto startLocation;

        @JsonProperty("travel_mode")
        private String travelMode;
    }

    @Getter
    @Setter
    public static class PolylineDto {

        @JsonProperty("points")
        private String points;
    }
}
