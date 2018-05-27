package tests;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UpdateProgessTest extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(setRoot(), 500, 100));
        primaryStage.show();
    }

    private Parent setRoot() {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        HBox buttonsContainer = new HBox(10);
        buttonsContainer.setAlignment(Pos.CENTER);

        Button cancelButton = new Button("Cancel");
        cancelButton.setDisable(true);
        Button startButton = new Button("Start");
        buttonsContainer.getChildren().addAll(startButton, cancelButton);

        ProgressBar progressBar = new ProgressBar(0);

        Task task = new Task<Void>() {

            @Override
            protected Void call() {

                int max = 1000000000;
                for (int i = 0; i < max; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    updateProgress(i, max);
                }
                return null;
            }
        };

        cancelButton.setOnAction(event -> {
            task.cancel();
            progressBar.progressProperty().unbind();
            progressBar.progressProperty().setValue(0);
        });

        startButton.setOnAction(event -> {
            cancelButton.setDisable(false);
            progressBar.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
        });

        progressBar.setPrefWidth(400);

        root.getChildren().addAll(progressBar, buttonsContainer);

        return root;
    }


}
