export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

NCSA_ROOT=$NCSA_DEV_ROOT/security-lib

cd $NCSA_ROOT
echo 'Changing to ' $NCSA_ROOT

mvn clean install

if [[ $? -ne 0 ]] ; then
    echo "NCSA maven build failed, exiting..."
    exit 1
fi

# javadoc and site package will fail since Java 8's sun packages are not allowed
# for export. This can be fixed with compiler options in the surefire and compile
# plugins, but it's a mess and will most likely break somewhere after Java 11.
# Read this
# https://stackoverflow.com/questions/42538750/unable-to-export-a-package-from-java-base-module