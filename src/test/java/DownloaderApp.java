package test.java;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;


public class DownloaderApp extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }

    private Parent createContent() {

        VBox root = new VBox();
        root.setPrefSize(400, 200);

        TextField fieldURL = new TextField();
        fieldURL.setPromptText("Enter url of file to download");
        fieldURL.setOnAction(event -> {

            if (!fieldURL.getText().isEmpty()){
                Task<Void> downloadTask = new DownloadTask(fieldURL.getText());
                ProgressBar progressBar = new ProgressBar();
                progressBar.setPrefWidth(350);
                progressBar.progressProperty().bind(downloadTask.progressProperty());
                root.getChildren().add(progressBar);

                fieldURL.clear();

                Thread thread = new Thread(downloadTask);
                thread.setDaemon(true);
                thread.start();
            }
        });



        root.getChildren().addAll(fieldURL);

        return root;

    }

    public class DownloadTask extends Task<Void>{

        private String url;

        DownloadTask(String url){
            this.url = url;
        }

        @Override
        protected Void call() throws Exception {

            String extention = url.substring(url.lastIndexOf(".", url.length()));
            URLConnection connection = new URL(url).openConnection();
            long fileLength = connection.getContentLengthLong();

            try (InputStream is = connection.getInputStream();
                 OutputStream os = Files.newOutputStream(Paths.get("downloadedfile" + extention))){

                long nread = 0L;
                byte[] buf = new byte[8192];
                int n;
                while ((n = is.read(buf)) > 0) {
                    os.write(buf, 0, n);
                    nread += n;
                    updateProgress(nread, fileLength);
                }
            }

            return null;
        }

        @Override
        protected void failed() {
            System.out.println("Download failed");
        }

        @Override
        protected void succeeded() {
            System.out.println("Downloaded file");
        }
    }

}
