// Generated from ini.g4 by ANTLR 4.9.1
package edu.uiuc.ncsa.qdl.ini_generated;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link iniParser}.
 */
public interface iniListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link iniParser#ini}.
	 * @param ctx the parse tree
	 */
	void enterIni(iniParser.IniContext ctx);
	/**
	 * Exit a parse tree produced by {@link iniParser#ini}.
	 * @param ctx the parse tree
	 */
	void exitIni(iniParser.IniContext ctx);
	/**
	 * Enter a parse tree produced by {@link iniParser#section}.
	 * @param ctx the parse tree
	 */
	void enterSection(iniParser.SectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link iniParser#section}.
	 * @param ctx the parse tree
	 */
	void exitSection(iniParser.SectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link iniParser#sectionheader}.
	 * @param ctx the parse tree
	 */
	void enterSectionheader(iniParser.SectionheaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link iniParser#sectionheader}.
	 * @param ctx the parse tree
	 */
	void exitSectionheader(iniParser.SectionheaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link iniParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(iniParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link iniParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(iniParser.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link iniParser#entries}.
	 * @param ctx the parse tree
	 */
	void enterEntries(iniParser.EntriesContext ctx);
	/**
	 * Exit a parse tree produced by {@link iniParser#entries}.
	 * @param ctx the parse tree
	 */
	void exitEntries(iniParser.EntriesContext ctx);
	/**
	 * Enter a parse tree produced by {@link iniParser#entry}.
	 * @param ctx the parse tree
	 */
	void enterEntry(iniParser.EntryContext ctx);
	/**
	 * Exit a parse tree produced by {@link iniParser#entry}.
	 * @param ctx the parse tree
	 */
	void exitEntry(iniParser.EntryContext ctx);
}