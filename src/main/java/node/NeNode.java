package node;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rabo
 */
public class NeNode extends OperationNode {
    public NeNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public Node simplify() {
        left = left.simplify();
        right = right.simplify();

        if (left instanceof IntNode && right instanceof IntNode) {
            Integer a = ((IntNode) left).getValue();
            Integer b = ((IntNode) right).getValue();
            return new BoolNode(!a.equals(b));
        }

        if (left instanceof StringNode && right instanceof StringNode) {
            String a = ((StringNode) left).getValue();
            String b = ((StringNode) right).getValue();
            return new BoolNode(!a.equals(b));
        }

        if (left instanceof BoolNode && right instanceof BoolNode) {
            Boolean a = ((BoolNode) left).getValue();
            Boolean b = ((BoolNode) right).getValue();
            return new BoolNode(a.booleanValue() != b.booleanValue());
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
            code.add("\t%tmp" + varCounter++ + " = icmp " + "ne" + " i1 %tmp" + leftCounter + ", %tmp" + rightCounter);
            return new Pair<>(new int[]{varCounter, helpCounter, constCounter}, new Pair<>(Type.BOOLEAN, code));
        } else
        if (leftGenerate.getValue().getKey() == Type.INT && rightGenerate.getValue().getKey() == Type.INT ) {
            code.add("\t%tmp" + varCounter++ + " = icmp " + "ne" + " i32 %tmp" + leftCounter + ", %tmp" + rightCounter);
            return new Pair<>(new int[]{varCounter, helpCounter, constCounter}, new Pair<>(Type.BOOLEAN, code));
        } else if (leftGenerate.getValue().getKey() == Type.STRING && rightGenerate.getValue().getKey() == Type.STRING) {
            code.add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 0");
            code.add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + leftCounter + ", i32 0, i32 0");
            code.add("\t%tmp" + varCounter++ + " = call i32 @strcmp(i8* %help_tmp" + (helpCounter - 2) + ", i8* %help_tmp" + (helpCounter - 1) + ") nounwind readonly");
            code.add("\t%tmp" + varCounter++ + " = icmp " + "ne" + " i32 0, %tmp" + (varCounter - 1));
            return new Pair<>(new int[]{varCounter, helpCounter, constCounter}, new Pair<>(Type.BOOLEAN, code));
        } else {
            throw new RuntimeException("Unexpected behavior");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NeNode)) return false;

        NeNode neNode = (NeNode) o;

        return left.equals(neNode.left) && right.equals(neNode.right)
                || left.equals(neNode.right) && right.equals(neNode.left);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }
}
