// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.gui;

import dev.gothickit.mdd.cst.Decl;
import dev.gothickit.mdd.decompiler.Decompiler;
import dev.gothickit.mdd.decompiler.DecompilerOptions;
import dev.gothickit.mdd.output.TextBuilder;
import dev.gothickit.zenkit.daedalus.DaedalusScript;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class GuiMainView extends JSplitPane {
    private final GuiSymbolsView symbolsView;
    private final GuiDecompilerView decompilerView;
    private final GuiDisassemblerView disassemblerView;
    private Decompiler decompiler;
    private DaedalusScript script;
    private Decl selected = null;

    public GuiMainView() {
        super(JSplitPane.HORIZONTAL_SPLIT);

        symbolsView = new GuiSymbolsView(this::onSymbolSelected);
        decompilerView = new GuiDecompilerView(this::onSymbolSelected);
        disassemblerView = new GuiDisassemblerView();

        JScrollPane decompilerScrollPane = new JScrollPane(decompilerView);
        decompilerScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Decompilation", decompilerScrollPane);
        tabbedPane.addTab("Disassembly", disassemblerView);

        setLeftComponent(symbolsView);
        setRightComponent(tabbedPane);
    }

    public void loadAndShowScript(File file) throws IOException {
        try {
            this.script = DaedalusScript.load(file.getPath());
            this.decompiler = new Decompiler(script);
            this.symbolsView.reset(this.decompiler.getTopLevelSymbols(), file.getName());
        } catch (Exception e) {
            throw new IOException("Script import failed", e);
        }
    }

    public void unloadScript() {
        this.script = null;
        this.decompiler = null;
        this.symbolsView.clear();
        this.decompilerView.clear();
        this.disassemblerView.clear();
    }

    private void onSymbolSelected(final Decl decl) {
        if (decl == null) {
            return;
        }
        decompiler.decompileCode(decl);
        decompilerView.setSymbol(decl, decompiler);
        disassemblerView.setSymbol(script, decl);
        selected = decl;
    }

    private void onSymbolSelected(final int id) {
        var decl = decompiler.decompileDecl(id);
        onSymbolSelected(decl);
    }

    public void exportScriptFiles(final @NotNull File path) throws IOException {
        if (!path.exists()) {
            path.mkdirs();
        }

        var symbols = decompiler.getTopLevelSymbols();

        ProgressMonitor progressBar = new ProgressMonitor(this, "Exporting Script", "", 1, symbols.size() * 2);
        progressBar.setNote("Decompiling Symbols ...");
        int i = 1;

        var files = new HashMap<Integer, TextBuilder>();
        for (var decl : symbols) {
            progressBar.setNote("Decompiling Symbols (" + decl.getName() + ") ...");
            if (progressBar.isCanceled()) {
                return;
            }

            var builder = files.getOrDefault(decl.getSymbol().getFileIndex(), null);
            if (builder == null) {
                builder = new TextBuilder();
                files.put(decl.getSymbol().getFileIndex(), builder);
            }

            try {
                decompiler.decompileCode(decl);
                builder.commentMultiline(decompiler.formatSymbolInfo(decl)).newline().syntax(decl).text(";").newline().newline();
            } catch (Exception e) {
                e.printStackTrace();
            }

            progressBar.setProgress(i + 1);
            i += 1;
        }

        progressBar.setNote("Saving Files ...");

        for (var entry : files.entrySet()) {
            if (progressBar.isCanceled()) {
                return;
            }
            Files.writeString(path.toPath().resolve(entry.getKey().toString() + ".d"), entry.getValue().toString());
            progressBar.setNote("Saving Files (" + entry.getKey() + ") ...");
            progressBar.setProgress(i + 1);
            i += 1;
        }

        progressBar.setNote("Done.");
        progressBar.close();
    }

    public void updateDecompilerOptions(DecompilerOptions decompilerOptions) {
        decompiler.setOptions(decompilerOptions);
        this.onSymbolSelected(selected); // Refresh the decompiled view
    }
}
