package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.ast.statements.StatementList;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.*;

public class Script {

    private final Map<BlockName, NamedBlock> handlers = new HashMap<>();
    private final Map<BlockName, Integer> handlerStartingLine = new HashMap<>();
    private final Map<BlockName, Integer> handlerEndingLine = new HashMap<>();
    private final Map<BlockName, UserFunction> functions = new HashMap<>();
    private StatementList statements = null;
    private Collection<Integer> appliedBreakpoints = new ArrayList<>();
    
    public Script () {
    }

    public Script(ParserRuleContext context, Statement statement) {
        insertStatement(context, statement);
    }

    public void defineHandler (NamedBlock handler, int startingLine, int endingLine) {
        BlockName name = new BlockName(handler.name);

        handlers.put(name, handler);
        handlerStartingLine.put(name, startingLine);
        handlerEndingLine.put(name, endingLine);
    }
    
    public void defineUserFunction (UserFunction function, int startingLine, int endingLine) {
        BlockName name = new BlockName(function.name);

        functions.put(name, function);
        handlerStartingLine.put(name, startingLine);
        handlerEndingLine.put(name, endingLine);
    }

    public Script insertStatement(ParserRuleContext context, Statement statement) {
        if (this.statements == null) {
            this.statements = new StatementList(context);
        }

        this.statements.list.add(0, statement);
        return this;
    }

    public NamedBlock getHandler(String handler) {
        return handlers.get(new BlockName(handler));
    }

    public Collection<String> getHandlers() {
        ArrayList<String> names = new ArrayList<>();
        for (BlockName thisBlock : handlers.keySet()) {
            names.add(thisBlock.name);
        }
        return names;
    }

    public Collection<String> getFunctions() {
        ArrayList<String> names = new ArrayList<>();
        for (BlockName thisBlock : functions.keySet()) {
            names.add(thisBlock.name);
        }
        return names;
    }

    public Integer getLineNumberForNamedBlock(String name) {
        return handlerStartingLine.get(new BlockName(name));
    }

    public String getNamedBlockForLine(int line) {
        ArrayList<BlockName> allBlocks = new ArrayList<>();
        allBlocks.addAll(handlers.keySet());
        allBlocks.addAll(functions.keySet());

        for (BlockName thisHandler : allBlocks) {
            int startingLine = handlerStartingLine.get(thisHandler);
            int endingLine = handlerEndingLine.get(thisHandler);

            if (line >= startingLine && line <= endingLine) {
                return thisHandler.name;
            }
        }

        return null;
    }

    public UserFunction getFunction(String function) {
        return functions.get(new BlockName(function));
    }

    public StatementList getStatements() {
        return statements;
    }

    public void applyBreakpoints(Collection<Integer> breakpointLines) {

        // Clear previously-applied breakpoints
        for (int oldBreakpoint : appliedBreakpoints) {
            for (Statement found : findStatementsOnLine(oldBreakpoint + 1)) {
                found.setBreakpoint(false);
            }
        }

        // Apply the new set
        for (int line : breakpointLines) {
            for (Statement found : findStatementsOnLine(line + 1)) {
                found.setBreakpoint(true);
            }
        }

        this.appliedBreakpoints = breakpointLines;
    }

    public Collection<Statement> findStatementsOnLine(int line) {
        ArrayList<Statement> foundStatements = new ArrayList<>();

        for (NamedBlock block : handlers.values()) {
            foundStatements.addAll(block.findStatementsOnLine(line));
        }

        for (UserFunction function : functions.values()) {
            foundStatements.addAll(function.findStatementsOnLine(line));
        }

        return foundStatements;
    }

    private class BlockName {
        private final String name;

        public BlockName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return this.name.equalsIgnoreCase(((BlockName) o).name);
        }

        @Override
        public int hashCode() {
            return this.name.toLowerCase().hashCode();
        }
    }
}
