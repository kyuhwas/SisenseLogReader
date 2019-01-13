package org.kobbigal.sisenselogreader.parsers;

import org.kobbigal.sisenselogreader.model.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrismWebServerLogParser implements ILogParser {

    private List<Log> logs;
    private Pattern logPattern = Pattern.compile("(\\d+) \\[(.*?)] \\[(.*?)]:\\[(.*?)],\\[(.*?)] \\[(.*?)]: \\[(.*)]");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    public PrismWebServerLogParser(List<String> logLines){
        logs = new ArrayList<>();
        parse(logLines);
    }

    private void parse(List<String> logLines) {

        System.out.println("Started parsing...");

        for (String line : logLines){

            Matcher matcher = logPattern.matcher(line);

            while (matcher.find()){
                try {
                    Log log = new Log();
                    log.setSource("IIS");
                    log.setTimeRunning(Integer.parseInt(matcher.group(1)));
                    log.setTime(dateFormat.parse(matcher.group(2)));
                    log.setVerbosity(matcher.group(5));
                    log.setComponent(matcher.group(6));
                    log.setDetails(matcher.group(7));
//                    System.out.println(log);
                    addToListOfLogs(log);
                } catch (ParseException | NullPointerException e){
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Finished parsing");

    }

    private void addToListOfLogs(Log log){
        this.logs.add(log);
    }

    @Override
    public List<Log> logList() {
        return logs;
    }
}
