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
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
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

    private final Path IIS_NODE_PATH = Paths.get("C:\\Program Files\\Sisense\\PrismWeb\\vnext\\iisnode\\");
//    private final Path IIS_NODE_LOGS_PATH = Paths.get("C:\\Program Files\\Sisense\\PrismWeb\\vnext\\iisnode\\LAP-IL-KOBBIG-24036-stdout-1525977954610.txt");
    private final Path ECS_LOGS_PATH = Paths.get("C:\\ProgramData\\Sisense\\PrismServer\\PrismServerLogs\\ECS.log");
    private final Path PRISMWEB_LOGS_PATH = Paths.get("C:\\ProgramData\\Sisense\\PrismWeb\\Logs\\PrismWebServer.log");

    // MAC
//    private final Path IIS_NODE_LOGS_PATH = Paths.get("/Users/kobbigal/Downloads/sample_logs/IISNodeLogs/LAP-IL-KOBBIG-24036-stdout-1526003706085.txt");
//    private final Path ECS_LOGS_PATH = Paths.get("/Users/kobbigal/Downloads/sample_logs/ECS.log");
//    private final Path PRISMWEB_LOGS_PATH = Paths.get("/Users/kobbigal/Downloads/sample_logs/PrismWebServer.log");

    public static void main(String[] args) {
        launch(args);
    }

    private List<Path> fileList(Path path, Date start, Date end){

        List<Path> files = new ArrayList<>();
        List<Path> filesToRemove = new ArrayList<>();
        File[] fls = new File(path.normalize().toString()).listFiles();
        for (File file : fls){
            if (file.isFile() && (getFileExtension(file).equalsIgnoreCase("txt") || getFileExtension(file).equalsIgnoreCase("log") )){
                files.add(file.toPath());
            }
        }

        for (Path p : files){
            try {
                BasicFileAttributes attributes = Files.readAttributes(p, BasicFileAttributes.class);
                Date created = new Date(attributes.creationTime().toMillis());
                Date modified = new Date(attributes.lastModifiedTime().toMillis());

                if (!created.after(start) || !modified.before(end)){
                    filesToRemove.add(p);
                }

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
                alert.showAndWait();
            }
        }

        files.removeAll(filesToRemove);
        return files;
    }

    private List<String> readFileLines(Path path){

        List<String> lines;

        try(Stream<String> stream = Files.lines(path)) {

            lines = stream.filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());
            stream.close();
            return lines;

        } catch (IOException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot read file " + path, ButtonType.CLOSE);
            alert.showAndWait();
            return null;

        }
    }

    // Log Parsers
    private Log iisNodeLogParse(String log){

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

    private Log ecsLogParser(String log){

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
                    } catch (ParseException e) {
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

    // Miscellaneous methods
    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    private void removeEmptyOutOfRangeLogs(List<Log> list, Date startTime, Date endTime){

        List<Log> logsToRemove = new ArrayList<>();

        for (Log log : list){
            if (log.getTime() == null || log.getTime().before(startTime) || log.getTime().after(endTime)){
                logsToRemove.add(log);
            }
        }

        list.removeAll(logsToRemove);
    }

    // Event handlers
    private void handleSubmit(){

        try {

            startTime = sdt.parse(startDatePicker.getValue() + " " + startTimeTxtFld.getText());
            endTime = sdt.parse(endDatePicker.getValue() + " " + endTimeTxtFld.getText());

            if (startTime.after(endTime)){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid range", ButtonType.OK);
                startTimeTxtFld.setText("");
                startTime = null;
                endTime = null;
                endTimeTxtFld.setText("");
                alert.showAndWait();
            }
            else {


                if (logTable.getItems().size() > 0){
                    logTable.getItems().clear();
                    logs.clear();
                }


                for (Path p : fileList(IIS_NODE_PATH, startTime, endTime)){
                    for (String l : readFileLines(p)){
                        logs.addAll(iisNodeLogParse(l));
                    }
                }

                // TODO check why ECS fls returns null values
//                for (Path p : fileList(ECS_LOGS_PATH, startTime, endTime)){
//                    for (String l : readFileLines(p)){
//                        logs.addAll(ecsLogParser(l));
//                    }
//                }

                removeEmptyOutOfRangeLogs(logs, startTime, endTime);

                // sorts logs
                Collections.sort(logs);

                logTable.getItems().addAll(logs);
            }

        }
        catch (NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please set both dates", ButtonType.OK);
            alert.showAndWait();
        }
        catch (ParseException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect time syntax", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // UI
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
        topMenuContainer.add(startTimeLabel, 0, 0);
        Label endTimeLabel = new Label("End");
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

        centerLogViewerContainer.getChildren().add(logTable);

        return centerLogViewerContainer;
    }

    private VBox initializeFilters(){
        VBox filtersContainer = new VBox(10);
        filtersContainer.setPadding(new Insets(15));
        Label filtersLabel = new Label("Filters");
        CheckBox sourceFilterCkBz = new CheckBox("Source");
        CheckBox verbosityFilterCkBx = new CheckBox("Verbosity");
        CheckBox componentFilterChBx = new CheckBox("Component");
        CheckBox detailsSeachFilterChBx = new CheckBox("Details");
        filtersContainer.getChildren().addAll(filtersLabel, sourceFilterCkBz, verbosityFilterCkBx,componentFilterChBx, detailsSeachFilterChBx);

        return filtersContainer;
    }
}
