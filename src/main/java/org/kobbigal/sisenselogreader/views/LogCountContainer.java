package org.kobbigal.sisenselogreader.views;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.kobbigal.sisenselogreader.model.Log;

public class LogCountContainer extends VBox {

    public LogCountContainer(Label numberOfLogsLabel, TableView<Log> logTable) {
        super();

        String NUMBER_OF_LOGS_STR = "Number of logs: ";
        Label numLogs = new Label(NUMBER_OF_LOGS_STR);

        numLogs.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));
        numberOfLogsLabel.textProperty().bind(Bindings.size((logTable.getItems())).asString());
        numberOfLogsLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));

        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(0,0,30,0));
        this.getChildren().addAll(numLogs, numberOfLogsLabel);
    }
}
