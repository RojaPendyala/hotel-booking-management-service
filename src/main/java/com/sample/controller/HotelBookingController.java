package main.java.com.sample.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import com.sun.net.httpserver.HttpExchange; 
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import main.java.com.sample.dto.HotelBookingDto;
import main.java.com.sample.service.HotelBookingService;
import main.java.com.sample.service.HotelBookingServiceImpl;



public class HotelBookingController {

    private static final Logger LOGGER = Logger.getLogger(HotelBookingController.class.getName());
    private static String  CONFIG_PROPERTIES_FILE = null;

    private static Properties properties= new Properties();

    private static HotelBookingService hotelBookingService = null;

    public static void main(String[] args) throws IOException {
    	
    	CONFIG_PROPERTIES_FILE = new File(".").getCanonicalPath() + File.separator +"resources"+File.separator+ "config.properties";

        readProperties();
        String roomNumbers = getProperty("room.numbers");
        hotelBookingService = roomNumbers != null?new HotelBookingServiceImpl(roomNumbers):new HotelBookingServiceImpl("");
        
        // Create HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Define HTTP endpoints
        server.createContext("/getAvailableRooms", new AvailableRoomsHandler());
        server.createContext("/saveBooking", new SaveBookingHandler());
        server.createContext("/findBookingsForGuest", new BookingsForGuestHandler());

        // Set logger level
        LOGGER.setLevel(Level.INFO);

        // Start the server
        server.start();
        System.out.println("Server started on port 8080");
    }

     static class SaveBookingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            LOGGER.info("Handling booking request to save...");

            try {
                if ("POST".equals(exchange.getRequestMethod())) {
                    String requestBody = getRequestBody(exchange.getRequestBody());
                    Map<String, String> params = Utils.parseQuery(requestBody);

                    String guestName = params.get("guestName");
                    String roomNumber = params.get("roomNumber");
                    LocalDate date = LocalDate.parse(params.get("date"));

                    // Save the booking
                    HotelBookingDto bookingDto = new HotelBookingDto(guestName, roomNumber, date);
                    hotelBookingService.saveBooking( bookingDto);

                    String response = "{\"message\": \"Booking saved successfully\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(HttpServletResponse.SC_OK, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    LOGGER.log(Level.SEVERE, "Error processing booking request", "Http Method not allowed");

                    String response = "{\"error\": \"Method not allowed\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(HttpServletResponse.SC_OK, response.length());
                    exchange.sendResponseHeaders(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }

                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error processing booking request", e);

                String errorMessage = "{\"error\": "+e.getMessage()+"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorMessage.getBytes());
                }
            }
        }
    }

    static class AvailableRoomsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            LOGGER.info("Handling available rooms request to get...");
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    // Parse query parameter to get the date
                    String query = exchange.getRequestURI().getQuery();
                    Map<String, String> params = Utils.parseQuery(query);
                    LocalDate date = LocalDate.parse(params.get("date"));

                    // get available rooms for the given date
                    List<String> availableRoomNumbers = hotelBookingService.getAvailableRooms(date);

                    // Send response with available rooms
                    String response = availableRoomNumbers != null?"Available Room numbers:"+availableRoomNumbers.toString():"[]";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(HttpServletResponse.SC_OK, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    LOGGER.log(Level.SEVERE, "Error processing available room request", "Http Method not allowed");

                    String response = "{\"error\": \"Method not allowed\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(HttpServletResponse.SC_OK, response.length());
                    exchange.sendResponseHeaders(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }

                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error processing available room request", e);

                String errorMessage = "{\"error\": "+e.getMessage()+"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorMessage.getBytes());
                }
            }
        }
    }

    static class BookingsForGuestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            LOGGER.info("Handling booking-for-guest request...");
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    // Parse query parameter to get the guest name
                    String query = exchange.getRequestURI().getQuery();
                    Map<String, String> params = Utils.parseQuery(query);
                    String guestName = params.get("guestName");

                    // Find bookings for the given guest, also can return the below object in the response directly
                    List<HotelBookingDto> bookingsForGuest = hotelBookingService.findBookingsByGuest(guestName);
                    Map<LocalDate, List<String>> bookingMap = bookingsForGuest.stream().collect(Collectors.groupingBy(HotelBookingDto::getDate,Collectors.mapping(
                    		HotelBookingDto::getRoomNumber, Collectors.toList())));
                    // Send response with bookings for the guest
                    //List<String> responseList = bookingsForGuest.stream().map(d -> "{\"roomNumber\":"+d.getRoomNumber()+",\"date\":"+d.getDate()+"}").collect(Collectors.toList());
                    String response = bookingMap != null?"Booking details for the guest "+guestName+":"+bookingMap.toString():"{}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(HttpServletResponse.SC_OK, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    LOGGER.log(Level.SEVERE, "Error processing booking-for-guest request", "Http Method not allow");

                    String response = "{\"error\": \"Method not allow\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(HttpServletResponse.SC_OK, response.length());
                    exchange.sendResponseHeaders(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }

                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error processing booking-for-guest request", e);

                String errorMessage = "{\"error\": "+e.getMessage()+"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorMessage.getBytes());
                }
            }
        }
    }

    static class Utils {
        public static Map<String, String> parseQuery(String query) {
            Map<String, String> queryParams = new HashMap<>();
            if (query != null && !query.isEmpty()) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        queryParams.put(key, value);
                    }
                }
            }
            return queryParams;
        }
    }


    private static String getRequestBody(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
    
    public static void readProperties()  {
        InputStream input = null;
        try {
            input = new FileInputStream(CONFIG_PROPERTIES_FILE);
            properties.load(input);
        } catch (IOException e) {
        	LOGGER.log(Level.SEVERE, "Unable to read the config.properties file.", e);
            System.exit(1);
        }
    }

    public static String getProperty(String property) {
        String roomNumbers = properties.getProperty(property);
        return roomNumbers;
    }
}
