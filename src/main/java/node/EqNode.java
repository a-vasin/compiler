package node;

/**
 * @author Rabo
 */
public class EqNode extends OperationNode {
    public EqNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public Node simplify() {
        left = left.simplify();
        right = right.simplify();

        if (left instanceof IntNode && right instanceof IntNode) {
            Integer a = ((IntNode) left).getValue();
            Integer b = ((IntNode) right).getValue();
            return new BoolNode(a.equals(b));
        }

        if (left instanceof StringNode && right instanceof StringNode) {
            String a = ((StringNode) left).getValue();
            String b = ((StringNode) right).getValue();
            return new BoolNode(a.equals(b));
        }

        if (left instanceof BoolNode && right instanceof BoolNode) {
            Boolean a = ((BoolNode) left).getValue();
            Boolean b = ((BoolNode) right).getValue();
            return new BoolNode(a.booleanValue() == b.booleanValue());
        }

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EqNode)) return false;

        EqNode eqNode = (EqNode) o;

        return left.equals(eqNode.left) && right.equals(eqNode.right)
                || left.equals(eqNode.right) && right.equals(eqNode.left);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }
}
