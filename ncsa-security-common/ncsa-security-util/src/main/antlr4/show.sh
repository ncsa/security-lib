# File to show the result of parsing. invoke the build file in this directory first
# then invoke this with the name to a file to test parse.
ANTLR4_ROOT=/home/ncsa/apps/java/antlr-4.7.2
ANTLR4_CP=$ANTLR4_ROOT/antlr-4.7.2-complete.jar
OUT_DIR="/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-util/src/main/java/edu/uiuc/ncsa/security/util/qdl/generated"
SCRIPT_DIR="/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-util/src/main/resources/antlr4/"
echo "reading command file from "  $SCRIPT_DIR
# And another thing... in a split grammar, the test rig will resolve the name of the grammar from the
# stem. Antlr is actually picky about this, so in a combined grammar you would invoke QDLParser here
# you just invoke QDL and it looks for QDLParser.class. Not well documented in the AAntkr reference man, FYI...
echo "switching to directory " $OUT_DIR
cd $OUT_DIR
echo "running file " $SCRIPT_DIR$1
java -cp $ANTLR4_CP: org.antlr.v4.gui.TestRig QDLParser elements -gui $SCRIPT_DIR$1
