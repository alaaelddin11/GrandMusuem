package com.example.grandmusuemclient;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ChattingRoom {
    private Stage stage;
    private Scene scene;
    private VBox chatBox; // Use VBox to stack messages vertically
    private TextField messageField;
    private Button sendButton;
    private Button homeButton; // Button to return to the home screen
    private Button saveChatLogButton; // Button to save chat log
    private ComboBox<String> statusComboBox; // Dropdown for user status
    private PrintWriter out;
    private BufferedReader in;

    public ChattingRoom(Stage stage) {
        this.stage = stage;

        // Get the socket from SocketManager
        Socket clientSocket = SocketManager.getSocket();
        if (clientSocket == null || !SocketManager.isConnected()) {
            showError("Socket connection is not available.");
            return;
        }

        // Initialize input and output streams for communication
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            showError("Error setting up communication: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // UI setup
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));

        // Create a VBox for chat messages
        chatBox = new VBox();
        chatBox.setSpacing(10);
        chatBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);
        layout.setCenter(scrollPane);

        // Create message input field and send button
        messageField = new TextField();
        messageField.setPromptText("Type your message here...");
        sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: #000000; -fx-font-size: 14px;");

        // Create home button
        homeButton = new Button("Home");
        homeButton.setStyle("-fx-background-color: #ffd700; -fx-text-fill: #000000; -fx-font-size: 14px;");
        homeButton.setOnAction(event -> goHome());

        // Create save chat log button
        saveChatLogButton = new Button("Save Chat Log");
        saveChatLogButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: #000000; -fx-font-size: 14px;");
        saveChatLogButton.setOnAction(event -> saveChatLog());

        // Create status dropdown
        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Available", "Busy", "Offline");
        statusComboBox.setValue("Available"); // Default status

        statusComboBox.setOnAction(event -> updateStatus(statusComboBox.getValue()));

        HBox inputBox = new HBox(10, messageField, sendButton);
        inputBox.setPadding(new Insets(10));

        VBox bottomBox = new VBox(10, inputBox, homeButton, saveChatLogButton, statusComboBox);
        layout.setBottom(bottomBox);

        // Setup the event for the send button
        sendButton.setOnAction(event -> sendMessage());
        messageField.setOnAction(event -> sendMessage());

        // Scene and stage setup
        scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Chat Room");
        stage.show();

        // Start listening for incoming messages
        new Thread(this::listenForMessages).start();
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            try {
                out.println("CHAT " + message); // Send chat message to the server
                messageField.clear();
                addMessageToChat(new ChatMessage(message, true)); // Add sent message to chat
            } catch (Exception e) {
                showError("Failed to send message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                // Parse the incoming message
                if (message.startsWith("MESSAGE")) {
                    String[] parts = message.split(",", 4); // Splits into "MESSAGE", <userId>, <status>, and <message>
                    if (parts.length == 4) {
                        String userId = parts[1];
                        String status = parts[2];
                        String actualMessage = parts[3];


                        // Format received messages as well
                        String formattedMessage = String.format("[%s - %s]: %s", userId, status, actualMessage);
                        addMessageToChat(new ChatMessage(formattedMessage, false)); // Add received message to chat
                    } else {
                   //    Platform.runLater(() -> addMessageToChat(new ChatMessage("Error: Invalid message format received", false)));
                    }
                } else {
                    // Handle other types of messages or errors if necessary
                  //  Platform.runLater(() -> addMessageToChat(new ChatMessage("Error: Invalid message format received", false)));
                }
            }
        } catch (IOException e) {
            showError("Connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addMessageToChat(ChatMessage chatMessage) {
        Platform.runLater(() -> {
            Label messageLabel = new Label(chatMessage.getContent());
            messageLabel.setWrapText(true);
            messageLabel.setPadding(new Insets(5, 10, 5, 10));

            if (chatMessage.isSent()) {
                // Style for messages sent by the user
                messageLabel.setStyle("-fx-background-color: #c3f1c3; -fx-text-fill: #000000; -fx-border-radius: 10; -fx-background-radius: 10;");
                messageLabel.setAlignment(Pos.CENTER_RIGHT);
            } else {
                // Style for messages from others
                messageLabel.setStyle("-fx-background-color: #cce5ff; -fx-text-fill: #000000; -fx-border-radius: 10; -fx-background-radius: 10;");
                messageLabel.setAlignment(Pos.CENTER_LEFT);
            }
            chatBox.getChildren().add(messageLabel);
        });
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void goHome() {
        stage.setScene(new HomeScreen(stage, true).getScene());
    }

    private void saveChatLog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Chat Log");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (var node : chatBox.getChildren()) {
                    if (node instanceof Label) {
                        Label label = (Label) node;
                        writer.write(label.getText());
                        writer.newLine();
                    }
                }
                showAlert("Chat log saved successfully.");
            } catch (IOException e) {
                showError("Failed to save chat log: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void updateStatus(String status) {
        out.println("STATUS " + status);
    }

    public Scene getScene() {
        return scene;
    }
}
