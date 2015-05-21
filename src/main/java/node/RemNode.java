package node;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rabo
 */
public class RemNode extends OperationNode {

    public RemNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public Node simplify() {
        left = left.simplify();
        right = right.simplify();

        if (left instanceof IntNode && right instanceof IntNode) {
            Integer a = ((IntNode) left).getValue();
            Integer b = ((IntNode) right).getValue();
            return new IntNode(a % b);
        }

        if (left.equals(right)) {
            return new IntNode(0);
        }

        if (left instanceof IntNode && ((IntNode) left).getValue().equals(0)) {
            return new IntNode(0);
        }

        if (right instanceof IntNode && ((IntNode) right).getValue().equals(0)) {
            throw new IllegalArgumentException("Division by zero");
        }

        return this;
    }

    @Override
    public Pair<int[], Pair<Type, List<String>>> generateCode(int varCounter, int helpCounter, int constCounter, List<String> constants) {
        List<String> code = new ArrayList<>();
        Pair<int[], Pair<Type, List<String>>> leftGenerate = left.generateCode(varCounter, helpCounter, constCounter, constants);
        varCounter = leftGenerate.getKey()[0];
        helpCounter = leftGenerate.getKey()[1];
        constCounter = leftGenerate.getKey()[2];
        int leftCounter = varCounter - 1;
        code.addAll(leftGenerate.getValue().getValue());
        Pair<int[], Pair<Type, List<String>>> rightGenerate = right.generateCode(varCounter, helpCounter, constCounter, constants);
        varCounter = rightGenerate.getKey()[0];
        helpCounter = rightGenerate.getKey()[1];
        constCounter = rightGenerate.getKey()[2];
        int rightCounter = varCounter - 1;
        code.addAll(leftGenerate.getValue().getValue());
        code.add("\t%tmp" + varCounter++ + " = srem i32 %tmp" + leftCounter + ", %tmp" + rightCounter);
        return new Pair<>(new int[]{varCounter, helpCounter, constCounter}, new Pair<>(Type.INT, code));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemNode)) return false;

        RemNode remNode = (RemNode) o;

        return left.equals(remNode.left) && right.equals(remNode.right);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }
}
