package org.kobbigal.sisenselogreader.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import org.kobbigal.sisenselogreader.LogGenerator;
import org.kobbigal.sisenselogreader.model.Log;
import org.kobbigal.sisenselogreader.model.LogFile;
import org.kobbigal.sisenselogreader.model.LogPaths;
import org.kobbigal.sisenselogreader.parsers.ECSLogParser;
import org.kobbigal.sisenselogreader.parsers.MicroServicesLogParser;
import org.kobbigal.sisenselogreader.parsers.PrismWebServerLogParser;
import org.kobbigal.sisenselogreader.views.RootLayout;
import org.kobbigal.sisenselogreader.workers.LogFileReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateSelectionController {

    private RootLayout rootLayout;

    private static Date startTime;
    private static Date endTime;
    private final static SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static ObservableList<Log> logs = FXCollections.observableArrayList();
    private static FilteredList<Log> logFilteredList = new FilteredList<>(logs);


    public DateSelectionController(RootLayout rootLayout) {

        this.rootLayout = rootLayout;
        
    }

    public void handleSubmit(){

        try {

            if  (fieldsValid()){

                startTime = sdt.parse(rootLayout.getDateSelectionContainer().getStartDatePicker().getValue() + " " + rootLayout.getDateSelectionContainer().getStartTimeTxtField().getText());
                endTime = sdt.parse(rootLayout.getDateSelectionContainer().getEndDatePicker().getValue() + " " + rootLayout.getDateSelectionContainer().getEndTimeTxtField().getText());

            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please set both dates", ButtonType.OK);
                alert.showAndWait();
            }

            if (startTime.after(endTime)){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Start time is set after end time", ButtonType.OK);
                rootLayout.getDateSelectionContainer().getStartTimeTxtField().setText("");
                startTime = null;
                endTime = null;
                rootLayout.getDateSelectionContainer().getEndTimeTxtField().setText("");
                alert.showAndWait();
            }
            else {

                if (logs.size() > 0){
                    logs.clear();
                    logFilteredList.clear();
                    rootLayout.clearList();
                }

                rootLayout.getDateSelectionContainer().getSetDatesBtn().setDisable(true);
//                logs.addAll(LogGenerator.getLogs(startTime));
                LogPaths logPaths = new LogPaths(startTime, endTime);
//                LogPaths logPaths = new LogPaths(new Date(1546732800000L), new Date());
                for (LogFile logFile : logPaths.getLogFileList()){
                    System.out.println("log file: " + logFile.getFile().getName());
                    LogFileReader logFileReader = new LogFileReader(logFile.getFile());
                    List<Log> logList = new ArrayList<>();

                    if (logFile.getSource().equals("ECS")){
                        ECSLogParser ecsLogParser = new ECSLogParser(logFileReader.getContent(), startTime, endTime);
                        logList.addAll(ecsLogParser.logList());
                    }
                    if (logFile.getSource().equals("IIS")){
                        PrismWebServerLogParser prismWebServerLogParser = new PrismWebServerLogParser(logFileReader.getContent(), startTime, endTime);
                        logList.addAll(prismWebServerLogParser.logList());
                    }
                    else {
                        MicroServicesLogParser microServicesLogParser = new MicroServicesLogParser(logFileReader.getContent(), startTime, endTime, logFile.getSource());
                        logList.addAll(microServicesLogParser.logList());
                    }

                    System.out.println("Logs parsed successfully for " + logFile.getFile().getName() + ": " + logList.size());
                    logs.addAll(logList);
                }

                Collections.sort(logs);
                System.out.println("Total logs added: " + logs.size());

                rootLayout.setLogFilteredList(logFilteredList);
                rootLayout.setNumberOfFiles(logPaths.getLogFileList().size());
                rootLayout.getDateSelectionContainer().getSetDatesBtn().setDisable(false);
            }

        }
        catch (NullPointerException ignored){

        }
        catch (ParseException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect time syntax", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private boolean fieldsValid(){
        return rootLayout.getDateSelectionContainer().getStartDatePicker().getValue() != null &&
                rootLayout.getDateSelectionContainer().getStartTimeTxtField().getText() != null && !rootLayout.getDateSelectionContainer().getStartTimeTxtField().getText().isEmpty() &&
                rootLayout.getDateSelectionContainer().getEndDatePicker().getValue() != null &&
                rootLayout.getDateSelectionContainer().getEndTimeTxtField().getText() != null && !rootLayout.getDateSelectionContainer().getEndTimeTxtField().getText().isEmpty() ;

    }

}
