package tests;

import classes.Log;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test_Filters extends Application{

    private VBox filtersContainer;
    private VBox filterOptionsContainer;
    private VBox componentSearchboxContainer;
    private VBox searchBoxContainer;
    private TableView<Log> logTable;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Date startTime;
    private Date endTime;
    private TextField startTimeTxtFld;
    private TextField endTimeTxtFld;
    private SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private ObservableList<Log> logs = FXCollections.observableArrayList();
    private CheckBox sourceFilterCkBz;
    private CheckBox verbosityFilterCkBx;
    private CheckBox componentFilterChBx;
    private CheckBox detailsSeachFilterChBx;
    private final String IIS_NODE_PATH = "/Users/kobbigal/Downloads/sample_logs/IISNodeLogs/";
    private final String ECS_LOG_PATH = "/Users/kobbigal/Downloads/sample_logs/PrismServerLogs/";
    private final String PRISMWEB_LOGS_PATH = "/Users/kobbigal/Downloads/sample_logs/PrismWebServer/";
    private Thread backgroundThread;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {

        Stage window = primaryStage;

        window.setTitle("Sisense classes.Log Reader");
        int WIDTH = 1400;
        window.setMinWidth(WIDTH);
        int HEIGHT = 600;
        window.setMinHeight(HEIGHT);

        BorderPane rootLayout = new BorderPane();
        rootLayout.setLeft(initializeFilters());
        rootLayout.setCenter(initializeLogTable());
        rootLayout.setTop(initializeDateMenu());

        Scene scene = new Scene(rootLayout, WIDTH, HEIGHT);
        scene.getStylesheets().add("style/style.css");
        window.setScene(scene);
        window.show();

    }

    private VBox initializeFilters(){
        filtersContainer = new VBox(10);
        filtersContainer.setPadding(new Insets(15));
        Label filtersLabel = new Label("Filters");
        filtersLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));

        sourceFilterCkBz = new CheckBox("Source");
        sourceFilterCkBz.setFont(Font.font("Agency FB", 15));
        // TODO use loaded Logs, filter for unique values then create ArrayList with values
        List<String> sourceOptions = new ArrayList<>();
        sourceOptions.add("ECS");
        sourceOptions.add("IISNode");
        sourceOptions.add("PrismWebServer");

        sourceFilterCkBz.setDisable(true);
        sourceFilterCkBz.setOnAction(e -> addFilterOptions(sourceFilterCkBz.isSelected(), sourceOptions, filtersContainer.getChildren().indexOf(e.getSource())));


        List<String> verbosityOptions = new ArrayList<>();
        verbosityOptions.add("INFO");
        verbosityOptions.add("ERROR");
        verbosityOptions.add("WARNING");
        verbosityOptions.add("TRACE");

        verbosityFilterCkBx = new CheckBox("Verbosity");
        verbosityFilterCkBx.setDisable(true);
        verbosityFilterCkBx.setFont(Font.font("Agency FB", 15));
        verbosityFilterCkBx.setOnAction(e -> addFilterOptions(verbosityFilterCkBx.isSelected(), verbosityOptions, filtersContainer.getChildren().indexOf(e.getSource())));

        componentFilterChBx = new CheckBox("Component");
        componentFilterChBx.setDisable(true);
        componentFilterChBx.setFont(Font.font("Agency FB", 15));
        componentFilterChBx.setOnAction(e -> addComponentTextBox(componentFilterChBx.isSelected(), filtersContainer.getChildren().indexOf(e.getSource())));

        detailsSeachFilterChBx = new CheckBox("Details");
        detailsSeachFilterChBx.setDisable(true);
        detailsSeachFilterChBx.setFont(Font.font("Agency FB", 15));
        detailsSeachFilterChBx.setOnAction(e -> addDetailsTextBox(detailsSeachFilterChBx.isSelected(), filtersContainer.getChildren().indexOf(e.getSource())));

        filtersContainer.getChildren().addAll(filtersLabel, sourceFilterCkBz, verbosityFilterCkBx,componentFilterChBx, detailsSeachFilterChBx);

        return filtersContainer;
    }

    private void addFilterOptions(boolean isSelected, List<String> values, int index){

            if (isSelected){
                filterOptionsContainer = new VBox(1);
                filterOptionsContainer.setPadding(new Insets(0, 0, 0, 15));

                for (String option : values) {
                    CheckBox sourceChkBox = new CheckBox(option);
                    filterOptionsContainer.getChildren().add(sourceChkBox);
                }
                filtersContainer.getChildren().add(index+1, filterOptionsContainer);
            }
            else {

                filtersContainer.getChildren().remove(filterOptionsContainer);

            }

    }

    // TODO: 5/27/18 For each value returned from set, create checkbox
    private static Set<String> verbositySet(List<Log> logs){

        List<String> list = new ArrayList<>();

        for (Log l : logs) {
            list.add(l.getVerbosity());
        }

        Set<String> set = new HashSet<>(list);
        System.out.println(set);

        return set;

    }

    private void addComponentTextBox(boolean isSelected, int index){

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

    private VBox initializeLogTable(){
        // Center

        // Create table
        VBox centerLogViewerContainer = new VBox(0);
        centerLogViewerContainer.setPadding(new Insets(0,0,0,30));
        logTable = new TableView<>();

        // Add columns
        TableColumn<Log, String> sourceColumn = new TableColumn<Log, String>("Source");
        TableColumn<Log, Date> timeColumn = new TableColumn<>("Time");
        TableColumn<Log, String> verbosityColumn = new TableColumn<>("Verbosity");
        TableColumn<Log, String> componentColumn = new TableColumn<>("Component");
        TableColumn<Log, String> detailsColumn = new TableColumn<Log, String>("Details");
        sourceColumn.setSortable(false);
        timeColumn.setMinWidth(185);
        verbosityColumn.setSortable(false);
        verbosityColumn.setMinWidth(60);
        componentColumn.setSortable(false);
        detailsColumn.setSortable(false);

        sourceColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("source"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<Log, Date>("time"));
        verbosityColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("verbosity"));
        componentColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("component"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("details"));
        logTable.getColumns().addAll(sourceColumn, timeColumn, verbosityColumn, componentColumn, detailsColumn);
//        logTable.setPadding(new Insets(0,10,0,0));


        centerLogViewerContainer.getChildren().add(logTable);

        return centerLogViewerContainer;
    }

    private GridPane initializeDateMenu(){
        GridPane topMenuContainer = new GridPane();
        topMenuContainer.setHgap(10);
        topMenuContainer.setVgap(2);
        topMenuContainer.setAlignment(Pos.CENTER);
        topMenuContainer.setPadding(new Insets(10));

        // Row 1 - Labels
        Label startTimeLabel = new Label("Start");
        startTimeLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));
        topMenuContainer.add(startTimeLabel, 0, 0);
        Label endTimeLabel = new Label("End");
        endTimeLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));
        topMenuContainer.add(endTimeLabel, 1, 0);

        // Row 2 - DatePickers  and Submit
        startDatePicker = new DatePicker();
        startDatePicker.setMinSize(50, 10);
        startDatePicker.setPromptText("MM/DD/YYYY");
        topMenuContainer.add(startDatePicker, 0,1);
        endDatePicker = new DatePicker();
        endDatePicker.setMinSize(50, 10);
        endDatePicker.setPromptText("MM/DD/YYYY");
        topMenuContainer.add(endDatePicker, 1,1);
        Button setDatesBtn = new Button("Submit");
        setDatesBtn.setOnAction(event ->  handleSubmit());

        startTimeTxtFld = new TextField();
        startTimeTxtFld.setPromptText("HH:mm");
        topMenuContainer.add(startTimeTxtFld, 0, 2);

        endTimeTxtFld = new TextField();
        endTimeTxtFld.setPromptText("HH:mm");
        topMenuContainer.add(endTimeTxtFld, 1, 2 );

        topMenuContainer.add(setDatesBtn, 2,1);

        return topMenuContainer;
    }

    private void handleSubmit(){

        try {

            if  (   startDatePicker.getValue() != null &&
                    startTimeTxtFld.getText() != null &&
                    endDatePicker.getValue() != null &&
                    endTimeTxtFld.getText() != null){

                startTime = sdt.parse(startDatePicker.getValue() + " " + startTimeTxtFld.getText());
                endTime = sdt.parse(endDatePicker.getValue() + " " + endTimeTxtFld.getText());

            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please set both dates", ButtonType.OK);
                alert.showAndWait();
            }

            if (startTime.after(endTime)){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid range", ButtonType.OK);
                startTimeTxtFld.setText("");
                startTime = null;
                endTime = null;
                endTimeTxtFld.setText("");
                alert.showAndWait();
            }
            else {

                // TODO add progressBar
                /*tests.ProgressBarScene.display();*/

                if (logTable.getItems().size() > 0){
                    logTable.getItems().clear();
                    logs.clear();
                }

                backgroundThread = new Thread(() -> {

                    logs.addAll(iisNodeLogs());
                    logs.addAll(prismWebLogs());
                    logs.addAll(ecsLogs());
                    Collections.sort(logs);
                    logTable.getItems().addAll(logs);

                    Platform.runLater(() -> {
                        sourceFilterCkBz.setDisable(false);
                        verbosityFilterCkBx.setDisable(false);
                        componentFilterChBx.setDisable(false);;
                        detailsSeachFilterChBx.setDisable(false);;
                    });

                });

                backgroundThread.setDaemon(true);
                backgroundThread.start();

            }
        }
        catch (NullPointerException ignored){

        }
        catch (ParseException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect time syntax", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private static Log iisNodeLogParse(String log){

        Log l = new Log();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(log);

        l.setSource("IISNode");
        int i = 0;
//        if (!matcher.matches()){
//            l = null;
//            return l;
//        }
        while (matcher.find()){

            switch (i){
                case 0:
                    try {
                        l.setTime(sdf.parse(matcher.group(1)));
                    } catch (ParseException e) {

                    }
                    break;
                case 2:
                    l.setVerbosity(matcher.group(1));
                    break;
                case 3:
                    l.setComponent(matcher.group(1));
                    break;
                case 4:
                    l.setDetails(matcher.group(1));
                    break;
            }
            i++;
        }
        return l;
    }

    private static Log ecsLogParser(String log){

        if (log.trim().startsWith("at") || log.startsWith("]") || log.startsWith("A")){
            return null;
        }

        Log l = new Log();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(log);

        int i = 0;

        while (matcher.find()){
            l.setSource("ECS");
            switch (i){
                case 0:
                    try {
                        l.setTime(sdf.parse(matcher.group(1)));
                    } catch (ParseException ignored) {
                    }
                    break;
                case 3:
                    l.setVerbosity(matcher.group(1));
                    break;
                case 4:
                    l.setComponent(matcher.group(1));
                    break;
                case 5:
                    l.setDetails(matcher.group(1));
                    break;
            }
            i++;
        }
        return l;
    }

    private Log prismWebLogParser(String log){

        if (log.startsWith("Exception") || log.trim().startsWith("at") || log.startsWith("Sisense") || log.startsWith("System")){
            return null;
        }

        Log l = new Log();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(log);

        l.setSource("PrismWeb");
        int i = 0;
        while (matcher.find()){

            switch (i){
                case 0:
                    try {
                        l.setTime(sdf.parse(matcher.group(1)));
                    } catch (ParseException e) {
                        System.out.println(log);
                        return null;
                    }
                    break;
                case 3:
                    l.setVerbosity(matcher.group(1));
                    break;
                case 4:
                    l.setComponent(matcher.group(1));
                    break;
                case 5:
                    l.setDetails(matcher.group(1));
                    break;
            }
            i++;
        }
        return l;
    }

    private List<Log> ecsLogs() {

        List<Log> logs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        File[] fls = new File(Paths.get(ECS_LOG_PATH).normalize().toString()).listFiles();
        List<String> allLogLines = new ArrayList<>();
        List<String> logLines;

        for (File f : fls) {
            if (f.getName().contains("ECS.log")) {

                // Open read stream for each file
                try (Stream<String> stream = Files.lines(Paths.get(f.getAbsolutePath()), StandardCharsets.ISO_8859_1)) {

                    // Filter log lines for empty and without dates
                    logLines = stream.filter(line -> !line.isEmpty())
                            .filter(line -> Character.isDigit(line.charAt(0)))
                            .collect(Collectors.toList());

//                    System.out.println("Number of logs added from " + f.getName() + ": " + logLines.size());
                    allLogLines.addAll(logLines);

                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file " + f.getName() + " for reading", ButtonType.CLOSE);
                    alert.showAndWait();
                }
            }
        }

//        System.out.println("Total number of ECS logs: " + allLogLines.size());

        for (String logStr: allLogLines){

            Log log = ecsLogParser(logStr);

            // Check if log time is in selected range and filter empty detail logs
            try {
                if (log.getTime() != null && !log.getDetails().isEmpty()){
                    if (log.getTime().after(startTime) && log.getTime().before(endTime)) logs.add(log);
                }
            }
            catch (NullPointerException ignored){
            }
        }

        System.out.println("Total number of ECS logs: " + logs.size());
        return logs;
    }

    private List<Log> iisNodeLogs(){

        List<Log> logs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        File[] fls = new File(Paths.get(IIS_NODE_PATH).normalize().toString()).listFiles();
        List<String> allLogLines = new ArrayList<>();
        List<String> currentLogLines;

        if (fls != null) {
            for (File f : fls){

                if(f.getName().contains("txt")){
                    try (Stream<String> stream = Files.lines(Paths.get(f.getAbsolutePath()), StandardCharsets.ISO_8859_1)) {

                        currentLogLines = stream.filter(line -> !line.isEmpty())
                                .filter(line  -> Character.isDigit(line.charAt(0)))
                                .collect(Collectors.toList());

                        allLogLines.addAll(currentLogLines);

                    } catch (IOException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file " + f.getName() + " for reading", ButtonType.CLOSE);
                        alert.showAndWait();
                    }
                }
            }
        }

        for (String logStr: allLogLines){

            Log log = iisNodeLogParse(logStr);

            // Check if log time is in selected range
            try {
                if (log.getTime() != null){
                    if (log.getTime().after(startTime) && log.getTime().before(endTime)) logs.add(log);
                }
            }
            catch (NullPointerException ignored){
            }
        }
        System.out.println("Total number of IISNode logs: " + logs.size());
        return logs;

    }

    private List<Log> prismWebLogs(){

        List<Log> logs = new ArrayList<>();
        File[] fls = new File(Paths.get(PRISMWEB_LOGS_PATH).normalize().toString()).listFiles();
        List<String> allLogLines = new ArrayList<>();
        List<String> logLines;

        if (fls != null) {
            for (File f : fls){
                if (f.getName().contains("PrismWebServer") && !f.getName().contains("Error") && !f.getName().contains("WhiteBox")){

                    try (Stream<String> stream = Files.lines(Paths.get(f.getAbsolutePath()), StandardCharsets.ISO_8859_1)){

                        logLines = stream.filter(line -> !line.isEmpty())
                                .filter(line -> Character.isDigit(line.charAt(0)))
                                .collect(Collectors.toList());

                        //                    System.out.println("Number of logs added from " + f.getName() + ": " + logLines.size());
                        allLogLines.addAll(logLines);

                    } catch (IOException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file " + f.getName() + " for reading", ButtonType.CLOSE);
                        alert.showAndWait();
                    }
                }
            }
        }

        for (String logStr: allLogLines){

            Log log = prismWebLogParser(logStr);

            // Check if log time is in selected range
            try {
                if (log.getTime() != null){
                    if (log.getTime().after(startTime) && log.getTime().before(endTime)) logs.add(log);
                }
            }
            catch (NullPointerException e){
                System.out.println("couldn't read log");
            }
        }

        System.out.println("Total number of PrismWeb logs: " + logs.size());
        return logs;

    }
}
