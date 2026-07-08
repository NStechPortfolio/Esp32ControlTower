package com.github.ailesgrises.esp32controltower.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "telemetries")
@Getter
@Setter
@NoArgsConstructor
public class Telemetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 自動採番

    @Column(nullable = false)
    private String satelliteId;

    @Column(nullable = false)
    private Double latitude; // 緯度

    @Column(nullable = false)
    private Double longitude; // 経度

    @Column(nullable = false)
    private Double temperature; // 温度

    @Column(nullable = false)
    private String timestamp; // タイムスタンプ
}
