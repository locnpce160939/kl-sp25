package com.ftcs.common.feature.location;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.common.feature.CommonURL;
import com.ftcs.common.feature.location.dto.LocationDto;
import com.ftcs.common.feature.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(CommonURL.LOCATION)
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/provinces")
    public ApiResponse<?> getProvinces() {
        return new ApiResponse<>(locationService.getProvinces());
    }

    @GetMapping("/districts/{provinceCode}")
    public ApiResponse<?> getDistrictsByProvince(@PathVariable("provinceCode") String provinceCode) {
        return new ApiResponse<>(locationService.getDistrictsByProvince(provinceCode));
    }

    @GetMapping("/wards/{districtCode}")
    public ApiResponse<?> getWardsByDistrictAndProvince(@PathVariable("districtCode") String districtCode) {
        return new ApiResponse<>(locationService.getWardsByDistrict(districtCode));
    }
}
