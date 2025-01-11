package com.ftcs.common.feature.location.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReverseGeocodeResponseDto {

    @JsonProperty("status")
    private String status;

    @JsonProperty("results")
    private List<ResultDto> results;

    @Getter
    @Setter
    public static class ResultDto {

        @JsonProperty("formatted_address")
        private String formattedAddress;

        @JsonProperty("address_components")
        private List<AddressComponentDto> addressComponents;

        @JsonProperty("geometry")
        private GeometryDto geometry;

        @JsonProperty("place_id")
        private String placeId;

        @JsonProperty("reference")
        private String reference;

        @JsonProperty("plus_code")
        private PlusCodeDto plusCode;

        @JsonProperty("compound")
        private CompoundDto compound;

        @JsonProperty("name")
        private String name;

        @JsonProperty("address")
        private String address;
    }

    @Getter
    @Setter
    public static class AddressComponentDto {

        @JsonProperty("long_name")
        private String longName;

        @JsonProperty("short_name")
        private String shortName;

        @JsonProperty("types")
        private List<String> types;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeometryDto {

        @JsonProperty("location")
        private LocationDto location;

        @Getter
        @Setter
        public static class LocationDto {

            @JsonProperty("lat")
            private double lat;

            @JsonProperty("lng")
            private double lng;
        }
    }

    @Getter
    @Setter
    public static class PlusCodeDto {

        @JsonProperty("compound_code")
        private String compoundCode;

        @JsonProperty("global_code")
        private String globalCode;
    }

    @Getter
    @Setter
    public static class CompoundDto {

        @JsonProperty("district")
        private String district;

        @JsonProperty("commune")
        private String commune;

        @JsonProperty("province")
        private String province;
    }
}
