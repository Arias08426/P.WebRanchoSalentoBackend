package com.example.PaginaWebRanchoSalento.service.interfac;


import com.example.PaginaWebRanchoSalento.dto.Response;
import com.example.PaginaWebRanchoSalento.model.Booking;

public interface IBookingService {

    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);

    Response findBookingByConfirmationCode(String confirmationCode);

    Response getAllBookings();

    Response cancelBooking(Long bookingId);

}
