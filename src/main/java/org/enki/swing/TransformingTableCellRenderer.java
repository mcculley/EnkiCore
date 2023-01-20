package org.enki.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.function.Function;

public class TransformingTableCellRenderer<T, R> extends DefaultTableCellRenderer {

    private final Function<T, R> transformer;

    public TransformingTableCellRenderer(final Function<T, R> transformer) {
        this.transformer = transformer;
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
                                                   final boolean cellHasFocus, final int row, final int column) {
        return super.getTableCellRendererComponent(table, transformer.apply((T) value), isSelected, cellHasFocus, row,
                column);
    }

}
