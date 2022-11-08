package com.platform.energy.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDTO {

    private Long id;
    private String description;
    private String address;
    private Double hourlyMaxEnergyConsumption;
    private UserDTO user;
    private List<MeasurementDTO> measurement;
}
