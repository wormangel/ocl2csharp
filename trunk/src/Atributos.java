public class Atributos {
	public String Code;
	public String Type;
	public Object Value;
	public int ColKind;

	public Atributos(String type, Object valor, String code) {
		this.Type = type;
		this.Value = valor;
		this.Code = code;
	}
	
	public Atributos(String type, Object valor, String code, int colKind) {
		this.Type = type;
		this.Value = valor;
		this.Code = code;
		this.ColKind = colKind;
	}
}
