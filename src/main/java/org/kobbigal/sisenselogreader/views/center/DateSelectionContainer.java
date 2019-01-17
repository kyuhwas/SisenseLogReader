package org.kobbigal.sisenselogreader.views.center;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;

public class DateSelectionContainer extends GridPane{

    private static Label startTimeLabel;
    private static Label endTimeLabel;
    private static DatePicker startDatePicker;
    private static DatePicker endDatePicker;
    private static TextField startTimeTxtField;
    private static TextField endTimeTxtField;
    private static Button setDatesBtn;

    private static final String FONT_FAMILY = "Agency FB";
    private static final int FONT_SIZE = 20;
    private static final int DATEPICKER_WIDTH = 50;
    private static final int DATEPICKER_HEIGHT = 10;
    private static final String DATEPICKER_PROMPT_TEXT = "MM/DD/YYYY";
    private static final LocalDate today = LocalDate.now();
    private static final String TIME_PROMPT_TEXT = "HH:mm";

    public DateSelectionContainer(){

        this.setHgap(10);
        this.setVgap(2);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(10));

        startTimeLabel = new Label("Start");
        endTimeLabel = new Label("End");
        startDatePicker = new DatePicker();
        endDatePicker = new DatePicker();
        setDatesBtn = new Button("Submit");
        setDatesBtn.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));
        startTimeTxtField  = new TextField();
        endTimeTxtField = new TextField();
        initiateStartTimeLabel();
        initiateEndTimeLabel();
        initiateStartDatePicker();
        initiateEndDatePicker();
        initiateStartTextField();
        initiateEndTextField();
        this.add(setDatesBtn, 2,1);
    }

    private void initiateStartTimeLabel(){
        startTimeLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE));
        this.add(startTimeLabel, 0, 0);
    }

    private void initiateEndTimeLabel(){
        endTimeLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE));
        this.add(endTimeLabel, 1, 0);
    }

    private void initiateStartDatePicker(){
        startDatePicker.setMinSize(DATEPICKER_WIDTH, DATEPICKER_HEIGHT);
        startDatePicker.setPromptText(DATEPICKER_PROMPT_TEXT);
        startDatePicker.setValue(today);
        this.add(startDatePicker, 0, 1);
    }

    private void initiateEndDatePicker(){
        endDatePicker.setMinSize(DATEPICKER_WIDTH, DATEPICKER_HEIGHT);
        endDatePicker.setPromptText(DATEPICKER_PROMPT_TEXT);
        endDatePicker.setValue(today);
        this.add(endDatePicker, 1,1 );
    }

    private void initiateStartTextField(){
        startTimeTxtField.setPromptText(TIME_PROMPT_TEXT);
        startTimeTxtField.setText("00:30");
        this.add(startTimeTxtField, 0, 2);
    }

    private void initiateEndTextField(){
        endTimeTxtField.setPromptText(TIME_PROMPT_TEXT);
        endTimeTxtField.setText("00:40");
        this.add(endTimeTxtField, 1,2);
    }

    public DatePicker getStartDatePicker() {
        return startDatePicker;
    }

    public DatePicker getEndDatePicker() {
        return endDatePicker;
    }

    public TextField getStartTimeTxtField() {
        return startTimeTxtField;
    }

    public TextField getEndTimeTxtField() {
        return endTimeTxtField;
    }

    public Button getSetDatesBtn() {
        return setDatesBtn;
    }

}
