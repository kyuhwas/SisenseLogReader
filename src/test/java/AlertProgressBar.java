//package tests;
//
//import classes.LogTest;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.VBox;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//public class AlertProgressBar{
//
//    private final String IIS_NODE_PATH = "/Users/kobbigal/Downloads/sample_logs/IISNodeLogs/";
//    private final String ECS_LOG_PATH = "/Users/kobbigal/Downloads/sample_logs/PrismServerLogs/";
//    private final String PRISMWEB_LOGS_PATH = "/Users/kobbigal/Downloads/sample_logs/PrismWebServer/";
//    private final static String[] sources = new String[]{"ECS","IISNode","PrismWebServer"};
//
//public  List<LogTest> display(){
//
//        List<LogTest> list = new ArrayList<>();
//        Stage window = new Stage();
//        Scene scene;
//        VBox layout;
//
//        window.initModality(Modality.APPLICATION_MODAL);
//        window.setTitle("Working....");
//
//        Label label = new Label("Reading and parsing");
//        ProgressBar progressBar = new ProgressBar();
//        Button cancelBtn = new Button("Cancel");
//        cancelBtn.setOnAction(event -> window.close());
//
//        layout = new VBox(20);
//        layout.getChildren().addAll(label, progressBar, cancelBtn);
//        layout.setAlignment(Pos.CENTER);
//        scene = new Scene(layout, 200, 100);
//
//        window.setScene(scene);
//
//        list.addAll(ecsLogs());
//        list.addAll(iisNodeLogs());
//        list.addAll(prismWebLogs());
//        window.showAndWait();
//
//        return list;
//        }
//
//        private List<LogTest> ecsLogs() {
//
//                List<LogTest> logs = new ArrayList<>();
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
//                File[] fls = new File(Paths.get(ECS_LOG_PATH).normalize().toString()).listFiles();
//                List<String> allLogLines = new ArrayList<>();
//                List<String> logLines;
//
//                if (fls != null) {
//                        for (File f : fls) {
//                                if (f.getName().contains("ECS.log")) {
//
//                                        // Open read stream for each file
//                                        try (Stream<String> stream = Files.lines(Paths.get(f.getAbsolutePath()), StandardCharsets.ISO_8859_1)) {
//
//                                                // Filter log lines for empty and without dates
//                                                logLines = stream.filter(line -> !line.isEmpty())
//                                                        .filter(line -> Character.isDigit(line.charAt(0)))
//                                                        .collect(Collectors.toList());
//
//                                                allLogLines.addAll(logLines);
//
//                                        } catch (IOException e) {
//                                                Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file " + f.getName() + " for reading", ButtonType.CLOSE);
//                                                alert.showAndWait();
//                                        }
//                                }
//                        }
//                }
//
//
//                for (String logStr: allLogLines){
//
//                        LogTest log = ecsLogParser(logStr);
//
//                        // Check if log time is in selected range and filter empty detail logs
//                        try {
//                                if (log != null && log.getTime() != null && !log.getDetails().isEmpty()) {
//                                        if (log.getTime().after(startTime) && log.getTime().before(endTime)) logs.add(log);
//                                }
//                        }
//                        catch (NullPointerException ignored){
//                        }
//                }
//
//                System.out.println("Total number of ECS logs: " + logs.size());
//                return logs;
//        }
//
//        private List<LogTest> iisNodeLogs(){
//
//                List<LogTest> logs = new ArrayList<>();
//                File[] fls = new File(Paths.get(IIS_NODE_PATH).normalize().toString()).listFiles();
//                List<String> allLogLines = new ArrayList<>();
//                List<String> currentLogLines;
//
//                if (fls != null) {
//                        for (File f : fls){
//
//                                if(f.getName().contains("txt")){
//                                        try (Stream<String> stream = Files.lines(Paths.get(f.getAbsolutePath()), StandardCharsets.ISO_8859_1)) {
//
//                                                currentLogLines = stream.filter(line -> !line.isEmpty())
//                                                        .filter(line  -> Character.isDigit(line.charAt(0)))
//                                                        .collect(Collectors.toList());
//
//                                                allLogLines.addAll(currentLogLines);
//
//                                        } catch (IOException e) {
//                                                Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file " + f.getName() + " for reading", ButtonType.CLOSE);
//                                                alert.showAndWait();
//                                        }
//                                }
//                        }
//                }
//
//                for (String logStr: allLogLines){
//
//                        LogTest log = iisNodeLogParse(logStr);
//
//                        // Check if log time is in selected range
//                        try {
//                                if (log.getTime() != null){
//                                        if (log.getTime().after(startTime) && log.getTime().before(endTime)) logs.add(log);
//                                }
//                        }
//                        catch (NullPointerException ignored){
//                        }
//                }
//                System.out.println("Total number of IISNode logs: " + logs.size());
//                return logs;
//
//        }
//
//        private List<LogTest> prismWebLogs(){
//
//                List<LogTest> logs = new ArrayList<>();
//                File[] fls = new File(Paths.get(PRISMWEB_LOGS_PATH).normalize().toString()).listFiles();
//                List<String> allLogLines = new ArrayList<>();
//                List<String> logLines;
//
//                if (fls != null) {
//                        for (File f : fls){
//                                if (f.getName().contains("PrismWebServer") && !f.getName().contains("Error") && !f.getName().contains("WhiteBox")){
//
//                                        try (Stream<String> stream = Files.lines(Paths.get(f.getAbsolutePath()), StandardCharsets.ISO_8859_1)){
//
//                                                logLines = stream.filter(line -> !line.isEmpty())
//                                                        .filter(line -> Character.isDigit(line.charAt(0)))
//                                                        .collect(Collectors.toList());
//
//                                                //                    System.out.println("Number of logs added from " + f.getName() + ": " + logLines.size());
//                                                allLogLines.addAll(logLines);
//
//                                        } catch (IOException e) {
//                                                Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file " + f.getName() + " for reading", ButtonType.CLOSE);
//                                                alert.showAndWait();
//                                        }
//                                }
//                        }
//                }
//
//                for (String logStr: allLogLines){
//
//                        LogTest log = prismWebLogParser(logStr);
//
//                        // Check if log time is in selected range
//                        try {
//                                if (log != null && log.getTime() != null) {
//                                        if (log.getTime().after(startTime) && log.getTime().before(endTime)) logs.add(log);
//                                }
//                        }
//                        catch (NullPointerException e){
//                                System.out.println("couldn't read log");
//                        }
//                }
//
//                System.out.println("Total number of PrismWeb logs: " + logs.size());
//                return logs;
//
//        }
//
//    private static LogTest iisNodeLogParse(String log){
//
//        LogTest l = new LogTest();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
//        Pattern pattern = Pattern.compile("\\[(.*?)]");
//        Matcher matcher = pattern.matcher(log);
//
//        l.setSource(sources[1]);
//        int i = 0;
////        if (!matcher.matches()){
////            l = null;
////            return l;
////        }
//        while (matcher.find()){
//
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
//                    l.setDetails(matcher.group(1));
//                    break;
//            }
//            i++;
//        }
//        return l;
//    }
//
//    private static LogTest ecsLogParser(String log){
//
//        LogTest l = new LogTest();
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
//    private static LogTest prismWebLogParser(String log){
//
//        LogTest l = new LogTest();
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
//}
