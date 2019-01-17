package org.kobbigal.sisenselogreader.views.filters;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.kobbigal.sisenselogreader.controllers.FiltersSelectionController;
import org.kobbigal.sisenselogreader.model.Log;

public class FiltersContainer extends VBox {

    private SourceListContainer sourceListContainer = new SourceListContainer();
    private VerbosityListContainer verbosityListContainer = new VerbosityListContainer();
    private ComponentSearchboxContainer componentSearchboxContainer = new ComponentSearchboxContainer();
    private DetailsSearchboxContainer detailsSearchboxContainer = new DetailsSearchboxContainer();
    private FiltersSelectionController filtersSelectionController = new FiltersSelectionController(this);

    public FiltersContainer() {

        this.setSpacing(0);
        this.setPadding(new Insets(5));

        Button filterBtn = new Button("Apply");
        filterBtn.setOnAction(event -> filtersSelectionController.filter());

        this.getChildren().addAll(
                sourceListContainer,
                verbosityListContainer,
                componentSearchboxContainer,
                detailsSearchboxContainer,
                filterBtn
        );

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
