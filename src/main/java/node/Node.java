package node;

import javafx.util.Pair;

import java.util.List;

/**
 * @author Rabo
 */
public interface Node {

    Object getValue();

    boolean isValue();

    Node simplify();

    Pair<int[], Pair<Type, List<String>>> generateCode(int varCounter, int helpCounter, int constCounter, List<String> constants);
}
