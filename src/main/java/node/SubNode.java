package node;

/**
 * @author Rabo
 */
public class SubNode extends OperationNode {

    public SubNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public Node simplify() {
        left = left.simplify();
        right = right.simplify();

        if (left instanceof IntNode && right instanceof IntNode) {
            Integer a = ((IntNode) left).getValue();
            Integer b = ((IntNode) right).getValue();
            return new IntNode(a - b);
        }

        if (left.equals(right)) {
            return new IntNode(0);
        }

        if (right instanceof IntNode && ((IntNode) right).getValue().equals(0)) {
            return left;
        }

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubNode)) return false;

        SubNode subNode = (SubNode) o;

        return left.equals(subNode.left) && right.equals(subNode.right);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }
}
