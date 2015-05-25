package node;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rabo
 */
public class AndNode extends OperationNode {

    public AndNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public Node simplify() {
        left = left.simplify();
        right = right.simplify();

        if (left instanceof BoolNode && right instanceof BoolNode) {
            Boolean a = ((BoolNode) left).getValue();
            Boolean b = ((BoolNode) right).getValue();
            return new BoolNode(a && b);
        }

        if (left instanceof BoolNode && !((BoolNode) left).getValue()
                || right instanceof BoolNode && !((BoolNode) right).getValue()) {
            return new BoolNode(false);
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
        code.addAll(rightGenerate.getValue().getValue());
        if (leftGenerate.getValue().getKey() == Type.BOOLEAN && rightGenerate.getValue().getKey() == Type.BOOLEAN) {
            code.add("\t%tmp" + varCounter++ + " = and" + " i1 %tmp" + leftCounter + ", %tmp" + rightCounter);
            return new Pair<>(new int[]{varCounter, helpCounter, constCounter}, new Pair<>(Type.BOOLEAN, code));
        } else {
            throw new RuntimeException("Unexpected behavior");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AndNode)) return false;

        AndNode andNode = (AndNode) o;

        return left.equals(andNode.left) && right.equals(andNode.right)
                || left.equals(andNode.right) && right.equals(andNode.left);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }
}
