package com.github.ailesgrises.esp32controltower.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class TelemetryWebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    // ブラウザが接続してきた時の処理
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("[WebSocket] Connected. SessionId: " + session.getId());
    }

    // ブラウザが切断した時の処理
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("[WebSocket] Disconnected. SessionId: " + session.getId());
    }

    public void broadcastTelemetry(String jsonPayload) {
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                continue;
            }
            try {
                session.sendMessage(new TextMessage(jsonPayload));
            } catch (IOException e) {
                System.err.println("[WebSocket] Failed to send message. SessionId: " + session.getId());
            }
        }
    }
}