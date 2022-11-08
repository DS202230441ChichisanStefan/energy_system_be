package com.platform.energy.controllers;

import com.platform.energy.dtos.MeasurementDTO;
import com.platform.energy.services.MeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Transactional
@CrossOrigin(origins = "http://localhost:4200/")
public class MeasurementController {

    private final MeasurementService measurementService;

    @PostMapping("/measurement/postMeasurement")
    public ResponseEntity<?> postMeasurement(@Valid @RequestBody MeasurementDTO measurementDTO) {
        MeasurementDTO addedMeasurement = measurementService.postMeasurement(measurementDTO);

        return new ResponseEntity<>(addedMeasurement, HttpStatus.OK);
    }

    @RequestMapping(value = "/measurement/getAllMeasurements", method = RequestMethod.GET)
    public ResponseEntity<?> getAllMeasurements() {
        List<MeasurementDTO> measurementDTOList = measurementService.listMeasurements();

        return new ResponseEntity<>(measurementDTOList, HttpStatus.OK);
    }
}
