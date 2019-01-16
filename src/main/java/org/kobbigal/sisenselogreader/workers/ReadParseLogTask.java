package org.kobbigal.sisenselogreader.workers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import org.kobbigal.sisenselogreader.model.Log;
import org.kobbigal.sisenselogreader.model.LogFile;
import org.kobbigal.sisenselogreader.model.LogPaths;
import org.kobbigal.sisenselogreader.parsers.ECSLogParser;
import org.kobbigal.sisenselogreader.parsers.MicroServicesLogParser;
import org.kobbigal.sisenselogreader.parsers.PrismWebServerLogParser;
import org.kobbigal.sisenselogreader.views.status.AppStatusContainer;

import java.util.*;

public class ReadParseLogTask extends Task<List<Log>> {

    private Date startTime;
    private Date endTime;
    private int numberOfLogs;

    public ReadParseLogTask(Date startTime, Date endTime) {

        this.startTime = startTime;
        this.endTime= endTime;

    }

    private void setNumberOfLogs(int numberOfLogs) {
        this.numberOfLogs = numberOfLogs;
    }

    public int getNumberOfLogs() {
        return numberOfLogs;
    }

    @Override
    protected List<Log> call() {

        LogPaths logPaths = new LogPaths(startTime, endTime);
        int numberOfFiles = logPaths.getLogFileList().size();
        setNumberOfLogs(numberOfFiles);
        List<Log> totalLogs = FXCollections.observableArrayList();

        Platform.runLater(() -> AppStatusContainer.getInstance().setAppRunHistory("Found " + numberOfFiles + " files in chosen range."));

        int currentFileRead = 1;
        for (LogFile logFile : logPaths.getLogFileList()) {

            currentFileRead++;

            Platform.runLater(() -> AppStatusContainer.getInstance().setAppRunHistory("Reading file and parsing " + logFile.getFile().getName() + "..."));

            LogFileReader logFileReader = new LogFileReader(logFile.getFile());
            List<Log> logList = new ArrayList<>();

            if (logFile.getSource().equals("ECS")) {
                ECSLogParser ecsLogParser = new ECSLogParser(logFileReader.getContent(), startTime, endTime);
                logList.addAll(ecsLogParser.logList());
            }
            if (logFile.getSource().equals("IIS")) {
                PrismWebServerLogParser prismWebServerLogParser = new PrismWebServerLogParser(logFileReader.getContent(), startTime, endTime);
                logList.addAll(prismWebServerLogParser.logList());
            } else {
                MicroServicesLogParser microServicesLogParser = new MicroServicesLogParser(logFileReader.getContent(), startTime, endTime, logFile.getSource());
                logList.addAll(microServicesLogParser.logList());
            }

            Platform.runLater(() -> AppStatusContainer.getInstance().setAppRunHistory("Logs parsed successfully for " + logFile.getFile().getName() + ": " + logList.size()));
            totalLogs.addAll(logList);

            updateProgress(currentFileRead, numberOfFiles);
        }

            Collections.sort(totalLogs);
            return totalLogs;
    }
}