// Generated from C:/Users/Rabo/IdeaProjects/ProgrammingLanguage/src/main/java\ProgrammingLanguage.g4 by ANTLR 4.5
package antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ProgrammingLanguageParser}.
 */
public interface ProgrammingLanguageListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#parse}.
	 * @param ctx the parse tree
	 */
	void enterParse(@NotNull ProgrammingLanguageParser.ParseContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#parse}.
	 * @param ctx the parse tree
	 */
	void exitParse(@NotNull ProgrammingLanguageParser.ParseContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#functionDefinition}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDefinition(@NotNull ProgrammingLanguageParser.FunctionDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#functionDefinition}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDefinition(@NotNull ProgrammingLanguageParser.FunctionDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#idList}.
	 * @param ctx the parse tree
	 */
	void enterIdList(@NotNull ProgrammingLanguageParser.IdListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#idList}.
	 * @param ctx the parse tree
	 */
	void exitIdList(@NotNull ProgrammingLanguageParser.IdListContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#typeSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterTypeSpecifier(@NotNull ProgrammingLanguageParser.TypeSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#typeSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitTypeSpecifier(@NotNull ProgrammingLanguageParser.TypeSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void enterFunctionBody(@NotNull ProgrammingLanguageParser.FunctionBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void exitFunctionBody(@NotNull ProgrammingLanguageParser.FunctionBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#compoundStatement}.
	 * @param ctx the parse tree
	 */
	void enterCompoundStatement(@NotNull ProgrammingLanguageParser.CompoundStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#compoundStatement}.
	 * @param ctx the parse tree
	 */
	void exitCompoundStatement(@NotNull ProgrammingLanguageParser.CompoundStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(@NotNull ProgrammingLanguageParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(@NotNull ProgrammingLanguageParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#unionStatement}.
	 * @param ctx the parse tree
	 */
	void enterUnionStatement(@NotNull ProgrammingLanguageParser.UnionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#unionStatement}.
	 * @param ctx the parse tree
	 */
	void exitUnionStatement(@NotNull ProgrammingLanguageParser.UnionStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#selectionStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectionStatement(@NotNull ProgrammingLanguageParser.SelectionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#selectionStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectionStatement(@NotNull ProgrammingLanguageParser.SelectionStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#caseStatement}.
	 * @param ctx the parse tree
	 */
	void enterCaseStatement(@NotNull ProgrammingLanguageParser.CaseStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#caseStatement}.
	 * @param ctx the parse tree
	 */
	void exitCaseStatement(@NotNull ProgrammingLanguageParser.CaseStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterJumpStatement(@NotNull ProgrammingLanguageParser.JumpStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitJumpStatement(@NotNull ProgrammingLanguageParser.JumpStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(@NotNull ProgrammingLanguageParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(@NotNull ProgrammingLanguageParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(@NotNull ProgrammingLanguageParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(@NotNull ProgrammingLanguageParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(@NotNull ProgrammingLanguageParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(@NotNull ProgrammingLanguageParser.ExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(@NotNull ProgrammingLanguageParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(@NotNull ProgrammingLanguageParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void enterAndExpr(@NotNull ProgrammingLanguageParser.AndExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void exitAndExpr(@NotNull ProgrammingLanguageParser.AndExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#eqExpr}.
	 * @param ctx the parse tree
	 */
	void enterEqExpr(@NotNull ProgrammingLanguageParser.EqExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#eqExpr}.
	 * @param ctx the parse tree
	 */
	void exitEqExpr(@NotNull ProgrammingLanguageParser.EqExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#relExpr}.
	 * @param ctx the parse tree
	 */
	void enterRelExpr(@NotNull ProgrammingLanguageParser.RelExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#relExpr}.
	 * @param ctx the parse tree
	 */
	void exitRelExpr(@NotNull ProgrammingLanguageParser.RelExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#addExpr}.
	 * @param ctx the parse tree
	 */
	void enterAddExpr(@NotNull ProgrammingLanguageParser.AddExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#addExpr}.
	 * @param ctx the parse tree
	 */
	void exitAddExpr(@NotNull ProgrammingLanguageParser.AddExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#mulExpr}.
	 * @param ctx the parse tree
	 */
	void enterMulExpr(@NotNull ProgrammingLanguageParser.MulExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#mulExpr}.
	 * @param ctx the parse tree
	 */
	void exitMulExpr(@NotNull ProgrammingLanguageParser.MulExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(@NotNull ProgrammingLanguageParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(@NotNull ProgrammingLanguageParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#lookup}.
	 * @param ctx the parse tree
	 */
	void enterLookup(@NotNull ProgrammingLanguageParser.LookupContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#lookup}.
	 * @param ctx the parse tree
	 */
	void exitLookup(@NotNull ProgrammingLanguageParser.LookupContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#index}.
	 * @param ctx the parse tree
	 */
	void enterIndex(@NotNull ProgrammingLanguageParser.IndexContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#index}.
	 * @param ctx the parse tree
	 */
	void exitIndex(@NotNull ProgrammingLanguageParser.IndexContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(@NotNull ProgrammingLanguageParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(@NotNull ProgrammingLanguageParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProgrammingLanguageParser#declarationId}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationId(@NotNull ProgrammingLanguageParser.DeclarationIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProgrammingLanguageParser#declarationId}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationId(@NotNull ProgrammingLanguageParser.DeclarationIdContext ctx);
}