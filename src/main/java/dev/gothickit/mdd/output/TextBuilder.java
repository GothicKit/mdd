// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.output;

import dev.gothickit.mdd.cst.Decl;
import dev.gothickit.mdd.cst.Syntax;

import java.util.function.Consumer;

public class TextBuilder implements SourceBuilder {
    private final StringBuilder builder = new StringBuilder();
    private String indent = "";

    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    public SourceBuilder comment(String comment) {
        builder.append("/* ").append(comment).append(" */");
        return this;
    }

    @Override
    public SourceBuilder commentMultiline(String... comment) {
        builder.append("/*");

        for (String s : comment) {
            newline();
            builder.append("&emsp;*&emsp;").append(s);
        }

        newline();
        builder.append("&emsp;*/");

        return this;
    }

    @Override
    public TextBuilder syntax(Syntax keyword) {
        keyword.getSource(this);
        return this;
    }

    @Override
    public TextBuilder number(int value) {
        builder.append(value);
        return this;
    }

    @Override
    public TextBuilder number(float value) {
        builder.append(value);
        return this;
    }

    @Override
    public TextBuilder string(String value) {
        builder.append('"').append(value).append('"');
        return this;
    }

    @Override
    public TextBuilder keyword(String keyword) {
        builder.append(keyword);
        return this;
    }

    @Override
    public TextBuilder clazz(String clazz) {
        builder.append(clazz);
        return this;
    }

    @Override
    public TextBuilder type(Decl reference) {
        builder.append(reference.getName());
        return this;
    }

    @Override
    public TextBuilder reference(String reference, int ref) {
        builder.append(reference);
        return this;
    }

    @Override
    public TextBuilder text(String text) {
        builder.append(text);
        return this;
    }

    @Override
    public TextBuilder newline() {
        this.builder.append(indent).append(System.lineSeparator());
        return this;
    }

    @Override
    public TextBuilder space() {
        this.builder.append("&nbsp;");
        return this;
    }

    @Override
    public TextBuilder indented(Consumer<SourceBuilder> build) {
        this.indent += "\t";
        build.accept(this);
        this.indent = this.indent.substring(1);
        return this;
    }
}
