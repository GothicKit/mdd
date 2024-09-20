// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.gui;

import com.formdev.flatlaf.icons.FlatAscendingSortIcon;
import com.formdev.flatlaf.icons.FlatFileViewFileIcon;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import dev.gothickit.mdd.cst.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class GuiSymbolsView extends JPanel {
    private static final DefaultMutableTreeNode ROOT = new DefaultMutableTreeNode("Empty", true);
    private final JTextField searchField;
    private final JComboBox<String> filterField;
    private final JTree symbolTree;
    private final boolean hideGeneratedSymbols = true;
    private List<@NotNull Decl> symbols = List.of();
    private String scriptName = "Empty";

    public GuiSymbolsView(@NotNull Consumer<@NotNull Decl> onClick) {
        super();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        setMinimumSize(new Dimension(0, 0));
        setPreferredSize(new Dimension(300, Integer.MAX_VALUE));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        searchField.setMinimumSize(new Dimension(0, 20));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                GuiSymbolsView.this.filter(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                GuiSymbolsView.this.filter(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        JLabel searchLabel = new JLabel("Search", new FlatSearchIcon(), JLabel.LEFT);
        searchLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 0));

        add(searchLabel);
        add(searchField);

        filterField = new JComboBox<>(new String[]{"All", "Functions", "Classes", "Prototypes", "Instances", "Variables", "Constants"});
        filterField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        filterField.setMinimumSize(new Dimension(0, 20));
        filterField.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            this.filter(searchField.getText());
        });

        JLabel filterLabel = new JLabel("Filter", new FlatAscendingSortIcon(), JLabel.LEFT);
        filterLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 0));

        add(filterLabel);
        add(filterField);

        symbolTree = new JTree(ROOT);
        symbolTree.setEditable(false);
        symbolTree.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        symbolTree.setMinimumSize(new Dimension(0, 0));
        symbolTree.setShowsRootHandles(true);
        symbolTree.setCellRenderer(new CstTreeCellRenderer());

        symbolTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                var node = (DefaultMutableTreeNode) symbolTree.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }

                if (!(node.getUserObject() instanceof Decl decl)) {
                    return;
                }

                onClick.accept(decl);
            }
        });

        JScrollPane scrollPane = new JScrollPane(symbolTree);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        scrollPane.setMinimumSize(new Dimension(0, 0));

        JLabel treeLabel = new JLabel("Symbols", new FlatFileViewFileIcon(), JLabel.LEFT);
        treeLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 0));
        add(treeLabel);
        add(scrollPane);
    }

    public void reset(final @NotNull List<@NotNull Decl> symbols, String scriptName) {
        this.symbols = symbols;
        this.scriptName = scriptName;

        this.searchField.setText("");
        this.filter("");
    }

    private boolean isSymbolShown(@NotNull Decl decl) {
        String filter = (String) filterField.getSelectedItem();
        if (filter.equals("All")) {
            return true;
        }

        return (decl instanceof InstanceDecl && filter.equals("Instances")) ||
                (decl instanceof ClassDecl && filter.equals("Classes")) ||
                (decl instanceof FunctionDecl && filter.equals("Functions")) ||
                (decl instanceof PrototypeDecl && filter.equals("Prototypes")) ||
                (decl instanceof VariableDecl v0 && !(decl instanceof InstanceDecl) && v0.getValue() == null && filter.equals("Variables")) ||
                (decl instanceof VariableDecl v1 && !(decl instanceof InstanceDecl) && v1.getValue() != null && filter.equals("Constants"));
    }

    private void filter(@NotNull String text) {
        ROOT.removeAllChildren();
        ROOT.setUserObject(this.scriptName);

        text = text.toUpperCase();
        for (Decl decl : this.symbols) {
            if (!isSymbolShown(decl)) {
                continue;
            }

            // Hide generated symbols if configured.
            if (hideGeneratedSymbols && decl.getSymbol().isGenerated()) {
                continue;
            }

            // Only show objects matching the filter
            if (!decl.getName().contains(text)) {
                continue;
            }

            var node = new DefaultMutableTreeNode(decl);
            ROOT.add(node);

            // If the symbol is a scope, also add its children
            if (decl instanceof Scope scope) {
                for (var member : scope.getMembers()) {
                    node.add(new DefaultMutableTreeNode(member));
                }
            }
        }

        this.symbolTree.updateUI();
    }

    public void clear() {
        ROOT.removeAllChildren();
        ROOT.setUserObject("Empty");
        symbols = List.of();
    }

    private static class CstTreeCellRenderer extends DefaultTreeCellRenderer {
        private final JLabel label = new JLabel();

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof Decl decl) {
                label.setIcon(GuiIcons.getIcon(decl));
                label.setText(decl.getName());
            } else {
                label.setIcon(null);
                label.setText(value.toString());
            }
            return label;
        }
    }
}
