package org.kobbigal.sisenselogreader.model;

import java.io.File;

public class LogFile {

    private String source;
    private File file;

    public LogFile(String source, File file){
        this.source = source;
        this.file = file;
    }

    public String getSource() {
        return source;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "LogFile{" +
                "source='" + source + '\'' +
                ", file=" + file +
                '}';
    }
}
