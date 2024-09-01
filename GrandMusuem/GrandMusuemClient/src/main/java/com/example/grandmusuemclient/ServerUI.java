package com.example.grandmusuemclient;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ServerUI extends Application {

    private ListView<String> onlineUsersList;
    private ListView<String> offlineUsersList;

    private ObservableList<String> onlineUsers = FXCollections.observableArrayList();
    private ObservableList<String> offlineUsers = FXCollections.observableArrayList();

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        onlineUsersList = new ListView<>(onlineUsers);
        offlineUsersList = new ListView<>(offlineUsers);

        Label onlineLabel = new Label("Online Users");
        Label offlineLabel = new Label("Offline Users");

        BorderPane onlinePane = new BorderPane();
        onlinePane.setTop(onlineLabel);
        onlinePane.setCenter(onlineUsersList);

        BorderPane offlinePane = new BorderPane();
        offlinePane.setTop(offlineLabel);
        offlinePane.setCenter(offlineUsersList);

        root.setLeft(onlinePane);
        root.setRight(offlinePane);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Museum Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize server connection in a separate thread
        new Thread(this::connectToServer).start();

        // Set up a periodic refresh
        setupPeriodicRefresh();
    }

    private void connectToServer() {
        try {
            // Connect to the server (replace with your server's IP and port)
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start listening for messages from the server
            new Thread(this::listenToServer).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenToServer() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                // Debug log for the line received
                System.out.println("Received from server: " + line);

                // Update UI based on the message from the server
                String finalLine = line;
                Platform.runLater(() -> {
                    if (finalLine.startsWith("ONLINE_USERS_LIST")) {
                        List<String> users = parseUserList(finalLine);
                        onlineUsers.setAll(users);
                    } else if (finalLine.startsWith("OFFLINE_USERS_LIST")) {
                        List<String> users = parseUserList(finalLine);
                        offlineUsers.setAll(users);
                    }
                });
            }
        } catch (SocketException e) {
            System.out.println("Connection closed by server.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            Platform.runLater(this::cleanup);
        }
    }

    private List<String> parseUserList(String response) {
        List<String> users = new ArrayList<>();
        String[] parts = response.split(":");
        if (parts.length > 1) {
            String[] userList = parts[1].split(",");  // Splitting by comma
            for (String user : userList) {
                if (!user.trim().isEmpty()) {
                    users.add(user.trim());
                }
            }
        }
        return users;
    }


    private void setupPeriodicRefresh() {
        // Refresh every 5 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> refreshUI()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void refreshUI() {
        new Thread(() -> {
            try {
                // Request online users from the server
                out.println("GET_ONLINE_USERS");

                // Request offline users from the server
                out.println("GET_OFFLINE_USERS");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void cleanup() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        cleanup();
    }
}
