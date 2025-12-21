package utTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

import processing.data.JSONObject;
import unCore.*;

public class DataNode<D> implements TreeNodeObject<DataNode<D>>, Iterable<DataNode<D>> {
	public enum DataKind { NULL, INT, BOOL, STRING, FLOAT }
	private DataKind kind = DataKind.NULL;
	SingularTreeData<DataNode<D>> core;
	D data;

	public int index;
	public int parentIndex = -1; // index in tree.nodes
	public int firstChild = -1;
	public int childCt = 0;
	public int depth = 0;

	public DataNode() {
		initNodeFields();
	}

	public DataNode(D data) {
		initNodeFields();
		setData(data);
	}

	// TREENODE Interface ////////////////////
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getParentIndex() {
		return parentIndex;
	}

	public void setParentIndex(int index) {
		this.parentIndex = index;
	}

	public int getFirstChildIndex() {
		return firstChild;
	}

	public void setFirstChildIndex(int index) {
		this.firstChild = index;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getChildCount() {
		return childCt;
	}

	public void setChildCount(int count) {
		this.childCt = count;
	}

	public SingularTreeData<DataNode<D>> getCoreFn() {
		return core;
	}

	public void setCore(SingularTreeData<DataNode<D>> input) {
		this.core = input;
	}

	public DataNode<D> getInstance() {
		return this;
	}

	public DataNode<D> defaultConstructor() {
		return new DataNode<>(null);
	}

	public Iterator<DataNode<D>> iterator() {
		return nodeIterator();
	}

	// OPTIONAL METHODS //////////////////////////

	public void transferSubclassFieldsTo(DataNode<D> newNode) {
		newNode.setData(getData());
	}

	// DATA METHODS //////////////////////////////
	public D getData() {
		return data;
	}

	public List<D> getLeafData() {
		return getLeafs().stream().map(n -> n.getData()).collect(Collectors.toList());
	}

	public void setData(D input) {
		this.data = input;
	}

	public boolean hasData() {
		return getData() != null;
	}

	public boolean equalsData(D dataIn) {
		return getData().equals(dataIn);
	}

	// cant use addChild, if D = int, will override addChild( index ) method
	public void addChildWithData(D data) {
		addChild(n -> n.setData(data));
	}

	// OTHER METHODS ///////////////////////////////////////

	public <E> DataNode<E> convertData(Function<D, E> convertFn) {
		return convertNodeSubtree(n -> new DataNode<>(convertFn.apply(n.getData())));
	}

	// DATA / GENERATION METHODS ///////////////////////////////////
	public static <E> DataNode<E> buildFromDataList(TreeNodeObject<?> structure, List<E> dataList) {
		if (structure.getTotalSize() != dataList.size())
			throw new UnsupportedOperationException("data list size not equal to node size");
		return structure.convertNodeSubtree(n -> new DataNode<>(dataList.get(n.getIndex())));
	}

	public List<D> exportDataList() {
		List<D> dataListOut = (List<D>) TreeNodeFunctions.initDataList(getInstance(), null);
		traverseOperation(dataListOut, (dList, n) -> dList.set(n.getIndex(), n.getData()));
		return dataListOut;
	}

	public void loadData(List<D> data) {
		IntWrapper index = new IntWrapper(0);
		buildTree().setLeafCt(data.size()).setChildCt(data.size()).setPostGenMod(n -> {
			if (n.isLeaf()) {
				n.setData(data.get(index.myValue));
				index.myValue = index.myValue + 1;
			}
		}).make();
	}

	public void loadData(List<D> data, int maxChildCt) {
		IntWrapper index = new IntWrapper(0);
		buildTree().setLeafCt(data.size()).setChildCt(maxChildCt).setPostGenMod(n -> {
			if (n.isLeaf()) {
				n.setData(data.get(index.myValue));
				index.myValue = index.myValue + 1;
			}
		}).make();
	}

	/*
	 * used for modifying integers within the scope of lambda
	 */
	public class IntWrapper {
		int myValue = 0;

		public IntWrapper(int input) {
			this.myValue = input;
		}
	}

	public void loadDataOperation(List<D> dataIn, BiFunction<DataNode<D>, List<D>, List<List<D>>> fn) {
		List<List<D>> dataList = new ArrayList<>(); // list of just root
		dataList.add(dataIn);
		loadDataOperationRecursive(dataList, fn);
	}

	public void loadDataOperationRecursive(List<List<D>> dataList, BiFunction<DataNode<D>, List<D>, List<List<D>>> fn) {
		dataListOperation(dataList, (n, nodeDList) -> {

//		  if( nodeDList == null ) return;
			List<List<D>> curList = fn.apply(n, nodeDList);
			if (curList.size() > 1) {
				for (int i = 0; i < curList.size(); i++) {
					n.addChild();
					dataList.add(curList.get(i)); // building datalist with nodelist, should be same index
				}
			} else { // fn results in only 1 D element
				n.setData(curList.get(0).get(0));
			}
		});
	}

	// JSON METHODS ////////////////////////////
	// Optional: override in typed subclasses if you want, but not required
	protected DataKind declaredKind() {
		// If you can’t know D at runtime, declare it explicitly per node instance.
		// Default: infer from current data (works unless data is null).
		Object d = data;
		if (d instanceof Integer)
			return DataKind.INT;
		if (d instanceof Boolean)
			return DataKind.BOOL;
		if (d instanceof String)
			return DataKind.STRING;
		if (d instanceof Float)
			return DataKind.FLOAT;
		return DataKind.NULL;
	}

	@Override
	  public JSONObject dataToJSON( JSONObject json ) {

	    DataKind k = declaredKind();
	    json.setString("kind", k.name());

	    if (data == null) return json; // null value; kind still stored

	    switch (k) {
	      case INT:
	    	  json.setInt("value", ((Integer) data).intValue());
	        break;
	      case BOOL:
	    	  json.setBoolean("value", ((Boolean) data).booleanValue());
	        break;
	      case STRING:
	    	  json.setString("value", (String) data);
	        break;
	      case FLOAT:
	    	  json.setFloat("value", ((Float) data).floatValue());
	        break;
	      case NULL:
	        // no value
	        break;
	      default:
	        throw new RuntimeException("Unhandled kind: " + k);
	    }

	    return json;
	  }

	  @SuppressWarnings("unchecked")
	  @Override
	  public void dataFromJSON(JSONObject o) {
	    if (o == null) { data = null; return; }

	    DataKind k = DataKind.valueOf(o.getString("kind", "NULL"));

	    // If there's no "value" key, treat as null
	    switch (k) {
	      case INT: {
	        Integer v = Integer.valueOf(o.getInt("value"));
	        data = (D) v;
	        break;
	      }
	      case BOOL: {
	        Boolean v = Boolean.valueOf(o.getBoolean("value"));
	        data = (D) v;
	        break;
	      }
	      case STRING: {
	        String v = o.getString("value");
	        data = (D) v;
	        break;
	      }
	      case FLOAT: {
	        Float v = Float.valueOf(o.getFloat("value"));
	        data = (D) v;
	        break;
	      }
	      case NULL:
	        data = null;
	        break;
	      default:
	        throw new RuntimeException("Unhandled kind: " + k);
	    }

	    // keep kind in sync after load
	    kind = k;
	  }

	public String toString() {
		return "node: " + (getData() != null ? getData().toString() : "null");
	}

}
