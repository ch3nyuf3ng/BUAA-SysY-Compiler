package pcode;

import foundation.Logger;
import pcode.code.*;
import pcode.protocols.PcodeType;

import java.util.*;

public class Interpreter {
    private final List<PcodeType> code;
    private final Map<String, Integer> labelToCodeIndex;
    private final ArrayList<Number> stack;
    private final Scanner scanner;
    private final StringBuilder outputBuilder;
    private int programCounter;
    private int stackPointer;
    private int markPointer;

    public Interpreter(List<PcodeType> code, String inputData) {
        this.code = code;
        labelToCodeIndex = new HashMap<>();
        for (var i = 0; i < code.size(); i += 1) {
            final var codeLine = code.get(i);
            if (codeLine instanceof Label label) {
                labelToCodeIndex.put(label.label(), i);
            }
        }
        stack = new ArrayList<>();
        if (inputData != null) {
            scanner = new Scanner(inputData);
        } else {
            scanner = new Scanner(System.in);
        }
        outputBuilder = new StringBuilder();
        programCounter = 0;
        stackPointer = -1;
        markPointer = -1;
    }

    public void run() {
        while (programCounter < code.size()) {
            final var codeLine = code.get(programCounter);
            if (codeLine instanceof BlockEnd blockEnd) {
                runBlockEnd(blockEnd);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof BlockStart) {
                runBlockStart();
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof CallFunction callFunction) {
                runCallFunction(callFunction);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof Jump jump) {
                runJump(jump);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof JumpIfNonzero jumpIfNonzero) {
                runJumpIfNonZero(jumpIfNonzero);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof JumpIfZero jumpIfZero) {
                runJumpIfZero(jumpIfZero);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof Label label) {
                runLabel(label);
            } else if (codeLine instanceof LoadAddress loadAddress) {
                runLoadAddress(loadAddress);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof LoadImmediate loadImmediate) {
                runLoadImmediate(loadImmediate);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof LoadValue loadValue) {
                runLoadValue(loadValue);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof MemAddZeros memAddZeros) {
                runMemAddZeros(memAddZeros);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof Operate operate) {
                runOperate(operate);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof ReadNumber) {
                runReadNumber();
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof ReturnFunction returnFunction) {
                runReturnFunction(returnFunction);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof StoreValue storeValue) {
                runStoreValue(storeValue);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof StackPointerMove stackPointerMove) {
                runStackPointerMove(stackPointerMove);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof WriteNumber) {
                runWriteNumber();
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof WriteString writeString) {
                runWriteString(writeString);
                if (Logger.LogEnabled) {
                    Logger.debug(toString(), Logger.Category.INTERPRETER);
                }
            } else if (codeLine instanceof DebugPcode debugPcode) {
                runDebug(debugPcode);
            } else {
                throw new RuntimeException();
            }
        }
    }

    public String output() {
        return outputBuilder.toString();
    }

    @Override
    public String toString() {
        return "Interpreter{" + '\n' +
                "  stack=" + stack + '\n' +
                "  currentActiveRecord=" + stack.subList(Math.max(markPointer, 0), stackPointer + 1) + '\n' +
                "  currentArguments=" + stack.subList(Math.min(markPointer + 4, stackPointer + 1), stackPointer + 1)
                + '\n' +
                "  programCounter=" + programCounter + '\n' +
                "  stackPointer=" + stackPointer + '\n' +
                "  markPointer=" + markPointer + '\n' +
                '}';
    }

    private void setStack(int index, Number number) {
        if (stack.size() > index) {
            stack.set(index, number);
        } else {
            while (stack.size() < index) {
                stack.add(0);
            }
            stack.add(number);
        }
    }

    private int returnAddress(int markPointer) {
        return (int) stack.get(markPointer + 1);
    }

    private int staticLink(int markPointer) {
        return (int) stack.get(markPointer + 2);
    }

    private int dynamicLink(int markPointer) {
        return (int) stack.get(markPointer + 3);
    }

    private void runDebug(DebugPcode debugPcode) {
        programCounter += 1;

        if (Logger.LogEnabled) {
            Logger.debug("Debug hint: " + debugPcode.hint(), Logger.Category.INTERPRETER);
        }
    }

    private void runBlockStart() {
        final var returnValue = 0;
        stackPointer += 1;
        setStack(stackPointer, returnValue);

        final var returnAddress = 0;
        stackPointer += 1;
        setStack(stackPointer, returnAddress);

        final var staticLink = markPointer;
        stackPointer += 1;
        setStack(stackPointer, staticLink);

        final var dynamicLink = 0;
        stackPointer += 1;
        setStack(stackPointer, dynamicLink);

        markPointer = stackPointer - 3;
        programCounter += 1;
        if (Logger.LogEnabled) {
            Logger.debug("Block Start.", Logger.Category.INTERPRETER);
        }
    }

    private void runBlockEnd(BlockEnd blockEnd) {
        if (blockEnd.returnValue()) {
            final var returnValue = stack.get(stackPointer);
            stackPointer = markPointer;
            stack.set(stackPointer, returnValue);
        } else {
            stackPointer = markPointer - 1;
        }

        markPointer = staticLink(markPointer);

        // Recycle stack space
        while (stack.size() > stackPointer + 1) {
            stack.remove(stack.size() - 1);
        }

        programCounter += 1;
        if (Logger.LogEnabled) {
            Logger.debug("Block end.", Logger.Category.INTERPRETER);
        }
    }

    private void runCallFunction(CallFunction callFunction) {
        final var arguments = new ArrayList<Number>();
        for (var i = 1; i <= callFunction.parameterCount(); i += 1) {
            arguments.add(stack.get(stackPointer - callFunction.parameterCount() + i));
        }
        for (var i = 0; i < callFunction.parameterCount(); i += 1) {
            stack.remove(stackPointer);
            stackPointer -= 1;
        }

        final var oldMarkPointer = markPointer;
        markPointer = stackPointer + 1;

        final var returnValue = 0;
        stackPointer += 1;
        stack.add(returnValue);

        final var returnAddress = programCounter + 1;
        stackPointer += 1;
        stack.add(returnAddress);

        final var staticLink = 0;
        stackPointer += 1;
        stack.add(staticLink);

        @SuppressWarnings("UnnecessaryLocalVariable") final var dynamicLink = oldMarkPointer; // Set dynamic link for returning function
        stackPointer += 1;
        stack.add(dynamicLink);

        stack.addAll(arguments);
        stackPointer += arguments.size();

        programCounter = labelToCodeIndex.get(callFunction.label());

        if (Logger.LogEnabled) {
            Logger.debug("Called a function: " + callFunction.label(), Logger.Category.INTERPRETER);
        }
    }

    private void runReturnFunction(ReturnFunction returnFunction) {
        if (returnFunction.hasReturnValue()) {
            stack.set(markPointer, stack.get(stackPointer));
        }
        stackPointer = markPointer;
        programCounter = returnAddress(markPointer);
        markPointer = dynamicLink(markPointer);
        while (stack.size() > stackPointer + 1) {
            stack.remove(stack.size() - 1);
        }

        if (Logger.LogEnabled) {
            Logger.debug(
                    "Returned a function "
                            + (returnFunction.hasReturnValue() ? "which has a return value." : "."),
                    Logger.Category.INTERPRETER
            );
        }
    }

    private void runLoadAddress(LoadAddress loadAddress) {
        final var dynamicOffset = (int) stack.get(stackPointer);
        stack.remove(stackPointer);
        stackPointer -= 1;

        var currentMarkPointer = markPointer;
        for (var i = 0; i < loadAddress.level(); i += 1) {
            currentMarkPointer = staticLink(currentMarkPointer);
        }

        final var address = currentMarkPointer + loadAddress.addr() + dynamicOffset;
        stack.add(address);
        stackPointer += 1;

        programCounter += 1;

        if (Logger.LogEnabled) {
            Logger.debug(
                    "Load address from [" + loadAddress.level() + ", " + loadAddress.addr() + "]",
                    Logger.Category.INTERPRETER
            );
        }
    }

    private void runLoadImmediate(LoadImmediate loadImmediate) {
        stack.add(loadImmediate.immediate());
        stackPointer += 1;
        programCounter += 1;
        if (Logger.LogEnabled) {
            Logger.debug(
                    "Load immediate: " + loadImmediate.immediate(),
                    Logger.Category.INTERPRETER
            );
        }
    }

    private void runLoadValue(LoadValue loadValue) {
        final var dynamicOffset = (int) stack.get(stackPointer);
        stack.remove(stackPointer);
        stackPointer -= 1;

        var currentMarkPointer = markPointer;
        if (loadValue.level() == -1) {
            currentMarkPointer = 0;
        } else {
            for (var i = 0; i < loadValue.level(); i += 1) {
                currentMarkPointer = staticLink(currentMarkPointer);
            }
        }

        if (Logger.LogEnabled) {
            Logger.debug(
                    "Load value from [" + loadValue.level() + ", " + loadValue.addr() + "]"
                            + "with offset: " + dynamicOffset + ".",
                    Logger.Category.INTERPRETER
            );
        }

        final var value = stack.get(currentMarkPointer + loadValue.addr() + dynamicOffset);
        stackPointer += 1;
        stack.add(value);

        programCounter += 1;
    }

    private void runStoreValue(StoreValue storeValue) {
        final var dynamicOffset = (int) stack.get(stackPointer);
        stack.remove(stackPointer);
        stackPointer -= 1;

        final var value = stack.get(stackPointer);
        stack.remove(stackPointer);
        stackPointer -= 1;

        var currentMarkPointer = markPointer;
        if (storeValue.level() == -1) {
            currentMarkPointer = 0;
        } else {
            for (var i = 0; i < storeValue.level(); i += 1) {
                currentMarkPointer = staticLink(currentMarkPointer);
            }
        }

        stack.set(currentMarkPointer + storeValue.addr() + dynamicOffset, value);

        programCounter += 1;

        if (Logger.LogEnabled) {
            Logger.debug(
                    "Store value " + value + " to [" + storeValue.level() + ", " + storeValue.addr() + "]"
                            + "with offset: " + dynamicOffset + ".",
                    Logger.Category.INTERPRETER
            );
        }
    }

    private void runJump(Jump jump) {
        programCounter = labelToCodeIndex.get(jump.label());
        if (Logger.LogEnabled) {
            Logger.debug(
                    "Jump to: " + jump.label(), Logger.Category.INTERPRETER
            );
        }
    }

    private void runJumpIfNonZero(JumpIfNonzero jumpIfNonzero) {
        if (((int) stack.get(stackPointer)) != 0) {
            programCounter = labelToCodeIndex.get(jumpIfNonzero.label());
        } else {
            programCounter += 1;
        }

        stack.remove(stackPointer);
        stackPointer -= 1;

        if (Logger.LogEnabled) {
            Logger.debug(
                    "Jump if non zero to: " + jumpIfNonzero.label(), Logger.Category.INTERPRETER
            );
        }
    }

    private void runJumpIfZero(JumpIfZero jumpIfZero) {
        if (((int) stack.get(stackPointer)) == 0) {
            programCounter = labelToCodeIndex.get(jumpIfZero.label());
        } else {
            programCounter += 1;
        }

        stack.remove(stackPointer);
        stackPointer -= 1;

        if (Logger.LogEnabled) {
            Logger.debug(
                    "Jump if zero to: " + jumpIfZero.label(), Logger.Category.INTERPRETER
            );
        }
    }

    private void runReadNumber() {
        stackPointer += 1;
        final var inputNumber = scanner.nextInt();
        stack.add(inputNumber);
        programCounter += 1;

        if (Logger.LogEnabled) {
            Logger.debug(
                    "Read an input number: " + inputNumber,
                    Logger.Category.INTERPRETER
            );
        }
    }

    private void runStackPointerMove(StackPointerMove stackPointerMove) {
        final var originalStackPointer = stackPointer;  // Just for logging.
        stackPointer += stackPointerMove.immediate();
        if (stackPointerMove.immediate() > 0) {
            while (stack.size() <= stackPointer) {
                stack.add(0);
            }
        } else {
            while (stack.size() > stackPointer + 1) {
                stack.remove(stack.size() - 1);
            }
        }

        programCounter += 1;

        if (Logger.LogEnabled) {
            Logger.debug(
                    "Move stack pointer from " + originalStackPointer
                            + " to " + stackPointer + " by " + stackPointerMove.immediate() + ".",
                    Logger.Category.INTERPRETER
            );
        }
    }

    private void runWriteNumber() {
        final var number = stack.get(stackPointer);
        stack.remove(stackPointer);
        stackPointer -= 1;
        outputBuilder.append(number);
        programCounter += 1;

        if (Logger.LogEnabled) {
            Logger.debug("Print Number: " + number, Logger.Category.INTERPRETER);
        }
    }

    private void runWriteString(WriteString writeString) {
        outputBuilder.append(writeString.string());
        programCounter += 1;
        if (Logger.LogEnabled) {
            Logger.debug("Print String: " + writeString.string(), Logger.Category.INTERPRETER);
        }
    }

    private void runLabel(Label label) {
        programCounter += 1;

        if (Logger.LogEnabled) {
            Logger.debug("Skip label: " + label.label(), Logger.Category.INTERPRETER);
        }
    }

    private void runMemAddZeros(MemAddZeros memAddZeros) {
        for (var i = 0; i < memAddZeros.count(); i += 1) {
            stack.add(0);
        }
        stackPointer += memAddZeros.count();
        programCounter += 1;

        if (Logger.LogEnabled) {
            Logger.debug("Added " + memAddZeros.count() + " zero(s).", Logger.Category.INTERPRETER);
        }
    }

    private void runOperate(Operate operate) {
        // OPR 指令结束后栈顶会移除操作数，保留一个运算结果。
        final int result, operand, lhs, rhs;
        operand = (int) stack.get(stackPointer);
        lhs = (int) stack.get(stackPointer - 1);
        rhs = (int) stack.get(stackPointer);
        switch (operate.opcode()) {
            case LOGICAL_NOT -> {
                final var toBool = operand != 0;
                result = !toBool ? 1 : 0;
                stack.set(stackPointer, result);
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: !" + operand + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case PLUS -> {
                result = lhs + rhs;
                stack.set(stackPointer - 1, result);
                stack.remove(stackPointer);
                stackPointer -= 1;
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: " + lhs + "+" + rhs + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case MINUS -> {
                result = lhs - rhs;
                stack.set(stackPointer - 1, result);
                stack.remove(stackPointer);
                stackPointer -= 1;
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: " + lhs + "-" + rhs + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case DIVIDE -> {
                result = lhs / rhs;
                stack.set(stackPointer - 1, result);
                stack.remove(stackPointer);
                stackPointer -= 1;
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: " + lhs + "/" + rhs + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case MODULUS -> {
                result = lhs % rhs;
                stack.set(stackPointer - 1, result);
                stack.remove(stackPointer);
                stackPointer -= 1;
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: " + lhs + "%" + rhs + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case MULTIPLY -> {
                result = lhs * rhs;
                stack.set(stackPointer - 1, result);
                stack.remove(stackPointer);
                stackPointer -= 1;
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: " + lhs + "*" + rhs + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case NEGATIVE -> {
                result = -operand;
                stack.set(stackPointer, result);
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: -" + operand + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case LOGICAL_AND -> {
                result = lhs > 0 && rhs > 0 ? 1 : 0;
                stack.set(stackPointer - 1, result);
                stack.remove(stackPointer);
                stackPointer -= 1;
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: " + lhs + "&&" + rhs + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case LOGICAL_OR -> {
                result = lhs > 0 || rhs > 0 ? 1 : 0;
                stack.set(stackPointer - 1, result);
                stack.remove(stackPointer);
                stackPointer -= 1;
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: " + lhs + "||" + rhs + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case EQUAL -> {
                result = lhs == rhs ? 1 : 0;
                stack.set(stackPointer - 1, result);
                stack.remove(stackPointer);
                stackPointer -= 1;
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: " + lhs + "==" + rhs + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case NOT_EQUAL -> {
                result = lhs != rhs ? 1 : 0;
                stack.set(stackPointer - 1, result);
                stack.remove(stackPointer);
                stackPointer -= 1;
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: " + lhs + "!=" + rhs + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case LESS -> {
                result = lhs < rhs ? 1 : 0;
                stack.set(stackPointer - 1, result);
                stack.remove(stackPointer);
                stackPointer -= 1;
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: " + lhs + "<" + rhs + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case LESS_OR_EQUAL -> {
                result = lhs <= rhs ? 1 : 0;
                stack.set(stackPointer - 1, result);
                stack.remove(stackPointer);
                stackPointer -= 1;
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: " + lhs + "<=" + rhs + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case GREATER -> {
                result = lhs > rhs ? 1 : 0;
                stack.set(stackPointer - 1, result);
                stack.remove(stackPointer);
                stackPointer -= 1;
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: " + lhs + ">" + rhs + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            case GREATER_OR_EQUAL -> {
                result = lhs >= rhs ? 1 : 0;
                stack.set(stackPointer - 1, result);
                stack.remove(stackPointer);
                stackPointer -= 1;
                if (Logger.LogEnabled) {
                    Logger.debug("Operate: " + lhs + ">=" + rhs + "=" + result, Logger.Category.INTERPRETER);
                }
            }
            default -> {
            }
        }
        programCounter += 1;
    }
}
