package org.kobbigal.sisenselogreader.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.kobbigal.sisenselogreader.App;

import java.time.LocalDate;

public class DateMenu extends GridPane {

    private final double HGAP = 10;
    private final double VGAP = 2;
    private final String START_LABEL = "Start";
    private final String END_LABEL = "End";
    private final String SUBMIT = "Submit";
    private final String FONT_FAMILY = "Agency FB";
    private final String DATE_FORMAT = "MM/DD/YYYY";
    private final String TIME_FORMAT = "HH:mm";
    private final String INITIAL_TIME = "00:00";
    private final String FINAL_TIME = "00:10";
    private final int FONT_SIZE = 20;
    private final Insets PADDING = new Insets(10);
    private final Label startTimeLabel = new Label(START_LABEL);
    private final Label endTimeLabel = new Label(END_LABEL);
    private final Button submit = new Button(SUBMIT);
    private LocalDate today = LocalDate.now();

    public DateMenu(DatePicker start, DatePicker end, TextField startTimeField, TextField endTimeField, Application app) {
        super();
        this.setHgap(HGAP);
        this.setVgap(VGAP);
        this.setAlignment(Pos.CENTER);
        this.setPadding(PADDING);

        startTimeLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE));
        endTimeLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE));
        this.add(startTimeLabel, 0, 0);
        this.add(endTimeLabel, 1, 0);

        start = new DatePicker(today);
        start.setMinSize(50, 10);
        start.setPromptText(DATE_FORMAT);
        this.add(start, 0,1);

        end = new DatePicker(today);
        end.setMinSize(50, 10);
        end.setPromptText(DATE_FORMAT);
        this.add(end, 1,1);

//        submit.setOnAction(event -> app.);

        startTimeField = new TextField();
        startTimeField.setPromptText(TIME_FORMAT);
        startTimeField.setText(INITIAL_TIME);
        this.add(startTimeField, 0, 2);

        endTimeField = new TextField();
        endTimeField.setPromptText(TIME_FORMAT);
        endTimeField.setText(FINAL_TIME);
        this.add(endTimeField, 1,2);

        this.add(submit, 2,1);
    }
}
