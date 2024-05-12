package main.java.com.sample.dto;

import java.time.LocalDate;

public class HotelBookingDto {

    private final String guestName;
    private final String roomNumber;
    private final LocalDate date;

    public HotelBookingDto(String guestName, String roomNumber, LocalDate date) {
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.date = date;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getDate() {
        return date;
    }

}
