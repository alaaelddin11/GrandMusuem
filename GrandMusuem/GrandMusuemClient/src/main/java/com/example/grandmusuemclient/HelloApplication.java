package com.example.grandmusuemclient;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.stage.Stage;


public class HelloApplication extends Application {


    @Override
    public void start(Stage primaryStage) {
        // Create HomeScreen instance and pass the primaryStage
        new HomeScreen(primaryStage , false);

        // Show the primary stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
