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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App extends Application {

    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Date startTime;
    private Date endTime;
    private TextField startTimeTxtFld;
    private TextField endTimeTxtFld;
    private ObservableList<Log> logs = FXCollections.observableArrayList();
    private SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final Path IIS_NODE_LOGS_PATH = Paths.get("/Users/kobbigal/Downloads/sample_logs/IISNodeLogs/LAP-IL-KOBBIG-24036-stdout-1526003706085.txt");
    private final Path ECS_LOGS_PATH = Paths.get("/Users/kobbigal/Downloads/sample_logs/ECS.log");
    private final Path PRISMWEB_LOGS_PATH = Paths.get("/Users/kobbigal/Downloads/sample_logs/PrismWebServer.log");

    public static void main(String[] args) {
        launch(args);
    }

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
//        window.widthProperty().addListener((obs, oldValue, newValue) -> {
//            System.out.println("Width changed: " + oldValue + " -> " + newValue);
//        });
//        window.heightProperty().addListener((obs, oldValue, newValue) -> {
//            System.out.println("Height changed: " + oldValue + " -> " + newValue);
//        });

        BorderPane rootLayout = new BorderPane();

        // Top Menu
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
        setDatesBtn.setOnAction(event -> {

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
                    fileList(IIS_NODE_LOGS_PATH, startTime, endTime);
                }

                System.out.println("Start: " + startTime);
                System.out.println("End: " + endTime);

            }
            catch (NullPointerException e){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please set both dates", ButtonType.OK);
                alert.showAndWait();
            }
            catch (ParseException e){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect time syntax", ButtonType.OK);
                alert.showAndWait();
            }
        });
        topMenuContainer.add(setDatesBtn, 2,1);

        // Row 3 - Time fields
        startTimeTxtFld = new TextField();
        startTimeTxtFld.setPromptText("HH:mm");
        topMenuContainer.add(startTimeTxtFld, 0, 2);

        endTimeTxtFld = new TextField();
        endTimeTxtFld.setPromptText("HH:mm");
        topMenuContainer.add(endTimeTxtFld, 1, 2 );

        // Center

        // Create table
        VBox centerLogViewerContainer = new VBox(0);
        TableView logTable = new TableView();

        // Add columns
        TableColumn sourceColumn = new TableColumn("Source");
        TableColumn timeColumn = new TableColumn("Time");
        TableColumn verbosityColumn = new TableColumn("Verbosity");
        TableColumn componentColumn = new TableColumn("Component");
        TableColumn detailsColumn = new TableColumn("Details");
        detailsColumn.setSortable(false);
        verbosityColumn.setSortable(false);
        componentColumn.setSortable(false);
        sourceColumn.setSortable(false);

        sourceColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("source"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<Log, Date>("time"));
        verbosityColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("verbosity"));
        componentColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("component"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("details"));

        // Add logs to table
        for (String l : readFileLines(IIS_NODE_LOGS_PATH)) {

            logs.add(iisNodeLogParse(l));

        }

        for (String l : readFileLines(ECS_LOGS_PATH)){

            if (ecsLogParser(l) != null) logs.add(ecsLogParser(l));

        }

        for (String l : readFileLines(PRISMWEB_LOGS_PATH)){

            if (prismWebLogParser(l) != null) logs.add(prismWebLogParser(l));

        }

        // TODO add sort of column by date
        // logs.sort();

        logTable.setItems(logs);
        logTable.getColumns().addAll(sourceColumn, timeColumn, verbosityColumn, componentColumn, detailsColumn);

        centerLogViewerContainer.getChildren().add(logTable);

//
//        logTable.setRowFactory(row -> new TableRow<Log>() {
//            @Override
//            protected void updateItem(Log item, boolean empty) {
//                super.updateItem(item, empty);
//
//                if (item.getVerbosity().equals("ERROR")){
//                    setStyle("-fx-background-color: tomato;");
//                }
//            }
//        });

        // Left
        VBox filtersContainer = new VBox(10);
        filtersContainer.setPadding(new Insets(15));
        Label filtersLabel = new Label("Filters");
        CheckBox sourceFilterCkBz = new CheckBox("Source");
        CheckBox verbosityFilterCkBx = new CheckBox("Verbosity");
        CheckBox componentFilterChBx = new CheckBox("Component");
        CheckBox detailsSeachFilterChBx = new CheckBox("Details");
        filtersContainer.getChildren().addAll(filtersLabel, sourceFilterCkBz, verbosityFilterCkBx,componentFilterChBx, detailsSeachFilterChBx);

        // UI binding
        rootLayout.setTop(topMenuContainer);
        rootLayout.setCenter(centerLogViewerContainer);
        rootLayout.setLeft(filtersContainer);

        Scene scene = new Scene(rootLayout, WIDTH, HEIGHT);
        window.setScene(scene);
        window.show();

    }

    private List<Path> fileList(Path path, Date start, Date end){

        List<Path> files = new ArrayList<>();

        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);

            Date creation = new Date(attributes.creationTime().toMillis());
            Date lastModified = new Date(attributes.lastModifiedTime().toMillis());

            System.out.println("created: " + creation);
            System.out.println("modified: " + lastModified);

            System.out.println(creation.after(start));
            System.out.println(lastModified.before(end));

            // TODO iterate over every file and add accordingly

            if (creation.after(start)){
                System.out.println("File " + path.getFileName() + " added" );
                files.add(path);
            }

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't read file " + path, ButtonType.CLOSE);
            alert.showAndWait();
        }

        return files;

    }

    // TODO read directory for files, filter files according to start/end time and return list of files
//    private static void files(Path path){
//
//        FileSystem fs = path.getFileSystem();
//        fs.getFileStores().forEach(System.out::println);
//
//    }


    private List<String> readFileLines(Path path){

        List<String> lines;

//        if (log.trim().startsWith("at") || log.startsWith("]") || log.startsWith("A")){
//            return null;
//        }
//        if (log.startsWith("Exception") || log.trim().startsWith("at") || log.startsWith("Sisense") || log.startsWith("System")){
//            return null;
//        }

        try(Stream<String> stream = Files.lines(path)) {

            lines = stream.filter(line -> !line.isEmpty())
//                    .filter(line -> !line.trim().startsWith("at"))
//                    .filter(line -> !line.startsWith("]"))
//                    .filter(line -> !line.startsWith("A"))
//                    .filter(line -> !line.startsWith("Exception"))
//                    .filter(line -> !line.startsWith("Sisense"))
//                    .filter(line -> !line.startsWith("System"))
                    .collect(Collectors.toList());
            stream.close();
            return lines;

        } catch (IOException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot read file " + path, ButtonType.CLOSE);
            alert.showAndWait();
            return null;

        }
    }

    private Log iisNodeLogParse(String log){

        Log l = new Log();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(log);

        l.setSource("IISNode");
        int i = 0;
        while (matcher.find()){

            switch (i){
                case 0:
                    try {
                        l.setTime(sdf.parse(matcher.group(1)));
                    } catch (ParseException e) {
                        e.printStackTrace();
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

    // TODO Handle null
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
                        System.out.println(log + " unable to parse");
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

    // TODO Handle null
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
}