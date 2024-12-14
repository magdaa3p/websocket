package com.websocket.demo.config;
import com.websocket.demo.service.ChatHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
@Configuration 
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
@Autowired
private ChatHandler chathandler;
@Override
public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(chathandler, "/chat");
}
}
