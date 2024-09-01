package com.example.grandmusuemclient;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.Socket;

public class HomeScreen {

    private Scene scene;
    private int currentImageIndex = 0;  // To track the current image
    private Stage primaryStage;
    private boolean isLoggedIn; // Boolean to track login status

    public HomeScreen(Stage primaryStage, boolean isLoggedIn) {
        this.primaryStage = primaryStage;
        this.isLoggedIn = isLoggedIn; // Initialize login status

        // Initialize layout
        VBox layout = new VBox();
        layout.setPadding(new Insets(0));

        // Create a navigation bar
        HBox navBar = new HBox();
        navBar.setStyle("-fx-background-color: #cda34b; -fx-padding: 10px; -fx-alignment: center; -fx-min-height: 60px;");
        navBar.setPadding(new Insets(10));
        navBar.setSpacing(10); // Add spacing between items

        // Logo image
        Image logoImage = new Image(getClass().getResourceAsStream("/Museum Logo.png")); // Ensure logo.png is in your resources
        ImageView logo = new ImageView(logoImage);
        logo.setFitHeight(100); // Adjust size as needed
        logo.setPreserveRatio(true);

        // Buttons
        Button leftButton = new Button(isLoggedIn ? "Chat Room" : "Sign Up");
        Button rightButton = new Button(isLoggedIn ? "Chat Bot" : "Login");

        // Style buttons
        leftButton.getStyleClass().add("gold-button");
        rightButton.getStyleClass().add("gold-button");

        // Layout for buttons in nav bar
        HBox buttonBox = new HBox(10, leftButton, rightButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        // Add elements to navigation bar
        navBar.getChildren().addAll(logo, buttonBox);

        // Create video section
        Media videoMedia = new Media(getClass().getResource("/GrandMus.mp4").toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(videoMedia);
        MediaView mediaView = new MediaView(mediaPlayer);

        // Create a pane for the video
        StackPane videoPane = new StackPane();
        videoPane.setPadding(new Insets(10));
        videoPane.getChildren().add(mediaView);

        // Bind MediaView width to the scene width
        mediaView.fitWidthProperty().bind(primaryStage.widthProperty());
        mediaView.setPreserveRatio(true);

        // Create controls for the video
        HBox controlsBox = new HBox();
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(10));

        Button playPauseButton = new Button("Play");
        Slider timeSlider = new Slider();
        Label timeLabel = new Label("0:00");

        playPauseButton.setOnAction(e -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playPauseButton.setText("Play");
            } else {
                mediaPlayer.play();
                playPauseButton.setText("Pause");
            }
        });

        // Update timeSlider and timeLabel with the current time
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!timeSlider.isValueChanging()) {
                timeSlider.setValue(newTime.toMillis());
            }
            timeLabel.setText(formatTime(newTime, mediaPlayer.getTotalDuration()));
        });

        // Update timeSlider when the user interacts with it
        timeSlider.setOnMouseReleased(e -> {
            mediaPlayer.seek(Duration.millis(timeSlider.getValue()));
        });

        // Update slider max value when media duration changes
        mediaPlayer.totalDurationProperty().addListener((obs, oldDuration, newDuration) -> {
            timeSlider.setMax(newDuration.toMillis());
        });

        // Add controls to the controlsBox
        controlsBox.getChildren().addAll(playPauseButton, timeSlider, timeLabel);

        // Image slider section
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);

        // Bind ImageView size to the scene size
        imageView.fitWidthProperty().bind(primaryStage.widthProperty().multiply(0.8)); // Slightly less than full width
        imageView.fitHeightProperty().bind(primaryStage.heightProperty().subtract(200)); // Adjusting height for other components

        // Load images
        String[] images = {
                "/path_to_image_1.jpg",  // Replace with actual image paths
                "/path_to_image_2.jpg",
                "/path_to_image_3.jpg",
                "/path_to_image_4.jpg"
        };

        // Function to update the image in the ImageView
        imageView.setImage(new Image(getClass().getResourceAsStream(images[currentImageIndex])));

        // Timeline to auto-slide images
        Timeline imageSliderTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), event -> {
                    currentImageIndex = (currentImageIndex + 1) % images.length;
                    imageView.setImage(new Image(getClass().getResourceAsStream(images[currentImageIndex])));
                })
        );
        imageSliderTimeline.setCycleCount(Timeline.INDEFINITE); // Loop indefinitely
        imageSliderTimeline.play();

        // Navigation buttons for image slider
        Button prevButton = new Button("<");
        Button nextButton = new Button(">");

        prevButton.setOnAction(e -> {
            currentImageIndex = (currentImageIndex - 1 + images.length) % images.length;
            imageView.setImage(new Image(getClass().getResourceAsStream(images[currentImageIndex])));
        });

        nextButton.setOnAction(e -> {
            currentImageIndex = (currentImageIndex + 1) % images.length;
            imageView.setImage(new Image(getClass().getResourceAsStream(images[currentImageIndex])));
        });

        // Layout for the image slider and navigation buttons
        HBox sliderBox = new HBox(10, prevButton, imageView, nextButton);
        sliderBox.setAlignment(Pos.CENTER);
        sliderBox.setPadding(new Insets(10));
        sliderBox.setMaxWidth(Double.MAX_VALUE); // Make the slider box stretch

        HBox.setHgrow(imageView, Priority.ALWAYS); // Allow ImageView to grow within HBox
        HBox.setHgrow(prevButton, Priority.NEVER); // Don't let buttons grow
        HBox.setHgrow(nextButton, Priority.NEVER);

        // Create footer section (always visible)
        HBox footer = new HBox();
        footer.setStyle("-fx-background-color: #cda34b; -fx-padding: 10px; -fx-alignment: center; -fx-min-height: 60px;");
        footer.setPadding(new Insets(10));
        footer.setSpacing(10); // Add spacing between items

        // Footer image
        Image footerImage = new Image(getClass().getResourceAsStream("/img.jfif")); // Ensure footer image is in your resources
        ImageView footerImageView = new ImageView(footerImage);
        footerImageView.setFitHeight(50); // Adjust size as needed
        footerImageView.setPreserveRatio(true);

        // Footer buttons
        Button footerLeftButton = new Button(isLoggedIn ? "Chat Room" : "Sign Up");
        Button footerRightButton = new Button(isLoggedIn ? "Chat Bot" : "Login");

        // Style footer buttons
        footerLeftButton.getStyleClass().add("gold-button");
        footerRightButton.getStyleClass().add("gold-button");

        // Layout for footer buttons
        HBox footerButtonBox = new HBox(10, footerLeftButton, footerRightButton);
        footerButtonBox.setAlignment(Pos.CENTER_RIGHT);

        // Add elements to footer
        footer.getChildren().addAll(footerImageView, footerButtonBox);

        // Add navigation bar, video, controls, image slider, and footer to layout
        layout.getChildren().addAll(navBar, videoPane, controlsBox, sliderBox, footer);

        // Wrap the layout in a ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);
        scrollPane.setFitToWidth(true); // Makes sure the content width fits the viewport
        scrollPane.setFitToHeight(true); // Makes sure the content height fits the viewport
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Disable horizontal scrollbar

        // Create scene and set it to the primary stage
        scene = new Scene(scrollPane, 900, 700); // Initialize with scrollPane for scrolling
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Grand Egyptian Museum");

        // Set up button actions
        leftButton.setOnAction(e -> {
            if (isLoggedIn) {
                navigateToChatRoomScreen();
            } else {
                navigateToSignUpScreen();
            }
        });

        rightButton.setOnAction(e -> {
            if (isLoggedIn) {
                navigateToChatBotScreen();
            } else {
                navigateToLoginScreen();
            }
        });

        // Footer button actions (same logic as nav buttons)
        footerLeftButton.setOnAction(leftButton.getOnAction());
        footerRightButton.setOnAction(rightButton.getOnAction());
    }

    public Scene getScene() {
        return scene;
    }

    private String formatTime(Duration elapsed, Duration total) {
        int totalSecs = (int) Math.floor(total.toSeconds());
        int elapsedSecs = (int) Math.floor(elapsed.toSeconds());
        int minutes = elapsedSecs / 60;
        int seconds = elapsedSecs % 60;
        int totalMinutes = totalSecs / 60;
        int totalSeconds = totalSecs % 60;
        return String.format("%02d:%02d / %02d:%02d", minutes, seconds, totalMinutes, totalSeconds);
    }

    private void navigateToSignUpScreen() {
        SignUpScreen signUpScreen = new SignUpScreen(primaryStage);
        primaryStage.setScene(signUpScreen.getScene());
    }

    private void navigateToLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(primaryStage);
        primaryStage.setScene(loginScreen.getScene());
    }

    private void navigateToChatRoomScreen() {
        Socket clientSocket = null;
        try {
            clientSocket = new Socket("localhost", 12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ChattingRoom chattingRoom = new ChattingRoom(primaryStage);
        primaryStage.setScene(chattingRoom.getScene());
        System.out.println("Navigating to Chat Room screen...");
    }

    private void navigateToChatBotScreen() {
        ChatbotScreen chatbotScreen = new ChatbotScreen(primaryStage);
        primaryStage.setScene(chatbotScreen.getScene());
        System.out.println("Navigating to Chat Bot screen...");
    }
}
