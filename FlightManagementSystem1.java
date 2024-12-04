/* A Project On Filght Management System 
 * BY B-9      Java-2
 * 09/10/23
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class FlightManagementSystem1 {
    static String DEFAULT_FILE_NAME = "pass.txt", flightNumber;
    static final int CONNECTION_TIMEOUT_SECONDS = 5;
    static Connection connection;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        try {
            // Register the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection details
            String dburl = "jdbc:mysql://localhost:3306/flight";
            String dbuser = "root";
            String dbpass = "";

            // Establish a connection to the database
            connection = DriverManager.getConnection(dburl, dbuser, dbpass);

            createTables();

            if (connection != null && isValidConnection(connection)) {
                System.out.println("Database is connected");
                role();
            } else {
                System.out.println("Error in connecting to database");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            closeScanner();
            System.out.println("Thanks for our application");
        }
    }

    static void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }

    static void role() throws Exception {
        System.out.println("Enter id");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        System.out.println("Enter Username");
        String username = scanner.nextLine();

        System.out.println("Enter password");
        String password = scanner.nextLine();

        String checkRoleQuery = "SELECT username, password, designation FROM user WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(checkRoleQuery)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String dbUsername = resultSet.getString("username");
                    String dbPassword = resultSet.getString("password");
                    String designation = resultSet.getString("designation");

                    if (username.equals(dbUsername) && password.equals(dbPassword)) {
                        System.out.println("Login successful.");

                        // Check designation and call the appropriate menu
                        if ("admin".equalsIgnoreCase(designation)) {
                            runFlightManagementSystem(connection, "admin");

                        } else if ("user".equalsIgnoreCase(designation)) {
                            runFlightManagementSystem(connection, "user");

                        } else {
                            System.out.println("Invalid designation.");
                        }
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                } else {
                    System.out.println("User not found.");
                }
            }
        }
    }

    static boolean isValidConnection(Connection connection) throws SQLException {
        return connection != null && !connection.isClosed() && connection.isValid(CONNECTION_TIMEOUT_SECONDS);
    }

    public static void createTables() {
        try {
            if (!isValidConnection(connection)) {
                System.err.println("Error: Connection is null, closed, or invalid.");
                return;
            }

            try (Statement statement = connection.createStatement()) {
                // Create pilot table
                String createPilotTableQuery = "CREATE TABLE IF NOT EXISTS pilot (" +
                        "pilot_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "first_name VARCHAR(50) NOT NULL," +
                        "last_name VARCHAR(50) NOT NULL," +
                        "license_number VARCHAR(20) NOT NULL" +
                        ")";
                statement.executeUpdate(createPilotTableQuery);

                // Create flight table
                String createFlightTableQuery = "CREATE TABLE IF NOT EXISTS flight (" +
                        "flight_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "flight_no VARCHAR(20) NOT NULL," +
                        "origin VARCHAR(50) NOT NULL," +
                        "destination VARCHAR(50) NOT NULL," +
                        "departure_time DATETIME NOT NULL," +
                        "arrival_time DATETIME NOT NULL," +
                        "available_seats INT NOT NULL," +
                        "pilot_id INT NULL," +
                        "payment_amount DOUBLE," +
                        "FOREIGN KEY (pilot_id) REFERENCES pilot(pilot_id)" +
                        ")";
                statement.executeUpdate(createFlightTableQuery);

                // Create customer table
                String createCustomerTableQuery = "CREATE TABLE IF NOT EXISTS customer (" +
                        "customer_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "first_name VARCHAR(50) NOT NULL," +
                        "last_name VARCHAR(50) NOT NULL," +
                        "email VARCHAR(100) NOT NULL" +
                        ")";
                statement.executeUpdate(createCustomerTableQuery);

                // Create booking table
                String createBookingTableQuery = "CREATE TABLE IF NOT EXISTS booking (" +
                        "booking_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "flight_id INT NOT NULL," +
                        "customer_id INT NOT NULL," +
                        "FOREIGN KEY (flight_id) REFERENCES flight(flight_id)," +
                        "FOREIGN KEY (customer_id) REFERENCES customer(customer_id)" +
                        ")";
                statement.executeUpdate(createBookingTableQuery);

                // Create payment table
                String createPaymentTableQuery = "CREATE TABLE IF NOT EXISTS payment (" +
                        "payment_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "amount DOUBLE NOT NULL, " +
                        "payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ";
                statement.executeUpdate(createPaymentTableQuery);

                // create user table
                String createUserTableQuery = "CREATE TABLE IF NOT EXISTS user (" +
                        "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "username VARCHAR(50) NOT NULL," +
                        "password VARCHAR(50) NOT NULL," +
                        "designation VARCHAR(50) NOT NULL" +
                        ")";
                statement.executeUpdate(createUserTableQuery);
                System.out.println("Tables created successfully.");

            }
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    static void displayUserMenu() throws Exception {
        System.out.println("User Menu:");
        System.out.println("1. View Flights");
        System.out.println("2. Book a Flight");
        System.out.println("3. Cancel Booking");
        System.out.println("4. View Bookings");
        System.out.println("5. Update Bookings");
        System.out.println("6. Update Customer Detail");
        System.out.println("7. Write Boarding Pass");

        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");
    }

    static void displayAdminMenu() throws Exception {
        System.out.println("Admin Menu:");
        System.out.println("1. View Flights");
        System.out.println("2. Book a Flight");
        System.out.println("3. Cancel Booking");
        System.out.println("4. View Bookings");
        System.out.println("5. Add Flight");
        System.out.println("6. Update Bookings");

        System.out.println("7. Update Available Seats");
        System.out.println("8. Update Customer Detail");
        System.out.println("9. Add Pilot");
        System.out.println("10. Assign Pilot To Flight");
        System.out.println("11. View Pilot Details");

        System.out.println("12. Exit");
        System.out.print("Enter your choice: ");

    }

    static void runFlightManagementSystem(Connection connection, String role) throws Exception {

        while (true) {
            if (role.equals("admin")) {
                displayAdminMenu();
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        viewFlights(connection);
                        break;
                    case 2:
                        bookFlight(connection);
                        break;
                    case 3:
                        cancelBooking(connection);
                        break;
                    case 4:
                        viewBookings(connection);
                        break;
                    case 5:
                        addFlightForAdmin(connection);
                        break;
                    case 6:
                        updateBooking(connection);

                        break;
                    case 7:
                        updateAvailableSeats(connection);
                        break;
                    case 8:
                        updateCustomerDetails(connection);
                        break;
                    case 9:
                        addPilot(connection);
                        break;
                    case 10:
                        assignPilotToFlight(connection);
                        break;
                    case 11:
                        viewPilotDetails(connection);
                        break;
                    case 12:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else if (role.equals("user")) {
                displayUserMenu();
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        viewFlights(connection);
                        break;
                    case 2:
                        bookFlight(connection);
                        break;
                    case 3:
                        cancelBooking(connection);
                        break;
                    case 4:
                        viewBookings(connection);
                        break;
                    case 5:
                        updateBooking(connection);
                        break;
                    case 6:
                        updateCustomerDetails(connection);
                        break;
                    case 7:
                        writeBoardingPassToFile(connection);
                        break;
                    case 8:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("Invalid role. Exiting...");
                return;
            }
        }
    }

    // Method to add a new flight for an admin
    static void addFlightForAdmin(Connection connection) throws SQLException {
        // Prompt the admin to enter flight details
        scanner.nextLine();
        System.out.print("Enter the flight number: ");
        String flightNumber = scanner.nextLine();

        System.out.print("Enter the origin: ");
        String origin = scanner.nextLine();

        System.out.print("Enter the destination: ");
        String destination = scanner.nextLine();

        System.out.print("Enter the departure time (yyyy-MM-dd HH:mm:ss): ");
        String departureTimeString = scanner.nextLine();
        Timestamp departureTime = Timestamp.valueOf(departureTimeString);

        System.out.print("Enter the arrival time (yyyy-MM-dd HH:mm:ss): ");
        String arrivalTimeString = scanner.nextLine();
        Timestamp arrivalTime = Timestamp.valueOf(arrivalTimeString);

        System.out.print("Enter the available seats: ");
        int availableSeats = scanner.nextInt();

        System.out.print("Enter the payment amount: ");
        double paymentAmount = scanner.nextDouble();

        // SQL query to insert the new flight into the database
        String insertQuery = "INSERT INTO flight (flight_no, origin, destination, departure_time, arrival_time, available_seats, payment_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Set parameters in the prepared statement
            preparedStatement.setString(1, flightNumber);
            preparedStatement.setString(2, origin);
            preparedStatement.setString(3, destination);
            preparedStatement.setTimestamp(4, departureTime);
            preparedStatement.setTimestamp(5, arrivalTime);
            preparedStatement.setInt(6, availableSeats);
            preparedStatement.setDouble(7, paymentAmount);

            // Execute the update and check the number of rows affected
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Flight added successfully.");
            } else {
                System.out.println("Failed to add flight.");
            }
        }
    }

    // Method to view all flights in the database
    static void viewFlights(Connection connection) throws Exception {
        // SQL query to select all flights from the database
        String query = "SELECT * FROM flight";

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            // Print column headers
            System.out
                    .println("Flight Number | Origin | Destination | Departure Time | Arrival Time | Available Seats");

            // Iterate over the result set and print flight details
            while (resultSet.next()) {
                String flightNumber = resultSet.getString("flight_no");
                String origin = resultSet.getString("origin");
                String destination = resultSet.getString("destination");
                Timestamp departureTime = resultSet.getTimestamp("departure_time");
                Timestamp arrivalTime = resultSet.getTimestamp("arrival_time");
                int availableSeats = resultSet.getInt("available_seats");

                // Print flight details
                System.out.printf("Flight: %s\n", flightNumber);
                System.out.printf("Origin: %s\n", origin);
                System.out.printf("Destination: %s\n", destination);
                System.out.printf("Departure Time: %s\n", departureTime);
                System.out.printf("Arrival Time: %s\n", arrivalTime);
                System.out.printf("Available Seats: %d\n\n", availableSeats);
            }
        }
    }

    // Method to update the available seats for a specific flight
    static void updateAvailableSeats(Connection connection) throws Exception {
        // Prompt the user for flight information
        System.out.print("Enter the flight number to update available seats: ");
        String flightNumber = scanner.nextLine();

        System.out.print("Enter the new available seats count: ");
        int availableSeats = scanner.nextInt();

        // SQL query to update the available seats for the specified flight
        String updateQuery = "UPDATE flight SET available_seats = ? WHERE flight_no = ?";

        // Execute the update
        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
        preparedStatement.setInt(1, availableSeats);
        preparedStatement.setString(2, flightNumber);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Successfully updated available seats for flight " + flightNumber);
        } else {
            System.out.println("Failed to update available seats. Flight number may be invalid.");
        }
    }

    // Method to cancel a booking
    static void cancelBooking(Connection connection) throws SQLException {
        // Prompt the user for booking information
        System.out.print("Enter your booking ID: ");
        int bookingId = scanner.nextInt();

        // Get the flight number associated with the booking
        String flightNumber = getFlightNumberForBooking(connection, bookingId);

        if (flightNumber != null) {
            // Increase the available seats for the flight
            increaseAvailableSeats(connection, flightNumber);

            // Delete the booking
            String cancelBookingQuery = "DELETE FROM booking WHERE booking_id = ?";
            try (PreparedStatement cancelBookingStatement = connection.prepareStatement(cancelBookingQuery)) {
                cancelBookingStatement.setInt(1, bookingId);

                int rowsAffected = cancelBookingStatement.executeUpdate();
                if (rowsAffected > 0) {
                    String cancelpayment = "SELECT amount FROM payment WHERE booking_id = ?";
                    try (PreparedStatement cancelpaymentstatement = connection.prepareStatement(cancelpayment)) {
                        cancelpaymentstatement.setInt(1, bookingId);

                        try (ResultSet paymentResult = cancelpaymentstatement.executeQuery()) {
                            if (paymentResult.next()) {
                                double gmount = paymentResult.getDouble("amount");
                                System.out.println("Amount :" + gmount);
                                String Deletepayment = "DELETE FROM payment WHERE booking_id = ?";
                                try (PreparedStatement deletepaymentstatement = connection
                                        .prepareStatement(Deletepayment)) {
                                    deletepaymentstatement.setInt(1, bookingId);
                                }
                            }
                        }
                    }
                    System.out.println("Successfully canceled booking " + bookingId);
                } else {
                    System.out.println("Failed to cancel booking. Booking ID may be invalid.");
                }
            }
        } else {
            System.out.println("Invalid booking ID. Cancel booking failed.");
        }
    }

    // Method to retrieve the flight number associated with a booking
    static String getFlightNumberForBooking(Connection connection, int bookingId) throws SQLException {
        // SQL query to get the flight number for a specific booking
        String getFlightNumberQuery = "SELECT flight.flight_no FROM flight INNER JOIN booking ON flight.flight_id = booking.flight_id WHERE booking.booking_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(getFlightNumberQuery)) {
            preparedStatement.setInt(1, bookingId);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("flight_no");
                } else {
                    return null; // Booking not found or associated flight not found
                }
            }
        }
    }

    // Method to increase the available seats for a specific flight
    static void increaseAvailableSeats(Connection connection, String flightNumber) throws SQLException {
        // SQL query to increase the available seats for the specified flight
        String updateQuery = "UPDATE flight SET available_seats = available_seats + 1 WHERE flight_no = ?";

        // Execute the update
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, flightNumber);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Successfully updated available seats for flight " + flightNumber);
            } else {
                System.out.println("Failed to update available seats. Flight number may be invalid.");
            }
        }
    }

    // Method to validate a customer ID
    static int validateCustomerId(Connection connection, int customerId) throws SQLException {
        // SQL query to validate a customer ID
        String validateCustomerQuery = "SELECT customer_id FROM customer WHERE customer_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(validateCustomerQuery)) {
            preparedStatement.setInt(1, customerId);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return customerId; // Customer ID is valid
                } else {
                    return -1; // Customer ID not found
                }
            }
        }
    }

    // Method to insert a new customer into the database
    static int insertCustomer(Connection connection, String firstName, String lastName, String email)
            throws SQLException {
        // SQL query to insert a new customer and obtain the generated customer ID
        String insertCustomerQuery = "INSERT INTO customer (first_name, last_name, email) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertCustomerQuery,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);

            // Execute the update and check the number of affected rows
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                return -1; // Inserting customer failed
            }

            // Retrieve the generated customer ID
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Return the generated customer ID
            } else {
                return -1; // No ID obtained
            }
        }
    }

    // Method to handle the booking process
    static void bookFlight(Connection connection) throws Exception {
        // Initialize customer ID to -1
        int customerId = -1;
        scanner.nextLine();
        // Ask if the user has a customer ID
        System.out.print("Do you have a customer ID? (yes/no): ");
        String hasCustomerId = scanner.nextLine().toLowerCase();

        // If the user has a customer ID
        if (hasCustomerId.equals("yes")) {
            System.out.print("Enter your customer ID: ");
            customerId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Validate the customer ID
            customerId = validateCustomerId(connection, customerId);

            // If the customer ID is invalid, prompt the user to try again
            if (customerId == -1) {
                System.out.println("Invalid customer ID. Please try again.");
                return;
            }
        } else if (!hasCustomerId.equals("no")) {
            // If the user enters an invalid choice, exit the booking process
            System.out.println("Invalid choice. Booking failed.");
            return;
        }

        // If the customer does not exist or chose to enter new details
        if (customerId == -1) {
            System.out.print("Enter your first name: ");
            String firstName = scanner.nextLine();

            System.out.print("Enter your last name: ");
            String lastName = scanner.nextLine();

            System.out.print("Enter your email: ");
            String email = scanner.nextLine();

            // Insert customer details and obtain the generated customer ID
            customerId = insertCustomer(connection, firstName, lastName, email);

            // If customer creation fails, exit the booking process
            if (customerId == -1) {
                System.out.println("Failed to create a new customer. Booking failed.");
                return;
            }
        }

        // Prompt the user for the flight number to book
        System.out.print("Enter the flight number you want to book: ");
        flightNumber = scanner.nextLine();

        // Get the flight_id for the given flight number
        int flightId = getFlightId(connection, flightNumber);

        // Check if the flight exists
        if (flightId != -1) {
            // Check if there are available seats for the selected flight
            int availableSeats = getAvailableSeats(connection, flightNumber);

            // If there are available seats, proceed with the booking
            if (availableSeats > 0) {
                // Decrease the available seats for the flight
                updateAvailableSeats(connection, flightNumber, availableSeats - 1);

                // Insert a new booking
                String insertBookingQuery = "INSERT INTO booking (customer_id, flight_id) VALUES (?, ?)";
                try (PreparedStatement insertBookingStatement = connection.prepareStatement(insertBookingQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
                    insertBookingStatement.setInt(1, customerId);
                    insertBookingStatement.setInt(2, flightId);

                    // Execute the booking insertion and check the number of affected rows
                    int bookingRowsAffected = insertBookingStatement.executeUpdate();
                    if (bookingRowsAffected > 0) {
                        // Retrieve the auto-generated booking ID
                        ResultSet generatedKeys = insertBookingStatement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int bookingId = generatedKeys.getInt(1);

                            payment();
                            System.out.println(
                                    "Successfully booked flight " + flightNumber + ". Booking ID: " + bookingId);
                        } else {
                            System.out.println("Failed to retrieve booking ID.");
                        }
                    } else {
                        System.out.println("Failed to book the flight.");
                    }
                }

            } else {
                // Inform the user that there are no available seats for the selected flight
                System.out.println("No available seats for the selected flight. Booking failed.");
            }
        } else {
            // Inform the user that the entered flight number is invalid
            System.out.println("Invalid flight number. Booking failed.");
        }
    }

    static void payment() throws Exception {
        double paymentAmount = fetchPaymentAmount(connection, flightNumber);

        if (paymentAmount < 0) {
            System.out.println("Invalid payment amount.");
            return;
        }

        System.out.println("Payment amount for Flight No " + flightNumber + " is: $" + paymentAmount);

        // Allow the customer to choose the payment method
        System.out.println("Choose your payment method:");
        System.out.println("1. Cash");
        System.out.println("2. Credit Card");
        System.out.println("3. Debit Card");
        System.out.print("Enter your choice: ");
        int paymentChoice = scanner.nextInt();

        switch (paymentChoice) {
            case 1:
                processCashPayment(paymentAmount);
                break;
            case 2:
                processCreditCardPayment(paymentAmount);
                break;
            case 3:
                processDebitCardPayment(paymentAmount);
                break;
            default:
                System.out.println("Invalid choice. Payment canceled.");
                return;
        }

    }

    static void processCashPayment(double paymentAmount) throws SQLException {

        System.out.print("Enter the amount of cash you are providing: $");
        double providedAmount = scanner.nextDouble();

        if (providedAmount < paymentAmount) {
            System.out.println("Insufficient payment. Payment canceled. Rebook your flight");
            processCashPayment(paymentAmount);
        } else {
            double change = providedAmount - paymentAmount;
            String paymentQuery = "INSERT INTO payment (amount) VALUES ( ?)";
            try (PreparedStatement paymentStatement = connection.prepareStatement(paymentQuery)) {
                paymentStatement.setDouble(1, paymentAmount);
                paymentStatement.executeUpdate();
            }
            System.out.println("Payment successful. Change to be returned: $" + change);

        }
    }

    private static void processCreditCardPayment(double paymentAmount) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter your credit card number: ");
        String creditCardNumber = scanner.nextLine();

        // Simulate credit card verification by checking if the card number is 16 digits
        if (creditCardNumber.length() == 16) {
            String paymentQuery = "INSERT INTO payment (amount) VALUES ( ?)";
            try (PreparedStatement paymentStatement = connection.prepareStatement(paymentQuery)) {
                paymentStatement.setDouble(1, paymentAmount);
                paymentStatement.executeUpdate();
            }
            System.out.println("Credit card payment processed successfully.");
        } else {
            System.out.println("Invalid credit card number. Payment failed.");
        }
    }

    private static void processDebitCardPayment(double paymentAmount) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter your debit card number: ");
        String debitCardNumber = scanner.nextLine();

        // Simulate debit card verification by checking if the card number is 16 digits
        if (debitCardNumber.length() == 16) {
            String paymentQuery = "INSERT INTO payment (amount) VALUES ( ?)";
            try (PreparedStatement paymentStatement = connection.prepareStatement(paymentQuery)) {
                paymentStatement.setDouble(1, paymentAmount);
                paymentStatement.executeUpdate();
            }
            System.out.println("Debit card payment processed successfully.");
        } else {
            System.out.println("Invalid debit card number. Payment failed.");
        }
    }

    private static double fetchPaymentAmount(Connection connection, String flightNumber) {
        String query = "SELECT payment_amount FROM flight WHERE flight_no = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, flightNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("payment_amount");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while fetching payment amount: " + e.getMessage());
        }
        return -1; // Return a negative value to indicate an error or invalid booking ID
    }

    // Method to get the available seats for a specific flight
    static int getAvailableSeats(Connection connection, String flightNumber) throws SQLException {
        // SQL query to get the available seats for a specific flight
        String getAvailableSeatsQuery = "SELECT available_seats FROM flight WHERE flight_no = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(getAvailableSeatsQuery)) {
            preparedStatement.setString(1, flightNumber);

            // Execute the query and retrieve the result set
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("available_seats");
                } else {
                    return -1; // Flight not found
                }
            }
        }
    }

    // Method to update the available seats for a specific flight in the database
    static void updateAvailableSeats(Connection connection, String flightNumber, int availableSeats)
            throws SQLException {
        // SQL query to update the available seats for the specified flight
        String updateQuery = "UPDATE flight SET available_seats = ? WHERE flight_no = ?";

        // Execute the update
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, availableSeats);
            preparedStatement.setString(2, flightNumber);

            // Check the number of affected rows to determine the success of the update
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Successfully updated available seats for flight " + flightNumber);
            } else {
                System.out.println("Failed to update available seats. Flight number may be invalid.");
            }
        }
    }

    // Method to get the flight ID for a specific flight number from the database
    static int getFlightId(Connection connection, String flightNumber) throws SQLException {
        // SQL query to retrieve the flight ID for the specified flight number
        String getFlightIdQuery = "SELECT flight_id FROM flight WHERE flight_no = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(getFlightIdQuery)) {
            preparedStatement.setString(1, flightNumber);

            // Execute the query and retrieve the result set
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("flight_id");
                } else {
                    return -1; // Flight not found
                }
            }
        }
    }

    // Method to view bookings for a specific customer in the database
    static void viewBookings(Connection connection) throws SQLException {
        // Prompt the user for their customer ID
        System.out.print("Enter your customer ID: ");
        int customerId = scanner.nextInt();

        // SQL query to retrieve booked flights for the specified customer
        String viewBookingsQuery = "SELECT booking.booking_id, booking.customer_id, flight.flight_no, flight.origin, flight.destination, flight.departure_time, flight.arrival_time, flight.available_seats "
                + "FROM flight INNER JOIN booking ON flight.flight_id = booking.flight_id "
                + "WHERE booking.customer_id = ?";
        try (PreparedStatement viewBookingsStatement = connection.prepareStatement(viewBookingsQuery)) {
            viewBookingsStatement.setInt(1, customerId);

            // Execute the query and retrieve the result set
            try (ResultSet resultSet = viewBookingsStatement.executeQuery()) {
                System.out.println("Your Booked Flights:");
                System.out.println(
                        "Booking ID | Customer ID | Flight Number | Origin | Destination | Departure Time | Arrival Time | Available Seats");

                // Display the booked flights
                while (resultSet.next()) {
                    int bookingId = resultSet.getInt("booking_id");
                    int customerID = resultSet.getInt("customer_id");
                    String flightNumber = resultSet.getString("flight_no");
                    String origin = resultSet.getString("origin");
                    String destination = resultSet.getString("destination");
                    Timestamp departureTime = resultSet.getTimestamp("departure_time");
                    Timestamp arrivalTime = resultSet.getTimestamp("arrival_time");
                    int availableSeats = resultSet.getInt("available_seats");

                    System.out.printf("%d | %d | %s | %s | %s | %s | %s | %d\n", bookingId, customerID, flightNumber,
                            origin, destination,
                            departureTime, arrivalTime, availableSeats);
                }
            }
        }
    }

    // Method to update an existing booking in the database
    static void updateBooking(Connection connection) throws SQLException {
        // Prompt the user for their booking ID
        System.out.print("Enter your booking ID: ");
        int bookingId = scanner.nextInt();

        // Prompt for the new flight number
        scanner.nextLine(); // Consume newline
        System.out.print("Enter the new flight number: ");
        String newFlightNumber = scanner.nextLine();

        // Get the flight number associated with the booking
        String oldFlightNumber = getFlightNumberForBooking(connection, bookingId);

        if (oldFlightNumber != null) {
            // Increase the available seats for the old flight
            increaseAvailableSeats(connection, oldFlightNumber);

            // Get the new flight_id for the given new flight number
            int newFlightId = getFlightId(connection, newFlightNumber);

            if (newFlightId != -1) {
                // Update the booking with the new flight
                String updateBookingQuery = "UPDATE booking SET flight_id = ? WHERE booking_id = ?";
                try (PreparedStatement updateBookingStatement = connection.prepareStatement(updateBookingQuery)) {
                    updateBookingStatement.setInt(1, newFlightId);
                    updateBookingStatement.setInt(2, bookingId);

                    // Execute the update and check the number of affected rows
                    int rowsAffected = updateBookingStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        // Decrease the available seats for the new flight
                        decreaseAvailableSeats(connection, newFlightNumber);
                        System.out.println("Successfully updated booking " + bookingId + " with new flight number "
                                + newFlightNumber);
                    } else {
                        System.out.println("Failed to update booking. Booking ID or flight number may be invalid.");
                    }
                }
            } else {
                System.out.println("Invalid new flight number. Update failed.");
            }
        } else {
            System.out.println("Invalid booking ID. Update failed.");
        }
    }

    // Method to decrease the available seats for a specific flight in the database
    static void decreaseAvailableSeats(Connection connection, String flightNumber) throws SQLException {
        // SQL query to decrease the available seats for the specified flight
        String updateQuery = "UPDATE flight SET available_seats = available_seats - 1 WHERE flight_no = ?";

        // Execute the update
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, flightNumber);

            // Check the number of affected rows to determine the success of the update
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Successfully updated available seats for flight " + flightNumber);
            } else {
                System.out.println("Failed to update available seats. Flight number may be invalid.");
            }
        }
    }

    // Method to update customer details in the database
    static void updateCustomerDetails(Connection connection) throws SQLException {
        // Prompt the user for their customer ID
        System.out.print("Enter your customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Check if the customer exists
        if (doesCustomerExist(connection, customerId)) {
            // Prompt the user for new details
            System.out.print("Enter new first name: ");
            String newFirstName = scanner.nextLine();

            System.out.print("Enter new last name: ");
            String newLastName = scanner.nextLine();

            System.out.print("Enter new email: ");
            String newEmail = scanner.nextLine();

            // SQL query to update customer details
            String updateCustomerQuery = "UPDATE customer SET first_name = ?, last_name = ?, email = ? WHERE customer_id = ?";

            // Execute the update
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateCustomerQuery)) {
                preparedStatement.setString(1, newFirstName);
                preparedStatement.setString(2, newLastName);
                preparedStatement.setString(3, newEmail);
                preparedStatement.setInt(4, customerId);

                // Check the number of affected rows to determine the success of the update
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Customer details updated successfully.");
                } else {
                    System.out.println("Failed to update customer details.");
                }
            }
        } else {
            System.out.println("Customer does not exist. Update failed.");
        }
    }

    // Method to check if a customer exists in the database
    static boolean doesCustomerExist(Connection connection, int customerId) throws SQLException {
        // SQL query to check if a customer with the given ID exists
        String checkCustomerQuery = "SELECT * FROM customer WHERE customer_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(checkCustomerQuery)) {
            preparedStatement.setInt(1, customerId);

            // Execute the query and check if the result set has any rows
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    // Method to add a pilot to the database
    static void addPilot(Connection connection) throws SQLException {
        // Prompt the user for pilot details
        scanner.nextLine();
        System.out.print("Enter the pilot's first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter the pilot's last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter the pilot's license number: ");
        String licenseNumber = scanner.nextLine();

        // SQL statement to insert the pilot and get the auto-generated ID
        String insertQuery = "INSERT INTO pilot (first_name, last_name, license_number) VALUES (?, ?, ?)";

        // Use PreparedStatement.RETURN_GENERATED_KEYS to retrieve the auto-generated ID
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, licenseNumber);

            // Execute the insert
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Pilot added successfully.");

                // Retrieve the auto-generated ID for the pilot
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int pilotId = generatedKeys.getInt(1);
                    System.out.println("Pilot ID: " + pilotId);
                } else {
                    System.out.println("Failed to retrieve pilot ID.");
                }
            } else {
                System.out.println("Failed to add pilot.");
            }
        }
    }

    // Method to assign a pilot to a flight in the database
    static void assignPilotToFlight(Connection connection) throws SQLException {
        // Prompt for pilot ID
        System.out.print("Enter the pilot ID: ");
        int pilotId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Check if the pilot with the given ID exists
        if (!doesPilotExist(connection, pilotId)) {
            System.out.println("Pilot with ID " + pilotId + " does not exist.");
            return;
        }

        // Prompt for flight number
        System.out.print("Enter the flight number: ");
        String flightNumber = scanner.nextLine();

        // Check if the flight with the given flight number exists
        if (!doesFlightExist(connection, flightNumber)) {
            System.out.println("Flight with number " + flightNumber + " does not exist.");
            return;
        }

        // Assign the pilot to the flight
        String assignQuery = "UPDATE flight SET pilot_id = ? WHERE flight_no = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(assignQuery)) {
            preparedStatement.setInt(1, pilotId);
            preparedStatement.setString(2, flightNumber);

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out
                        .println("Pilot with ID " + pilotId + " assigned to flight " + flightNumber + " successfully.");
            } else {
                System.out.println("Failed to assign pilot to the flight.");
            }
        }
    }

    // Method to check if a pilot with a given ID exists in the pilote table
    static boolean doesPilotExist(Connection connection, int pilotId) throws SQLException {
        // SQL query to check if a pilot with the given ID exists
        String checkPilotQuery = "SELECT * FROM pilot WHERE pilot_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(checkPilotQuery)) {
            preparedStatement.setInt(1, pilotId);

            // Execute the query and check if the result set has any rows
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    // Method to check if a flight with a given flight number exists in the flight
    // table
    static boolean doesFlightExist(Connection connection, String flightNumber) throws SQLException {
        // SQL query to check if a flight with the given flight number exists
        String checkFlightQuery = "SELECT * FROM flight WHERE flight_no = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(checkFlightQuery)) {
            preparedStatement.setString(1, flightNumber);

            // Execute the query and check if the result set has any rows
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    // Method to view pilot details from the pilote table
    static void viewPilotDetails(Connection connection) throws SQLException {
        // SQL query to select all columns from the pilote table
        String query = "SELECT * FROM pilot";

        // Execute the query and print pilot details
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("Pilot ID | First Name | Last Name");
            while (resultSet.next()) {
                int pilotId = resultSet.getInt("pilot_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                System.out.printf("%d | %s | %s\n", pilotId, firstName, lastName);
            }
        }
    }

    // Method to write boarding pass details to a file for a given customer ID
    public static void writeBoardingPassToFile(Connection connection) throws SQLException {
        // Use the default file name
        String filename = DEFAULT_FILE_NAME;

        // Ask for customer ID
        System.out.print("Enter your customer ID: ");
        int customerId = scanner.nextInt();

        // Check if the customer ID exists in the bookings table
        if (doesCustomerIdExistInBookings(connection, customerId)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                // SQL query to retrieve boarding pass details for the given customer ID
                String query = "SELECT booking.booking_id, flight.flight_no, flight.origin, flight.destination, " +
                        "flight.departure_time, flight.arrival_time, customer.first_name, " +
                        "customer.last_name, pilot.first_name AS pilot_first_name, " +
                        "pilot.last_name AS pilot_last_name " +
                        "FROM booking " +
                        "INNER JOIN flight ON booking.flight_id = flight.flight_id " +
                        "INNER JOIN customer ON booking.customer_id = customer.customer_id " +
                        "INNER JOIN pilot ON flight.pilot_id = pilot.pilot_id " +
                        "WHERE booking.customer_id = ?";

                // Execute the query and write boarding pass details to the file
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, customerId);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            // Write boarding pass details to the file
                            writer.write("Boarding Pass for Customer ID " + customerId);
                            writer.newLine();
                            writer.write("Booking ID: " + resultSet.getInt("booking_id"));
                            writer.newLine();
                            writer.write("Passenger Name: " + resultSet.getString("first_name") + " "
                                    + resultSet.getString("last_name"));
                            writer.newLine();
                            writer.write("Flight Number: " + resultSet.getString("flight_no"));
                            writer.newLine();
                            writer.write("Departure Airport: " + resultSet.getString("origin"));
                            writer.newLine();
                            writer.write("Destination Airport: " + resultSet.getString("destination"));
                            writer.newLine();
                            writer.write("Departure Time: " + resultSet.getTimestamp("departure_time"));
                            writer.newLine();
                            writer.write("Arrival Time: " + resultSet.getTimestamp("arrival_time"));
                            writer.newLine();
                            writer.write("Pilot Name: " + resultSet.getString("pilot_first_name") + " "
                                    + resultSet.getString("pilot_last_name"));
                        }
                    }
                }

                System.out.println("Boarding pass details written to " + filename);
            } catch (IOException | SQLException e) {
                System.out.println("An error occurred while writing to the file.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Customer ID does not exist in the bookings table.");
        }
    }

    // Helper method to check if a customer ID exists in the bookings table
    static boolean doesCustomerIdExistInBookings(Connection connection, int customerId) throws SQLException {
        // SQL query to count the occurrences of the customer ID in the bookings table
        String query = "SELECT COUNT(*) AS count FROM booking WHERE customer_id = ?";

        // Execute the query and check if there are any occurrences
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, customerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }
        }

        return false;
    }

}