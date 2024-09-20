// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.cst;

import dev.gothickit.mdd.output.SourceBuilder;

/**
 * Base class for all Daedalus syntax nodes.
 */
public interface Syntax {
    void getSource(SourceBuilder builder);
}
