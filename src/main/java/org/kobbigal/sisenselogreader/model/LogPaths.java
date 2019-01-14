package org.kobbigal.sisenselogreader.model;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LogPaths {

    private List<LogFile> logFileList;
//    private static final Path PRISMWEB_LOGS_PATH = Paths.get("C:\\ProgramData\\Sisense\\PrismWeb\\Logs\\");
//    private static final Path ECS_LOG_PATH = Paths.get("C:\\ProgramData\\Sisense\\PrismServer\\PrismServerLogs\\");
//    private static final Path APPLICATION_LOGS = Paths.get("C:\\ProgramData\\Sisense\\application-logs");

    private static final File webserverFolder = new File("C:\\ProgramData\\Sisense\\PrismWeb\\Logs\\");
    private static final File microservicersFolder = new File("C:\\ProgramData\\Sisense\\application-logs");
    private static final File ecsFolder = new File("C:\\ProgramData\\Sisense\\PrismServer\\PrismServerLogs\\");

    public LogPaths(Date startTime, Date endTime){
        System.out.println("Logs between " + startTime.toString() + " - " + endTime.toString());
        this.logFileList = new ArrayList<>();
        setLogPaths(startTime, endTime);
    }

    public List<LogFile> getLogFileList() {
        return logFileList;
    }

    private void addToLogFileList(LogFile logFile){
        System.out.println("File " + logFile.getFile().getName() + " to be read");
        logFileList.add(logFile);
    }

    private boolean logInSelectedRange(Date start, Date end, Date modified){

        return modified.after(start) && modified.before(end);

    }

    private void setLogPaths(Date start, Date end) {

        // IIS
        File[] listOfWebServerLogs = webserverFolder.listFiles((dir, name) -> name.startsWith("PrismWebServer"));
        if (listOfWebServerLogs != null) {
            for (File file : listOfWebServerLogs){
                if (!file.getName().toLowerCase().contains("error") && !file.getName().toLowerCase().contains("whitebox") && logInSelectedRange(start, end, new Date(file.lastModified()))){
                    addToLogFileList(new LogFile("IIS", file));
                }
            }
        }

        // Pre-7.2 nodejs process logs
        File[] listOfNodeLogs = webserverFolder.listFiles(((dir, name) -> name.startsWith("node")));
        if (listOfNodeLogs != null){
            for (File file : listOfNodeLogs){
                if (logInSelectedRange(start, end, new Date(file.lastModified()))){
                    addToLogFileList(new LogFile("Web Application", file));
                }
            }
        }

        // microservices logs
        File[] listOfMicroserviceFolders = microservicersFolder.listFiles();
        if (listOfMicroserviceFolders != null) {
            for (File folder : listOfMicroserviceFolders){
                File[] microserviceLogs = folder.listFiles(((dir, name) -> !name.toLowerCase().contains("error")));
                if (microserviceLogs != null) {
                    for (File file : microserviceLogs){
                        if (logInSelectedRange(start, end, new Date(file.lastModified()))){
                            addToLogFileList(new LogFile(file.getName().split(".log")[0].replaceAll("[0-9]", ""), file));
                        }
                    }
                }
            }
        }

        // ECS
        File[] listOfECSLogs = ecsFolder.listFiles();
        if (listOfECSLogs != null){
            for (File file : listOfECSLogs){
                if (file.getName().contains("ECS.log") && logInSelectedRange(start, end, new Date(file.lastModified()))){
                    addToLogFileList(new LogFile("ECS", file));
                }
            }
        }

    }
}
