package node;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rabo
 */
public class StringNode implements Node {

    public StringNode(String value) {
        this.value = value;
    }

    private String value;

    @Override
    public String getValue() {
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

    private int countNewStrings(String str) {
        int result = 0;
        for (int i = 0; i < str.length() - 2; ++i) {
            if (str.substring(i, i + 3).equals("\\0A")) {
                result += 1;
            }
        }
        return result;
    }

    @Override
    public Pair<int[], Pair<Type, List<String>>> generateCode(int varCounter, int helpCounter, int constCounter, List<String> constants) {
        List<String> code = new ArrayList<>();
        if (value.length() > 255) {
            value = value.substring(0, 255);
        }

        String constant = "@.str" + constCounter++ + " = private unnamed_addr constant [256 x i8] c\"" + value;
        int newStr = countNewStrings(value);
        for (int i = 0; i < 256 - value.length() + newStr * 2; ++i) {
            constant += "\\00";
        }
        constant += "\", align 1";
        constants.add(constant);
        code.add("\t%tmp" + varCounter++ + " = alloca [256 x i8]");
        code.add("\t%help_tmp" + helpCounter++ + " = bitcast [256 x i8]* %tmp" + (varCounter - 1) + " to i8*");
        code.add("\tcall void @llvm.memcpy.p0i8.p0i8.i32(i8* %help_tmp" + (helpCounter - 1) + ", i8* getelementptr inbounds ([256 x i8]* @.str" + (constCounter - 1) + ", i32 0, i32 0), i32 256, i32 1, i1 false)");
        int[] counters = {varCounter, helpCounter, constCounter};
        return new Pair<>(counters, new Pair<>(Type.STRING, code));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringNode)) return false;

        StringNode that = (StringNode) o;

        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
