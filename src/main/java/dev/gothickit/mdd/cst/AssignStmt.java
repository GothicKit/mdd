// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class AssignStmt implements Statement {
    private final AccessExpr target;
    private final AssignOp operator;
    private Expression value;

    public AssignStmt(AccessExpr target, Expression value, AssignOp operator) {
        this.target = target;
        this.value = value;
        this.operator = operator;
    }

    public AssignOp getOperator() {
        return operator;
    }

    public AccessExpr getTarget() {
        return target;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression expr) {
        this.value = expr;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        builder.syntax(target).space().text(operator.getRepr()).space().syntax(value);
    }

    @Override
    public void visit(@NotNull Consumer<@NotNull Code> consumer) {
        consumer.accept(this);
        target.visit(consumer);
        value.visit(consumer);
    }
}
