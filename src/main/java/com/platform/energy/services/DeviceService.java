package com.platform.energy.services;

import com.platform.energy.dtos.DeviceDTO;
import com.platform.energy.entities.Device;
import com.platform.energy.entities.User;
import com.platform.energy.mappers.Mappers;
import com.platform.energy.repo.IDeviceRepository;
import com.platform.energy.repo.IMeasurementRepository;
import com.platform.energy.repo.IUserRepository;
import com.platform.energy.services.customexceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final IDeviceRepository iDeviceRepository;
    private final IUserRepository iUserRepository;
    private final IMeasurementRepository iMeasurementRepository;
    private final Mappers mapper;

    public List<DeviceDTO> getAllDevices() throws ResourceNotFoundException {
        return iDeviceRepository.findAll().stream()
                .map(mapper::deviceToDTO)
                .collect(Collectors.toList());
    }

    public List<DeviceDTO> getAllDevicesById(Long id) throws ResourceNotFoundException {
        return iDeviceRepository.findAllByUserId(id).stream()
                .map(mapper::deviceToDTO)
                .collect(Collectors.toList());
    }

    public DeviceDTO getDevice(Long id) {
        Optional<Device> foundDevice = iDeviceRepository.findById(id);

        if (foundDevice.isEmpty()) {
            throw new ResourceNotFoundException("Device with id " + id + " not found.");
        }

        return mapper.deviceToDTO(foundDevice.get());
    }

    public DeviceDTO addDevice(DeviceDTO deviceDTO, Long userId) throws ResourceNotFoundException {
        if (deviceDTO.getId() != null) {
            throw new ResourceNotFoundException("Device " + deviceDTO.getId() + " already found. Cannot perform create operation.");
        }

        User currentUser = iUserRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found."));

        Device device = mapper.deviceToEntity(deviceDTO);

        device.setUser(currentUser);

        Device getDeviceSaved = iDeviceRepository.save(device);

        return mapper.deviceToDTO(getDeviceSaved);
    }

    public DeviceDTO updateDevice(DeviceDTO deviceDTO, Long userId) throws ResourceNotFoundException {
        if (deviceDTO.getUser() == null || deviceDTO.getUser().getId() == null) {
            throw new ResourceNotFoundException("Device has no user.");
        }

        if (!Objects.equals(deviceDTO.getUser().getId(), userId)) {
            throw new ResourceNotFoundException("Device with id " + deviceDTO.getId() + " cannot be found.");
        }

        Device device = mapper.deviceToEntity(deviceDTO);

        Device getDeviceSaved = iDeviceRepository.save(device);

        return mapper.deviceToDTO(getDeviceSaved);
    }

    public void deleteDevice(Long id, Long userId) throws ResourceNotFoundException {
        Optional<Device> foundDevice = iDeviceRepository.findById(id);

        if (foundDevice.isEmpty()) {
            throw new ResourceNotFoundException("Given device was not found. Delete operation could not be performed.");
        } else {
            if (foundDevice.get().getUser() == null || foundDevice.get().getUser().getId() == null) {
                throw new ResourceNotFoundException("Device has no user.");
            }

            if (!Objects.equals(foundDevice.get().getUser().getId(), userId)) {
                throw new ResourceNotFoundException("You can't delete another user device.");
            }

            iMeasurementRepository.deleteAllByDeviceId(id);
            iDeviceRepository.delete(foundDevice.get());
        }
    }
}
