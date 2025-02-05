package com.ftcs.realtimeservice.socket.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.ftcs.realtimeservice.socket.contants.Message;
import com.ftcs.realtimeservice.socket.contants.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocketService {

    private final SocketIOServer server;

    public void sendSocketMessage(Message message) {
        for (SocketIOClient client : server.getRoomOperations(message.getRoom()).getClients()) {
            //Boolean isAuthenticated = client.get("authenticate");
            //if (isAuthenticated != null && isAuthenticated) {
                client.sendEvent(String.valueOf(message.getMessageType()), message);
            //}
        }
    }
}
