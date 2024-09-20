// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import dev.gothickit.zenkit.daedalus.DaedalusSymbol;

public final class InstanceDecl extends VariableDecl {
    private final ClassDecl parentClass;
    private final PrototypeDecl parentPrototype;
    private Block body;

    public InstanceDecl(DaedalusSymbol symbol, String name, Block body) {
        super(symbol, BuiltinDecl.INSTANCE, name, null);
        this.parentClass = null;
        this.parentPrototype = null;
        this.body = body;
    }

    public InstanceDecl(DaedalusSymbol symbol, String name, ClassDecl parentClass, Block body) {
        super(symbol, parentClass, name, null);
        this.parentClass = parentClass;
        this.parentPrototype = null;
        this.body = body;
    }

    public InstanceDecl(DaedalusSymbol symbol, String name, PrototypeDecl parentPrototype, Block body) {
        super(symbol, parentPrototype.getParentClass(), name, null);
        this.parentClass = parentPrototype.getParentClass();
        this.parentPrototype = parentPrototype;
        this.body = body;
    }

    public ClassDecl getParentClass() {
        return parentClass;
    }

    public PrototypeDecl getParentPrototype() {
        return parentPrototype;
    }

    public Block getBody() {
        return body;
    }

    public void setBody(Block body) {
        this.body = body;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        if (body == null) {
            super.getSource(builder);
            return;
        }

        builder.keyword("instance").space().text(getName()).text("(").type(parentPrototype == null ? parentClass : parentPrototype).text(") {").indented(b -> {
            b.syntax(body);
        }).newline().text("}");
    }

}
