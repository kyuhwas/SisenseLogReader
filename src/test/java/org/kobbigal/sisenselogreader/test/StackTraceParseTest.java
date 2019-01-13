package org.kobbigal.sisenselogreader.test;

import org.kobbigal.sisenselogreader.parsers.MicroServicesLogParser;
import org.kobbigal.sisenselogreader.workers.LogFileReader;

import java.io.File;

public class StackTraceParseTest {

    public static void main(String[] args) {
        LogFileReader logFileReader = new LogFileReader(new File("/Users/kobbigal/temp/SNS-12621/application-logs/galaxy/microservice_stacktrace.log"));
        MicroServicesLogParser microServicesLogParser = new MicroServicesLogParser(logFileReader.getContent(), "galaxy") ;
    }


}
