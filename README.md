[![Javadocs](https://www.javadoc.io/badge/edu.uiuc.ncsa.security/ncsa-security-core.svg)](https://www.javadoc.io/doc/edu.uiuc.ncsa.security/ncsa-security-core)
  
# Building the library
                              
## Required software

To build the NCSA security library you need

* Java 11
* Maven 3.9.1 or above
* The ability to run bash scripts. Linux works and there are ports of bash to other platfroms as well.
* The version of the code you want to build checked out from git. Check this out, from https://github.com/ncsa/security-lib and clone it or however you want to get it to NCSA_DEV_INPUT.,

__Note:__ You must set the environment variable for NCSA_DEV_INPUT, as well as JAVA_HOME. 

## Creating the library (rolling a snapshot)

There is a single file named build.sh. Typically you set a couple of environment variables
and invoke this script. You need to set  the environment  variable NCSA_DEV_INPUT as per
above. The script will then run and your local maven repository will be populated
with the current version.

## Output

The result is a set of jars in your maven repository. These will be local to your
system but will be used i building subsequent components (such as OA4MP, QDL, CILogon).

## Uploading a snapshot to Sonatype

You may also create the distro and upload it to Sonatype. Make sure you have permissions
to do so. This consists of changing the maven target of the build file to deploy, so 
`mvn clean install` would be replaced with `mvn clean deploy`. The resulting snapshot
should be findable under https://oss.sonatype.org/#view-repositories;snapshots~browsestorage
by navigating to edu > uiuc > ncsa > security
                         
# Rolling a release

The critical part is to replace the SNAPSHOT tag (e.g. 5.3-SNAPSHOT)
globally with your preferred version. Note that this tag will also be found in
java files (so the system is aware of the current release version), so this
change does not merely affect the pom.xml files.

## Local usage

If you are not planning on uploading it to Sonatype, simply run the build file with 
the default maven target and your local repository will be updated. All references to this
version (e.g. when building OA4MP) will use your version. 

## Uploading to Sonatype

Again, change the target of the maven build  from `install` to `deploy` and then
got to https://oss.sonatype.org/index.html#welcome, login and close the release. 
Note that you will have to be able to log in to Sonatype with correct access
to do this step. 

# Creating the website

If you want to update the website, you must be able to commit to Git. 
You would

* run build.sh 
* invoke  website/make-website.sh
* commit the resulting directory $NCSA_DEV_INPUT/security-lib/docs to Git.
* switch the branch for [Git Pages](https://github.com/ncsa/security-lib/settings/pages) over to the version you want to use.

