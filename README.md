[![Javadocs](https://www.javadoc.io/badge/edu.uiuc.ncsa.security/ncsa-security-core.svg)](https://www.javadoc.io/doc/edu.uiuc.ncsa.security/ncsa-security-core)
  
# Building the library
                              
## Required software

To build the NCSA security library you need

* Java 11
* Maven 3.9.1 or above
* The ability to run bas scripts. Linux works and there are ports of bash to other platfroms as well.
* The version of the code you want to build checked out from git. Check this out, from https://github.com/ncsa/security-lib and clone it or however you want to get it to NCSA_DEV_ROOT.,

__Note:__ You must set the environment variable for NCSA_DEV_ROOT. 
## Creating the library

There is a single file named build.sh. Typically you set a couple of environment variables
and invoke this script. You need to set  the environment  variable NCSA_DEV_ROOT to  
 

## Creating the website

If you want to update the website, you would create the library, then invoke
the script in website/make-website.sh

To build the library, check it out to a location. Set this in the
build.sh file, in the SVN_ROOT variable,  along with pointing to your 
Java distro in JAVA_HOME. These are the only two things you need
to set. 

Note that Java
should be Java 11 (recommended) or perhaps up to Java 15. Higher or lower will not work.