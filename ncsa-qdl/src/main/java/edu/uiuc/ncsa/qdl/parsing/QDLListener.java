package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.evaluate.ControlEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.AssignmentException;
import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.expressions.*;
import edu.uiuc.ncsa.qdl.functions.FunctionDefinitionStatement;
import edu.uiuc.ncsa.qdl.functions.FunctionRecord;
import edu.uiuc.ncsa.qdl.functions.FunctionReferenceNode;
import edu.uiuc.ncsa.qdl.generated.QDLParserListener;
import edu.uiuc.ncsa.qdl.generated.QDLParserParser;
import edu.uiuc.ncsa.qdl.module.QDLModule;
import edu.uiuc.ncsa.qdl.state.QDLConstants;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.*;
import edu.uiuc.ncsa.qdl.variables.*;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.apache.commons.lang.StringEscapeUtils;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
        //       StatementRecord p = (StatementRecord) parsingMap.get(IDUtils.createIdentifier(ctx));

    }

    @Override
    public void exitVariable(QDLParserParser.VariableContext ctx) {
        StatementRecord p = (StatementRecord) parsingMap.get(IDUtils.createIdentifier(ctx));
        if (ctx.getText().equals(QDLConstants.RESERVED_TRUE) ||
                ctx.getText().equals(QDLConstants.RESERVED_TRUE2) ||
                ctx.getText().equals(QDLConstants.RESERVED_FALSE) ||
                ctx.getText().equals(QDLConstants.RESERVED_FALSE2)) {
            // SPECIAL CASE. The parse recognizes true and false, but does not know what to do with them.
            // We are here because it lumps them together with the variable values.
            ConstantNode cnode = new ConstantNode(new Boolean(ctx.getText().equals(QDLConstants.RESERVED_TRUE)), Constant.BOOLEAN_TYPE);
            p.statement = cnode;
            cnode.setSourceCode(getSource(ctx));
            return;
        }
        if (ctx.getText().equals(QDLConstants.RESERVED_NULL) || ctx.getText().equals(QDLConstants.RESERVED_NULL2)) {
            p.statement = QDLNull.getInstance();
            return;
        }
        ((VariableNode) parsingMap.getStatementFromContext(ctx)).setVariableReference(ctx.getText());

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
    public void exitAssignment(QDLParserParser.AssignmentContext assignmentContext) {
        Assignment currentA = (Assignment) parsingMap.getStatementFromContext(assignmentContext);
        Assignment topNode = currentA;
        topNode.setSourceCode(getSource(assignmentContext));
        Assignment nextA = null;
        // The variable is the 0th child, the argument is the last child (it is an expression and ends up in the symbol table).
        int exprIndex = assignmentContext.children.size() - 1;
        //     boolean isStem = false;
        for (int i = 0; i < exprIndex; i++) {
            boolean isVariableCase = true;
            String currentVar = null;
            if (assignmentContext.children.get(i) instanceof QDLParserParser.KeywordsContext) {
                throw new IllegalArgumentException("error: cannot assign constant a value");

            }
            if (assignmentContext.children.get(i) instanceof QDLParserParser.VariablesContext) {
                currentVar = assignmentContext.children.get(i).getText();
                currentA.setVariableReference(currentVar);
            } else {
                Statement s = resolveChild(assignmentContext.children.get(i));
                if (!(s instanceof StatementWithResultInterface)) {
                    throw new IllegalArgumentException("error: illegal assignment expression ");
                }
                currentA.setExpStatement((StatementWithResultInterface) s);
                isVariableCase = false;
            }
            // The general pattern (i.e.  the children of assignmentContext) is
            //   V0 op0 V1 op1 V2 op2 V3 op3 Expr
            // (The final argument must always be an expression). So this gets
            //   V0 op0 V1
            //  and increments the loop once, so the next iteration it gets
            //   V1 op1 V2
            // In this way it has enough information at each iteration to make the
            // right type of assignment operator.
            String op = assignmentContext.children.get(++i).getText(); // this is the operator
            String nextVar;
            if (currentA.getSourceCode() == null) {
                List<String> source = new ArrayList<>();
                if (isVariableCase) {
                    source.add(currentVar + op + assignmentContext.children.get(i + 1).getText());
                } else {
                    source.add(currentA.getExpStatement().getSourceCode() + op + assignmentContext.children.get(i + 1).getText());
                }
                currentA.setSourceCode(source);
            }
            nextA = new Assignment();
            if (assignmentContext.children.size() == i + 1) {
                // no next element, implies the user sent something like a :=; so no rhs, OR the
                // rhs was so munged the parser can't figure out what it is.
                // Throw an  exception they can understand rather than an index out of bounds.
                throw new AssignmentException("missing/unparseable right-hand expression for assignment");
            }

            if (assignmentContext.children.get(i + 1) instanceof QDLParserParser.VariablesContext) {
                nextVar = assignmentContext.children.get(i + 1).getText();
                nextA.setVariableReference(nextVar);
            } else {
                Statement s = resolveChild(assignmentContext.children.get(i + 1));
                if (!(s instanceof StatementWithResultInterface)) {
                    throw new IllegalArgumentException("error: illegal assignment expression ");
                }
                nextA.setExpStatement((StatementWithResultInterface) s);
                // isVariableCase = false;
            }
//            nextVar = assignmentContext.children.get(i + 1).getText();
            //          nextA.setVariableReference(nextVar);
            // Special cases. := means assignment, if not then, then it is one
            // of +=, *=,..., so create the appropriate dyad
            if (op.equals(":=") || op.equals("≔") | op.equals("=:") | op.equals("≕")) {   // Allows unicode 2254, 2255 as assignment too.
                if (op.equals("=:") | op.equals("≕")) {
                    // This is the only place for sure we know what the operator is.
                    currentA.setFlippedAssignment(true);
                }

                if (i + 1 == exprIndex) {
                    Statement arg = resolveChild(assignmentContext.children.get(i + 1));
                    currentA.setArgument(arg);
                    return;
                    // test for this. q and A should have the value 'abc', B == 'bc'
                    //  A := 'a'; B := 'b';
                    // q := A += B += 'c';
                }
                currentA.setArgument(nextA);
            } else {
                Dyad d = new Dyad(getAssignmentType(op));
                d.setLeftArgument(new VariableNode(currentVar));
                d.setRightArgument(new AssignmentNode(nextA));
                currentA.setArgument(d);
            }
            if (i + 1 == exprIndex) {
                Statement arg = resolveChild(assignmentContext.children.get(i + 1));
                // If this is not here, you will create a variable with the name of the argument,
                // e.g. 'qqq'
                if (isVariableCase) {
                    nextA.setVariableReference(currentA.getVariableReference());
                } else {
                    nextA.setExpStatement(currentA.getExpStatement());
                }
                nextA.setArgument(arg);
                return;
                //  A := 'a'; B := 'b';
                // q := A += B += 'c';
            } else {
                // Chain them together if there are more to come
            }
            currentA = nextA;
            //          i = nextIndex - 1; // back up cursor so on increment in loop it points to right place
        } //end for
    }

    protected int getAssignmentType(String op) {
        switch (op) {
            case "+=":
                return OpEvaluator.PLUS_VALUE;
            case "-=":
                return OpEvaluator.MINUS_VALUE;
            case "×=":
            case "*=":
                return OpEvaluator.TIMES_VALUE;
            case "÷=":
            case "/=":
                return OpEvaluator.DIVIDE_VALUE;
            case "%=":
                return OpEvaluator.INTEGER_DIVIDE_VALUE;
            case "^=":
                return OpEvaluator.POWER_VALUE;
        }
        return OpEvaluator.UNKNOWN_VALUE;
    }

/*    @Override
    public void enterArgList(QDLParserParser*//**//*.ArgListContext ctx) {

    }

    @Override
    public void exitArgList(QDLParserParser.ArgListContext ctx) {

    }*/

    @Override
    public void enterFunction(QDLParserParser.FunctionContext ctx) {
        stash(ctx, new Polyad());
    }

    @Override
    public void exitFunction(QDLParserParser.FunctionContext ctx) {
        // Note that this resolves **all** functions, specifically the built in ones.
        Polyad polyad = (Polyad) parsingMap.getStatementFromContext(ctx);
        polyad.setSourceCode(getSource(ctx));
        // need to process its argument list.
        // There are 3 children  "f(" arglist ")", so we want the middle one.

        String name = ctx.getChild(0).getText();
        if (name.endsWith("(")) {
            name = name.substring(0, name.length() - 1);
        }
        polyad.setName(name);
        // Generally built-in functions are resolved by their name. This next line figures out
        // if the given function is not a built-in and flags it. If this is not set
        // then no user-defined functions are going to be available.
        polyad.setBuiltIn(state.getMetaEvaluator().isBuiltInFunction(name));
        ParseTree kids = ctx.getChild(1); //
        for (int i = 0; i < kids.getChildCount(); i++) {
            ParseTree kid = kids.getChild(i);
            if (!kid.getText().equals(",")) {

                // add it.
                //        dyad.setLeftArgument((ExpressionNode) resolveChild(parseTree.getChild(0)));
                StatementWithResultInterface swri = (StatementWithResultInterface) resolveChild(kid);
                polyad.getArguments().add(swri);
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
        List<String> source = new ArrayList<>();
        source.add(ctx.getText());
        monad.setSourceCode(source);
        finish(monad, ctx);
    }

    @Override
    public void enterNumber(QDLParserParser.NumberContext ctx) {

    }

    @Override
    public void exitNumber(QDLParserParser.NumberContext ctx) {
        BigDecimal decimal = new BigDecimal(ctx.getText());
        ConstantNode constantNode = new ConstantNode(decimal, Constant.DECIMAL_TYPE);
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
    }

    @Override
    public void exitNotExpression(QDLParserParser.NotExpressionContext ctx) {
        // For some weird reason, entering the NOT expression does not always happen, but exiting it does.
        stash(ctx, new Monad(OpEvaluator.NOT_VALUE, false));// automatically prefix
        Monad monad = (Monad) parsingMap.getStatementFromContext(ctx);
        finish(monad, ctx);
    }
/*
    @Override
    public void enterLeftBracket(QDLParserParser.LeftBracketContext ctx) {

    }

    @Override
    public void exitLeftBracket(QDLParserParser.LeftBracketContext ctx) {

    }*/


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
        dyad.setLeftArgument((StatementWithResultInterface) resolveChild(parseTree.getChild(0)));
        dyad.setRightArgument((StatementWithResultInterface) resolveChild(parseTree.getChild(2)));
        List<String> source = new ArrayList<>();
        source.add(parseTree.getText());
        dyad.setSourceCode(source);

    }

    protected void finish(Monad monad, ParseTree parseTree) {
        int index = monad.isPostFix() ? 0 : 1; // post fix means 0th is the arg, prefix means 1 is the arg.
        monad.setArgument((StatementWithResultInterface) resolveChild(parseTree.getChild(index)));
        List<String> source = new ArrayList<>();
        source.add(parseTree.getText());
        monad.setSourceCode(source);
    }

    @Override
    public void exitOrExpression(QDLParserParser.OrExpressionContext ctx) {
        Dyad orStmt = (Dyad) parsingMap.getStatementFromContext(ctx);
        finish(orStmt, ctx);
    }

    boolean isUnaryMinus = false;

    @Override
    public void enterUnaryMinusExpression(QDLParserParser.UnaryMinusExpressionContext ctx) {
        // ARGH as per antlr4 spec, this need not be called, and whether it is varies by compilation.
        // Do not use this. Put everything in the exit statement.
    }

    @Override
    public void exitUnaryMinusExpression(QDLParserParser.UnaryMinusExpressionContext ctx) {
        Monad monad = new Monad(ctx.children.get(0).getText().equals(OpEvaluator.MINUS) ? OpEvaluator.MINUS_VALUE : OpEvaluator.PLUS_VALUE, false);
        stash(ctx, monad);
        finish(monad, ctx);
    }

    @Override
    public void enterEqExpression(QDLParserParser.EqExpressionContext ctx) {
        stash(ctx, new Dyad(OpEvaluator.UNKNOWN_VALUE));
    }

    @Override
    public void exitEqExpression(QDLParserParser.EqExpressionContext ctx) {
        Dyad dyad = (Dyad) parsingMap.getStatementFromContext(ctx);
        dyad.setOperatorType(state.getOperatorType(ctx.op.getText()));
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
    public void enterPowerExpression(QDLParserParser.PowerExpressionContext ctx) {

    }

    @Override
    public void exitPowerExpression(QDLParserParser.PowerExpressionContext ctx) {
        Dyad dyad = new Dyad(OpEvaluator.POWER_VALUE);
        stash(ctx, dyad);
        finish(dyad, ctx);
    }

    @Override
    public void enterMultiplyExpression(QDLParserParser.MultiplyExpressionContext ctx) {

    }

    @Override
    public void exitMultiplyExpression(QDLParserParser.MultiplyExpressionContext ctx) {
        Dyad dyad = new Dyad(state.getOperatorType(ctx.op.getText()));
        stash(ctx, dyad);
        finish(dyad, ctx);
    }

    @Override
    public void enterNull(QDLParserParser.NullContext ctx) {
    }

    @Override
    public void exitNull(QDLParserParser.NullContext ctx) {
        ConstantNode constantNode = new ConstantNode(QDLNull.getInstance(), Constant.NULL_TYPE);
        stash(ctx, constantNode);
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
        value = value.replace("\\'", "'");
        value = StringEscapeUtils.unescapeJava(value);
/*
        value = value.replace("\\n", "\n");
        value = value.replace("\\t", "\t");
        value = value.replace("\\r", "\r");
        value = value.replace("\\b", "\b");
        value = value.replace("\\\\", "\\");
*/
        ConstantNode node = new ConstantNode(value, Constant.STRING_TYPE);
        List<String> source = new ArrayList<>();
        source.add(ctx.getText());
        node.setSourceCode(source);
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

    protected ConditionalStatement doConditional(ParseTree ctx) {
        ConditionalStatement conditionalStatement = (ConditionalStatement) parsingMap.getStatementFromContext(ctx);
        //#0 is if[ // #1 is conditional, #2 is ]then[. #3 starts the statements
        //conditionalStatement.setConditional((ExpressionNode) resolveChild(ctx.getChild(1))); // first child is always conditional
        List<String> source = new ArrayList<>();
        source.add(ctx.getText());
        conditionalStatement.setSourceCode(source);
        boolean addToIf = true;

        ParseExpressionBlock parseExpressionBlock = (ParseExpressionBlock) parsingMap.getStatementFromContext(ctx.getChild(1));
        conditionalStatement.setConditional(parseExpressionBlock.getExpressionNodes().get(0));
        for (int i = 2; i < ctx.getChildCount(); i++) {
            ParseTree p = ctx.getChild(i);
            if (p instanceof TerminalNodeImpl) {
                continue; // skip tokens like [, then, else
            }
            ParseStatementBlock parseStatementBlock = (ParseStatementBlock) parsingMap.getStatementFromContext(p);

            if (addToIf) {
                conditionalStatement.setIfArguments(parseStatementBlock.getStatements());
                addToIf = false;
            } else {
                conditionalStatement.setElseArguments(parseStatementBlock.getStatements());
            }
        }
        return conditionalStatement;
    }

    protected ConditionalStatement doConditionalOLD(ParseTree ctx) {
        ConditionalStatement conditionalStatement = (ConditionalStatement) parsingMap.getStatementFromContext(ctx);
        //#0 is if[ // #1 is conditional, #2 is ]then[. #3 starts the statements
        conditionalStatement.setConditional((ExpressionNode) resolveChild(ctx.getChild(1)));
        List<String> source = new ArrayList<>();
        source.add(ctx.getText());
        conditionalStatement.setSourceCode(source);
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

        return conditionalStatement;
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
        QDLParserParser.ConditionalBlockContext conditionalBlockContext = ctx.conditionalBlock();
        QDLParserParser.StatementBlockContext statementBlockContext = ctx.statementBlock();
        WhileLoop whileLoop = (WhileLoop) parsingMap.getStatementFromContext(ctx);
        whileLoop.setConditional((ExpressionNode) resolveChild(conditionalBlockContext.expression()));
        for (QDLParserParser.StatementContext stmt : statementBlockContext.statement()) {
            whileLoop.getStatements().add(resolveChild(stmt));
        }
        whileLoop.setSourceCode(getSource(ctx));
    }

    @Override
    public void enterSwitchStatement(QDLParserParser.SwitchStatementContext ctx) {
        stash(ctx, new SwitchStatement());
        parsingMap.startMark();
    }

    // )load switch_test.qdl
    @Override
    public void exitSwitchStatement(QDLParserParser.SwitchStatementContext ctx) {
        SwitchStatement switchStatement = (SwitchStatement) parsingMap.getStatementFromContext(ctx);
        switchStatement.setSourceCode(getSource(ctx));
        for (QDLParserParser.IfStatementContext ifc : ctx.ifStatement()) {
            ConditionalStatement cs = (ConditionalStatement) parsingMap.getStatementFromContext(ifc);
            switchStatement.getArguments().add(cs);
        }
        parsingMap.endMark();
        parsingMap.rollback();
        parsingMap.clearMark();
    }

    @Override
    public void enterFdoc(QDLParserParser.FdocContext ctx) {
    }

    @Override
    public void exitFdoc(QDLParserParser.FdocContext ctx) {
        // we don't do anything with this element, but since we are implementing the interface,
        // have to put it here. The fdoc elements are handled in the function definition
        // code.
    }

    @Override
    public void enterDefineStatement(QDLParserParser.DefineStatementContext ctx) {
        //     parsingMap.startMark();
        FunctionDefinitionStatement fds = new FunctionDefinitionStatement();
        fds.setLambda(false);
        stash(ctx, fds);
    }

    protected void doDefine2(QDLParserParser.DefineStatementContext defineContext) {
        FunctionRecord functionRecord = new FunctionRecord();
        FunctionDefinitionStatement fds = (FunctionDefinitionStatement) parsingMap.getStatementFromContext(defineContext);
        fds.setFunctionRecord(functionRecord);
        //FunctionDefinitionStatement fds =
        // not quite the original source... The issue is that this comes parsed and stripped of any original
        // end of line markers, so we cannot tell what was there. Since it may include documentation lines
        // we have to add back in EOLs at the end of every statement so the comments don't get lost.
        // Best we can do with ANTLR...

        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < defineContext.getChildCount(); i++) {
            stringList.add((i == 0 ? "" : "\n") + defineContext.getChild(i).getText());
        }
        // ANTLR may strip final terminator. Put it back as needed.
        if (!stringList.get(stringList.size() - 1).endsWith(";")) {
            stringList.set(stringList.size() - 1, stringList.get(stringList.size() - 1) + ";");
        }
        functionRecord.sourceCode = stringList;

        QDLParserParser.FunctionContext nameAndArgsNode = defineContext.function();
        String name = nameAndArgsNode.getChild(0).getText();
        if (name.endsWith("(")) {
            name = name.substring(0, name.length() - 1);
        }
        functionRecord.name = name;
        for (QDLParserParser.F_argsContext argListContext : nameAndArgsNode.f_args()) {
            // this is a comma delimited list of arguments.
            String allArgs = argListContext.getText();

            StringTokenizer st = new StringTokenizer(allArgs, ",");
            while (st.hasMoreElements()) {
                functionRecord.argNames.add(st.nextToken());
            }
        }
        QDLParserParser.DocStatementBlockContext docStatementBlockContext = defineContext.docStatementBlock();
        for (QDLParserParser.FdocContext fd : docStatementBlockContext.fdoc()) {
            String doc = fd.getText();
            // strip off function comment marker
            if (doc.startsWith(">>")) {
                doc = doc.substring(2).trim();
            }
            functionRecord.documentation.add(doc);
        }
        for (QDLParserParser.StatementContext sc : docStatementBlockContext.statement()) {
            functionRecord.statements.add(resolveChild(sc));
        }
    }


    @Override
    public void exitDefineStatement(QDLParserParser.DefineStatementContext ctx) {
        doDefine2(ctx);
    }


    @Override
    public void enterLambdaStatement(QDLParserParser.LambdaStatementContext ctx) {
        // don't clear the functions added already.
        // Special case since f(x) -> ... means f is added before we get here and won't get picked
        // up. That means that the system will think f needs to be evaluated asap and you will
        // get errors.
        //   parsingMap.startMark(false);
        FunctionDefinitionStatement fds = new FunctionDefinitionStatement();
        fds.setLambda(true);

        stash(ctx, fds);

    }
    //     define[g(x)][z:=x+1;return(x);]
    //     h(x) -> [z:=x+1;return(z);];

    @Override
    public void exitLambdaStatement(QDLParserParser.LambdaStatementContext lambdaContext) {
        createLambdaStatement(lambdaContext);
    }

    protected void createLambdaStatement(QDLParserParser.LambdaStatementContext lambdaContext) {
        QDLParserParser.FunctionContext nameAndArgsNode = lambdaContext.function();

        if (nameAndArgsNode == null) {
            return; // do nothing.
        }
        FunctionRecord functionRecord = new FunctionRecord();
        // not quite the original source... The issue is that this comes parsed and stripped of any original
        // end of line markers, so we cannot tell what was there. Since it may include documentation lines
        // we have to add back in EOLs at the end of every statement so the comments don't get lost.
        // Best we can do with ANTLR...

        FunctionDefinitionStatement fds = (FunctionDefinitionStatement) parsingMap.getStatementFromContext(lambdaContext);
        fds.setFunctionRecord(functionRecord);

        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < lambdaContext.getChildCount(); i++) {
            stringList.add(lambdaContext.getChild(i).getText());
        }
        // ANTLR may strip final terminator. Put it back as needed.
        if (!stringList.get(stringList.size() - 1).endsWith(";")) {
            stringList.set(stringList.size() - 1, stringList.get(stringList.size() - 1) + ";");
        }
        functionRecord.sourceCode = stringList;

        String name = nameAndArgsNode.getChild(0).getText();
        if (name.endsWith("(")) {
            name = name.substring(0, name.length() - 1);
        }
        functionRecord.name = name;
        //for (QDLParserParser.ArgListContext argListContext : nameAndArgsNode.argList()) {
        for (QDLParserParser.F_argsContext argListContext : nameAndArgsNode.f_args()) {
            // this is a comma delimited list of arguments.
            String allArgs = argListContext.getText();

            StringTokenizer st = new StringTokenizer(allArgs, ",");
            while (st.hasMoreElements()) {
                functionRecord.argNames.add(st.nextToken());
            }
        }

//       For fdoc support. Maybe enable in lambdas? Probably not, but maybe
//        for (QDLParserParser.FdocContext fd : lambdaContext.fdoc()) {
//           String doc = fd.getText();
//           // strip off function comment marker
//           if (doc.startsWith(">>")) {
//               doc = doc.substring(2).trim();
//           }
//           functionRecord.documentation.add(doc);
//       }
        boolean hasSingleArg = true;
        ParseTree p;
        if (lambdaContext.getChild(2).getChild(0) instanceof QDLParserParser.LambdaStatementContext) {
            p = lambdaContext.getChild(2).getChild(0).getChild(0);
            hasSingleArg = false;
        } else {
            p = lambdaContext.getChild(2).getChild(0);
        }
        String x = p.getChild(0).getText();

        if (!hasSingleArg) {
            // its a set of statements
            for (int i = 0; i < p.getChildCount(); i++) {
                ParseTree parserTree = p.getChild(i);
                if (parserTree.getText().equals("[") ||
                        parserTree.getText().equals(";") ||
                        parserTree.getText().equals("]")) {
                    continue;
                }
                functionRecord.statements.add(resolveChild(parserTree));

            }

        } else {
            // its a single expression most likely. Check to see if it needs wrapped in
            // a return
/*
            if(lambdaContext.statementBlock()!=null){

                p = lambdaContext.statementBlock().statement().get(0);
            }
*/
            Statement stmt = resolveChild(p);
            // Contract: Wrap simple expressions in a return.
            if (stmt instanceof StatementWithResultInterface) {
                if (stmt instanceof ExpressionImpl) {
                    ExpressionImpl expr = (ExpressionImpl) stmt;
                    if (expr.getOperatorType() != ControlEvaluator.RETURN_TYPE) {
                        Polyad expr1 = new Polyad(ControlEvaluator.RETURN_TYPE);
                        expr1.setName(ControlEvaluator.RETURN);
                        expr1.addArgument(expr);
                        functionRecord.statements.add(expr1); // wrapped in a return
                    } else {
                        functionRecord.statements.add(expr); // already has a return
                    }
                } else {
                    Polyad expr1 = new Polyad(ControlEvaluator.RETURN_TYPE);
                    expr1.setName(ControlEvaluator.RETURN);
                    expr1.addArgument((StatementWithResultInterface) stmt);
                    functionRecord.statements.add(expr1); // wrapped in a return
                }
            } else {
                functionRecord.statements.add(stmt); // a statement, not merely an expression
            }

        }
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

    boolean isModule = false;
    QDLModule currentModule = null;
    ModuleStatement moduleStatement = null;

    @Override
    public void enterModuleStatement(QDLParserParser.ModuleStatementContext ctx) {
        if (isModule) {
            throw new QDLException("Error: Modules cannot be nested");
        }
        moduleStatement = new ModuleStatement();

        stash(ctx, moduleStatement);
        // start mark after module statement is added or it gets
        // removed from the element list and never evaluates.
        parsingMap.startMark();

        isModule = true;
        State localState = state.newModuleState();
        currentModule = new QDLModule();
        currentModule.setState(localState);
        moduleStatement.setLocalState(localState);
        // keep the top. level module but don't let the system try to turn anything else in to statements.
    }

    /**
     * There are various ways to try and get the source. Most of them strip off line feeds so
     * if there is line comment (// ... ) the rest of the source turns into a comment *if*
     * the source ever gets reparsed. NOTE this is exactly what happens with module.
     * This tries to get the original source with line feeds
     * and all. It may be more fragile than the ANTLR documentation lets on, so be advised.
     * The ctx.getText() method, however, is definitely broken...
     *
     * @param ctx
     * @return
     */
    protected List<String> getSource(ParserRuleContext ctx) {
        List<String> text = new ArrayList<>();
        if (ctx.start == null || ctx.stop == null) {
            // odd ball case
            text.add("no source");
            return text;
        }
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        if (b < a) {
            text.add("no source");
            return text;
        }
        Interval interval = new Interval(a, b);
        // Next line actually gets the original source:
        String txt = ctx.start.getInputStream().getText(interval);
        if (!StringUtils.isTrivial(txt)) {
            // Sometimes the parser strips the final ;. If the source is used later
            // this causes re-parsing it to reliably bomb. Add it back as needed.
            txt = txt + (txt.endsWith(";") ? "" : ";");
        }
        StringTokenizer stringTokenizer = new StringTokenizer(txt, "\n");
        while (stringTokenizer.hasMoreTokens()) {
            text.add(stringTokenizer.nextToken());
        }
        return text;
    }

    @Override
    public void exitModuleStatement(QDLParserParser.ModuleStatementContext moduleContext) {
        // module['uri:/foo','bar']body[ say( 'hi');]; // single line test
        // )load module_example.qdl
        isModule = false;
        ModuleStatement moduleStatement = (ModuleStatement) parsingMap.getStatementFromContext(moduleContext);
        URI namespace = URI.create(stripSingleQuotes(moduleContext.STRING(0).toString()));
        String alias = stripSingleQuotes(moduleContext.STRING(1).toString());
        currentModule.setNamespace(namespace);
        currentModule.setAlias(alias);
        moduleStatement.setSourceCode(getSource(moduleContext));
        state.getModuleMap().put(namespace, currentModule);
        QDLParserParser.DocStatementBlockContext docStatementBlockContext = moduleContext.docStatementBlock();
        for (QDLParserParser.StatementContext stmt : docStatementBlockContext.statement()) {
            // Issue is that resolving children as we do gets the function definitions.
            // that are in any define contexts. So if this is a define context,
            // skip it. They are handled elsewhere and the definitions are never evaluated
            // just stashed.
/*
            boolean isSkip = false;
            for (int i = 0; i < stmt.getChildCount(); i++) {
                isSkip = isSkip || (stmt.getChild(i) instanceof QDLParserParser.DefineStatementContext);
                isSkip = isSkip || (stmt.getChild(i) instanceof QDLParserParser.LambdaStatementContext);
            }
            if (!isSkip) {
                Statement kid = resolveChild(stmt);
                moduleStatement.getStatements().add(kid);
            }
*/
            Statement kid = resolveChild(stmt);
            moduleStatement.getStatements().add(kid);

            //}
        }
        //       For fdoc support.  Probably not, but maybe
        for (QDLParserParser.FdocContext fd : docStatementBlockContext.fdoc()) {
            String doc = fd.getText();
            // strip off function comment marker
            if (doc.startsWith(">>")) {
                doc = doc.substring(2).trim();
            }
            currentModule.getDocumentation().add(doc);
        }
        // Parser strips off trailing ; which in turn causes a parser error later when we are runnign the import command.
        moduleStatement.setSourceCode(getSource(moduleContext));
        currentModule.setModuleStatement(moduleStatement);

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
        tryCatch.setSourceCode(getSource(tcContext));
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

    @Override
    public void enterStemVariable(QDLParserParser.StemVariableContext ctx) {
        StemVariableNode stemVariableNode = new StemVariableNode();
        stash(ctx, stemVariableNode);
    }

    @Override
    public void exitStemVariable(QDLParserParser.StemVariableContext ctx) {
        StemVariableNode svn = (StemVariableNode) parsingMap.getStatementFromContext(ctx);
        svn.setSourceCode(getSource(ctx));
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree p = ctx.getChild(i);
            if (p instanceof QDLParserParser.StemEntryContext) {
                StemEntryNode stmt = (StemEntryNode) resolveChild(p);
                svn.getStatements().add(stmt);
            }
        }
    }

    @Override
    public void enterStemEntry(QDLParserParser.StemEntryContext ctx) {
        StemEntryNode sen = new StemEntryNode();
        stash(ctx, sen);
    }

    @Override
    public void exitStemEntry(QDLParserParser.StemEntryContext ctx) {
        StemEntryNode svn = (StemEntryNode) parsingMap.getStatementFromContext(ctx);
        if (ctx.getChild(0).getText().equals("*")) {
            StatementWithResultInterface value = (StatementWithResultInterface) resolveChild(ctx.getChild(2));
            svn.setDefaultValue(true);
            svn.setValue(value);
            return;
        }
        StatementWithResultInterface key = (StatementWithResultInterface) resolveChild(ctx.getChild(0));
        StatementWithResultInterface value = (StatementWithResultInterface) resolveChild(ctx.getChild(2));
        svn.setKey(key);
        svn.setValue(value);


    }

    @Override
    public void enterStemList(QDLParserParser.StemListContext ctx) {
        StemListNode sln = new StemListNode();
        stash(ctx, sln);
    }

    @Override
    public void exitStemList(QDLParserParser.StemListContext ctx) {
        StemListNode sln = (StemListNode) parsingMap.getStatementFromContext(ctx);
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree pt = ctx.getChild(i);
            if (pt instanceof QDLParserParser.StemValueContext) {
                StatementWithResultInterface stmt = (StatementWithResultInterface) resolveChild(pt);
                sln.getStatements().add(stmt);
            }
        }
    }

    @Override
    public void enterStemValue(QDLParserParser.StemValueContext ctx) {

    }

    @Override
    public void exitStemValue(QDLParserParser.StemValueContext ctx) {

    }

    @Override
    public void enterStemLi(QDLParserParser.StemLiContext ctx) {
        System.out.println("entering stemli");
    }

    @Override
    public void exitStemLi(QDLParserParser.StemLiContext ctx) {

    }

    @Override
    public void enterStemVar(QDLParserParser.StemVarContext ctx) {

    }

    @Override
    public void exitStemVar(QDLParserParser.StemVarContext ctx) {

    }

    @Override
    public void enterTildeExpression(QDLParserParser.TildeExpressionContext ctx) {
    }

    @Override
    public void exitTildeExpression(QDLParserParser.TildeExpressionContext ctx) {
        String x = ctx.getChild(1).getText();
        Dyad dyad;
        if (x.equals("~|")) {
            dyad = new Dyad(OpEvaluator.TILDE_STILE_VALUE);
        } else {
            dyad = new Dyad(OpEvaluator.TILDE_VALUE);

        }
        stash(ctx, dyad);
        finish(dyad, ctx);
    }

 /*   @Override
    public void enterExpressionStem(QDLParserParser.ExpressionStemContext ctx) {
       System.out.println("enter exp stem");
    }

    @Override
    public void exitExpressionStem(QDLParserParser.ExpressionStemContext ctx) {
        System.out.println("exit exp stem");

    }

    @Override
    public void enterExpressionStems(QDLParserParser.ExpressionStemsContext ctx) {
        System.out.println("enter expressions stem");

    }

    @Override
    public void exitExpressionStems(QDLParserParser.ExpressionStemsContext ctx) {

    }*/

    @Override
    public void enterDotOp(QDLParserParser.DotOpContext ctx) {
        ExpressionStemNode expressionStemNode = new ExpressionStemNode();
        stash(ctx, expressionStemNode);

    }

    @Override
    public void exitDotOp(QDLParserParser.DotOpContext dotOpContext) {
        ExpressionStemNode expressionStemNode = (ExpressionStemNode) parsingMap.getStatementFromContext(dotOpContext);
        expressionStemNode.setSourceCode(getSource(dotOpContext));
        for (int i = 0; i < dotOpContext.getChildCount(); i++) {
            ParseTree p = dotOpContext.getChild(i);
            // If it is a termminal node (a node consisting of just be the stem marker) skip it
            if (!(p instanceof TerminalNodeImpl)) {
                expressionStemNode.getStatements().add((StatementWithResultInterface) resolveChild(p));
            }
        }

    }


    @Override
    public void enterInteger(QDLParserParser.IntegerContext ctx) {

    }

    @Override
    public void exitInteger(QDLParserParser.IntegerContext ctx) {
        Long value = Long.parseLong(ctx.getChild(0).getText());
        ConstantNode constantNode = new ConstantNode(value, Constant.LONG_TYPE);
        stash(ctx, constantNode);
    }

    @Override
    public void enterIntegers(QDLParserParser.IntegersContext ctx) {

    }

    @Override
    public void exitIntegers(QDLParserParser.IntegersContext ctx) {

    }

/*    @Override
    public void enterStem_ref(QDLParserParser.Stem_refContext ctx) {
        System.out.println("enter stem-ref");
        stash(ctx, new VariableNode(null));

    }

    @Override
    public void exitStem_ref(QDLParserParser.Stem_refContext ctx) {
        System.out.println("exit stem-ref");
        StatementRecord p = (StatementRecord) parsingMap.get(IDUtils.createIdentifier(ctx));
        ((VariableNode) parsingMap.getStatementFromContext(ctx)).setVariableReference(ctx.getText());
    }

    @Override
    public void enterStem_refs(QDLParserParser.Stem_refsContext ctx) {
        System.out.println("enter stem-refs");
    }

    @Override
    public void exitStem_refs(QDLParserParser.Stem_refsContext ctx) {
        System.out.println("exit stem_refs");
    }*/

    @Override
    public void enterF_ref(QDLParserParser.F_refContext ctx) {
        //  System.out.println("enter F_REF");
        stash(ctx, new FunctionReferenceNode());

    }

    @Override
    public void exitF_ref(QDLParserParser.F_refContext ctx) {
        //      System.out.println("exit F_REF");
        // FunctionReferenceNode frn = new FunctionReferenceNode();
        FunctionReferenceNode frn = (FunctionReferenceNode) parsingMap.getStatementFromContext(ctx);
        String name = ctx.getText();
        name = name.substring(QDLConstants.FUNCTION_REFERENCE_MARKER.length());
        //frn.setFunctionName(name); // if we allow function references to not end in ()
        int parenIndex = name.indexOf("(");
        if (-1 < parenIndex) {
            // whack off any dangling parenthese
            name = name.substring(0, name.indexOf("("));
        }
        frn.setFunctionName(name);
    }

    @Override
    public void enterF_arg(QDLParserParser.F_argContext ctx) {
        //  System.out.println("enter f_arg");

    }

    @Override
    public void exitF_arg(QDLParserParser.F_argContext ctx) {
        //  System.out.println("exit f_arg");

    }

    @Override
    public void enterF_args(QDLParserParser.F_argsContext ctx) {
        // System.out.println("enter f_argS");

    }

    @Override
    public void exitF_args(QDLParserParser.F_argsContext ctx) {
        //   System.out.println("exit f_argS");

    }

    @Override
    public void enterRegexMatches(QDLParserParser.RegexMatchesContext ctx) {
        stash(ctx, new Dyad(OpEvaluator.UNKNOWN_VALUE));

    }

    @Override
    public void exitRegexMatches(QDLParserParser.RegexMatchesContext ctx) {
        Dyad dyad = (Dyad) parsingMap.getStatementFromContext(ctx);
        dyad.setOperatorType(state.getOperatorType(ctx.op.getText()));
        finish(dyad, ctx);
    }

    @Override
    public void enterAltIFExpression(QDLParserParser.AltIFExpressionContext ctx) {
        stash(ctx, new AltIfExpressionNode());

    }

    @Override
    public void exitAltIFExpression(QDLParserParser.AltIFExpressionContext ctx) {
        AltIfExpressionNode altIfExpressionNode = (AltIfExpressionNode) parsingMap.getStatementFromContext(ctx);
        //#0 is if[ // #1 is conditional, #2 is ]then[. #3 starts the statements
        altIfExpressionNode.setIF((ExpressionNode) resolveChild(ctx.getChild(0)));
        altIfExpressionNode.setTHEN((ExpressionNode) resolveChild(ctx.getChild(2)));
        altIfExpressionNode.setELSE((ExpressionNode) resolveChild(ctx.getChild(4)));
        List<String> source = new ArrayList<>();
        source.add(ctx.getText());
        altIfExpressionNode.setSourceCode(source);
    }

    /*
    @Override

    public void enterLambdaDef(QDLParserParser.LambdaDefContext ctx) {
     //   FunctionDefinitionStatement fds = new FunctionDefinitionStatement();
     //   fds.setLambda(true);

     //   stash(ctx, fds);
    }
    */

    /*
    5/29/2021
    Almost works, but FunctionDefinitionStatement has to become a StatementWithResultInterface
    and return a function reference -- which means a new type in Constant, e.g. and dealing
    with it throughout the code.
    Straightforward but tedious and probably quite time consuming

    @Override
    public void exitLambdaDef(QDLParserParser.LambdaDefContext ctx) {
        System.out.println(ctx.function());
        System.out.println(ctx.expression());
        FunctionDefinitionStatement fds = new FunctionDefinitionStatement();
        fds.setLambda(true);

        stash(ctx, fds);
        QDLParserParser.FunctionContext nameAndArgsNode = ctx.function();

        if (nameAndArgsNode == null) {
            return; // do nothing.
        }
        FunctionRecord functionRecord = new FunctionRecord();
        // not quite the original source... The issue is that this comes parsed and stripped of any original
        // end of line markers, so we cannot tell what was there. Since it may include documentation lines
        // we have to add back in EOLs at the end of every statement so the comments don't get lost.
        // Best we can do with ANTLR...

         fds = (FunctionDefinitionStatement) parsingMap.getStatementFromContext(ctx);
        fds.setFunctionRecord(functionRecord);

        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            stringList.add(ctx.getChild(i).getText());
        }
        // ANTLR may strip final terminator. Put it back as needed.
        if (!stringList.get(stringList.size() - 1).endsWith(";")) {
            stringList.set(stringList.size() - 1, stringList.get(stringList.size() - 1) + ";");
        }
        functionRecord.sourceCode = stringList;

        String name = nameAndArgsNode.getChild(0).getText();
        if (name.endsWith("(")) {
            name = name.substring(0, name.length() - 1);
        }
        functionRecord.name = name;
        //for (QDLParserParser.ArgListContext argListContext : nameAndArgsNode.argList()) {
        for (QDLParserParser.F_argsContext argListContext : nameAndArgsNode.f_args()) {
            // this is a comma delimited list of arguments.
            String allArgs = argListContext.getText();

            StringTokenizer st = new StringTokenizer(allArgs, ",");
            while (st.hasMoreElements()) {
                functionRecord.argNames.add(st.nextToken());
            }
        }

        ParseTree p = ctx.expression();
        String x = p.getChild(0).getText();
        QDLParserParser.ExpressionContext sc = ctx.expression();
        Statement stmt = resolveChild(sc);
        // Contract: Wrap simple expressions in a return.
            if (stmt instanceof ExpressionImpl) {
                ExpressionImpl expr = (ExpressionImpl) stmt;
                if (expr.getOperatorType() != ControlEvaluator.RETURN_TYPE) {
                    Polyad expr1 = new Polyad(ControlEvaluator.RETURN_TYPE);
                    expr1.setName(ControlEvaluator.RETURN);
                    expr1.addArgument(expr);
                    functionRecord.statements.add(expr1); // wrapped in a return
                } else {
                    functionRecord.statements.add(expr); // already has a return
                }
            } else {
                Polyad expr1 = new Polyad(ControlEvaluator.RETURN_TYPE);
                expr1.setName(ControlEvaluator.RETURN);
                expr1.addArgument((StatementWithResultInterface) stmt);
                functionRecord.statements.add(expr1); // wrapped in a return
            }


    }
      */

    @Override
    public void enterExpressionBlock(QDLParserParser.ExpressionBlockContext ctx) {
        stash(ctx, new ParseExpressionBlock());

    }

    @Override
    public void exitExpressionBlock(QDLParserParser.ExpressionBlockContext ctx) {
        System.out.println("exiting expression bl");
        ParseExpressionBlock parseExpressionBlock = (ParseExpressionBlock) parsingMap.getStatementFromContext(ctx);
        // Now we fill it up with expressions
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof TerminalNodeImpl) {
                continue;
            }
            ParseTree expr = ctx.getChild(i);
            parseExpressionBlock.getExpressionNodes().add((ExpressionNode) parsingMap.getStatementFromContext(expr));
        }
    }

    @Override
    public void enterConditionalBlock(QDLParserParser.ConditionalBlockContext ctx) {
        stash(ctx, new ParseExpressionBlock());

    }

    @Override
    public void exitConditionalBlock(QDLParserParser.ConditionalBlockContext ctx) {
        ParseExpressionBlock parseExpressionBlock = (ParseExpressionBlock) parsingMap.getStatementFromContext(ctx);
        ParseTree expr = ctx.getChild(1); // only one
        parseExpressionBlock.getExpressionNodes().add((ExpressionNode) resolveChild(expr));

    }


    @Override
    public void enterRealInterval(QDLParserParser.RealIntervalContext ctx) {

    }

    @Override
    public void exitRealInterval(QDLParserParser.RealIntervalContext ctx) {

    }

    @Override
    public void enterIntInterval(QDLParserParser.IntIntervalContext ctx) {

    }

    @Override
    public void exitIntInterval(QDLParserParser.IntIntervalContext ctx) {

    }

    @Override
    public void enterStatementBlock(QDLParserParser.StatementBlockContext ctx) {
        stash(ctx, new ParseStatementBlock());
    }

    @Override
    public void exitStatementBlock(QDLParserParser.StatementBlockContext ctx) {
        ParseStatementBlock parseStatementBlock = (ParseStatementBlock) parsingMap.getStatementFromContext(ctx);
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof TerminalNodeImpl) {
                // typically these are just parse tokens like [, ;, ], comma
                continue;
            }
            // Typically this is populated with statment contexts that hold exactly what we want.
            ParseTree expr = ctx.getChild(i).getChild(0);
            parseStatementBlock.getStatements().add(resolveChild(expr));

        }
    }

    @Override
    public void enterDocStatementBlock(QDLParserParser.DocStatementBlockContext ctx) {

    }

    @Override
    public void exitDocStatementBlock(QDLParserParser.DocStatementBlockContext ctx) {

    }

    @Override
    public void enterIInterval(QDLParserParser.IIntervalContext ctx) {
        stash(ctx, new SliceNode());
    }

    @Override
    public void exitIInterval(QDLParserParser.IIntervalContext ctx) {
        SliceNode sliceNode = (SliceNode) parsingMap.getStatementFromContext(ctx);
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof TerminalNodeImpl) {
                continue;
            }
            sliceNode.getArguments().add((StatementWithResultInterface) resolveChild(ctx.getChild(i)));
        }
    }

    @Override
    public void enterRInterval(QDLParserParser.RIntervalContext ctx) {
        stash(ctx, new RealIntervalNode());
    }

    @Override
    public void exitRInterval(QDLParserParser.RIntervalContext ctx) {
        RealIntervalNode realIntervalNode = (RealIntervalNode) parsingMap.getStatementFromContext(ctx);
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof TerminalNodeImpl) {
                continue;
            }
            realIntervalNode.getArguments().add((StatementWithResultInterface) resolveChild(ctx.getChild(i)));
        }
    }

    @Override
    public void enterKeywords(QDLParserParser.KeywordsContext ctx) {
        System.out.println("entering keywordS:" + ctx.getText());
    }

    @Override
    public void exitKeywords(QDLParserParser.KeywordsContext ctx) {

    }

    @Override
    public void enterKeyword(QDLParserParser.KeywordContext ctx) {
        ConstantNode constantNode = new ConstantNode(null, Constant.STRING_TYPE);
        stash(ctx, constantNode);

    }

    @Override
    public void exitKeyword(QDLParserParser.KeywordContext ctx) {
        StatementRecord p = (StatementRecord) parsingMap.get(IDUtils.createIdentifier(ctx));
        ConstantNode constantNode = (ConstantNode) p.statement;
        switch (ctx.getText()) {
            case QDLConstants.RESERVED_FALSE:
            case QDLConstants.RESERVED_FALSE2:
                constantNode.setResult(Boolean.FALSE);
                constantNode.setResultType(Constant.BOOLEAN_TYPE);
                break;
            case QDLConstants.RESERVED_TRUE:
            case QDLConstants.RESERVED_TRUE2:
                constantNode.setResult(Boolean.TRUE);
                constantNode.setResultType(Constant.BOOLEAN_TYPE);
                break;
            case QDLConstants.RESERVED_NULL:
            case QDLConstants.RESERVED_NULL2:
                constantNode.setResult(QDLNull.getInstance());
                constantNode.setResultType(Constant.NULL_TYPE);
                break;
            default:
                throw new IllegalArgumentException("error: unknown reserved keyword constant");
        }

    }
}


