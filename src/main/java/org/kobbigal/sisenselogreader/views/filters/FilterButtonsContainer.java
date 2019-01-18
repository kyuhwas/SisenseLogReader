package org.kobbigal.sisenselogreader.views.filters;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FilterButtonsContainer extends HBox {

    private static FilterButtonsContainer instance;
    private Button applyFilters;
    private Button clearFilters;

    public static FilterButtonsContainer getInstance() {
        if (instance == null){
            instance = new FilterButtonsContainer();
        }
        return instance;
    }

    private FilterButtonsContainer(){

        applyFilters = new Button("Apply");
        clearFilters = new Button("Clear");

        applyFilters.setOnAction(event -> FiltersContainer.getInstance().getFiltersSelectionController().filter());
        applyFilters.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));
        applyFilters.setDisable(true);

        clearFilters.setOnAction(event -> {
            FiltersContainer.getInstance().getComponentSearchboxContainer().clearSearchFieldText();
            FiltersContainer.getInstance().getDetailsSearchboxContainer().clearSearchFieldText();
            FiltersContainer.getInstance().getFiltersSelectionController().filter();
        });

        clearFilters.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));
        clearFilters.setDisable(true);

        this.setSpacing(10);
        this.getChildren().addAll(applyFilters, clearFilters);
    }

    void disableApplyFiltersButton(){
        applyFilters.setDisable(true);
    }

    void disableClearFiltersButton() {
        clearFilters.setDisable(true);
    }

    void enableApplyFiltersButton(){
        applyFilters.setDisable(false);
    }

    void enableClearFiltersButton(){
        clearFilters.setDisable(false);
    }
}
