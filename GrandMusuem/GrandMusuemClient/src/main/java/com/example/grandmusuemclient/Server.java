package com.example.grandmusuemclient;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class Server {

    private static final int PORT = 12345; // Port number for the server
    private static Set<ClientHandler> clientHandlers = new HashSet<>(); // Store connected clients
    private static Connection dbConnection; // Database connection

    public static void main(String[] args) {
        // Initialize database connection
        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/museum", "root", "password");
            System.out.println("Database connected.");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Start the server
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            // Continuously accept new client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start(); // Handle each client in a new thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close database connection when the server is stopped
            try {
                if (dbConnection != null) dbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Broadcast message to all connected clients
    public static void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    // Handle user registration
    public static boolean registerUser(String username, String email, String password, String nationality, String gender) {
        String query = "INSERT INTO users (username, email, password, nationality, gender) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password); // Consider hashing the password
            stmt.setString(4, nationality);
            stmt.setString(5, gender);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Handle user login
    public static boolean loginUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // Consider hashing the password
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next(); // Returns true if user is found
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
