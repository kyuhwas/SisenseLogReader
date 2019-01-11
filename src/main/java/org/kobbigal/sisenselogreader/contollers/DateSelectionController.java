package org.kobbigal.sisenselogreader.contollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import org.kobbigal.sisenselogreader.LogGenerator;
import org.kobbigal.sisenselogreader.model.Log;
import org.kobbigal.sisenselogreader.views.RootLayout;
import org.kobbigal.sisenselogreader.views.count.LogCountContainer;
import org.kobbigal.sisenselogreader.views.filters.FiltersContainer;
import org.kobbigal.sisenselogreader.views.table.DateSelectionContainer;
import org.kobbigal.sisenselogreader.views.table.LogTableContainer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateSelectionController {

    private RootLayout rootLayout;
    private static Date startTime;
    private static Date endTime;

    private static ObservableList<Log> logs = FXCollections.observableArrayList();
    private static FilteredList<Log> logFilteredList = new FilteredList<>(logs);

    private final static SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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
                rootLayout.getDateSelectionContainer().getSetDatesBtn().setDisable(true);
                logs.addAll(LogGenerator.getLogs(startTime));
                rootLayout.setLogFilteredList(logFilteredList);

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
