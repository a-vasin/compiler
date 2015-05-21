package node;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rabo
 */
public class BoolNode implements Node {

    public BoolNode(Boolean value) {
        this.value = value;
    }

    private Boolean value;

    @Override
    public Boolean getValue() {
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
        code.add("\t%tmp" + varCounter++ + " = alloca i1");
        code.add("\tstore i1 " + (value ? 1 : 0) + ", i1* %tmp" + (varCounter - 1));
        code.add("\t%tmp" + varCounter++ + " = load i1* %tmp" + (varCounter - 2));
        return new Pair<>(new int[]{varCounter, helpCounter, constCounter}, new Pair<>(Type.BOOLEAN, code));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoolNode)) return false;

        BoolNode boolNode = (BoolNode) o;

        return value.equals(boolNode.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
