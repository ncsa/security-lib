
NCSA_ROOT=$NCSA_DEV_INPUT/security-lib

cd $NCSA_ROOT || exit
echo 'building NCSA Sec-Lib'

mvn clean install > maven.log

if [[ $? -ne 0 ]] ; then
    echo "NCSA maven build failed, see $NCSA_ROOT/maven.log"
    exit 1
fi
echo '     ... done!'

# javadoc and site package will fail since Java 8's sun packages are not allowed
# for export. This can be fixed with compiler options in the surefire and compile
# plugins, but it's a mess and will most likely break somewhere after Java 11.
# Read this
# https://stackoverflow.com/questions/42538750/unable-to-export-a-package-from-java-base-module