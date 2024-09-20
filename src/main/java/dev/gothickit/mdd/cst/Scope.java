// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import java.util.List;

public interface Scope extends Syntax {
    List<VariableDecl> getMembers();

    boolean hasMember(VariableDecl member);
}
