package org.enki.swing;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.function.Function;

public class TransformingTreeCellRenderer<T, R> extends DefaultTreeCellRenderer {

    private final Function<T, R> transformer;

    public TransformingTreeCellRenderer(final Function<T, R> transformer) {
        this.transformer = transformer;
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel,
                                                  final boolean expanded, final boolean leaf, int row,
                                                  final boolean hasFocus) {
        return super.getTreeCellRendererComponent(tree, transformer.apply((T) value), sel, expanded, leaf, row,
                hasFocus);
    }

}