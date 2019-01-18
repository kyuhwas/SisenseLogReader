package org.kobbigal.sisenselogreader.views.filters;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import org.kobbigal.sisenselogreader.controllers.FiltersSelectionController;
import org.kobbigal.sisenselogreader.model.Log;

public class FiltersContainer extends VBox {

    private static FiltersContainer instance;
    private SourceListContainer sourceListContainer = new SourceListContainer();
    private VerbosityListContainer verbosityListContainer = new VerbosityListContainer();
    private ComponentSearchboxContainer componentSearchboxContainer = new ComponentSearchboxContainer();
    private DetailsSearchboxContainer detailsSearchboxContainer = new DetailsSearchboxContainer();
    private FiltersSelectionController filtersSelectionController = new FiltersSelectionController(this);
    private FilterButtonsContainer filterButtonsContainer = FilterButtonsContainer.getInstance();

    public static FiltersContainer getInstance() {
        if (instance == null){
            instance = new FiltersContainer();
        }
        return instance;
    }

    private FiltersContainer() {

        this.setSpacing(0);
        this.setPadding(new Insets(5));


        this.getChildren().addAll(
                sourceListContainer,
                verbosityListContainer,
                componentSearchboxContainer,
                detailsSearchboxContainer,
                filterButtonsContainer
        );

    }

    FiltersSelectionController getFiltersSelectionController() {
        return filtersSelectionController;
    }

    ComponentSearchboxContainer getComponentSearchboxContainer() {
        return componentSearchboxContainer;
    }

    DetailsSearchboxContainer getDetailsSearchboxContainer() {
        return detailsSearchboxContainer;
    }

    SourceListContainer getSourceListContainer() {
        return sourceListContainer;
    }

    VerbosityListContainer getVerbosityListContainer() {
        return verbosityListContainer;
    }

    public void enableFilterButton(){
        filterButtonsContainer.enableApplyFiltersButton();
    }

    public void disableFilterButton(){
        filterButtonsContainer.disableApplyFiltersButton();
    }

    public void disableClearButton(){
        filterButtonsContainer.disableClearFiltersButton();
    }

    public void enableClearButton(){
        filterButtonsContainer.enableClearFiltersButton();
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
