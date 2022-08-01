# This is a script for unit testing.
# Note that the key and value pair below are in the unit test and will be checked for.
echo('Hello world');
setEnv('key0','value0');
echo('The environment has key0=${key0}');
# Next line evaluates to true and creates an exception. Normally this would simply abort the
# script but since this is the last thing in the script, it just terminates it a wee bit earlier.
# You should see a stack trace with the argument as the message.
echo('You should see a stack trace next...');
if[contains('f','foo')]then[raiseError('this is an exception')];