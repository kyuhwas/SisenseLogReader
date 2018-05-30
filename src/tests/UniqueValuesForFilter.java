package tests;

import classes.Log;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class UniqueValuesForFilter {

    public static void main(String[] args) {

        List<Log> logList = new ArrayList<>();

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

        for (int i = 0; i < 50; i++){

            Date randomDate = new Date(ThreadLocalRandom.current().nextLong(d1.getTime(), d2.getTime()));
            int randomSource = random.nextInt(sources.size());
            int randomVerbosity = random.nextInt(verbosity.size());

            //Log l = new Log(sources.get(randomSource), randomDate, verbosity.get(randomVerbosity), "component" + i, "details " + i);
//            System.out.println(l);
            //logList.add(l);
        }

        verbositySet(logList);

    }

    private static Set<String> verbositySet(List<Log> logs){

        List<String> list = new ArrayList<>();


        for (Log l : logs) {
            list.add(l.getVerbosity());
        }

        Set<String> set = new HashSet<>(list);
        System.out.println(set);

        return set;

    }

}
