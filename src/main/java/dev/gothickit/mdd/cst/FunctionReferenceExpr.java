// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FunctionReferenceExpr implements Expression {
    private final FunctionDecl function;

    public FunctionReferenceExpr(FunctionDecl function) {
        this.function = function;
    }

    @Override
    public void visit(@NotNull Consumer<@NotNull Code> consumer) {
        consumer.accept(this);
    }

    @Override
    public void getSource(SourceBuilder builder) {
        builder.reference(function.getName(), function.getSymbol().getIndex());
    }
}
