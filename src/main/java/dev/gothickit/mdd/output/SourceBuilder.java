// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.output;

import dev.gothickit.mdd.cst.Decl;
import dev.gothickit.mdd.cst.Syntax;

import java.util.function.Consumer;

public interface SourceBuilder {
    String toString();

    SourceBuilder comment(String comment);

    SourceBuilder commentMultiline(String... comment);

    SourceBuilder syntax(Syntax keyword);

    SourceBuilder number(int value);

    SourceBuilder number(float value);

    SourceBuilder string(String value);

    SourceBuilder keyword(String keyword);

    SourceBuilder clazz(String clazz);

    SourceBuilder type(Decl reference);

    SourceBuilder reference(String reference, int ref);

    SourceBuilder text(String text);

    SourceBuilder newline();

    SourceBuilder space();

    SourceBuilder indented(Consumer<SourceBuilder> build);
}
