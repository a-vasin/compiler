package node;

/**
 * @author Rabo
 */
public class IntNode implements Node {

    public IntNode(Integer value) {
        this.value = value;
    }

    private Integer value;

    @Override
    public Integer getValue() {
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
        if (!(o instanceof IntNode)) return false;

        IntNode intNode = (IntNode) o;

        return value.equals(intNode.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
