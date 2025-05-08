package controlador;
import java.sql.Connection;
import modelo.Pinguino;
import modelo.Tablero;
public class GuardarConBD {
	private static Connection conexion;
	private static Pinguino pinguino;
	private static int idPartidaCargada;
	private static Tablero tableroCargado;
	
	public static void setTableroCargado(Tablero tablero) {
	    if (tablero == null) {
	        System.out.println("Advertencia: Intentando asignar un tablero null a GuardarConBD.");
	    } else {
	        System.out.println("Tablero almacenado correctamente en GuardarConBD.");
	    }
	    tableroCargado = tablero;
	}

	public static Tablero getTableroCargado() {
	    if (tableroCargado == null) {
	        System.out.println("Advertencia: Se intenta obtener un tablero null desde GuardarConBD.");
	    } else {
	        System.out.println("Tablero recuperado correctamente desde GuardarConBD.");
	    }
	    return tableroCargado;
	}

	public static int getIdPartidaCargada() {
		return idPartidaCargada;
	}

	public static void setIdPartidaCargada(int idPartidaCargada) {
		GuardarConBD.idPartidaCargada = idPartidaCargada;
	}

	public static void setConexion(Connection con) {
		conexion = con;
	}
	
	public static Connection getConexion() {
		return conexion;
	}
	
	public static Pinguino getPinguino() {
		return pinguino;
	}
	
	public static void setPinguino(Pinguino pingu) {
		pinguino = pingu;
	}

	
}
