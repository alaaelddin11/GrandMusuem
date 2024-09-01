package com.example.grandmusuemclient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpScreen {

    private Scene scene;
    private Stage primaryStage;

    public SignUpScreen(Stage primaryStage) {
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
        Label title = new Label("Sign Up");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Image
        Image signUpImage = new Image(getClass().getResourceAsStream("/img.jfif"));
        ImageView imageView = new ImageView(signUpImage);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);

        // Form fields
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");

        // Gender dropdown
        Label genderLabel = new Label("Gender:");
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female");

        // Nationality dropdown
        Label nationalityLabel = new Label("Nationality:");
        ComboBox<String> nationalityComboBox = new ComboBox<>();
        nationalityComboBox.getItems().addAll("American", "British", "Canadian", "Egyptian", "French", "German", "Other");

        // Buttons
        Button signUpButton = new Button("Sign Up");
        signUpButton.getStyleClass().add("button");

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("button");

        // Add elements to layout
        layout.getChildren().addAll(title, imageView, usernameLabel, usernameField, emailField, passwordField,
                confirmPasswordField, genderLabel, genderComboBox, nationalityLabel, nationalityComboBox, signUpButton, cancelButton);

        // Set button actions
        signUpButton.setOnAction(e -> handleSignUp(usernameField.getText(), emailField.getText(), passwordField.getText(), confirmPasswordField.getText(), genderComboBox.getValue(), nationalityComboBox.getValue()));
        cancelButton.setOnAction(e -> primaryStage.setScene(new HomeScreen(primaryStage, false).getScene()));

        // Create scene and set it to the primary stage
        scene = new Scene(layout, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }

    private void handleSignUp(String username, String email, String password, String confirmPassword, String gender, String nationality) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Sign Up Error", "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Sign Up Error", "Passwords do not match.");
            return;
        }

        try {
            // Send sign-up request
            SocketManager.send("SIGNUP " + username + "," + email + "," + password + "," + gender + "," + nationality);

            // Read response
            String response = SocketManager.receive();
            switch (response) {
                case "SUCCESS":
                    showAlert(Alert.AlertType.INFORMATION, "Sign Up Successful", "You have signed up successfully!");
                    primaryStage.setScene(new LoginScreen(primaryStage).getScene());
                    break;
                case "SIGNUP_FAILED: User already exists":
                    showAlert(Alert.AlertType.ERROR, "Sign Up Error", "User already exists.");
                    break;
                case "SIGNUP_FAILED: Invalid email":
                    showAlert(Alert.AlertType.ERROR, "Sign Up Error", "Invalid email.");
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Sign Up Error", "An unknown error occurred.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Sign Up Error", "An error occurred. Please try again.");
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
