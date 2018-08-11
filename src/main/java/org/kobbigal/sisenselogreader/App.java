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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import org.kobbigal.sisenselogreader.test.LogGenerator;

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


    private TableView<Log> logTable;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private LocalDate startDate = LocalDate.of(2018, 6, 1);
    private LocalDate endDate = LocalDate.of(2018, 6, 30);
    private Date startTime;
    private Date endTime;
    private TextField startTimeTxtFld;
    private TextField endTimeTxtFld;
    private SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private ObservableList<Log> logs = FXCollections.observableArrayList();
    private FilteredList<Log> logFilteredList = new FilteredList<>(logs);
    private Button setDatesBtn;
    private Label numLogsLoaded;
    private BorderPane rootLayout;

    private ObservableList<String> verbosityObsList;
    private ListView<String> verbosityListView;

//    private ObjectProperty<Predicate<Log>> componentSearchFilter;
//    private ObjectProperty<Predicate<Log>> detailsSearchFilter;

    private final static String[] sources = new String[]{"ECS","IISNode","PrismWebServer"};

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

    private void loadUI(Stage window){

        window.getIcons().add(new Image(IMAGE_URL));
        window.setTitle("Sisense Log Reader");
        int WINDOW_WIDTH = 1600;
        window.setMinWidth(WINDOW_WIDTH);
        int WINDOW_HEIGHT = 600;
        window.setMinHeight(WINDOW_HEIGHT);

        rootLayout = new BorderPane();

        // UI binding
        rootLayout.setTop(initializeDateMenu());
        rootLayout.setCenter(initializeLogTable());
        rootLayout.setLeft(getFiltersContainer());

        Scene scene = new Scene(rootLayout, WINDOW_WIDTH, WINDOW_HEIGHT);

        // TODO: 6/9/18 fix style
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
        startTimeTxtFld.setText("12:00");
        topMenuContainer.add(startTimeTxtFld, 0, 2);

        endTimeTxtFld = new TextField();
        endTimeTxtFld.setPromptText("HH:mm");
        endTimeTxtFld.setText("12:00");
        topMenuContainer.add(endTimeTxtFld, 1, 2 );

        topMenuContainer.add(setDatesBtn, 2,1);

        return topMenuContainer;
    }

    private VBox initializeLogTable(){
        // Center

        // Create table
        VBox centerLogViewerContainer = new VBox(0);
        logTable = new TableView();
        logTable.setItems(logFilteredList);
        logTable.setPrefHeight(400);

        // Add columns
        TableColumn sourceColumn = new TableColumn("Source");
        TableColumn timeColumn = new TableColumn("Time");
        TableColumn verbosityColumn = new TableColumn("Verbosity");
        TableColumn componentColumn = new TableColumn("Component");
        TableColumn detailsColumn = new TableColumn("Details");
        sourceColumn.setSortable(false);
        sourceColumn.setMinWidth(80);
        timeColumn.setMinWidth(180);
        verbosityColumn.setSortable(false);
        verbosityColumn.setMinWidth(60);
        componentColumn.setSortable(false);
        componentColumn.setMinWidth(200);
        detailsColumn.setSortable(false);
        detailsColumn.setMinWidth(575);

        sourceColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("source"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<Log, Date>("time"));
        verbosityColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("verbosity"));
        componentColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("component"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("details"));
        logTable.getColumns().addAll(sourceColumn, timeColumn, verbosityColumn, componentColumn, detailsColumn);

        centerLogViewerContainer.getChildren().add(logTable);

        return centerLogViewerContainer;
    }

    private VBox getFiltersContainer(){

        VBox container = new VBox(10);
        VBox sourceListContainer = new VBox(5);
        VBox verbosityCheckBoxContainer = new VBox(5);
        VBox detailsSearchContainer = new VBox(5);
        VBox componentSearchContainer = new VBox(5);

        ObjectProperty<Predicate<Log>> sourceFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Log>> verbosityFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Log>> detailsSearchFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<Log>> componentSearchFilter = new SimpleObjectProperty<>();

        Label sourceLabel = new Label("Sources");
        sourceLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));

        ListView<String> sourceList = new ListView<>();
        ObservableList<String> sourceItems = FXCollections.observableArrayList(sources);
        sourceList.getItems().addAll(sourceItems);
        sourceList.setPrefHeight(sourceItems.size() * 26);
        sourceList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ObjectBinding<Predicate<Log>> sourcesObjectBinding = new ObjectBinding<Predicate<Log>>() {
            private final Set<String> srcs = new HashSet<>();

            {
                sourceList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<String>() {
                    @Override
                    public void onChanged(Change<? extends String> c) {
                        boolean changed = false;

                        while (c.next()){
                            if (c.wasRemoved()){
                                changed = true;
                                c.getRemoved().stream().map(String::toLowerCase).forEach(srcs::remove);
                            }
                            if (c.wasAdded()){
                                changed = true;
                                c.getAddedSubList().stream().map(String::toLowerCase).forEach(srcs::add);
                            }
                        }
                        if (changed){
                            invalidate();
                        }
                    }
                });
            }


            @Override
            protected Predicate<Log> computeValue() {
                return log -> srcs.contains(log.getSource().toLowerCase());
            }
        };
        sourceFilter.bind(sourcesObjectBinding);


        ListView<String> selected = new ListView<>();
        sourceList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            selected.setItems(sourceList.getSelectionModel().getSelectedItems());
            System.out.println(Arrays.toString(selected.getItems().toArray()));;

        });

        Label verbosityLabel = new Label("Verbosity");
        verbosityLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));
        verbosityListView = new ListView<>();
        verbosityListView.setPrefHeight(verbosityListView.getItems().size() * 26);

        Label detailsLabel = new Label("Details");
        detailsLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));

        TextField detailsSearchField = new TextField();
        detailsSearchField.setPromptText("e.g. finished initializing");
        detailsSearchFilter.bind(Bindings.createObjectBinding(() ->

                        log -> log.getDetails().toLowerCase().contains(detailsSearchField.getText().toLowerCase()),
                detailsSearchField.textProperty()
        ));

        Label componentLabel = new Label("Components");
        componentLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));

        TextField componentSearchField = new TextField();
        componentSearchField.setPromptText("e.g. Application.ElastiCubeManager");
        componentSearchFilter.bind(Bindings.createObjectBinding(() ->

                log -> log.getComponent().toLowerCase().contains(componentSearchField.getText().toLowerCase()),
                    componentSearchField.textProperty()
        ));

        logFilteredList.predicateProperty().bind(Bindings.createObjectBinding(() ->
                        detailsSearchFilter.get().and(componentSearchFilter.get()).and(sourceFilter.get()),
                detailsSearchFilter, componentSearchFilter, sourceFilter
        ));


        detailsSearchContainer.getChildren().addAll(detailsLabel, detailsSearchField);
        componentSearchContainer.getChildren().addAll(componentLabel, componentSearchField);
        sourceListContainer.getChildren().addAll(sourceLabel, sourceList);
        verbosityCheckBoxContainer.getChildren().addAll(verbosityLabel, verbosityListView);

        container.getChildren().addAll(sourceListContainer, verbosityCheckBoxContainer,componentSearchContainer, detailsSearchContainer);
        container.setPadding(new Insets(5,5,5,5));

        return container;
    }

    private VBox numberOfLogsContainer(){
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(0,0,30,0));
        container.getChildren().add(numLogsLoaded);
        return container;
    }

    // Log loaders
    private List<Log> ecsLogs() {

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

    private List<Log> iisNodeLogs(){

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
                }

                Thread backgroundThread = new Thread(() -> {
                    // todo bind back
//                    logs.addAll(iisNodeLogs());
//                    logs.addAll(prismWebLogs());
//                    logs.addAll(ecsLogs());

                     //testing
                    LogGenerator logGenerator = new LogGenerator();
                    logs.addAll(logGenerator.getLogs());

                    if (logs.size() > 0){

                        numLogsLoaded = new Label();
                        numLogsLoaded .setFont(Font.font("Agency FB", FontWeight.BOLD, 20));

                        Collections.sort(logs);

                        verbosityObsList = FXCollections.observableArrayList(verbositySet(logs));

                        verbosityListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                        verbosityListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                            ListView<String> selected = new ListView<>();
                            selected.setItems(verbosityListView.getSelectionModel().getSelectedItems());
                            System.out.println(Arrays.toString(selected.getItems().toArray()));
                        });

                        // disable submission while log loading occurs
                        Platform.runLater(() -> {
                            numLogsLoaded.setText("Number of logs: " + String.valueOf(logs.size()));
                            rootLayout.setBottom(numberOfLogsContainer());
                            setDatesBtn.setDisable(false);
                            verbosityListView.getItems().addAll(verbosityObsList);
                            verbosityListView.setPrefHeight(verbosityListView.getItems().size() * 26);
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
    private static Set<String> verbositySet(List<Log> logs){

        List<String> list = new ArrayList<>();

        for (Log l : logs) {
            list.add(l.getVerbosity());
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