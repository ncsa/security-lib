package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.generated.QDLParserBaseListener;
import edu.uiuc.ncsa.qdl.generated.QDLParserParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashMap;
// These are examples to feed to the parser for debugging expressions. They are here for me for quick reference.
// foo:=((a&&b)||((f(b)||d)&&!a.2)); // with a function
// foo:=((a&&b)||((b||d)&&    !a.2));  // without a function
// asd.qwe:='hi' + (1234 < (a||b));
// if[ a<=b]then[xxx:='the quick' + 'brown';];

/**
 * This should be used for debugging in that it does nothing in the way of producing usable objects,
 * it simply prints out the various states and relations during parsing. This lets you see how to implement
 * object creation.
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/20 at  11:50 AM
 */
public class QDLDebugListener extends QDLParserBaseListener {

    HashMap<String, String> fakeStack = new HashMap<>();
    int i = 0;

    protected int incr() {
        return i++;
    }

    protected int decr() {
        return --i;
    }

    protected String enter(String component) {
        return "id:/" + component + "/" + incr();
    }

    protected String xit(String component) {
        return "id:/" + component + "/" + decr();
    }

    @Override
    public void enterSemi_for_empty_expressions(QDLParserParser.Semi_for_empty_expressionsContext ctx) {
        say("enterSemi", ctx);

    }

    @Override
    public void exitSemi_for_empty_expressions(QDLParserParser.Semi_for_empty_expressionsContext ctx) {
        say("exitSemi", ctx);
    }

    String indent = "  ";

    protected void sayii(String x) {
        sayi(indent + x);
    }

    protected void sayi(String x) {
        say(indent + x);
    }

    protected void say(String x) {
        System.out.println("QDLDebugListener:" + x);
    }

    protected void printKids(ParserRuleContext ctx) {
        sayii("this <" + ctx.getText() + ">=[" + Integer.toHexString(ctx.hashCode()) + "]");
        sayii("parent = [" + Integer.toHexString(ctx.getParent().hashCode()) + "]");
        sayii("child count = " + ctx.getChildCount());
        if (ctx.children != null) {
            for (ParseTree parseTree : ctx.children) {
                sayii("child <" + parseTree.getText() + ">=[" + Integer.toHexString(parseTree.hashCode()) +
                        "], its parent=[" + Integer.toHexString(parseTree.getParent().hashCode()) + "]");
            }
        }
        sayii("-------------");

    }

    protected String glomParseTree(ParserRuleContext ctx) {
        // take parse tree, reconstruct the text in it.
        String out = "[" + Integer.toHexString(ctx.hashCode()) + "]:";
        if (ctx.children != null) {
            for (ParseTree o : ctx.children) {
                out = out + o.getText();
            }
        }
        return out;
    }

    protected void printContext(ParserRuleContext ctx) {
        sayii("child count=" + ctx.getChildCount());
        for (int i = 0; i < ctx.getChildCount(); i++) {
            sayii("text = " + ctx.getChild(i).getText());
            sayii("payload = " + ctx.getChild(i).getPayload());
            sayii("to string = " + ctx.getChild(i).toStringTree());
        }
    }

    protected void sayii(String x, ParserRuleContext ctx) {
        sayi(indent + x, ctx);
    }

    protected void sayi(String x, ParserRuleContext ctx) {
        say(indent + x, ctx);
    }

    protected void say(String x, ParserRuleContext ctx) {
        say(x + ", content= \"" + glomParseTree(ctx) + "\"");
    }
   /* @Override
    public void enterElements(QDLParserParser.ElementsContext ctx) {

    }

    @Override
    public void exitElements(QDLParserParser.ElementsContext ctx) {
            say("exitElements", ctx);
    }*/



    @Override
    public void enterAssignment(QDLParserParser.AssignmentContext ctx) {
        say("enter assignment:" + enter("assignment"), ctx);
        printKids(ctx);
    }


    @Override
    public void exitAssignment(QDLParserParser.AssignmentContext ctx) {
        sayi("exit assignment:" + xit("assignment"), ctx);
        sayii("op=" + ctx.op);
        sayii("[" + Integer.toHexString(ctx.hashCode()) + "]");
        sayii("expr=" + ctx.expression());
        sayii("variable=" + ctx.variable());
        sayii("parent id [" + Integer.toHexString(ctx.getParent().hashCode()) + "]");
        for (ParseTree parseTree : ctx.children) {
            sayii("child =[" + Integer.toHexString(parseTree.hashCode()) +
                    "], its parent=[" + Integer.toHexString(parseTree.getParent().hashCode()) + "]");
        }
    }


    @Override
    public void enterArgList(QDLParserParser.ArgListContext ctx) {
        say("enter arg list:" + enter("argList"));
        printKids(ctx);

    }

    @Override
    public void exitArgList(QDLParserParser.ArgListContext ctx) {
        sayi("exit arg list:" + xit("argList"), ctx);
        printKids(ctx);
    }

    @Override
    public void enterFunction(QDLParserParser.FunctionContext ctx) {
        say("enter function:" + enter("function"));
        printKids(ctx);
    }

    @Override
    public void enterLambdaStatement(QDLParserParser.LambdaStatementContext ctx) {
        say("enter lamnda stme:" + enter("lambda"));
        printKids(ctx);
    }

    @Override
    public void exitLambdaStatement(QDLParserParser.LambdaStatementContext ctx) {
        sayi("exit lambda:" + xit("lambda"), ctx);
        printKids(ctx);
    }

    @Override
    public void exitFunction(QDLParserParser.FunctionContext ctx) {

        sayi("exit Function:" + xit("function"), ctx);
        printKids(ctx);

    }

    @Override
    public void enterVariables(QDLParserParser.VariablesContext ctx) {
        say("enter variables");
        printKids(ctx);
    }

    @Override
    public void exitVariables(QDLParserParser.VariablesContext ctx) {

        sayi("exitVariables", ctx);
        sayi("variable =" + ctx.variable());
        sayi("text =" + ctx.getText());
    }

    @Override
    public void enterFunctions(QDLParserParser.FunctionsContext ctx) {
        say("enter functionS");
        printKids(ctx);
    }

    @Override
    public void exitFunctions(QDLParserParser.FunctionsContext ctx) {
        say("exitFunctions", ctx);
        printKids(ctx);

    }

    @Override
    public void enterPrefix(QDLParserParser.PrefixContext ctx) {
        say("enter prefix");
        printKids(ctx);
    }

    @Override
    public void exitPrefix(QDLParserParser.PrefixContext ctx) {
        sayi("exit prefix", ctx);
        sayii("prefix = " + ctx.prefix);

    }

    @Override
    public void enterNumbers(QDLParserParser.NumbersContext ctx) {
        say("enter numbers");
        printKids(ctx);
    }

    @Override
    public void exitNumbers(QDLParserParser.NumbersContext ctx) {
        sayi("exit numbers", ctx);
        sayii("number=" + ctx.number());

    }



    @Override
    public void enterAssociation(QDLParserParser.AssociationContext ctx) {
        say("enter association");
        printKids(ctx);
    }

    @Override
    public void exitAssociation(QDLParserParser.AssociationContext ctx) {
        sayi("exit assoc", ctx);
        sayi("expr=" + ctx.expression());

    }

    @Override
    public void enterNotExpression(QDLParserParser.NotExpressionContext ctx) {
        say("enter Not expression:" + enter("not"));
        printKids(ctx);
    }

    @Override
    public void exitNotExpression(QDLParserParser.NotExpressionContext ctx) {
        sayi("exit Not Expression:" + xit("not"), ctx);
        sayii("expr+" + ctx.expression());

    }

    @Override
    public void enterLeftBracket(QDLParserParser.LeftBracketContext ctx) {
        say("enter left bracket");
    }

    @Override
    public void exitLeftBracket(QDLParserParser.LeftBracketContext ctx) {
        sayi("exit left bracket ", ctx);

    }

    @Override
    public void enterVariable(QDLParserParser.VariableContext ctx) {
        say("enter variables");
        printKids(ctx);
    }

    @Override
    public void exitVariable(QDLParserParser.VariableContext ctx) {
        sayi("exit variables ", ctx);

    }



    @Override
    public void enterLogical(QDLParserParser.LogicalContext ctx) {
        say("enter logical:" + enter("logical"));
        printKids(ctx);
    }

    @Override
    public void exitLogical(QDLParserParser.LogicalContext ctx) {
        sayi("exit Logical:" + xit("logical"), ctx);

    }

    @Override
    public void enterOrExpression(QDLParserParser.OrExpressionContext ctx) {
        String id = enter("or");
        say("enter OR Expression:" + id);
        fakeStack.put(id, null);
        printKids(ctx);

    }

    @Override
    public void exitOrExpression(QDLParserParser.OrExpressionContext ctx) {
        String id = xit("or");
        fakeStack.put(id, ctx.Or().toString());
        sayi("exit OR Expression:" + id, ctx);

    }

    @Override
    public void enterUnaryMinusExpression(QDLParserParser.UnaryMinusExpressionContext ctx) {
        say("enter unaryMinusExpression:" + enter("uminus"));
        printKids(ctx);
    }

    @Override
    public void exitUnaryMinusExpression(QDLParserParser.UnaryMinusExpressionContext ctx) {
        sayi("exit unaryMinusExpression:" + xit("uminus"), ctx);
        sayii("Minus=" + ctx.Minus());

    }

    @Override
    public void enterEqExpression(QDLParserParser.EqExpressionContext ctx) {
        say("enter eq expression:" + enter("equals"));
        printKids(ctx);
    }

    @Override
    public void exitEqExpression(QDLParserParser.EqExpressionContext ctx) {
        sayi("exit EqExpression:" + xit("equals"), ctx);
    }

    @Override
    public void enterAndExpression(QDLParserParser.AndExpressionContext ctx) {
        String id = enter("and");
        say("enter AndExpression:" + id);
        printKids(ctx);

    }

    @Override
    public void exitAndExpression(QDLParserParser.AndExpressionContext ctx) {
        String id = xit("and");
        sayi("exit AndExpression:" + id, ctx);
        fakeStack.put(id, ctx.And().toString());
    }

    @Override
    public void enterNull(QDLParserParser.NullContext ctx) {
        say("enter null");
        printKids(ctx);

    }

    @Override
    public void exitNull(QDLParserParser.NullContext ctx) {
        sayi("exit null ", ctx);

    }

    @Override
    public void enterStrings(QDLParserParser.StringsContext ctx) {
        say("enter strings");
        printKids(ctx);

    }

    @Override
    public void exitStrings(QDLParserParser.StringsContext ctx) {
        sayi("exitStrings", ctx);
    }

    @Override
    public void enterAddExpression(QDLParserParser.AddExpressionContext ctx) {
        say("  enter add expression:" + enter("add"));
        printKids(ctx);

    }

    @Override
    public void exitAddExpression(QDLParserParser.AddExpressionContext ctx) {
        sayi("exit Add Expression:" + xit("add"), ctx);
        sayii("op=" + ctx.op);
        sayii("Minus=" + ctx.Minus());
        sayii("Plus=" + ctx.Plus());
        for (QDLParserParser.ExpressionContext q : ctx.expression()) {
            sayii("expr=" + q.getText());

        }

    }

    @Override
    public void enterCompExpression(QDLParserParser.CompExpressionContext ctx) {
        say("enter comp expression:" + enter("compare"));
        printKids(ctx);

    }

    @Override
    public void exitCompExpression(QDLParserParser.CompExpressionContext ctx) {
        sayi("exit compExpression:" + xit("compare"), ctx);

    }

    @Override
    public void enterPostfix(QDLParserParser.PostfixContext ctx) {
        say("enter postfix:" + enter("postfix"));
        printKids(ctx);

    }

    @Override
    public void exitPostfix(QDLParserParser.PostfixContext ctx) {
        sayi("exit postfix:" + xit("postfix"), ctx);
        sayii("postfix = " + ctx.postfix);

    }


    @Override
    public void enterStatement(QDLParserParser.StatementContext ctx) {
        say("enter statment:" + enter("statement"));
        printKids(ctx);

    }

    @Override
    public void exitStatement(QDLParserParser.StatementContext ctx) {
        sayi("exit statement:" + xit("statement"), ctx);
        say(fakeStack.toString());
        printKids(ctx);
    }

    @Override
    public void enterIfStatement(QDLParserParser.IfStatementContext ctx) {
        say("enter if statement:" + enter("if"));
        printKids(ctx);

    }

    @Override
    public void exitIfStatement(QDLParserParser.IfStatementContext ctx) {
        sayi("exit if Statement:" + xit("if"), ctx);
        sayi("[" + Integer.toHexString(ctx.hashCode()) + "]");
        printKids(ctx);
    }

    @Override
    public void enterIfElseStatement(QDLParserParser.IfElseStatementContext ctx) {
        sayi("enter ifElse Statement:" + enter("ifElse"));
        printKids(ctx);

    }

    @Override
    public void exitIfElseStatement(QDLParserParser.IfElseStatementContext ctx) {
        say("exit ifElse Statement:" + xit("ifElse"), ctx);
        printKids(ctx);

    }

    @Override
    public void enterConditionalStatement(QDLParserParser.ConditionalStatementContext ctx) {
        say("enter Conditional:" + enter("conditional"), ctx);
        printKids(ctx);

    }

    @Override
    public void exitConditionalStatement(QDLParserParser.ConditionalStatementContext ctx) {
        say("exitConditional:" + xit("conditional"), ctx);
        printKids(ctx);


    }

    @Override
    public void enterLoopStatement(QDLParserParser.LoopStatementContext ctx) {
        say("enter loop statment:" + enter("loop"));
        printKids(ctx);
    }

    @Override
    public void exitLoopStatement(QDLParserParser.LoopStatementContext ctx) {
        say("exit LoopStatement ", ctx);
         printKids(ctx);
    }

    @Override
    public void enterSwitchStatement(QDLParserParser.SwitchStatementContext ctx) {
        say("enter switch statement:" + enter("switch"));
        printKids(ctx);

    }

    @Override
    public void exitSwitchStatement(QDLParserParser.SwitchStatementContext ctx) {
        say("exitSwitch", ctx);

    }

    @Override
    public void enterDefineStatement(QDLParserParser.DefineStatementContext ctx) {
        say("enter define statement:" + enter("define"));
        printKids(ctx);
    }

    @Override
    public void exitDefineStatement(QDLParserParser.DefineStatementContext ctx) {
        say("exitDefine", ctx);
        printKids(ctx);

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
        say("visiting error node text=" + errorNode.getText());
        say("visiting error node symbol=" + errorNode.getSymbol());
    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }
/*
    @Override
      public void enterDotOp(QDLParserParser.DotOpContext ctx) {
          System.out.println("enter dot op");
        printKids(ctx);
      }

      @Override
      public void exitDotOp(QDLParserParser.DotOpContext ctx) {
          System.out.println("exit dot op");
          printKids(ctx);
      }
      */

}
