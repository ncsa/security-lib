#
# Run this AFTER build.sh or it will fail.
#
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
NCSA_ROOT=/home/ncsa/dev/ncsa-git/security-lib
WEBSITE_ROOT=$NCSA_ROOT/website


cd $NCSA_ROOT/language
mvn javadoc:javadoc
cp -r $NCSA_ROOT/language/target/site/apidocs/* $GITHUB_ROOT/apidocs
cd $NCSA_ROOT/website
mvn clean site
# Note the source directory in the next command has no apidocs subdirectory, so this overlays
# without overwriting.
cp -r $NCSA_ROOT/website/target/site/* $WEBSITE_ROOT
