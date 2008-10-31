package org.meta_environment.rascal.ast;
import org.eclipse.imp.pdb.facts.ITree;
public abstract class ShortChar extends AbstractAST
{
  static public class Lexical extends ShortChar
  {
    /* character:[a-zA-Z0-9] -> ShortChar  */
  } static public class Ambiguity extends ShortChar
  {
    private final java.util.List < org.meta_environment.rascal.ast.ShortChar >
      alternatives;
    public Ambiguity (java.util.List <
		      org.meta_environment.rascal.ast.ShortChar >
		      alternatives)
    {
      this.alternatives =
	java.util.Collections.unmodifiableList (alternatives);
    }
    public java.util.List < org.meta_environment.rascal.ast.ShortChar >
      getAlternatives ()
    {
      return alternatives;
    }
  }
}
