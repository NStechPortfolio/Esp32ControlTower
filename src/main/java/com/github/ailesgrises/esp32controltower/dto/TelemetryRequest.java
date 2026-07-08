package com.github.ailesgrises.esp32controltower.dto;

import lombok.Data;

// "/api/telemetry" のリクエスト情報
@Data
public class TelemetryRequest {
    private String satelliteId; // デバイスID
    private Double latitude; // 緯度
    private Double longitude; // 経度
    private Double temperature; // 温度
    private Long timestamp; // タイムスタンプ
}
