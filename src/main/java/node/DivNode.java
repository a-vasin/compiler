package node;

/**
 * @author Rabo
 */
public class DivNode extends OperationNode {

    public DivNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public Node simplify() {
        left = left.simplify();
        right = right.simplify();

        if (left instanceof IntNode && right instanceof IntNode) {
            Integer a = ((IntNode) left).getValue();
            Integer b = ((IntNode) right).getValue();
            return new IntNode(a / b);
        }

        if (left.equals(right)) {
            return new IntNode(1);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DivNode)) return false;

        DivNode divNode = (DivNode) o;

        return left.equals(divNode.left) && right.equals(divNode.right);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }
}
