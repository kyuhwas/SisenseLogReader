package org.kobbigal.sisenselogreader.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
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
import org.kobbigal.sisenselogreader.workers.ReadParseLogTask;

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
                ReadParseLogTask readParseLogTask = new ReadParseLogTask(startTime, endTime);
                rootLayout.getAppStatusContainer().bindProgressBar(readParseLogTask.progressProperty());

                Thread thread = new Thread(readParseLogTask);
                thread.setDaemon(true);
                thread.start();
                readParseLogTask.setOnSucceeded(event -> {
                    logs.addAll(readParseLogTask.getValue());
                    rootLayout.setLogFilteredList(logFilteredList);
                    rootLayout.setNumberOfFiles(readParseLogTask.getNumberOfLogs());
                    rootLayout.getDateSelectionContainer().getSetDatesBtn().setDisable(false);
                });
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
