package com.example.grandmusuemclient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatbotScreen {

    private VBox layout;
    private ListView<String> questionsList;
    private TextArea answerArea;
    private Stage primaryStage;

    public ChatbotScreen(Stage primaryStage) {
        this.primaryStage = primaryStage; // Save reference to the primary stage

        // Initialize the main layout container
        layout = new VBox(20);
        layout.setPadding(new Insets(15, 20, 15, 20));
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("chatbot-layout"); // Add CSS class to layout

        // Label for the question section
        Label selectQuestionLabel = new Label("Select a question:");
        selectQuestionLabel.getStyleClass().add("chatbot-label"); // Add CSS class to label

        // List view to display questions
        questionsList = new ListView<>();
        questionsList.getItems().addAll(
                "What are your opening hours?",
                "Where is the museum located?",
                "What exhibits are currently on display?",
                "How much does an entry ticket cost for adults and children?",
                "Is the museum open for visiting now?",
                "Regarding the entry of veterans?",
                "What is the price of show ticket?",
                "Is there a phone number to contact?"
        );
        questionsList.getStyleClass().add("chatbot-questions-list"); // Add CSS class to list view

        // Text area to display answers
        answerArea = new TextArea();
        answerArea.setPromptText("The answer will appear here...");
        answerArea.setWrapText(true);
        answerArea.setEditable(false);
        answerArea.getStyleClass().add("chatbot-answer-area"); // Add CSS class to text area

        // Button to get the answer
        Button getAnswerButton = new Button("Get Answer");
        getAnswerButton.getStyleClass().add("gold-button"); // Add CSS class to button
        getAnswerButton.setOnAction(e -> displayAnswer());

        // Back button to return to the home screen
        Button backButton = new Button("Back to Home");
        backButton.getStyleClass().add("gold-button"); // Add CSS class to button
        backButton.setOnAction(e -> navigateToHomeScreen());

        // HBox to hold the buttons
        HBox buttonBox = new HBox(10, getAnswerButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        HBox.setHgrow(getAnswerButton, Priority.ALWAYS);
        HBox.setHgrow(backButton, Priority.ALWAYS);

        // Add components to the layout
        layout.getChildren().addAll(selectQuestionLabel, questionsList, buttonBox, answerArea);

        // Create the scene with the layout and set the CSS file
        Scene scene = new Scene(layout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Set the scene on the primary stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Grand Museum - Chatbot");
        primaryStage.show();
    }

    private void displayAnswer() {
        String selectedQuestion = questionsList.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            switch (selectedQuestion) {
                case "What are your opening hours?":
                    answerArea.setText("We are open from 9 AM to 6 PM, Monday to Thursday, and from 9 AM to 10 PM on Fridays and Saturdays.");
                    break;
                case "Where is the museum located?":
                    answerArea.setText("The museum is located at X4VF+V38, Cairo - Alexandria Desert Rd, Kafr Nassar, Al Haram, Giza Governorate 3513204.");
                    break;
                case "What exhibits are currently on display?":
                    answerArea.setText("We have several exhibits on Ancient Egypt and modern art.");
                    break;
                case "How much does an entry ticket cost for adults and children?":
                    answerArea.setText("30 pounds for Egyptians from all external gates of the New Egyptian Museum and free for all children under the age of six.");
                    break;
                case "Is the museum open for visiting now?":
                    answerArea.setText("Partially open. Advice, go after opening because there is no fun at the moment.");
                    break;
                case "Regarding the entry of veterans?":
                    answerArea.setText("The price of ticket for veterans is 75 EGP.");
                    break;
                case "What is the price of show ticket?":
                    answerArea.setText("The price is 300 EGP.");
                    break;
                case "Is there a phone number to contact?":
                    answerArea.setText("There are no numbers. Go to the museum’s website via this link: https://visit-gem.com/ar/home.");
                    break;
                default:
                    answerArea.setText("Please select a valid question.");
                    break;
            }
        } else {
            answerArea.setText("Please select a question first.");
        }
    }


    private void navigateToHomeScreen() {
        // Logic to navigate back to the home screen
        HomeScreen homeScreen = new HomeScreen(primaryStage, true);
        primaryStage.setScene(homeScreen.getScene());
    }

    public Scene getScene() {
        return new Scene(layout, 900, 700); // Return a scene for use in setting the primary stage
    }
}
