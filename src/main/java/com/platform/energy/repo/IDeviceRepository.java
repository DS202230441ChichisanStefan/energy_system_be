package com.platform.energy.repo;

import com.platform.energy.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDeviceRepository extends JpaRepository<Device, Long> {

    List<Device> findAllByUserId(Long id);

    @Modifying
    void deleteAllByUserId(Long id);
}
