// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Base class for all Daedalus code syntax.
 */
public interface Code extends Syntax {
    void visit(@NotNull Consumer<@NotNull Code> consumer);
}
