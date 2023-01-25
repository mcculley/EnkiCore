package org.enki.swing;

import org.enki.core.Predicates;

import javax.swing.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilteredList<T extends Comparable<? super T>> extends AbstractListModel<T> {

    private final List<T> allElements;
    private final Comparator<T> comparator;
    private List<T> filteredElements = Collections.emptyList();

    public FilteredList(final List<T> allElements) {
        this(allElements, Comparator.naturalOrder(), Predicates.alwaysTrue());
    }

    public FilteredList(final List<T> allElements, final Comparator<T> comparator) {
        this(allElements, comparator, Predicates.alwaysTrue());
    }

    public FilteredList(final List<T> allElements, final Predicate<T> filter) {
        this(allElements, Comparator.naturalOrder(), filter);
    }

    public FilteredList(final List<T> allElements, final Comparator<T> comparator, final Predicate<T> filter) {
        this.allElements = allElements;
        this.comparator = comparator;
        setFilter(filter);
    }

    public List<T> getAllElements() {
        return allElements;
    }

    @Override
    public int getSize() {
        return filteredElements.size();
    }

    @Override
    public T getElementAt(final int index) {
        return filteredElements.get(index);
    }

    public void setFilter(final Predicate<T> filter) {
        filteredElements = allElements.stream().filter(filter).sorted(comparator).collect(Collectors.toList());
        fireContentsChanged(this, 0, filteredElements.size() - 1);
    }

}