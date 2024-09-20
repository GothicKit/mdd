// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.gui;

import dev.gothickit.mdd.cst.Decl;
import dev.gothickit.zenkit.daedalus.DaedalusScript;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GuiDisassemblerView extends JPanel {
    private final JTable table;

    public GuiDisassemblerView() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        table = new JTable(new Object[][]{}, new Object[]{"Address", "Bytes", "Disassembly", "Comment"});
        table.setShowGrid(false);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setDragEnabled(false);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        table.setDefaultRenderer(Object.class, new MonospaceCellRenderer());

        this.add(table.getTableHeader());
        this.add(new JScrollPane(table));
    }

    public void setSymbol(final @NotNull DaedalusScript script, final @NotNull Decl decl) {
        var model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Address", "Bytes", "Disassembly", "Comment"});

        var symbol = decl.getSymbol();
        if (symbol.getAddress() < 1 || decl.getSymbol().isExternal()) {
            table.setModel(model);
            table.updateUI();
            return;
        }

        var offset = symbol.getAddress();
        var after = offset;
        var exit = false;

        var buffer = ByteBuffer.allocate(6);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        while (!exit) {
            var i = script.getInstruction(offset);

            var dis = i.op().name();
            var cmnt = "";
            var off = String.format("%-20x", offset);

            offset += i.size;

            buffer.rewind();
            buffer.put((byte) i.op_);

            switch (i.op()) {
                case ADD, SUB, MUL, DIV, MOD, OR, ANDB, LT, GT, ORR, AND, LSL, LSR, LTE, EQ, NEQ, GTE, MOVI, ADDMOVI,
                     SUBMOVI, MULMOVI, DIVMOVI, PLUS, NEGATE, NOT, CMPL, MOVS, MOVSS, MOVVF, MOVF, MOVVI, NOP -> {
                    dis = i.op().name();
                }
                case RSR -> {
                    if (offset > after) {
                        exit = true;
                    }
                }
                case BL -> {
                    dis += " 0x" + Integer.toHexString(i.data);

                    var sym = script.getSymbolByAddress(i.data);
                    if (sym != null) {
                        cmnt = sym.getName();
                    }

                    buffer.putInt(i.data);
                }
                case BE, PUSHV, PUSHVI, GMOVI -> {
                    dis += " 0x" + Integer.toHexString(i.data);

                    var sym = script.getSymbolByIndex(i.data);
                    if (sym != null) {
                        cmnt = sym.getName();
                    }

                    buffer.putInt(i.data);
                }
                case PUSHVV -> {
                    dis += " 0x" + Integer.toHexString(i.data) + " #" + i.index;

                    var sym = script.getSymbolByIndex(i.data);
                    if (sym != null) {
                        cmnt = sym.getName() + "[" + i.index + "]";
                    }

                    buffer.putInt(i.data);
                    buffer.put((byte) i.index);
                }
                case PUSHI -> {
                    dis += " " + i.data;
                    buffer.putInt(i.data);
                }
                case B, BZ -> {
                    dis += " 0x" + Integer.toHexString(i.data);
                    after = Math.max(after, i.data);
                    buffer.putInt(i.data);
                }
            }

            var count = buffer.position();
            byte[] bytes = buffer.array();
            String[] s = new String[count];
            for (int j = 0; j < count; j++) {
                s[j] = String.format("%02x", bytes[j] & 0xFF);
            }

            model.addRow(new Object[]{off, String.join(" ", s), dis, cmnt});
        }

        table.setModel(model);
        table.updateUI();
    }

    public void clear() {
        var model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Address", "Bytes", "Disassembly", "Comment"});
    }

    private static class MonospaceCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            var cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setFont(new Font("Monospaced", Font.PLAIN, 14));
            return cell;
        }
    }
}
