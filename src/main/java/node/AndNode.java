package node;

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

        return this;
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
