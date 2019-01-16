package org.kobbigal.sisenselogreader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.kobbigal.sisenselogreader.version.VersionRetriever;
import org.kobbigal.sisenselogreader.views.RootLayout;

import java.nio.file.Paths;

// TODO
// Add live file watcher + search

public class App extends Application {

    //    private List<Path> logPaths;

    private final String IMAGE_URL = "file:" + Paths.get(System.getProperty("user.dir"), "res", "logo.png");

    public static void main(String[] args) {
        launch(args);
    }

    //   UI
    @Override
    public void start(Stage primaryStage) {
        loadUI(primaryStage);
    }

    private void loadUI(Stage window){

        window.getIcons().add(new Image(IMAGE_URL));
        window.setTitle("Sisense Log Reader - " + VersionRetriever.getVersion());
//        window.setTitle("Sisense Log Reader");
        window.setMinWidth(1600);
        window.setMinHeight(600);

        Scene scene = new Scene(RootLayout.getInstance(), 1600, 600);
        scene.getStylesheets().add("style.css");

        window.setScene(scene);
        window.show();
    }

}