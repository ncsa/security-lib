This is the notes for running an SAS server in the development environment.

The server configuration resides at

/home/ncsa/dev/csd/config/sas/sat.xml

The filestore of clients resides at

/home/ncsa/apps/qdl/var/storage/clients

(should be elsewhere.)

How to run it
=============
 * Build with packaging of war in the sas-server/pom.xml:
    <packaging>war</packaging>

    NOTE that this should normally be commented out (so the default of jar is used).
    Otherwise it cannot be a dependency of other projects. Only set it to war
    for testing locally and reverse that when deploying.

 * Deploy into tomcat with the web.xml with the following parameters

    <context-param>
        <param-name>sas:server.config.file</param-name>
        <param-value>/home/ncsa/dev/csd/config/sas/sat.xml</param-value>
    </context-param>

    <context-param>
        <param-name>sas:server.config.name</param-name>
        <param-value>default</param-value>
    </context-param>

NOTE that this is probably done on my local tomcat install.

 * Start Tomcat.
   Note that this does not have a real landing page, it being a service, so you should get
   a mostly blank page with the single word "Yo!".

 * (In Intellij -- easiest) run  the web client with parameters
    -cfg /home/ncsa/dev/csd/config/sas/sas-config.xml
 * this should connect to the server and just work. Remember that the endpoint (assuming
   the war is named sas-server) is sas-server/sas.

 * If this works, then you should get a prompt like
 sas>

 * Issue /logon (this should just log you on) then the default service just runs the EchoExecutable.
   Any thing you type at the prompt is echoed back with its wrapper, e.g.

 sas>/logon
 login complete
 sas>hi
 test: execute(hi)
 sas>foo
 test: execute(foo)  