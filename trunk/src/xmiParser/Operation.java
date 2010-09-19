package xmiParser;

import java.util.ArrayList;
import java.util.List;

public class Operation {

	private String name;
	private String returnType;
	private String body;
	private List<OperationParameter> parameters;
	
	public boolean hasParameters() {
		return !parameters.isEmpty();
	}
	
	public Operation(String name, String returnType){
		this.name = name;
		this.returnType = returnType;
		if(!this.returnType.equals(XmiParser.TYPE_VOID)){
			this.body = "return null;";
		} else {
			this.body = "";
		}
		
		parameters = new ArrayList<OperationParameter>();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getReturnType() {
		return returnType;
	}

	public void setParameters(List<OperationParameter> parameters) {
		this.parameters = parameters;
	}

	public List<OperationParameter> getParameters() {
		return parameters;
	}
	
	public String toString(){
		String resultado = "";
		resultado += this.returnType + " " + this.name + "(";
		for (OperationParameter parameter : this.parameters) {
			resultado += parameter.getType() + " " + parameter.getName() + ", ";
		}
		if(resultado.endsWith(", ")){
			resultado = resultado.substring(0, resultado.length()-2);
		}
		resultado += ")";
		resultado += "{" + this.body + "}";
		
		return resultado;
	} 
}
