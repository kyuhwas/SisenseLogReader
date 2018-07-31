import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kobbigal.sisenselogreader.classes.Log;

import java.util.Date;
import java.util.function.Predicate;

public class Playground extends Application {

    TableView<Log> logTable;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        BorderPane rootLayout = new BorderPane();

        rootLayout.setCenter(initializeLogTable());
        rootLayout.setBottom(filtersCheckBoxes());
//        ProgressBar progressBar = new ProgressBar(0.0);

        Scene scene = new Scene(rootLayout, 1000, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private VBox initializeLogTable() {
        // Center

        // Create table
        VBox centerLogViewerContainer = new VBox(0);
        logTable = new TableView<>();

        logTable.setItems(UniqueValuesForFilter.createRandomLogs(100));

        // Add columns
        TableColumn<Log, String> sourceColumn = new TableColumn<>("Source");
        TableColumn<Log, Date> timeColumn = new TableColumn<>("Time");
        TableColumn<Log, String> verbosityColumn = new TableColumn<>("Verbosity");
        TableColumn<Log, String> componentColumn = new TableColumn<>("Component");
        TableColumn<Log, String> detailsColumn = new TableColumn<>("Details");
        sourceColumn.setSortable(false);
        timeColumn.setMinWidth(190);
        verbosityColumn.setSortable(false);
        verbosityColumn.setMinWidth(60);
        componentColumn.setSortable(false);
        componentColumn.setMinWidth(200);
        detailsColumn.setSortable(false);
        detailsColumn.setMinWidth(400);

        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        verbosityColumn.setCellValueFactory(new PropertyValueFactory<>("verbosity"));
        componentColumn.setCellValueFactory(new PropertyValueFactory<>("component"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        logTable.getColumns().addAll(sourceColumn, timeColumn, verbosityColumn, componentColumn, detailsColumn);

        centerLogViewerContainer.getChildren().add(logTable);

        return centerLogViewerContainer;
    }

    private HBox filtersCheckBoxes() {

        HBox container = new HBox(10);

        ObservableList<Log> originalList = logTable.getItems();
        FilteredList<Log> filteredList = new FilteredList<>(originalList);

        CheckBox sources = new CheckBox("ECS");
        sources.setSelected(true);
        sources.selectedProperty().addListener((observable, oldValue, newValue) -> {

            // if unchecked
            if (!newValue) {

                Predicate<Log> predicate = log -> !log.getSource().contains("ECS");

                filteredList.setPredicate(predicate);

                logTable.setItems(filteredList);

            } else {

                filteredList.setPredicate(null);

            }


        });

        CheckBox verbosity = new CheckBox("INFO");
        verbosity.setSelected(true);
        verbosity.selectedProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue) {

                logTable.setItems(logTable.getItems().filtered(log -> !log.getVerbosity().contains("INFO")));

            } else {



            }

        });

        TextField component = new TextField();
        component.setMinWidth(25);
        component.setPromptText("Search for component");
        component.setOnAction(event -> {

            if (component.getText().isEmpty()) {

                logTable.setItems(originalList);

            } else {
                logTable.setItems(logTable.getItems().filtered(log -> log.getComponent().contains(component.getText())));
            }
        });

        TextField details = new TextField();
        details.setMinWidth(25);
        details.setPromptText("Search for log details");
        details.setOnAction(event -> {
            if (details.getText().isEmpty()){
                logTable.setItems(originalList);
            }
            else {
                logTable.setItems(logTable.getItems().filtered(log -> log.getDetails().contains(details.getText())));
            }
        });

        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(sources, verbosity, component, details);

        return container;

    }
}
