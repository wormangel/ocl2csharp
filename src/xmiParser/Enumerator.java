package xmiParser;

import java.util.ArrayList;
import java.util.List;

public class Enumerator {

	private String name;
	private List<String> itens;
	
	public Enumerator(String name){
		this.name = name;
		itens = new ArrayList<String>();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setItens(List<String> itens) {
		this.itens = itens;
	}

	public List<String> getItens() {
		return itens;
	}
	
	public String toString(){
		String lineSep = System.getProperty("line.separator");
		String result = "";
		result += "enum " + this.name + "{" + lineSep;
		for (String item : this.itens) {
			result += item + "," + lineSep;
		}
		result = result.substring(0,result.length()-3);
		result += lineSep + "}" + lineSep;
		return result;
	}
}
