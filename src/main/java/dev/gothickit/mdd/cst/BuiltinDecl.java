// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;
import dev.gothickit.zenkit.daedalus.DaedalusSymbol;

public final class BuiltinDecl implements TypeDecl {
    public static final BuiltinDecl INT = new BuiltinDecl(BuiltinType.INT);
    public static final BuiltinDecl FLOAT = new BuiltinDecl(BuiltinType.FLOAT);
    public static final BuiltinDecl STRING = new BuiltinDecl(BuiltinType.STRING);
    public static final BuiltinDecl FUNCTION = new BuiltinDecl(BuiltinType.FUNCTION);
    public static final BuiltinDecl VOID = new BuiltinDecl(BuiltinType.VOID);
    public static final BuiltinDecl INSTANCE = new BuiltinDecl(BuiltinType.INSTANCE);
    private final BuiltinType type;

    public BuiltinDecl(BuiltinType type) {
        this.type = type;
    }

    public static BuiltinDecl of(BuiltinType type) {
        return switch (type) {
            case INT -> BuiltinDecl.INT;
            case FLOAT -> BuiltinDecl.FLOAT;
            case STRING -> BuiltinDecl.STRING;
            case FUNCTION -> BuiltinDecl.FUNCTION;
            case VOID -> BuiltinDecl.VOID;
            case INSTANCE -> BuiltinDecl.INSTANCE;
        };
    }

    @Override
    public DaedalusSymbol getSymbol() {
        return null;
    }

    @Override
    public String getName() {
        return type.getName();
    }

    public BuiltinType getType() {
        return type;
    }

    @Override
    public void getSource(SourceBuilder builder) {
        builder.keyword(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
