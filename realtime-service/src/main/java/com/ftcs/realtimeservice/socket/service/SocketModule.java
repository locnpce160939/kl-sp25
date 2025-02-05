package com.ftcs.realtimeservice.socket.service;


import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.ftcs.authservice.configs.filters.JwtAuthenticationFilter;
import com.ftcs.authservice.configs.filters.ParseToken;
import com.ftcs.realtimeservice.socket.contants.Message;
import com.ftcs.realtimeservice.socket.contants.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Slf4j
public class SocketModule {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ParseToken parseToken;

    public SocketModule(SocketIOServer server, JwtAuthenticationFilter jwtAuthenticationFilter, ParseToken parseToken) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.parseToken = parseToken;
        server.addConnectListener(onConnected(server));
        server.addDisconnectListener(onDisconnected(server));
        server.addEventListener("message", Object.class, (client, data, ackRequest) -> {
            log.info("Event: message, Data: {}", data);
        });
        server.addEventListener("authenticate", String.class, onAuthenticate());
    }

    private ConnectListener onConnected(SocketIOServer server) {
        return (client) -> {
            var params = client.getHandshakeData().getUrlParams();
            String room = params.get("room").stream().collect(Collectors.joining());
            String username = params.get("username").stream().collect(Collectors.joining());
            client.joinRoom(room);
            log.info("Socket ID[{}] - room[{}] - username [{}]  Connected to chat module through", client.getSessionId().toString(), room, username);
            log.info("Client connected. Total clients: {}", server.getAllClients().size());
        };
    }

    private DisconnectListener onDisconnected(SocketIOServer server) {
        return client -> {
            var params = client.getHandshakeData().getUrlParams();
            String room = params.get("room").stream().collect(Collectors.joining());
            String username = params.get("username").stream().collect(Collectors.joining());
            log.info("Socket ID[{}] - room[{}] - username [{}]  disconnected to chat module through", client.getSessionId().toString(), room, username);
            log.info("Client disconnected. Total clients: {}", server.getAllClients().size());
        };
    }

    private DataListener<String> onAuthenticate() {
        return (client, token, ackSender) -> {
            try {
                if (token == null || token.isEmpty()) {
                    log.error("Token is missing for client {}", client.getSessionId());
                    client.disconnect();
                    return;
                }

                var params = client.getHandshakeData().getUrlParams();
                String room = params.get("room").stream().collect(Collectors.joining());

                if (jwtAuthenticationFilter.isTokenInvalid(token)) {
                    log.error("Invalid token for client {}", client.getSessionId());
                    client.disconnect();
                    return;
                }

                if (parseToken.getId(token).toString().equals(room)) {
                    client.set("authenticate", true);
                    log.info("Client of account [{}] authenticated successfully", parseToken.getUsername(token));
                    ackSender.sendAckData("Authenticated successfully!");
                } else {
                    log.error("Room mismatch for client {}: expected room {}, but token ID is {}",
                            client.getSessionId(), room, parseToken.getId(token));
                    client.disconnect();
                }

            } catch (Exception e) {
                log.error("Authentication failed for client {}", client.getSessionId(), e);
                client.disconnect();
            }
        };
    }

}
