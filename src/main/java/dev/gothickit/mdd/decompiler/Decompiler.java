// Copyright © 2024. GothicKit Contributors.
// SPDX-License-Identifier: MIT-Modern-Variant
package dev.gothickit.mdd.decompiler;

import dev.gothickit.mdd.cst.*;
import dev.gothickit.zenkit.daedalus.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Decompiler {
    private final DaedalusScript script;
    private final List<DaedalusSymbol> symbols; // Cached to improve performance
    private final List<Decl> topLevelNodes = new ArrayList<>();
    private final HashMap<Integer, Syntax> cache = new HashMap<>();
    private DecompilerOptions options = new DecompilerOptions();

    public Decompiler(DaedalusScript script) {
        this.script = script;
        this.symbols = script.getSymbols();

        for (var sym : symbols) {
            this.decompileDeclaration(sym);
        }
    }

    public List<Decl> getTopLevelSymbols() {
        return topLevelNodes;
    }

    private ClassDecl decompileClassDecl(DaedalusSymbol clazz) {
        assert clazz.getType() == DaedalusDataType.CLASS;

        if (this.cache.containsKey(clazz.getIndex())) {
            return (ClassDecl) this.cache.get(clazz.getIndex());
        }

        // Find class member variables
        var members = new ArrayList<VariableDecl>();
        var prefix = clazz.getName() + ".";
        for (var i = clazz.getIndex() + 1; i < symbols.size(); ++i) {
            var sym = symbols.get(i);
            if (!sym.getName().startsWith(prefix)) {
                break;
            }

            members.add(this.decompileVariableDecl(sym));
        }

        ClassDecl decl = new ClassDecl(clazz, clazz.getName(), members);
        this.topLevelNodes.add(decl);
        this.cache.put(clazz.getIndex(), decl);
        return decl;
    }

    private PrototypeDecl decompilePrototypeDecl(DaedalusSymbol prototype) {
        assert prototype.getType() == DaedalusDataType.PROTOTYPE;

        if (this.cache.containsKey(prototype.getIndex())) {
            return (PrototypeDecl) this.cache.get(prototype.getIndex());
        }

        String name = prototype.getName();
        if (name.contains(".")) {
            name = name.substring(name.lastIndexOf('.') + 1);
        }

        var parent = prototype.getParent() == -1 || prototype.getParent() >= symbols.size() ? null : symbols.get(prototype.getParent());
        assert parent != null;
        assert parent.getType() == DaedalusDataType.CLASS;

        PrototypeDecl decl = new PrototypeDecl(prototype, name, this.decompileClassDecl(parent), null);
        this.topLevelNodes.add(decl);
        this.cache.put(prototype.getIndex(), decl);
        return decl;
    }

    private InstanceDecl decompileInstanceDecl(DaedalusSymbol instance) {
        assert instance.getType() == DaedalusDataType.INSTANCE;

        if (this.cache.containsKey(instance.getIndex())) {
            return (InstanceDecl) this.cache.get(instance.getIndex());
        }

        boolean toplevel = true;
        String name = instance.getName();
        if (name.contains(".")) {
            name = name.substring(name.lastIndexOf('.') + 1);
            toplevel = false;
        }

        InstanceDecl decl;
        var parent = instance.getParent() == -1 || instance.getParent() >= symbols.size() ? null : symbols.get(instance.getParent());
        if (parent == null) {
            decl = new InstanceDecl(instance, name, null);
        } else if (parent.getType() == DaedalusDataType.PROTOTYPE) {
            decl = new InstanceDecl(instance, name, this.decompilePrototypeDecl(parent), null);
        } else if (parent.getType() == DaedalusDataType.CLASS) {
            decl = new InstanceDecl(instance, name, this.decompileClassDecl(parent), null);
        } else {
            throw new RuntimeException("Unsupported instance parent: " + parent.getType());
        }

        if (toplevel) {
            topLevelNodes.add(decl);
        }

        this.cache.put(instance.getIndex(), decl);
        return decl;
    }

    private Expression decompileVariableValue(DaedalusSymbol variable) {
        if (!variable.isConst()) {
            return null;
        }

        switch (variable.getType()) {
            case FLOAT -> {
                if (variable.getSize() > 1) {
                    var vals = new ArrayList<Expression>();
                    for (short i = 0; i < variable.getSize(); ++i) {
                        vals.add(new FloatExpr(variable.getFloat(i, null)));
                    }
                    return new ArrayExpr(vals);
                } else {
                    return new FloatExpr(variable.getFloat((short) 0, null));
                }
            }
            case INT -> {
                if (variable.getSize() > 1) {
                    var vals = new ArrayList<Expression>();
                    for (short i = 0; i < variable.getSize(); ++i) {
                        vals.add(new IntExpr(variable.getInt(i, null)));
                    }
                    return new ArrayExpr(vals);
                } else {
                    return new IntExpr(variable.getInt((short) 0, null));
                }
            }
            case STRING -> {
                if (variable.getSize() > 1) {
                    var vals = new ArrayList<Expression>();
                    for (short i = 0; i < variable.getSize(); ++i) {
                        vals.add(new StringExpr(variable.getString(i, null)));
                    }
                    return new ArrayExpr(vals);
                } else {
                    return new StringExpr(variable.getString((short) 0, null));
                }
            }
            default -> throw new RuntimeException("Unsupported variable type: " + variable.getType());
        }
    }

    private VariableDecl decompileVariableDecl(DaedalusSymbol variable) {
        if (variable.getType() == DaedalusDataType.INSTANCE) {
            return this.decompileInstanceDecl(variable);
        }

        if (this.cache.containsKey(variable.getIndex())) {
            return (VariableDecl) this.cache.get(variable.getIndex());
        }

        boolean toplevel = true;
        String name = variable.getName();
        if (name.contains(".")) {
            name = name.substring(name.lastIndexOf('.') + 1);
            toplevel = false;
        }

        VariableDecl decl;
        var value = this.decompileVariableValue(variable);
        switch (variable.getType()) {
            case FLOAT -> {
                if (variable.getSize() > 1) {
                    decl = new VariableArrayDecl(variable, BuiltinDecl.FLOAT, name, new IntExpr(variable.getSize()), value);
                } else {
                    decl = new VariableDecl(variable, BuiltinDecl.FLOAT, name, value);
                }
            }
            case INT -> {
                if (variable.getSize() > 1) {
                    decl = new VariableArrayDecl(variable, BuiltinDecl.INT, name, new IntExpr(variable.getSize()), value);
                } else {
                    decl = new VariableDecl(variable, BuiltinDecl.INT, name, value);
                }
            }
            case STRING -> {
                if (variable.getSize() > 1) {
                    decl = new VariableArrayDecl(variable, BuiltinDecl.STRING, name, new IntExpr(variable.getSize()), value);
                } else {
                    decl = new VariableDecl(variable, BuiltinDecl.STRING, name, value);
                }
            }
            case FUNCTION -> {
                if (variable.getSize() > 1) {
                    decl = new VariableArrayDecl(variable, BuiltinDecl.FUNCTION, name, new IntExpr(variable.getSize()), value);
                } else {
                    decl = new VariableDecl(variable, BuiltinDecl.FUNCTION, name, value);
                }
            }
            default -> throw new RuntimeException("Unsupported variable type: " + variable.getType());
        }

        if (toplevel) {
            topLevelNodes.add(decl);
        }

        this.cache.put(variable.getIndex(), decl);
        return decl;
    }

    private FunctionDecl decompileFunctionDecl(DaedalusSymbol function) {
        assert function.getType() == DaedalusDataType.FUNCTION && function.isConst();

        if (this.cache.containsKey(function.getIndex())) {
            return (FunctionDecl) this.cache.get(function.getIndex());
        }

        // Find function parameters
        var params = new ArrayList<VariableDecl>();
        for (var i = 0; i < function.getSize(); ++i) {
            var sym = symbols.get(function.getIndex() + 1 + i);
            params.add(this.decompileVariableDecl(sym));
        }

        // Find function local variables
        var locals = new ArrayList<VariableDecl>();
        var prefix = function.getName() + ".";
        for (var i = function.getIndex() + 1 + function.getSize(); i < symbols.size(); ++i) {
            var sym = symbols.get(i);
            if (!sym.getName().startsWith(prefix)) {
                break;
            }

            locals.add(this.decompileVariableDecl(sym));
        }

        var decl = new FunctionDecl(function, this.decompileType(function.getReturnType()), function.getName(), params, locals, null);
        this.topLevelNodes.add(decl);
        this.cache.put(function.getIndex(), decl);
        return decl;
    }

    private TypeDecl decompileType(DaedalusDataType type) {
        return switch (type) {
            case VOID -> BuiltinDecl.VOID;
            case FLOAT -> BuiltinDecl.FLOAT;
            case INT -> BuiltinDecl.INT;
            case STRING -> BuiltinDecl.STRING;
            case FUNCTION -> BuiltinDecl.FUNCTION;
            case INSTANCE -> BuiltinDecl.INSTANCE;
            default -> throw new IllegalArgumentException("Unknown builtin: " + type);
        };
    }

    private Decl decompileDeclaration(DaedalusSymbol symbol) {
        if (symbol.getType() == DaedalusDataType.FUNCTION && symbol.isConst()) {
            return this.decompileFunctionDecl(symbol);
        } else if (symbol.getType() == DaedalusDataType.PROTOTYPE) {
            return this.decompilePrototypeDecl(symbol);
        } else if (symbol.getType() == DaedalusDataType.CLASS) {
            return this.decompileClassDecl(symbol);
        } else {
            return this.decompileVariableDecl(symbol);
        }
    }

    public Decl decompileDecl(int symbolId) {
        var sym = script.getSymbolByIndex(symbolId);
        if (sym == null) {
            return null;
        }
        return this.decompileDeclaration(sym);
    }

    private Expression decompileExpression(Stack<DecompilerStackFrame> stack) {
        return this.decompileExpression(stack, false);
    }

    private Expression decompileExpression(Stack<DecompilerStackFrame> stack, boolean isFloat) {
        if (stack.empty()) {
            return new IntExpr(0);
        }

        var top = stack.pop();
        return switch (top.instruction().op()) {
            case ADD, SUB, MUL, DIV, MOD, OR, ANDB, LT, GT, ORR, AND, LSL, LSR, LTE, EQ, NEQ, GTE -> {
                var op = BinaryOp.fromOpcode(top.instruction().op());
                var lhs = this.decompileExpression(stack);
                var rhs = this.decompileExpression(stack);
                yield new BinaryExpr(op, lhs, rhs);
            }
            case PLUS, NEGATE, NOT, CMPL, NOP -> {
                var op = UnaryOp.fromOpcode(top.instruction().op());
                var rhs = this.decompileExpression(stack);
                yield new UnaryExpr(op, rhs);
            }
            case BL -> {
                var sym = script.getSymbolByAddress(top.instruction().data);
                if (sym == null) {
                    throw new IllegalStateException("BL target not found");
                }

                if (sym.getType() != DaedalusDataType.FUNCTION || !sym.isConst()) {
                    throw new IllegalStateException("BL target is not a function");
                }

                var fn = this.decompileFunctionDecl(sym);
                var args = new ArrayList<Expression>();

                for (int i = 0; i < fn.getParameters().size(); ++i) {
                    args.add(this.decompileExpression(stack));
                }

                Collections.reverse(args);
                yield new CallExpr(fn, args);
            }
            case BE -> {
                var sym = script.getSymbolByIndex(top.instruction().data);
                if (sym == null) {
                    throw new IllegalStateException("BE target not found");
                }

                if (sym.getType() != DaedalusDataType.FUNCTION || !sym.isConst() || !sym.isExternal()) {
                    throw new IllegalStateException("BE target is not an external function");
                }

                var fn = this.decompileFunctionDecl(sym);
                var args = new ArrayList<Expression>();

                for (int i = 0; i < fn.getParameters().size(); ++i) {
                    args.add(this.decompileExpression(stack));
                }

                Collections.reverse(args);
                yield new CallExpr(fn, args);
            }
            case PUSHI -> {
                if (isFloat) {
                    yield new FloatExpr(Float.intBitsToFloat(top.instruction().data));
                }
                yield new IntExpr(top.instruction().data);
            }
            case PUSHV, PUSHVI -> {
                var sym = script.getSymbolByIndex(top.instruction().data);
                if (sym == null) {
                    throw new IllegalStateException("PUSHV target not found");
                }

                // NOTE(lmichaelis): converts literal strings
                if (sym.isGenerated() && !sym.isMember() && options.generateStringLiterals()) {
                    if (sym.isConst()) {
                        yield this.decompileVariableValue(sym);
                    } else if (sym.getType() == DaedalusDataType.INSTANCE && sym.getAddress() == 0) {
                        yield new IntExpr(0); // FIXME: This should be 'null'
                    }
                }

                var ctx = top.context() != null ? this.decompileInstanceDecl(top.context()) : null;
                yield new AccessExpr(this.decompileVariableDecl(sym), null, ctx);
            }
            case PUSHVV -> {
                var sym = script.getSymbolByIndex(top.instruction().data);
                if (sym == null) {
                    throw new IllegalStateException("PUSHVV target not found");
                }

                var ctx = top.context() != null ? this.decompileInstanceDecl(top.context()) : null;
                yield new AccessExpr(this.decompileVariableDecl(sym), new IntExpr(top.instruction().index), ctx);
            }

            default -> throw new IllegalStateException("Unexpected instruction: " + top.instruction().op());
        };
    }

    private void decompileRestOfStack(Block block, Stack<DecompilerStackFrame> stack, boolean beforeLast) {
        var missed = new ArrayList<Expression>();
        while (!stack.empty()) {
            missed.add(this.decompileExpression(stack));
        }
        Collections.reverse(missed);

        if (!missed.isEmpty()) {
            Code last = null;
            if (!block.getCode().isEmpty() && beforeLast) {
                last = block.getCode().remove(block.getCode().size() - 1);
            }
            for (var expr : missed) {
                block.addCode(expr);
            }

            if (last != null) {
                block.addCode(last);
            }
        }
    }

    private BlockDecompilationResult decompileBlock(int baseAddress, int limitAddress, int ignoreFunctionParameters, boolean returnsValue, boolean returnExists) {
        int offset = baseAddress;
        DaedalusSymbol context = null;
        Stack<DecompilerStackFrame> stack = new Stack<>();
        Block block = new Block();

        do {
            DaedalusInstruction instruction = script.getInstruction(offset);
            offset += instruction.size;

            switch (instruction.op()) {
                case MOVI, ADDMOVI, SUBMOVI, MULMOVI, DIVMOVI, MOVS, MOVSS, MOVVF, MOVF, MOVVI -> {
                    if (ignoreFunctionParameters != 0) {
                        stack.pop(); // Target;
                        ignoreFunctionParameters -= 1;
                        break;
                    }

                    var op = AssignOp.fromOpcode(instruction.op());
                    var lhs = this.decompileExpression(stack);
                    if (!(lhs instanceof AccessExpr lhsAccess)) {
                        throw new IllegalStateException("LHS of assignment must be AccessExpr");
                    }

                    var rhs = this.decompileExpression(stack, instruction.op() == DaedalusOpcode.MOVF);
                    this.decompileRestOfStack(block, stack, false);
                    block.addCode(new AssignStmt(lhsAccess, rhs, op));
                }
                case NOP -> {
                    /* ignore */
                }
                case RSR -> {
                    if (returnsValue && !stack.isEmpty()) {
                        // FIXME(lmichaelis): Functions returing float!
                        var value = new ReturnStmt(this.decompileExpression(stack));
                        this.decompileRestOfStack(block, stack, false);
                        block.addCode(value);
                    } else if (!returnExists) {
                        // NOTE(lmichaelis): If return statements exit, but we don't return a value then this
                        //                   must be a redundant `return`-statement at the very end of `void`
                        //                   function, so we can ignore it.
                        this.decompileRestOfStack(block, stack, false);
                        block.addCode(new ReturnStmt(null));
                    } else {
                        this.decompileRestOfStack(block, stack, false);
                    }


                    if (returnExists) {
                        return new BlockDecompilationResult(block, DaedalusOpcode.RSR, offset);
                    }
                }
                case BL -> {
                    stack.push(new DecompilerStackFrame(instruction, context));

                    var sym = script.getSymbolByAddress(instruction.data);
                    if (sym == null) {
                        throw new IllegalStateException("BL target not found");
                    }

                    if (sym.getType() == DaedalusDataType.PROTOTYPE) {
                        stack.pop(); // Don't emit prototype calls
                    } else if (sym.getReturnType() == DaedalusDataType.VOID) {
                        var call = this.decompileExpression(stack);
                        this.decompileRestOfStack(block, stack, false);
                        block.addCode(call);
                    }
                }
                case BE -> {
                    stack.push(new DecompilerStackFrame(instruction, context));

                    var sym = script.getSymbolByIndex(instruction.data);
                    if (sym == null) {
                        throw new IllegalStateException("BE target not found");
                    }

                    if (sym.getReturnType() == DaedalusDataType.VOID) {
                        var call = this.decompileExpression(stack);
                        this.decompileRestOfStack(block, stack, false);
                        block.addCode(call);
                    }
                }
                case B -> {
                    this.decompileRestOfStack(block, stack, false);
                    return new BlockDecompilationResult(block, DaedalusOpcode.B, instruction.data);
                }
                case BZ -> {
                    var condition = this.decompileExpression(stack);
                    var body = this.decompileBlock(offset, instruction.data, 0, returnsValue, false);
                    var if_ = new IfStmt(condition, body.block, null);

                    this.decompileRestOfStack(block, stack, false);
                    block.addCode(if_);

                    offset = instruction.data;
                    if (body.opcode() == DaedalusOpcode.B) {
                        // This is an else block!
                        body = this.decompileBlock(instruction.data, body.address(), 0, returnExists, false);
                        if_.setNext(new ElseStmt(body.block));
                        offset = body.address();
                    } else if (body.opcode() != DaedalusOpcode.NOP) {
                        throw new IllegalStateException("Invalid conditional state");
                    }
                }
                case GMOVI -> {
                    context = script.getSymbolByIndex(instruction.data);
                }
                default -> {
                    stack.push(new DecompilerStackFrame(instruction, context));
                }
            }
        } while (offset < limitAddress);

        this.decompileRestOfStack(block, stack, false);
        return new BlockDecompilationResult(block, DaedalusOpcode.NOP, offset);
    }

    protected void decompilePostprocess(Scope scope, Block block) {
        block.visit(c -> {
            if (c instanceof CallExpr call) {
                // Replace function parameters if required:
                //  - int -> float
                //  - int -> func-ref
                //  - int -> instance
                var params = call.getTarget().getParameters();
                for (int i = 0; i < params.size(); i++) {
                    // Replace integers with floats in calls to functions taking float parameters
                    var param = params.get(i);
                    if (param.getType() == BuiltinDecl.FLOAT && call.getArgument(i) instanceof IntExpr arg) {
                        call.replaceArgument(i, new FloatExpr(Float.intBitsToFloat(arg.getValue())));
                    } else if (options.resolveFunctionReferences() && param.getType() == BuiltinDecl.FUNCTION && call.getArgument(i) instanceof IntExpr arg) {
                        var sym = script.getSymbolByIndex(arg.getValue());
                        if (sym != null && sym.getType() == DaedalusDataType.FUNCTION && sym.isConst()) {
                            call.replaceArgument(i, new FunctionReferenceExpr(this.decompileFunctionDecl(sym)));
                        }
                    } else if (options.resolveInstanceReferences() && param.getType() == BuiltinDecl.INT && call.getArgument(i) instanceof IntExpr arg && arg.getValue() > 100) {
                        var sym = script.getSymbolByIndex(arg.getValue());
                        if (sym != null && sym.getType() == DaedalusDataType.INSTANCE) {
                            var decl = this.decompileInstanceDecl(sym);
                            if (sym.isConst() || topLevelNodes.contains(decl) || (scope != null && scope.hasMember(decl))) {
                                call.replaceArgument(i, new AccessExpr(decl, null, null));
                            }
                        }
                    }
                }
            } else if (c instanceof AssignStmt assign) {
                // Replace assignment values if required
                //  - int -> func
                //  - int -> instance
                if (options.resolveFunctionReferences() && assign.getTarget().getTarget().getType() == BuiltinDecl.FUNCTION && assign.getValue() instanceof IntExpr arg) {
                    var sym = script.getSymbolByIndex(arg.getValue());

                    if (sym != null && sym.getType() == DaedalusDataType.FUNCTION && sym.isConst()) {
                        assign.setValue(new FunctionReferenceExpr(this.decompileFunctionDecl(sym)));
                    }
                } else if (options.resolveInstanceReferences() && assign.getTarget().getTarget().getType() == BuiltinDecl.INT && assign.getValue() instanceof IntExpr arg && arg.getValue() > 100) {
                    var sym = script.getSymbolByIndex(arg.getValue());
                    if (sym != null && sym.getType() == DaedalusDataType.INSTANCE) {
                        var decl = this.decompileInstanceDecl(sym);
                        if (sym.isConst() || topLevelNodes.contains(decl) || (scope != null && scope.hasMember(decl))) {
                            assign.setValue(new AccessExpr(decl, null, null));
                        }
                    }
                }
            } else if (c instanceof IfStmt if_) {
                // Collapse if ... else ... statements to if ... else if ... else where applicable
                if (options.generateElseIf() && if_.getNext() instanceof ElseStmt else_ && else_.getBody().getCode().size() == 1 && else_.getBody().getCode(0) instanceof IfStmt if_nested) {
                    if_.setNext(if_nested);
                }
            }
        });
    }

    public Block decompileCode(@NotNull Decl exec) {
        var sym = exec.getSymbol();
        if (sym == null || sym.isExternal()) {
            return null;
        }

        Block body = null;
        if (exec instanceof FunctionDecl fn) {
            if (fn.getBody() == null) {
                var dcr = this.decompileBlock(sym.getAddress(), Integer.MAX_VALUE, fn.getParameters().size(), fn.getReturnType() != BuiltinDecl.VOID, true);
                fn.setBody(dcr.block);
            }

            body = fn.getBody();
        } else if (exec instanceof PrototypeDecl fn) {
            if (fn.getBody() == null) {
                var dcr = this.decompileBlock(sym.getAddress(), Integer.MAX_VALUE, 0, false, true);
                fn.setBody(dcr.block);
            }

            body = fn.getBody();
        } else if (exec instanceof InstanceDecl fn && sym.isConst()) {
            if (fn.getBody() == null) {
                var dcr = this.decompileBlock(sym.getAddress(), Integer.MAX_VALUE, 0, false, true);
                fn.setBody(dcr.block);
            }

            body = fn.getBody();
        }

        if (body != null) {
            this.decompilePostprocess(exec instanceof Scope s ? s : null, body);
        }

        return body;
    }

    public String[] formatSymbolInfo(@NotNull Decl decl) {
        var orig = decl.getSymbol();
        return String.format("""
                        Name: %s
                        Index: %d
                        Address: %d
                        Size: %d
                        Type: %s
                        Return Type: %s
                        Flags:
                        &nbsp;Const: %b
                        &nbsp;Member: %b
                        &nbsp;External: %b
                        &nbsp;Merged: %b
                        &nbsp;Generated: %b
                        Parent Index: %d
                        Member Offset: %d
                        Class Size: %d
                        
                        File Index: %d
                        Line Start: %d
                        Line Count: %d
                        Char Start: %d
                        Char Count: %d
                        """, orig.getName(), orig.getIndex(), orig.getAddress(), orig.getSize(), orig.getType(), orig.getReturnType(),
                orig.isConst(), orig.isMember(), orig.isExternal(), orig.isMerged(), orig.isGenerated(),
                orig.getParent(), orig.getOffsetAsMember(), orig.getClassSize(), orig.getFileIndex(),
                orig.getLineStart(), orig.getLineCount(), orig.getCharStart(), orig.getCharCount()).split("\n");
    }

    public void setOptions(DecompilerOptions decompilerOptions) {
        this.options = decompilerOptions;

        for (var decl : topLevelNodes) {
            if (decl instanceof FunctionDecl fn) {
                fn.setBody(null);
            } else if (decl instanceof InstanceDecl fn) {
                fn.setBody(null);
            } else if (decl instanceof PrototypeDecl fn) {
                fn.setBody(null);
            }
        }
    }

    private record BlockDecompilationResult(
            Block block,
            DaedalusOpcode opcode,
            int address
    ) {
    }
}
