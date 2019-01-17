package org.kobbigal.sisenselogreader.views.status;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.kobbigal.sisenselogreader.views.RootLayout;

public class AppStatusContainer extends VBox {

    private static AppStatusContainer instance;
    private TextArea appRunHistory = new TextArea();
    private ProgressBar progressBar = new ProgressBar();
    private Button hideContainerBtn;

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
        this.setPrefHeight(900);
        this.setPrefWidth(320);
        Label statusLabel = new Label("Progress");
        statusLabel.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));

        hideContainerBtn = new Button("Hide");
        hideContainerBtn.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));
        hideContainerBtn.setOnAction(event -> RootLayout.getInstance().setRight(null));

        this.getChildren().addAll(statusLabel, progressBar, appRunHistory, hideContainerBtn);
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
