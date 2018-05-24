import javafx.application.Application;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App extends Application {

    private TableView logTable;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Date startTime;
    private Date endTime;
    private TextField startTimeTxtFld;
    private TextField endTimeTxtFld;
    private ObservableList<Log> logs = FXCollections.observableArrayList();
    private SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // TODO menu item to configure log paths
    private final String IIS_NODE_PATH = "C:\\Program Files\\Sisense\\PrismWeb\\vnext\\iisnode\\";
    private final String IIS_NODE_LOGS_PATH = "C:\\Program Files\\Sisense\\PrismWeb\\vnext\\iisnode\\LAP-IL-KOBBIG-24036-stdout-1525977954610.txt";
    private final String PRISMWEB_LOGS_PATH = "C:\\ProgramData\\Sisense\\PrismWeb\\Logs\\PrismWebServer.log";

    // MAC
//    private final Path IIS_NODE_LOGS_PATH = Paths.get("/Users/kobbigal/Downloads/sample_logs/IISNodeLogs/LAP-IL-KOBBIG-24036-stdout-1526003706085.txt");
//    private final Path ECS_LOGS_PATH = Paths.get("/Users/kobbigal/Downloads/sample_logs/ECS.log");
//    private final Path PRISMWEB_LOGS_PATH = Paths.get("/Users/kobbigal/Downloads/sample_logs/PrismWebServer.log");

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
        window.setTitle("Sisense Log Reader");
        int WIDTH = 1400;
        window.setMinWidth(WIDTH);
        int HEIGHT = 600;
        window.setMinHeight(HEIGHT);

        BorderPane rootLayout = new BorderPane();

        // UI binding
        rootLayout.setTop(initializeDateMenu());
        rootLayout.setCenter(initializeLogTable());
        rootLayout.setLeft(initializeFilters());

        Scene scene = new Scene(rootLayout, WIDTH, HEIGHT);
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

    private VBox initializeLogTable(){
        // Center

        // Create table
        VBox centerLogViewerContainer = new VBox(0);
        logTable = new TableView();

        // Add columns
        TableColumn sourceColumn = new TableColumn("Source");
        TableColumn timeColumn = new TableColumn("Time");
        TableColumn verbosityColumn = new TableColumn("Verbosity");
        TableColumn componentColumn = new TableColumn("Component");
        TableColumn detailsColumn = new TableColumn("Details");
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

    private VBox initializeFilters(){
        VBox filtersContainer = new VBox(10);
        filtersContainer.setPadding(new Insets(15));
        Label filtersLabel = new Label("Filters");
        filtersLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));
        CheckBox sourceFilterCkBz = new CheckBox("Source");
        CheckBox verbosityFilterCkBx = new CheckBox("Verbosity");
        CheckBox componentFilterChBx = new CheckBox("Component");
        CheckBox detailsSeachFilterChBx = new CheckBox("Details");
        filtersContainer.getChildren().addAll(filtersLabel, sourceFilterCkBz, verbosityFilterCkBx,componentFilterChBx, detailsSeachFilterChBx);

        return filtersContainer;
    }

    // Modified date isn't accurate
//    private List<Path> fileList(Path path, Date start, Date end){
//
//        List<Path> files = new ArrayList<>();
//        List<Path> filesToRemove = new ArrayList<>();
//        File[] fls = new File(path.normalize().toString()).listFiles();
//        for (File file : fls){
//            if (file.isFile() && (getFileExtension(file).equalsIgnoreCase("txt") || getFileExtension(file).equalsIgnoreCase("log") )){
//                files.add(file.toPath());
//            }
//        }
//
//        for (Path p : files){
//            try {
//                BasicFileAttributes attributes = Files.readAttributes(p, BasicFileAttributes.class);
//                Date created = new Date(attributes.creationTime().toMillis());
//                Date modified = new Date(attributes.lastModifiedTime().toMillis());
//
//                if (!created.after(start) || !modified.before(end)){
//                    filesToRemove.add(p);
//                }
//
//            } catch (IOException e) {
//                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
//                alert.showAndWait();
//            }
//        }
//
//        files.removeAll(filesToRemove);
//        return files;
//    }

    private List<Log> ecsLogs() {

        List<Log> logs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        File[] fls = new File(Paths.get("C:\\ProgramData\\Sisense\\PrismServer\\PrismServerLogs\\").normalize().toString()).listFiles();
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
        System.out.println("Number of IISlogs from range: " + logs.size());
        return logs;

    }

    private List<Log> prismWebLogs(){

        List<Log> logs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        File[] fls = new File(Paths.get("C:\\ProgramData\\Sisense\\PrismWeb\\Logs\\").normalize().toString()).listFiles();
        List<String> allLogLines = new ArrayList<>();
        List<String> logLines;

        for (File f : fls){
            if (f.getName().contains("PrismWebServer") && !f.getName().contains("Error") && !f.getName().contains("WhiteBox")){
                System.out.println(f.getName());

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

        System.out.println("Number of logs added from PrismWeb: " + logs.size());
        return logs;

    }

//    private List<String> readFileLines(Path path){
//
//        List<String> lines;
//
//        try(Stream<String> stream = Files.lines(path)) {
//
//            lines = stream.filter(line -> !line.isEmpty())
//                    .collect(Collectors.toList());
//            stream.close();
//            return lines;
//
//        } catch (IOException e) {
//
//            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot read file " + path, ButtonType.CLOSE);
//            alert.showAndWait();
//            return null;
//
//        }
//    }

    // Log Parsers
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

    // TODO read file first line and last line and check if time interval is relevant
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

    // Helpers
//    private String getFileExtension(File file) {
//        String name = file.getName();
//        try {
//            return name.substring(name.lastIndexOf(".") + 1);
//        } catch (Exception e) {
//            return "";
//        }
//    }

//    private void removeEmptyOutOfRangeLogs(List<Log> list, Date startTime, Date endTime){
//
//        List<Log> logsToRemove = new ArrayList<>();
//
//        for (Log log : list){
//            if (log.getTime() == null || log.getTime().before(startTime) || log.getTime().after(endTime)){
//                logsToRemove.add(log);
//            }
//        }
//
//        list.removeAll(logsToRemove);
//    }

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

//                ProgressBarScene.display();

                if (logTable.getItems().size() > 0){
                    logTable.getItems().clear();
                    logs.clear();
                }

                // add logs
                logs.addAll(iisNodeLogs());
                logs.addAll(prismWebLogs());
                logs.addAll(ecsLogs());

                // sorts logs
                Collections.sort(logs);

                // update UI
                logTable.getItems().addAll(logs);
            }

        }
        catch (NullPointerException ignored){

        }
        catch (ParseException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect time syntax", ButtonType.OK);
            alert.showAndWait();
        }
    }

}
