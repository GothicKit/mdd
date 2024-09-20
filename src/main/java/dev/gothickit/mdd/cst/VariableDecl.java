// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import dev.gothickit.zenkit.daedalus.DaedalusSymbol;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class VariableDecl implements Statement, Decl {
    private final DaedalusSymbol symbol;
    private final TypeDecl type;
    private final String name;
    private final Expression value;

    public VariableDecl(DaedalusSymbol symbol, TypeDecl type, String name, Expression value) {
        this.symbol = symbol;
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public TypeDecl getType() {
        return type;
    }

    @Override
    public DaedalusSymbol getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        if (this.value != null) {
            builder.keyword("const").space().keyword(type.getName()).space().text(name).space().text("=").space().syntax(value);
        } else {
            builder.keyword("var").space().type(type).space().text(name);
        }
    }

    @Override
    public void visit(@NotNull Consumer<@NotNull Code> consumer) {
        consumer.accept(this);

        if (value != null) {
            value.visit(consumer);
        }
    }
}
