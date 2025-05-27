package utTypes;

import java.util.List;
import java.util.function.*;
import java.util.stream.*;

public class TreeFunctionGroup<I,O> extends TreeFunctionObject<I,O> {
	BiFunction<List<O>,I,O> myFn;
	
	public TreeFunctionGroup( BiFunction<List<O>,I,O> myFn ) {
		this.myFn = myFn;
	}

	
	public O runFn(I input) {
		return myFn.apply(getChildren().stream().map(c -> c.runFn(input)).collect(Collectors.toList()), input);
	}

	public TreeFunctionGroup<I,O> defaultConstructor() {
		return new TreeFunctionGroup<>((oList,i) -> null );
	}

	public TreeFunctionGroup<I,O> getInstance() {
		return this;
	}

	@Override
	public TreeFunctionGroup<I,O> copyNode(boolean transferNodeData) {
		return (TreeFunctionGroup<I,O>) super.copyNode(transferNodeData);
	}

	@Override
	public void transferSubclassFieldsTo(TreeFunctionObject<I,O> newNode) {
		super.transferSubclassFieldsTo(newNode);
	}

	@Override
	public String toString() {
		return super.toString() + ": ";
	};

}
