// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public final class ArrayExpr implements Expression {
    private final List<Expression> values;

    public ArrayExpr(List<Expression> values) {
        this.values = values;
    }

    public List<Expression> getValues() {
        return values;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        builder.text("{");
        for (int i = 0; i < values.size(); i++) {
            builder.syntax(values.get(i));
            if (i < values.size() - 1) {
                builder.text(", ");
            }
        }
        builder.text("}");
    }

    @Override
    public void visit(@NotNull Consumer<@NotNull Code> consumer) {
        consumer.accept(this);
        for (Expression value : values) {
            value.visit(consumer);
        }
    }
}
