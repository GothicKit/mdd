// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.gui;

import dev.gothickit.mdd.cst.Decl;
import dev.gothickit.mdd.decompiler.Decompiler;
import dev.gothickit.mdd.output.HtmlBuilder;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.util.function.Consumer;

public class GuiDecompilerView extends JEditorPane {
    public GuiDecompilerView(Consumer<Integer> onClick) {
        super();
        setEditable(false);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        setMinimumSize(new Dimension(0, 0));
        setEditorKit(new HTMLEditorKit());

        addHyperlinkListener(event -> {
            if (event.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
                return;
            }

            int id = Integer.parseInt(event.getDescription().substring(1));
            onClick.accept(id);
        });
    }

    public void setSymbol(Decl decl, Decompiler decompiler) {
        var builder = new HtmlBuilder();
        builder.commentMultiline(decompiler.formatSymbolInfo(decl)).newline().syntax(decl).text(";");
        setText(builder.toString());
    }

    public void clear() {
        setText("");
    }
}
