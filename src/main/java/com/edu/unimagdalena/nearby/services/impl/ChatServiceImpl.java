package com.edu.unimagdalena.nearby.services.impl;

import com.edu.unimagdalena.nearby.entities.Conversacion;
import com.edu.unimagdalena.nearby.entities.CuentaUsuario;
import com.edu.unimagdalena.nearby.entities.Mensaje;
import com.edu.unimagdalena.nearby.enums.EstadoDeMensaje;
import com.edu.unimagdalena.nearby.enums.TipoDeConversacion;
import com.edu.unimagdalena.nearby.enums.TipoDeMensaje;
import com.edu.unimagdalena.nearby.repositories.ConversacionRepository;
import com.edu.unimagdalena.nearby.repositories.CuentaUsuarioRepository;
import com.edu.unimagdalena.nearby.repositories.MensajeRepository;
import com.edu.unimagdalena.nearby.services.ChatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ConversacionRepository conversacionRepo;
    private final MensajeRepository mensajeRepo;
    private final CuentaUsuarioRepository cuentaRepo;

    public ChatServiceImpl(ConversacionRepository conversacionRepo,
                           MensajeRepository mensajeRepo,
                           CuentaUsuarioRepository cuentaRepo) {
        this.conversacionRepo = conversacionRepo;
        this.mensajeRepo = mensajeRepo;
        this.cuentaRepo = cuentaRepo;
    }

    @Override
    public Conversacion createConversation(Map<String, Object> payload) {
        // payload: { "nombre": "...", "tipoDeConversacion": "...", "miembros": ["uuid1","uuid2"], "administradores": ["uuidX"] }
        if (payload == null) throw new RuntimeException("Payload obligatorio");
        Conversacion c = new Conversacion();
        c.setNombre(payload.getOrDefault("nombre", "").toString());
        Object tipo = payload.get("tipoDeConversacion");
        if (tipo != null) {
            try {
                c.setTipoDeConversacion(TipoDeConversacion.valueOf(tipo.toString()));
            } catch (IllegalArgumentException ignored) {
                // leave null if enum not parsable
            }
        }
        // miembros
        Object membObj = payload.get("miembros");
        if (membObj instanceof List) {
            List<?> l = (List<?>) membObj;
            Set<CuentaUsuario> miembros = l.stream().map(Object::toString).map(s -> {
                try { return UUID.fromString(s); } catch (Exception ex) { return null; }
            }).filter(Objects::nonNull).map(uuid -> cuentaRepo.findById(uuid).orElse(null))
                    .filter(Objects::nonNull).collect(Collectors.toSet());
            c.setMiembros(miembros);
        } else {
            c.setMiembros(new HashSet<>());
        }
        // administradores
        Object admObj = payload.get("administradores");
        if (admObj instanceof List) {
            List<?> l = (List<?>) admObj;
            Set<CuentaUsuario> admins = l.stream().map(Object::toString).map(s -> {
                try { return UUID.fromString(s); } catch (Exception ex) { return null; }
            }).filter(Objects::nonNull).map(uuid -> cuentaRepo.findById(uuid).orElse(null))
                    .filter(Objects::nonNull).collect(Collectors.toSet());
            c.setAdministradores(admins);
        } else {
            c.setAdministradores(new HashSet<>());
        }
        c.setDescripcion(payload.getOrDefault("descripcion", "").toString());
        c.setEstado(payload.getOrDefault("estado", "ACTIVE").toString());
        c.setFechaCreacion(LocalDateTime.now());
        Conversacion saved = conversacionRepo.save(c);
        return saved;
    }

    @Override
    public List<Conversacion> listConversations(UUID userId) {
        if (userId == null) {
            return conversacionRepo.findAll();
        }
        return conversacionRepo.findByMiembros_Id(userId);
    }

    @Override
    public Conversacion getConversation(UUID id) {
        return conversacionRepo.findById(id).orElseThrow(() -> new RuntimeException("Conversacion no encontrada"));
    }

    @Override
    public Mensaje sendMessage(UUID conversationId, Map<String, Object> message) {
        if (message == null) throw new RuntimeException("Payload de mensaje requerido");
        Conversacion conv = conversacionRepo.findById(conversationId).orElseThrow(() -> new RuntimeException("Conversacion no encontrada"));
        Object emisorObj = message.get("emisorId");
        if (emisorObj == null) throw new RuntimeException("emisorId es obligatorio");
        UUID emisorId = UUID.fromString(emisorObj.toString());
        CuentaUsuario emisor = cuentaRepo.findById(emisorId).orElseThrow(() -> new RuntimeException("Emisor no encontrado"));

        Mensaje m = new Mensaje();
        m.setEmisor(emisor);
        m.setReceptor(conv);
        m.setContenido(message.getOrDefault("contenido", "").toString());
        m.setFechaDeEnvio(LocalDateTime.now());
        Object tipo = message.get("tipoDeMensaje");
        if (tipo != null) {
            try {
                m.setTipoDeMensaje(TipoDeMensaje.valueOf(tipo.toString()));
            } catch (IllegalArgumentException ignored) {}
        }
        try {
            m.setEstado(EstadoDeMensaje.valueOf("SENT"));
        } catch (Exception ignored) {}

        Mensaje saved = mensajeRepo.save(m);
        // optionally add to conversation.chat list (not strictly needed)
        return saved;
    }

    @Override
    public List<Mensaje> getMessages(UUID conversationId) {
        // ordered by fechaDeEnvio asc
        return mensajeRepo.findByReceptor_IdOrderByFechaDeEnvioAsc(conversationId);
    }

    @Override
    public Mensaje markMessageRead(UUID messageId, String readerId) {
        Mensaje m = mensajeRepo.findById(messageId).orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        try {
            m.setEstado(EstadoDeMensaje.valueOf("READ"));
        } catch (Exception ignore) {
            // no-op si enum no tiene READ
        }
        mensajeRepo.save(m);
        return m;
    }

    @Override
    public Mensaje uploadAttachment(UUID messageId, MultipartFile file) {
        Mensaje m = mensajeRepo.findById(messageId).orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        if (file == null || file.isEmpty()) throw new RuntimeException("Archivo no proporcionado");
        // guardar archivo en temp dir con nombre único
        String tmpDir = System.getProperty("java.io.tmpdir");
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File dest = new File(tmpDir, filename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo: " + e.getMessage());
        }
        // colocar la ruta / URL local en el contenido del mensaje (o append)
        String prev = m.getContenido() != null ? m.getContenido() : "";
        String link = "file://" + dest.getAbsolutePath();
        m.setContenido(prev.isEmpty() ? link : prev + "\n" + link);
        Mensaje saved = mensajeRepo.save(m);
        return saved;
    }
}
