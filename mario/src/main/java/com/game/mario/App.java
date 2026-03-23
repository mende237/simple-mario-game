package com.game.mario;

import javafx.application.Application;
import javafx.scene.layout.StackPane;
import com.game.mario.game.Clavier;
import com.game.mario.game.FirstStage;
// import javafx.scene.Scene;
import com.game.mario.game.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Scene scene;

    @Override
    public void start(Stage stage) {
        // Create a custom Scene (subclass of Canvas)
        scene = new FirstStage(false);

        Clavier clavier = new Clavier(scene);

        // Add the canvas to a StackPane
        StackPane root = new StackPane();
        root.getChildren().add(scene);

        scene.widthProperty().bind(root.widthProperty());
        scene.heightProperty().bind(root.heightProperty());

        // Create a JavaFX Scene with the root pane
        javafx.scene.Scene fxScene = new javafx.scene.Scene(root, 800, 350);

        // Register key event handlers
        fxScene.setOnKeyPressed(clavier::handleKeyPressed);
        fxScene.setOnKeyReleased(clavier::handleKeyReleased);

        // Set the scene and show the stage
        stage.setScene(fxScene);
        stage.setTitle("Mario Game");
        stage.setResizable(false);

        stage.setOnCloseRequest(event -> {
            // Optionally, you can add a confirmation dialog here
            System.exit(0); // Stop the program
        });

        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}