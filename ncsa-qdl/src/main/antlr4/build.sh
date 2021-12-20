# Build this for maven, since the plugin is really hinky.
# Run it from the command line
# Make sure you "reload from disk" in the generated directory!!
# THEN rebuild the *entire project.
#ANTLR4_ROOT=/home/ncsa/apps/java/antlr-4.9.1
#ANTLR4_CP=$ANTLR4_ROOT/antlr-4.9.1-complete.jar
ANTLR4_ROOT=/home/ncsa/apps/java/antlr-4.9.3
ANTLR4_CP=$ANTLR4_ROOT/antlr-4.9.3-complete.jar
OUT_DIR="/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/java/edu/uiuc/ncsa/qdl/generated"
SOURCE_DIR="/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/antlr4"
OUT_PACKAGE="edu.uiuc.ncsa.qdl.generated"
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
