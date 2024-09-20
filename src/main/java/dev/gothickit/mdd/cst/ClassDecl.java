// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import dev.gothickit.zenkit.daedalus.DaedalusSymbol;

import java.util.List;

public final class ClassDecl implements TypeDecl, Scope, Decl {
    private final DaedalusSymbol symbol;
    private final String name;
    private final List<VariableDecl> members;

    public ClassDecl(DaedalusSymbol symbolId, String name, List<VariableDecl> members) {
        this.symbol = symbolId;
        this.name = name;
        this.members = members;
    }

    @Override
    public DaedalusSymbol getSymbol() {
        return symbol;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public List<VariableDecl> getMembers() {
        return members;
    }

    @Override
    public boolean hasMember(VariableDecl member) {
        return members.contains(member);
    }

    @Override
    public void getSource(SourceBuilder builder) {
        builder.keyword("class").space().clazz(name).space().text("{");
        builder.indented((b) -> {
            for (var member : members) {
                b.newline().syntax(member).text(";");
            }
        });
        builder.newline().text("}");
    }
}
