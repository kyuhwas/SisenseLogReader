package org.kobbigal.sisenselogreader.version;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class VersionRetriever {

    public static String getVersion(){

        Process process = null;
        try {
            process = Runtime.getRuntime().exec("reg QUERY HKEY_LOCAL_MACHINE\\SOFTWARE\\Sisense\\ECS /v Version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringWriter stringWriter = new StringWriter();


        int c;
        if (process != null) {
            try (InputStream is = process.getInputStream()) {
                while((c = is.read()) != -1){
                    stringWriter.write(c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return extractVersionFromRegistry(stringWriter.toString().trim());
    }

    private static String extractVersionFromRegistry(String s){

        return s.split("    ")[3];
    }
}
