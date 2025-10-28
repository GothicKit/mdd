/*
 * Copyright © 2025. Luis Michaelis <me@lmichaelis.de>
 * SPDX-License-Identifier: MIT-Modern-Variant
 */

package dev.gothickit.mdd.output;

import com.google.gson.stream.JsonWriter;
import dev.gothickit.mdd.cst.*;

import java.io.IOException;

public class JsonBuilder {
    private final JsonWriter jw;

    public JsonBuilder(JsonWriter jw) {
        this.jw = jw;
    }

    public void apply(final Expression expr) throws IOException {
        jw.beginObject();

        jw.name("kind");
        jw.value(expr.getClass().getSimpleName());

        switch (expr) {
            case UnaryExpr e -> {
                jw.name("op");
                jw.value(e.getOperator().getRepr());

                jw.name("rhs");
                this.apply(e.getRight());
            }
            case BinaryExpr e -> {
                jw.name("op");
                jw.value(e.getOperator().getRepr());

                jw.name("lhs");
                this.apply(e.getLeft());

                jw.name("rhs");
                this.apply(e.getRight());
            }
            case IntExpr e -> {
                jw.name("literal");
                jw.value(e.getValue());
            }
            case StringExpr e -> {
                jw.name("literal");
                jw.value(e.getValue());
            }
            case FloatExpr e -> {
                jw.name("literal");
                jw.value(e.getValue());
            }
            case ArrayExpr e -> {
                jw.name("literal");
                jw.beginArray();
                for (var item : e.getValues()) {
                    this.apply(item);
                }
                jw.endArray();
            }
            case AccessExpr e -> {
                jw.name("target");
                jw.value(e.getTarget().getName());

                jw.name("scope");
                if (e.getScope() != null) {
                    jw.value(e.getScope().getName());
                } else {
                    jw.nullValue();
                }

                jw.name("index");
                if (e.getIndex() != null) {
                    this.apply(e.getIndex());
                } else {
                    jw.nullValue();
                }
            }
            case CallExpr e -> {
                jw.name("target");
                jw.value(e.getTarget().getName());

                jw.name("args");
                jw.beginArray();
                for (var arg : e.getArguments()) {
                    this.apply(arg);
                }
                jw.endArray();
            }
            case FunctionReferenceExpr e -> {
                jw.name("target");
                jw.value(e.getFunction().getName());
            }
            default -> throw new IllegalStateException("Unexpected value: " + expr);
        }

        jw.endObject();
    }

    public void apply(final Statement stmt) throws IOException {
        jw.beginObject();

        jw.name("kind");
        jw.value(stmt.getClass().getSimpleName());

        switch (stmt) {
            case VariableDecl s -> this.apply((Decl) s);
            case IfStmt s -> {
                jw.name("condition");
                this.apply(s.getCondition());

                jw.name("body");
                this.applyCode(s.getBody());

                jw.name("next");
                if (s.getNext() != null) {
                    this.apply(s.getNext());
                } else {
                    jw.nullValue();
                }
            }
            case ElseStmt s -> {
                jw.name("body");
                this.applyCode(s.getBody());
            }
            case AssignStmt s -> {
                jw.name("op");
                jw.value(s.getOperator().getRepr());

                jw.name("lhs");
                this.apply(s.getTarget());

                jw.name("rhs");
                this.apply(s.getValue());
            }
            default -> throw new IllegalStateException("Unexpected value: " + stmt);
        }

        jw.endObject();
    }

    public void applyCode(final Code code) throws IOException {
        switch (code) {
            case Block c -> {
                jw.beginArray();
                for (var i : c.getCode()) {
                    this.applyCode(i);
                }
                jw.endArray();
            }
            case Expression c -> this.apply(c);
            case Statement c -> this.apply(c);
            case ReturnStmt c -> {
                jw.beginObject();
                jw.name("kind");
                jw.value(c.getClass().getSimpleName());
                jw.name("value");
                if (c.getValue() != null) {
                    this.applyCode(c.getValue());
                } else {
                    jw.nullValue();
                }
                jw.endObject();
            }
            default -> throw new IllegalStateException("Unexpected value: " + code);
        }
    }

    public void apply(final Decl decl) throws IOException {
        jw.beginObject();

        jw.name("kind");
        jw.value(decl.getClass().getSimpleName());

        jw.name("name");
        jw.value(decl.getName());

        switch (decl) {
            case ClassDecl d -> {
                jw.name("members");
                jw.beginArray();
                for (var member : d.getMembers()) {
                    this.apply((Decl) member);
                }
                jw.endArray();
            }
            case InstanceDecl d -> {
                if (d.getParentClass() != null) {
                    jw.name("type");
                    jw.value(d.getParentClass().getName());

                    jw.name("prototype");
                    if (d.getParentPrototype() != null) {
                        jw.value(d.getParentPrototype().getName());
                    } else {
                        jw.nullValue();
                    }

                    jw.name("body");
                    if (d.getBody() != null) {
                        this.applyCode(d.getBody());
                    } else {
                        jw.nullValue();
                    }
                } else {
                    jw.name("type");
                    jw.nullValue();

                    jw.name("prototype");
                    jw.nullValue();

                    jw.name("body");
                    jw.nullValue();
                }

            }
            case VariableDecl d -> {
                jw.name("type");
                jw.value(d.getType().getName());

                jw.name("value");
                if (d.getValue() != null) {
                    this.apply(d.getValue());
                } else {
                    jw.nullValue();
                }

                if (d instanceof VariableArrayDecl da) {
                    jw.name("size");
                    this.apply(da.getSize());
                }
            }
            case PrototypeDecl d -> {
                jw.name("type");
                jw.value(d.getParentClass().getName());

                jw.name("body");
                this.applyCode(d.getBody());
            }
            case FunctionDecl d -> {
                jw.name("return");
                jw.value(d.getReturnType().getName());

                jw.name("args");
                jw.beginArray();
                for (var p : d.getParameters()) {
                    this.apply((Decl) p);
                }
                jw.endArray();

                jw.name("body");
                if (d.getBody() != null) {
                    this.applyCode(d.getBody());
                } else {
                    jw.nullValue();
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + decl);
        }

        jw.endObject();
    }
}
