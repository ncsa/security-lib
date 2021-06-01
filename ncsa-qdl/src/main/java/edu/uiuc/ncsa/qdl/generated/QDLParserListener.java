// Generated from QDLParser.g4 by ANTLR 4.9.1
package edu.uiuc.ncsa.qdl.generated;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link QDLParserParser}.
 */
public interface QDLParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#elements}.
	 * @param ctx the parse tree
	 */
	void enterElements(QDLParserParser.ElementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#elements}.
	 * @param ctx the parse tree
	 */
	void exitElements(QDLParserParser.ElementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(QDLParserParser.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(QDLParserParser.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(QDLParserParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(QDLParserParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(QDLParserParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(QDLParserParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#conditionalStatement}.
	 * @param ctx the parse tree
	 */
	void enterConditionalStatement(QDLParserParser.ConditionalStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#conditionalStatement}.
	 * @param ctx the parse tree
	 */
	void exitConditionalStatement(QDLParserParser.ConditionalStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(QDLParserParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(QDLParserParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#ifElseStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfElseStatement(QDLParserParser.IfElseStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#ifElseStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfElseStatement(QDLParserParser.IfElseStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void enterLoopStatement(QDLParserParser.LoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void exitLoopStatement(QDLParserParser.LoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#switchStatement}.
	 * @param ctx the parse tree
	 */
	void enterSwitchStatement(QDLParserParser.SwitchStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#switchStatement}.
	 * @param ctx the parse tree
	 */
	void exitSwitchStatement(QDLParserParser.SwitchStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#defineStatement}.
	 * @param ctx the parse tree
	 */
	void enterDefineStatement(QDLParserParser.DefineStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#defineStatement}.
	 * @param ctx the parse tree
	 */
	void exitDefineStatement(QDLParserParser.DefineStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#lambdaStatement}.
	 * @param ctx the parse tree
	 */
	void enterLambdaStatement(QDLParserParser.LambdaStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#lambdaStatement}.
	 * @param ctx the parse tree
	 */
	void exitLambdaStatement(QDLParserParser.LambdaStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#moduleStatement}.
	 * @param ctx the parse tree
	 */
	void enterModuleStatement(QDLParserParser.ModuleStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#moduleStatement}.
	 * @param ctx the parse tree
	 */
	void exitModuleStatement(QDLParserParser.ModuleStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#tryCatchStatement}.
	 * @param ctx the parse tree
	 */
	void enterTryCatchStatement(QDLParserParser.TryCatchStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#tryCatchStatement}.
	 * @param ctx the parse tree
	 */
	void exitTryCatchStatement(QDLParserParser.TryCatchStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#statementBlock}.
	 * @param ctx the parse tree
	 */
	void enterStatementBlock(QDLParserParser.StatementBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#statementBlock}.
	 * @param ctx the parse tree
	 */
	void exitStatementBlock(QDLParserParser.StatementBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#docStatementBlock}.
	 * @param ctx the parse tree
	 */
	void enterDocStatementBlock(QDLParserParser.DocStatementBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#docStatementBlock}.
	 * @param ctx the parse tree
	 */
	void exitDocStatementBlock(QDLParserParser.DocStatementBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#expressionBlock}.
	 * @param ctx the parse tree
	 */
	void enterExpressionBlock(QDLParserParser.ExpressionBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#expressionBlock}.
	 * @param ctx the parse tree
	 */
	void exitExpressionBlock(QDLParserParser.ExpressionBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#conditionalBlock}.
	 * @param ctx the parse tree
	 */
	void enterConditionalBlock(QDLParserParser.ConditionalBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#conditionalBlock}.
	 * @param ctx the parse tree
	 */
	void exitConditionalBlock(QDLParserParser.ConditionalBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#fdoc}.
	 * @param ctx the parse tree
	 */
	void enterFdoc(QDLParserParser.FdocContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#fdoc}.
	 * @param ctx the parse tree
	 */
	void exitFdoc(QDLParserParser.FdocContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#iInterval}.
	 * @param ctx the parse tree
	 */
	void enterIInterval(QDLParserParser.IIntervalContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#iInterval}.
	 * @param ctx the parse tree
	 */
	void exitIInterval(QDLParserParser.IIntervalContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#rInterval}.
	 * @param ctx the parse tree
	 */
	void enterRInterval(QDLParserParser.RIntervalContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#rInterval}.
	 * @param ctx the parse tree
	 */
	void exitRInterval(QDLParserParser.RIntervalContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#stemVariable}.
	 * @param ctx the parse tree
	 */
	void enterStemVariable(QDLParserParser.StemVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#stemVariable}.
	 * @param ctx the parse tree
	 */
	void exitStemVariable(QDLParserParser.StemVariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#stemEntry}.
	 * @param ctx the parse tree
	 */
	void enterStemEntry(QDLParserParser.StemEntryContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#stemEntry}.
	 * @param ctx the parse tree
	 */
	void exitStemEntry(QDLParserParser.StemEntryContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#stemList}.
	 * @param ctx the parse tree
	 */
	void enterStemList(QDLParserParser.StemListContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#stemList}.
	 * @param ctx the parse tree
	 */
	void exitStemList(QDLParserParser.StemListContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#stemValue}.
	 * @param ctx the parse tree
	 */
	void enterStemValue(QDLParserParser.StemValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#stemValue}.
	 * @param ctx the parse tree
	 */
	void exitStemValue(QDLParserParser.StemValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(QDLParserParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(QDLParserParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#f_arg}.
	 * @param ctx the parse tree
	 */
	void enterF_arg(QDLParserParser.F_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#f_arg}.
	 * @param ctx the parse tree
	 */
	void exitF_arg(QDLParserParser.F_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#f_args}.
	 * @param ctx the parse tree
	 */
	void enterF_args(QDLParserParser.F_argsContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#f_args}.
	 * @param ctx the parse tree
	 */
	void exitF_args(QDLParserParser.F_argsContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#f_ref}.
	 * @param ctx the parse tree
	 */
	void enterF_ref(QDLParserParser.F_refContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#f_ref}.
	 * @param ctx the parse tree
	 */
	void exitF_ref(QDLParserParser.F_refContext ctx);
	/**
	 * Enter a parse tree produced by the {@code functions}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunctions(QDLParserParser.FunctionsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code functions}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunctions(QDLParserParser.FunctionsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code keywords}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterKeywords(QDLParserParser.KeywordsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code keywords}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitKeywords(QDLParserParser.KeywordsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code prefix}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrefix(QDLParserParser.PrefixContext ctx);
	/**
	 * Exit a parse tree produced by the {@code prefix}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrefix(QDLParserParser.PrefixContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tildeExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterTildeExpression(QDLParserParser.TildeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tildeExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitTildeExpression(QDLParserParser.TildeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numbers}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNumbers(QDLParserParser.NumbersContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numbers}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNumbers(QDLParserParser.NumbersContext ctx);
	/**
	 * Enter a parse tree produced by the {@code association}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAssociation(QDLParserParser.AssociationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code association}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAssociation(QDLParserParser.AssociationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNotExpression(QDLParserParser.NotExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNotExpression(QDLParserParser.NotExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code multiplyExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplyExpression(QDLParserParser.MultiplyExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multiplyExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplyExpression(QDLParserParser.MultiplyExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code integers}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterIntegers(QDLParserParser.IntegersContext ctx);
	/**
	 * Exit a parse tree produced by the {@code integers}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitIntegers(QDLParserParser.IntegersContext ctx);
	/**
	 * Enter a parse tree produced by the {@code andExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAndExpression(QDLParserParser.AndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code andExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAndExpression(QDLParserParser.AndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code strings}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterStrings(QDLParserParser.StringsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code strings}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitStrings(QDLParserParser.StringsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code compExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterCompExpression(QDLParserParser.CompExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code compExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitCompExpression(QDLParserParser.CompExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code postfix}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPostfix(QDLParserParser.PostfixContext ctx);
	/**
	 * Exit a parse tree produced by the {@code postfix}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPostfix(QDLParserParser.PostfixContext ctx);
	/**
	 * Enter a parse tree produced by the {@code realInterval}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterRealInterval(QDLParserParser.RealIntervalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code realInterval}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitRealInterval(QDLParserParser.RealIntervalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code variables}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterVariables(QDLParserParser.VariablesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code variables}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitVariables(QDLParserParser.VariablesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stemLi}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterStemLi(QDLParserParser.StemLiContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stemLi}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitStemLi(QDLParserParser.StemLiContext ctx);
	/**
	 * Enter a parse tree produced by the {@code regexMatches}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterRegexMatches(QDLParserParser.RegexMatchesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code regexMatches}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitRegexMatches(QDLParserParser.RegexMatchesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code intInterval}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterIntInterval(QDLParserParser.IntIntervalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intInterval}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitIntInterval(QDLParserParser.IntIntervalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code logical}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLogical(QDLParserParser.LogicalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code logical}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLogical(QDLParserParser.LogicalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code altIFExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAltIFExpression(QDLParserParser.AltIFExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code altIFExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAltIFExpression(QDLParserParser.AltIFExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code orExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterOrExpression(QDLParserParser.OrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code orExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitOrExpression(QDLParserParser.OrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unaryMinusExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryMinusExpression(QDLParserParser.UnaryMinusExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unaryMinusExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryMinusExpression(QDLParserParser.UnaryMinusExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code powerExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPowerExpression(QDLParserParser.PowerExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code powerExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPowerExpression(QDLParserParser.PowerExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eqExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterEqExpression(QDLParserParser.EqExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eqExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitEqExpression(QDLParserParser.EqExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code null}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNull(QDLParserParser.NullContext ctx);
	/**
	 * Exit a parse tree produced by the {@code null}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNull(QDLParserParser.NullContext ctx);
	/**
	 * Enter a parse tree produced by the {@code addExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAddExpression(QDLParserParser.AddExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code addExpression}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAddExpression(QDLParserParser.AddExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code semi_for_empty_expressions}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSemi_for_empty_expressions(QDLParserParser.Semi_for_empty_expressionsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code semi_for_empty_expressions}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSemi_for_empty_expressions(QDLParserParser.Semi_for_empty_expressionsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stemVar}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterStemVar(QDLParserParser.StemVarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stemVar}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitStemVar(QDLParserParser.StemVarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code dotOp}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterDotOp(QDLParserParser.DotOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code dotOp}
	 * labeled alternative in {@link QDLParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitDotOp(QDLParserParser.DotOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(QDLParserParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(QDLParserParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(QDLParserParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(QDLParserParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#integer}.
	 * @param ctx the parse tree
	 */
	void enterInteger(QDLParserParser.IntegerContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#integer}.
	 * @param ctx the parse tree
	 */
	void exitInteger(QDLParserParser.IntegerContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDLParserParser#keyword}.
	 * @param ctx the parse tree
	 */
	void enterKeyword(QDLParserParser.KeywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDLParserParser#keyword}.
	 * @param ctx the parse tree
	 */
	void exitKeyword(QDLParserParser.KeywordContext ctx);
}