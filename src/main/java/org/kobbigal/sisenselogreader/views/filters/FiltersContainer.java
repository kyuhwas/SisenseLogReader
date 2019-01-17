package org.kobbigal.sisenselogreader.views.filters;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.kobbigal.sisenselogreader.controllers.FiltersSelectionController;
import org.kobbigal.sisenselogreader.model.Log;

public class FiltersContainer extends VBox {

    private static FiltersContainer instance;
    private SourceListContainer sourceListContainer = new SourceListContainer();
    private VerbosityListContainer verbosityListContainer = new VerbosityListContainer();
    private ComponentSearchboxContainer componentSearchboxContainer = new ComponentSearchboxContainer();
    private DetailsSearchboxContainer detailsSearchboxContainer = new DetailsSearchboxContainer();
    private FiltersSelectionController filtersSelectionController = new FiltersSelectionController(this);
    private Button filterBtn = new Button("Apply");
    private Button clearSearchFieldsBtn = new Button("Clear");

    public static FiltersContainer getInstance() {
        if (instance == null){
            instance = new FiltersContainer();
        }
        return instance;
    }

    private FiltersContainer() {

        this.setSpacing(0);
        this.setPadding(new Insets(5));


        filterBtn.setOnAction(event -> filtersSelectionController.filter());
        filterBtn.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));
        filterBtn.setDisable(true);

        clearSearchFieldsBtn.setFont(Font.font("Agency FB", FontWeight.BOLD, 20));
        clearSearchFieldsBtn.setDisable(true);
        clearSearchFieldsBtn.setOnAction(event -> {
            componentSearchboxContainer.clearSearchFieldText();
            detailsSearchboxContainer.clearSearchFieldText();
            filtersSelectionController.filter();
        });

        this.getChildren().addAll(
                sourceListContainer,
                verbosityListContainer,
                componentSearchboxContainer,
                detailsSearchboxContainer,
                filterBtn,
                clearSearchFieldsBtn
        );

    }

    public void enableClearButton(){
        clearSearchFieldsBtn.setDisable(false);
    }

    public void disableClearButton(){
        clearSearchFieldsBtn.setDisable(true);
    }

    public void enableFilterButton(){
        filterBtn.setDisable(false);
    }

    public void disableFilterButton(){
        filterBtn.setDisable(true);
    }

    public void setFilteredList(FilteredList<Log> filteredList) {
        setVerbosityList(filteredList);
        setSourcesList(filteredList);
        filtersSelectionController.setLogFilteredList(filteredList);
    }

    private void setVerbosityList(FilteredList<Log> filteredList) {
        verbosityListContainer.setList(filteredList);
    }

    private void setSourcesList(FilteredList<Log> filteredList){
        sourceListContainer.setList(filteredList);
    }

    public void clearFilters() {
        verbosityListContainer.clearList();
        sourceListContainer.clearList();
    }

    public ObservableList<String> getSourceListContainerList() {
        return sourceListContainer.getList();
    }

    public ObservableList<String> getVerbosityListContainerList() {
        return verbosityListContainer.getList();
    }

    public String getComponentSearchboxContainerText() {
        return componentSearchboxContainer.getSearchText();
    }

    public String getDetailsSearchboxContainerText() {
        return detailsSearchboxContainer.getText();
    }
}
