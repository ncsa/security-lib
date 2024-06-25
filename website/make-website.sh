#
# Run this AFTER build.sh or it will fail.
#
NCSA_ROOT=$NCSA_DEV_INPUT/security-lib
# Output of everything goes to WEBSITE_ROOT
WEBSITE_ROOT=$NCSA_ROOT/docs
cd $WEBSITE_ROOT/pdf

echo "converting docs to PDF"

lowriter --headless --convert-to pdf $NCSA_ROOT/sas-server/src/main/docs/SAS-protocol.odt
lowriter --headless --convert-to pdf $NCSA_ROOT/storage/src/main/docs/upkeep.odt
lowriter --headless --convert-to pdf $NCSA_ROOT/web-installer/src/main/docs/installer.odt
echo "done converting PDFs"



cd $NCSA_ROOT
mvn clean javadoc:aggregate
cd $NCSA_ROOT/website
mvn clean site
# Note the source directory in the next command has no apidocs subdirectory, so this overlays
# without overwriting.
cp -r $NCSA_ROOT/website/target/site/* $WEBSITE_ROOT # copy maven site
cp -r $NCSA_ROOT/target/site/* $WEBSITE_ROOT   # copy javadoc in toto
