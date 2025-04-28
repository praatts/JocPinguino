package controlador;
import java.sql.Connection;
public class GuardarConBD {
	private static Connection conexion;
	
	public static void setConexion(Connection con) {
		conexion = con;
	}
	
	public static Connection getConexion() {
		return conexion;
	}
	
}
