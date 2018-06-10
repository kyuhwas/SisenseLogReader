package test.java;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LogUploadProgress {
    public LogUploadProgress() {
    }

//    public static void display(double progress) {
public static void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Reading and filtering logs");

        VBox layout = new VBox(20.0D);

        ProgressBar progressBar = new ProgressBar(0);

        Label label = new Label("Click to cancel");
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction((event) -> {

            // kill thread

            window.close();
        });

        layout.getChildren().addAll(progressBar, label, cancelButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 200.0D, 100.0D);
        window.setScene(scene);
        window.showAndWait();
    }
}
