// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import dev.gothickit.zenkit.daedalus.DaedalusSymbol;

import java.util.List;

public final class FunctionDecl implements Syntax, Scope, Decl {
    private final DaedalusSymbol symbol;
    private final TypeDecl rtype;
    private final String name;
    private final List<VariableDecl> parameters;
    private final List<VariableDecl> locals;
    private Block body;

    public FunctionDecl(DaedalusSymbol symbol, TypeDecl rtype, String name, List<VariableDecl> parameters, List<VariableDecl> locals, Block body) {
        this.rtype = rtype;
        this.name = name;
        this.parameters = parameters;
        this.locals = locals;
        this.body = body;
        this.symbol = symbol;
    }

    public TypeDecl getReturnType() {
        return rtype;
    }

    @Override
    public DaedalusSymbol getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public List<VariableDecl> getParameters() {
        return parameters;
    }

    public Block getBody() {
        return body;
    }

    public void setBody(Block body) {
        this.body = body;
    }

    @Override
    public List<VariableDecl> getMembers() {
        return locals;
    }

    @Override
    public boolean hasMember(VariableDecl member) {
        return locals.contains(member);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void getSource(SourceBuilder builder) {
        builder.keyword("func").space().type(rtype).space().text(name).text("(");

        for (int i = 0; i < parameters.size(); i++) {
            builder.syntax(parameters.get(i));
            if (i < parameters.size() - 1) {
                builder.text(", ");
            }
        }

        builder.text(") {").indented(b -> {
            for (var v : locals) {
                b.newline().syntax(v).text(";");
            }

            if (body != null) {
                b.syntax(body);
            }
        });

        builder.newline().text("}");
    }
}
