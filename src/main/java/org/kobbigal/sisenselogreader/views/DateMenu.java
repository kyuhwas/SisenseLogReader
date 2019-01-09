package org.kobbigal.sisenselogreader.views;

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

public class DateMenu{

    private static DateMenu instance;
    private static GridPane container;
    private static Label startTimeLabel;
    private static Label endTimeLabel;

    private static DatePicker startDatePicker;
    private static DatePicker endDatePicker;


    private static Button setDatesBtn;
    private static TextField startTimeTxtField;
    private static TextField endTimeTxtField;

    private static final String FONT_FAMILY = "Agency FB";
    private static final int FONT_SIZE = 20;
    private static final int DATEPICKER_WIDTH = 50;
    private static final int DATEPICKER_HEIGHT = 10;
    private static final String DATEPICKER_PROMPT_TEXT = "MM/DD/YYYY";
    private static final LocalDate today = LocalDate.now();
    private static final String TIME_PROMPT_TEXT = "HH:mm";

    public static DateMenu getInstance(){
        if (instance == null){
            instance = new DateMenu();
        }

        return instance;
    }

    private DateMenu(){
        container = new GridPane();
        startTimeLabel = new Label("Start");
        endTimeLabel = new Label("End");
        startDatePicker = new DatePicker();
        endDatePicker = new DatePicker();
        setDatesBtn = new Button("Submit");
        startTimeTxtField  = new TextField();
        endTimeTxtField = new TextField();
    }

    public GridPane load(){

        initiateContainer();
        initiateStartTimeLabel();
        initiateEndTimeLabel();
        initiateStartDatePicker();
        initiateEndDatePicker();
        initiateStartTextField();
        initiateEndTextField();
        container.add(setDatesBtn, 2,1);
        return container;
    }

    private static void initiateContainer(){
        container.setHgap(10);
        container.setVgap(2);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10));
    }

    private static void initiateStartTimeLabel(){
        startTimeLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE));
        container.add(startTimeLabel, 0, 0);
    }

    private static void initiateEndTimeLabel(){
        endTimeLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE));
        container.add(endTimeLabel, 1, 0);
    }

    private static void initiateStartDatePicker(){
        startDatePicker.setMinSize(DATEPICKER_WIDTH, DATEPICKER_HEIGHT);
        startDatePicker.setPromptText(DATEPICKER_PROMPT_TEXT);
        startDatePicker.setValue(today);
        container.add(startDatePicker, 0, 1);
    }

    private static void initiateEndDatePicker(){
        endDatePicker.setMinSize(DATEPICKER_WIDTH, DATEPICKER_HEIGHT);
        endDatePicker.setPromptText(DATEPICKER_PROMPT_TEXT);
        endDatePicker.setValue(today);
        container.add(endDatePicker, 1,1 );
    }

    private static void initiateStartTextField(){
        startTimeTxtField.setPromptText(TIME_PROMPT_TEXT);
        startTimeTxtField.setText("00:30");
        container.add(startTimeTxtField, 0, 2);
    }

    private static void initiateEndTextField(){
        endTimeTxtField.setPromptText(TIME_PROMPT_TEXT);
        endTimeTxtField.setText("00:40");
        container.add(endTimeTxtField, 1,2);
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
