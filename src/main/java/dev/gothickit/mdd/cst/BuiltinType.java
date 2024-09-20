// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

public enum BuiltinType {
    INT("int"),
    STRING("string"),
    FLOAT("float"),
    FUNCTION("func"),
    VOID("void"),
    INSTANCE("instance");

    private final String name;

    BuiltinType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
