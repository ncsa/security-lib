# This is a script for testing. It has conditionals
# Note that you may have multiple commands inside a then or else block if you
# separate them with commas, not semi-colons. This is because they are treated as arguments
# to a then or else function (which has a lot of advantages practically).
echo('Hello world');
setEnv('foo','bar');
setEnv('fnord','baz');
if[
   equals('bar',getEnv('foo'))
  ]then[
    echo('foo=${foo}'),
    echo('fnord=${fnord}'),
    echo('success!')
  ]else[
     echo('fail! ${foo}')
  ];