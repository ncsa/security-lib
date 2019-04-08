setEnv('vo','voPersonExternalID');
setEnv('github','http://github.com/login/oauth/authorize');
setEnv('google','http://google.com/accounts/o8/id');
setEnv('orcid','http://orcid.org/oauth/authorize');
# Here is a comment

echo('${orcid}');

if[
    endsWith(getEnv('orcid'),'ize')
   ]then[
     echo('got one')
   ]else[
     echo('newp')
];

