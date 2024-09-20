// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class ElseStmt implements Conditional {
    private final Block body;

    public ElseStmt(Block body) {
        this.body = body;
    }

    public Block getBody() {
        return body;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        builder.text("{");
        builder.indented((b) -> {
            if (body != null) {
                b.syntax(body);
            }
        });
        builder.newline().text("}");
    }

    @Override
    public void visit(@NotNull Consumer<@NotNull Code> consumer) {
        consumer.accept(this);
        body.visit(consumer);
    }
}
