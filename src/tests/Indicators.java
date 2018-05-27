package tests;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Random;

public class Indicators extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Pane root = new HBox();
        stage.setScene(new Scene(root, 300, 100));

        for (int i = 0; i < 10; i++) {
            final ProgressIndicator pi = new ProgressIndicator(0);
            root.getChildren().add(pi);

            // separate non-FX thread
            // runnable for that thread
            new Thread(() -> {
                for (int i1 = 0; i1 < 20; i1++) {
                    try {
                        // imitating work
                        Thread.sleep(new Random().nextInt(1000));
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    final double progress = i1 *0.05;
                    // update ProgressIndicator on FX thread
                    Platform.runLater(() -> pi.setProgress(progress));
                }
            }).start();
        }

        stage.show();

    }
}
