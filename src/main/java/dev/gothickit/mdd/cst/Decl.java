// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.zenkit.daedalus.DaedalusSymbol;

public interface Decl extends Syntax {
    DaedalusSymbol getSymbol();

    String getName();
}
