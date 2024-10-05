package utTypes;

public class GroupSubclassTemplate extends SubclassableTreeNodeTemplate {

	public GroupSubclassTemplate(String name) {
		super(name, "GroupSubclassTemplate");
	}

	/*
	 * example implementation of an abstract method in superclass recursively adds
	 * value of children returns total value of subtree
	 */
//	  float getValue(){
//	    float out = 0;
//	    if( hasChildren() ) for( SubclassableTreeNodeTemplate c : getChildren() ) out += c.getValue();
//	    return out;
//	  }

	public GroupSubclassTemplate defaultConstructor() {
		return new GroupSubclassTemplate("");
	}

	public GroupSubclassTemplate getInstance() {
		return this;
	}

	@Override
	public GroupSubclassTemplate copyNode(boolean transferNodeData) {
		return (GroupSubclassTemplate) super.copyNode(transferNodeData);
	}

	@Override
	public void transferSubclassFieldsTo(SubclassableTreeNodeTemplate newNode) {
		super.transferSubclassFieldsTo(newNode);
	}

	@Override
	public String toString() {
		return super.toString() + ": ";
	};

}
