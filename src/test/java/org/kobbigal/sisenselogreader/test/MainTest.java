package org.kobbigal.sisenselogreader.test;

import org.kobbigal.sisenselogreader.workers.LogFileReader;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainTest {

    public static void main(String[] args) {

        try {
            LogFileReader logFileReader = new LogFileReader(new File("/Users/kobbigal/temp/traceroute.txt"));


            new Thread(() -> {

                List<String> content = logFileReader.getContent();


                    }).start();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
