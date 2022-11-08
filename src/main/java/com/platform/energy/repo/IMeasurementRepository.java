package com.platform.energy.repo;

import com.platform.energy.entities.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface IMeasurementRepository extends JpaRepository<Measurement, Long> {

    List<Measurement> getAllByDeviceId(Long id);
    List<Measurement> getAllByDeviceIdAndTimestamp(Long id, Timestamp timestamp);

    @Modifying
    void deleteAllByDeviceId(Long id);

    @Modifying
    void deleteAllByDeviceUserId(Long id);
}
