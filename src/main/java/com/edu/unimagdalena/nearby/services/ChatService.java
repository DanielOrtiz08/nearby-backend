package com.edu.unimagdalena.nearby.services;

import com.edu.unimagdalena.nearby.entities.Conversacion;
import com.edu.unimagdalena.nearby.entities.Mensaje;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ChatService {
    Conversacion createConversation(Map<String, Object> payload);
    List<Conversacion> listConversations(UUID userId);
    Conversacion getConversation(UUID id);
    Mensaje sendMessage(UUID conversationId, Map<String, Object> message);
    List<Mensaje> getMessages(UUID conversationId);
    Mensaje markMessageRead(UUID messageId, String readerId);
    Mensaje uploadAttachment(UUID messageId, MultipartFile file);
}
