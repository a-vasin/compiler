// Generated from C:/Users/Rabo/IdeaProjects/ProgrammingLanguage/src/main/java\ProgrammingLanguage.g4 by ANTLR 4.5
package antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ProgrammingLanguageParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ProgrammingLanguageVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#parse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParse(@NotNull ProgrammingLanguageParser.ParseContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#functionDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDefinition(@NotNull ProgrammingLanguageParser.FunctionDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#idList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdList(@NotNull ProgrammingLanguageParser.IdListContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#typeSpecifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeSpecifier(@NotNull ProgrammingLanguageParser.TypeSpecifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#functionBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionBody(@NotNull ProgrammingLanguageParser.FunctionBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#compoundStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompoundStatement(@NotNull ProgrammingLanguageParser.CompoundStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(@NotNull ProgrammingLanguageParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#unionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnionStatement(@NotNull ProgrammingLanguageParser.UnionStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#selectionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectionStatement(@NotNull ProgrammingLanguageParser.SelectionStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#caseStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseStatement(@NotNull ProgrammingLanguageParser.CaseStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#jumpStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJumpStatement(@NotNull ProgrammingLanguageParser.JumpStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(@NotNull ProgrammingLanguageParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(@NotNull ProgrammingLanguageParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(@NotNull ProgrammingLanguageParser.ExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(@NotNull ProgrammingLanguageParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#andExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndExpr(@NotNull ProgrammingLanguageParser.AndExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#eqExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqExpr(@NotNull ProgrammingLanguageParser.EqExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#relExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelExpr(@NotNull ProgrammingLanguageParser.RelExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#addExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExpr(@NotNull ProgrammingLanguageParser.AddExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#mulExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulExpr(@NotNull ProgrammingLanguageParser.MulExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(@NotNull ProgrammingLanguageParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#lookup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLookup(@NotNull ProgrammingLanguageParser.LookupContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#index}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndex(@NotNull ProgrammingLanguageParser.IndexContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(@NotNull ProgrammingLanguageParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProgrammingLanguageParser#declarationId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarationId(@NotNull ProgrammingLanguageParser.DeclarationIdContext ctx);
}