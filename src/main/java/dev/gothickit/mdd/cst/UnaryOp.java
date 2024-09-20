// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.zenkit.daedalus.DaedalusOpcode;

public enum UnaryOp {
    PLUS("+"),
    MINUS("-"),
    LOGICAL_NOT("!"),
    BITWISE_NOT("~"),
    ;

    private final String repr;

    UnaryOp(String repr) {
        this.repr = repr;
    }

    public static UnaryOp fromOpcode(DaedalusOpcode op) {
        return switch (op) {
            case PLUS -> PLUS;
            case NEGATE -> MINUS;
            case NOT -> LOGICAL_NOT;
            case CMPL -> BITWISE_NOT;
            default -> throw new IllegalStateException("Unexpected value: " + op);
        };
    }

    public String getRepr() {
        return repr;
    }
}
