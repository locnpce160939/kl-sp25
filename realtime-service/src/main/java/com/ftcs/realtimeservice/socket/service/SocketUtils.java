package com.ftcs.realtimeservice.socket.service;

import com.corundumstudio.socketio.SocketIOClient;

public class SocketUtils {
    public static boolean isAuthenticated(SocketIOClient client) {
        Boolean isAuthenticated = client.get("authenticate");
        return isAuthenticated != null && isAuthenticated;
    }
}
