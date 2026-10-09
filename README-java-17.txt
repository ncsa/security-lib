This branch ports the project(s) to Java 17 from Java 11. Important points are

* The older NCSA Suecrity Library uses Java 8 classes to do a lot of key and
  certificate operations. These are legacy and work great. There is no need to
  update or change them. THe only way to do that is to quit using Java classes
  and switch to something like Bouncy Castle. (See below).

* The target for this is 17, but it has been tested with Java 25 and works
  fine. The stumblionb block is that CILogon (major user of OA4MP) can
  reasonably support. The language level, however, is Java 11 and does
  not need to change.

* To compile, swtich to the correct version of Java on your machine and
  update JAVA_HOME to point to it. Maven (and other parts of the build)
  will use this and you may get quite odd failures if it is not set.

* If you are using the major build-all.sh script, then you should clean the
  target directory before running it. This is typically called temp-deploy.
  This way if something fails you don't have an older version of some component
  that is a silent failure someplace.