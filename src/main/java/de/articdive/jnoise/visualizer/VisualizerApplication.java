package de.articdive.jnoise.visualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class VisualizerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(VisualizerApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280 + 10,  640 + 25);
        stage.setTitle("JNoise Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    static void launchApplication(String[] args) {
        launch(args);
    }
}