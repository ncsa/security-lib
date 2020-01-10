package edu.uiuc.ncsa.security.util.qdl;

import edu.uiuc.ncsa.security.util.qdl.generated.QDLParserListener;
import edu.uiuc.ncsa.security.util.qdl.generated.QDLParserParser;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/20 at  11:50 AM
 */
public class QDLListener implements QDLParserListener {
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    SymbolTable symbolTable  = new SymbolTable();
    
    @Override
    public void enterSemi_for_empty_expressions(QDLParserParser.Semi_for_empty_expressionsContext ctx) {
        say("enterSemi", ctx);

    }

    @Override
    public void exitSemi_for_empty_expressions(QDLParserParser.Semi_for_empty_expressionsContext ctx) {
           say("exitSemi", ctx);
    }

    protected void say(String x){
        System.out.println("QDLListener:" + x);
    }
    protected String glomParseTree(ParserRuleContext ctx){
        // take parse tree, reconstruct the text in it.
        String out = "";
        for(ParseTree o : ctx.children){
           out = out + o.getText();
        }
        return out;
    }
    protected void say(String x, ParserRuleContext ctx){
        say(x + ", content= \"" + glomParseTree(ctx) + "\"");
    }
    @Override
    public void enterElements(QDLParserParser.ElementsContext ctx) {

    }

    @Override
    public void exitElements(QDLParserParser.ElementsContext ctx) {
            say("exitElements", ctx);
    }

    @Override
    public void enterScalar(QDLParserParser.ScalarContext ctx) {

    }

    @Override
    public void exitScalar(QDLParserParser.ScalarContext ctx) {

    }

    @Override
    public void enterStem(QDLParserParser.StemContext ctx) {
    }

    @Override
    public void exitStem(QDLParserParser.StemContext ctx) {
    }

    @Override
    public void enterAssignment(QDLParserParser.AssignmentContext ctx) {

    }



    @Override
    public void exitAssignment(QDLParserParser.AssignmentContext ctx) {
           say("exit assignment", ctx);
           String name = ctx.children.get(0).getText();
           String rawValue = ctx.children.get(2).getText().trim();


        //   getSymbolTable().put(realName, realValue);
    }


    @Override
    public void enterArgList(QDLParserParser.ArgListContext ctx) {

    }

    @Override
    public void exitArgList(QDLParserParser.ArgListContext ctx) {

    }

    @Override
    public void enterFunction(QDLParserParser.FunctionContext ctx) {

    }

    @Override
    public void exitFunction(QDLParserParser.FunctionContext ctx) {
     say("exitFunction", ctx);
    }

    @Override
    public void enterVariables(QDLParserParser.VariablesContext ctx) {

    }

    @Override
    public void exitVariables(QDLParserParser.VariablesContext ctx) {
             say("exitVariables", ctx);
    }

    @Override
    public void enterFunctions(QDLParserParser.FunctionsContext ctx) {

    }

    @Override
    public void exitFunctions(QDLParserParser.FunctionsContext ctx) {
                say("exitFunctions", ctx);
    }

    @Override
    public void enterPrefix(QDLParserParser.PrefixContext ctx) {

    }

    @Override
    public void exitPrefix(QDLParserParser.PrefixContext ctx) {

    }

    @Override
    public void enterNumbers(QDLParserParser.NumbersContext ctx) {

    }

    @Override
    public void exitNumbers(QDLParserParser.NumbersContext ctx) {

    }

    @Override
    public void enterAssociation(QDLParserParser.AssociationContext ctx) {

    }

    @Override
    public void exitAssociation(QDLParserParser.AssociationContext ctx) {

    }

    @Override
    public void enterNotExpression(QDLParserParser.NotExpressionContext ctx) {

    }

    @Override
    public void exitNotExpression(QDLParserParser.NotExpressionContext ctx) {

    }

    @Override
    public void enterLeftBracket(QDLParserParser.LeftBracketContext ctx) {

    }

    @Override
    public void exitLeftBracket(QDLParserParser.LeftBracketContext ctx) {

    }

    @Override
    public void enterStemVariables(QDLParserParser.StemVariablesContext ctx) {

    }

    @Override
    public void exitStemVariables(QDLParserParser.StemVariablesContext ctx) {

    }

    @Override
    public void enterLogical(QDLParserParser.LogicalContext ctx) {

    }

    @Override
    public void exitLogical(QDLParserParser.LogicalContext ctx) {

    }

    @Override
    public void enterOrExpression(QDLParserParser.OrExpressionContext ctx) {

    }

    @Override
    public void exitOrExpression(QDLParserParser.OrExpressionContext ctx) {

    }

    @Override
    public void enterUnaryMinusExpression(QDLParserParser.UnaryMinusExpressionContext ctx) {

    }

    @Override
    public void exitUnaryMinusExpression(QDLParserParser.UnaryMinusExpressionContext ctx) {

    }

    @Override
    public void enterEqExpression(QDLParserParser.EqExpressionContext ctx) {

    }

    @Override
    public void exitEqExpression(QDLParserParser.EqExpressionContext ctx) {

    }

    @Override
    public void enterAndExpression(QDLParserParser.AndExpressionContext ctx) {

    }

    @Override
    public void exitAndExpression(QDLParserParser.AndExpressionContext ctx) {

    }

    @Override
    public void enterNull(QDLParserParser.NullContext ctx) {

    }

    @Override
    public void exitNull(QDLParserParser.NullContext ctx) {

    }

    @Override
    public void enterStrings(QDLParserParser.StringsContext ctx) {

    }

    @Override
    public void exitStrings(QDLParserParser.StringsContext ctx) {
             say("exitStrings", ctx);
    }

    @Override
    public void enterAddExpression(QDLParserParser.AddExpressionContext ctx) {

    }

    @Override
    public void exitAddExpression(QDLParserParser.AddExpressionContext ctx) {

    }

    @Override
    public void enterCompExpression(QDLParserParser.CompExpressionContext ctx) {

    }

    @Override
    public void exitCompExpression(QDLParserParser.CompExpressionContext ctx) {

    }

    @Override
    public void enterPostfix(QDLParserParser.PostfixContext ctx) {

    }

    @Override
    public void exitPostfix(QDLParserParser.PostfixContext ctx) {

    }

    @Override
    public void enterElement(QDLParserParser.ElementContext ctx) {

    }

    @Override
    public void exitElement(QDLParserParser.ElementContext ctx) {

    }

    @Override
    public void enterStatement(QDLParserParser.StatementContext ctx) {

    }

    @Override
    public void exitStatement(QDLParserParser.StatementContext ctx) {

    }

    @Override
    public void enterIfStatement(QDLParserParser.IfStatementContext ctx) {

    }

    @Override
    public void exitIfStatement(QDLParserParser.IfStatementContext ctx) {

    }

    @Override
    public void enterIfElseStatement(QDLParserParser.IfElseStatementContext ctx) {

    }

    @Override
    public void exitIfElseStatement(QDLParserParser.IfElseStatementContext ctx) {

    }

    @Override
    public void enterConditionalStatement(QDLParserParser.ConditionalStatementContext ctx) {

    }

    @Override
    public void exitConditionalStatement(QDLParserParser.ConditionalStatementContext ctx) {
        say("exitConditional", ctx);

    }

    @Override
    public void enterLoopStatement(QDLParserParser.LoopStatementContext ctx) {

    }

    @Override
    public void exitLoopStatement(QDLParserParser.LoopStatementContext ctx) {

    }

    @Override
    public void enterSwitchStatement(QDLParserParser.SwitchStatementContext ctx) {

    }

    @Override
    public void exitSwitchStatement(QDLParserParser.SwitchStatementContext ctx) {
        say("exitSwitch", ctx);

    }

    @Override
    public void enterDefineStatement(QDLParserParser.DefineStatementContext ctx) {

    }

    @Override
    public void exitDefineStatement(QDLParserParser.DefineStatementContext ctx) {
             say("exitDefine", ctx);
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }
}
