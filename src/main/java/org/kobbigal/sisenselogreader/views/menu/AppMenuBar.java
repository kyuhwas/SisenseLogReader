package org.kobbigal.sisenselogreader.views.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import org.kobbigal.sisenselogreader.views.modals.LogLocationModal;

public class AppMenuBar extends MenuBar {

    private final String FILE_MENU_STR = "File";
    private final String SETTINGS_MENU_STR = "Settings";
    private final String HELP_MENU_STR = "Help";

    public AppMenuBar() {

        Menu fileMenu = new Menu(FILE_MENU_STR);
        Menu settingsMenu = new Menu(SETTINGS_MENU_STR);
        Menu helpMenu = new Menu(HELP_MENU_STR);

        MenuItem logLocationMenuItem = new MenuItem("Change log location...");
        logLocationMenuItem.setOnAction(event -> new LogLocationModal());
        settingsMenu.getItems().add(logLocationMenuItem);

        MenuItem aboutMenuItem = new MenuItem("About");
        aboutMenuItem.setOnAction(event -> System.out.println("About option clicked"));
        helpMenu.getItems().add(aboutMenuItem);

        this.getMenus().addAll(fileMenu, settingsMenu, helpMenu);

    }
}
