package org.enki.swing;

import javax.swing.*;
import java.util.List;

public class ListListModel<T> extends AbstractListModel<T> {

    private final List<T> list;

    public ListListModel(final List<T> list) {
        this.list = list;
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public T getElementAt(int i) {
        return list.get(i);
    }

}