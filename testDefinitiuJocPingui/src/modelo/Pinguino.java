
package modelo;

import java.sql.*;
import java.util.Scanner;
import controlador.*;

public class Pinguino {

	private String nombre;
	private String color;
	private int posicion;
	private int id;
	private Inventario inventario;
	private String tipo;

	public Pinguino(String nombre, String color, int id, int posicion, Inventario inventario, String tipo) {
		this.nombre = nombre;
		this.color = color;
		this.id = id;
		this.posicion = 0;
		this.inventario = inventario;
		this.tipo = tipo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getPosicion() {
		return posicion;
	}

	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Inventario getInventario() {
		return inventario;
	}

	public void setInventario(Inventario inv) {
		this.inventario = inv;
	}

	public void crearPinguino(Pinguino pingu, Connection con, bbdd sentencias) {
		Scanner s = new Scanner(System.in);
		String nombre;
		String color;
		int posicion = 0;
		boolean nombreRepetido;
		Inventario inv = new Inventario(0, 0, 0, 0, 0);
		String tipo = "Jugador";

		System.out.println("Introduzca su nombre");

		do {
			nombreRepetido = false;
			nombre = s.nextLine();
			String comprobacionNombreSql = "SELECT COUNT(*) FROM JUGADOR WHERE NICKNAME = '" + nombre + "'";

			try {
				ResultSet rs = sentencias.select(con, comprobacionNombreSql);
				if (rs != null && rs.next()) {
					if (rs.getInt(1) > 0) {
						System.out.println(
								"Este nickname ya está en uso. Por favor, introduzca otro nickname para continuar");
						nombreRepetido = true;
					}
				}
				rs.close();
			} catch (SQLException e) {
				System.out.println("Error al comprobar el nickname introducido: " + e.getMessage());
			}

		} while (nombreRepetido);
		
		System.out.println("Introduzca el color de su pingüino");
		color = s.nextLine();

		pingu.setNombre(nombre);
		pingu.setColor(color);
		pingu.setPosicion(posicion);
		pingu.setInventario(inv);
		pingu.setTipo(tipo);

		String sqlInsert = "INSERT INTO JUGADOR (NICKNAME, CONTRASENYA, NUM_PARTIDAS_JUGADAS, COLOR) "
	            + "VALUES ('" + nombre + "', 'defaultPassword', 0, '" + color + "')";

		sentencias.insert(con, sqlInsert);

		System.out.println("Se ha creado su pingüino correctamente!");

	}

	public void avanzarConDado(Pinguino pingu, Dado dado) {

		pingu.setPosicion(getPosicion() + dado.getValorDado());
		System.out.println("Has avanzado " + dado.getValorDado() + " casillas!");

	}

}
