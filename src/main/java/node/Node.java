package node;

/**
 * @author Rabo
 */
public interface Node {

    Object getValue();

    boolean isValue();

    Node simplify();
}
