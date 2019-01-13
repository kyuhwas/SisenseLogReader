package org.kobbigal.sisenselogreader.parsers;

import org.kobbigal.sisenselogreader.model.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MicroServicesLogParser implements ILogParser {

    private List<Log> logs;
    private Pattern logPattern = Pattern.compile("(\\d+.\\d+) \\[(.*?)] \\[(.*?)],\\[(.*?)] \\[(.*?)]: \\[(.*)]");
    private Pattern logErrorPattern = Pattern.compile("(\\d+.\\d+) \\[(.*?)] \\[(.*?)],\\[(.*?)] \\[(.*?)]: \\[(.*)");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    public MicroServicesLogParser(List<String> logLines, String microservice){
        this.logs = new ArrayList<>();
        parse(logLines, microservice);
    }

    private void parse(List<String> logLines, String microservice) {

        System.out.println("Started parsing...");

        for (String line : logLines){

//            System.out.println(line);
            if (!line.endsWith("]") || !Character.isDigit(line.charAt(0))){
                line = line.trim();
                System.out.println(line);
            }

            Matcher matcher = logPattern.matcher(line);
            Matcher errorMatcher = logErrorPattern.matcher(line);

            while (matcher.find()){

                System.out.println("Entered");
                try {
                    Log log = new Log();
                    log.setSource(microservice);
                    log.setTimeRunning(Math.round(Float.parseFloat(matcher.group(1))));
                    log.setTime(dateFormat.parse(matcher.group(2)));
                    log.setVerbosity(matcher.group(4));
                    log.setComponent(matcher.group(5));
                    log.setDetails(matcher.group(6));
                    System.out.println(log);
                    addToListOfLogs(log);

                } catch (ParseException | NullPointerException e){
                    e.printStackTrace();
                }
            }

        }

    }

    private void addToListOfLogs(Log log) {
        this.logs.add(log);
    }

    @Override
    public List<Log> logList() {
        return this.logs;
    }
}
