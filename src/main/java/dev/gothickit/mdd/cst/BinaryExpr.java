// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class BinaryExpr implements Expression {
    private final BinaryOp operator;
    private final Expression left;
    private final Expression right;

    public BinaryExpr(BinaryOp operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public BinaryOp getOperator() {
        return operator;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        boolean lparen = left instanceof BinaryExpr bin && bin.operator.getPrecedence() > operator.getPrecedence();
        boolean rparen = right instanceof BinaryExpr bin && bin.operator.getPrecedence() > operator.getPrecedence();

        if (lparen) {
            builder.text("(").syntax(left).text(")").space();
        } else {
            builder.syntax(left).space();
        }

        builder.text(this.operator.getRepr()).space();

        if (rparen) {
            builder.text("(").syntax(right).text(")");
        } else {
            builder.syntax(right);
        }
    }

    @Override
    public void visit(@NotNull Consumer<@NotNull Code> consumer) {
        consumer.accept(this);
        left.visit(consumer);
        right.visit(consumer);
    }
}
