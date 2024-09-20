// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class AccessExpr implements Expression {
    private final VariableDecl target;
    private final Expression index;
    private final InstanceDecl scope;

    public AccessExpr(VariableDecl target, Expression index, InstanceDecl scope) {
        this.target = target;
        this.index = index;
        this.scope = scope;
    }

    public InstanceDecl getScope() {
        return scope;
    }

    public Expression getIndex() {
        return index;
    }

    public VariableDecl getTarget() {
        return target;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        if (scope != null && scope.getParentClass().hasMember(target)) {
            builder.reference(scope.getName(), scope.getSymbol().getIndex()).text(".");
        }

        builder.reference(target.getName(), target.getSymbol().getIndex());

        if (index != null) {
            builder.text("[").syntax(index).text("]");
        } else if (target instanceof VariableArrayDecl) {
            builder.text("[").number(0).text("]");
        }
    }

    @Override
    public void visit(@NotNull Consumer<@NotNull Code> consumer) {
        consumer.accept(this);
        target.visit(consumer);

        if (index != null) {
            index.visit(consumer);
        }
    }
}
