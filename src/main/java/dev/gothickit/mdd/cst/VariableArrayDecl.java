// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import dev.gothickit.zenkit.daedalus.DaedalusSymbol;

public final class VariableArrayDecl extends VariableDecl {
    private final Expression size;

    public VariableArrayDecl(DaedalusSymbol symbol, TypeDecl type, String name, Expression size, Expression value) {
        super(symbol, type, name, value);
        this.size = size;
    }

    public Expression getSize() {
        return size;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        if (getValue() != null) {
            builder.keyword("const").space().keyword(getType().getName()).space().text(getName()).text("[").syntax(size).text("]").space().text("=").space().syntax(getValue());
        } else {
            builder.keyword("var").space();

            if (getType() instanceof BuiltinDecl) {
                builder.keyword(getType().getName()).space();
            } else {
                builder.reference(getType().getName(), getType().getSymbol().getIndex()).space();
            }

            builder.text(getName()).text("[").syntax(size).text("]");
        }

    }
}
