package org.kobbigal.sisenselogreader.views.count;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LogCountContainer extends VBox {

    private Label numberOfLogsLabel;
    private Label numberOfFilesRead;

    public LogCountContainer() {

        numberOfLogsLabel = new Label();
        numberOfLogsLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));
        numberOfFilesRead = new Label();
        numberOfFilesRead.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));

        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(30,0,30,0));
        this.getChildren().addAll(numberOfLogsLabel, numberOfFilesRead);
    }

    public void setNumberOfFilesRead(int numberOfFiles) {
        numberOfFilesRead.setText("Number of files read: " + numberOfFiles);
    }

    public void setNumberOfLogs(int numberOfLogs) {
        numberOfLogsLabel.setText("Number of logs parsed: " + numberOfLogs);
    }
}
