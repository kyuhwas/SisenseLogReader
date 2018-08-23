package org.kobbigal.sisenselogreader.views;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.kobbigal.sisenselogreader.model.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class FiltersContainer extends VBox {

    private final Insets PADDING = new Insets(5,5,5,5);
    private final int SPACING = 5;
    private final int FONT_SIZE = 16;
    private final int FILTER_HEIGHT = 26;
    private final String SOURCES_STR = "Sources";
    private final String VERBOSITY_STR = "Log Level";
    private final String DETAILS_STR = "Details";
    private final String COMPONENT_STR = "Components";
    private final String FONT_FAMILY = "Agency FB";
    private final VBox sourceListContainer = new VBox(SPACING);
    private final VBox verbosityListContainer = new VBox(SPACING);
    private final VBox detailsSearchContainer = new VBox(SPACING);
    private final VBox componentSearchContainer = new VBox(SPACING);
    private final ObjectProperty<Predicate<Log>> sourceFilter = new SimpleObjectProperty<>();
    private final ObjectProperty<Predicate<Log>> verbosityFilter = new SimpleObjectProperty<>();
    private final ObjectProperty<Predicate<Log>> detailsSearchFilter = new SimpleObjectProperty<>();
    private final ObjectProperty<Predicate<Log>> componentSearchFilter = new SimpleObjectProperty<>();
    private final Label sourceLabel = new Label(SOURCES_STR);
    private final Label verbosityLabel = new Label(VERBOSITY_STR);
    private final Label componentLabel = new Label(COMPONENT_STR);
    private final Label detailsLabel = new Label(DETAILS_STR);
    private final Tooltip tooltip = new Tooltip("Hold CMD/CTRL to select multiple values");

    public FiltersContainer(ListView<String> sourcesListView, ListView<String> verbosityListView) {
        super();
        this.setPadding(PADDING);

        initializeSourceFilter(verbosityListView);
        verbosityLabel.setFont(Font.font("FONT_FAMILY", FontWeight.BOLD, FONT_SIZE));
        componentLabel.setFont(Font.font("FONT_FAMILY", FontWeight.BOLD, FONT_SIZE));
        detailsLabel.setFont(Font.font("FONT_FAMILY", FontWeight.BOLD, FONT_SIZE));

    }

    private void initializeSourceFilter(final ListView<String> list){
        sourceLabel.setFont(Font.font("FONT_FAMILY", FontWeight.BOLD, FONT_SIZE));
        list.setPrefHeight(list.getItems().size() * FILTER_HEIGHT);
        list.setTooltip(tooltip);
        ObjectBinding<Predicate<Log>> sourcesObjectBinding = new ObjectBinding<Predicate<Log>>() {
            private final Set<String> srcs = new HashSet<>();
            @Override
            protected Predicate<Log> computeValue() {
                return log -> srcs.contains(log.getSource().toLowerCase());
            }
        };
        sourceFilter.bind(sourcesObjectBinding);
    }

    private void initializeVerbosityFilter(final ListView<String > list){
        verbosityLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE));
        list.setPrefHeight(list.getItems().size() * FILTER_HEIGHT);
        list.setTooltip(tooltip);
        ObjectBinding<Predicate<Log>> verbosityObjectBinding = new ObjectBinding<Predicate<Log>>() {
            private final Set<String> verbosityStrs = new HashSet<>();
            {
                list.getSelectionModel().getSelectedItems().addListener((ListChangeListener<String>) c -> {
                    boolean changed = false;

                    while (c.next()){
                        if (c.wasRemoved()){
                            changed = true;
                            c.getRemoved().stream().map(String::toLowerCase).forEach(verbosityStrs::remove);
                        }
                        if (c.wasAdded()){
                            changed = true;

                            try {
                                c.getAddedSubList().stream().map(String::toLowerCase).forEach(verbosityStrs::add);
                            }
                            catch (IndexOutOfBoundsException ignored){

                            }
                        }
                    }
                    if (changed){
                        invalidate();
                    }
                });
            }
            @Override
            protected Predicate<Log> computeValue() {
                return log -> verbosityStrs.contains(log.getVerbosity().toLowerCase());
            }
        };
        verbosityFilter.bind(verbosityObjectBinding);
    }
}
