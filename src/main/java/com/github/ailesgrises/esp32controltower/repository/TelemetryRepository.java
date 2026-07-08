package com.github.ailesgrises.esp32controltower.repository;

import com.github.ailesgrises.esp32controltower.entity.Telemetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {
    List<Telemetry> findAllByOrderByTimestampAsc();
}