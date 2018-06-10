import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.kobbigal.sisenselogreader.classes.Log;

import java.util.*;

public class TestFilterState extends Application{

    private final static String[] sources = new String[]{"ECS","IISNode","PrismWebServer"};
    private ObservableList<Log> logs = UniqueValuesForFilter.createRandomLogs(100);
    private VBox verbosityOptionsContainer;
    VBox filtersContainer = new VBox(10);
    private VBox componentSearchboxContainer;
    private TableView<Log> logTable;
    private VBox searchBoxContainer;
    private VBox SourceOptionsContainer;
    private Set<String> verbosityOptions;
    private List<CheckBox> filterCheckBoxList = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane rootLayout = new BorderPane();
        rootLayout.setLeft(initializeFilters());
        rootLayout.setCenter(initializeLogTable());

        Scene scene = new Scene(rootLayout, 800,400);

        primaryStage.setTitle(this.getClass().getName());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox initializeFilters(){

        filtersContainer.setPadding(new Insets(15));
        Label filtersLabel = new Label("Filters");
        filtersLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));

        CheckBox sourceFilterCkBz = new CheckBox("Source");
        sourceFilterCkBz.setSelected(true);
        sourceFilterCkBz.setFont(Font.font("Agency FB", 15));
        // TODO use loaded Logs, filter for unique values then create ArrayList with values
        Set<String> sourceOptions = new HashSet<>(Arrays.asList(sources));

        sourceFilterCkBz.selectedProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue){
                addSourceOptions(sourceOptions, filtersContainer.getChildren().indexOf(sourceFilterCkBz));
            }
            else {
                filtersContainer.getChildren().remove(SourceOptionsContainer);

            }

        });

        verbosityOptions = verbositySet(logs);

        CheckBox verbosityFilterCkBx = new CheckBox("Verbosity");
        verbosityFilterCkBx.setSelected(true);
        verbosityFilterCkBx.setFont(Font.font("Agency FB", 15));
        verbosityFilterCkBx.selectedProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue){
                addVerbosityOptions(verbosityOptions, filtersContainer.getChildren().indexOf(verbosityFilterCkBx));
            }
            else {
                filtersContainer.getChildren().remove(verbosityOptionsContainer);
            }

        });

        CheckBox componentFilterChBx = new CheckBox("Component");
        componentFilterChBx.setFont(Font.font("Agency FB", 15));
        componentFilterChBx.setOnAction(e -> addComponentTextBox(componentFilterChBx.isSelected(), filtersContainer.getChildren().indexOf(e.getSource())));

        CheckBox detailsSeachFilterChBx = new CheckBox("Details");
        detailsSeachFilterChBx.setFont(Font.font("Agency FB", 15));
        detailsSeachFilterChBx.setOnAction(e -> addDetailsTextBox(detailsSeachFilterChBx.isSelected(), filtersContainer.getChildren().indexOf(e.getSource())));

        filtersContainer.getChildren().addAll(filtersLabel, sourceFilterCkBz, verbosityFilterCkBx,componentFilterChBx, detailsSeachFilterChBx);

        return filtersContainer;
    }

    private void addComponentTextBox(boolean isSelected, int index){

        componentSearchboxContainer = new VBox();
        if (isSelected){

            componentSearchboxContainer = new VBox(5);
            componentSearchboxContainer.setPadding(new Insets(0,0,0,15));

            TextField searchField = new TextField();
            searchField.setPromptText("e.g. Application.ElastiCubeManager");

            Button submit = new Button("Search");
            submit.setOnAction(event -> {
                if (!searchField.getText().isEmpty()){
                    System.out.println("Searched for component " + searchField.getText());
                }
                else {
                    Alert alert  = new Alert(Alert.AlertType.WARNING, "Please enter text to search for", ButtonType.OK);
                    alert.showAndWait();
                }
            });

            componentSearchboxContainer.getChildren().addAll(searchField, submit);
            filtersContainer.getChildren().add(index + 1, componentSearchboxContainer);
        }

        else {
            filtersContainer.getChildren().remove(componentSearchboxContainer);
        }

    }

    private void addDetailsTextBox(boolean isSelected, int index){

        if (isSelected){

            searchBoxContainer = new VBox(5);
            searchBoxContainer.setPadding(new Insets(0,0,0,15));

            TextField searchField = new TextField();
            searchField.setPromptText("e.g. finished initializing");

            Button submit = new Button("Search");
            submit.setOnAction(e -> {
                if (!searchField.getText().isEmpty()){
                    System.out.println("Searched for " + searchField.getText());
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter text to search for", ButtonType.OK);
                    alert.showAndWait();
                }
            });

            searchBoxContainer.getChildren().addAll(searchField, submit);
            filtersContainer.getChildren().add(index + 1, searchBoxContainer);
        }

        else {
            filtersContainer.getChildren().remove(searchBoxContainer);
        }
    }

    private void addSourceOptions(Set<String> values, int index){

        SourceOptionsContainer = new VBox(1);
        SourceOptionsContainer.setPadding(new Insets(0, 0, 0, 15));

        for (String option : values) {
            CheckBox checkBox = new CheckBox(option);
            checkBox.setSelected(true);
            checkBox.selectedProperty().addListener((observable, oldState, newState) -> {
                System.out.println("checkbox value: " + checkBox.getText() + "old state: " + oldState + "\tnew state: " + newState);
            });

            filterCheckBoxList.add(checkBox);
            SourceOptionsContainer.getChildren().add(checkBox);
        }
        filtersContainer.getChildren().add(index+1, SourceOptionsContainer);
    }

    private void addVerbosityOptions(Set<String> values, int index){

        verbosityOptionsContainer = new VBox(1);
        verbosityOptionsContainer.setPadding(new Insets(0, 0, 0, 15));

        for (String option : values) {
            CheckBox checkBox = new CheckBox(option);
            checkBox.setSelected(true);
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {

                System.out.println();

            });

            filterCheckBoxList.add(checkBox);
            verbosityOptionsContainer.getChildren().add(checkBox);
        }
        filtersContainer.getChildren().add(index + 1, verbosityOptionsContainer);

    }

    private static Set<String> verbositySet(List<Log> logs){

        List<String> list = new ArrayList<>();

        for (Log l : logs) {
            list.add(l.getVerbosity());
        }

        return new HashSet<>(list);

    }

    private VBox initializeLogTable(){
        // Center

        // Create table
        VBox centerLogViewerContainer = new VBox(0);
        logTable = new TableView();
        logTable.setItems(logs);

        // Add columns
        TableColumn sourceColumn = new TableColumn("Source");
        TableColumn timeColumn = new TableColumn("Time");
        TableColumn verbosityColumn = new TableColumn("Verbosity");
        TableColumn componentColumn = new TableColumn("Component");
        TableColumn detailsColumn = new TableColumn("Details");
        sourceColumn.setSortable(false);
        timeColumn.setMinWidth(190);
        verbosityColumn.setSortable(false);
        verbosityColumn.setMinWidth(60);
        componentColumn.setSortable(false);
        componentColumn.setMinWidth(200);
        detailsColumn.setSortable(false);
        detailsColumn.setMinWidth(400);

        sourceColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("source"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<Log, Date>("time"));
        verbosityColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("verbosity"));
        componentColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("component"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("details"));
        logTable.getColumns().addAll(sourceColumn, timeColumn, verbosityColumn, componentColumn, detailsColumn);

        centerLogViewerContainer.getChildren().add(logTable);

        return centerLogViewerContainer;
    }

}
