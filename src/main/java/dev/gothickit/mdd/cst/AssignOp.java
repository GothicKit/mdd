// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.zenkit.daedalus.DaedalusOpcode;

public enum AssignOp {
    NONE("="),
    ADD("+="),
    SUBTRACT("-="),
    MULTIPLY("*="),
    DIVIDE("/="),
    ;

    private final String repr;

    AssignOp(String repr) {
        this.repr = repr;
    }

    public static AssignOp fromOpcode(DaedalusOpcode op) {
        return switch (op) {
            case ADDMOVI -> AssignOp.ADD;
            case SUBMOVI -> AssignOp.SUBTRACT;
            case MULMOVI -> AssignOp.MULTIPLY;
            case DIVMOVI -> AssignOp.DIVIDE;
            default -> NONE;
        };
    }

    public String getRepr() {
        return repr;
    }
}
