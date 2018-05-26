package tests;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgressBarScene {

    public static void display(){
        Stage window = new Stage();
        Scene scene;
        VBox layout;

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Loading logs");

        ProgressBar progressBar = new ProgressBar(0);

        Button closeButton = new Button("Cancel");
        closeButton.setOnAction(event -> window.close());

        layout = new VBox(20);
        layout.getChildren().addAll(progressBar, closeButton);
        layout.setAlignment(Pos.CENTER);
        scene = new Scene(layout, 200, 100);

        window.setScene(scene);
        window.showAndWait();

    }
}
