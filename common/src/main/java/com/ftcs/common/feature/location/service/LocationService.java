package com.ftcs.common.feature.location.service;

import com.ftcs.common.feature.location.dto.LocationDto;
import com.ftcs.common.feature.location.repository.DistrictRepository;
import com.ftcs.common.feature.location.repository.ProvinceRepository;
import com.ftcs.common.feature.location.repository.WardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LocationService {

    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final ProvinceRepository provinceRepository;

    public List<LocationDto> getProvinces() {
        return provinceRepository.findAll().stream()
                .map(province -> new LocationDto(province.getCode(), province.getFullName()))
                .collect(Collectors.toList());
    }

    public List<LocationDto> getDistrictsByProvince(Integer provinceCode) {
        return districtRepository.findByProvinceCode(provinceCode).stream()
                .map(district -> new LocationDto(district.getCode(), district.getFullName()))
                .collect(Collectors.toList());
    }

    public List<LocationDto> getWardsByDistrict(Integer districtCode) {
        return wardRepository.findByDistrictCode(districtCode).stream()
                .map(ward -> new LocationDto(ward.getCode(), ward.getFullName()))
                .collect(Collectors.toList());
    }

}
