package tests;

import classes.Log;
import classes.LogTest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
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

public class Test_Filters extends Application {

    // UI Components
    private VBox filtersContainer;
    private VBox filterOptionsContainer;
    private VBox componentSearchboxContainer;
    private VBox searchBoxContainer;
    private TableView<LogTest> logTable;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private TextField startTimeTxtFld;
    private TextField endTimeTxtFld;
    private CheckBox sourceFilterCkBz;
    private CheckBox verbosityFilterCkBx;
    private CheckBox componentFilterChBx;
    private CheckBox detailsSeachFilterChBx;
    private Button setDatesBtn;

    // Java classes
    private SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Date startTime;
    private Date endTime;

    private final static String[] sources = new String[]{"ECS","IISNode","PrismWebServer"};

    // State
    private ObservableList<LogTest> logs = FXCollections.observableArrayList();
    private Set<String> verbosityOptions;

    // TODO menu item to configure log paths
    // WINDOWS
    private final String IIS_NODE_PATH = "C:\\Program Files\\Sisense\\PrismWeb\\vnext\\iisnode\\";
    private final String PRISMWEB_LOGS_PATH = "C:\\ProgramData\\Sisense\\PrismWeb\\Logs\\";
    private final String ECS_LOG_PATH = "C:\\ProgramData\\Sisense\\PrismServer\\PrismServerLogs\\";


    // MAC
//    private final String IIS_NODE_PATH = "/Users/kobbigal/Downloads/sample_logs/IISNodeLogs/";
//    private final String ECS_LOG_PATH = "/Users/kobbigal/Downloads/sample_logs/PrismServerLogs/";
//    private final String PRISMWEB_LOGS_PATH = "/Users/kobbigal/Downloads/sample_logs/PrismWebServer/";
    private final String IMAGE_URL = "file:" + String.valueOf(Paths.get(System.getProperty("user.dir"),"res","logo.png"));

    public static void main(String[] args) {
        launch(args);
    }

    //   UI
    @Override
    public void start(Stage primaryStage) {
        loadUI(primaryStage);
    }

    private void loadUI(Stage primaryStage){

        Stage window = primaryStage;
        window.getIcons().add(new Image(IMAGE_URL));
        window.setTitle("Sisense Log Reader");
        int WINDOW_WIDTH = 1400;
        window.setMinWidth(WINDOW_WIDTH);
        int WINDOW_HEIGHT = 600;
        window.setMinHeight(WINDOW_HEIGHT);

        BorderPane rootLayout = new BorderPane();

        // UI binding
        rootLayout.setTop(initializeDateMenu());
        rootLayout.setCenter(initializeLogTable());
        rootLayout.setLeft(initializeFilters());

        Scene scene = new Scene(rootLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add("style/style.css");
        window.setScene(scene);
        window.show();
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
        setDatesBtn = new Button("Submit");
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

        sourceColumn.setCellValueFactory(new PropertyValueFactory<LogTest, String>("source"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<LogTest, Date>("time"));
        verbosityColumn.setCellValueFactory(new PropertyValueFactory<LogTest, String>("verbosity"));
        componentColumn.setCellValueFactory(new PropertyValueFactory<LogTest, String>("component"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<LogTest, String>("details"));
        logTable.getColumns().addAll(sourceColumn, timeColumn, verbosityColumn, componentColumn, detailsColumn);

        centerLogViewerContainer.getChildren().add(logTable);

        return centerLogViewerContainer;
    }

    private VBox initializeFilters(){
        filtersContainer = new VBox(10);
        filtersContainer.setPadding(new Insets(15));
        Label filtersLabel = new Label("Filters");
        filtersLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));

        sourceFilterCkBz = new CheckBox("Source");
        sourceFilterCkBz.setFont(Font.font("Agency FB", 15));
        // TODO use loaded Logs, filter for unique values then create ArrayList with values
        Set<String> sourceOptions = new HashSet<>(Arrays.asList(sources));

        sourceFilterCkBz.setDisable(true);
        sourceFilterCkBz.setOnAction(e -> addFilterOptions(sourceFilterCkBz.isSelected(), sourceOptions, filtersContainer.getChildren().indexOf(e.getSource())));

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

    // Log loaders
    private List<LogTest> ecsLogs() {

        List<LogTest> logs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        File[] fls = new File(Paths.get(ECS_LOG_PATH).normalize().toString()).listFiles();
        List<String> allLogLines = new ArrayList<>();
        List<String> logLines;

        if (fls != null) {
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
        }

//        System.out.println("Total number of ECS logs: " + allLogLines.size());

        for (String logStr: allLogLines){

            LogTest log = ecsLogParser(logStr);

            // Check if log time is in selected range and filter empty detail logs
            try {
                if (log != null && log.getTime() != null && !log.getDetails().isEmpty()) {
                    if (log.getTime().after(startTime) && log.getTime().before(endTime)) logs.add(log);
                }
            }
            catch (NullPointerException ignored){
            }
        }

        System.out.println("Total number of ECS logs: " + logs.size());
        return logs;
    }

    private List<LogTest> iisNodeLogs(){

        List<LogTest> logs = new ArrayList<>();
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

            LogTest log = iisNodeLogParse(logStr);

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

    private List<LogTest> prismWebLogs(){

        List<LogTest> logs = new ArrayList<>();
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

            LogTest log = prismWebLogParser(logStr);

            // Check if log time is in selected range
            try {
                if (log != null && log.getTime() != null) {
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

    // Log Parsers
    private static LogTest iisNodeLogParse(String log){

        LogTest l = new LogTest();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(log);

        l.setSource(sources[1]);
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
                    } catch (ParseException ignored) {

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

    private static LogTest ecsLogParser(String log){

        LogTest l = new LogTest();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(log);

        int i = 0;

        while (matcher.find()){
            l.setSource(sources[0]);
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

    private static LogTest prismWebLogParser(String log){

        LogTest l = new LogTest();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(log);

        l.setSource(sources[2]);
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

    // Event handlers
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

                setDatesBtn.setDisable(true);

                if (logs.size() > 0){
                    logs.clear();
                }

                Thread backgroundThread = new Thread(() -> {

                    logs.addAll(iisNodeLogs());
                    logs.addAll(prismWebLogs());
                    logs.addAll(ecsLogs());

                    if (logs.size() > 0){

                        Collections.sort(logs);

                        verbosityOptions = verbositySet(logs);

                        Platform.runLater(() -> {
                            sourceFilterCkBz.setDisable(false);
                            verbosityFilterCkBx.setDisable(false);
                            componentFilterChBx.setDisable(false);
                            detailsSeachFilterChBx.setDisable(false);
                            setDatesBtn.setDisable(false);
                        });
                    }

                    else {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No logs were found for the selected dates", ButtonType.OK);
                            alert.showAndWait();
                        });
                    }
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

    private void addFilterOptions(boolean isSelected, Set<String> values, int index){

        if (isSelected){
            filterOptionsContainer = new VBox(1);
            filterOptionsContainer.setPadding(new Insets(0, 0, 0, 15));

            for (String option : values) {
                CheckBox checkBox = new CheckBox(option);
                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {

                    if (newValue){
//                        logTable.remo
                        logs.filtered(log -> !log.getSource().equals(checkBox.getText()));

                    }

                });
                filterOptionsContainer.getChildren().add(checkBox);
            }
            filtersContainer.getChildren().add(index+1, filterOptionsContainer);
        }
        else {

            filtersContainer.getChildren().remove(filterOptionsContainer);

        }

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

    // Helper methods
    private static Set<String> verbositySet(List<LogTest> logs){

        List<String> list = new ArrayList<>();

        for (LogTest l : logs) {
            list.add(l.getVerbosity());
        }

        Set<String> set = new HashSet<>(list);
        return set;

    }

}
