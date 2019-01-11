package org.kobbigal.sisenselogreader;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kobbigal.sisenselogreader.contollers.DateSelectionController;
import org.kobbigal.sisenselogreader.model.Log;
import org.kobbigal.sisenselogreader.version.VersionRetriever;
import org.kobbigal.sisenselogreader.views.RootLayout;
import org.kobbigal.sisenselogreader.views.count.LogCountContainer;
import org.kobbigal.sisenselogreader.views.menu.AppMenuBar;
import org.kobbigal.sisenselogreader.views.table.DateSelectionContainer;
import org.kobbigal.sisenselogreader.views.table.LogTableContainer;
import org.kobbigal.sisenselogreader.views.filters.FiltersContainer;

import java.nio.file.Paths;

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
        window.setMinWidth(1600);
        window.setMinHeight(600);

        Scene scene = new Scene(RootLayout.getInstance(), 1600, 600);
        scene.getStylesheets().add("style.css");

        window.setScene(scene);
        window.show();
    }

}