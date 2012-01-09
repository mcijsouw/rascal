package org.rascalmpl.library.util;

//This code was generated by Rascal API gen
import org.rascalmpl.interpreter.types.RascalTypeFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.eclipse.imp.pdb.facts.*;

public class Maybe {
	public static final TypeStore typestore = new TypeStore(
		org.rascalmpl.values.errors.Factory.getStore(), 
		org.rascalmpl.values.locations.Factory.getStore());

	private static final TypeFactory tf = TypeFactory.getInstance();
	private static final RascalTypeFactory rtf = RascalTypeFactory.getInstance();

	
	public static final Type Maybe = tf.abstractDataType(typestore, "Maybe",tf.parameterType("A",tf.valueType()));

	public static final Type Maybe_nothing = tf.constructor(typestore,Maybe,"nothing");
	public static final Type Maybe_just = tf.constructor(typestore,Maybe,"just",tf.parameterType("A",tf.valueType()),"val");
					
	
	 public static IValue Maybe_just_val(IConstructor c){
	return (IValue)c.get(0);
}

	private static final class InstanceHolder {
		public final static Maybe factory = new Maybe();
	}
	  
	public static Maybe getInstance() {
		return InstanceHolder.factory;
	}
	
	
	public static TypeStore getStore() {
		return typestore;
	}
}