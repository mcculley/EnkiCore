package org.enki.swing;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class TransformingListCellRenderer<T> extends DefaultListCellRenderer {

    private final Function<T, String> transformer;

    public TransformingListCellRenderer(final Function<T, String> transformer) {
        this.transformer = transformer;
    }

    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index,
                                                  final boolean isSelected, final boolean cellHasFocus) {
        return super.getListCellRendererComponent(list, transformer.apply((T) value), index, isSelected, cellHasFocus);
    }

}
