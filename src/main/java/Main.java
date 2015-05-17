import antlr.ProgrammingLanguageLexer;
import antlr.ProgrammingLanguageParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        CharStream input = new ANTLRFileStream("test.txt");
        ProgrammingLanguageLexer lexer = new ProgrammingLanguageLexer(input);
        TokenStream tokens = new CommonTokenStream(lexer);
        ProgrammingLanguageParser parser = new ProgrammingLanguageParser(tokens);

        ParseTree tree = parser.parse();
        List<String> code = new Vizitor().visit(tree).getValue();
        for (String line : code) {
            System.out.println(line);
        }
    }
}
