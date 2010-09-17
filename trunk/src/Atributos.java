public class Atributos {
	public String Code;
	public Class<?> Type;
	public Object Value;

	public Atributos(Class<?> classe, Object valor, String code) {
		this.Type = classe;
		this.Value = valor;
		this.Code = code;
	}
}
