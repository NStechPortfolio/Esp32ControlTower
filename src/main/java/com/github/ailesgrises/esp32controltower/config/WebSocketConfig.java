package com.github.ailesgrises.esp32controltower.config;

import com.github.ailesgrises.esp32controltower.websocket.TelemetryWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private TelemetryWebSocketHandler telemetryWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(telemetryWebSocketHandler, "/ws/telemetry")
                .setAllowedOrigins("*"); // 開発用：どこからのブラウザ接続も受け入れる
    }
}