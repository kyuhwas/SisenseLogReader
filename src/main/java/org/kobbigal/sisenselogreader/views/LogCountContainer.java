package org.kobbigal.sisenselogreader.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.kobbigal.sisenselogreader.model.Log;
import javafx.beans.binding.Bindings;

public class LogCountContainer extends HBox {

    private final String NUMBER_OF_LOGS_STR = "Number of logs: ";
    private final Label numLogs = new Label(NUMBER_OF_LOGS_STR);

    public LogCountContainer(Label numberOfLogsLabel, TableView<Log> logTable) {
        super();

        numLogs.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));
        numberOfLogsLabel.textProperty().bind(Bindings.size((logTable.getItems())).asString());
        numberOfLogsLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));

        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(0,0,30,0));
        this.getChildren().addAll(numLogs, numberOfLogsLabel);
    }
}
