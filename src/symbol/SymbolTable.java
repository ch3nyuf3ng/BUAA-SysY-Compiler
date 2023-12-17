package symbol;

import symbol.protocols.SymbolType;

import java.util.*;

public class SymbolTable {
    private final SymbolTable outerScopeSymbolTable;
    private final Map<String, SymbolType> currentScopeSymbols;
    private final List<SymbolTable> innerScopeSymbolTables;
    private int activeRecordOffset;

    public SymbolTable(SymbolTable outerScopeSymbolTable) {
        this.outerScopeSymbolTable = outerScopeSymbolTable;
        currentScopeSymbols = new HashMap<>();
        innerScopeSymbolTables = new ArrayList<>();
        activeRecordOffset = 4;
    }

    public void addSymbol(SymbolType symbol) {
        currentScopeSymbols.put(symbol.identifier(), symbol);
        if (symbol instanceof VariableSymbol variableSymbol) {
            activeRecordOffset += variableSymbol.metadata().totalSize();
        } else if (symbol instanceof FunctionParameterSymbol) {
            activeRecordOffset += 1;
        }
    }

    public void addInnerScopeSymbolTable(SymbolTable symbolTable) {
        innerScopeSymbolTables.add(symbolTable);
    }

    public Optional<SymbolType> findSymbol(String identifier) {
        return Optional.ofNullable(currentScopeSymbols.getOrDefault(identifier, null));
    }

    public List<SymbolTable> innerScopeSymbolTables() {
        return Collections.unmodifiableList(innerScopeSymbolTables);
    }

    public Optional<SymbolTable> outerScopeSymbolTable() {
        return Optional.ofNullable(outerScopeSymbolTable);
    }

    public int activeRecordOffset() {
        return activeRecordOffset;
    }
}
