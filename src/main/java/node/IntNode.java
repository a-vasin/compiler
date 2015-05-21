package node;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rabo
 */
public class IntNode implements Node {

    public IntNode(Integer value) {
        this.value = value;
    }

    private Integer value;

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public boolean isValue() {
        return true;
    }

    @Override
    public Node simplify() {
        return this;
    }

    @Override
    public Pair<int[], Pair<Type, List<String>>> generateCode(int varCounter, int helpCounter, int constCounter, List<String> constants) {
        List<String> code = new ArrayList<>();
        code.add("\t%tmp" + varCounter++ + " = alloca i32");
        code.add("\tstore i32 " + value + ", i32* %tmp" + (varCounter - 1));
        code.add("\t%tmp" + varCounter++ + " = load i32* %tmp" + (varCounter - 2));
        return new Pair<>(new int[]{varCounter, helpCounter, constCounter}, new Pair<>(Type.INT, code));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntNode)) return false;

        IntNode intNode = (IntNode) o;

        return value.equals(intNode.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
