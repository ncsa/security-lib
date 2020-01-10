/*
  Tests for the QDL2Parser (comments, variables and constants with assignments). Just parse it.

*/


qrs.tuv  :=
          12345;


qrs.tuv:=-2468; // check stem + key works.
z := 0;
abc   :=
                       54321;
c.0     :=    4; // check stem + index works.
qqq:='foo{}[]()';
www:=zxc.vbn;
zzz.xxx:=true;
rrr:=f('a',abc.def);
ttt:=f(g(123));
