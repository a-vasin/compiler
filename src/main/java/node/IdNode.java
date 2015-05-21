package node;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rabo
 */
public class IdNode implements Node {

    private String id;
    private Type type;
    private int counter;

    public IdNode(String id, Type type, int counter) {
        this.id = id;
        this.type = type;
        this.counter = counter;
    }

    @Override
    public Object getValue() {
        return id;
    }

    @Override
    public boolean isValue() {
        return true;
    }

    @Override
    public Node simplify() {
        return this;
    }

    private String getLLVMType(Type type) {
        switch (type) {
            case VOID:
                return "void";
            case INT:
                return "i32";
            case BOOLEAN:
                return "i1";
            case STRING:
                return "[256 x i8]";
        }

        throw new IllegalArgumentException();
    }

    @Override
    public Pair<int[], Pair<Type, List<String>>> generateCode(int varCounter, int helpCounter, int constCounter, List<String> constants) {
        List<String> code = new ArrayList<>();
        code.add("\t%tmp" + varCounter++ + " = load " + getLLVMType(type) + "* %var_" + id + counter);
        if (type == Type.STRING) {
            code.add("\t%tmp" + varCounter++ + " = alloca [256 x i8]");
            code.add("\tstore [256 x i8] %tmp" + (varCounter - 2) + ", [256 x i8]* %tmp" + (varCounter - 1));
        }
        return new Pair<>(new int[]{varCounter, helpCounter, constCounter}, new Pair<>(type, code));
    }
}
