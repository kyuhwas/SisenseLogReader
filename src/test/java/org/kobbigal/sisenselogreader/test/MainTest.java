package org.kobbigal.sisenselogreader.test;

import org.kobbigal.sisenselogreader.model.Log;
import org.kobbigal.sisenselogreader.model.LogFile;
import org.kobbigal.sisenselogreader.model.LogPaths;
import org.kobbigal.sisenselogreader.parsers.ECSLogParser;
import org.kobbigal.sisenselogreader.parsers.MicroServicesLogParser;
import org.kobbigal.sisenselogreader.parsers.PrismWebServerLogParser;
import org.kobbigal.sisenselogreader.workers.LogFileReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainTest {

    public static void main(String[] args) {

        List<Log> totalLogList = new ArrayList<>();
        Date start = new Date(1546732800000L);
        Date end = new Date();

        LogPaths logPaths = new LogPaths(start, end);
        for (LogFile logFile : logPaths.getLogFileList()){
            LogFileReader logFileReader = new LogFileReader(logFile.getFile());
            List<Log> logList = new ArrayList<>();

            if (logFile.getSource().equals("ECS")){
                ECSLogParser ecsLogParser = new ECSLogParser(logFileReader.getContent(), start, end);
                logList.addAll(ecsLogParser.logList());
            }
            if (logFile.getSource().equals("IIS")){
                PrismWebServerLogParser prismWebServerLogParser = new PrismWebServerLogParser(logFileReader.getContent());
                logList.addAll(prismWebServerLogParser.logList());
            }
            else  {
                MicroServicesLogParser microServicesLogParser = new MicroServicesLogParser(logFileReader.getContent(), start, end, logFile.getSource());
                logList.addAll(microServicesLogParser.logList());
            }

            System.out.println("Logs parsed successfully for " + logFile.getFile().getName() + ": " + logList.size());
            totalLogList.addAll(logList);
        }

        System.out.println("Total logs parsed: " + totalLogList.size());


        // TODO parse errors with stack traces
        String s = "28135.886 [2019/01/7 18:07:13.570] [18364],[ERROR] [event-driven-middleware]: [connect ECONNREFUSED 10.100.101.8:5672 Error: connect ECONNREFUSED 10.100.101.8:5672\n" +
                "    at Object._errnoException (util.js:1024:11)\n" +
                "    at _exceptionWithHostPort (util.js:1046:20)\n" +
                "    at TCPConnectWrap.afterConnect [as oncomplete] (net.js:1182:14)]";


    }


}
