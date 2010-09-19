package xmiParser;

import java.util.ArrayList;
import java.util.List;

public class DomainClass {

	private List<Attribute> attributes;
	private List<Operation> operations;
	
	private boolean isSubclass;
	private String superClass;
	
	private String name;

	public DomainClass(String name) {
		this.name = name;
		attributes = new ArrayList<Attribute>();
		operations = new ArrayList<Operation>();
		this.isSubclass = false;
	}
	
	public String getName() {
		return name;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}

	public List<Operation> getOperations() {
		return operations;
	}
	
	public String toString() {
		
		String lineSep = System.getProperty("line.separator");
		String result = "";
		result += "class " + this.name;
		if (this.isSubclass) {
			result += " : " + this.superClass;
		}
		result += " {" + lineSep;
		for (Attribute att : this.attributes) {
			result += att.toString() + ";" + lineSep;
		}
		result += lineSep;
		for (Operation op : this.operations) {
			result += op.toString() + lineSep;			
		}
		result += "}" + lineSep;
		return result;
	}

	public void setSuperClass(String superClass) {
		this.superClass = superClass;
		this.setSubclass(true);
	}

	public String getSuperClass() {
		return superClass;
	}

	public void setSubclass(boolean isSubclass) {
		this.isSubclass = isSubclass;
	}

	public boolean isSubclass() {
		return isSubclass;
	}
	
	
}
