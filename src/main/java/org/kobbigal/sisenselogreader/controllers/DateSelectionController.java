package org.kobbigal.sisenselogreader.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.kobbigal.sisenselogreader.model.Log;
import org.kobbigal.sisenselogreader.views.RootLayout;
import org.kobbigal.sisenselogreader.views.filters.FiltersContainer;
import org.kobbigal.sisenselogreader.views.status.AppStatusContainer;
import org.kobbigal.sisenselogreader.workers.ReadParseLogTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                    System.out.println("Number of logs: " + logs.size());
                    logs.clear();
                    logFilteredList = new FilteredList<>(logs);
                    rootLayout.clearList();
                    System.out.println("Logs cleared");
                }

                rootLayout.getDateSelectionContainer().getSetDatesBtn().setDisable(true);
                FiltersContainer.getInstance().disableFilterButton();
                FiltersContainer.getInstance().disableClearButton();
                ReadParseLogTask readParseLogTask = new ReadParseLogTask(startTime, endTime);
                rootLayout.setRight(AppStatusContainer.getInstance());
                rootLayout.getAppStatusContainer().bindProgressBar(readParseLogTask.progressProperty());

                Thread thread = new Thread(readParseLogTask);
                readParseLogTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                        event -> {

                            System.out.println("Number of logs in logs: " + logs.size());
                            rootLayout.getDateSelectionContainer().getSetDatesBtn().setDisable(false);

                            if (readParseLogTask.getValue().size() > 0){
                                logs.addAll(readParseLogTask.getValue());
                                rootLayout.setLogFilteredList(logFilteredList);
                                rootLayout.setNumberOfFiles(readParseLogTask.getNumberOfLogs());

                                Platform.runLater(() -> {
                                    FiltersContainer.getInstance().enableFilterButton();
                                    FiltersContainer.getInstance().enableClearButton();
                                });
                            }

                            else {
                                Platform.runLater(() -> {
                                    RootLayout.getInstance().setRight(null);
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "No logs were found between " + startTime + " and  " + endTime, ButtonType.OK);
                                    alert.showAndWait();
                                });
                            }
                        });
                thread.setDaemon(true);
                thread.start();

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
