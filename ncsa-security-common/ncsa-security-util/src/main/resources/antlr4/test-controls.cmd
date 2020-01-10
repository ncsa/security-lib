if[ abc <= 42
  ]then[
    asd.qwe:=f(ddd);
    f(42,32,'asd');
  ];

if[
    ((i<foo) && size(array)<=4) || (i < size(foo))
   ]then[
    foo := foo + i;
   ]else[
    foo := foo -i;
  ];

// Next test looping construction.
i:=0;
foo:=5;
while[
    i < foo
   ]do[
    out:='the index is [' + i + ']';
    echo(out);
    i++;
  ];


define[
    f(A,B,C)
  ]body[
    A.0:=B;if[C==2]then[C++;];
 ];

switch[
   if[a<b]then[c:=2;];
   if[a==b]then[c:=3;];
  ];