@license{
  Copyright (c) 2009-2013 CWI
  All rights reserved. This program and the accompanying materials
  are made available under the terms of the Eclipse Public License v1.0
  which accompanies this distribution, and is available at
  http://www.eclipse.org/legal/epl-v10.html
}
module lang::rascal::grammar::definition::Priorities

import ParseTree;
import Grammar;
import Set;
import List;
import IO;
import util::Maybe;
 
import lang::rascal::grammar::definition::Productions;
import lang::rascal::grammar::definition::Symbols;
// import lang::rascal::grammar::analyze::Recursion;

public alias Priorities = rel[Production father, Production child];
public alias DoNotNest = rel[Production father, int position, Production child];

public DoNotNest doNotNest(Grammar g) {
  DoNotNest result = {};
  
  for (s <- g.rules) {
    lefties = {s}; // leftRecursive(g, s);
    righties = {s}; //rightRecursive(g, s);
    <ordering,ass> = doNotNest(g.rules[s], lefties, righties);
    result += ass;
    
    ordering = ordering+; // priority is transitive
   
    for (<Production father, Production child> <- ordering) {
      switch (father) {
        case prod(Symbol rhs,lhs:[Symbol l,_*,Symbol r],_) : {
          if (match(l,lefties) && match(r,righties)) {
            if (prod(Symbol crhs,clhs:[_*,Symbol cl],_) := child, match(cl,righties)) {
            result += {<father, 0, child>};
          }
          if (prod(Symbol crhs,clhs:[Symbol cl,_*],_) := child, match(cl,lefties)) {
            result += {<father, size(lhs) - 1, child>};
          }
        }   
        else fail;
      }
      case prod(Symbol rhs,lhs:[Symbol l,_*],_) :
        if (match(l,lefties), prod(Symbol crhs,clhs:[_*,Symbol cl],_) := child, match(cl,righties)) {
          result += {<father, 0, child>};
        }   
        else { 
          fail;
        }
      case prod(Symbol rhs,lhs:[_*,Symbol r],_) :
        if (match(r,righties), prod(Symbol crhs,clhs:[Symbol cl,_*],_) := child, match(cl,lefties)) {
          result += {<father, size(lhs) - 1, child>};
        }   
        else { 
          fail;
        }
      }
    } 
  }
  
  return result // TODO: in the future the except relation needs to be reported separately because it should not be indirect.
       + {*except(p, g) | /Production p <- g, p is prod || p is regular}
       ;
}

@doc{
This one-liner searches a given production for "except restrictions". 
For every position in the production that is restricted, and for every restriction it finds 
at this position, it adds a 'do-not-nest' tuple to the result.
}
public DoNotNest except(Production p:prod(Symbol _, list[Symbol] lhs, set[Attr] _), Grammar g) 
  = { <p, i, q>  | i <- index(lhs), conditional(s, excepts) := delabel(lhs[i]), g.rules[s]?, except(c) <- excepts, /q:prod(label(c,s),_,_) := g.rules[s]};
  
public DoNotNest except(Production p:regular(Symbol s), Grammar g) {
  Maybe[Production] find(str c, Symbol t) = (/q:prod(label(c,t),_,_) := (g.rules[t]?choice(s,{}))) ? just(q) : nothing();
  
  switch (s) {
    case \opt(conditional(t,cs)) : 
      return {<p,0,q> | except(c) <- cs, just(q) := find(c,t)};
    case \iter-star(conditional(t,cs)) :
      return {<p,0,q> | except(c) <- cs, just(q) := find(c,t)};
    case \iter(conditional(t,cs)) :
      return {<p,0,q> | except(c) <- cs, just(q) := find(c,t)};
    case \iter-seps(conditional(t,cs),ss) :
      return {<p,0,q> | except(c) <- cs, just(q) := find(c,t)}
           + {<p,i+1,q> | i <- index(ss), conditional(u,css) := ss[i], except(ds) <- css, just(q) := find(ds,u)};
    case \iter-seps(_,ss) :
      return {<p,i+1,q> | i <- index(ss), conditional(u,css) := ss[i], except(ds) <- css, just(q) := find(ds,u)};
    case \iter-star-seps(conditional(t,cs),ss) :
      return {<p,0,q> | except(c) <- cs, just(q) := find(c,t)}
           + {<p,i+1,q> | i <- index(ss), conditional(u,css) := ss[i], except(ds) <- css, just(q) := find(ds,u)};
    case \iter-star-seps(_,ss) :
      return {<p,i+1,q> | i <- index(ss), conditional(u,css) := ss[i], except(ds) <- css, just(q) := find(ds,u)};       
    case \alt(as) :
      return {<p,0,q> | conditional(t,cs) <- as, except(c) <- cs, just(q) := find(c,t)};
    case \seq(ss) :
      return {<p,i,q> | i <- index(ss), conditional(t,cs) <- ss, except(c) <- cs, just(q) := find(c,t)};
     default: return {};
  }
  
  return {};
}


public tuple[Priorities prio,DoNotNest ass] doNotNest(Production p, set[Symbol] lefties, set[Symbol] righties) {
  switch (p) {
    case prod(s, [*Symbol \o, t],{_*,\assoc(left())}) :
      if (match(t, righties)) return <{},{<p, size(\o), p>}>;
    case prod(s,[*Symbol \o, t],{_*,\assoc(\assoc())}) :
      if (match(t, righties)) return <{},{<p, size(\o), p>}>;
    case prod(s,[t,_*],{_*,\assoc(\right())}) :
      if (match(t, lefties)) return <{},{<p, 0, p>}>; 
    case prod(s,[t, *Symbol \o, u],{_*,\assoc(\non-assoc())}) :
      if (match(t, lefties) && match(u, righties)) return <{},{<p, 0, p>,<p,size(\o) + 1,p>}>;       
    case prod(s,[t,_*],{_*,\assoc(\non-assoc())}) :
      if (match(t, lefties)) return <{},{<p, 0, p>}>; 
    case prod(s,[*Symbol \o, t],{_*,\assoc(\non-assoc())}) :
      if (match(t, righties)) return <{},{<p, size(\o), p>}>;
    case choice(_, set[Production] alts) : {
        Priorities pr = {}; DoNotNest as = {};
        for (a <- alts, <prA,asA> := doNotNest(a, lefties, righties)) {
          pr += prA;
          as += asA;
        }
        return <pr, as>; 
      }
    case \lookahead(_,_,q) :
      return doNotNest(q); 
    case priority(_, list[Production] levels) : 
      return priority(levels, lefties, righties);
    case \associativity(_, Associativity a, set[Production] alts) : 
      return associativity(a, alts, lefties, righties);
  }
  
  return <{},{}>;
}

tuple[Priorities,DoNotNest] associativity(Associativity a, set[Production] alts, set[Symbol] lefties, set[Symbol] righties) {
  result = {};
  
  // note that there are nested groups and that each member of a nested group needs to be paired
  // with all the members of the other nested group. This explains the use of the / deep match operator.
  for ({Production pivot, *Production rest} := alts,  Production child:prod(_,_,_) := pivot) {
    switch (a) {
      case \left(): 
        for (/Production father:prod(Symbol rhs, lhs:[_*,Symbol r],_) <- rest, match(r,rhs)) 
          result += {<father, size(lhs) - 1, child>};
      case \assoc():
        result += {<father, size(lhs) - 1, child> | /Production father:prod(Symbol rhs,lhs:[_*,Symbol r],_) <- rest, match(r,righties)};
      case \right():
        result += {<father, 0, child>             | /Production father:prod(Symbol rhs,lhs:[Symbol l,_*],_) <- rest, match(l,lefties)};
      case \non-assoc(): {
        result += {<father, size(lhs) - 1, child> | /Production father:prod(Symbol rhs,lhs:[_*,Symbol r],_) <- rest, match(r,righties)}
                + {<father, 0, child>             | /Production father:prod(Symbol rhs,lhs:[Symbol l,_*],_) <- rest, match(l,lefties)};
      }
    } 
  }
  
  pr = {};
  for (x <- alts, <prX,asX> := doNotNest(x, lefties, righties)) {
    pr += prX;
    result += asX;
  }
  
  return <pr, result>;
}

public tuple[Priorities,DoNotNest] priority(list[Production] levels, set[Symbol] lefties, set[Symbol] righties) {
  // collect basic filter
  ordering = { <father,child> | [pre*,Production father, Production child, post*] := levels };

  // flatten nested structure to obtain direct relations
  todo = ordering;
  ordering = {};
  while (todo != {}) {
    <prio,todo> = takeOneFrom(todo);
    switch (prio) {
      case <choice(_,set[Production] alts),Production child> :
        todo += alts * {child};
      case <Production father, choice(_,set[Production] alts)> :
        todo += {father} * alts;
      case <associativity(_,_,set[Production] alts),Production child> :
        todo += alts * {child};
      case <Production father, associativity(_,_,set[Production] alts)> :
        todo += {father} * alts;
      default:
        ordering += prio;
    }
  }
  
  DoNotNest as = {};
  for (x <- levels, <prX,asX> := doNotNest(x, lefties, righties)) {
    ordering += prX;
    as += asX;
  }
  
  return <ordering, as>;
}

private bool match(Symbol x, set[Symbol] reference) = striprec(x) in reference;