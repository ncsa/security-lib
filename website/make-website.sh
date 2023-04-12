#
# Run this AFTER build.sh or it will fail.
#
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
NCSA_ROOT=/home/ncsa/dev/ncsa-git/security-lib
# Output of everything goes to WEBSITE_ROOT
WEBSITE_ROOT=$NCSA_ROOT/docs


cd $NCSA_ROOT
mvn clean javadoc:aggregate
cd $NCSA_ROOT/website
mvn clean site
# Note the source directory in the next command has no apidocs subdirectory, so this overlays
# without overwriting.
cp -r $NCSA_ROOT/website/target/site/* $WEBSITE_ROOT # copy maven site
cp -r $NCSA_ROOT/target/site/* $WEBSITE_ROOT   # copy javadoc in toto
