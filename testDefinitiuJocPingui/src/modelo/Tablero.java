
package modelo;
import controlador.*;
import java.util.ArrayList;
import java.util.Random;

public class Tablero {

	private int numeroCasillas;
	private ArrayList<Evento> casillas = new ArrayList<>();

	public Tablero (int numeroCasillas) {
	this.numeroCasillas = 50;
	this.casillas = new ArrayList<>();
	}

	public void setNumeroCasillas(String numeroCasillas){
		this.numeroCasillas = 50;
		}

	public int getNumeroCasillas() {
		return numeroCasillas;
	}
	
	public ArrayList<Evento> getCasillas() {
		return casillas;
	}

	public void setCasillas(ArrayList<Evento> casillas) {
		this.casillas = casillas;
	}

	public ArrayList<Evento> creacionTablero () {
		int generador = 0;
		Evento evt = null;
		int contadorOso = 0;
		Random r = new Random();
		int contadorCasillas = 0;
		int casillasVacias = 0;
		
		casillas.clear();
		
		casillas.add(new Evento(1, "Casilla vacía"));
		casillas.add(new Evento(1, "Casilla vacía"));
		
		while (contadorCasillas < 47) {
			generador = r.nextInt(15) + 1; 
			
		
			switch (generador) {
			case 1:
				evt = new Evento(1, "Casilla vacía");
				break;
			case 2:
				
				if (contadorOso < 2) {
				evt = new Evento(2, "Oso");
				contadorOso++;
				
				} else if (casillasVacias < 25){
					evt = new Evento (1, "Casilla vacía");
					casillasVacias++;
				}
				break;
			case 3:
				evt = new Evento(3, "Agujero en el hielo");
				break;
			case 4:
				evt = new Evento (4, "Trineo");
				break;
			case 5:
				evt = new Evento (5, "Interrogante");
				break;
			case 7:
				evt = new Evento (6, "Obtener Pez");
				break;
			case 8:
				evt = new Evento (7, "Obtener bolas de nieve");
				break;
			case 9:
				evt = new Evento (8, "Obtener dado");
				break;
			default:
				if (casillasVacias < 25) {
				evt = new Evento (1, "Casilla vacía");
				casillasVacias++;
				}
			break;	
			
			
			}
			
			casillas.add(evt);
			contadorCasillas++;
			
		}
		casillas.add(new Evento(10, "Línea de meta"));
		return casillas;
	}

	
}
