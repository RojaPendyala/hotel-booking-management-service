package main.java.com.sample.service;

import java.time.LocalDate;
import java.util.List;

import main.java.com.sample.dto.HotelBookingDto;


public interface HotelBookingService {

    void saveBooking(HotelBookingDto bookingDto) throws Exception;
    List<String> getAvailableRooms(LocalDate date) throws Exception;
    List<HotelBookingDto> findBookingsByGuest(String guestName) throws Exception;

}
