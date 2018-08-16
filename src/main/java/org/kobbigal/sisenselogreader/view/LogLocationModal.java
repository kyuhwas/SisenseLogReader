package org.kobbigal.sisenselogreader.view;

import javafx.stage.Stage;

public class LogLocationModal extends Stage {

    private String ecsPath;
    private String iisnodePath;
    private String prismwebserverPath;


    private LogLocationModal(String ecsPath, String iisnodePath, String prismwebserverPath){
        this.ecsPath = ecsPath;
        this.iisnodePath = iisnodePath;
        this.prismwebserverPath = prismwebserverPath;
    }

    public LogLocationModal() {
        super();
    }

    public void load(){

        LogLocationModal modal = new LogLocationModal(getEcsPath(), getIisnodePath(), getPrismwebserverPath());

        modal.show();

    }

    @Override
    public String toString() {
        return super.toString();
    }

    private String getEcsPath() {
        return ecsPath;
    }

    public void setEcsPath(String ecsPath) {
        this.ecsPath = ecsPath;
    }

    private String getIisnodePath() {
        return iisnodePath;
    }

    public void setIisnodePath(String iisnodePath) {
        this.iisnodePath = iisnodePath;
    }

    private String getPrismwebserverPath() {
        return prismwebserverPath;
    }

    public void setPrismwebserverPath(String prismwebserverPath) {
        this.prismwebserverPath = prismwebserverPath;
    }
}
