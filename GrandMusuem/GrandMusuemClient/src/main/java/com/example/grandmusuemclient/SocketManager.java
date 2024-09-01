package com.example.grandmusuemclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketManager {

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void connect(String host, int port) throws IOException {
        // Create a new socket connection to the server
        socket = new Socket(host, port);
        // Initialize input and output streams
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static void send(String message) {
        if (socket != null && !socket.isClosed()) {
            out.println(message);
        } else {
            System.err.println("Socket is not connected.");
        }
    }

    public static String receive() throws IOException {
        if (socket != null && !socket.isClosed()) {
            return in.readLine();
        } else {
            throw new IOException("Socket is not connected.");
        }
    }

    public static void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            in.close();
            out.close();
            socket.close();
        }
    }

    public static boolean isConnected() {
        return socket != null && !socket.isClosed();
    }
    public static Socket getSocket() {
        return socket;
    }
}
