# File to show the result of parsing. invoke the build file in this directory first
# then invoke this with the name to a file to test parse.
ANTLR4_ROOT=/home/ncsa/apps/java/antlr-4.9.1
OUT_DIR="/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/java/edu/uiuc/ncsa/qdl/generated"
ANTLR4_CP=.:$ANTLR4_ROOT/antlr-4.9.1-complete.jar:$OUT_DIR

SCRIPT_DIR="/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/antlr4"
echo "reading command file from "  $SCRIPT_DIR
# And another thing... in a split grammar, the test rig will resolve the name of the grammar from the
# stem. Antlr is actually picky about this, so in a combined grammar you would invoke QDLInterpreter here
# you just invoke QDL and it looks for QDLInterpreter.class. Not well documented in the AAntkr reference man, FYI...
echo "switching to directory " $OUT_DIR
cd $OUT_DIR
echo "running file " $SCRIPT_DIR$1
java -cp $ANTLR4_CP: org.antlr.v4.gui.TestRig $OUT_DIR/QDLParser elements -diagnostics -gui $SCRIPT_DIR$1 -v
