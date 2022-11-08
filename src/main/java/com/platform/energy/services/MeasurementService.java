package com.platform.energy.services;

import com.platform.energy.dtos.MeasurementDTO;
import com.platform.energy.entities.Measurement;
import com.platform.energy.mappers.Mappers;
import com.platform.energy.repo.IMeasurementRepository;
import com.platform.energy.services.customexceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeasurementService {

    private final IMeasurementRepository iMeasurementRepository;
    private final Mappers mapper;

    public MeasurementDTO postMeasurement(MeasurementDTO measurementDTO) {
        Measurement measurement = mapper.measurementToEntity(measurementDTO);

        measurement.setTimestamp(Timestamp.from(Instant.now()));

        Measurement getMeasurementSaved = iMeasurementRepository.save(measurement);

        return mapper.measurementToDTO(getMeasurementSaved);
    }

    public List<MeasurementDTO> getAllForUserDevices(List<Long> deviceId) {
        List<MeasurementDTO> measurementDTOs = new ArrayList<>();

        for (Long id : deviceId) {
            measurementDTOs.addAll(iMeasurementRepository.getAllByDeviceId(id)
                    .stream()
                    .map(mapper::measurementToDTO).toList());
        }

        return measurementDTOs;
    }

    public List<MeasurementDTO> listMeasurements() throws ResourceNotFoundException {
        return iMeasurementRepository.findAll().stream()
                .map(mapper::measurementToDTO)
                .collect(Collectors.toList());
    }
}
