package com.defano.hypertalk.ast.common;

import com.defano.hypertalk.ast.functions.UserFunction;
import com.defano.hypertalk.ast.statements.StatementList;

import java.util.*;

public class Script {

    private final Map<BlockName, NamedBlock> handlers = new HashMap<>();
    private final Map<BlockName, Integer> handlerStartingLine = new HashMap<>();
    private final Map<BlockName, Integer> handlerEndingLine = new HashMap<>();
    private final Map<BlockName, UserFunction> functions = new HashMap<>();
    private StatementList statements = null;
    
    public Script () {
    }

    public Script(NamedBlock handler, int startingLine, int endingLine) {
        defineHandler(handler, startingLine, endingLine);
    }

    public Script defineHandler (NamedBlock handler, int startingLine, int endingLine) {
        BlockName name = new BlockName(handler.name);

        handlers.put(name, handler);
        handlerStartingLine.put(name, startingLine);
        handlerEndingLine.put(name, endingLine);
        return this;
    }
    
    public Script defineUserFunction (UserFunction function, int startingLine, int endingLine) {
        BlockName name = new BlockName(function.name);

        functions.put(name, function);
        handlerStartingLine.put(name, startingLine);
        handlerEndingLine.put(name, endingLine);
        return this;
    }
    
    public Script defineStatementList (StatementList statements) {
        this.statements = statements;
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
