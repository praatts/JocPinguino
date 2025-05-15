
package modelo;
import controlador.*;
import java.util.ArrayList;
import java.util.Random;

public class Tablero {

	private static final int totalCasillas = 50;
	private ArrayList<Evento> casillas = new ArrayList<>();

	public Tablero () {
	this.casillas = new ArrayList<>(totalCasillas);
	this.creacionTablero();
	}

	public int getTotalCasillas() {
		return totalCasillas;
	}
	
	public ArrayList<Evento> getCasillas() {
		return casillas;
	}

	public void setCasillas(ArrayList<Evento> casillas) {
		this.casillas = casillas;
	}

	public ArrayList<Evento> creacionTablero () {
		casillas.clear();
		
		casillas.add(new Evento(1, "Casilla vacía"));
		casillas.add(new Evento(1, "Casilla vacía"));
		int generador = 0;
		int contadorOso = 0;
		Random r = new Random();
		int casillasVacias = 2;
		
		
		while (casillas.size() < totalCasillas - 1) {
			Evento evt = null;
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
			case 10:
				evt = new Evento (10, "Moto de nieve");
				break;
			case 11:
				evt = new Evento (11, "Suelo Quebradizo");
			default:
				if (casillasVacias < 25) {
				evt = new Evento (1, "Casilla vacía");
				casillasVacias++;
				}
			break;	
			
			
			}
		
			if (evt != null) {
				casillas.add(evt);
			}
			
		}
		casillas.add(new Evento(9, "Línea de meta"));
		return casillas;
	}

	
}
