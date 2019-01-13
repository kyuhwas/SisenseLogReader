package org.kobbigal.sisenselogreader.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Date;

public class Log implements Comparable<Log>{

//    private IntegerProperty timeRunning;
    private StringProperty source;
    private Date time;
    private StringProperty verbosity;
    private StringProperty component;
    private StringProperty details;

    public Log() {
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getSource() {
        return sourceProperty().get();
    }

    private StringProperty sourceProperty() {
        if (source == null) source = new SimpleStringProperty(this, "source");
        return source;
    }

    public void setSource(String source) {
        sourceProperty().set(source);
    }

    private Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getVerbosity() {
        return verbosityProperty().get();
    }

    private StringProperty verbosityProperty() {
        if (verbosity == null) verbosity = new SimpleStringProperty(this, "verbosity");
        return verbosity;
    }

    public void setVerbosity(String verbosity) {
        verbosityProperty().set(verbosity);
    }

    public String getComponent() {
        return componentProperty().get();
    }

    private StringProperty componentProperty() {
        if (component == null) component = new SimpleStringProperty(this, "component");
        return component;
    }

    public void setComponent(String component) {
        componentProperty().set(component);
    }

    public String getDetails() {
        return detailsProperty().get();
    }

    private StringProperty detailsProperty() {
        if (details == null) details = new SimpleStringProperty(this, "details");
        return details;
    }

    public void setDetails(String details) {
        detailsProperty().set(details);
    }

    @Override
    public int compareTo(Log o) {
        if (getTime() == null || o.getTime() == null)
            return 0;
        return getTime().compareTo(o.getTime());
    }

    @Override
    public String toString() {
        return "Log: {\n" +
//                "\n\ttimeRunning: " + (timeRunning != null ? timeRunning.get() : "") +
                "\n\tSource: " + (source != null ? source.get() : "") +
                "\n\tTime: " + (time != null ? time : "") +
                "\n\tLog Level: " + (verbosity != null ? verbosity.get() : "") +
                "\n\tClass: " + (component != null ? component.get() : "") +
                "\n\tDetails: " + (details != null ? details.get() : "") +
                "\n}"
                ;
    }

//    public int getTimeRunning() {
//        return timeRunning.get();
//    }
//
//    public IntegerProperty timeRunningProperty() {
//        if (timeRunning == null) timeRunning = new SimpleIntegerProperty(this, "timeRunning");
//        return timeRunning;
//    }
//
//    public void setTimeRunning(int timeRunning) {
//        timeRunningProperty().set(timeRunning);
//    }
}
