package org.kobbigal.sisenselogreader.workers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogFileReader {

    private File file;
    private List<String> content;

    LogFileReader(File file) {

        this.file = file;
        read();

    }

    private void read(){

        System.out.println("Started reading file " + file.getName());

        try(Stream<String> stream = Files.lines(this.file.toPath(), StandardCharsets.ISO_8859_1)) {

            setContent(
                    stream
                        .filter(line -> !line.isEmpty())
//                        .filter(line -> Character.isDigit(line.charAt(0)))
                        .collect(Collectors.toList()));


            System.out.println("Finished reading file");

        } catch(IOException e){
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't read file " + file.getAbsolutePath() + "\n" + e.getMessage(), ButtonType.CLOSE);
                alert.showAndWait();
            });
        }
    }

    private void setContent(List<String> content) {
        this.content = content;
    }

    List<String> getContent() {

        System.out.println("Number of logs returned " + content.size());
        return content;
    }
}
