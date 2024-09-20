// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.output;

import dev.gothickit.mdd.cst.BuiltinDecl;
import dev.gothickit.mdd.cst.Decl;
import dev.gothickit.mdd.cst.Syntax;

import java.util.function.Consumer;

public class HtmlBuilder implements SourceBuilder {
    private final StringBuilder output = new StringBuilder();
    private int indent = 0;

    public HtmlBuilder() {
        output.append("""
                <html><head><style>
                body {font-family: monospaced;}
                .kw {color: #eb4817;}
                .ref {color: white; text-decoration: none;}
                .num {color: #44abff;}
                .str {color: #73d93f;}
                .cmnt {color: #888888;}
                </style></head><body><div>
                """);
    }

    @Override
    public String toString() {
        return output + "</div></body></html>";
    }

    @Override
    public SourceBuilder comment(String comment) {
        output.append("<span class=\"cmnt\">").append("/* ").append(comment).append(" */").append("</span>");
        return this;
    }

    @Override
    public SourceBuilder commentMultiline(String... comment) {
        output.append("<span class=\"cmnt\">").append("/*").append("</span>");

        for (String s : comment) {
            newline();
            output.append("<span class=\"cmnt\">").append(" * ").append(s).append("</span>");
        }

        newline();
        output.append("<span class=\"cmnt\">").append("*/").append("</span>");

        return this;
    }

    @Override
    public HtmlBuilder syntax(Syntax keyword) {
        keyword.getSource(this);
        return this;
    }

    @Override
    public HtmlBuilder number(int value) {
        output.append("<span class=\"num\">").append(value).append("</span>");
        return this;
    }

    @Override
    public HtmlBuilder number(float value) {
        output.append("<span class=\"num\">").append(value).append("</span>");
        return this;
    }

    @Override
    public HtmlBuilder string(String value) {
        output.append("<span class=\"str\">\"").append(value).append("\"</span>");
        return this;
    }

    @Override
    public HtmlBuilder keyword(String keyword) {
        output.append("<span class=\"kw\">").append(keyword).append("</span>");
        return this;
    }

    @Override
    public HtmlBuilder clazz(String clazz) {
        output.append("<span class=\"cls\">").append(clazz).append("</span>");
        return this;
    }

    @Override
    public HtmlBuilder type(Decl reference) {
        if (reference instanceof BuiltinDecl) {
            this.keyword(reference.getName()).space();
        } else {
            this.reference(reference.getName(), reference.getSymbol().getIndex());
        }

        return this;
    }

    @Override
    public HtmlBuilder reference(String reference, int ref) {
        output.append("<a class=\"ref\" href=\"#").append(ref).append("\">").append(reference).append("</a>");
        return this;
    }


    @Override
    public HtmlBuilder text(String text) {
        output.append(text.replace("<", "&lt;").replace(">", "&gt;"));
        return this;
    }

    @Override
    public HtmlBuilder newline() {
        output.append("</div>\n<div style=\"padding-left:").append(indent * 20).append("px\">");
        return this;
    }

    @Override
    public HtmlBuilder space() {
        output.append(" ");
        return this;
    }

    @Override
    public HtmlBuilder indented(Consumer<SourceBuilder> build) {
        indent += 1;
        build.accept(this);
        indent -= 1;
        return this;
    }
}
