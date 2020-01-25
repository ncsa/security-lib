package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.expressions.*;
import edu.uiuc.ncsa.qdl.generated.QDLParserListener;
import edu.uiuc.ncsa.qdl.generated.QDLParserParser;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.*;
import edu.uiuc.ncsa.qdl.variables.Constant;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigDecimal;
import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/15/20 at  6:17 AM
 */
public class QDLListener implements QDLParserListener {
    ParsingMap parsingMap = new ParsingMap();

    public ParsingMap getParsingMap() {
        return parsingMap;
    }

    public void setParsingMap(ParsingMap parsingMap) {
        this.parsingMap = parsingMap;
    }

    State state;

    /**
     * Note that the state is supplied here not for evaluation, but because it has the evaluators needed
     * to resolve the various types of operators that are being produced by the parser. The state
     * should be injected at runtime where it is actually known.
     *
     * @param parsingMap
     * @param state
     */
    public QDLListener(ParsingMap parsingMap, State state) {
        this.parsingMap = parsingMap;
        this.state = state;
    }

    @Override
    public void enterElements(QDLParserParser.ElementsContext ctx) {

    }

    @Override
    public void exitElements(QDLParserParser.ElementsContext ctx) {

    }

    @Override
    public void enterVariable(QDLParserParser.VariableContext ctx) {
        stash(ctx, new VariableNode(null));

    }

    @Override
    public void exitVariable(QDLParserParser.VariableContext ctx) {
        StatementRecord p = (StatementRecord) parsingMap.get(IDUtils.createIdentifier(ctx));
        if (ctx.getText().equals("true") || ctx.getText().equals("false")) {
            // SPECIAL CASE. The parse recognizes true and false, but does not know what to do with them.
            // We are here because it lumps them together with the variable values.
            ConstantNode cnode = new ConstantNode(new Boolean(ctx.getText().equals("true")), Constant.BOOLEAN_TYPE);
            p.statement = cnode;
            cnode.setSourceCode(ctx.getText());
        } else {
            ((VariableNode) parsingMap.getStatementFromContext(ctx)).setVariableReference(ctx.getText());
        }

    }

    protected void stash(ParseTree parseTree, Element element) {
        ElementRecord pr = new ElementRecord(parseTree, element);
        parsingMap.put(pr);
    }

    protected void stash(ParseTree parseTree, Statement statement) {
        StatementRecord pr = new StatementRecord(parseTree, statement);
        parsingMap.put(pr);
    }

    @Override
    public void enterAssignment(QDLParserParser.AssignmentContext ctx) {

        stash(ctx, new Assignment());
    }

    protected Statement resolveChild(ParseTree currentChild, boolean removeChild) {
        // the most common pattern is that a child node or one of its children is
        // the actual node we need. This checks if the argument is a child in the table
        // and returns that, if not it starts to look through descendents.
        String id = IDUtils.createIdentifier(currentChild);
        if (parsingMap.containsKey(id)) {
            Statement s = parsingMap.getStatementFromContext(currentChild);
            if (removeChild) {
                parsingMap.remove(IDUtils.createIdentifier(currentChild));
            }
            return s;
        }
        ParseRecord p = parsingMap.findFirstChild(currentChild);
        if (p != null && p instanceof StatementRecord) {
            Statement s = ((StatementRecord) p).statement;
            if (removeChild) {
                parsingMap.remove(IDUtils.createIdentifier(currentChild));
            }
            return s;
        }
        return null;

    }

    protected Statement resolveChild(ParseTree currentChild) {
        return resolveChild(currentChild, false);
    }

    @Override
    public void exitAssignment(QDLParserParser.AssignmentContext ctx) {
        Assignment assignment = (Assignment) parsingMap.getStatementFromContext(ctx);
        // The variable is the 0th child, the argument is the 2nd.
        assignment.setVariableReference(ctx.children.get(0).getText());
        ParseTree valueNode = ctx.children.get(2);
        ExpressionNode arg = (ExpressionNode) resolveChild(ctx.children.get(2));
        assignment.setArgument(arg);
        assignment.setSourceCode(ctx.getText());
    }

    @Override
    public void enterArgList(QDLParserParser.ArgListContext ctx) {

    }

    @Override
    public void exitArgList(QDLParserParser.ArgListContext ctx) {

    }

    @Override
    public void enterFunction(QDLParserParser.FunctionContext ctx) {
        stash(ctx, new Polyad());
    }

    @Override
    public void exitFunction(QDLParserParser.FunctionContext ctx) {
        // Note that this resolves **all** functions, specifically the built in ones.
        Polyad polyad = (Polyad) parsingMap.getStatementFromContext(ctx);
        polyad.setSourceCode(ctx.getText());
        // need to process its argument list.
        // There are 3 children  "f(" arglist ")", so we want the middle one.

        String name = ctx.getChild(0).getText();
        if (name.endsWith("(")) {
            name = name.substring(0, name.length() - 1);
        }
        polyad.setName(name);
        polyad.setOperatorType(state.getFunctionType(name));
        ParseTree kids = ctx.getChild(1); //
        for (int i = 0; i < kids.getChildCount(); i++) {
            ParseTree kid = kids.getChild(i);
            if (!kid.getText().equals(",")) {

                // add it.
                //        dyad.setLeftArgument((ExpressionNode) resolveChild(parseTree.getChild(0)));
                polyad.getArgumments().add((ExpressionNode) resolveChild(kid));
            }
        }
    }

    @Override
    public void enterVariables(QDLParserParser.VariablesContext ctx) {

    }

    @Override
    public void exitVariables(QDLParserParser.VariablesContext ctx) {

    }

    @Override
    public void enterFunctions(QDLParserParser.FunctionsContext ctx) {

    }

    @Override
    public void exitFunctions(QDLParserParser.FunctionsContext ctx) {
    }

    @Override
    public void enterPrefix(QDLParserParser.PrefixContext ctx) {
        // really hit or miss if the parser invokes this, so everything is in the exit method.
    }

    @Override
    public void exitPrefix(QDLParserParser.PrefixContext ctx) {
        Monad monad = new Monad(false);
        stash(ctx, monad);

        // in the context, the operator is the 0th child since it is prefixed.
        monad.setOperatorType(state.getOperatorType(ctx.getChild(0).getText()));
        monad.setSourceCode(ctx.getText());
        finish(monad, ctx);
    }

    @Override
    public void enterNumber(QDLParserParser.NumberContext ctx) {

    }

    @Override
    public void exitNumber(QDLParserParser.NumberContext ctx) {
        ConstantNode constantNode;
        if (ctx.getText().contains(".")) {
            BigDecimal decimal = new BigDecimal(ctx.getText());
            constantNode = new ConstantNode(decimal, Constant.DECIMAL_TYPE);
        } else {
            Long value = Long.parseLong(ctx.getChild(0).getText());
            constantNode = new ConstantNode(value, Constant.LONG_TYPE);
        }
        stash(ctx, constantNode);

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
        //stash(ctx, new Monad(opEvaluator, OpEvaluator.NOT_VALUE, false));// automatically prefix
    }

    @Override
    public void exitNotExpression(QDLParserParser.NotExpressionContext ctx) {
        // For some weird reason, entering the NOT expression does not always happen, but exiting it does.
        stash(ctx, new Monad(OpEvaluator.NOT_VALUE, false));// automatically prefix
        Monad monad = (Monad) parsingMap.getStatementFromContext(ctx);
        finish(monad, ctx);
    }

    @Override
    public void enterLeftBracket(QDLParserParser.LeftBracketContext ctx) {

    }

    @Override
    public void exitLeftBracket(QDLParserParser.LeftBracketContext ctx) {

    }


    @Override
    public void enterLogical(QDLParserParser.LogicalContext ctx) {

    }

    @Override
    public void exitLogical(QDLParserParser.LogicalContext ctx) {

    }

    @Override
    public void enterOrExpression(QDLParserParser.OrExpressionContext ctx) {
        stash(ctx, new Dyad(OpEvaluator.OR_VALUE));
    }

    protected void finish(Dyad dyad, ParseTree parseTree) {
        dyad.setLeftArgument((ExpressionNode) resolveChild(parseTree.getChild(0)));
        dyad.setRightArgument((ExpressionNode) resolveChild(parseTree.getChild(2)));
        dyad.setSourceCode(parseTree.getText());

    }

    protected void finish(Monad monad, ParseTree parseTree) {
        int index = monad.isPostFix() ? 0 : 1; // post fix means 0th is the arg, prefix means 1 is the arg.
        monad.setArgument((ExpressionNode) resolveChild(parseTree.getChild(index)));
        monad.setSourceCode(parseTree.getText());

    }

    @Override
    public void exitOrExpression(QDLParserParser.OrExpressionContext ctx) {
        Dyad orStmt = (Dyad) parsingMap.getStatementFromContext(ctx);
        finish(orStmt, ctx);
    }

    @Override
    public void enterUnaryMinusExpression(QDLParserParser.UnaryMinusExpressionContext ctx) {

    }

    @Override
    public void exitUnaryMinusExpression(QDLParserParser.UnaryMinusExpressionContext ctx) {

    }

    @Override
    public void enterEqExpression(QDLParserParser.EqExpressionContext ctx) {
        stash(ctx, new Dyad(OpEvaluator.EQUALS_VALUE));
    }

    @Override
    public void exitEqExpression(QDLParserParser.EqExpressionContext ctx) {
        Dyad dyad = (Dyad) parsingMap.getStatementFromContext(ctx);
        finish(dyad, ctx);
    }

    @Override
    public void enterAndExpression(QDLParserParser.AndExpressionContext ctx) {
        stash(ctx, new Dyad(OpEvaluator.AND_VALUE));
    }

    @Override
    public void exitAndExpression(QDLParserParser.AndExpressionContext ctx) {
        Dyad andStmt = (Dyad) parsingMap.getStatementFromContext(ctx);
        finish(andStmt, ctx);
    }

    @Override
    public void enterMultiplyExpression(QDLParserParser.MultiplyExpressionContext ctx) {

    }

    @Override
    public void exitMultiplyExpression(QDLParserParser.MultiplyExpressionContext ctx) {
        Dyad dyad;
        if (ctx.Times() != null) {
            dyad = new Dyad(OpEvaluator.TIMES_VALUE);
        } else {
            dyad = new Dyad(OpEvaluator.DIVIDE_VALUE);
        }
        stash(ctx, dyad);
        finish(dyad, ctx);
    }

    @Override
    public void enterNull(QDLParserParser.NullContext ctx) {

    }

    @Override
    public void exitNull(QDLParserParser.NullContext ctx) {

    }

    @Override
    public void enterStrings(QDLParserParser.StringsContext ctx) {
        // This is called when there is just a string.
    }

    @Override
    public void exitStrings(QDLParserParser.StringsContext ctx) {
        String value = ctx.getChild(0).getText().trim();
        // IF the parser is on the ball, then this is enclosed in single quotes, so we string them
        if (value.startsWith("'")) {
            value = value.substring(1);
        }
        if (value.endsWith("'")) {
            value = value.substring(0, value.length() - 1);
        }
        ConstantNode node = new ConstantNode(value, Constant.STRING_TYPE);
        node.setSourceCode(ctx.getText());
        stash(ctx, node);
        // that's it. set the value and we're done.
    }

    @Override
    public void enterAddExpression(QDLParserParser.AddExpressionContext ctx) {

    }

    @Override
    public void exitAddExpression(QDLParserParser.AddExpressionContext ctx) {
        Dyad dyad;

        // Dyad dyad = (Dyad) parsingMap.getStatementFromContext(ctx);
        if (ctx.Minus() != null) {
            dyad = new Dyad(OpEvaluator.MINUS_VALUE);
        } else {
            dyad = new Dyad(OpEvaluator.PLUS_VALUE);
        }
        stash(ctx, dyad);
        finish(dyad, ctx);
    }

    @Override
    public void enterCompExpression(QDLParserParser.CompExpressionContext ctx) {
    }

    @Override
    public void exitCompExpression(QDLParserParser.CompExpressionContext ctx) {
        // only now can we determine the comparison type
        stash(ctx, new Dyad(state.getOperatorType(ctx.getChild(1).getText())));
        Dyad dyad = (Dyad) parsingMap.getStatementFromContext(ctx);
        finish(dyad, ctx);
    }

    @Override
    public void enterPostfix(QDLParserParser.PostfixContext ctx) {
        stash(ctx, new Monad(true));
    }

    @Override
    public void exitPostfix(QDLParserParser.PostfixContext ctx) {
        Monad monad = (Monad) parsingMap.getStatementFromContext(ctx);
        // in the context, the operator is the 1st (vs. 0th!) child since it is postfixed.
        monad.setOperatorType(state.getOperatorType(ctx.getChild(1).getText()));
        finish(monad, ctx);
    }

    @Override
    public void enterSemi_for_empty_expressions(QDLParserParser.Semi_for_empty_expressionsContext ctx) {

    }

    @Override
    public void exitSemi_for_empty_expressions(QDLParserParser.Semi_for_empty_expressionsContext ctx) {

    }

    @Override
    public void enterElement(QDLParserParser.ElementContext ctx) {
        stash(ctx, new Element());
    }

    @Override
    public void exitElement(QDLParserParser.ElementContext ctx) {
        // Find the correct statement and put it in the element
        StatementRecord statementRecord = (StatementRecord) parsingMap.findFirstChild(ctx, true);
        if (statementRecord == null) {
            return;
        }
        String id = IDUtils.createIdentifier(ctx);
        ElementRecord elementRecord = (ElementRecord) parsingMap.get(id);
        elementRecord.getElement().setStatement(statementRecord.statement);
    }

    @Override
    public void enterStatement(QDLParserParser.StatementContext ctx) {

    }

    @Override
    public void exitStatement(QDLParserParser.StatementContext ctx) {

    }

    @Override
    public void enterIfStatement(QDLParserParser.IfStatementContext ctx) {
        stash(ctx, new ConditionalStatement());
    }

    protected void doConditional(ParseTree ctx) {
        ConditionalStatement conditionalStatement = (ConditionalStatement) parsingMap.getStatementFromContext(ctx);
        //#0 is if[ // #1 is conditional, #2 is ]then[. #3 starts the statements
        conditionalStatement.setConditional((ExpressionNode) resolveChild(ctx.getChild(1)));
        conditionalStatement.setSourceCode(ctx.getText());
        boolean addToIf = true;
        try {
            for (int i = 3; i < ctx.getChildCount(); i++) {
                ParseTree p = ctx.getChild(i);
                if (p.getText().equals("]else[")) {
                    addToIf = false;
                    continue;
                }
                if (p.getText().equals(";") || p.getText().equals("]")) {
                    continue;
                }
                if (addToIf) {
                    Statement s = resolveChild(p);
                    conditionalStatement.getIfArguments().add(s);
                } else {
                    conditionalStatement.getElseArguments().add(resolveChild(p));
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    @Override
    public void exitIfStatement(QDLParserParser.IfStatementContext ctx) {
        doConditional(ctx);
    }

    @Override
    public void enterIfElseStatement(QDLParserParser.IfElseStatementContext ctx) {
        stash(ctx, new ConditionalStatement());

    }

    @Override
    public void exitIfElseStatement(QDLParserParser.IfElseStatementContext ctx) {
        doConditional(ctx);
    }

    @Override
    public void enterConditionalStatement(QDLParserParser.ConditionalStatementContext ctx) {

    }

    @Override
    public void exitConditionalStatement(QDLParserParser.ConditionalStatementContext ctx) {

    }

    @Override
    public void enterLoopStatement(QDLParserParser.LoopStatementContext ctx) {
        stash(ctx, new WhileLoop());
    }

    @Override
    public void exitLoopStatement(QDLParserParser.LoopStatementContext ctx) {
        doLoop(ctx);
    }

    protected void doLoop(QDLParserParser.LoopStatementContext ctx) {
        // single line loop tests
        //a:=3; while[ a < 6 ]do[ q:= 6; say(q + a++); ]; // prints 9 10 11
        //a:=3; while[ a < 6 ]do[ q:= 6; say(q+a); ++a;]; // prints 9 10 11
        //a:=8; while[ a > 6 ]do[ q:= 6; say(q+a); a--;]; // prints 14 13
        //a:=8; while[ a > 6 ]do[ q:= 6; say(q + --a); ]; // prints 13 12
        WhileLoop whileLoop = (WhileLoop) parsingMap.getStatementFromContext(ctx);
        whileLoop.setConditional((ExpressionNode) resolveChild(ctx.expression()));
        for (QDLParserParser.StatementContext stmt : ctx.statement()) {
            whileLoop.getStatements().add(resolveChild(stmt));
        }
        whileLoop.setSourceCode(ctx.getText());
    }

    @Override
    public void enterSwitchStatement(QDLParserParser.SwitchStatementContext ctx) {

    }

    @Override
    public void exitSwitchStatement(QDLParserParser.SwitchStatementContext ctx) {

    }

    @Override
    public void enterDefineStatement(QDLParserParser.DefineStatementContext ctx) {
        parsingMap.startMark();
    }

    protected void doDefine2(QDLParserParser.DefineStatementContext defineContext) {
        FunctionRecord functionRecord = new FunctionRecord();
        functionRecord.sourceCode = defineContext.getText();
        if (!functionRecord.sourceCode.endsWith(";")) {
            functionRecord.sourceCode = functionRecord.sourceCode + ";";
        }
        QDLParserParser.FunctionContext nameAndArgsNode = defineContext.function();
        String name = nameAndArgsNode.getChild(0).getText();
        if (name.endsWith("(")) {
            name = name.substring(0, name.length() - 1);
        }
        functionRecord.name = name;
        for (QDLParserParser.ArgListContext argListContext : nameAndArgsNode.argList()) {
            functionRecord.argNames.add(argListContext.getText());
        }
        for (QDLParserParser.StatementContext sc : defineContext.statement()) {
            functionRecord.statements.add(resolveChild(sc));
        }
        state.getFunctionTable().put(functionRecord);
        parsingMap.rollback();
        parsingMap.endMark();
        parsingMap.clearMark();

    }


    @Override
    public void exitDefineStatement(QDLParserParser.DefineStatementContext ctx) {
        doDefine2(ctx);
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

    @Override
    public void enterModuleStatement(QDLParserParser.ModuleStatementContext ctx) {
        ModuleStatement module = new ModuleStatement();
        stash(ctx, module);
        // keep the top level module but don't let the system try to turn anything else in to statements.
        parsingMap.startMark();
    }

    protected String stripSingleQuotes(String rawString) {
        String out = rawString.trim();
        if (out.startsWith("'")) {
            out = out.substring(1);
        }
        if (out.endsWith("'")) {
            out = out.substring(0, out.length() - 1);
        }
        return out;
    }

    @Override
    public void exitModuleStatement(QDLParserParser.ModuleStatementContext moduleContext) {
        // module['uri:/foo','bar']body[ say( 'hi');]; // single line test
        ModuleStatement module = (ModuleStatement) parsingMap.getStatementFromContext(moduleContext);
        URI namespace = URI.create(stripSingleQuotes(moduleContext.STRING(0).toString()));
        String alias = stripSingleQuotes(moduleContext.STRING(1).toString());
        module.setNamespace(namespace);
        module.setAlias(alias);
        for (QDLParserParser.StatementContext stmt : moduleContext.statement()) {
            module.getStatements().add(resolveChild(stmt));
        }
       module.setSourceCode(moduleContext.getText());

        parsingMap.endMark();
        parsingMap.rollback();
        parsingMap.clearMark();

    }

    @Override
    public void enterTryCatchStatement(QDLParserParser.TryCatchStatementContext ctx) {
        TryCatch tryCatch = new TryCatch();
        stash(ctx, tryCatch);
    }

    @Override
    public void exitTryCatchStatement(QDLParserParser.TryCatchStatementContext tcContext) {
        TryCatch tryCatch = (TryCatch) parsingMap.getStatementFromContext(tcContext);
         tryCatch.setSourceCode(tcContext.getText());
        boolean addToTry = true;
        try {
            for (int i = 1; i < tcContext.getChildCount(); i++) {
                ParseTree p = tcContext.getChild(i);
                if (p.getText().equals("]catch[")) {
                    addToTry = false;
                    continue;
                }
                if (p.getText().equals(";") || p.getText().equals("]")) {
                    continue;
                }
                if (addToTry) {
                    Statement s = resolveChild(p);
                    tryCatch.getTryStatements().add(s);
                } else {
                    tryCatch.getCatchStatements().add(resolveChild(p));
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

}
