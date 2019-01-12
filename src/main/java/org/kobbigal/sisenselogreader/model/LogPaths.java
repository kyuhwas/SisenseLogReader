package org.kobbigal.sisenselogreader.model;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogPaths {

    private String sisenseVersion;
    private List<LogFile> logFileList;
//    private static final Path PRISMWEB_LOGS_PATH = Paths.get("C:\\ProgramData\\Sisense\\PrismWeb\\Logs\\");
//    private static final Path ECS_LOG_PATH = Paths.get("C:\\ProgramData\\Sisense\\PrismServer\\PrismServerLogs\\");
//    private static final Path APPLICATION_LOGS = Paths.get("C:\\ProgramData\\Sisense\\application-logs");

    private static final File webserverFolder = new File("/Users/kobbigal/temp/SNS-12621/PrismWeb/Logs");
    private static final File microservicersFolder = new File("/Users/kobbigal/temp/SNS-12621/application-logs");
    private static final File ecsFolder = new File("/Users/kobbigal/temp/SNS-12621/PrismServer/PrismServerLogs");

    public LogPaths(String sisenseVersion) {
        this.sisenseVersion = sisenseVersion;
        this.logFileList = new ArrayList<>();
        setLogPaths();
    }

    public LogPaths(){
        this.logFileList = new ArrayList<>();
        setLogPaths();
    }

    public List<LogFile> getLogFileList() {
        return logFileList;
    }

    private void addToLogFileList(LogFile logFile){
        logFileList.add(logFile);
    }

    private void setLogPaths() {

        // IIS
        File[] listOfWebServerLogs = webserverFolder.listFiles((dir, name) -> name.startsWith("PrismWebServer"));
        if (listOfWebServerLogs != null) {
            for (File file : listOfWebServerLogs){
                if (!file.getName().toLowerCase().contains("error") && !file.getName().toLowerCase().contains("whitebox")){
                    addToLogFileList(new LogFile("IIS", file));
                }
            }
        }

        // Pre-7.2 nodejs process logs
        File[] listOfNodeLogs = webserverFolder.listFiles(((dir, name) -> name.startsWith("node")));
        if (listOfNodeLogs != null){
            for (File file : listOfNodeLogs){
                addToLogFileList(new LogFile("Web Application", file));
            }
        }

        // microservices logs
        File[] listOfMicroserviceFolders = microservicersFolder.listFiles();
        if (listOfMicroserviceFolders != null) {
            for (File folder : listOfMicroserviceFolders){
                File[] microserviceLogs = folder.listFiles(((dir, name) -> !name.toLowerCase().contains("error")));
                if (microserviceLogs != null) {
                    for (File file : microserviceLogs){
                        addToLogFileList(new LogFile(file.getName().split(".log")[0].replaceAll("[0-9]", ""), file));
                    }
                }
            }
        }

        File[] listOfECSLogs = ecsFolder.listFiles();
        if (listOfECSLogs != null){
            for (File file : listOfECSLogs){
                if (file.getName().contains("ECS.log")){
                    addToLogFileList(new LogFile("ECS", file));
                }
            }
        }

    }
}
