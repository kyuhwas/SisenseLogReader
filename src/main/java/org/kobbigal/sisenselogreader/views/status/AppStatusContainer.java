package org.kobbigal.sisenselogreader.views.status;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class AppStatusContainer extends VBox {

    private static AppStatusContainer instance;
    private TextArea appRunHistory = new TextArea();
    private ProgressBar progressBar = new ProgressBar();

    public static AppStatusContainer getInstance() {

        if (instance == null) {
            instance = new AppStatusContainer();
        }
        return instance;
    }

    private AppStatusContainer(){

        appRunHistory.setEditable(false);

        progressBar.setPrefWidth(300);
        appRunHistory.setPrefWidth(300);

        this.setAlignment(Pos.CENTER);
        Label statusLabel = new Label("Status");
        this.getChildren().addAll(statusLabel, progressBar, appRunHistory);
    }

    public void setAppRunHistory(String message) {
        appRunHistory.appendText(message);
        appRunHistory.appendText("\n");
    }

    public void bindProgressBar(ReadOnlyDoubleProperty property) {
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(property);
    }
}
