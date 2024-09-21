# `mdd` — The Modern Daedalus Decompiler

| ![](assets/demo-decompiler.png) | ![](assets/demo.disassembler.png) |
|---------------------------------|-----------------------------------|

`mdd` is a visual decompiler and disassembler for compiled *Daedalus* scripts used by the *ZenGin*, an early-2000s game engine made by [Piranha Bytes](https://en.wikipedia.org/wiki/Piranha_Bytes) for their open-world role-playing games [Gothic](https://en.wikipedia.org/wiki/Gothic_(video_game)) and [Gothic II](https://en.wikipedia.org/wiki/Gothic_II). More information about the scripting language is available in the [Gothic Mod Development Kit](https://github.com/PhoenixTales/gothic-devkit).

It builds on lots of knowledge collected by the Gothic modding community and previous projects including [DecDat](https://github.com/auronen/DecDat) and [ZenLib](https://github.com/ataulien/ZenLib) and is based on [ZenKit](https://github.com/GothicKit/ZenKit), a library for parsing all sorts of file formats used by the ZenGin.

## using

To get started decompiling some scripts, download the JAR file published with the latest [release](https://github.com/GothicKit/mdd/releases).
Make sure Java is installed on your computer, then double-click the downloaded JAR file (or run `java -jar mdd-<version>-all.jar`).
You can then open a compiled Daedalus script (ending with a `.DAT` extension) by going to `File > Open` and choosing the
file or by dragging it into the symbol list to the left of the window. `mdd` will then load the file (which might take 
a couple of seconds) and display a list of all symbols available within.

You can filter symbols by their type and name by typing into the search box above the symbol tree or selecting a symbol
type from the dropdown menu. Clicking on any symbol in the symbol tree will decompile it and show its code in the right
hand panel. The comment before each symbol contain the raw information saved for that symbol in the script file and below
that the decompiled code is shown. You can click on any function, variable, instance, prototype or class name in the
decompiled code to navigate directly to it.

You can customize the decompilation step, by toggling some post-processing filters applied by the decompiler. To do this
click the *"Decompiler"* menu bar item and toggle any of the options on or off to see their effect. It is recommended to
keep all of these options enabled to improve readability. **Should the encoding of strings look incorrect,** you can
set the encoding of the file in the *"File"* menu bar item. Doing so will re-load the entire script and clear any filters
you may have set, and can take a couple seconds.

Above the decompiled code panel to the right, you can find another tab called "Disassembly". Clicking on it will display
the raw Daedalus bytecode instructions saved in the script file.

## building

`mdd` is written in Java and uses the Gradle build system. To build an executable JAR file, clone the project, then
open a terminal in the project root and run `./gradlew shadowJar`. Once the build process is complete, you can find
an executable JAR file in the `build/libs/` directory.

## technical details

`mdd` decompiles *Daedalus* binaries by parsing them into an abstract syntax tree which is then altered by applying
post-processing filters.

![](assets/abstract-syntax-tree.png)
