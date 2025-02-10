package com.ftcs.common.feature.location;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.common.feature.CommonURL;
import com.ftcs.common.feature.location.service.GeocodingService;
import com.ftcs.common.feature.location.service.LocationService;
import com.ftcs.common.service.ReverseGeocodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(CommonURL.LOCATION)
public class LocationController {

    private final LocationService locationService;
    private final ReverseGeocodeService reverseGeocodeService;
    private final GeocodingService geocodingService;

    @GetMapping("/provinces")
    public ApiResponse<?> getProvinces() {
        return new ApiResponse<>(locationService.getProvinces());
    }

    @GetMapping("/districts/{provinceCode}")
    public ApiResponse<?> getDistrictsByProvince(@PathVariable("provinceCode") Integer provinceCode) {
        return new ApiResponse<>(locationService.getDistrictsByProvince(provinceCode));
    }

    @GetMapping("/wards/{districtCode}")
    public ApiResponse<?> getWardsByDistrictAndProvince(@PathVariable("districtCode") Integer districtCode) {
        return new ApiResponse<>(locationService.getWardsByDistrict(districtCode));
    }

    @GetMapping("/reverse-geocode")
    public ApiResponse<?> reverseGeocode(@RequestParam("latitude") Double latitude, @RequestParam("longitude") Double longitude) {
        return new ApiResponse<>(reverseGeocodeService.getAddressFromCoordinates(latitude, longitude));
    }

    @GetMapping("/address-geocode")
    public ApiResponse<?> reverseGeocode(@RequestParam("address") String address) {
        return new ApiResponse<>(geocodingService.getGeocode(address));
    }
}
