// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class IfStmt implements Conditional {
    private final Expression condition;
    private final Block body;
    private Conditional next;

    public IfStmt(Expression condition, Block body, Conditional next) {
        this.condition = condition;
        this.body = body;
        this.next = next;
    }

    public Expression getCondition() {
        return condition;
    }

    public Block getBody() {
        return body;
    }

    public Conditional getNext() {
        return next;
    }

    public void setNext(Conditional next) {
        this.next = next;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        builder.keyword("if").space().text("(").syntax(condition).text(") {").indented(b -> {
            if (body != null) {
                b.syntax(body);
            }
        }).newline().text("}");

        if (next != null) {
            builder.space().keyword("else").space().syntax(next);
        }
    }

    @Override
    public void visit(@NotNull Consumer<@NotNull Code> consumer) {
        consumer.accept(this);
        condition.visit(consumer);
        body.visit(consumer);

        if (next != null) {
            next.visit(consumer);
        }
    }
}
