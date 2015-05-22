import antlr.ProgrammingLanguageLexer;
import antlr.ProgrammingLanguageParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        CharStream input = new ANTLRFileStream(args[0]);
        ProgrammingLanguageLexer lexer = new ProgrammingLanguageLexer(input);
        TokenStream tokens = new CommonTokenStream(lexer);
        ProgrammingLanguageParser parser = new ProgrammingLanguageParser(tokens);

        PrintWriter printWriter = new PrintWriter(new FileWriter(args[1]));

        ParseTree tree = parser.parse();
        List<String> code = new Vizitor().visit(tree).getKey().getValue();
        for (String line : code) {
            printWriter.println(line);
        }

        printWriter.close();
    }
}
