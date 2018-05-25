import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test_Parsing {


    public static void main(String[] args) {

//        System.out.println(System.getProperty("user.dir"));
        Path logoPath = Paths.get(System.getProperty("user.dir"),"res","60x60bb.jpg");
        System.out.println(logoPath);
//        Date startDate = new Date();
//        Date endDate = new Date();
//
//        startDate.setTime(1508774481000L);
//        endDate.setTime(1514044881000L);

//        List<Log> logs = ecsLogs(startDate, endDate);
//        System.out.println("Number of logs to add to UI: " + logs.size());
//        iisNodeLogs(startDate, endDate);

//        prismWebLogs(startDate, endDate);

    }

    private static List<Log> ecsLogs(Date start, Date end) {

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

                    System.out.println("Number of logs added from " + f.getName() + ": " + logLines.size());
                    allLogLines.addAll(logLines);

                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file " + f.getName() + " for reading", ButtonType.CLOSE);
                    alert.showAndWait();
                }
            }
        }

//        System.out.println("Total number of logs: " + allLogLines.size());

        for (String logStr: allLogLines){

            Log log = ecsLogParser(logStr);

            // Check if log time is in selected range
            try {
                if (log.getTime() != null){
                    if (log.getTime().after(start) && log.getTime().before(end)) logs.add(log);
                }
            }
            catch (NullPointerException e){
                System.out.println("couldn't read log");
            }
        }
        return logs;
    }

    private static Log ecsLogParser(String log){

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
                        System.out.println(e.getMessage());
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

    private static List<Log> iisNodeLogs(Date start, Date end){

        List<Log> logs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        File[] fls = new File(Paths.get("C:\\Program Files\\Sisense\\PrismWeb\\vnext\\iisnode\\").normalize().toString()).listFiles();
        List<String> allLogLines = new ArrayList<>();
        List<String> currentLogLines;

        for (File f : fls){

            if(f.getName().contains("txt")){
                try (Stream<String> stream = Files.lines(Paths.get(f.getAbsolutePath()), StandardCharsets.ISO_8859_1)) {

                    currentLogLines = stream.filter(line -> !line.isEmpty())
                            .filter(line  -> Character.isDigit(line.charAt(0)))
                            .collect(Collectors.toList());

                    System.out.println("Number of logs in " + f.getName() + ": " + currentLogLines.size());

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
                    if (log.getTime().after(start) && log.getTime().before(end)) logs.add(log);
                }
            }
            catch (NullPointerException ignored){
            }
        }
        System.out.println("Number of IISlogs from range: " + logs.size());
        return logs;

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

    private static List<Log> prismWebLogs(Date start, Date end){

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

                    System.out.println("Number of logs added from " + f.getName() + ": " + logLines.size());
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
                    if (log.getTime().after(start) && log.getTime().before(end)) logs.add(log);
                }
            }
            catch (NullPointerException e){
                System.out.println("couldn't read log");
            }
        }
        return logs;

    }

    private static Log prismWebLogParser(String log){

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

}