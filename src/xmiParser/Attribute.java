package xmiParser;

public class Attribute {
	
	private String name;
	private String type;
	private String visibility; 
	
	public Attribute(String name, String type){
		this.name = name;
		this.type = type;
		this.visibility = XmiParser.VISIBILITY_PACKAGE;
	}
	

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}


	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}


	public String getVisibility() {
		return visibility;
	}
	
	public String toString() {
		String result = "";
		result += this.visibility + " " + this.type + " " + this.name;
		return result;
	}
	
}
