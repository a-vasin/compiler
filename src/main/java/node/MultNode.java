package node;

/**
 * @author Rabo
 */
public class MultNode extends OperationNode {

    public MultNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public Node simplify() {
        left = left.simplify();
        right = right.simplify();

        if (left instanceof IntNode && right instanceof IntNode) {
            Integer a = ((IntNode) left).getValue();
            Integer b = ((IntNode) right).getValue();
            return new IntNode(a * b);
        }

        if (left instanceof IntNode && ((IntNode) left).getValue().equals(0)) {
            return new IntNode(0);
        }

        if (right instanceof IntNode && ((IntNode) right).getValue().equals(0)) {
            return new IntNode(0);
        }

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultNode)) return false;

        MultNode multNode = (MultNode) o;

        return left.equals(multNode.left) && right.equals(multNode.right)
                || left.equals(multNode.right) && right.equals(multNode.left);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }
}
