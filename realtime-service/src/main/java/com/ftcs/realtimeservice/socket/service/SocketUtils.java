package com.ftcs.realtimeservice.socket.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;

import java.util.stream.Collectors;

public class SocketUtils {
    public static boolean isAuthenticated(SocketIOClient client) {
        Boolean isAuthenticated = client.get("authenticate");
        return isAuthenticated != null && isAuthenticated;
    }

    public static Integer getRoomSocket(SocketIOClient client) {
        var params = client.getHandshakeData().getUrlParams();
        String room = params.get("room").stream().collect(Collectors.joining());
        return Integer.parseInt(room);
    }

}
