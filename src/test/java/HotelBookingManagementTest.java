
import org.junit.Before;
import org.junit.Test;

import main.java.com.sample.dto.HotelBookingDto;
import main.java.com.sample.service.HotelBookingServiceImpl;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;


public class HotelBookingManagementTest {

    private HotelBookingServiceImpl bookingService;

    @Before
    public void setUp() {
    	bookingService = new HotelBookingServiceImpl("100,101,102"); // Create a booking service with 3 rooms
    }

    @Test
    public void testSaveBooking() {
    	bookingService.saveBooking(new HotelBookingDto("guestUser_1", "100", LocalDate.now()));
        assertEquals(1, bookingService.findBookingsByGuest("guestUser_1").size());
    }

    @Test
    public void testGetAvailableRooms() {
        // Booking room no 100 for today
    	bookingService.saveBooking(new HotelBookingDto("guestUser_1", "100", LocalDate.now()));

        // get available rooms for tomorrow should return all rooms
        List<String> availableRooms = bookingService.getAvailableRooms(LocalDate.now().plusDays(1));
        assertEquals(3, availableRooms.size());
        assertTrue(availableRooms.contains("100"));
        assertTrue(availableRooms.contains("101"));
        assertTrue(availableRooms.contains("102"));

        // Booking room no 100 for tomorrow
        bookingService.saveBooking(new HotelBookingDto("guestUser_2", "100", LocalDate.now().plusDays(1)));
        // Booking room no 101 for tomorrow
        bookingService.saveBooking(new HotelBookingDto("guestUser_3", "101", LocalDate.now().plusDays(1)));
        // Now, only room no 102 should be available for tomorrow
        availableRooms = bookingService.getAvailableRooms(LocalDate.now().plusDays(1));
        assertEquals(1, availableRooms.size());
        assertTrue(availableRooms.contains("102"));
    }

    @Test
    public void testFindBookingsByGuest() {
    	bookingService.saveBooking(new HotelBookingDto("guestUser_1", "100", LocalDate.now()));
    	bookingService.saveBooking(new HotelBookingDto("guestUser_1", "101", LocalDate.now()));
    	bookingService.saveBooking(new HotelBookingDto("guestUser_2", "102", LocalDate.now()));

        List<HotelBookingDto> guestUser_1Bookings = bookingService.findBookingsByGuest("guestUser_1");
        assertEquals(2, guestUser_1Bookings.size());
        assertEquals("guestUser_1", guestUser_1Bookings.get(0).getGuestName());
        assertEquals("100", guestUser_1Bookings.get(0).getRoomNumber());
        assertEquals("guestUser_1", guestUser_1Bookings.get(1).getGuestName());
        assertEquals("101", guestUser_1Bookings.get(1).getRoomNumber());

        List<HotelBookingDto> guestUser_2Bookings = bookingService.findBookingsByGuest("guestUser_2");
        assertEquals(1, guestUser_2Bookings.size());
        assertEquals("guestUser_2", guestUser_2Bookings.get(0).getGuestName());
        assertEquals("102", guestUser_2Bookings.get(0).getRoomNumber());
    }

}
