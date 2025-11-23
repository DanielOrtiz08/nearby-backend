package com.edu.unimagdalena.nearby.services;

import com.edu.unimagdalena.nearby.entities.Reservacion;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BookingService {
    Reservacion createBooking(Map<String, Object> payload);
    Reservacion getBooking(UUID id);
    Reservacion cancelBooking(UUID id);
    List<Reservacion> bookingsByStudent(UUID studentId);
    List<Reservacion> bookingsByOwner(UUID ownerUserId);
    Reservacion acceptBooking(UUID id);
    Reservacion rejectBooking(UUID id, String reason);
    Reservacion confirmArrival(UUID id);
    Reservacion completeBooking(UUID id);
}
