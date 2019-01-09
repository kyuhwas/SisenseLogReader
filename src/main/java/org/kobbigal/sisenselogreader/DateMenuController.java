package org.kobbigal.sisenselogreader;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.kobbigal.sisenselogreader.views.DateMenu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class DateMenuController{

    private DateMenu view;
    private static Date startTime;
    private static Date endTime;
    private final static SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    DateMenuController(DateMenu view) {

        this.view = view;
        
    }

    Date getStartTime() {
        return startTime;
    }

    Date getEndTime() {
        return endTime;
    }

    void handleSubmit(){

        try {

            if  (fieldsValid()){

                startTime = sdt.parse(view.getStartDatePicker().getValue() + " " + view.getStartTimeTxtField().getText());
                endTime = sdt.parse(view.getEndDatePicker().getValue() + " " + view.getEndTimeTxtField().getText());

            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please set both dates", ButtonType.OK);
                alert.showAndWait();
            }

            if (startTime.after(endTime)){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Start time is set after end time", ButtonType.OK);
                view.getStartTimeTxtField().setText("");
                startTime = null;
                endTime = null;
                view.getEndTimeTxtField().setText("");
                alert.showAndWait();
            }
//            else {
//                view.getSetDatesBtn().setDisable(true);
//
//                if (logs.size() > 0){
//                    logs.clear();
//                    verbosityListView.getItems().clear();
//                    sourcesListView.getItems().clear();
//                }
//
//                Thread backgroundThread = new Thread(() -> {
//                    logs.addAll(iisNodeLogs());
//                    logs.addAll(prismWebLogs());
//                    logs.addAll(ecsLogs());
//
//                    if (logs.size() > 0){
////                        logs.filtered(log -> log.getSource().equals("Galaxy")).forEach(log -> System.out.println(log.toString()));
//                        numLogsLoaded = new Label();
//
//                        Collections.sort(logs);
//
//                        verbosityObsList = FXCollections.observableArrayList(verbosityUniqueValues(logs));
//                        sourcesObsList = FXCollections.observableArrayList(sourcesUniqueValues(logs));
//
//                        verbosityListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//                        sourcesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//
//                        // run after logs were added
//                        Platform.runLater(() -> {
//
//                            rootLayout.setBottom(new LogCountContainer(numLogsLoaded, logTable));
//
//                            if (appVersion != null) appWindow.setTitle("Sisense Log Reader - version detected: " + appVersion);
//
//
//                            setDatesBtn.setDisable(false);
//
//                            verbosityListView.getItems().addAll(verbosityObsList);
//                            verbosityListView.setPrefHeight(verbosityListView.getItems().size() * 26);
//                            verbosityListView.getSelectionModel().selectAll();
//
//                            sourcesListView.getItems().addAll(sourcesObsList);
//                            sourcesListView.setPrefHeight(sourcesListView.getItems().size() * 26);
//                            sourcesListView.getSelectionModel().selectAll();
//                        });
//                    }
//
//                    // no logs found
//                    else {
//                        Platform.runLater(() -> {
//                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No logs were found for the selected dates", ButtonType.OK);
//                            alert.showAndWait();
//                            setDatesBtn.setDisable(false);
//                        });
//                    }
//                });
//                backgroundThread.setDaemon(true);
//                backgroundThread.start();
//
//            }
        }
        catch (NullPointerException ignored){

        }
        catch (ParseException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect time syntax", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private boolean fieldsValid(){
        return view.getStartDatePicker().getValue() != null &&
        view.getStartTimeTxtField().getText() != null && !view.getStartTimeTxtField().getText().isEmpty() &&
        view.getEndDatePicker().getValue() != null &&
        view.getEndTimeTxtField().getText() != null && !view.getEndTimeTxtField().getText().isEmpty() ;

    }
}
