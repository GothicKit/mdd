// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.zenkit.daedalus.DaedalusOpcode;

public enum BinaryOp {
    ADD("+", 6),
    SUBTRACT("-", 6),
    MULTIPLY("*", 5),
    DIVIDE("/", 5),
    MODULO("%", 5),
    BITWISE_AND("&", 11),
    BITWISE_OR("|", 13),
    LOGICAL_AND("&&", 14),
    LOGICAL_OR("||", 15),
    LEFT_SHIFT("<<", 7),
    RIGHT_SHIFT(">>", 7),
    LESS_THAN("<", 9),
    LESS_THAN_OR_EQUAL("<=", 9),
    GREATER_THAN(">", 9),
    GREATER_THAN_OR_EQUAL(">=", 9),
    EQUAL_TO("==", 10),
    NOT_EQUAL_TO("!=", 10),
    ;

    private final String repr;
    private final int precedence;

    BinaryOp(String repr, int precedence) {
        this.repr = repr;
        this.precedence = precedence;
    }

    public static BinaryOp fromOpcode(DaedalusOpcode op) {
        return switch (op) {
            case ADD -> ADD;
            case SUB -> SUBTRACT;
            case MUL -> MULTIPLY;
            case DIV -> DIVIDE;
            case MOD -> MODULO;
            case ANDB -> BITWISE_AND;
            case OR -> BITWISE_OR;
            case AND -> LOGICAL_AND;
            case ORR -> LOGICAL_OR;
            case LSL -> LEFT_SHIFT;
            case LSR -> RIGHT_SHIFT;
            case LT -> LESS_THAN;
            case LTE -> LESS_THAN_OR_EQUAL;
            case GT -> GREATER_THAN;
            case GTE -> GREATER_THAN_OR_EQUAL;
            case EQ -> EQUAL_TO;
            case NEQ -> NOT_EQUAL_TO;
            default -> throw new IllegalStateException("Unexpected value: " + op);
        };
    }

    public int getPrecedence() {
        return precedence;
    }

    public String getRepr() {
        return repr;
    }
}
