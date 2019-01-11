package org.kobbigal.sisenselogreader.views.count;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LogCountContainer extends VBox {

    private int numberOfLogs;

    public LogCountContainer() {

        String NUMBER_OF_LOGS_STR = "Number of logs: ";
        Label numberOfLogsLabel = new Label(NUMBER_OF_LOGS_STR + numberOfLogs);
        numberOfLogsLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));

        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(0,0,30,0));
        this.getChildren().addAll(numberOfLogsLabel);
    }


    public void setNumberOfLogs(int numberOfLogs) {
        this.numberOfLogs = numberOfLogs;
    }
}
