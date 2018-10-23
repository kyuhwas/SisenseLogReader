package org.kobbigal.sisenselogreader.version;

import javax.xml.stream.util.StreamReaderDelegate;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionRetriever {

    public static String getVersion() throws IOException {

        Process process = null;
        try {
            process = Runtime.getRuntime().exec("reg QUERY HKEY_LOCAL_MACHINE\\SOFTWARE\\Sisense\\ECS /v Version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringWriter stringWriter = new StringWriter();

        InputStream is = process.getInputStream();
        int c;
        while((c = is.read()) != -1){
            stringWriter.write(c);
        }
        is.close();
        return extractVersionFromRegistry(stringWriter.toString());
    }

    private static String extractVersionFromRegistry(String s){

        return s.split("    ")[3];
    }
}
