package com.websocket.demo.service;

import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Service
public class ChatHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<String> mensajes = new CopyOnWriteArrayList<>();
    private int sum = 0; // Suma acumulada de números enteros

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        session.sendMessage(new TextMessage("Servidor: BIENVENIDO AL CHAT :3"));
        session.sendMessage(new TextMessage("Servidor: Envía números enteros para sumarlos. Escribe 'TOTAL' para ver la suma acumulada."));
        
        // Enviar historial de mensajes al nuevo cliente
        for (String historial : mensajes) {
            session.sendMessage(new TextMessage(historial));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload().trim();

        if (msg.contains(": ")) {
            mensajes.add(msg);
            String content = msg.substring(msg.indexOf(": ") + 2);

            // Manejar mensajes especiales
            if (content.equalsIgnoreCase("TOTAL")) {
                session.sendMessage(new TextMessage("Servidor: La suma acumulada es: " + sum));
                return;
            }

            try {
                // Intentar convertir el mensaje en un número entero
                int number = Integer.parseInt(content);
                sum += number; // Actualizar suma acumulada

                // Informar a todos los clientes sobre la nueva suma
                broadcastMessage("Servidor: Número recibido: " + number + ". Nueva suma acumulada: " + sum);
            } catch (NumberFormatException e) {
                // No es un número válido
                session.sendMessage(new TextMessage("Servidor: Envía un número entero válido o escribe 'TOTAL'."));
            }

            // Enviar el mensaje original a todos los demás clientes
            broadcastMessage(msg, session);
        }
    }

    private void broadcastMessage(String message) throws Exception {
        for (WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(message));
        }
    }

    private void broadcastMessage(String message, WebSocketSession sender) throws Exception {
        for (WebSocketSession session : sessions) {
            if (session != sender) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }
}