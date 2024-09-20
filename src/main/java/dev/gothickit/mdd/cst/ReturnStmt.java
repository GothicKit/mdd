// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ReturnStmt implements Code {
    private final Expression value;

    public ReturnStmt(Expression value) {
        this.value = value;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        if (value != null) {
            builder.keyword("return").space().syntax(value);
        } else {
            builder.keyword("return");
        }
    }

    @Override
    public void visit(@NotNull Consumer<@NotNull Code> consumer) {
        consumer.accept(this);
        if (value != null) {
            value.visit(consumer);
        }
    }
}
