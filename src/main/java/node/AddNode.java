package node;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rabo
 */
public class AddNode extends OperationNode {

    public AddNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public Node simplify() {
        left = left.simplify();
        right = right.simplify();

        if (left instanceof IntNode && right instanceof IntNode) {
            Integer a = ((IntNode) left).getValue();
            Integer b = ((IntNode) right).getValue();
            return new IntNode(a + b);
        }

        if (left instanceof StringNode && right instanceof StringNode) {
            String a = ((StringNode) left).getValue();
            String b = ((StringNode) right).getValue();
            return new StringNode(a + b);
        }

        if (left.equals(right) && (left instanceof IntNode || left instanceof IdNode && ((IdNode)left).getType() == Type.INT)) {
            return new MultNode(left, new IntNode(2));
        }

        if (left instanceof IntNode && ((IntNode) left).getValue().equals(0)) {
            return right;
        }

        if (right instanceof IntNode && ((IntNode) right).getValue().equals(0)) {
            return left;
        }

        if (left instanceof StringNode && ((StringNode) left).getValue().equals("")) {
            return right;
        }

        if (right instanceof StringNode && ((StringNode) right).getValue().equals("")) {
            return left;
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
        if (leftGenerate.getValue().getKey() == Type.INT && rightGenerate.getValue().getKey() == Type.INT ) {
            code.add("\t%tmp" + varCounter++ + " = add i32 %tmp" + leftCounter + ", %tmp" + rightCounter);
            return new Pair<>(new int[]{varCounter, helpCounter, constCounter}, new Pair<>(Type.INT, code));
        } else if (leftGenerate.getValue().getKey() == Type.STRING && rightGenerate.getValue().getKey() == Type.STRING) {
            code.add("\t%tmp" + varCounter++ + " = alloca [256 x i8]");
            code.add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 0");
            code.add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + leftCounter + ", i32 0, i32 0");
            code.add("\tcall i8* @strcpy(i8* %help_tmp" + (helpCounter - 2) + ", i8* %help_tmp" + (helpCounter - 1) + ") nounwind");
            code.add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + rightCounter + ", i32 0, i32 0");
            code.add("\tcall i8* @strcat(i8* %help_tmp" + (helpCounter - 3) + ", i8* %help_tmp" + (helpCounter - 1) + ") nounwind");
            return new Pair<>(new int[]{varCounter, helpCounter, constCounter}, new Pair<>(Type.STRING, code));
        } else {
            throw new RuntimeException("Unexpected behavior");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddNode)) return false;

        AddNode addNode = (AddNode) o;

        return left.equals(addNode.left) && right.equals(addNode.right)
                || left.equals(addNode.right) && right.equals(addNode.left);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }
}
