package org.kobbigal.sisenselogreader.test;

import org.kobbigal.sisenselogreader.model.LogPaths;
import org.kobbigal.sisenselogreader.version.VersionRetriever;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class LogDirectories {

    public static void main(String[] args) throws IOException {

        String version = VersionRetriever.getVersion();
        LogPaths logPathsInstance = new LogPaths(version);
        List<Path> paths = logPathsInstance.returnListOfLogPaths();

        Path ecs = null;
        Path prismWebServer = null;
        for(Path path: paths){
//            System.out.println(path);

            if (path.getFileName().toString().equals("PrismServerLogs")){
                ecs = path;
            }

            if (path.getFileName().toString().equals("Logs")){
                prismWebServer = path;
            }
        }

        System.out.println(ecs);
        System.out.println(prismWebServer);

    }

}
