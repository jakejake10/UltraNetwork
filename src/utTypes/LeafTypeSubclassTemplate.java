package utTypes;

public class LeafTypeSubclassTemplate extends SubclassableTreeNodeTemplate {
	// example of unique fields
	// int timesPerYear = 12;
	// float cost = 0;

	public LeafTypeSubclassTemplate(String name, float cost) {
		super(name, "LeafTypeSubclassTemplate");
		// unique settings in constructor
//		this.cost = cost;
	}

	/*
	 * this nodes implementation of abstract method in superclass
	 * accessible with other node types in recursive operations
	 */
//	float getValue() {
//		return cost * -1;
//	}

	public LeafTypeSubclassTemplate defaultConstructor() {
		return new LeafTypeSubclassTemplate("", 0);
	}

	public LeafTypeSubclassTemplate getInstance() {
		return this;
	}

	@Override
	public LeafTypeSubclassTemplate copyNode(boolean transferNodeData) {
		return (LeafTypeSubclassTemplate) super.copyNode(transferNodeData);
	}

	@Override
	public void transferSubclassFieldsTo(SubclassableTreeNodeTemplate newNode) {
		super.transferSubclassFieldsTo(newNode);	// transfers fields in superclass
//		((LeafTypeSubclassTemplate) newNode).timesPerYear = timesPerYear;	// transfer any unique fields of subclass
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
