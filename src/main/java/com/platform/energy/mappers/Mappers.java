package com.platform.energy.mappers;

import com.platform.energy.dtos.DeviceDTO;
import com.platform.energy.dtos.MeasurementDTO;
import com.platform.energy.dtos.UserDTO;
import com.platform.energy.entities.*;
import com.platform.energy.repo.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Mappers {

    private final IRoleRepository iRoleRepository;

    public UserDTO userToDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .role(user.getRole().getName().toString())
                .build();
    }

    public User userToEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        Role roleDTO = iRoleRepository.findRoleByName(ERole.valueOf(userDTO.getRole()));

        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .role(roleDTO)
                .build();
    }

    public DeviceDTO deviceToDTO(Device device) {

        if (device == null) {
            return null;
        }

        List<MeasurementDTO> measurementDTOList = new ArrayList<>();

        if (device.getMeasurement() != null) {
            device.getMeasurement().forEach(measurement -> {
                MeasurementDTO measurementDTO = measurementToDTO(measurement);

                measurementDTOList.add(measurementDTO);
            });
        }

        return DeviceDTO.builder()
                .id(device.getId())
                .description(device.getDescription())
                .address(device.getAddress())
                .hourlyMaxEnergyConsumption(device.getHourlyMaxEnergyConsumption())
                .user(userToDTO(device.getUser()))
                .measurement(measurementDTOList)
                .build();
    }

    public Device deviceToEntity(DeviceDTO deviceDTO) {

        if (deviceDTO == null) {
            return null;
        }

        List<Measurement> measurements = new ArrayList<>();

        if (deviceDTO.getMeasurement() != null) {
            deviceDTO.getMeasurement().forEach(measurementDTO -> {
                Measurement measurement = measurementToEntity(measurementDTO);

                measurements.add(measurement);
            });
        }

        return Device.builder()
                .id(deviceDTO.getId())
                .description(deviceDTO.getDescription())
                .address(deviceDTO.getAddress())
                .hourlyMaxEnergyConsumption(deviceDTO.getHourlyMaxEnergyConsumption())
                .user(userToEntity(deviceDTO.getUser()))
                .measurement(measurements)
                .build();
    }

    public MeasurementDTO measurementToDTO(Measurement measurement) {
        if (measurement == null) {
            return null;
        }

        return MeasurementDTO.builder()
                .id(measurement.getId())
                .timestamp(measurement.getTimestamp())
                .energyConsumption(measurement.getEnergyConsumption())
                .device(deviceToDTO(measurement.getDevice()))
                .build();
    }

    public Measurement measurementToEntity(MeasurementDTO measurementDTO) {
        if (measurementDTO == null) {
            return null;
        }

        return Measurement.builder()
                .id(measurementDTO.getId())
                .timestamp(measurementDTO.getTimestamp())
                .energyConsumption(measurementDTO.getEnergyConsumption())
                .device(deviceToEntity(measurementDTO.getDevice()))
                .build();
    }
}
