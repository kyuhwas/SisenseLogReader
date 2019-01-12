package org.kobbigal.sisenselogreader.test;

import org.kobbigal.sisenselogreader.model.LogFile;
import org.kobbigal.sisenselogreader.model.LogPaths;
import org.kobbigal.sisenselogreader.workers.LogFileReader;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainTest {

    public static void main(String[] args) {

//        LogFileReader logFileReader = new LogFileReader(new File("/Users/kobbigal/Downloads/traceroute 2.txt"));
//
//
//        new Thread(() -> {
//
//            List<String> content = logFileReader.getContent();
//
//                }).start();


        LogPaths logPaths = new LogPaths();
        for (LogFile logFile : logPaths.getLogFileList()){
            System.out.println(logFile);
        }

    }


}
