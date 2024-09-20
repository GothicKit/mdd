// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.gui;

import dev.gothickit.mdd.cst.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

public class GuiIcons {
    private static final HashMap<String, ImageIcon> icons = new HashMap<>();

    static {
        icons.put("instance", loadIcon("/icons/interface_dark.png"));
        icons.put("prototype", loadIcon("/icons/classInitializer_dark.png"));
        icons.put("class", loadIcon("/icons/class_dark.png"));
        icons.put("member", loadIcon("/icons/property_dark.png"));
        icons.put("constant", loadIcon("/icons/constant_dark.png"));
        icons.put("variable", loadIcon("/icons/variable_dark.png"));
        icons.put("globalVariable", loadIcon("/icons/gvariable_dark.png"));
        icons.put("function", loadIcon("/icons/function_dark.png"));
        icons.put("external", loadIcon("/icons/include_dark.png"));
    }

    private static @NotNull ImageIcon loadIcon(String path) {
        var ico = new ImageIcon(Objects.requireNonNull(GuiIcons.class.getResource(path)));
        var img = ico.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static ImageIcon getIcon(String name) {
        return icons.get(name);
    }

    public static ImageIcon getIcon(Decl decl) {
        return icons.get(getIconName(decl));
    }

    public static String getIconName(@NotNull Decl decl) {
        if (decl instanceof InstanceDecl) {
            return "instance";
        } else if (decl instanceof PrototypeDecl) {
            return "prototype";
        } else if (decl instanceof VariableDecl v) {
            if (decl.getSymbol().isMember()) {
                return "member";
            } else if (v.getValue() != null) {
                return "constant";
            } else if (decl.getSymbol().getName().contains(".")) {
                return "variable";
            }
            return "globalVariable";
        } else if (decl instanceof ClassDecl) {
            return "class";
        } else if (decl instanceof FunctionDecl) {
            if (decl.getSymbol().isExternal()) {
                return "external";
            }
            return "function";
        }

        return null;
    }

}
