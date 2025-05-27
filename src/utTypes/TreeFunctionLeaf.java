package utTypes;

import java.util.function.*;

public class TreeFunctionLeaf<I,O> extends TreeFunctionObject<I,O> {
	BiFunction<TreeFunctionLeaf<I,O>,I,O> myFn;

	public TreeFunctionLeaf(BiFunction<TreeFunctionLeaf<I,O>,I,O> myFn) {
		this.myFn = myFn;
	}

	O runFn( I input ) {
		return myFn.apply(getInstance(),input);
	}

	public TreeFunctionLeaf<I,O> defaultConstructor() {
		return new TreeFunctionLeaf<>( (i,n) -> null );
	}

	public TreeFunctionLeaf<I,O> getInstance() {
		return this;
	}

	@Override
	public TreeFunctionLeaf<I,O> copyNode(boolean transferNodeData) {
		return (TreeFunctionLeaf<I,O>) super.copyNode(transferNodeData);
	}

	@Override
	public void transferSubclassFieldsTo(TreeFunctionObject<I,O> newNode) {
//		super.transferSubclassFieldsTo(newNode);	// transfers fields in superclass
		((TreeFunctionLeaf<I,O>) newNode).myFn = myFn;	// transfer any unique fields of subclass
	}

	@Override
	public String toString() {
		return super.toString() + ", timesPerYear: "; // + timesPerYear;
	};

	/*
	 * we want this type to be a leaf only
	 * overriding and returning false returns exception if trying to add child
	 */
	@Override
	public boolean allowChildren() {
		return false;
	}

}
