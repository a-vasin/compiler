import antlr.ProgrammingLanguageBaseVisitor;
import antlr.ProgrammingLanguageParser;
import javafx.util.Pair;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

 /**
  * @author Rabo
  */
public class Vizitor extends ProgrammingLanguageBaseVisitor<Pair<Type, List<String>>> {

    private int varCounter = 0;
    private int helpCounter = 0;
    private int labelCounter = 0;
    private int constCounter = 0;
    private Map<String, Type> variables = new HashMap<>();
    private Map<String, Pair<Type, List<Type>>> functions = new HashMap<>();
    private Stack<Integer> endLabels = new Stack<>();
    private Stack<Integer> endWhileLabels = new Stack<>();
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

    public Pair<Type, List<String>> visitParse(@NotNull ProgrammingLanguageParser.ParseContext ctx) {
        List<String> code = new LinkedList<>();
        code.add("\n@.read_int = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1");
        code.add("@.read_str = private unnamed_addr constant [6 x i8] c\"%255s\\00\", align 1");
        code.add("@.write_int = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1");
        code.add("@.write_str = private unnamed_addr constant [3 x i8] c\"%s\\00\", align 1");
        code.add("@.false_str = private unnamed_addr constant [6 x i8] c\"false\\00\", align 1");
        code.add("@.true_str = private unnamed_addr constant [5 x i8] c\"true\\00\", align 1");
        code.add("\n");
        for (ProgrammingLanguageParser.FunctionDefinitionContext fdc : ctx.functionDefinition()) {
            code.addAll(visitFunctionDefinition(fdc).getValue());
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
        return new Pair<>(null, code);
    }

    public Pair<Type, List<String>> visitFunctionDefinition(@NotNull ProgrammingLanguageParser.FunctionDefinitionContext ctx) {
        varCounter = 0;
        List<String> code = new ArrayList<>();

        Type functionType = visitTypeSpecifier(ctx.typeSpecifier()).getKey();
        String functionName = ctx.Id().getText();
        if (!functionName.equals("main")) {
            functionName = "var_" + functionName;
        }
        String functionHeader = "define " + getLLVMType(functionType) + " @" + functionName + "(";

        Map<String, Type> funcArgs = new HashMap<>();
        List<Type> argTypes = new LinkedList<>();
        if (ctx.idList() != null) {
            for (int i = 0; i < ctx.idList().typeSpecifier().size(); ++i) {
                Type argType = visitTypeSpecifier(ctx.idList().typeSpecifier(i)).getKey();
                String argName = ctx.idList().Id(i).getText();
                funcArgs.put(argName, argType);
                argTypes.add(argType);
                functionHeader += getLLVMType(argType) + " %var_" + argName + (i == ctx.idList().typeSpecifier().size() - 1 ? "" : ", ");
            }
        }
        functionHeader += ") {";

        functions.put(functionName, new Pair<>(functionType, argTypes));
        variables.putAll(funcArgs);

        Pair<Type, List<String>> body = visitFunctionBody(ctx.functionBody());
        if (functionType != body.getKey()) {
            throw new IllegalArgumentException("Function type and body type does not match");
        }

        code.add(functionHeader);
        code.addAll(body.getValue());
        code.add("}");

        return new Pair<>(functionType, code);
    }

    public Pair<Type, List<String>> visitIdList(@NotNull ProgrammingLanguageParser.IdListContext ctx) {
        throw new UnsupportedOperationException();
    }

    public Pair<Type, List<String>> visitTypeSpecifier(@NotNull ProgrammingLanguageParser.TypeSpecifierContext ctx) {
        if (ctx.String_type() != null) {
            return new Pair<>(Type.STRING, null);
        } else if (ctx.Bool_type() != null) {
            return new Pair<>(Type.BOOLEAN, null);
        } else if (ctx.String_type() != null) {
            return new Pair<>(Type.STRING, null);
        } else if (ctx.Int_type() != null) {
            return new Pair<>(Type.INT, null);
        } else if (ctx.Void_type() != null) {
            return new Pair<>(Type.VOID, null);
        }

        throw new UnsupportedOperationException("Unsupported type");
    }

    public Pair<Type, List<String>> visitFunctionBody(@NotNull ProgrammingLanguageParser.FunctionBodyContext ctx) {
        List<String> code = new LinkedList<>();

        for (ProgrammingLanguageParser.StatementContext sc : ctx.statement()) {
            code.addAll(visitStatement(sc).getValue());
        }

        Type returnType = Type.VOID;

        if(ctx.expression() != null) {
            Pair<Type, List<String>> returnStatement = visitExpression(ctx.expression());
            returnType = returnStatement.getKey();
            code.addAll(returnStatement.getValue());
            code.add("\tret " + getLLVMType(returnType) + " %tmp" + (varCounter - 1));
        }

        return new Pair<>(returnType, code);
    }

    public Pair<Type, List<String>> visitCompoundStatement(@NotNull ProgrammingLanguageParser.CompoundStatementContext ctx) {
        Map<String, Type> context = new HashMap<>();
        context.putAll(variables);
        List<String> code = new ArrayList<>();
        for (ProgrammingLanguageParser.StatementContext statement : ctx.statement()) {
            code.addAll(visitStatement(statement).getValue());
        }
        variables = context;
        return new Pair<>(Type.VOID, code);
    }

    public Pair<Type, List<String>> visitStatement(@NotNull ProgrammingLanguageParser.StatementContext ctx) {
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

    public Pair<Type, List<String>> visitUnionStatement(@NotNull ProgrammingLanguageParser.UnionStatementContext ctx) {
        return null;
    }

    public Pair<Type, List<String>> visitSelectionStatement(@NotNull ProgrammingLanguageParser.SelectionStatementContext ctx) {
        List<String> code = new ArrayList<>();
        Pair<Type, List<String>> expression = visitExpression(ctx.expression());
        code.addAll(expression.getValue());
        if (ctx.If() != null) {
            if (expression.getKey() != Type.BOOLEAN) {
                throw new IllegalArgumentException("If condition must be boolean");
            }
            int labelTrue = labelCounter++;
            int labelFalse = labelCounter++;
            code.add("\tbr i1 %tmp" + (varCounter - 1) + ", label %Label" + labelTrue + ", label %Label" + labelFalse);
            code.add("Label" + labelTrue + ":");
            code.addAll(visitCompoundStatement(ctx.compoundStatement().get(0)).getValue());

            if (ctx.compoundStatement().size() == 2) {
                int labelEnd = labelCounter++;
                code.add("\tbr label %Label" + labelEnd);
                code.add("Label" + labelFalse + ":");
                code.addAll(visitCompoundStatement(ctx.compoundStatement().get(1)).getValue());
                code.add("\tbr label %Label" + labelEnd);
                code.add("Label" + labelEnd + ":");
            } else {
                code.add("\tbr label %Label" + labelFalse);
                code.add("Label" + labelFalse + ":");
            }
            return new Pair<>(Type.VOID, code);
        } else if (ctx.Switch() != null) {
            if (expression.getKey() != Type.INT) {
                throw new IllegalArgumentException("Switch statement works only for integer type");
            }
            int labelEnd = labelCounter++;
            endLabels.push(labelEnd);
            int firstLabel = labelCounter;
            labelCounter += ctx.caseStatement().size();
            String switchString = "\tswitch i32 %tmp" + (varCounter - 1) + ", label %Label" + labelEnd + " [";
            for (int i = 0; i < ctx.caseStatement().size(); ++i) {
                ProgrammingLanguageParser.CaseStatementContext caseStatementContext = ctx.caseStatement(i);
                Pair<Type, List<String>> caseExpression = visitExpression(caseStatementContext.expression());
                if (caseExpression.getKey() != Type.INT) {
                    throw new IllegalArgumentException("Switch statement works only for integer type");
                }
                code.addAll(caseExpression.getValue());
                switchString = switchString + " i32 %tmp" + (varCounter - 1) + ", label %Label" + (firstLabel + i);
            }
            switchString = switchString + "]";
            code.add(switchString);

            for (int i = 0; i < ctx.caseStatement().size(); ++i) {
                code.add("Label" + (firstLabel + i) + ":");
                code.addAll(visitCompoundStatement(ctx.caseStatement(i).compoundStatement()).getValue());
                code.add("\tbr label %Label" + labelEnd);
            }

            code.add("Label" + labelEnd + ":");
            endLabels.pop();
            return new Pair<>(Type.VOID, code);
        }
        throw new UnsupportedOperationException("Unexpected selection statement behavior");
    }

    public Pair<Type, List<String>> visitCaseStatement(@NotNull ProgrammingLanguageParser.CaseStatementContext ctx) {
        throw new UnsupportedOperationException("You should not get there");
    }

    public Pair<Type, List<String>> visitJumpStatement(@NotNull ProgrammingLanguageParser.JumpStatementContext ctx) {
        List<String> code = new ArrayList<>();
        if (ctx.Break() != null) {
            if (endLabels.isEmpty()) {
                throw new IllegalArgumentException("Break should be inside while or switch");
            }
            code.add("\tbr label %Label" + endLabels.peek());
        } else if (ctx.Continue() != null) {
            if (endWhileLabels.isEmpty()) {
                throw new IllegalArgumentException("Continue should be inside while");
            }
            code.add("\tbr label %Label" + endWhileLabels.peek());
        } else {
            throw new UnsupportedOperationException("You should not get there");
        }
        return new Pair<>(Type.VOID, code);
    }

    public Pair<Type, List<String>> visitWhileStatement(@NotNull ProgrammingLanguageParser.WhileStatementContext ctx) {
        List<String> code = new ArrayList<>();
        Pair<Type, List<String>> expression = visitExpression(ctx.expression());
        if (expression.getKey() != Type.BOOLEAN) {
            throw new IllegalArgumentException("While conditional should be boolean");
        }
        int startLabel = labelCounter++;
        int bodyLabel = labelCounter++;
        int endLabel = labelCounter++;
        endLabels.push(endLabel);
        endWhileLabels.push(endLabel);

        code.add("\tbr label %Label" + startLabel);
        code.add("Label" + startLabel + ":");
        code.addAll(expression.getValue());
        code.add("\tbr i1 %tmp" + (varCounter - 1) + ", label %Label" + bodyLabel + ", label %Label" + endLabel);
        code.add("Label" + bodyLabel + ":");
        code.addAll(visitCompoundStatement(ctx.compoundStatement()).getValue());
        code.add("\tbr label %Label" + startLabel);
        code.add("Label" + endLabel + ":");

        endLabels.pop();
        endWhileLabels.pop();
        return new Pair<>(Type.VOID, code);
    }

    public Pair<Type, List<String>> visitFunctionCall(@NotNull ProgrammingLanguageParser.FunctionCallContext ctx) {
        List<String> code = new ArrayList<>();
        if (ctx.Read() != null) {
            String id = ctx.Id().getText();
            if (!variables.containsKey(id)) {
                throw new IllegalArgumentException("Undefined variable");
            }
            Type type = variables.get(id);
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
            return new Pair<>(Type.VOID, code);
        } else if (ctx.Write() != null) {
            Pair<Type, List<String>> expression = visitExpression(ctx.expression());
            code.addAll(expression.getValue());
            Type type = expression.getKey();
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
            return new Pair<>(Type.VOID, code);
        } else {
            throw new UnsupportedOperationException("You should not get there");
        }
    }

    public Pair<Type, List<String>> visitExpressionList(@NotNull ProgrammingLanguageParser.ExpressionListContext ctx) {
        return null;
    }

    public Pair<Type, List<String>> visitExpression(@NotNull ProgrammingLanguageParser.ExpressionContext ctx) {
        Pair<Type, List<String>> andExpr = visitAndExpr(ctx.andExpr(0));
        int andExprNumber = varCounter - 1;
        for (int i = 1; i < ctx.andExpr().size(); ++i) {
            Pair<Type, List<String>> nextAndExpr = visitAndExpr(ctx.andExpr(i));
            int nextAndExprNumber = varCounter - 1;
            if (andExpr.getKey() == Type.BOOLEAN && nextAndExpr.getKey() == Type.BOOLEAN) {
                andExpr.getValue().addAll(nextAndExpr.getValue());
                andExpr.getValue().add("\t%tmp" + varCounter + " = or i1 %tmp" + andExprNumber + ", %tmp" + nextAndExprNumber);
                andExprNumber = varCounter++;
            } else {
                throw new IllegalArgumentException("Both values must be boolean");
            }
        }

        return andExpr;
    }

    public Pair<Type, List<String>> visitAndExpr(@NotNull ProgrammingLanguageParser.AndExprContext ctx) {
        Pair<Type, List<String>> eqExpr = visitEqExpr(ctx.eqExpr(0));
        int eqExprNumber = varCounter - 1;
        for (int i = 1; i < ctx.eqExpr().size(); ++i) {
            Pair<Type, List<String>> nextEqExpr = visitEqExpr(ctx.eqExpr(i));
            int nextEqExprNumber = varCounter - 1;
            if (eqExpr.getKey() == Type.BOOLEAN && nextEqExpr.getKey() == Type.BOOLEAN) {
                eqExpr.getValue().addAll(nextEqExpr.getValue());
                eqExpr.getValue().add("\t%tmp" + varCounter + " = and i1 %tmp" + eqExprNumber + ", %tmp" + nextEqExprNumber);
                eqExprNumber = varCounter++;
            } else {
                throw new IllegalArgumentException("Both values must be boolean");
            }
        }

        return eqExpr;
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

    public Pair<Type, List<String>> visitEqExpr(@NotNull ProgrammingLanguageParser.EqExprContext ctx) {
        Pair<Type, List<String>> relExpr = visitRelExpr(ctx.relExpr(0));
        int relExprNumber = varCounter - 1;
        for (int i = 1; i < ctx.relExpr().size(); ++i) {
            Pair<Type, List<String>> nextRelExpr = visitRelExpr(ctx.relExpr(i));
            int nextRelExprNumber = varCounter - 1;
            String operationType = getEqOperationType(ctx.getChild(i * 2 - 1).getText());
            if (relExpr.getKey() == Type.BOOLEAN && nextRelExpr.getKey() == Type.BOOLEAN) {
                relExpr.getValue().addAll(nextRelExpr.getValue());
                relExpr.getValue().add("\t%tmp" + varCounter + " = icmp " + operationType + " i1 %tmp" + relExprNumber + ", %tmp" + nextRelExprNumber);
                relExprNumber = varCounter++;
            } else if (relExpr.getKey() == Type.INT && nextRelExpr.getKey() == Type.INT) {
                relExpr.getValue().addAll(nextRelExpr.getValue());
                relExpr.getValue().add("\t%tmp" + varCounter + " = icmp " + operationType + " i32 %tmp" + relExprNumber + ", %tmp" + nextRelExprNumber);
                relExprNumber = varCounter++;
                relExpr = new Pair<>(Type.BOOLEAN, relExpr.getValue());
            } else if (relExpr.getKey() == Type.STRING && nextRelExpr.getKey() == Type.STRING) {
                relExpr.getValue().addAll(nextRelExpr.getValue());
                relExpr.getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 0");
                relExpr.getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + relExprNumber + ", i32 0, i32 0");
                relExpr.getValue().add("\t%tmp" + varCounter++ + " = call i32 @strcmp(i8* %help_tmp" + (helpCounter - 2) + ", i8* %help_tmp" + (helpCounter - 1) + ") nounwind readonly");
                relExpr.getValue().add("\t%tmp" + varCounter + " = icmp " + operationType + " i32 0, %tmp" + (varCounter - 1));
                relExprNumber = varCounter++;
                relExpr = new Pair<>(Type.BOOLEAN, relExpr.getValue());
            }
            else {
                throw new IllegalArgumentException("Both values must be boolean");
            }
        }

        return relExpr;
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

    public Pair<Type, List<String>> visitRelExpr(@NotNull ProgrammingLanguageParser.RelExprContext ctx) {
        Pair<Type, List<String>> addExpr = visitAddExpr(ctx.addExpr(0));
        int addExprNumber = varCounter - 1;
        for (int i = 1; i < ctx.addExpr().size(); ++i) {
            Pair<Type, List<String>> nextAddExpr = visitAddExpr(ctx.addExpr(i));
            int nextAddExprNumber = varCounter - 1;
            String operationType = getRelOperationType(ctx.getChild(i * 2 - 1).getText());
            if (addExpr.getKey() == Type.INT && nextAddExpr.getKey() == Type.INT) {
                addExpr.getValue().addAll(nextAddExpr.getValue());
                addExpr.getValue().add("\t%tmp" + varCounter + " = icmp " + operationType + " i32 %tmp" + addExprNumber + ", %tmp" + nextAddExprNumber);
                addExprNumber = varCounter++;
                addExpr = new Pair<>(Type.BOOLEAN, addExpr.getValue());
            } else if (addExpr.getKey() == Type.STRING && nextAddExpr.getKey() == Type.STRING) {
                addExpr.getValue().addAll(nextAddExpr.getValue());
                addExpr.getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 0");
                addExpr.getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + addExprNumber + ", i32 0, i32 0");
                addExpr.getValue().add("\t%tmp" + varCounter++ + " = call i32 @strcmp(i8* %help_tmp" + (helpCounter - 2) + ", i8* %help_tmp" + (helpCounter - 1) + ") nounwind readonly");
                addExpr.getValue().add("\t%tmp" + varCounter + " = icmp " + operationType + " i32 0, %tmp" + (varCounter - 1));
                addExprNumber = varCounter++;
                addExpr = new Pair<>(Type.BOOLEAN, addExpr.getValue());
            }
            else {
                throw new IllegalArgumentException("Both values must have same type");
            }
        }

        return addExpr;
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

    public Pair<Type, List<String>> visitAddExpr(@NotNull ProgrammingLanguageParser.AddExprContext ctx) {
        Pair<Type, List<String>> mulExpr = visitMulExpr(ctx.mulExpr(0));
        int mulExprNumber = varCounter - 1;
        for (int i = 1; i < ctx.mulExpr().size(); ++i) {
            Pair<Type, List<String>> nextMulExpr = visitMulExpr(ctx.mulExpr(i));
            int nextMulExprNumber = varCounter - 1;
            String operationType = getAddOperationType(ctx.getChild(i * 2 - 1).getText());
            if (mulExpr.getKey() == Type.INT && nextMulExpr.getKey() == Type.INT) {
                mulExpr.getValue().addAll(nextMulExpr.getValue());
                mulExpr.getValue().add("\t%tmp" + varCounter + " = " + operationType + " i32 %tmp" + mulExprNumber + ", %tmp" + nextMulExprNumber);
                mulExprNumber = varCounter++;
            } else if (mulExpr.getKey() == Type.STRING && nextMulExpr.getKey() == Type.STRING) {
                mulExpr.getValue().addAll(nextMulExpr.getValue());
                mulExpr.getValue().add("\t%tmp" + varCounter++ + " = alloca [256 x i8]");
                mulExpr.getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 0");
                mulExpr.getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + mulExprNumber + ", i32 0, i32 0");
                mulExpr.getValue().add("\tcall i8* @strcpy(i8* %help_tmp" + (helpCounter - 2) + ", i8* %help_tmp" + (helpCounter - 1) + ") nounwind");
                mulExpr.getValue().add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + nextMulExprNumber + ", i32 0, i32 0");
                mulExpr.getValue().add("\tcall i8* @strcat(i8* %help_tmp" + (helpCounter - 3) + ", i8* %help_tmp" + (helpCounter - 1) + ") nounwind");
                mulExprNumber = varCounter - 1;
            }
            else {
                throw new IllegalArgumentException("Both values must have int or string type");
            }
        }

        return mulExpr;
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

    public Pair<Type, List<String>> visitMulExpr(@NotNull ProgrammingLanguageParser.MulExprContext ctx) {
        Pair<Type, List<String>> atom = visitAtom(ctx.atom(0));
        int atomNumber = varCounter - 1;
        for (int i = 1; i < ctx.atom().size(); ++i) {
            Pair<Type, List<String>> nextAtom = visitAtom(ctx.atom(i));
            int nextAtomNumber = varCounter - 1;
            String operationType = getMulOperationType(ctx.getChild(i * 2 - 1).getText());
            if (atom.getKey() == Type.INT && nextAtom.getKey() == Type.INT) {
                atom.getValue().addAll(nextAtom.getValue());
                atom.getValue().add("\t%tmp" + varCounter + " = " + operationType + " i32 %tmp" + atomNumber + ", %tmp" + nextAtomNumber);
                atomNumber = varCounter++;
            }
            else {
                throw new IllegalArgumentException("Both values must have int type");
            }
        }

        return atom;
    }

    public Pair<Type, List<String>> visitAtom(@NotNull ProgrammingLanguageParser.AtomContext ctx) {
        List<String> code = new LinkedList<>();
        if (ctx.Bool() != null) {
            code.add("\t%tmp" + varCounter++ + " = alloca i1");
            code.add("\tstore i1 " + (ctx.Bool().getText().equals("true") ? 1 : 0) + ", i1* %tmp" + (varCounter - 1));
            code.add("\t%tmp" + varCounter++ + " = load i1* %tmp" + (varCounter - 2));
            return new Pair<>(Type.BOOLEAN, code);
        } else if (ctx.Int() != null) {
            code.add("\t%tmp" + varCounter++ + " = alloca i32");
            code.add("\tstore i32 " + ctx.Int().getText() + ", i32* %tmp" + (varCounter - 1));
            code.add("\t%tmp" + varCounter++ + " = load i32* %tmp" + (varCounter - 2));
            return new Pair<>(Type.INT, code);
        } else if (ctx.lookup() != null) {
            return visitLookup(ctx.lookup());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private int countNewStrings(String str) {
        int result = 0;
        for (int i = 0; i < str.length() - 2; ++i) {
            if (str.substring(i, i + 3).equals("\\0A")){
                result += 1;
            }
        }
        return result;
    }

    //TODO add index? lookup
    public Pair<Type, List<String>> visitLookup(@NotNull ProgrammingLanguageParser.LookupContext ctx) {
        List<String> code = new LinkedList<>();
        if (ctx.Id() != null) {
            String id = ctx.Id().getText();
            if (!variables.containsKey(id)) {
                throw new IllegalArgumentException("Undeclared variable: " + id);
            }
            code.add("\t%tmp" + varCounter++ + " = load " + getLLVMType(variables.get(id)) + "* %var_" + id);
            if (variables.get(id) == Type.STRING) {
                code.add("\t%tmp" + varCounter++ + " = alloca [256 x i8]");
                code.add("\tstore [256 x i8] %tmp" + (varCounter - 2) + ", [256 x i8]* %tmp" + (varCounter - 1));
            }
            return new Pair<>(variables.get(id), code);
        } else if (ctx.String() != null) {
            String str = ctx.String().getText();
            str = str.substring(1, str.length() - 1);
            if (str.length() > 255) {
                str = str.substring(0, 255);
            }

            String constant = "@.str" + constCounter++  + " = private unnamed_addr constant [256 x i8] c\"" + str;
            int newStr = countNewStrings(str);
            for (int i = 0; i < 256 - str.length() + newStr * 2; ++i) {
                constant += "\\00";
            }
            constant += "\", align 1";
            constants.add(constant);
            code.add("\t%tmp" + varCounter++ + " = alloca [256 x i8]");
            code.add("\t%help_tmp" + helpCounter++ + " = bitcast [256 x i8]* %tmp" + (varCounter - 1) + " to i8*");
            code.add("\tcall void @llvm.memcpy.p0i8.p0i8.i32(i8* %help_tmp" + (helpCounter - 1) + ", i8* getelementptr inbounds ([256 x i8]* @.str" + (constCounter - 1) + ", i32 0, i32 0), i32 256, i32 1, i1 false)");
            return new Pair<>(Type.STRING, code);
        } else if (ctx.expression() != null) {
            return visitExpression(ctx.expression());
        } else if (ctx.functionCall() != null) {
            return visitFunctionCall(ctx.functionCall());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public Pair<Type, List<String>> visitIndex(@NotNull ProgrammingLanguageParser.IndexContext ctx) {
        Pair<Type, List<String>> expr = visitExpression(ctx.expression());
        if (expr.getKey() != Type.INT) {
            throw new IllegalArgumentException("Index must integer value");
        }
        return expr;
    }

    public Pair<Type, List<String>> visitAssignment(@NotNull ProgrammingLanguageParser.AssignmentContext ctx) {
        String id = ctx.Id().getText();
        Pair<Type, List<String>> expr = visitExpression(ctx.expression());
        List<String> code = expr.getValue();
        switch (expr.getKey()) {
            case INT:
                if (variables.get(id) != Type.INT) {
                    throw new IllegalArgumentException("Can't assign integer to variable of type " + variables.get(id).toString());
                }
                code.add("\tstore i32 %tmp" + (varCounter - 1) + ", i32* %var_" + id + ", align 4");
                break;
            case BOOLEAN:
                if (variables.get(id) != Type.BOOLEAN) {
                    throw new IllegalArgumentException("Can't assign boolean to variable of type " + variables.get(id).toString());
                }
                code.add("\tstore i1 %tmp" + (varCounter - 1) + ", i1* %var_" + id + ", align 4");
                break;
            case STRING:
                if (variables.get(id) != Type.STRING) {
                    throw new IllegalArgumentException("Can't assign string to variable of type " + variables.get(id).toString());
                }
                code.add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %var_" + id + ", i32 0, i32 0");
                code.add("\t%help_tmp" + helpCounter++ + " = getelementptr inbounds [256 x i8]* %tmp" + (varCounter - 1) + ", i32 0, i32 0");
                code.add("\tcall i8* @strcpy(i8* %help_tmp" + (helpCounter - 2) + ", i8* %help_tmp" + (helpCounter - 1) + ") nounwind");
                break;
            default:
                throw new IllegalArgumentException("Expression of type void can't be assigned to anything");
        }
        return new Pair<>(Type.VOID, code);
    }

    public Pair<Type, List<String>> visitDeclarationId(@NotNull ProgrammingLanguageParser.DeclarationIdContext ctx) {
        Type varType = visitTypeSpecifier(ctx.typeSpecifier()).getKey();
        List<String> code = new ArrayList<>();
        for (TerminalNode terminalNode : ctx.Id()) {
            variables.put(terminalNode.getText(), varType);
            code.add("\t%var_" + terminalNode.getText() + " = alloca " + getLLVMType(varType));
        }
        return new Pair<>(Type.VOID, code);
    }

    public Pair<Type, List<String>> visitChildren(RuleNode ruleNode) {
        return null;
    }

    public Pair<Type, List<String>> visitTerminal(TerminalNode terminalNode) {
        return null;
    }

    public Pair<Type, List<String>> visitErrorNode(ErrorNode errorNode) {
        return null;
    }
}
