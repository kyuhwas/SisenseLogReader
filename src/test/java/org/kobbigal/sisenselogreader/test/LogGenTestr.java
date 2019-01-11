package org.kobbigal.sisenselogreader.test;

import org.kobbigal.sisenselogreader.model.Log;

import java.util.List;

public class LogGenTestr {

    public static void main(String[] args) {

        LogGenerator logGenerator = new LogGenerator();
        List<Log> logs = logGenerator.getLogs();

        for (Log log : logs){
            System.out.println(log);
        }



    }
}
