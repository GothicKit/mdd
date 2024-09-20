// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public final class CallExpr implements Expression {
    private final FunctionDecl target;
    private final List<Expression> arguments;

    public CallExpr(FunctionDecl target, List<Expression> arguments) {
        this.target = target;
        this.arguments = arguments;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public void replaceArgument(int i, Expression arg) {
        arguments.set(i, arg);
    }

    public FunctionDecl getTarget() {
        return target;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        builder.reference(target.getName(), target.getSymbol().getIndex()).text("(");

        for (int i = 0; i < arguments.size(); i++) {
            builder.syntax(arguments.get(i));
            if (i < arguments.size() - 1) {
                builder.text(", ");
            }
        }

        builder.text(")");
    }

    @Override
    public void visit(@NotNull Consumer<@NotNull Code> consumer) {
        consumer.accept(this);
        for (Expression argument : arguments) {
            argument.visit(consumer);
        }
    }

    public Expression getArgument(int i) {
        return arguments.get(i);
    }
}
