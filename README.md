# Hotel Booking Management Service

This is a simple Java-based microservice to manage the hotel bookings. It contains some features for save room booking, get available list of rooms for a given date, and find booking room numbers for a specific guest.

## Features

- get Available Rooms: Endpoint to get available rooms list for a given date.
- Save Booking: Endpoint to save a booking with guest name, room number, and date.
- Find Bookings for Guest: Endpoint to find all booking rooms for a specific guest.

## Prerequisites
- Java Development Kit (JDK) installed on your machine
- Maven or Gradle for building the project
- Git for cloning the repository (optional)
- Any IDE like Eclipse / Intellij to organize and manage the code properly (optional)

## Project setup
1. Clone the repository to your local machine:
```
git clone https://github.com/RojaPendyala/hotel-booking-management-service
```
2. Navigate to the project directory:
```
cd hotel-booking-management-service
```
3. Build the project using Maven:
```
mvn clean install
```

## Usage
1. Run the main class HotelBookingController to start the HTTP server:
```
java -cp target/classes main.java.com.sample.controller.HotelBookingController or If you work with IDE, can right click on the class and run as Java application
```
2. Use a tool like cURL or Postman to make HTTP requests to the API endpoints:
- Save Booking:
  `POST /saveBooking`
- get Available Rooms:
  `GET /getAvailableRooms?date={date}`
- Find Bookings for Guest:
  `GET /findBookingsForGuest?guestName={guestName}`

## Example Requests using post man
- Save Booking:

	URL: http://localhost:8080/saveBooking
	Request body: guestName=Roja&roomNumber=100&date=2024-05-11
- get available rooms for Booking:

	URL: http://localhost:8080/getAvailableRooms?date=2024-05-11

- Find Bookings for Guest:

	URL: http://localhost:8080/findBookingsForGuest?guestName=Roja

