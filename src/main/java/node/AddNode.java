package node;

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

        if (left.equals(right) && (left instanceof IntNode)) {
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
