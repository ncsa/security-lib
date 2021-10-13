package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.ini_generated.iniListener;
import edu.uiuc.ncsa.qdl.ini_generated.iniParser;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigDecimal;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/11/21 at  8:15 AM
 */
public class IniListenerImpl implements iniListener {
    public StemVariable getOutput() {
        return output;
    }

    public void setOutput(StemVariable output) {
        this.output = output;
    }

    StemVariable output = null;

    public IniListenerImpl(StemVariable output) {
        this.output = output;
    }

    StemVariable currentStem;

    @Override
    public void enterIni(iniParser.IniContext ctx) {

    }

    @Override
    public void exitIni(iniParser.IniContext ctx) {

    }

    @Override
    public void enterSection(iniParser.SectionContext ctx) {
        currentStem = new StemVariable();
    }

    @Override
    public void exitSection(iniParser.SectionContext ctx) {

        currentStem = null; // so we clean up
    }

    String currentSectionHeader;

    @Override
    public void enterSectionheader(iniParser.SectionheaderContext ctx) {

    }

    @Override
    public void exitSectionheader(iniParser.SectionheaderContext ctx) {
        currentSectionHeader = ctx.Identifier().getText();
        output.put(currentSectionHeader, currentStem);
    }

    String currentLineID = null;

    @Override
    public void enterLine(iniParser.LineContext ctx) {
    }

    @Override
    public void exitLine(iniParser.LineContext ctx) {
        if(ctx.Identifier() == null){
            return; // means there was a blank line
        }
        currentLineID = ctx.Identifier().getText(); // don't know if this is scalar or stem at this point
        currentStem.put(currentLineID, currentLineValue);
    }

    @Override
    public void enterEntries(iniParser.EntriesContext ctx) {
    }
    Object currentLineValue;
    @Override
    public void exitEntries(iniParser.EntriesContext ctx) {
        int entryCount = ctx.children.size();
        if(entryCount == 1){
            currentLineValue = convertEntryToValue(ctx.entry(0));
            return;
        }
        StemVariable stemList = new StemVariable();
        for (int i = 0; i < entryCount; i++) {
            if(ctx.entry(i)==null){
                continue;
            }
            Object obj = convertEntryToValue(ctx.entry(i));
            stemList.put(i, obj);
        }
        currentLineValue = stemList;

    }

    protected Object convertEntryToValue(iniParser.EntryContext entryContext) {
        if (entryContext.String() != null) {
            String outString = entryContext.String().getText().trim();
            // returned text will have the '' included, so string them off
            if(outString.startsWith("'") && outString.endsWith("'")){
                outString = outString.substring(1, outString.length()-1);
            }
            return outString;
        }
        if (entryContext.ConstantKeywords() != null) {
            System.out.println(entryContext.ConstantKeywords().getClass());
            if (entryContext.ConstantKeywords().getText().equals("true")) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }
        if (entryContext.Number() != null) {
            try {
             return   new BigDecimal(entryContext.Number().getText());
            } catch (Throwable t) {

            }
            return new Long(entryContext.Number().getText());
        }
        throw new IllegalArgumentException("unkown type");
    }

    @Override
    public void enterEntry(iniParser.EntryContext ctx) {

    }

    @Override
    public void exitEntry(iniParser.EntryContext ctx) {

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
