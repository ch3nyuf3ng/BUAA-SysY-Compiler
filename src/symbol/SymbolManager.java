package symbol;


import symbol.protocols.SymbolType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SymbolManager {
    private SymbolTable currentSymbolTable;
    private int currentDepth;
    private final List<Integer> innerSymbolTableIndexes;
    private final List<Integer> loopIndexes;
    private final List<Integer> loopDepths;
    private int loopCount;
    private int ifCount;
    private int andCount;
    private int orCount;
    private FunctionSymbol currentDefiningFunction;

    public SymbolManager() {
        currentDepth = 0;
        innerSymbolTableIndexes = new ArrayList<>();
        innerSymbolTableIndexes.add(0);
        loopIndexes = new ArrayList<>();
        loopDepths = new ArrayList<>();
        loopCount = 0;
        ifCount = 0;
        andCount = 0;
        orCount = 0;
        currentSymbolTable = new SymbolTable(null);
        currentDefiningFunction = null;
    }

    public FunctionSymbol getCurrentDefiningFunction() {
        return currentDefiningFunction;
    }

    public void setCurrentDefiningFunction(FunctionSymbol currentDefiningFunction) {
        this.currentDefiningFunction = currentDefiningFunction;
    }

    public void delCurrentDefiningFunction() {
        currentDefiningFunction = null;
    }

    public int currentDepth() {
        return currentDepth;
    }

    public int ifCount() {
        return ifCount;
    }

    public void increaseIfCount() {
        ifCount += 1;
    }

    public Optional<Integer> loopIndex() {
        if (loopIndexes.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(loopIndexes.get(loopIndexes.size() - 1));
        }
    }

    public Optional<Integer> loopDepth() {
        if (loopIndexes.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(loopDepths.get(loopDepths.size() - 1));
        }
    }

    public int createLoopIndex() {
        loopIndexes.add(loopCount);
        loopDepths.add(currentDepth);
        loopCount += 1;
        return loopCount - 1;
    }

    public void discardLoopIndex() {
        loopIndexes.remove(loopIndexes.size() - 1);
        loopDepths.remove(loopDepths.size() - 1);
    }

    public int andCount() {
        return andCount;
    }

    public void increaseAndCount() {
        andCount += 1;
    }

    public int orCount() {
        return orCount;
    }

    public void increaseOrCount() {
        orCount += 1;
    }

    public int activeRecordOffset() {
        return currentSymbolTable.activeRecordOffset();
    }

    public void createSymbolTable() {
        currentDepth += 1;
        final var newSymbolTable = new SymbolTable(currentSymbolTable);
        currentSymbolTable.addInnerScopeSymbolTable(newSymbolTable);
        maintainIndexWhenEnterTable(innerSymbolTableIndexes);
        currentSymbolTable = newSymbolTable;
    }

    private void maintainIndexWhenEnterTable(List<Integer> indexes) {
        if (indexes.size() == currentDepth) {
            indexes.add(0);
        } else if (indexes.size() == currentDepth + 1) {
            indexes.set(currentDepth, indexes.get(currentDepth) + 1);
        } else {
            throw new RuntimeException();
        }
    }

    public void tracebackSymbolTable() {
        if (currentSymbolTable.outerScopeSymbolTable().isPresent()) {
            maintainIndexWhenExitTable(innerSymbolTableIndexes);
            currentDepth -= 1;
            currentSymbolTable = currentSymbolTable.outerScopeSymbolTable().get();
        } else {
            throw new RuntimeException();
        }
    }

    private void maintainIndexWhenExitTable(List<Integer> indexes) {
        while (indexes.size() > currentDepth + 1) {
            indexes.remove(currentDepth + 1);
        }
    }

    public void addSymbol(SymbolType symbol) {
        if (findSymbol(symbol.identifier(), false).isPresent()) {
            throw new RuntimeException();
        }
        currentSymbolTable.addSymbol(symbol);
    }

    public Optional<SymbolType> findSymbol(String identifier, boolean isRecursively) {
        if (!isRecursively) {
            return currentSymbolTable.findSymbol(identifier);
        }
        var symbolTableToFind = currentSymbolTable;
        var symbolToFind = symbolTableToFind.findSymbol(identifier);
        while (symbolToFind.isEmpty()) {
            if (symbolTableToFind.outerScopeSymbolTable().isEmpty()) {
                return Optional.empty();
            }
            symbolTableToFind = symbolTableToFind.outerScopeSymbolTable().get();
            symbolToFind = symbolTableToFind.findSymbol(identifier);
        }
        return symbolToFind;
    }

    public List<Integer> innerSymbolTableIndex() {
        return Collections.unmodifiableList(innerSymbolTableIndexes.subList(0, currentDepth + 1));
    }
}
