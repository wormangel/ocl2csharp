package xmiParser;

public class OperationParameter {

	private String name;
	private String type;
	
	public OperationParameter(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
}
