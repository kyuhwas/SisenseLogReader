package org.kobbigal.sisenselogreader.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.kobbigal.sisenselogreader.views.status.AppStatusContainer;
import org.kobbigal.sisenselogreader.workers.ReadParseLogTask;

import java.util.Date;

public class MainTest extends Application {

    public static void main(String[] args) {

        launch(args);

    }


    @Override
    public void start(Stage primaryStage){

        AppStatusContainer appStatusContainer = AppStatusContainer.getInstance();

        Date startTime = new Date(1546732800000L);
        Date endTime = new Date();

        primaryStage.setScene(new Scene(new BorderPane(appStatusContainer), 400, 500));

        ReadParseLogTask readFiles = new ReadParseLogTask(startTime, endTime);

        appStatusContainer.bindProgressBar(readFiles.progressProperty());
        Thread thread = new Thread(readFiles);
        thread.setDaemon(true);
        thread.start();

        primaryStage.show();
    }
}
