import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {


    public static void main(String[] args) {

        File[] fls = new File(Paths.get("C:\\ProgramData\\Sisense\\PrismServer\\PrismServerLogs\\").normalize().toString()).listFiles();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("\\[(.*?)]");


        for (File f : fls) {
            if (f.getName().contains("ECS.log")) {
                Path path = Paths.get(f.getAbsolutePath());
                System.out.println(path.normalize().toString());

                List<String> lines;

                try(Stream<String> stream = Files.lines(path, StandardCharsets.ISO_8859_1)) {

                    lines = stream
                            .filter(line -> !line.isEmpty())
                            .filter(line -> Character.isDigit(line.charAt(0)))
                            .collect(Collectors.toList());

//                    System.out.println(lines.toArray()[0].toString());
                    Matcher matcher = pattern.matcher(lines.toArray()[0].toString());

                    int i = 0;
                    while (matcher.find()){

                        System.out.println(matcher.group(1));

                    }

//                    System.out.println("First line:");
//                    System.out.println(sdf.parse(matcher.group(1)));
//                    System.out.println(lines.toArray()[0].toString());
//
//                    System.out.println("Last line:");
//                    System.out.println(lines.toArray()[lines.size()-1].toString());

                } catch (IOException e) {
                    e.printStackTrace();
                }
// catch (ParseException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }
}


/*
if (log.trim().startsWith("at") || log.startsWith("]") || log.startsWith("A")){
            return null;
        }

        Log l = new Log();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(log);

        int i = 0;

        while (matcher.find()){
            l.setSource("ECS");
            switch (i){
                case 0:
                    try {
                        l.setTime(sdf.parse(matcher.group(1)));
                    } catch (ParseException ignored) {
                    }
                    break;
                case 3:
                    l.setVerbosity(matcher.group(1));
                    break;
                case 4:
                    l.setComponent(matcher.group(1));
                    break;
                case 5:
                    l.setDetails(matcher.group(1));
                    break;
            }
        i++;
        }
         return l;
 */