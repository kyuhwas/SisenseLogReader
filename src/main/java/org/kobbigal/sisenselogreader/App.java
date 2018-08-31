package org.kobbigal.sisenselogreader;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import org.kobbigal.sisenselogreader.model.Log;
import org.kobbigal.sisenselogreader.views.AppMenuBar;
import org.kobbigal.sisenselogreader.views.LogCountContainer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO add logic to filtering source and verbosity listview

public class App extends Application {

    private static DatePicker startDatePicker;
    private static DatePicker endDatePicker;
    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate = LocalDate.now();
    private static Date startTime;
    private static Date endTime;
    private static TextField startTimeTxtFld;
    private static TextField endTimeTxtFld;
    private static TableView<Log> logTable;
    private static SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static ObservableList<Log> logs = FXCollections.observableArrayList();
    private static FilteredList<Log> logFilteredList = new FilteredList<>(logs);
    private static Button setDatesBtn;
    private static Label numLogsLoaded;
    private static BorderPane rootLayout;

    private static ObservableList<String> verbosityObsList = FXCollections.observableArrayList();
    private static ObservableList<String> sourcesObsList = FXCollections.observableArrayList();
    private static ListView<String> verbosityListView;
    private static ListView<String> sourcesListView;

//    private ObjectProperty<Predicate<Log>> componentSearchFilter;
//    private ObjectProperty<Predicate<Log>> detailsSearchFilter;

    private final static String[] sources = new String[]{"ECS","IISNode","PrismWebServer"};

    // TODO menu item to configure log paths
    // WINDOWS
    private final static String IIS_NODE_PATH = "C:\\Program Files\\Sisense\\PrismWeb\\vnext\\iisnode\\";
    private final static String PRISMWEB_LOGS_PATH = "C:\\ProgramData\\Sisense\\PrismWeb\\Logs\\";
    private final static String ECS_LOG_PATH = "C:\\ProgramData\\Sisense\\PrismServer\\PrismServerLogs\\";

    private final String IMAGE_URL = "file:" + String.valueOf(Paths.get(System.getProperty("user.dir"),"res","logo.png"));

    public static void main(String[] args) {
        launch(args);
    }

    //   UI
    @Override
    public void start(Stage primaryStage) {

        loadUI(primaryStage);
    }

    private void loadUI(Stage window){

        window.getIcons().add(new Image(IMAGE_URL));
        window.setTitle("Sisense Log Reader");
        int WINDOW_WIDTH = 1600;
        window.setMinWidth(WINDOW_WIDTH);
        int WINDOW_HEIGHT = 600;
        window.setMinHeight(WINDOW_HEIGHT);

        rootLayout = new BorderPane();

        // UI binding
        rootLayout.setTop(new AppMenuBar());
        rootLayout.setCenter(centerLayoutDateSelectionAndTable(initializeDateMenu(), initializeLogTable()));
//        rootLayout.setCenter(centerLayoutDateSelectionAndTable(new DateMenu(startDatePicker, endDatePicker, startTimeTxtFld, endTimeTxtFld, this), initializeLogTable()));
        rootLayout.setLeft(getFiltersContainer());

        Scene scene = new Scene(rootLayout, WINDOW_WIDTH, WINDOW_HEIGHT);

        scene.getStylesheets().add("style.css");
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
        startDatePicker.setValue(startDate);

        topMenuContainer.add(startDatePicker, 0,1);
        endDatePicker = new DatePicker();
        endDatePicker.setMinSize(50, 10);
        endDatePicker.setPromptText("MM/DD/YYYY");
        endDatePicker.setValue(endDate);

        topMenuContainer.add(endDatePicker, 1,1);
        setDatesBtn = new Button("Submit");
        setDatesBtn.setOnAction(event ->  handleSubmit());

        startTimeTxtFld = new TextField();
        startTimeTxtFld.setPromptText("HH:mm");
        startTimeTxtFld.setText("11:50");
        topMenuContainer.add(startTimeTxtFld, 0, 2);

        endTimeTxtFld = new TextField();
        endTimeTxtFld.setPromptText("HH:mm");
        endTimeTxtFld.setText("12:00");
        topMenuContainer.add(endTimeTxtFld, 1, 2 );

        topMenuContainer.add(setDatesBtn, 2,1);

        return topMenuContainer;
    }

    private VBox initializeLogTable(){
        VBox centerLogViewerContainer = new VBox(0);
        logTable = new TableView<>();
        logTable.setItems(logFilteredList);
        logTable.setPrefHeight(400);

        TableColumn<Log, String> sourceColumn = new TableColumn<>("Source");
        TableColumn<Log, Date> timeColumn = new TableColumn<>("Time");
        TableColumn<Log, String> verbosityColumn = new TableColumn<>("Log Level");
        TableColumn<Log, String> componentColumn = new TableColumn<>("Class");
        TableColumn<Log, String> detailsColumn = new TableColumn<>("Details");
        sourceColumn.setSortable(false);
        sourceColumn.setMinWidth(80);
        timeColumn.setMinWidth(180);
        timeColumn.setSortable(true);
        verbosityColumn.setSortable(false);
        verbosityColumn.setMinWidth(60);
        componentColumn.setSortable(false);
        componentColumn.setMinWidth(200);
        detailsColumn.setSortable(false);
        detailsColumn.setMinWidth(575);

        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        verbosityColumn.setCellValueFactory(new PropertyValueFactory<>("verbosity"));
        componentColumn.setCellValueFactory(new PropertyValueFactory<>("component"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        logTable.getColumns().add(sourceColumn);
        logTable.getColumns().add(timeColumn);
        logTable.getColumns().add(verbosityColumn);
        logTable.getColumns().add(componentColumn);
        logTable.getColumns().add(detailsColumn);

        centerLogViewerContainer.getChildren().add(logTable);

        return centerLogViewerContainer;
    }

    private VBox getFiltersContainer(){

        VBox container = new VBox(10);
        container.setPadding(new Insets(5,5,5,5));

        VBox sourceListContainer = new VBox(5);
        VBox verbosityCheckBoxContainer = new VBox(5);
        VBox detailsSearchContainer = new VBox(5);
        VBox componentSearchContainer = new VBox(5);

        ObjectProperty<Predicate<Log>> sourceFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Log>> verbosityFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Log>> detailsSearchFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Log>> componentSearchFilter = new SimpleObjectProperty<>();


        // Sources
        Label sourceLabel = new Label("Sources");
        sourceLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));

        sourcesListView = new ListView<>();
        sourcesListView.setPrefHeight(sourcesListView.getItems().size() * 26);
        sourcesListView.setTooltip(new Tooltip("Hold CMD/CTRL to select multiple values"));
        ObjectBinding<Predicate<Log>> sourcesObjectBinding = new ObjectBinding<Predicate<Log>>() {
            private final Set<String> srcs = new HashSet<>();
            {
                sourcesListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<String>) c -> {
                    boolean changed = false;

                    while (c.next()){
                        if (c.wasRemoved()){
                            changed = true;
                            c.getRemoved().stream().map(String::toLowerCase).forEach(srcs::remove);
                        }
                        if (c.wasAdded()){
                            changed = true;
                            try {
                                c.getAddedSubList().stream().map(String::toLowerCase).forEach(srcs::add);
                            }
                            catch (IndexOutOfBoundsException ignored){

                            }
                        }
                    }
                    if (changed){
                        invalidate();
                    }
                });
            }
            @Override
            protected Predicate<Log> computeValue() {
                return log -> srcs.contains(log.getSource().toLowerCase());
            }
        };
        sourceFilter.bind(sourcesObjectBinding);


        // Verbosity
        Label verbosityLabel = new Label("Log Level");
        verbosityLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));

        verbosityListView = new ListView<>();
        verbosityListView.setTooltip(new Tooltip("Hold CMD/CTRL to select multiple values"));
        verbosityListView.setPrefHeight(verbosityListView.getItems().size() * 26);
        ObjectBinding<Predicate<Log>> verbosityObjectBinding = new ObjectBinding<Predicate<Log>>() {
            private final Set<String> verbosityStrs = new HashSet<>();
            {
                verbosityListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<String>) c -> {
                    boolean changed = false;

                    while (c.next()){
                        if (c.wasRemoved()){
                            changed = true;
                            c.getRemoved().stream().map(String::toLowerCase).forEach(verbosityStrs::remove);
                        }
                        if (c.wasAdded()){
                            changed = true;

                            try {
                                c.getAddedSubList().stream().map(String::toLowerCase).forEach(verbosityStrs::add);
                            }
                            catch (IndexOutOfBoundsException ignored){

                            }
                        }
                    }
                    if (changed){
                        invalidate();
                    }
                });
            }
            @Override
            protected Predicate<Log> computeValue() {
                return log -> verbosityStrs.contains(log.getVerbosity().toLowerCase());
            }
        };
        verbosityFilter.bind(verbosityObjectBinding);

        // Details
        Label detailsLabel = new Label("Details");
        detailsLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));

        TextField detailsSearchField = new TextField();
        detailsSearchField.setPromptText("e.g. finished initializing");
        detailsSearchFilter.bind(Bindings.createObjectBinding(() ->

                        log -> log.getDetails().toLowerCase().contains(detailsSearchField.getText().toLowerCase()),
                detailsSearchField.textProperty()
        ));

        // Component
        Label componentLabel = new Label("Class");
        componentLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));

        TextField componentSearchField = new TextField();
        componentSearchField.setPromptText("e.g. Application.ElastiCubeManager");
        componentSearchFilter.bind(Bindings.createObjectBinding(() ->

                            log -> log.getComponent().toLowerCase().contains(componentSearchField.getText().toLowerCase()),
                    componentSearchField.textProperty()
            ));

        // Bind filtered list to filter predicates
        logFilteredList.predicateProperty().bind(Bindings.createObjectBinding(() ->
                        sourceFilter.get().and(componentSearchFilter.get()).and(detailsSearchFilter.get()).and(verbosityFilter.get()),
                detailsSearchFilter, componentSearchFilter, sourceFilter, verbosityFilter
        ));

        // add labels and lists/search fields to containers
        detailsSearchContainer.getChildren().addAll(detailsLabel, detailsSearchField);
        componentSearchContainer.getChildren().addAll(componentLabel, componentSearchField);
        sourceListContainer.getChildren().addAll(sourceLabel, sourcesListView);
        verbosityCheckBoxContainer.getChildren().addAll(verbosityLabel, verbosityListView);

        // add filter containers to root container
        container.getChildren().addAll(sourceListContainer, verbosityCheckBoxContainer,componentSearchContainer, detailsSearchContainer);

        return container;
    }

    private VBox centerLayoutDateSelectionAndTable(GridPane dateContainer, VBox table){

        VBox container = new VBox(5);
        container.getChildren().addAll(dateContainer, table);
        return container;

    }

    // Log loaders
    static private List<Log> ecsLogs() {

        List<Log> logs = new ArrayList<>();
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
                        allLogLines.addAll(logLines);

                    } catch (IOException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file " + f.getName() + " for reading", ButtonType.CLOSE);
                        alert.showAndWait();
                    }
                }
            }
        }


        for (String logStr: allLogLines){

            Log log = ecsLogParser(logStr);

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
        return removeDuplicates(logs);
    }

    static private List<Log> iisNodeLogs(){

        List<Log> logs = new ArrayList<>();
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
        return removeDuplicates(logs);

    }

    static private List<Log> prismWebLogs(){

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
                if (log != null && log.getTime() != null) {
                    if (log.getTime().after(startTime) && log.getTime().before(endTime)) logs.add(log);
                }
            }
            catch (NullPointerException e){
                System.out.println("couldn't read log");
            }
        }

        System.out.println("Total number of PrismWeb logs: " + logs.size());
        return removeDuplicates(logs);

    }

    // Log Parsers
    private static Log iisNodeLogParse(String log){

        Log l = new Log();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(log);

        l.setSource(sources[1]);
        int i = 0;
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

    private static Log prismWebLogParser(String log){

        if (log.startsWith("Exception") || log.trim().startsWith("at") || log.startsWith("Sisense") || log.startsWith("System")){
            return null;
        }

        Log l = new Log();

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
                    verbosityListView.getItems().clear();
                    sourcesListView.getItems().clear();
                }

                Thread backgroundThread = new Thread(() -> {
                    logs.addAll(iisNodeLogs());
                    logs.addAll(prismWebLogs());
                    logs.addAll(ecsLogs());

                    if (logs.size() > 0){

                        numLogsLoaded = new Label();

                        Collections.sort(logs);

                        verbosityObsList = FXCollections.observableArrayList(verbosityUniqueValues(logs));
                        sourcesObsList = FXCollections.observableArrayList(sourcesUniqueValues(logs));

                        verbosityListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                        sourcesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                        // run after logs were added
                        Platform.runLater(() -> {

                            rootLayout.setBottom(new LogCountContainer(numLogsLoaded, logTable));

                            setDatesBtn.setDisable(false);

                            verbosityListView.getItems().addAll(verbosityObsList);
                            verbosityListView.setPrefHeight(verbosityListView.getItems().size() * 26);
                            verbosityListView.getSelectionModel().selectAll();

                            sourcesListView.getItems().addAll(sourcesObsList);
                            sourcesListView.setPrefHeight(sourcesListView.getItems().size() * 26);
                            sourcesListView.getSelectionModel().selectAll();
                        });
                    }

                    // no logs found
                    else {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No logs were found for the selected dates", ButtonType.OK);
                            alert.showAndWait();
                            setDatesBtn.setDisable(false);
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

    // Helper methods
    private static Set<String> verbosityUniqueValues(List<Log> logs){

        List<String> list = new ArrayList<>();

        for (Log l : logs) {
            list.add(l.getVerbosity());
        }

        return new HashSet<>(list);

    }

    private static Set<String> sourcesUniqueValues(List<Log> logs){

        List<String> list = new ArrayList<>();
        for (Log l : logs){
            list.add(l.getSource());
        }

        return new HashSet<>(list);

    }

    private static List<Log> removeDuplicates(List<Log> logs){

        Set<Log> s = new HashSet<>(logs);
        logs = new ArrayList<>();
        logs.addAll(s);
        return logs;
    }
}