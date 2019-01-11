package org.kobbigal.sisenselogreader.views.filters;

import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import org.kobbigal.sisenselogreader.model.Log;

public class FiltersContainer extends VBox {

    private FilteredList<Log> filteredList;
    private SourceListContainer sourceListContainer = new SourceListContainer();
    private VerbosityListContainer verbosityListContainer = new VerbosityListContainer();
    private ComponentSearchboxContainer componentSearchboxContainer = new ComponentSearchboxContainer();
    private DetailsSearchboxContainer detailsSearchboxContainer = new DetailsSearchboxContainer();

    public FiltersContainer(FilteredList<Log> filteredList) {

        this.setSpacing(0);
        this.setPadding(new Insets(5));
        this.filteredList = filteredList;

        sourceListContainer.setList(filteredList);
        verbosityListContainer.setList(filteredList);

        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() ->
                sourceListContainer.getFilter()
                        .and(verbosityListContainer.getFilter())
                        .and(detailsSearchboxContainer.getFilter())
                        .and(componentSearchboxContainer.getFilter())
                ));

        this.getChildren().addAll(
                sourceListContainer,
                verbosityListContainer,
                componentSearchboxContainer,
                detailsSearchboxContainer
        );

    }

    public void setVerbosityList() {
        verbosityListContainer.setList(filteredList);
    }

    public void setSourcesList(){
        sourceListContainer.setList(filteredList);
    }
}
