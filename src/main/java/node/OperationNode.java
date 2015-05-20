package node;

/**
 * @author Rabo
 */
public abstract class OperationNode implements Node {

    protected Node left, right;

    public OperationNode(Node left, Node right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Object getValue() {
        throw new UnsupportedOperationException("Operation do not have a value");
    }

    @Override
    public boolean isValue() {
        return false;
    }
}
