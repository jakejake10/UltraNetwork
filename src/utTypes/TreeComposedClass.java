package utTypes;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import unCore.*;

public class TreeComposedClass<T>  {
	TreeNode<T> root;
	
	TreeComposedClass(){
		root = new TreeNode<>();
	}
	
	// NODE FNS //////////////////////////////////
	public TreeNode<T> root(){
		return root;
	}
	
	public TreeNode<T> get( int index ){
		return root.get(index);
	}
	public List<TreeNode<T>> getLeafs(){
		return root.getLeafs();
	}
	
	


}
