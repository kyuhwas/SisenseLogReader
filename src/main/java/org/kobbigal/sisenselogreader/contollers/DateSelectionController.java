package org.kobbigal.sisenselogreader.contollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import org.kobbigal.sisenselogreader.LogGenerator;
import org.kobbigal.sisenselogreader.model.Log;
import org.kobbigal.sisenselogreader.views.filters.FiltersContainer;
import org.kobbigal.sisenselogreader.views.table.DateSelectionContainer;
import org.kobbigal.sisenselogreader.views.table.LogTableContainer;

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

public class DateSelectionController {

    private DateSelectionContainer view;
    private FiltersContainer filtersContainer;
    private LogTableContainer logTableContainer;
    private static Date startTime;
    private static Date endTime;
    private static Label numLogsLoaded;

    private static ObservableList<Log> logs = FXCollections.observableArrayList();
    private static FilteredList<Log> logFilteredList = new FilteredList<>(logs);

//    private final static String[] sources = new String[]{"ECS","IISNode","PrismWebServer"};
//    private final static String IIS_NODE_PATH = "C:\\Program Files\\Sisense\\PrismWeb\\vnext\\iisnode\\";
//    private final static String GALAXY_PATH = "C:\\ProgramData\\Sisense\\application-logs\\galaxy\\";
//    private final static String PRISMWEB_LOGS_PATH = "C:\\ProgramData\\Sisense\\PrismWeb\\Logs\\";
//    private final static String ECS_LOG_PATH = "C:\\ProgramData\\Sisense\\PrismServer\\PrismServerLogs\\";
    private final static SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public DateSelectionController(DateSelectionContainer view, FiltersContainer filtersContainer, LogTableContainer logTableContainer) {

        this.view = view;
        this.filtersContainer = filtersContainer;
        this.logTableContainer = logTableContainer;
        
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void handleSubmit(){

        try {

            if  (fieldsValid()){

                startTime = sdt.parse(view.getStartDatePicker().getValue() + " " + view.getStartTimeTxtField().getText());
                endTime = sdt.parse(view.getEndDatePicker().getValue() + " " + view.getEndTimeTxtField().getText());

            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please set both dates", ButtonType.OK);
                alert.showAndWait();
            }

            if (startTime.after(endTime)){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Start time is set after end time", ButtonType.OK);
                view.getStartTimeTxtField().setText("");
                startTime = null;
                endTime = null;
                view.getEndTimeTxtField().setText("");
                alert.showAndWait();
            }
            else {
                view.getSetDatesBtn().setDisable(true);
                logs.addAll(LogGenerator.getLogs(startTime));
                filtersContainer.setSourcesList();
                filtersContainer.setVerbosityList();
                logTableContainer.setTableItems(logFilteredList);
            }
//            else {
//                view.getSetDatesBtn().setDisable(true);
//
//                if (logs.size() > 0){
//                    logs.clear();
//                    verbosityListView.getItems().clear();
//                    sourcesListView.getItems().clear();
//                }
//
//                Thread backgroundThread = new Thread(() -> {
//                    logs.addAll(iisNodeLogs());
//                    logs.addAll(prismWebLogs());
//                    logs.addAll(ecsLogs());
//
//                    if (logs.size() > 0){
//                        numLogsLoaded = new Label();
//
//                        Collections.sort(logs);
//
//                        verbosityObsList = FXCollections.observableArrayList(verbosityUniqueValues(logs));
//                        sourcesObsList = FXCollections.observableArrayList(sourcesUniqueValues(logs));
//
//                        verbosityListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//                        sourcesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//
//                        // run after logs were added
//                        Platform.runLater(() -> {
//
//                            rootLayout.setBottom(new LogCountContainer(numLogsLoaded, logTable));
//
//                            view.getSetDatesBtn().setDisable(false);
//
//                            verbosityListView.getItems().addAll(verbosityObsList);
//                            verbosityListView.setPrefHeight(verbosityListView.getItems().size() * 26);
//                            verbosityListView.getSelectionModel().selectAll();
//
//                            sourcesListView.getItems().addAll(sourcesObsList);
//                            sourcesListView.setPrefHeight(sourcesListView.getItems().size() * 26);
//                            sourcesListView.getSelectionModel().selectAll();
//                        });
//                    }
//
//                    // no logs found
//                    else {
//                        Platform.runLater(() -> {
//                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No logs were found for the selected dates", ButtonType.OK);
//                            alert.showAndWait();
//                            view.getSetDatesBtn().setDisable(false);
//                        });
//                    }
//                });
//                backgroundThread.setDaemon(true);
//                backgroundThread.start();
//
//            }
        }
        catch (NullPointerException ignored){

        }
        catch (ParseException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect time syntax", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private boolean fieldsValid(){
        return view.getStartDatePicker().getValue() != null &&
        view.getStartTimeTxtField().getText() != null && !view.getStartTimeTxtField().getText().isEmpty() &&
        view.getEndDatePicker().getValue() != null &&
        view.getEndTimeTxtField().getText() != null && !view.getEndTimeTxtField().getText().isEmpty() ;

    }

//    static private List<Log> ecsLogs() {
//
//        List<Log> logs = new ArrayList<>();
//        File[] fls = new File(Paths.get(ECS_LOG_PATH).normalize().toString()).listFiles();
//        List<String> allLogLines = new ArrayList<>();
//        List<String> logLines;
//
//        if (fls != null) {
//            for (File f : fls) {
//                if (f.getName().contains("ECS.log")) {
//
//                    // Open read stream for each file
//                    try (Stream<String> stream = Files.lines(Paths.get(f.getAbsolutePath()), StandardCharsets.ISO_8859_1)) {
//
//                        // Filter log lines for empty and without dates
//                        logLines = stream.filter(line -> !line.isEmpty())
//                                .filter(line -> Character.isDigit(line.charAt(0)))
//                                .collect(Collectors.toList());
//                        allLogLines.addAll(logLines);
//
//                    } catch (IOException e) {
//                        Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file " + f.getName() + " for reading", ButtonType.CLOSE);
//                        alert.showAndWait();
//                    }
//                }
//            }
//        }
//
//
//        for (String logStr: allLogLines){
//
//            Log log = ecsLogParser(logStr);
//
//            // Check if log time is in selected range and filter empty detail logs
//            try {
//                if (log != null && log.getTime() != null && !log.getDetails().isEmpty()) {
//                    if (log.getTime().after(startTime) && log.getTime().before(endTime)) logs.add(log);
//                }
//            }
//            catch (NullPointerException ignored){
//            }
//        }
//
//        System.out.println("Total number of ECS logs: " + logs.size());
//        return removeDuplicates(logs);
//    }
//
//    static private List<Log> iisNodeLogs(){
//
//        List<Log> logs = new ArrayList<>();
//        File[] fls = new File(Paths.get(IIS_NODE_PATH).normalize().toString()).listFiles();
//        List<String> allLogLines = new ArrayList<>();
//        List<String> currentLogLines;
//
//        if (fls != null) {
//            for (File f : fls){
////                if(f.getName().contains("log") && !f.getName().contains("errors")){
//                if(f.getName().contains("txt")){
//                    System.out.println(f.getName());
//                    try (Stream<String> stream = Files.lines(Paths.get(f.getAbsolutePath()), StandardCharsets.ISO_8859_1)) {
//
//                        currentLogLines = stream.filter(line -> !line.isEmpty())
//                                .filter(line  -> Character.isDigit(line.charAt(0)))
//                                .collect(Collectors.toList());
//
//                        allLogLines.addAll(currentLogLines);
//
//                    } catch (IOException e) {
//                        Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file " + f.getName() + " for reading", ButtonType.CLOSE);
//                        alert.showAndWait();
//                    }
//                }
//            }
//        }
//
//        for (String logStr: allLogLines){
//
//            Log log = iisNodeLogParse(logStr);
//
//            // Check if log time is in selected range
//            try {
//                if (log.getTime() != null){
//                    if (log.getTime().after(startTime) && log.getTime().before(endTime)) logs.add(log);
//                }
//            }
//            catch (NullPointerException ignored){
//            }
//        }
//        System.out.println("Total number of IISNode logs: " + logs.size());
//        return removeDuplicates(logs);
//
//    }
//
//    static private List<Log> prismWebLogs(){
//
//        List<Log> logs = new ArrayList<>();
//        File[] fls = new File(Paths.get(PRISMWEB_LOGS_PATH).normalize().toString()).listFiles();
//        List<String> allLogLines = new ArrayList<>();
//        List<String> logLines;
//
//        if (fls != null) {
//            for (File f : fls){
//                if (f.getName().contains("PrismWebServer") && !f.getName().contains("Error") && !f.getName().contains("WhiteBox")){
//
//                    try (Stream<String> stream = Files.lines(Paths.get(f.getAbsolutePath()), StandardCharsets.ISO_8859_1)){
//
//                        logLines = stream.filter(line -> !line.isEmpty())
//                                .filter(line -> Character.isDigit(line.charAt(0)))
//                                .collect(Collectors.toList());
//
//                        //                    System.out.println("Number of logs added from " + f.getName() + ": " + logLines.size());
//                        allLogLines.addAll(logLines);
//
//                    } catch (IOException e) {
//                        Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file " + f.getName() + " for reading", ButtonType.CLOSE);
//                        alert.showAndWait();
//                    }
//                }
//            }
//        }
//
//        for (String logStr: allLogLines){
//
//            Log log = prismWebLogParser(logStr);
//
//            // Check if log time is in selected range
//            try {
//                if (log != null && log.getTime() != null) {
//                    if (log.getTime().after(startTime) && log.getTime().before(endTime)) logs.add(log);
//                }
//            }
//            catch (NullPointerException e){
//                System.out.println("couldn't read log");
//            }
//        }
//
//        System.out.println("Total number of PrismWeb logs: " + logs.size());
//        return removeDuplicates(logs);
//
//    }
//
//    // Log Parsers
//    private static Log iisNodeLogParse(String log){
//
//        Log l = null;
//
//        // TODO handling issues where there are no details
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
//        Pattern pattern = Pattern.compile("\\[(.*?)]");
//        Matcher matcher = pattern.matcher(log);
//
//        int i = 0;
//        while (matcher.find()){
//            l = new Log();
//            l.setSource(sources[1]);
//            switch (i){
//                case 0:
//                    try {
//                        l.setTime(sdf.parse(matcher.group(1)));
//                    } catch (ParseException ignored) {
//
//                    }
//                    break;
//                case 2:
//                    l.setVerbosity(matcher.group(1));
//                    break;
//                case 3:
//                    l.setComponent(matcher.group(1));
//                    break;
//                case 4:
////                    System.out.println("details: " + matcher.group(1));
//                    l.setDetails(matcher.group(1));
//                    break;
//            }
//            i++;
//        }
//
//        if (l != null){
//            return l;
//        }
//
//        return null;
//    }
//
//    private static Log ecsLogParser(String log){
//
//        if (log.trim().startsWith("at") || log.startsWith("]") || log.startsWith("A")){
//            return null;
//        }
//
//        Log l = new Log();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
//
//        Pattern pattern = Pattern.compile("\\[(.*?)]");
//        Matcher matcher = pattern.matcher(log);
//
//        int i = 0;
//
//        while (matcher.find()){
//            l.setSource(sources[0]);
//            switch (i){
//                case 0:
//                    try {
//                        l.setTime(sdf.parse(matcher.group(1)));
//                    } catch (ParseException ignored) {
//                    }
//                    break;
//                case 3:
//                    l.setVerbosity(matcher.group(1));
//                    break;
//                case 4:
//                    l.setComponent(matcher.group(1));
//                    break;
//                case 5:
//                    l.setDetails(matcher.group(1));
//                    break;
//            }
//            i++;
//        }
//        return l;
//    }
//
//    private static Log prismWebLogParser(String log){
//
//        if (log.startsWith("Exception") || log.trim().startsWith("at") || log.startsWith("Sisense") || log.startsWith("System")){
//            return null;
//        }
//
//        Log l = new Log();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
//        Pattern pattern = Pattern.compile("\\[(.*?)]");
//        Matcher matcher = pattern.matcher(log);
//
//        l.setSource(sources[2]);
//        int i = 0;
//        while (matcher.find()){
//
//            switch (i){
//                case 0:
//                    try {
//                        l.setTime(sdf.parse(matcher.group(1)));
//                    } catch (ParseException e) {
//                        System.out.println(log);
//                        return null;
//                    }
//                    break;
//                case 3:
//                    l.setVerbosity(matcher.group(1));
//                    break;
//                case 4:
//                    l.setComponent(matcher.group(1));
//                    break;
//                case 5:
//                    l.setDetails(matcher.group(1));
//                    break;
//            }
//            i++;
//        }
//        return l;
//    }
//
//    private static Set<String> verbosityUniqueValues(List<Log> logs){
//
//        List<String> list = new ArrayList<>();
//
//        for (Log l : logs) {
//            list.add(l.getVerbosity());
//        }
//
//        return new HashSet<>(list);
//
//    }
//
//    private static Set<String> sourcesUniqueValues(List<Log> logs){
//
//        List<String> list = new ArrayList<>();
//        for (Log l : logs){
//            list.add(l.getSource());
//        }
//
//        return new HashSet<>(list);
//
//    }
//
//    private static List<Log> removeDuplicates(List<Log> logs){
//
//        Set<Log> s = new HashSet<>(logs);
//        logs = new ArrayList<>();
//        logs.addAll(s);
//        return logs;
//    }
}
