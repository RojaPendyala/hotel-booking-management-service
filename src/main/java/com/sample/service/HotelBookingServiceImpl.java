package main.java.com.sample.service;


import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import main.java.com.sample.dto.HotelBookingDto;

public class HotelBookingServiceImpl implements HotelBookingService{

    private List<String> listOfAvailableRooms;
    private Map<LocalDate, List<HotelBookingDto>> bookingsByDateMap;
    private Map<String, List<HotelBookingDto>> bookingsByRoomNumberMap;
    private Map<String, List<HotelBookingDto>> bookingsByGuestNameMap;

	
    
    public HotelBookingServiceImpl(String listOfAvailableRoomsStr) {
        List<String> availableRoomsList = Arrays.asList(listOfAvailableRoomsStr.split(","));
        this.listOfAvailableRooms = new ArrayList<>();
        this.bookingsByRoomNumberMap = new ConcurrentHashMap<>();
        this.bookingsByGuestNameMap = new ConcurrentHashMap<>();
        this.bookingsByDateMap = new ConcurrentHashMap<>();
        if(availableRoomsList != null) {
	        for (int i = 0; i < availableRoomsList.size(); i++) {
	        	listOfAvailableRooms.add(availableRoomsList.get(i));
	        	bookingsByRoomNumberMap.put(availableRoomsList.get(i), new ArrayList<>());
	        }
        }
    }


    @Override
    public void saveBooking(HotelBookingDto bookingDto) throws IllegalArgumentException{

        try {
            // Checking if a booking already a for the given room and date
            for (HotelBookingDto details : bookingsByRoomNumberMap.get(bookingDto.getRoomNumber())) {
                if (details.getDate().equals(bookingDto.getDate())) {
                    throw new IllegalArgumentException("Booking already available for the room number " + bookingDto.getRoomNumber() + " on date " + bookingDto.getDate()+", Please choose another room.");
                }
            }
            bookingsByDateMap.computeIfAbsent(bookingDto.getDate(), d -> new ArrayList<>()).add(bookingDto);
            bookingsByRoomNumberMap.get(bookingDto.getRoomNumber()).add(bookingDto);
            bookingsByGuestNameMap.computeIfAbsent(bookingDto.getGuestName(), d -> new ArrayList<>()).add(bookingDto);

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to save booking: " + e.getMessage());
        }
    }

    @Override
    public List<String> getAvailableRooms(LocalDate date) throws IllegalArgumentException{
        List<String> availableRoomNumbers = new ArrayList<>();

        try {
        	availableRoomNumbers.addAll(this.listOfAvailableRooms);
        	List<HotelBookingDto> hotelBookingList = bookingsByDateMap.get(date);
           
            if(hotelBookingList != null && !hotelBookingList.isEmpty()) {
            	 List<String> availableRoomsList = hotelBookingList.stream().filter(d -> availableRoomNumbers.contains(d.getRoomNumber())).map(HotelBookingDto :: getRoomNumber).collect(Collectors.toList());
            	 availableRoomNumbers.removeAll(availableRoomsList);
               
        	}
            return availableRoomNumbers;

        } catch (Exception e) {
            throw new IllegalArgumentException("Error while finding available rooms: " + e.getMessage());
        }
    }

    @Override
    public List<HotelBookingDto> findBookingsByGuest(String guestName) throws IllegalArgumentException{
        try {
            return bookingsByGuestNameMap.getOrDefault(guestName, new ArrayList<>());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while finding bookings for guest: " + e.getMessage());
        }
    }
}
