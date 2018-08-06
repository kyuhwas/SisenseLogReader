import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.kobbigal.sisenselogreader.model.Log;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class UniqueValuesForFilter {

    public static ObservableList<Log> createRandomLogs(int numOfLogs){

        ObservableList<Log> logList = FXCollections.observableArrayList();

        Date d1 = new Date(1525910400000L);
        Date d2 = new Date(1526169599000L);

        List<String> sources = new ArrayList<>();
        sources.add("ECS");
        sources.add("IISNode");
        sources.add("PrismWebServer");

        List<String> verbosity = new ArrayList<>();
        verbosity.add("DEBUG");
        verbosity.add("INFO");
        verbosity.add("WARN");
        verbosity.add("ERROR");
        verbosity.add("FATAL");

        Random random = new Random();

        for (int i = 0; i < numOfLogs; i++){

            Date randomDate = new Date(ThreadLocalRandom.current().nextLong(d1.getTime(), d2.getTime()));
            int randomSource = random.nextInt(sources.size());
            int randomVerbosity = random.nextInt(verbosity.size());

            Log l = new Log();
            l.setSource(sources.get(randomSource));
            l.setTime(randomDate);
            l.setVerbosity(verbosity.get(randomVerbosity));
            l.setComponent("component" + i);
            l.setDetails("details" + i);
            logList.add(l);
        }

        return logList;



    }

    private static void verbositySet(List<Log> logs){

        List<String> list = new ArrayList<>();


        for (Log l : logs) {
            list.add(l.getVerbosity());
        }

        Set<String> set = new HashSet<>(list);
        System.out.println(set);

    }

}
