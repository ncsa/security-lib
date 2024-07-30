[![Javadocs](https://www.javadoc.io/badge/edu.uiuc.ncsa.security/ncsa-security-core.svg)](https://www.javadoc.io/doc/edu.uiuc.ncsa.security/ncsa-security-core)
 
# Using the NCSA Security Libraries

The easiest way to incorporate these into your project is using Maven. There are
several libraries, e.g. This is how the storage library would be referenced
```
<dependency>
    <groupId>edu.uiuc.ncsa.security</groupId>
    <artifactId>storage</artifactId>
    <version>5.6</version>
    <scope>compile</scope>
</dependency>
```
 
# Building the libraries
                              
## Required software

To build the NCSA security library you need

* Java 11 JDK (must have javadoc command available)
* Maven 3.6 or above
* The ability to run bash scripts. Linux works and there are ports of bash to other platfroms as well.
* The version of the code you want to build checked out from git. Check this out, from https://github.com/ncsa/security-lib and clone it or however you want to get it to NCSA_DEV_INPUT.,

## Required environment variables

* NCSA_DEV_INPUT - location of root. 
* NCSA_DEV_OUTPUT  - location of created artifacts
* NCSA_CONFIG_ROOT - location of the configuration files
* JAVA_HOME - location of JDK.  

# Getting the sources

These are available from the [NCSA Security Library](https://github.com/ncsa/security-lib.git) project on GitHub.
You would clone this to `$NCSA_DEV_INPUT` which would result in `$NCSA_DEV_INPUT/security-lib`. This
is the root for this project.

## Creating the library (local release)

There is a single file named build.sh in `$NCSA_DEV_INPUT/security-lib`. 
Typically you set the  environment variables as per above
and invoke this script.  The script will then run and your local maven 
repository will be populated with the current version.

## Output

The result is a set of jars in your local maven repository. These will be local to your
system but will be used i building subsequent components (such as OA4MP, QDL, CILogon). This
project does not currently use `$NCSA_DEV_OUTPUT`.

                         
# Rolling a release

The critical part is to replace the SNAPSHOT tag (e.g. 5.6)
globally with your preferred version. Note that this tag will also be found in
java files (so the system is aware of the current release version), so this
change does not merely affect the pom.xml files. You should also be sure before
doing a deploy to Sonatype that you have administrator privileges and have uploaded
your signing keys. 

__Note__ There may be issues with the website module. I suggest you comment that out
of `$NCSA_DEV_INPUT/security-lib/pom.xml` and sidestep having to tweak more of the 
configuration. You should also ensure that in the pom.xml file, that signing with GPG keys is
set since Sonatype will refuse to validate the release without keys.

You would cd to

`$NCSA_DEV_INPUT/security-lib`

and issue

`mvn clean deploy`

If successful, you should then be able to go to the [Nexus Repository Manager](https://oss.sonatype.org/index.html#welcome)
and close the upload. 


# Creating the website

If you want to update the website, you must be able to commit to Git. 
You would (make sure that the website module is uncommented in `$NCSA_DEV_INPUT/security-lib/pom.xml`)

* run build.sh 
* invoke  website/make-website.sh
* commit the resulting directory $NCSA_DEV_INPUT/security-lib/docs to Git.
* switch the branch for [Git Pages](https://github.com/ncsa/security-lib/settings/pages) over to the version you want to use.

