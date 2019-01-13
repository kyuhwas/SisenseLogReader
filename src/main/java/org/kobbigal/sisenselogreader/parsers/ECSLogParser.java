package org.kobbigal.sisenselogreader.parsers;

import org.kobbigal.sisenselogreader.model.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ECSLogParser implements ILogParser {

    private List<Log> logs;
//    private Pattern logPatternNewLine = Pattern.compile("(\\d+) \\[(.*?)] \\[(.*?)]:\\[(.*?)],\\[(.*?)] \\[(.*?)]: \\[(.*?)\\n]");
    private Pattern logPattern = Pattern.compile("(\\d+) \\[(.*?)] \\[(.*?)]:\\[(.*?)],\\[(.*?)] \\[(.*?)]: \\[(.*)]");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    public ECSLogParser(List<String> logLines, Date start, Date end){
        logs = new ArrayList<>();
        parse(logLines, start, end);
    }

    @Override
    public List<Log> logList() {
        return logs;
    }

    private void addToListOfLogs(Log log){
//        System.out.println("Added log " + log.toString());
        this.logs.add(log);
    }

    private void parse(List<String> logLines, Date start, Date end){

        System.out.println("Started parsing...");

        for (String line : logLines){
//            System.out.println(line);
            Matcher matcher = logPattern.matcher(line);

            while (matcher.find()){
                try {

                    // check if log timestamp is in range
                    Date logTime = dateFormat.parse(matcher.group(2));
                    if (logTime.after(start) && logTime.before(end)){

                        Log log = new Log();
                        log.setSource("ECS");
//                    log.setTimeRunning(Integer.parseInt(matcher.group(1)));
                        log.setTime(dateFormat.parse(matcher.group(2)));
                        log.setVerbosity(matcher.group(5));
                        log.setComponent(matcher.group(6));
                        log.setDetails(matcher.group(7));
//                    System.out.println(log);
                        addToListOfLogs(log);
                    }

                } catch (ParseException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("finished parsing");
    }

}
