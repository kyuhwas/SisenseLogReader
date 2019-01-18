package org.kobbigal.sisenselogreader.views.filters;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.kobbigal.sisenselogreader.model.Log;

public interface IListFilter {

    void setList(FilteredList<Log> logList);
    void selectAll();
    void clearList();
    ObservableList<String> getList();

}
