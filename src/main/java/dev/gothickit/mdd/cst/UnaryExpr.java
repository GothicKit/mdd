// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class UnaryExpr implements Expression {
    private final UnaryOp operator;
    private final Expression right;

    public UnaryExpr(UnaryOp operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    public UnaryOp getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        builder.text(operator.getRepr());

        if (right instanceof BinaryExpr) {
            builder.text("(").syntax(right).text(")");
        } else {
            builder.syntax(right);
        }
    }

    @Override
    public void visit(@NotNull Consumer<@NotNull Code> consumer) {
        consumer.accept(this);
        right.visit(consumer);
    }
}
