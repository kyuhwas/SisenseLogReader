package org.kobbigal.sisenselogreader.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogMatcher {

    public static void main(String[] args) {

        String filename = "C:\\ProgramData\\Sisense\\application-logs\\galaxy\\galaxy.log";
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("\\[(.*?)]");

        List<String> lines = new ArrayList<>();
        try(Stream<String> stream = Files.lines(Paths.get(filename))){

            lines = stream.collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String line : lines){
            System.out.println("\n" + line + "\n");
            Matcher matcher = pattern.matcher(line);

            int i = 0;
            while (matcher.find()){
                switch (i){
                    case 0:
                        System.out.println(i + ":" + matcher.group(1));
                        break;
                    case 1:
                        System.out.println(i + ":" + matcher.group(1));
                        break;
                    case 2:
                        System.out.println(i + ":" + matcher.group(1));
                        break;
                    case 3:
                        System.out.println(i + ":" + matcher.group(1));
                        break;
                    case 4:
                        System.out.println(i + ":" + matcher.group(1));
                        break;
                }
                i++;
            }

        }


    }



}
