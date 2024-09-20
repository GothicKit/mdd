// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.decompiler;

import dev.gothickit.zenkit.daedalus.DaedalusInstruction;
import dev.gothickit.zenkit.daedalus.DaedalusSymbol;

public record DecompilerStackFrame(DaedalusInstruction instruction, DaedalusSymbol context) {
}
