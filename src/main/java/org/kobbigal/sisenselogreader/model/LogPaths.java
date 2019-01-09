package org.kobbigal.sisenselogreader.model;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LogPaths {

    private String sisenseVersion;
    private static final Path PRISMWEB_LOGS_PATH = Paths.get("C:\\ProgramData\\Sisense\\PrismWeb\\Logs\\");
    private static final Path ECS_LOG_PATH = Paths.get("C:\\ProgramData\\Sisense\\PrismServer\\PrismServerLogs\\");

    // v 7.2-
    private static final Path IIS_NODE_PATH = Paths.get("C:\\Program Files\\Sisense\\PrismWeb\\vnext\\iisnode\\");

    // v 7.2+
    private static final Path APPLICATION_LOGS = Paths.get("C:\\ProgramData\\Sisense\\application-logs");


    public LogPaths(String sisenseVersion) {
        this.sisenseVersion = sisenseVersion;
    }

    public List<Path> returnListOfLogPaths(){

        List<Path> logPaths = new ArrayList<>();
        logPaths.add(PRISMWEB_LOGS_PATH);
        logPaths.add(ECS_LOG_PATH);

        if (this.sisenseVersion.startsWith("7.2")){
            File file = APPLICATION_LOGS.toFile();
            String[] directories = file.list((dir, name) -> new File(dir, name).isDirectory());

            assert directories != null;
            for (String directory : directories) logPaths.add(Paths.get(String.valueOf(Paths.get(APPLICATION_LOGS.toString(), directory))));

        }
        else {
            logPaths.add(IIS_NODE_PATH);
        }

        return logPaths;
    }

    public Path returnLogSource(Path logPath){
        return returnListOfLogPaths().get(returnListOfLogPaths().indexOf(logPath));
    }

}
