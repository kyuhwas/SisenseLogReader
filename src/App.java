import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Date;

public class App extends Application {

    private Stage window;
    private Scene scene;
    private GridPane topMenuContainer;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Button setDatesBtn;
    private LocalDate startTime;
    private LocalDate endTime;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        window = primaryStage;
        window.setTitle("Sisense Log Reader");
        window.setMinWidth(500);
        window.setMinHeight(300);

        BorderPane rootLayout = new BorderPane();

        topMenuContainer = new GridPane();
        topMenuContainer.setHgap(10);
        topMenuContainer.setVgap(2);
        topMenuContainer.setAlignment(Pos.CENTER);
        topMenuContainer.setPadding(new Insets(10));

        // Labels
        Label startTimeLabel = new Label("Start");
        topMenuContainer.add(startTimeLabel, 0, 0);
        Label endTimeLabel = new Label("End");
        topMenuContainer.add(endTimeLabel, 1, 0);

        // Buttons
        startDatePicker = new DatePicker();
        startDatePicker.setMinSize(50, 10);
        startDatePicker.setPromptText("MM/DD/YYYY HH:mm:ss");
        topMenuContainer.add(startDatePicker, 0,1);
        endDatePicker = new DatePicker();
        endDatePicker.setMinSize(50, 10);
        endDatePicker.setPromptText("MM/DD/YYYY HH:mm:ss");
        topMenuContainer.add(endDatePicker, 1,1);
        setDatesBtn = new Button("Submit");
        setDatesBtn.setOnAction(event -> {

            try {

                startTime = startDatePicker.getValue();
                endTime = endDatePicker.getValue();

                System.out.println(startTime.toString());
                System.out.println(endTime.toString());



            }
            catch (NullPointerException e){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please set both dates", ButtonType.OK);
                alert.showAndWait();
            }
        });
        topMenuContainer.add(setDatesBtn, 2,1);

        rootLayout.setTop(topMenuContainer);

        scene = new Scene(rootLayout, 500, 400);

        window.setScene(scene);
        window.show();

    }
}
