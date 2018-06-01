package tests;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertProgressBar{

public static void display(){
        Stage window = new Stage();
        Scene scene;
        VBox layout;

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Working....");

        Label label = new Label("Reading and parsing");
        ProgressBar progressBar = new ProgressBar();
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(event -> window.close());

        layout = new VBox(20);
        layout.getChildren().addAll(label, progressBar, cancelBtn);
        layout.setAlignment(Pos.CENTER);
        scene = new Scene(layout, 200, 100);

        window.setScene(scene);
        window.showAndWait();

        }

}
