module Tree

data Tree appl(Production prod, Args args);
data Tree cycle(Symbol symbol, NatCon length);
data Tree amb(Args args);
data Tree char(NatCon character);

type list[Tree] Args;

data Production prod(Symbols lhs, Symbol rhs, Attributes attributes);
data Production \list(Symbol rhs);