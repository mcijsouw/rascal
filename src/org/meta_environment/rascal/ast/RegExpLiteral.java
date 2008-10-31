package org.meta_environment.rascal.ast;
import org.eclipse.imp.pdb.facts.ITree;
public abstract class RegExpLiteral extends AbstractAST
{
  static public class Lexical extends RegExpLiteral
  {
    /* "/" RegExp* "/" RegExpModifier? -> RegExpLiteral  */
  }
  static public class Ambiguity extends RegExpLiteral
  {
    private final java.util.List <
      org.meta_environment.rascal.ast.RegExpLiteral > alternatives;
    public Ambiguity (java.util.List <
		      org.meta_environment.rascal.ast.RegExpLiteral >
		      alternatives)
    {
      this.alternatives =
	java.util.Collections.unmodifiableList (alternatives);
    }
    public java.util.List < org.meta_environment.rascal.ast.RegExpLiteral >
      getAlternatives ()
    {
      return alternatives;
    }
  }
}
