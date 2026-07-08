package com.github.ailesgrises.esp32controltower.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ailesgrises.esp32controltower.dto.TelemetryRequest;
import com.github.ailesgrises.esp32controltower.entity.Telemetry;
import com.github.ailesgrises.esp32controltower.repository.TelemetryRepository;
import com.github.ailesgrises.esp32controltower.websocket.TelemetryWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryRepository telemetryRepository;

    @Autowired
    private TelemetryWebSocketHandler webSocketHandler;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * ESP32デバイスからのPOSTリクエスト
     */
    @PostMapping
    public ResponseEntity<String> ingestTelemetry(@RequestBody TelemetryRequest request){
        Telemetry telemetry = new Telemetry();
        telemetry.setSatelliteId(request.getSatelliteId());
        telemetry.setLatitude(request.getLatitude());
        telemetry.setLongitude(request.getLongitude());
        telemetry.setTemperature(request.getTemperature());
        telemetry.setTimestamp(request.getTimestamp());
        telemetryRepository.save(telemetry);

        // for debug
        System.out.println("POST request was received. ");
        System.out.println("satelliteId: " + request.getSatelliteId());
        System.out.println("latitude: " + request.getLatitude());
        System.out.println("longitude: " + request.getLongitude());
        System.out.println("temperature: " + request.getTemperature());
        System.out.println("timestamp: " + request.getTimestamp());

        // web socket へのブロードキャスト
        try {
            String json = objectMapper.writeValueAsString(request);
            webSocketHandler.broadcastTelemetry(json);
        } catch (Exception e) {
            System.err.println("Failed to forward the message to the web socket. " + e.getMessage());
        }

        return ResponseEntity.ok("Telemetry received and saved successfully.");
    }

    /**
     * フロントからの履歴取得リクエスト
     */
    @GetMapping("/history")
    public ResponseEntity<List<Telemetry>> getHistory(){
        List<Telemetry> history = telemetryRepository.findAllByOrderByTimestampAsc();
        return ResponseEntity.ok(history);
    }
}
