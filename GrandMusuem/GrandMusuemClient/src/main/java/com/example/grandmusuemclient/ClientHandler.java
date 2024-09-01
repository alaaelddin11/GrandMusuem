package com.example.grandmusuemclient;
import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Read messages from client and handle commands
            String message;
            while ((message = in.readLine()) != null) {
                handleMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Handle incoming messages
    private void handleMessage(String message) {
        if (message.startsWith("LOGIN:")) {
            String[] parts = message.substring(6).split(",");
            String username = parts[0].trim();
            String password = parts[1].trim();
            if (Server.loginUser(username, password)) {
                this.username = username;
                sendMessage("Login successful");
                Server.broadcastMessage(username + " has joined the chat");
            } else {
                sendMessage("Login failed");
            }
        } else if (message.startsWith("SIGNUP")) {
            String[] parts = message.substring(9).split(",");
            String username = parts[0].trim();
            String email = parts[1].trim();
            String password = parts[2].trim();
            String nationality = parts[3].trim();
            String gender = parts[4].trim();
            if (Server.registerUser(username, email, password, nationality, gender)) {
                sendMessage("Registration successful");
            } else {
                sendMessage("Registration failed");
            }
        } else {
            // Broadcast the message to all clients
            Server.broadcastMessage(username + ": " + message);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
