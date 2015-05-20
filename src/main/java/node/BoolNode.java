package node;

/**
 * @author Rabo
 */
public class BoolNode implements Node {

    public BoolNode(Boolean value) {
        this.value = value;
    }

    private Boolean value;

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public boolean isValue() {
        return true;
    }

    @Override
    public Node simplify() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoolNode)) return false;

        BoolNode boolNode = (BoolNode) o;

        return value.equals(boolNode.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
