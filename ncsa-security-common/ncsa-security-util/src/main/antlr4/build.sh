# Build this for maven, since the plugin is really hinky.
# Run it from the command line
# Make sure you "reload from disk" in the generated directory!!
ANTLR4_ROOT=/home/ncsa/apps/java/antlr-4.7.2
ANTLR4_CP=$ANTLR4_ROOT/antlr-4.7.2-complete.jar
OUT_DIR="/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-util/src/main/java/edu/uiuc/ncsa/security/util/qdl/generated"
SOURCE_DIR="/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-util/src/main/antlr4"
OUT_PACKAGE="edu.uiuc.ncsa.security.util.qdl.generated"
cd $SOURCE_DIR
# Stupidly, the antlr tool only build from the current directory.
# There is no option to set the source directory
echo "switching to " $SOURCE_DIR
echo "putting files in "$OUT_DIR
antlr4 QDLParser.g4 -o $OUT_DIR -package $OUT_PACKAGE
cd $OUT_DIR
#rm *.java
#rm *.class
javac -cp $ANTLR4_CP: QDL*.java
