package com.edu.unimagdalena.nearby.services.impl;

import com.edu.unimagdalena.nearby.entities.Alojamiento;
import com.edu.unimagdalena.nearby.entities.CuentaUsuario;
import com.edu.unimagdalena.nearby.entities.Reservacion;
import com.edu.unimagdalena.nearby.repositories.AlojamientoRepository;
import com.edu.unimagdalena.nearby.repositories.CuentaUsuarioRepository;
import com.edu.unimagdalena.nearby.repositories.ReservacionRepository;
import com.edu.unimagdalena.nearby.services.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final ReservacionRepository reservacionRepo;
    private final AlojamientoRepository alojamientoRepo;
    private final CuentaUsuarioRepository cuentaRepo;

    public BookingServiceImpl(ReservacionRepository reservacionRepo,
                              AlojamientoRepository alojamientoRepo,
                              CuentaUsuarioRepository cuentaRepo) {
        this.reservacionRepo = reservacionRepo;
        this.alojamientoRepo = alojamientoRepo;
        this.cuentaRepo = cuentaRepo;
    }

    // Helper: check date overlap
    private boolean overlaps(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    @Override
    public Reservacion createBooking(Map<String, Object> payload) {
        // expected payload contains alojamientoId, huespedId, fechaInicio, fechaFin, numeroDePersonas, tipoContrato (optional)
        if (payload == null) throw new RuntimeException("Payload obligatorio");
        Object alojIdObj = payload.get("alojamientoId");
        Object huespedIdObj = payload.get("huespedId");
        Object fInicioObj = payload.get("fechaInicio");
        Object fFinObj = payload.get("fechaFin");
        Object numPersObj = payload.getOrDefault("numeroDePersonas", 1);

        if (alojIdObj == null || huespedIdObj == null || fInicioObj == null || fFinObj == null) {
            throw new RuntimeException("alojamientoId, huespedId, fechaInicio y fechaFin son obligatorios");
        }

        UUID alojamientoId = UUID.fromString(alojIdObj.toString());
        UUID huespedId = UUID.fromString(huespedIdObj.toString());

        Alojamiento alojamiento = alojamientoRepo.findById(alojamientoId)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));

        CuentaUsuario huesped = cuentaRepo.findById(huespedId)
                .orElseThrow(() -> new RuntimeException("Huésped no encontrado"));

        LocalDateTime fechaInicio;
        LocalDateTime fechaFin;
        try {
            fechaInicio = LocalDateTime.parse(fInicioObj.toString());
            fechaFin = LocalDateTime.parse(fFinObj.toString());
        } catch (DateTimeParseException ex) {
            throw new RuntimeException("Formato de fecha inválido. Use ISO_LOCAL_DATE_TIME.");
        }

        if (fechaFin.isBefore(fechaInicio) || fechaFin.equals(fechaInicio)) {
            throw new RuntimeException("fechaFin debe ser posterior a fechaInicio");
        }

        int numeroDePersonas;
        try {
            numeroDePersonas = Integer.parseInt(numPersObj.toString());
        } catch (Exception ex) {
            numeroDePersonas = 1;
        }

        if (numeroDePersonas > alojamiento.getCapacidad()) {
            throw new RuntimeException("Numero de personas excede la capacidad del alojamiento");
        }

        // check overlapping reservations that block the dates (PENDING, ACCEPTED, IN_PROGRESS)
        List<Reservacion> existing = reservacionRepo.findByAlojamientoId(alojamientoId);
        for (Reservacion r : existing) {
            String estado = r.getEstado() != null ? r.getEstado() : "";
            if (estado.equalsIgnoreCase("CANCELLED") || estado.equalsIgnoreCase("REJECTED") || estado.equalsIgnoreCase("COMPLETED")) {
                continue;
            }
            LocalDateTime s = r.getFechaInicio();
            LocalDateTime e = r.getFechaFin();
            if (s != null && e != null && overlaps(s, e, fechaInicio, fechaFin)) {
                throw new RuntimeException("Fechas no disponibles para el alojamiento seleccionado");
            }
        }

        // calculate cost: precioGeneral * nights
        long days = Duration.between(fechaInicio, fechaFin).toDays();
        if (days <= 0) days = 1;
        BigDecimal precioUnit = alojamiento.getPrecioGeneral() != null ? alojamiento.getPrecioGeneral() : BigDecimal.ZERO;
        BigDecimal costoFinal = precioUnit.multiply(BigDecimal.valueOf(days));

        Reservacion reservacion = new Reservacion();
        reservacion.setAlojamiento(alojamiento);
        reservacion.setHuesped(huesped);
        reservacion.setFechaInicio(fechaInicio);
        reservacion.setFechaFin(fechaFin);
        reservacion.setNumeroDePersonas(numeroDePersonas);
        reservacion.setEstado("PENDING");
        reservacion.setCostoFinal(costoFinal);
        reservacion.setTipoContrato(payload.getOrDefault("tipoContrato", "").toString());
        reservacion.setFechaPago(null);

        reservacionRepo.save(reservacion);
        return reservacion;
    }

    @Override
    public Reservacion getBooking(UUID id) {
        return reservacionRepo.findById(id).orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
    }

    @Override
    public Reservacion cancelBooking(UUID id) {
        Reservacion r = reservacionRepo.findById(id).orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
        String estado = r.getEstado() != null ? r.getEstado() : "";
        if (estado.equalsIgnoreCase("COMPLETED") || estado.equalsIgnoreCase("CANCELLED")) {
            throw new RuntimeException("No se puede cancelar una reserva completada o ya cancelada");
        }
        r.setEstado("CANCELLED");
        reservacionRepo.save(r);
        return r;
    }

    @Override
    public List<Reservacion> bookingsByStudent(UUID studentId) {
        return reservacionRepo.findByHuespedId(studentId);
    }

    @Override
    public List<Reservacion> bookingsByOwner(UUID ownerUserId) {
        return reservacionRepo.findByOwnerUserId(ownerUserId);
    }

    @Override
    public Reservacion acceptBooking(UUID id) {
        Reservacion r = reservacionRepo.findById(id).orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
        String estado = r.getEstado() != null ? r.getEstado() : "";
        if (!estado.equalsIgnoreCase("PENDING")) {
            throw new RuntimeException("Solo reservas en estado PENDING pueden ser aceptadas");
        }
        r.setEstado("ACCEPTED");
        reservacionRepo.save(r);
        return r;
    }

    @Override
    public Reservacion rejectBooking(UUID id, String reason) {
        Reservacion r = reservacionRepo.findById(id).orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
        String estado = r.getEstado() != null ? r.getEstado() : "";
        if (!estado.equalsIgnoreCase("PENDING")) {
            throw new RuntimeException("Solo reservas en estado PENDING pueden ser rechazadas");
        }
        r.setEstado("REJECTED");
        if (reason != null && !reason.isBlank()) {
            String prev = r.getTipoContrato() != null ? r.getTipoContrato() : "";
            r.setTipoContrato(prev + (prev.isEmpty() ? "" : " ") + "[RECHAZADO: " + reason + "]");
        }
        reservacionRepo.save(r);
        return r;
    }

    @Override
    public Reservacion confirmArrival(UUID id) {
        Reservacion r = reservacionRepo.findById(id).orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
        String estado = r.getEstado() != null ? r.getEstado() : "";
        if (!estado.equalsIgnoreCase("ACCEPTED")) {
            throw new RuntimeException("Solo reservas aceptadas pueden confirmar llegada");
        }
        r.setEstado("IN_PROGRESS");
        reservacionRepo.save(r);
        return r;
    }

    @Override
    public Reservacion completeBooking(UUID id) {
        Reservacion r = reservacionRepo.findById(id).orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
        String estado = r.getEstado() != null ? r.getEstado() : "";
        if (!estado.equalsIgnoreCase("IN_PROGRESS")) {
            throw new RuntimeException("Solo reservas en progreso pueden completarse");
        }
        r.setEstado("COMPLETED");
        r.setFechaPago(LocalDateTime.now());
        reservacionRepo.save(r);
        return r;
    }
}
