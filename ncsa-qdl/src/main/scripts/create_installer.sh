# This will create the installer (an executable jar file).
# Be sure you have run the build scripts first so there is stuff to copy.
QDL_ROOT="/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl"
# /src/main/java/edu/uiuc/ncsa/qdl"
TARGET_ROOT="/home/ncsa/dev/temp-deploy/qdl"

echo "cleaning out old deploy in " $DEPLOY_ROOT
if [ ! -d "$TARGET_ROOT" ]; then
    mkdir "$TARGET_ROOT"
fi
cd $TARGET_ROOT
rm -Rf *

mkdir edu
mkdir edu/uiuc
mkdir edu/uiuc/ncsa
mkdir edu/uiuc/ncsa/qdl
mkdir edu/uiuc/ncsa/qdl/install


cd $TARGET_ROOT
cp /home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/scripts/installer.mf .
cp /home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/scripts/version.txt .
cp /home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/target/classes/edu/uiuc/ncsa/qdl/install/Installer.class edu/uiuc/ncsa/qdl/install

# Now make the directories
mkdir "bin"
cp /home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/scripts/qdl bin
mkdir "docs"
cp /home/ncsa/dev/ncsa-git/cilogon.github.io.git/qdl/docs/*.pdf docs
mkdir "etc"
cp /home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/resources/min-cfg.xml etc/min-cfg.xml
mkdir "lib"
cp "$QDL_ROOT/target/qdl.jar" lib
mkdir "log"
mkdir "lib/cp"
mkdir "var"
mkdir "var/ws"
# jar cmf manifest-file jar-file input-files
jar cmf installer.mf install.jar edu/uiuc/ncsa/qdl/install/Installer.class "version.txt" bin docs etc lib log var
