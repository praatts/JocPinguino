
package modelo;

public class Casilla {
	
	private String tipo;
	private int id;
	
	public Casilla (String  tipo, int  id) {
		this.tipo = tipo;
		this.id = id;
	}
	
	public String  getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}	
}
