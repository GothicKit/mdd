// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.icons.FlatTreeOpenIcon;
import dev.gothickit.mdd.decompiler.DecompilerOptions;
import dev.gothickit.mdd.gui.GuiMainView;
import dev.gothickit.zenkit.capi.ZenKit;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Main {

    private static String getResource(String path) {
        try {
            return Files.readString(Path.of(Main.class.getResource("/" + path).toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Main.loadNativeLibraries();
        FlatDarkLaf.setup();

        var mainView = new GuiMainView();

        var menuBar = new JMenuBar();
        menuBar.setOpaque(true);

        var fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        var openItem = new JMenuItem("Open", new FlatTreeOpenIcon());
        openItem.setMnemonic('O');
        openItem.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));

        var exportItem = new JMenuItem("Export");
        exportItem.setMnemonic('E');
        exportItem.setEnabled(false);
        exportItem.setAccelerator(KeyStroke.getKeyStroke('E', InputEvent.CTRL_DOWN_MASK));
        exportItem.addActionListener(e -> {
            var fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setMultiSelectionEnabled(false);
            fc.setDialogTitle("Export Script");
            fc.setApproveButtonText("Export");
            fc.setDialogType(JFileChooser.SAVE_DIALOG);

            var result = fc.showSaveDialog(mainView);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            try {
                mainView.exportScriptFiles(fc.getSelectedFile());
            } catch (IOException ex) {
                showErrorMessage(ex);
                throw new RuntimeException(ex);
            }
        });

        fileMenu.add(openItem);
        fileMenu.add(exportItem);
        menuBar.add(fileMenu);

        var fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle("Open Script");
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setFileFilter(new FileNameExtensionFilter("Daedalus Scripts", "dat"));
        openItem.addActionListener(e -> {
            var result = fc.showOpenDialog(mainView);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            try {
                mainView.loadAndShowScript(fc.getSelectedFile());
            } catch (IOException ex) {
                showErrorMessage(ex);
                throw new RuntimeException(ex);
            }

            exportItem.setEnabled(true);
        });

        var encodingMenu = createEncodingMenu(encoding -> {
            mainView.unloadScript();
            exportItem.setEnabled(false);

            ZenKit.unload();
            ZenKit.load(encoding);
            try {
                mainView.loadAndShowScript(fc.getSelectedFile());
            } catch (IOException ex) {
                showErrorMessage(ex);
                throw new RuntimeException(ex);
            }
            exportItem.setEnabled(true);
        });

        fileMenu.add(encodingMenu);

        var decompilerMenu = createDecompilerMenu(mainView::updateDecompilerOptions);
        menuBar.add(decompilerMenu);

        if (args.length > 0) {
            try {
                mainView.loadAndShowScript(new File(args[0]));
                fc.setSelectedFile(new File(args[0]));
                exportItem.setEnabled(true);
            } catch (IOException ex) {
                showErrorMessage(ex);
                throw new RuntimeException(ex);
            }
        }

        JFrame frame = new JFrame("Modern Daedalus Decompiler (zkmdd)");
        frame.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                for (DataFlavor flavor : support.getDataFlavors()) {
                    if (flavor.isFlavorJavaFileListType()) {
                        return true;
                    }
                }
                return false;
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean importData(TransferSupport support) {
                if (!this.canImport(support)) {
                    return false;
                }

                try {
                    var droppedFiles = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    try {
                        mainView.loadAndShowScript(droppedFiles.get(0));
                        fc.setSelectedFile(droppedFiles.get(0));
                        exportItem.setEnabled(true);
                    } catch (IOException ex) {
                        showErrorMessage(ex);
                        throw new RuntimeException(ex);
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    showErrorMessage(e);
                    throw new RuntimeException(e);
                }
                return true;
            }
        });
        frame.setJMenuBar(menuBar);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(mainView);
        frame.setSize(1200, 800);
        frame.setVisible(true);
    }

    private static @NotNull JMenu createDecompilerMenu(Consumer<DecompilerOptions> onOptionChange) {
        var decompilerMenu = new JMenu("Decompiler");
        var options = new AtomicReference<>(new DecompilerOptions());

        var optionEmitElseIf = new JCheckBoxMenuItem("Generate `else if`");
        optionEmitElseIf.setSelected(true);
        optionEmitElseIf.addItemListener(e -> {
            options.set(options.get().setGenerateElseIf(optionEmitElseIf.isSelected()));
            onOptionChange.accept(options.get());
        });
        decompilerMenu.add(optionEmitElseIf);

        var optionGenerateStringLiterals = new JCheckBoxMenuItem("Generate string literals");
        optionGenerateStringLiterals.setSelected(true);
        optionGenerateStringLiterals.addItemListener(e -> {
            options.set(options.get().setGenerateStringLiterals(optionGenerateStringLiterals.isSelected()));
            onOptionChange.accept(options.get());
        });
        decompilerMenu.add(optionGenerateStringLiterals);

        var optionResolveFunctionRefs = new JCheckBoxMenuItem("Resolve function references");
        optionResolveFunctionRefs.setSelected(true);
        optionResolveFunctionRefs.addItemListener(e -> {
            options.set(options.get().setResolveFunctionReferences(optionResolveFunctionRefs.isSelected()));
            onOptionChange.accept(options.get());
        });
        decompilerMenu.add(optionResolveFunctionRefs);

        var optionResolveInstanceRefs = new JCheckBoxMenuItem("Resolve instance references");
        optionResolveInstanceRefs.setSelected(true);
        optionResolveInstanceRefs.addItemListener(e -> {
            options.set(options.get().setResolveInstanceReferences(optionResolveInstanceRefs.isSelected()));
            onOptionChange.accept(options.get());
        });
        decompilerMenu.add(optionResolveInstanceRefs);

        decompilerMenu.add(new JSeparator());
        var optionEnableAll = new JMenuItem("Enable All Features");
        optionEnableAll.addActionListener(e -> {
            options.set(new DecompilerOptions());

            optionEmitElseIf.setSelected(true);
            optionGenerateStringLiterals.setSelected(true);
            optionResolveInstanceRefs.setSelected(true);
            optionResolveFunctionRefs.setSelected(true);

            onOptionChange.accept(options.get());
        });
        decompilerMenu.add(optionEnableAll);


        return decompilerMenu;
    }

    private static @NotNull JMenu createEncodingMenu(Consumer<String> onEncodingChange) {
        var encodingMenu = new JMenu("Encoding");

        var windows1250Encoding = new JRadioButtonMenuItem("Windows-1250");
        windows1250Encoding.addItemListener(e -> {
            if (windows1250Encoding.isSelected()) {
                onEncodingChange.accept("Windows-1250");
            }
        });

        var windows1251Encoding = new JRadioButtonMenuItem("Windows-1251");
        windows1251Encoding.addItemListener(e -> {
            if (windows1251Encoding.isSelected()) {
                onEncodingChange.accept("Windows-1251");
            }
        });

        var windows1252Encoding = new JRadioButtonMenuItem("Windows-1252");
        windows1252Encoding.setSelected(true);
        windows1252Encoding.addItemListener(e -> {
            if (windows1252Encoding.isSelected()) {
                onEncodingChange.accept("Windows-1252");
            }
        });

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(windows1250Encoding);
        buttonGroup.add(windows1251Encoding);
        buttonGroup.add(windows1252Encoding);

        encodingMenu.add(windows1250Encoding);
        encodingMenu.add(windows1251Encoding);
        encodingMenu.add(windows1252Encoding);
        return encodingMenu;
    }

    private static void loadNativeLibraries() {
        try {
            ZenKit.load("Windows-1252");
        } catch (Exception e) {
            showErrorMessage(e);
            throw e;
        }
    }

    private static void showErrorMessage(final @NotNull Exception e) {
        JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}