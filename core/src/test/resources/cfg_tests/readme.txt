Test for new configuration framework code, which eschews Apache code and is just plain old XML.
The reason for this is that the old Apache code is no longer maintained and does
not use XML, it has its own strucutre (so that in theory we could have had other
configuration formats than XML). In reality, we only use XML and a fair bit of the
code is navigating their layer for no good reason.

These tests are for XML configurations and test file includes, alias resolution
getting attributes etc. None of it is rocket science, it just needs to work
in all cases flawlessly, so regression testing is in order.