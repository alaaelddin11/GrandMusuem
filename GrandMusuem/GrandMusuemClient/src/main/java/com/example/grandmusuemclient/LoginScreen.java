package com.example.grandmusuemclient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginScreen {

    private Scene scene;
    private Stage primaryStage;

    public LoginScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;

        try {
            // Initialize SocketManager and connect to server
            SocketManager.connect("localhost", 12345);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Unable to connect to the server.");
            return;
        }

        // Initialize layout
        VBox layout = new VBox();
        layout.setPadding(new Insets(20));
        layout.setSpacing(15);
        layout.setAlignment(Pos.CENTER);

        // Title
        Label title = new Label("Login");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Image
        Image loginImage = new Image(getClass().getResourceAsStream("/img.jfif"));
        ImageView imageView = new ImageView(loginImage);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);

        // Form fields
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        // Buttons
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button");

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("button");

        // Add elements to layout
        layout.getChildren().addAll(title, imageView, emailLabel, emailField, passwordLabel, passwordField, loginButton, cancelButton);

        // Set button actions
        loginButton.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText()));
        cancelButton.setOnAction(e -> primaryStage.setScene(new HomeScreen(primaryStage, false).getScene()));

        // Create scene and set it to the primary stage
        scene = new Scene(layout, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }

    private void handleLogin(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "All fields are required.");
            return;
        }

        try {
            // Send login request
            SocketManager.send("LOGIN " + email + "," + password);

            // Read response
            String response = SocketManager.receive();
            switch (response) {
                case "SUCCESS":
                    showAlert(Alert.AlertType.INFORMATION, "Login Successful", "You have logged in successfully!");
                    primaryStage.setScene(new HomeScreen(primaryStage, true).getScene()); // HomeScreen with logged-in state
                    break;
                case "LOGIN_FAILED: Invalid password":
                    showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid password.");
                    break;
                case "LOGIN_FAILED: User not found":
                    showAlert(Alert.AlertType.ERROR, "Login Error", "User not found.");
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Login Error", "An unknown error occurred.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred. Please try again.");
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
