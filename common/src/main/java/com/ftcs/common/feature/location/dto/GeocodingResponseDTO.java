package com.ftcs.common.feature.location.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingResponseDTO {

    @JsonProperty("results")
    private List<Result> results;

    @JsonProperty("status")
    private String status;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {

        @JsonProperty("address_components")
        private List<AddressComponent> addressComponents;

        @JsonProperty("formatted_address")
        private String formattedAddress;

        @JsonProperty("geometry")
        private Geometry geometry;

        @JsonProperty("place_id")
        private String placeId;

        @JsonProperty("reference")
        private String reference;

        @JsonProperty("plus_code")
        private PlusCode plusCode;

        @JsonProperty("compound")
        private Compound compound;

        @JsonProperty("types")
        private List<String> types;

        @JsonProperty("name")
        private String name;

        @JsonProperty("address")
        private String address;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressComponent {
        @JsonProperty("long_name")
        private String longName;

        @JsonProperty("short_name")
        private String shortName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geometry {
        @JsonProperty("location")
        private Location location;

        @JsonProperty("boundary")
        private Object boundary;
    }

    @Data
    public static class Location {
        @JsonProperty("lat")
        private double lat;

        @JsonProperty("lng")
        private double lng;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlusCode {
        @JsonProperty("compound_code")
        private String compoundCode;

        @JsonProperty("global_code")
        private String globalCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Compound {
        @JsonProperty("district")
        private String district;

        @JsonProperty("commune")
        private String commune;

        @JsonProperty("province")
        private String province;
    }
}
