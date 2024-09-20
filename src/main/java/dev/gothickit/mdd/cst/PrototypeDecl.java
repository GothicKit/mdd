// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import dev.gothickit.zenkit.daedalus.DaedalusSymbol;

public final class PrototypeDecl implements Syntax, Decl {
    private final DaedalusSymbol symbol;
    private final String name;
    private final ClassDecl parent;
    private Block body;

    public PrototypeDecl(DaedalusSymbol symbol, String name, ClassDecl parent, Block body) {
        this.name = name;
        this.parent = parent;
        this.body = body;
        this.symbol = symbol;
    }

    @Override
    public DaedalusSymbol getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public ClassDecl getParentClass() {
        return parent;
    }

    @Override
    public String toString() {
        return getName();
    }

    public Block getBody() {
        return body;
    }

    public void setBody(Block body) {
        this.body = body;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        builder.keyword("prototype").space().text(getName()).text("(").type(parent).text(") {").indented(b -> {
            if (body != null) {
                b.syntax(body);
            }
        }).newline().text("}");
    }
}
