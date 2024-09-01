package com.example.grandmusuemclient;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class AdminInterface extends Application {

    private TableView<User> tableView;
    private ObservableList<User> users;
    private TextField usernameField, emailField, passwordField;
    private ComboBox<String> nationalityComboBox, genderComboBox, statusComboBox;
    private Button addButton, updateButton, deleteButton;
    private Connection dbConnection;

    @Override
    public void start(Stage primaryStage) {
        // Establish database connection
        connectToDatabase();

        // Initialize user list
        users = FXCollections.observableArrayList();
        loadUsersFromDatabase();

        // Initialize UI components
        tableView = new TableView<>();
        tableView.setItems(users);
        tableView.setEditable(true);

        // Setup columns
        TableColumn<User, Integer> userIdColumn = new TableColumn<>("User ID");
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> nationalityColumn = new TableColumn<>("Nationality");
        nationalityColumn.setCellValueFactory(new PropertyValueFactory<>("nationality"));

        TableColumn<User, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<User, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableView.getColumns().addAll(userIdColumn, usernameColumn, emailColumn, nationalityColumn, genderColumn, statusColumn);

        // Input fields
        usernameField = new TextField();
        usernameField.setPromptText("Username");

        emailField = new TextField();
        emailField.setPromptText("Email");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        nationalityComboBox = new ComboBox<>(FXCollections.observableArrayList("Egyptian", "American", "British", "French")); // Add more options as needed
        nationalityComboBox.setPromptText("Nationality");

        genderComboBox = new ComboBox<>(FXCollections.observableArrayList("Male", "Female"));
        genderComboBox.setPromptText("Gender");

        statusComboBox = new ComboBox<>(FXCollections.observableArrayList("Available", "Busy", "Offline"));
        statusComboBox.setPromptText("Status");
        statusComboBox.setValue("Offline"); // Default value

        // Buttons
        addButton = new Button("Add User");
        updateButton = new Button("Update User");
        deleteButton = new Button("Delete User");

        addButton.setOnAction(e -> addUser());
        updateButton.setOnAction(e -> updateUser());
        deleteButton.setOnAction(e -> deleteUser());

        // Layout
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        gridPane.add(new Label("Username:"), 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(new Label("Email:"), 0, 1);
        gridPane.add(emailField, 1, 1);
        gridPane.add(new Label("Password:"), 0, 2);
        gridPane.add(passwordField, 1, 2);
        gridPane.add(new Label("Nationality:"), 0, 3);
        gridPane.add(nationalityComboBox, 1, 3);
        gridPane.add(new Label("Gender:"), 0, 4);
        gridPane.add(genderComboBox, 1, 4);
        gridPane.add(new Label("Status:"), 0, 5);
        gridPane.add(statusComboBox, 1, 5);

        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton);
        gridPane.add(buttonBox, 1, 6);

        VBox layout = new VBox(10, tableView, gridPane);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Interface");
        primaryStage.show();
    }

    private void connectToDatabase() {
        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/museum", "root", "123456");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUsersFromDatabase() {
        try (Statement stmt = dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("nationality"),
                        rs.getString("gender"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addUser() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String nationality = nationalityComboBox.getValue();
        String gender = genderComboBox.getValue();
        String status = statusComboBox.getValue();

        try {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String sql = "INSERT INTO users (username, email, password_hash, nationality, gender, status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, nationality);
            pstmt.setString(5, gender);
            pstmt.setString(6, status);
            pstmt.executeUpdate();

            int userId = getGeneratedUserId();
            users.add(new User(userId, username, email, hashedPassword, nationality, gender, status));

            clearInputFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUser() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("No user selected", "Please select a user to update.");
            return;
        }

        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String nationality = nationalityComboBox.getValue();
        String gender = genderComboBox.getValue();
        String status = statusComboBox.getValue();

        try {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String sql = "UPDATE users SET username = ?, email = ?, password_hash = ?, nationality = ?, gender = ?, status = ? WHERE user_id = ?";
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, nationality);
            pstmt.setString(5, gender);
            pstmt.setString(6, status);
            pstmt.setInt(7, selectedUser.getUserId());
            pstmt.executeUpdate();

            selectedUser.setUsername(username);
            selectedUser.setEmail(email);
            selectedUser.setPassword(hashedPassword);
            selectedUser.setNationality(nationality);
            selectedUser.setGender(gender);
            selectedUser.setStatus(status);
            tableView.refresh();

            clearInputFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteUser() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("No user selected", "Please select a user to delete.");
            return;
        }

        try {
            String sql = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, selectedUser.getUserId());
            pstmt.executeUpdate();

            users.remove(selectedUser);

            clearInputFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 // not used
    private int getGeneratedUserId() {
        try (Statement stmt = dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return an invalid ID if something goes wrong
    }

    private void clearInputFields() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        nationalityComboBox.setValue(null);
        genderComboBox.setValue(null);
        statusComboBox.setValue("Offline");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
