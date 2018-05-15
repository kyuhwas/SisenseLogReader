import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Log  implements Comparable<Log>{

    private String source;
    private Date time;
    private String verbosity;
    private String component;
    private String details;

    public Log() {
    }

    public Log(String source, Date time, String verbosity, String component, String details) {
        this.source = source;
        this.time = time;
        this.verbosity = verbosity;
        this.component = component;
        this.details = details;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {

        this.time = time;

    }

    public String getVerbosity() {
        return verbosity;
    }

    public void setVerbosity(String verbosity) {
        this.verbosity = verbosity;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Log{" +
                "time=" + time +
                ", verbosity='" + verbosity + '\'' +
                ", component='" + component + '\'' +
                ", details='" + details + '\'' +
                '}';
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    @Override
    public int compareTo(Log o) {
        return getTime().compareTo(o.getTime());
    }
}
