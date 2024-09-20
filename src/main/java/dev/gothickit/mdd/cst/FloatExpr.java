// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class FloatExpr implements Expression {
    private final float value;

    public FloatExpr(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public void getSource(@NotNull SourceBuilder builder) {
        builder.number(value);
    }

    @Override
    public void visit(@NotNull Consumer<@NotNull Code> consumer) {
        consumer.accept(this);
    }
}
