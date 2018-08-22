package org.kobbigal.sisenselogreader.test;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.soap.Text;


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) {

        Button button =  new Button("Submit");
        button.setOnAction(System.out::println);
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu settingMenu = new Menu("Settings");

        MenuItem changeLocation = new MenuItem("Change log location");
        changeLocation.setOnAction(event -> new LocationStage());
        settingMenu.getItems().add(changeLocation);

        menuBar.getMenus().addAll(fileMenu, settingMenu);

        BorderPane rootLayout = new BorderPane(button, menuBar, null, null, null);
        Scene scene = new Scene(rootLayout, 450, 250);

        stage.setScene(scene);
        stage.show();

    }
}

class LocationStage extends Stage{

    private final String title = "Title";
    private Stage stage;
    private Scene scene;
    private BorderPane rootLayout;
    private final int HEIGHT = 250;
    private final int WIDTH = 600;
    private final String IIS_NODE_PATH = "C:\\Program Files\\Sisense\\PrismWeb\\vnext\\iisnode\\";
    private final String PRISMWEB_LOGS_PATH = "C:\\ProgramData\\Sisense\\PrismWeb\\Logs\\";
    private final String ECS_LOG_PATH = "C:\\ProgramData\\Sisense\\PrismServer\\PrismServerLogs\\";


    LocationStage(){

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.minWidth(400);

        Label ecsLabel = new Label("ECS: ");
        GridPane.setRowIndex(ecsLabel, 0);
        GridPane.setColumnIndex(ecsLabel, 0);

        TextField ecsPathField = new TextField();
        ecsPathField.setText(ECS_LOG_PATH);
        GridPane.setRowIndex(ecsPathField, 0);
        GridPane.setColumnIndex(ecsPathField, 1);

        Label iisnodeLabel = new Label("IISNode: ");
        GridPane.setRowIndex(iisnodeLabel, 1);
        GridPane.setColumnIndex(iisnodeLabel, 0);

        TextField iisnodePathField = new TextField();
        iisnodePathField.setText(IIS_NODE_PATH);

        GridPane.setRowIndex(iisnodePathField, 1);
        GridPane.setColumnIndex(iisnodePathField, 1);

        Label webserverLabel = new Label("Web server: ");
        GridPane.setRowIndex(webserverLabel, 2);
        GridPane.setColumnIndex(webserverLabel, 0);

        TextField webserverPathField = new TextField();
        webserverPathField.setText(PRISMWEB_LOGS_PATH);
        GridPane.setRowIndex(webserverPathField, 2);
        GridPane.setColumnIndex(webserverPathField, 1);

        gridPane.getChildren().addAll(ecsLabel, ecsPathField, iisnodeLabel, iisnodePathField, webserverLabel, webserverPathField);
        rootLayout = new BorderPane();
        Button submitBtn = new Button("Save");
        submitBtn.setOnAction(event -> {

            if (pathValidated()){
                System.out.println("Path validated");
                stage.close();
            }

        });


        rootLayout.setBottom(submitBtn);
        rootLayout.setCenter(gridPane);
        BorderPane.setAlignment(submitBtn, Pos.CENTER);
        BorderPane.setAlignment(gridPane, Pos.CENTER);
        stage = new Stage();
        scene = new Scene(rootLayout, WIDTH, HEIGHT);
        stage.setResizable(false);
        stage.setTitle(this.title);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

    }

    private boolean pathValidated() {
        return false;
    }
}
