// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.decompiler;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record DecompilerOptions(boolean generateElseIf, boolean generateStringLiterals,
                                boolean resolveFunctionReferences, boolean resolveInstanceReferences) {
    public DecompilerOptions {

    }

    public DecompilerOptions() {
        this(true, true, true, true);
    }

    @Contract(value = "_ -> new", pure = true)
    public @NotNull DecompilerOptions setGenerateElseIf(boolean value) {
        return new DecompilerOptions(value, generateStringLiterals, resolveFunctionReferences, resolveInstanceReferences);
    }

    @Contract(value = "_ -> new", pure = true)
    public @NotNull DecompilerOptions setGenerateStringLiterals(boolean value) {
        return new DecompilerOptions(generateElseIf, value, resolveFunctionReferences, resolveInstanceReferences);
    }

    @Contract(value = "_ -> new", pure = true)
    public @NotNull DecompilerOptions setResolveFunctionReferences(boolean value) {
        return new DecompilerOptions(generateElseIf, generateStringLiterals, value, resolveInstanceReferences);
    }

    @Contract(value = "_ -> new", pure = true)
    public @NotNull DecompilerOptions setResolveInstanceReferences(boolean value) {
        return new DecompilerOptions(generateElseIf, generateStringLiterals, resolveFunctionReferences, value);
    }
}
