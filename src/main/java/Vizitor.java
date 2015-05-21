import antlr.ProgrammingLanguageBaseVisitor;
import antlr.ProgrammingLanguageParser;
import javafx.util.Pair;
import node.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

/**
 * @author Rabo
 */
public class Vizitor extends ProgrammingLanguageBaseVisitor<Pair<Pair<Type, List<String>>, Node>> {

    private int varCounter = 0;
    private int helpCounter = 0;
    private int labelCounter = 0;
    private int constCounter = 0;
    private Map<String, Type> variables = new HashMap<>();
    private Map<String, Integer> nameCounter = new HashMap<>();
    private Map<String, List<String>> unionMap = new HashMap<>();
    private Map<String, Pair<Type, List<Type>>> functions = new HashMap<>();
    private Stack<Integer> endLabels = new Stack<>();
    private Stack<Integer> startWhileLabels = new Stack<>();
    private List<String> constants = new ArrayList<>();

    private String getLLVMType(Type type) {
        switch (type) {
            case VOID:
                return "void";
            case INT:
                return "i32";
            case BOOLEAN:
                return "i1";
            case STRING:
                return "[256 x i8]";
        }

        throw new IllegalArgumentException();
    }

    public Pair<Pair<Type, List<String>>, Node> visitParse(@NotNull ProgrammingLanguageParser.ParseContext ctx) {
        List<String> code = new LinkedList<>();
        code.add("\n@.read_int = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1");
        code.add("@.read_str = private unnamed_addr constant [6 x i8] c\"%255s\\00\", align 1");
        code.add("@.write_int = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1");
        code.add("@.write_str = private unnamed_addr constant [3 x i8] c\"%s\\00\", align 1");
        code.add("@.false_str = private unnamed_addr constant [6 x i8] c\"false\\00\", align 1");
        code.add("@.true_str = private unnamed_addr constant [5 x i8] c\"true\\00\", align 1");
        code.add("\n");

        for (ProgrammingLanguageParser.FunctionDefinitionContext fdc : ctx.functionDefinition()) {
            String name = fdc.Id().getText();
            if (functions.containsKey(name)) {
                throw new IllegalArgumentException("Double declaration of function: " + name);
            }
            Type functionType = visitTypeSpecifier(fdc.typeSpecifier()).getKey().getKey();
            List<Type> argTypes = new LinkedList<>();
            if (fdc.idList() != null) {
                for (int i = 0; i < fdc.idList().typeSpecifier().size(); ++i) {
                    Type argType = visitTypeSpecifier(fdc.idList().typeSpecifier(i)).getKey().getKey();
                    argTypes.add(argType);
                }
            }
            functions.put(name, new Pair<>(functionType, argTypes));
        }

        for (ProgrammingLanguageParser.FunctionDefinitionContext fdc : ctx.functionDefinition()) {
            code.addAll(visitFunctionDefinition(fdc).getKey().getValue());
            code.add("\n");
        }
        code.add("declare i32 @scanf(i8*, ...) nounwind\n");
        code.add("declare i32 @printf(i8*, ...) nounwind\n");
        code.add("declare i8* @strcpy(i8*, i8*) nounwind\n");
        code.add("declare i32 @strlen(i8*) nounwind readonly\n");
        code.add("declare i8* @strcat(i8*, i8*) nounwind\n");
        code.add("declare i32 @strcmp(i8*, i8*) nounwind readonly\n");
        code.add("declare void @llvm.memcpy.p0i8.p0i8.i32(i8* nocapture, i8* nocapture, i32, i32, i1) nounwind\n");
        code.addAll(0, constants);
        return new Pair<>(new Pair<>(null, code), null);
    }

    public Pair<Pair<Type, List<String>>, Node> visitFunctionDefinition(@NotNull ProgrammingLanguageParser.FunctionDefinitionContext ctx) {
        varCounter = 0;
        helpCounter = 0;
        nameCounter.clear();
        variables.clear();
        List<String> code = new ArrayList<>();

        Type functionType = visitTypeSpecifier(ctx.typeSpecifier()).getKey().getKey();
        String functionName = ctx.Id().getText();
        if (!functionName.equals("main")) {
            functionName = "func_" + functionName;
        }
        String functionHeader = "define " + getLLVMType(functionType)
                + (functionType == Type.STRING ? "*" : "")
                + " @" + functionName + "(";

        Map<String, Type> funcArgs = new HashMap<>();
        if (ctx.idList() != null) {
            for (int i = 0; i < ctx.idList().typeSpecifier().size(); ++i) {
                Type argType = visitTypeSpecifier(ctx.idList().typeSpecifier(i)).getKey().getKey();
                String argName = ctx.idList().Id(i).getText();
                if (nameCounter.containsKey(argName)) {
                    throw new IllegalArgumentException("Argument name must be unique");
                }
                nameCounter.put(argName, 0);
                funcArgs.put(argName, argType);
                functionHeader += getLLVMType(argType) + "* %var_" + argName + nameCounter.get(argName) + (i == ctx.idList().typeSpecifier().size() - 1 ? "" : ", ");
            }
        }
        functionHeader += ") {";

        variables.putAll(funcArgs);

        Pair<Pair<Type, List<String>>, Node> body = visitFunctionBody(ctx.functionBody());
        if (functionType != body.getKey().getKey()) {
            throw new IllegalArgumentException("Function type and body type does not match");
        }

        code.add(functionHeader);
        code.addAll(body.getKey().getValue());
        code.add("}");

        return new Pair<>(new Pair<>(functionType, code), null);
    }

    public Pair<Pair<Type, List<String>>, Node> visitIdList(@NotNull ProgrammingLanguageParser.IdListContext ctx) {
        throw new UnsupportedOperationException();
    }

    public Pair<Pair<Type, List<String>>, Node> visitTypeSpecifier(@NotNull ProgrammingLanguageParser.TypeSpecifierContext ctx) {
        if (ctx.String_type() != null) {
            return new Pair<>(new Pair<>(Type.STRING, null), null);
        } else if (ctx.Bool_type() != null) {
            return new Pair<>(new Pair<>(Type.BOOLEAN, null), null);
        } else if (ctx.String_type() != null) {
            return new Pair<>(new Pair<>(Type.STRING, null), null);
        } else if (ctx.Int_type() != null) {
            return new Pair<>(new Pair<>(Type.INT, null), null);
        } else if (ctx.Void_type() != null) {
            return new Pair<>(new Pair<>(Type.VOID, null), null);
        }

        throw new UnsupportedOperationException("Unsupported type");
    }

    private List<String> extractValues(Pair<int[], Pair<Type, List<String>>> generated) {
        varCounter = generated.getKey()[0];
        helpCounter = generated.getKey()[1];
        constCounter = generated.getKey()[2];
        return generated.getValue().getValue();
    }

    public Pair<Pair<Type, List<String>>, Node> visitFunctionBody(@NotNull ProgrammingLanguageParser.FunctionBodyContext ctx) {
        List<String> code = new LinkedList<>();

        for (ProgrammingLanguageParser.StatementContext sc : ctx.statement()) {
            code.addAll(visitStatement(sc).getKey().getValue());
        }

        Type returnType = Type.VOID;

        if (ctx.expression() != null) {
            Pair<Pair<Type, List<String>>, Node> returnStatement = visitExpression(ctx.expression());
            returnType = returnStatement.getKey().getKey();
            if (returnStatement.getValue() != null) {
                code.addAll(extractValues(returnStatement.getValue().simplify().generateCode(varCounter, helpCounter, constCounter, constants)));
            } else {
                code.addAll(returnStatement.getKey().getValue());
            }
            code.add("\tret " + getLLVMType(returnType)
                    + (returnType == Type.STRING ? "*" : "")
                    + " %tmp" + (varCounter - 1));
        }

        return new Pair<>(new Pair<>(returnType, code), null);
    }

    public Pair<Pair<Type, List<String>>, Node> visitCompoundStatement(@NotNull ProgrammingLanguageParser.CompoundStatementContext ctx) {
        Map<String, Type> context = new HashMap<>();
        Map<String, List<String>> unionContext = new HashMap<>();
        context.putAll(variables);
        unionContext.putAll(unionMap);

        List<String> code = new ArrayList<>();
        for (ProgrammingLanguageParser.StatementContext statement : ctx.statement()) {
            code.addAll(visitStatement(statement).getKey().getValue());
        }

        variables = context;
        unionMap = unionContext;
        return new Pair<>(new Pair<>(Type.VOID, code), null);
    }

    public Pair<Pair<Type, List<String>>, Node> visitStatement(@NotNull ProgrammingLanguageParser.StatementContext ctx) {
        if (ctx.functionCall() != null) {
            return visitFunctionCall(ctx.functionCall());
        } else if (ctx.assignment() != null) {
            return visitAssignment(ctx.assignment());
        } else if (ctx.compoundStatement() != null) {
            return visitCompoundStatement(ctx.compoundStatement());
        } else if (ctx.selectionStatement() != null) {
            return visitSelectionStatement(ctx.selectionStatement());
        } else if (ctx.whileStatement() != null) {
            return visitWhileStatement(ctx.whileStatement());
        } else if (ctx.declarationId() != null) {
            return visitDeclarationId(ctx.declarationId());
        } else if (ctx.unionStatement() != null) {
            return visitUnionStatement(ctx.unionStatement());
        } else if (ctx.jumpStatement() != null) {
            return visitJumpStatement(ctx.jumpStatement());
        }
        throw new UnsupportedOperationException("Unexpected visit statement behavior");
    }

    public Pair<Pair<Type, List<String>>, Node> visitUnionStatement(@NotNull ProgrammingLanguageParser.UnionStatementContext ctx) {
        List<String> unionId = new ArrayList<>();
        List<String> code = new ArrayList<>();
        for (ProgrammingLanguageParser.DeclarationIdContext declarationIdContext : ctx.declarationId()) {
            Type type = visitTypeSpecifier(declarationIdContext.typeSpecifier()).getKey().getKey();
            if (type == Type.STRING) {
                throw new IllegalArgumentException("Strings are not allowed in union");
            }
            code.addAll(visitDeclarationId(declarationIdContext).getKey().getValue());
            for (TerminalNode terminalNode : declarationIdContext.Id()) {
                unionId.add(terminalNode.getText());
            }
        }
        for (ProgrammingLanguageParser.DeclarationIdContext declarationIdContext : ctx.declarationId()) {
            for (TerminalNode terminalNode : declarationIdContext.Id()) {
                unionMap.put(terminalNode.getText(), unionId);
            }
        }
        return new Pair<>(new Pair<>(Type.VOID, code), null);
    }

    public Pair<Pair<Type, List<String>>, Node> visitSelectionStatement(@NotNull ProgrammingLanguageParser.SelectionStatementContext ctx) {
        List<String> code = new ArrayList<>();
        Pair<Pair<Type, List<String>>, Node> expression = visitExpression(ctx.expression());
        if (ctx.If() != null) {
            if (expression.getKey().getKey() != Type.BOOLEAN) {
                throw new IllegalArgumentException("If condition must be boolean");
            }
            if (expression.getValue() != null) {
                Node simplified = expression.getValue().simplify();
                if (simplified instanceof BoolNode) {
                    if (((BoolNode) simplified).getValue()) {
                        return visitCompoundStatement(ctx.compoundStatement(0));
                    } else {
                        if (ctx.compoundStatement().size() == 2) {
                            return visitCompoundStatement(ctx.compoundStatement(1));
                        } else {
                            return new Pair<>(new Pair<>(Type.VOID, new ArrayList<>()), null);
                        }
                    }
                }
                code.addAll(extractValues(simplified.generateCode(varCounter, helpCounter, constCounter, constants)));
            } else {
                code.addAll(expression.getKey().getValue());
            }
            int labelTrue = labelCounter++;
            int labelFalse = labelCounter++;
            code.add("\tbr i1 %tmp" + (varCounter - 1) + ", label %Label" + labelTrue + ", label %Label" + labelFalse);
            code.add("Label" + labelTrue + ":");
            code.addAll(visitCompoundStatement(ctx.compoundStatement().get(0)).getKey().getValue());

            if (ctx.compoundStatement().size() == 2) {
                int labelEnd = labelCounter++;
                code.add("\tbr label %Label" + labelEnd);
                code.add("Label" + labelFalse + ":");
                code.addAll(visitCompoundStatement(ctx.compoundStatement().get(1)).getKey().getValue());
                code.add("\tbr label %Label" + labelEnd);
                code.add("Label" + labelEnd + ":");
            } else {
                code.add("\tbr label %Label" + labelFalse);
                code.add("Label" + labelFalse + ":");
            }
            return new Pair<>(new Pair<>(Type.VOID, code), null);
        } else if (ctx.Switch() != null) {
            if (expression.getKey().getKey() != Type.INT) {
                throw new IllegalArgumentException("Switch statement works only for integer type");
            }
            if (expression.getValue() != null) {
                code.addAll(extractValues(expression.getValue().simplify().generateCode(varCounter, helpCounter, constCounter, constants)));
            } else {
                code.addAll(expression.getKey().getValue());
            }
            int firstLabel = labelCounter;
            labelCounter += ctx.caseStatement().size();
            int labelEnd = labelCounter++;
            endLabels.push(labelEnd);
            String switchString = "\tswitch i32 %tmp" + (varCounter - 1) + ", label %Label" + labelEnd + " [";
            for (int i = 0; i < ctx.caseStatement().size(); ++i) {
                ProgrammingLanguageParser.CaseStatementContext caseStatementContext = ctx.caseStatement(i);
                Pair<Pair<Type, List<String>>, Node> caseExpression = visitExpression(caseStatementContext.expression());
                if (caseExpression.getKey().getKey() != Type.INT) {
                    throw new IllegalArgumentException("Switch statement works only for integer type");
                }
                Node exprValue = caseExpression.getValue().simplify();
                if (!(exprValue instanceof IntNode)) {
                    throw new IllegalArgumentException("Only constant values are avaliable for switch statement");
                }
                code.addAll(caseExpression.getKey().getValue());
                switchString = switchString + " i32 " + ((IntNode) exprValue).getValue() + ", label %Label" + (firstLabel + i);
            }
            switchString = switchString + "]";
            code.add(switchString);

            for (int i = 0; i < ctx.caseStatement().size(); ++i) {
                code.add("Label" + (firstLabel + i) + ":");
                code.addAll(visitCompoundStatement(ctx.caseStatement(i).compoundStatement()).getKey().getValue());
                code.add("\tbr label %Label" + (firstLabel + i + 1));
            }

            code.add("Label" + labelEnd + ":");
            endLabels.pop();
            return new Pair<>(new Pair<>(Type.VOID, code), null);
        }
        throw new UnsupportedOperationException("Unexpected selection statement behavior");
    }

    public Pair<Pair<Type, List<String>>, Node> visitCaseStatement(@NotNull ProgrammingLanguageParser.CaseStatementContext ctx) {
        throw new UnsupportedOperationException("You should not get there");
    }

    public Pair<Pair<Type, List<String>>, Node> visitJumpStatement(@NotNull ProgrammingLanguageParser.JumpStatementContext ctx) {
        List<String> code = new ArrayList<>();
        if (ctx.Break() != null) {
            if (endLabels.isEmpty()) {
                throw new IllegalArgumentException("Break should be inside while or switch");
            }
            code.add("\tbr label %Label" + endLabels.peek());
        } else if (ctx.Continue() != null) {
            if (startWhileLabels.isEmpty()) {
                throw new IllegalArgumentException("Continue should be inside while");
            }
            code.add("\tbr label %Label" + startWhileLabels.peek());
        } else {
            throw new UnsupportedOperationException("You should not get there");
        }
        return new Pair<>(new Pair<>(Type.VOID, code), null);
    }

    public Pair<Pair<Type, List<String>>, Node> visitWhileStatement(@NotNull ProgrammingLanguageParser.WhileStatementContext ctx) {
        List<String> code = new ArrayList<>();
        Pair<Pair<Type, List<String>>, Node> expression = visitExpression(ctx.expression());
        if (expression.getKey().getKey() != Type.BOOLEAN) {
            throw new IllegalArgumentException("While conditional should be boolean");
        }
        if (expression.getValue() != null) {
            Node simplify = expression.getValue().simplify();
            if (!((BoolNode) simplify).getValue()) {
                return new Pair<>(new Pair<>(Type.VOID, new ArrayList<>()), null);
            }
        }
        int startLabel = labelCounter++;
        int bodyLabel = labelCounter++;
        int endLabel = labelCounter++;
        endLabels.push(endLabel);
        startWhileLabels.push(startLabel);

        code.add("\tbr label %Label" + startLabel);
        code.add("Label" + startLabel + ":");
        if (expression.getValue() != null) {
            code.addAll(extractValues(expression.getValue().simplify().generateCode(varCounter, helpCounter, constCounter, constants)));
        } else {
            code.addAll(expression.getKey().getValue());
        }
        code.add("\tbr i1 %tmp" + (varCounter - 1) + ", label %Label" + bodyLabel + ", label %Label" + endLabel);
        code.add("Label" + bodyLabel + ":");
        code.addAll(visitCompoundStatement(ctx.compoundStatement()).getKey().getValue());
        code.add("\tbr label %Label" + startLabel);
        code.add("Label" + endLabel + ":");

        endLabels.pop();
        startWhileLabels.pop();
        return new Pair<>(new Pair<>(Type.VOID, code), null);
    }

    public Pair<Pair<Type, List<String>>, Node> visitFunctionCall(@NotNull ProgrammingLanguageParser.FunctionCallContext ctx) {
        List<String> code = new ArrayList<>();
        if (ctx.Read() != null) {
            String id = ctx.Id().getText();
            if (!variables.containsKey(id)) {
                throw new IllegalArgumentException("Undefined variable");
            }
            Type type = variables.get(id);
            id += nameCounter.get(id);
            switch (type) {
                case INT:
                    code.add("\tcall i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([3 x i8]* @.read_int, i32 0, i32 0), i32* %var_" + id + ") nounwind");
                    break;
                case STRING:
                    code.add("\t%ptr" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %var_" + id + ", i32 0, i32 0");
                    code.add("\tcall i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([6 x i8]* @.read_str, i32 0, i32 0), i8* %ptr" + (helpCounter - 1) + ") nounwind");
                    break;
                case BOOLEAN:
                    code.add("\t%bool_tmp" + varCounter++ + " = alloca i32");
                    code.add("\tcall i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([3 x i8]* @.read_int, i32 0, i32 0), i32* %bool_tmp" + (varCounter - 1) + ") nounwind");
                    code.add("\t%tmp" + varCounter++ + " = load i32* %bool_tmp" + (varCounter - 2));
                    code.add("\t%tmp" + varCounter++ + " = icmp ne i32 %tmp" + (varCounter - 2) + ", 0");
                    code.add("\tstore i1 %tmp" + (varCounter - 1) + ", i1* %var_" + id);
                    break;
                default:
                    throw new UnsupportedOperationException("You should not be there");
            }
            return new Pair<>(new Pair<>(Type.VOID, code), null);
        } else if (ctx.Write() != null) {
            Pair<Pair<Type, List<String>>, Node> expression = visitExpression(ctx.expression());
            if (expression.getValue() != null) {
                code.addAll(extractValues(expression.getValue().simplify().generateCode(varCounter, helpCounter, constCounter, constants)));
            } else {
                code.addAll(expression.getKey().getValue());
            }
            Type type = expression.getKey().getKey();
            switch (type) {
                case INT:
                    code.add("\tcall i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([3 x i8]* @.write_int, i32 0, i32 0), i32 %tmp" + (varCounter - 1) + ") nounwind");
                    break;
                case STRING:
                    code.add("\t%str_ptr" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 0");
                    code.add("\tcall i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([3 x i8]* @.write_str, i32 0, i32 0), i8* %str_ptr" + (helpCounter - 1) + ") nounwind");
                    break;
                case BOOLEAN:
                    int labelTrue = labelCounter++;
                    int labelFalse = labelCounter++;
                    int labelEnd = labelCounter++;
                    code.add("\t%bool_tmp" + varCounter++ + " = icmp eq i1 %tmp" + (varCounter - 2) + ", 0");
                    code.add("\tbr i1 %bool_tmp" + (varCounter - 1) + ", label %Label" + labelTrue + ", label %Label" + labelFalse);
                    code.add("Label" + labelTrue + ":");
                    code.add("\tcall i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([6 x i8]* @.false_str, i32 0, i32 0)) nounwind");
                    code.add("\tbr label %Label" + labelEnd);
                    code.add("Label" + labelFalse + ":");
                    code.add("\tcall i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.true_str, i32 0, i32 0)) nounwind");
                    code.add("\tbr label %Label" + labelEnd);
                    code.add("Label" + labelEnd + ":");
                    break;
                default:
                    throw new UnsupportedOperationException("Can not write void type");
            }
            return new Pair<>(new Pair<>(Type.VOID, code), null);
        } else if (ctx.Length() != null) {
            Pair<Pair<Type, List<String>>, Node> expression = visitExpression(ctx.expression());
            if (expression.getKey().getKey() != Type.STRING) {
                throw new IllegalArgumentException("Length function works with strings only");
            }
            if (expression.getValue() != null) {
                Node node = expression.getValue().simplify();
                if (node instanceof StringNode) {
                    int len = ((StringNode) node).getValue().length();
                    List<String> newCode = new ArrayList<>();
                    newCode.add("\t%tmp" + varCounter++ + " = alloca i32");
                    newCode.add("\tstore i32 " + len + ", i32* %tmp" + (varCounter - 1));
                    newCode.add("\t%tmp" + varCounter++ + " = load i32* %tmp" + (varCounter - 2));
                    return new Pair<>(new Pair<>(Type.INT, newCode), new IntNode(len));
                }
                code.addAll(extractValues(expression.getValue().simplify().generateCode(varCounter, helpCounter, constCounter, constants)));
            } else {
                code.addAll(expression.getKey().getValue());
            }
            code.add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 0");
            code.add("\t%tmp" + varCounter++ + " = call i32 @strlen(i8* %help_tmp" + (helpCounter - 1) + ") nounwind readonly");
            return new Pair<>(new Pair<>(Type.INT, code), null);
        } else if (ctx.Id() != null) {
            String id = ctx.Id().getText();
            if (!functions.containsKey(id)) {
                throw new IllegalArgumentException("Call to undeclared function");
            }
            Pair<Type, List<Type>> functSignature = functions.get(id);
            if (ctx.expressionList().expression().size() != functSignature.getValue().size()) {
                throw new IllegalArgumentException("Number of arguments does not match with function declaration");
            }
            List<Integer> argsNum = new ArrayList<>();
            Iterator<Type> typeIterator = functions.get(id).getValue().iterator();
            for (ProgrammingLanguageParser.ExpressionContext expressionContext : ctx.expressionList().expression()) {
                Type argType = typeIterator.next();
                Pair<Pair<Type, List<String>>, Node> expr = visitExpression(expressionContext);
                if (expr.getKey().getKey() != argType) {
                    throw new IllegalArgumentException("Expected type " + argType.toString() + ", found " + expr.getKey() + " for " + argsNum.size() + " argument of call to " + id + " function");
                }
                if (expr.getValue() != null) {
                    code.addAll(extractValues(expr.getValue().simplify().generateCode(varCounter, helpCounter, constCounter, constants)));
                } else {
                    code.addAll(expr.getKey().getValue());
                }
                if (argType != Type.STRING) {
                    code.add("\t%tmp" + varCounter++ + " = alloca " + getLLVMType(expr.getKey().getKey()));
                    code.add("\tstore " + getLLVMType(expr.getKey().getKey()) + " %tmp" + (varCounter - 2) + ", " + getLLVMType(expr.getKey().getKey()) + "* %tmp" + (varCounter - 1));
                }
                argsNum.add(varCounter - 1);
            }
            String call = "call " + getLLVMType(functSignature.getKey())
                    + (functSignature.getKey() == Type.STRING ? "*" : "")
                    + " @func_" + id + "(";
            typeIterator = functions.get(id).getValue().iterator();
            for (Integer i : argsNum) {
                call += getLLVMType(typeIterator.next()) + "* %tmp" + i + (typeIterator.hasNext() ? ", " : "");
            }
            call += ")";
            if (functSignature.getKey() != Type.VOID) {
                call = "%tmp" + varCounter++ + " = " + call;
            }
            code.add("\t" + call);
            return new Pair<>(new Pair<>(functSignature.getKey(), code), null);
        } else {
            throw new UnsupportedOperationException("You should not get there");
        }
    }

    public Pair<Pair<Type, List<String>>, Node> visitExpressionList(@NotNull ProgrammingLanguageParser.ExpressionListContext ctx) {
        throw new UnsupportedOperationException("You should not be there");
    }

    public Pair<Pair<Type, List<String>>, Node> visitExpression(@NotNull ProgrammingLanguageParser.ExpressionContext ctx) {
        Pair<Pair<Type, List<String>>, Node> andExpr = visitAndExpr(ctx.andExpr(0));
        Node node = andExpr.getValue();
        int andExprNumber = varCounter - 1;
        for (int i = 1; i < ctx.andExpr().size(); ++i) {
            Pair<Pair<Type, List<String>>, Node> nextAndExpr = visitAndExpr(ctx.andExpr(i));
            int nextAndExprNumber = varCounter - 1;
            if (andExpr.getKey().getKey() == Type.BOOLEAN && nextAndExpr.getKey().getKey() == Type.BOOLEAN) {
                andExpr.getKey().getValue().addAll(nextAndExpr.getKey().getValue());
                andExpr.getKey().getValue().add("\t%tmp" + varCounter + " = or i1 %tmp" + andExprNumber + ", %tmp" + nextAndExprNumber);
                andExprNumber = varCounter++;
                if (node != null && nextAndExpr.getValue() != null) {
                    node = new OrNode(node, nextAndExpr.getValue());
                }
            } else {
                throw new IllegalArgumentException("Both values must be boolean");
            }
        }

        return new Pair<>(andExpr.getKey(), node);
    }

    public Pair<Pair<Type, List<String>>, Node> visitAndExpr(@NotNull ProgrammingLanguageParser.AndExprContext ctx) {
        Pair<Pair<Type, List<String>>, Node> eqExpr = visitEqExpr(ctx.eqExpr(0));
        Node node = eqExpr.getValue();
        int eqExprNumber = varCounter - 1;
        for (int i = 1; i < ctx.eqExpr().size(); ++i) {
            Pair<Pair<Type, List<String>>, Node> nextEqExpr = visitEqExpr(ctx.eqExpr(i));
            int nextEqExprNumber = varCounter - 1;
            if (eqExpr.getKey().getKey() == Type.BOOLEAN && nextEqExpr.getKey().getKey() == Type.BOOLEAN) {
                eqExpr.getKey().getValue().addAll(nextEqExpr.getKey().getValue());
                eqExpr.getKey().getValue().add("\t%tmp" + varCounter + " = and i1 %tmp" + eqExprNumber + ", %tmp" + nextEqExprNumber);
                eqExprNumber = varCounter++;
                if (node != null && nextEqExpr.getValue() != null) {
                    node = new AndNode(node, nextEqExpr.getValue());
                }
            } else {
                throw new IllegalArgumentException("Both values must be boolean");
            }
        }

        return new Pair<>(eqExpr.getKey(), node);
    }

    private String getEqOperationType(String operation) {
        switch (operation) {
            case "==":
                return "eq";
            case "!=":
                return "ne";
            default:
                throw new IllegalArgumentException();
        }
    }

    private Node getEqOperationNode(String operation, Node left, Node right) {
        switch (operation) {
            case "==":
                return new EqNode(left, right);
            case "!=":
                return new NeNode(left, right);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Pair<Pair<Type, List<String>>, Node> visitEqExpr(@NotNull ProgrammingLanguageParser.EqExprContext ctx) {
        Pair<Pair<Type, List<String>>, Node> relExpr = visitRelExpr(ctx.relExpr(0));
        Node node = relExpr.getValue();
        int relExprNumber = varCounter - 1;
        for (int i = 1; i < ctx.relExpr().size(); ++i) {
            Pair<Pair<Type, List<String>>, Node> nextRelExpr = visitRelExpr(ctx.relExpr(i));
            int nextRelExprNumber = varCounter - 1;
            String operationType = getEqOperationType(ctx.getChild(i * 2 - 1).getText());
            if (relExpr.getKey().getKey() == Type.BOOLEAN && nextRelExpr.getKey().getKey() == Type.BOOLEAN) {
                relExpr.getKey().getValue().addAll(nextRelExpr.getKey().getValue());
                relExpr.getKey().getValue().add("\t%tmp" + varCounter + " = icmp " + operationType + " i1 %tmp" + relExprNumber + ", %tmp" + nextRelExprNumber);
                relExprNumber = varCounter++;
                if (node != null && nextRelExpr.getValue() != null) {
                    node = getEqOperationNode(ctx.getChild(i * 2 - 1).getText(), node, nextRelExpr.getValue());
                }
            } else if (relExpr.getKey().getKey() == Type.INT && nextRelExpr.getKey().getKey() == Type.INT) {
                relExpr.getKey().getValue().addAll(nextRelExpr.getKey().getValue());
                relExpr.getKey().getValue().add("\t%tmp" + varCounter + " = icmp " + operationType + " i32 %tmp" + relExprNumber + ", %tmp" + nextRelExprNumber);
                relExprNumber = varCounter++;
                if (node != null && nextRelExpr.getValue() != null) {
                    node = getEqOperationNode(ctx.getChild(i * 2 - 1).getText(), node, nextRelExpr.getValue());
                }
                relExpr = new Pair<>(new Pair<>(Type.BOOLEAN, relExpr.getKey().getValue()), node);
            } else if (relExpr.getKey().getKey() == Type.STRING && nextRelExpr.getKey().getKey() == Type.STRING) {
                relExpr.getKey().getValue().addAll(nextRelExpr.getKey().getValue());
                relExpr.getKey().getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 0");
                relExpr.getKey().getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + relExprNumber + ", i32 0, i32 0");
                relExpr.getKey().getValue().add("\t%tmp" + varCounter++ + " = call i32 @strcmp(i8* %help_tmp" + (helpCounter - 2) + ", i8* %help_tmp" + (helpCounter - 1) + ") nounwind readonly");
                relExpr.getKey().getValue().add("\t%tmp" + varCounter + " = icmp " + operationType + " i32 0, %tmp" + (varCounter - 1));
                relExprNumber = varCounter++;
                if (node != null && nextRelExpr.getValue() != null) {
                    node = getEqOperationNode(ctx.getChild(i * 2 - 1).getText(), node, nextRelExpr.getValue());
                }
                relExpr = new Pair<>(new Pair<>(Type.BOOLEAN, relExpr.getKey().getValue()), node);
            } else {
                throw new IllegalArgumentException("Both values must be boolean");
            }
        }

        return new Pair<>(relExpr.getKey(), node);
    }

    private String getRelOperationType(String operation) {
        switch (operation) {
            case ">=":
                return "sge";
            case "<=":
                return "sle";
            case ">":
                return "sgt";
            case "<":
                return "slt";
            default:
                throw new IllegalArgumentException();
        }
    }

    private Node getRelOperationNode(String operation, Node left, Node right) {
        switch (operation) {
            case ">=":
                return new GENode(left, right);
            case "<=":
                return new LENode(left, right);
            case ">":
                return new GNode(left, right);
            case "<":
                return new LNode(left, right);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Pair<Pair<Type, List<String>>, Node> visitRelExpr(@NotNull ProgrammingLanguageParser.RelExprContext ctx) {
        Pair<Pair<Type, List<String>>, Node> addExpr = visitAddExpr(ctx.addExpr(0));
        Node node = addExpr.getValue();
        int addExprNumber = varCounter - 1;
        for (int i = 1; i < ctx.addExpr().size(); ++i) {
            Pair<Pair<Type, List<String>>, Node> nextAddExpr = visitAddExpr(ctx.addExpr(i));
            int nextAddExprNumber = varCounter - 1;
            String operationType = getRelOperationType(ctx.getChild(i * 2 - 1).getText());
            if (addExpr.getKey().getKey() == Type.INT && nextAddExpr.getKey().getKey() == Type.INT) {
                addExpr.getKey().getValue().addAll(nextAddExpr.getKey().getValue());
                addExpr.getKey().getValue().add("\t%tmp" + varCounter + " = icmp " + operationType + " i32 %tmp" + addExprNumber + ", %tmp" + nextAddExprNumber);
                addExprNumber = varCounter++;
                if (node != null && nextAddExpr.getValue() != null) {
                    node = getRelOperationNode(ctx.getChild(i * 2 - 1).getText(), node, nextAddExpr.getValue());
                }
                addExpr = new Pair<>(new Pair<>(Type.BOOLEAN, addExpr.getKey().getValue()), node);
            } else if (addExpr.getKey().getKey() == Type.STRING && nextAddExpr.getKey().getKey() == Type.STRING) {
                addExpr.getKey().getValue().addAll(nextAddExpr.getKey().getValue());
                addExpr.getKey().getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 0");
                addExpr.getKey().getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + addExprNumber + ", i32 0, i32 0");
                addExpr.getKey().getValue().add("\t%tmp" + varCounter++ + " = call i32 @strcmp(i8* %help_tmp" + (helpCounter - 2) + ", i8* %help_tmp" + (helpCounter - 1) + ") nounwind readonly");
                addExpr.getKey().getValue().add("\t%tmp" + varCounter + " = icmp " + operationType + " i32 0, %tmp" + (varCounter - 1));
                addExprNumber = varCounter++;
                if (node != null && nextAddExpr.getValue() != null) {
                    node = getRelOperationNode(ctx.getChild(i * 2 - 1).getText(), node, nextAddExpr.getValue());
                }
                addExpr = new Pair<>(new Pair<>(Type.BOOLEAN, addExpr.getKey().getValue()), node);
            } else {
                throw new IllegalArgumentException("Both values must have same type");
            }
        }

        return new Pair<>(addExpr.getKey(), node);
    }

    private String getAddOperationType(String operation) {
        switch (operation) {
            case "+":
                return "add";
            case "-":
                return "sub";
            default:
                throw new IllegalArgumentException();
        }
    }

    private Node getAddOperationNode(String operation, Node left, Node right) {
        switch (operation) {
            case "+":
                return new AddNode(left, right);
            case "-":
                return new SubNode(left, right);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Pair<Pair<Type, List<String>>, Node> visitAddExpr(@NotNull ProgrammingLanguageParser.AddExprContext ctx) {
        Pair<Pair<Type, List<String>>, Node> mulExpr = visitMulExpr(ctx.mulExpr(0));
        Node node = mulExpr.getValue();
        int mulExprNumber = varCounter - 1;
        for (int i = 1; i < ctx.mulExpr().size(); ++i) {
            Pair<Pair<Type, List<String>>, Node> nextMulExpr = visitMulExpr(ctx.mulExpr(i));
            int nextMulExprNumber = varCounter - 1;
            String operationType = getAddOperationType(ctx.getChild(i * 2 - 1).getText());
            if (mulExpr.getKey().getKey() == Type.INT && nextMulExpr.getKey().getKey() == Type.INT) {
                mulExpr.getKey().getValue().addAll(nextMulExpr.getKey().getValue());
                mulExpr.getKey().getValue().add("\t%tmp" + varCounter + " = " + operationType + " i32 %tmp" + mulExprNumber + ", %tmp" + nextMulExprNumber);
                mulExprNumber = varCounter++;
                if (node != null && nextMulExpr.getValue() != null) {
                    node = getAddOperationNode(ctx.getChild(i * 2 - 1).getText(), node, nextMulExpr.getValue());
                }
            } else if (mulExpr.getKey().getKey() == Type.STRING && nextMulExpr.getKey().getKey() == Type.STRING) {
                mulExpr.getKey().getValue().addAll(nextMulExpr.getKey().getValue());
                mulExpr.getKey().getValue().add("\t%tmp" + varCounter++ + " = alloca [256 x i8]");
                mulExpr.getKey().getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 0");
                mulExpr.getKey().getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + mulExprNumber + ", i32 0, i32 0");
                mulExpr.getKey().getValue().add("\tcall i8* @strcpy(i8* %help_tmp" + (helpCounter - 2) + ", i8* %help_tmp" + (helpCounter - 1) + ") nounwind");
                mulExpr.getKey().getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + nextMulExprNumber + ", i32 0, i32 0");
                mulExpr.getKey().getValue().add("\tcall i8* @strcat(i8* %help_tmp" + (helpCounter - 3) + ", i8* %help_tmp" + (helpCounter - 1) + ") nounwind");
                mulExprNumber = varCounter - 1;
                if (node != null && nextMulExpr.getValue() != null) {
                    node = getAddOperationNode(ctx.getChild(i * 2 - 1).getText(), node, nextMulExpr.getValue());
                }
            } else {
                throw new IllegalArgumentException("Both values must have int or string type");
            }
        }

        return new Pair<>(mulExpr.getKey(), node);
    }

    private String getMulOperationType(String operation) {
        switch (operation) {
            case "*":
                return "mul";
            case "/":
                return "sdiv";
            case "%":
                return "srem";
            default:
                throw new IllegalArgumentException();
        }
    }

    private Node getMulOperationNode(String operation, Node left, Node right) {
        switch (operation) {
            case "*":
                return new MultNode(left, right);
            case "/":
                return new DivNode(left, right);
            case "%":
                return new RemNode(left, right);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Pair<Pair<Type, List<String>>, Node> visitMulExpr(@NotNull ProgrammingLanguageParser.MulExprContext ctx) {
        Pair<Pair<Type, List<String>>, Node> atom = visitAtom(ctx.atom(0));
        Node node = atom.getValue();
        int atomNumber = varCounter - 1;
        for (int i = 1; i < ctx.atom().size(); ++i) {
            Pair<Pair<Type, List<String>>, Node> nextAtom = visitAtom(ctx.atom(i));
            int nextAtomNumber = varCounter - 1;
            String operationType = getMulOperationType(ctx.getChild(i * 2 - 1).getText());
            if (atom.getKey().getKey() == Type.INT && nextAtom.getKey().getKey() == Type.INT) {
                atom.getKey().getValue().addAll(nextAtom.getKey().getValue());
                atom.getKey().getValue().add("\t%tmp" + varCounter + " = " + operationType + " i32 %tmp" + atomNumber + ", %tmp" + nextAtomNumber);
                atomNumber = varCounter++;
                if (node != null && nextAtom.getValue() != null) {
                    node = getMulOperationNode(ctx.getChild(i * 2 - 1).getText(), node, nextAtom.getValue());
                }
            } else {
                throw new IllegalArgumentException("Both values must have int type");
            }
        }

        return new Pair<>(atom.getKey(), node);
    }

    public Pair<Pair<Type, List<String>>, Node> visitAtom(@NotNull ProgrammingLanguageParser.AtomContext ctx) {
        List<String> code = new LinkedList<>();
        if (ctx.Bool() != null) {
            code.add("\t%tmp" + varCounter++ + " = alloca i1");
            code.add("\tstore i1 " + (ctx.Bool().getText().equals("true") ? 1 : 0) + ", i1* %tmp" + (varCounter - 1));
            code.add("\t%tmp" + varCounter++ + " = load i1* %tmp" + (varCounter - 2));
            return new Pair<>(new Pair<>(Type.BOOLEAN, code), new BoolNode(ctx.Bool().getText().equals("true")));
        } else if (ctx.Int() != null) {
            code.add("\t%tmp" + varCounter++ + " = alloca i32");
            code.add("\tstore i32 " + ctx.Int().getText() + ", i32* %tmp" + (varCounter - 1));
            code.add("\t%tmp" + varCounter++ + " = load i32* %tmp" + (varCounter - 2));
            return new Pair<>(new Pair<>(Type.INT, code), new IntNode(Integer.valueOf(ctx.Int().getText())));
        } else if (ctx.lookup() != null) {
            return visitLookup(ctx.lookup());
        } else {
            throw new UnsupportedOperationException();
        }
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

    public Pair<Pair<Type, List<String>>, Node> visitLookup(@NotNull ProgrammingLanguageParser.LookupContext ctx) {
        List<String> code = new LinkedList<>();
        Pair<Pair<Type, List<String>>, Node> result;
        if (ctx.Id() != null) {
            String id = ctx.Id().getText();
            if (!variables.containsKey(id)) {
                throw new IllegalArgumentException("Undeclared variable: " + id);
            }
            code.add("\t%tmp" + varCounter++ + " = load " + getLLVMType(variables.get(id)) + "* %var_" + id + nameCounter.get(id));
            if (variables.get(id) == Type.STRING) {
                code.add("\t%tmp" + varCounter++ + " = alloca [256 x i8]");
                code.add("\tstore [256 x i8] %tmp" + (varCounter - 2) + ", [256 x i8]* %tmp" + (varCounter - 1));
            }
            result = new Pair<>(new Pair<>(variables.get(id), code), new IdNode(id, variables.get(id), nameCounter.get(id)));
        } else if (ctx.String() != null) {
            String str = ctx.String().getText();
            str = str.substring(1, str.length() - 1);
            if (str.length() > 255) {
                str = str.substring(0, 255);
            }

            String constant = "@.str" + constCounter++ + " = private unnamed_addr constant [256 x i8] c\"" + str;
            int newStr = countNewStrings(str);
            for (int i = 0; i < 256 - str.length() + newStr * 2; ++i) {
                constant += "\\00";
            }
            constant += "\", align 1";
            constants.add(constant);
            code.add("\t%tmp" + varCounter++ + " = alloca [256 x i8]");
            code.add("\t%help_tmp" + helpCounter++ + " = bitcast [256 x i8]* %tmp" + (varCounter - 1) + " to i8*");
            code.add("\tcall void @llvm.memcpy.p0i8.p0i8.i32(i8* %help_tmp" + (helpCounter - 1) + ", i8* getelementptr inbounds ([256 x i8]* @.str" + (constCounter - 1) + ", i32 0, i32 0), i32 256, i32 1, i1 false)");
            result = new Pair<>(new Pair<>(Type.STRING, code), new StringNode(str));
        } else if (ctx.expression() != null) {
            result = visitExpression(ctx.expression());
        } else if (ctx.functionCall() != null) {
            result = visitFunctionCall(ctx.functionCall());
        } else {
            throw new UnsupportedOperationException();
        }
        if (ctx.index() != null) {
            if (result.getKey().getKey() != Type.STRING) {
                throw new IllegalArgumentException("Can not use index with " + result.getKey().toString() + ", only suitable for string type");
            }
            int stringNumber = varCounter - 1;
            Pair<Pair<Type, List<String>>, Node> expr = visitExpression(ctx.index().expression());
            if (expr.getKey().getKey() != Type.INT) {
                throw new IllegalArgumentException("Index must be integer");
            }
            result.getKey().getValue().addAll(expr.getKey().getValue());
            result.getKey().getValue().add("\t%tmp" + varCounter++ + " = alloca [256 x i8]");
            result.getKey().getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + stringNumber + ", i32 0, i32 %tmp" + (varCounter - 2));
            result.getKey().getValue().add("\t%help_tmp" + helpCounter++ + " = load i8* %help_tmp" + (helpCounter - 2));
            result.getKey().getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 0");
            result.getKey().getValue().add("\tstore i8 %help_tmp" + (helpCounter - 2) + ", i8* %help_tmp" + (helpCounter - 1) + ", align 1");
            result.getKey().getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 1");
            result.getKey().getValue().add("\tstore i8 0, i8* %help_tmp" + (helpCounter - 1) + ", align 1");

            Node simplifiedString = result.getValue().simplify();
            Node simplifiedIndex = expr.getValue().simplify();
            if (simplifiedString instanceof StringNode && simplifiedIndex instanceof IntNode) {
                String str = ((StringNode) simplifiedString).getValue();
                Integer index = ((IntNode) simplifiedIndex).getValue();
                result = new Pair<>(result.getKey(), new StringNode(str.substring(index, index + 1)));
            }
        }
        return result;
    }

    public Pair<Pair<Type, List<String>>, Node> visitIndex(@NotNull ProgrammingLanguageParser.IndexContext ctx) {
        Pair<Pair<Type, List<String>>, Node> expr = visitExpression(ctx.expression());
        if (expr.getKey().getKey() != Type.INT) {
            throw new IllegalArgumentException("Index must integer value");
        }
        return expr;
    }

    public Pair<Pair<Type, List<String>>, Node> visitUnionAssignment(@NotNull ProgrammingLanguageParser.AssignmentContext ctx) {
        List<String> ids = unionMap.get(ctx.Id().getText());
        Pair<Pair<Type, List<String>>, Node> expr = visitExpression(ctx.expression());
        List<String> code = new ArrayList<>();
        if (expr.getValue() != null) {
            code.addAll(extractValues(expr.getValue().simplify().generateCode(varCounter, helpCounter, constCounter, constants)));
        } else {
            code.addAll(expr.getKey().getValue());
        }
        int exprNum = varCounter - 1;
        for (String id : ids) {
            switch (expr.getKey().getKey()) {
                case INT:
                    if (variables.get(id) == Type.BOOLEAN) {
                        code.add("\t%tmp" + varCounter++ + " = trunc i32 %tmp" + exprNum + " to i1");
                        code.add("\tstore i1 %tmp" + (varCounter - 1) + ", i1* %var_" + id + nameCounter.get(id) + ", align 4");
                    } else {
                        code.add("\tstore i32 %tmp" + exprNum + ", i32* %var_" + id + nameCounter.get(id) + ", align 4");
                    }
                    break;
                case BOOLEAN:
                    if (variables.get(id) == Type.INT) {
                        code.add("\t%tmp" + varCounter++ + " = zext i1 %tmp" + exprNum + " to i32");
                        code.add("\tstore i32 %tmp" + (varCounter - 1) + ", i32* %var_" + id + nameCounter.get(id) + ", align 4");
                    } else {
                        code.add("\tstore i1 %tmp" + exprNum + ", i1* %var_" + id + nameCounter.get(id) + ", align 4");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Expressions of type void or string can't be assigned to variable in union");
            }
        }
        return new Pair<>(new Pair<>(Type.VOID, code), null);
    }

    public Pair<Pair<Type, List<String>>, Node> visitAssignment(@NotNull ProgrammingLanguageParser.AssignmentContext ctx) {
        String id = ctx.Id().getText();
        if (unionMap.containsKey(id)) {
            return visitUnionAssignment(ctx);
        }
        Pair<Pair<Type, List<String>>, Node> expr = visitExpression(ctx.expression());
        List<String> code;
        if (expr.getValue() != null) {
            code = extractValues(expr.getValue().simplify().generateCode(varCounter, helpCounter, constCounter, constants));
        } else {
            code = expr.getKey().getValue();
        }
        switch (expr.getKey().getKey()) {
            case INT:
                if (variables.get(id) != Type.INT) {
                    throw new IllegalArgumentException("Can't assign integer to variable of type " + variables.get(id).toString());
                }
                code.add("\tstore i32 %tmp" + (varCounter - 1) + ", i32* %var_" + id + nameCounter.get(id) + ", align 4");
                break;
            case BOOLEAN:
                if (variables.get(id) != Type.BOOLEAN) {
                    throw new IllegalArgumentException("Can't assign boolean to variable of type " + variables.get(id).toString());
                }
                code.add("\tstore i1 %tmp" + (varCounter - 1) + ", i1* %var_" + id + nameCounter.get(id) + ", align 4");
                break;
            case STRING:
                if (variables.get(id) != Type.STRING) {
                    throw new IllegalArgumentException("Can't assign string to variable of type " + variables.get(id).toString());
                }
                code.add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %var_" + id + nameCounter.get(id) + ", i32 0, i32 0");
                code.add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 0");
                code.add("\tcall i8* @strcpy(i8* %help_tmp" + (helpCounter - 2) + ", i8* %help_tmp" + (helpCounter - 1) + ") nounwind");
                break;
            default:
                throw new IllegalArgumentException("Expression of type void can't be assigned to anything");
        }
        return new Pair<>(new Pair<>(Type.VOID, code), null);
    }

    public Pair<Pair<Type, List<String>>, Node> visitDeclarationId(@NotNull ProgrammingLanguageParser.DeclarationIdContext ctx) {
        Type varType = visitTypeSpecifier(ctx.typeSpecifier()).getKey().getKey();
        if (varType == Type.VOID) {
            throw new IllegalArgumentException("Variables of type void can't be created");
        }
        List<String> code = new ArrayList<>();
        for (TerminalNode terminalNode : ctx.Id()) {
            if (variables.containsKey(terminalNode.getText())) {
                throw new IllegalArgumentException("Double declaration of variable " + terminalNode.getText());
            }
            variables.put(terminalNode.getText(), varType);
            if (!nameCounter.containsKey(terminalNode.getText())) {
                nameCounter.put(terminalNode.getText(), 0);
            } else {
                nameCounter.put(terminalNode.getText(), nameCounter.get(terminalNode.getText()) + 1);
            }
            code.add("\t%var_" + terminalNode.getText() + nameCounter.get(terminalNode.getText()) + " = alloca " + getLLVMType(varType));
        }
        return new Pair<>(new Pair<>(Type.VOID, code), null);
    }

    public Pair<Pair<Type, List<String>>, Node> visitChildren(RuleNode ruleNode) {
        return null;
    }

    public Pair<Pair<Type, List<String>>, Node> visitTerminal(TerminalNode terminalNode) {
        return null;
    }

    public Pair<Pair<Type, List<String>>, Node> visitErrorNode(ErrorNode errorNode) {
        return null;
    }
}
