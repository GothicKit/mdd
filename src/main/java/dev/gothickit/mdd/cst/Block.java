// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class Block implements Code {
    private final List<Code> code = new ArrayList<>();

    public Block() {
    }

    public List<Code> getCode() {
        return code;
    }

    public Code getCode(int i) {
        return code.get(i);
    }

    public void addCode(Code code) {
        this.code.add(code);
    }

    public void visit(final @NotNull Consumer<@NotNull Code> consumer) {
        for (Code code : this.code) {
            code.visit(consumer);
        }

        consumer.accept(this);
    }

    @Override
    public void getSource(SourceBuilder builder) {
        for (Code code : code) {
            builder.newline().syntax(code).text(";");
        }
    }
}
