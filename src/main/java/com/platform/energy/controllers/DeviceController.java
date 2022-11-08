package com.platform.energy.controllers;

import com.platform.energy.dtos.DeviceDTO;
import com.platform.energy.services.DeviceService;
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
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping("/devices/addDevice/{userId}")
    public ResponseEntity<?> addDevice(@Valid @RequestBody DeviceDTO device, @PathVariable Long userId) {
        DeviceDTO addedDevice = deviceService.addDevice(device, userId);

        return new ResponseEntity<>(addedDevice, HttpStatus.OK);
    }

    @RequestMapping(value = "/devices/getAllDevices", method = RequestMethod.GET)
    public ResponseEntity<?> getAllDevices() {
        List<DeviceDTO> deviceDTOList = deviceService.getAllDevices();

        return new ResponseEntity<>(deviceDTOList, HttpStatus.OK);
    }

    @RequestMapping(value = "/devices/getDevicesById/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getAllDevicesById(@PathVariable(name = "id") Long id) {
        List<DeviceDTO> deviceDTOList = deviceService.getAllDevicesById(id);

        return new ResponseEntity<>(deviceDTOList, HttpStatus.OK);
    }

    @RequestMapping(value = "/devices/getDevice/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getDevice(@PathVariable(name = "id") Long id) {
        DeviceDTO device = deviceService.getDevice(id);

        return new ResponseEntity<>(device, HttpStatus.OK);
    }

    @PostMapping("/devices/updateDevice/{userId}")
    public ResponseEntity<?> updateDevice(@Valid @RequestBody DeviceDTO device, @PathVariable Long userId) {
        DeviceDTO updatedDevice = deviceService.updateDevice(device, userId);

        return new ResponseEntity<>(updatedDevice, HttpStatus.OK);
    }

    @DeleteMapping("/devices/deleteDevice/{id}/{userId}")
    public ResponseEntity<?> deleteDeviceById(@PathVariable(name = "id") Long id, @PathVariable(name = "userId") Long userId) {
        deviceService.deleteDevice(id, userId);

        return ResponseEntity.ok().build();
    }
}
