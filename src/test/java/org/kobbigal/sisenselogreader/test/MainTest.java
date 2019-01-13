package org.kobbigal.sisenselogreader.test;

import org.kobbigal.sisenselogreader.model.Log;
import org.kobbigal.sisenselogreader.model.LogFile;
import org.kobbigal.sisenselogreader.model.LogPaths;
import org.kobbigal.sisenselogreader.parsers.ECSLogParser;
import org.kobbigal.sisenselogreader.workers.LogFileReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainTest {

    public static void main(String[] args) {

//        String logWithNewline = "16711633 [2018/12/13 14:48:57.131] []:[0x00001948],[INFO] [Monet.Client]: [function getColumnProperties(schema:str, table:str, field:str):bat[:str,:str];sql.init();_1 := sql.mvc();bInfo:bat[:str,:str] := sql.col_bat_info(_1,schema,table,field);return bI^Cnfo;end getColumnProperties;batInfo := getColumnProperties(\"aSustainabilityXwAaDW\",\"aNCRIAAaTrackerIAAaJgAaIAAaPerformance\",\"aRootIAAaCause\");io.print(batInfo);sql.exit();;\n" +
//                "]";
//        String logStr = "81539775 [2018/12/12 15:22:00.175] []:[0x00002b18],[INFO] [Application.PhysicalInstance]: [EndConnection. Ecube=[Health & Safety].]";
//        Pattern patternWithNewline = Pattern.compile("(\\d+) \\[(.*?)] \\[(.*?)]:\\[(.*?)],\\[(.*?)] \\[(.*?)]: \\[(.*?)\\n]");
//        Pattern pattern = Pattern.compile("(\\d+) \\[(.*?)] \\[(.*?)]:\\[(.*?)],\\[(.*?)] \\[(.*?)]: \\[(.*)]");
//        Matcher matcher = pattern.matcher(logStr);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
//
//        Log log = new Log();
//        log.setSource("ECS");
//        while (matcher.find()){
//
//            System.out.println(matcher.groupCount());
//
//            try {
//                System.out.println(matcher.group(1));
//                log.setTimeRunning(Integer.parseInt(matcher.group(1)));
//
//                System.out.println(matcher.group(2));
//                log.setTime(dateFormat.parse(matcher.group(2)));
//
//                System.out.println(matcher.group(5));
//                log.setVerbosity(matcher.group(5));
//
//                System.out.println(matcher.group(6));
//                log.setComponent(matcher.group(6));
//
//                System.out.println(matcher.group(7));
//                log.setDetails(matcher.group(7));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            System.out.println(log);
//
//        }




        LogPaths logPaths = new LogPaths();
        for (LogFile logFile : logPaths.getLogFileList()){
            if (logFile.getSource().equals("ECS")){
                LogFileReader logFileReader = new LogFileReader(logFile.getFile());
                ECSLogParser ecsLogParser = new ECSLogParser(logFileReader.getContent());
                List<Log> loglist = ecsLogParser.logList();

                System.out.println("Logs parsed successfully for " + logFile.getFile().getName() + ": " + loglist.size());
            }
        }

    }


}
