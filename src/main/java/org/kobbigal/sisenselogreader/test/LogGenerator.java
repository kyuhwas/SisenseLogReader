package org.kobbigal.sisenselogreader.test;

import org.kobbigal.sisenselogreader.model.Log;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LogGenerator {

    private List<Log> logs;
    private static final Date startDate = new Date(1530403200000l);
    public enum LogLevel {DEBUG, INFO, WARN, ERROR, FATAL}
    private final static String[] sources = new String[]{"ECS","IISNode","PrismWebServer"};
    private static final List<LogLevel> VERBOSITY = Collections.unmodifiableList(Arrays.asList(LogLevel.values()));

    private static final int SIZE = VERBOSITY.size();
    private static final int SOURCES = sources.length;
    private static final Random RANDOM = new Random();

    public List<Log> getLogs() {
        return createLogs();
    }

    private static List<Log> createLogs(){

        int numberOfLogs = 50000;

        List<Log> logs = new ArrayList<>();

        for (int i = 0; i < numberOfLogs; i++){

            long random = ThreadLocalRandom.current().nextLong(startDate.getTime(), System.currentTimeMillis());
            Date date = new Date(random);

            Log log = new Log();

            log.setComponent("Application" + i);
            log.setDetails("Details" + i);
            log.setVerbosity(VERBOSITY.get(RANDOM.nextInt(SIZE)).toString());
            log.setTime(date);
            log.setSource(sources[RANDOM.nextInt(SOURCES)]);

            logs.add(log);

        }

        return logs;
    }

}