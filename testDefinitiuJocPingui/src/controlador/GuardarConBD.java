package controlador;
import java.sql.Connection;

import modelo.Pinguino;
public class GuardarConBD {
	private static Connection conexion;
	private static Pinguino pinguino;
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
