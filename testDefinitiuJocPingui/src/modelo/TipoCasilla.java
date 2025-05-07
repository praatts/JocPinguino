
package modelo;

import java.sql.Connection;
import java.util.*;
import java.util.ArrayList;
import java.util.Random;
import controlador.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import vista.pantallaJuegoController;

public class TipoCasilla extends Casilla {
	private int idPartida;
	private String tipo;
	private int id;
	private pantallaJuegoController actualizarPosicionVisual;

	// Constructor

	public TipoCasilla(String tipo, int id) {

		super(tipo, id);

	}

	// Setters i Getters
	public String getTipo() {
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

	// Manda al jugador al inicio del juego si no tiene 2 pescados

	public void casillaOso(Pinguino pingu, pantallaJuegoController actualizarPosicionVisual) {
		Alert alerta = null;
		int pecesJugador = pingu.getInventario().getPeces();
		System.out.println("¡" + pingu.getNombre() + " se ha encontrado a un oso!");

		if (pecesJugador >= 2) {
			System.out.println("¡Jugador +" + pingu.getNombre() + "ha usado dos peces para sobornar al oso!");
			pingu.getInventario().setPeces(pecesJugador - 2);
			alerta = new Alert(AlertType.INFORMATION,
					"Has usado dos peces para sobornar al oso y no has sido penalizado!", ButtonType.OK);
			alerta.setTitle("Encuentro con el oso!");
			alerta.setHeaderText(null);
			alerta.showAndWait();
		} else {
			System.out.println("No tienes suficientes peces para sobornar al oso. Has vuelto al inicio :(");
			pingu.setPosicion(0);
			alerta = new Alert(AlertType.WARNING,
					"No tienes suficientes peces para sobornar al oso. Has vuelto al inicio :(", ButtonType.OK);
			alerta.setTitle("Encuentro con el oso!");
			alerta.setHeaderText(null);
			alerta.showAndWait();

			actualizarPosicionVisual.moveP1(0);
		}

	}

	// Método casilla agujeroHielo, manda al jugador al anterior agujero de hielo
	// (si lo hay)

	public void casillaAgujeroHielo(Pinguino pingu, ArrayList<Evento> casillas,
			pantallaJuegoController actualizarPosicionVisual) {
		int agujeroActual = pingu.getPosicion();
		int agujeroAnterior = 0;
		boolean agujeroAnteriorEncontrado = false;
		Alert alerta = null;
		if (agujeroActual > 0 && casillas.get(agujeroActual).getIDEvento() == 3) {
			System.out.println("Has caído en un agujero de hielo!");
			actualizarPosicionVisual.colocarPinguino(agujeroActual);
			for (int j = agujeroActual - 1; j >= 0; j--) {
				if (!agujeroAnteriorEncontrado && casillas.get(j).getIDEvento() == 3) {
					agujeroAnterior = j;
					agujeroAnteriorEncontrado = true;
				}

			}
		}

		if (agujeroAnteriorEncontrado && agujeroAnterior != 0) {
			System.out.println("Has retrocedido " + (agujeroActual - agujeroAnterior) + " casillas :(");
			pingu.setPosicion(agujeroAnterior);
			alerta = new Alert(Alert.AlertType.WARNING);
			alerta.setTitle("¡Agujero de Hielo!");
			alerta.setHeaderText(null);
			alerta.setContentText(
					"¡Te has caído en un agujero de hielo!\nRetrocedes a la casilla " + (agujeroAnterior + 1) + " :(");

		} else if (agujeroAnterior == 0) {
			System.out.println("No se ha encontrado ningún agujero anterior a este, no has sido penalizado");
			alerta = new Alert(Alert.AlertType.WARNING);
			alerta.setTitle("¡Agujero de hielo evitado!");
			alerta.setHeaderText(null);
			alerta.setContentText("No se ha encontrado ningún agujero anterior a este, no has sido penalizado!");
		}
		if (alerta != null) {
			alerta.showAndWait();
			if (agujeroAnteriorEncontrado && agujeroAnterior != 0) {
				pingu.setPosicion(agujeroAnterior);
				actualizarPosicionVisual.colocarPinguino(agujeroAnterior);
				actualizarPosicionVisual.actualizarPosicionBaseDeDatos(pingu, actualizarPosicionVisual.idPartida);

			}
		}
	}

	// Método de casilla trinero (manda al jugador al SIGUIENTE trineo)

	public void casillaTrineo(Pinguino pingu, ArrayList<Evento> casillas,
			pantallaJuegoController actualizarPosicionVisual) {
		int trineoActual = pingu.getPosicion();
		int trineoSiguiente = 0;
		boolean trineoPosteriorEncontrado = false;
		Alert alerta = null;

		if (trineoActual > 0 && casillas.get(trineoActual).getIDEvento() == 4) {
			System.out.println("Has caído en un trineo!");
			for (int j = trineoActual + 1; j < casillas.size(); j++) {
				if (!trineoPosteriorEncontrado && casillas.get(j).getIDEvento() == 4) {
					trineoSiguiente = j;
					trineoPosteriorEncontrado = true;
				}

			}
		}

		if (trineoPosteriorEncontrado && trineoSiguiente != 0) {

			System.out.println("Has avanzado " + (trineoSiguiente - trineoActual) + " casillas!");
			pingu.setPosicion(trineoSiguiente);
			alerta = new Alert(Alert.AlertType.INFORMATION);
			alerta.setTitle("¡Trineo encontrado!");
			alerta.setHeaderText(null);
			alerta.setContentText(
					"¡Has encontrado un trineo!\nAvanzas hasta el siguiente trineo que se encuentra en la casilla "
							+ (trineoSiguiente) + ".");
			alerta.showAndWait();

			pingu.setPosicion(trineoSiguiente);
			actualizarPosicionVisual.colocarPinguino(trineoSiguiente);
			actualizarPosicionVisual.actualizarPosicionBaseDeDatos(pingu, actualizarPosicionVisual.idPartida);
		} else if (trineoSiguiente == 0) {
			System.out.println("No se ha encontrado ningún trineo posterior a este, no has avanzado ninguna casilla");

			alerta = new Alert(Alert.AlertType.INFORMATION);
			alerta.setTitle("¡Trineo no encontrado!");
			alerta.setHeaderText(null);
			alerta.setContentText("No hay más trineos adelante, no avanzas ninguna casilla!");
			alerta.showAndWait();
		}

	}

	// Método que activa un evento aleatorio

	public void accionInterrogante(Pinguino pingu) {
		TipoCasilla activador = new TipoCasilla("", 0);
		Random random = new Random();
		Alert alerta = new Alert(AlertType.INFORMATION);
		alerta.setTitle("Evento de Casilla Interrogante");
		alerta.setHeaderText(null);

		System.out.println("¡Jugador " + pingu.getNombre() + " ha activado una casilla de interrogante!");

		int esdeveniment = random.nextInt(3);
		switch (esdeveniment) {
		case 0:
			System.out.println("¡Jugador " + pingu.getNombre() + " ha obtenido un pez!");
			alerta.setContentText("¡Jugador " + pingu.getNombre() + " ha activado una casilla de interrogante!");
			activador.obtenerPescado(pingu);

			break;
		case 1:
			System.out.println("¡Jugador " + pingu.getNombre() + " ha obtenido una/s bola de nieve!");
			alerta.setContentText("¡Jugador " + pingu.getNombre() + " ha activado una casilla de interrogante!");
			activador.obtenerBolasdeNieve(pingu);
			break;
		case 2:
			alerta.setContentText("¡Jugador " + pingu.getNombre() + " ha activado una casilla de interrogante!");
			System.out.println("¡Jugador " + pingu.getNombre() + " ha obtenido un dado aleatorio!");
			activador.obtenerDadoAleatorio(pingu);
			break;

		}
	}

	// Método que añade pescados al inventario del jugador.

	public void obtenerPescado(Pinguino pingu) {
		Alert alerta = null;
		int pecesJugador = pingu.getInventario().getPeces();
		idPartida = GuardarConBD.getIdPartidaCargada();
		if (pecesJugador < 2) {

			try {
				Connection con = GuardarConBD.getConexion();
				String sqlUpdate = "UPDATE INVENTARIO SET NUM_PECES = " + (pecesJugador + 1) + " WHERE IDPropietario = "
						+ pingu.getId() + " AND id_partida = " + idPartida;

				bbdd.update(con, sqlUpdate);
				con.commit();
				pingu.getInventario().setPeces(pecesJugador + 1);

				alerta = new Alert(AlertType.INFORMATION);
				alerta.setTitle("Pescado/s añadido al inventario");
				alerta.setHeaderText(null);
				alerta.setContentText("Se ha añadido un pez al inventario!\nPeces en el inventario: "
						+ pingu.getInventario().getPeces());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			alerta = new Alert(AlertType.WARNING);
			alerta.setTitle("Inventario lleno de pescados");
			alerta.setHeaderText(null);
			alerta.setContentText(
					"No ha sido posible añadir ningún pescado al inventario, ya posees el máximo de pescados permitidos");

			System.out.println("Tienes el máximo de peces permitidos, no se ha añadido el pez al inventario.");
		}

		alerta.showAndWait();
	}

	// Método para añadir de 1 a 3 bolas de nieve al inventario del jugador

	public void obtenerBolasdeNieve(Pinguino pingu) {
		Random r = new Random();
		int generador = r.nextInt(3) + 1;
		int bolasJugador = pingu.getInventario().getBolasDeNieve();
		int bolasTotales = 0;

		if (bolasJugador >= 6) {
			System.out.println("Tienes el máximo de bolas de nieve permitidas");
		} else if (bolasJugador + generador >= 6) {
			bolasTotales = 6;
		} else {
			bolasTotales = bolasJugador + generador;
		}

		// Actualización en la base de datos
		try {
			Connection con = GuardarConBD.getConexion();
			int idPartida = GuardarConBD.getIdPartidaCargada();

			String sqlUpdate = "UPDATE INVENTARIO SET NUM_BOLASNIEVE = " + bolasTotales + " WHERE idpropietario = "
					+ pingu.getId() + " AND id_partida = " + idPartida;

			bbdd.update(con, sqlUpdate);
			con.commit();
			pingu.getInventario().setBolasDeNieve(bolasTotales);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Alert alerta = new Alert(AlertType.INFORMATION);
		alerta.setTitle("Bolas de Nieve");
		alerta.setHeaderText(null);

		if (bolasJugador >= 6) {
			alerta.setAlertType(AlertType.WARNING);
			alerta.setContentText(
					"Ya tienes el máximo número de bolas de nieve permitidas, no se ha añadido ninguna bola de nieve al inventario.");
		} else {
			alerta.setContentText("Has conseguido " + generador + " bolas de nieve.\nAhora tienes " + bolasTotales
					+ " en el inventario.");
		}

		alerta.showAndWait();

	}

	// Método con probabilidades (30/70)% para obtener un dado rápido (5 a 10
	// casillas) o un dado lento (1 a 3 casillas)

	public void obtenerDadoAleatorio(Pinguino pingu) {
		Random r = new Random();
		Alert alerta = null;
		int idPartida = GuardarConBD.getIdPartidaCargada();
		int nDadosLentos = pingu.getInventario().getDadosL();
		int nDadosRapidos = pingu.getInventario().getDadosR();
		int nDados = pingu.getInventario().getDados();
		int probabilidad = r.nextInt(10) + 1;
		String tipoDado = "";
		if (pingu.getInventario().getDados() >= 3) {
			System.out.println("No se ha añadido un dado al inventario, ya posees el máximo permitido.");

			alerta = new Alert(AlertType.WARNING);
			alerta.setTitle("Inventario lleno");
			alerta.setHeaderText(null);
			alerta.setContentText(
					"No se ha podido añadir el dado al inventario, ya posees el máximo de dados especiales permitidos.");
			alerta.showAndWait();
		} else {
			if (probabilidad <= 3) {
				try {

					Connection con = GuardarConBD.getConexion();
					System.out.println("Jugador " + pingu.getNombre() + " ha obtenido un dado rápido");
					pingu.getInventario().setDadosR(pingu.getInventario().getDadosR() + 1);
					tipoDado = "dado rápido";

					String sqlUpdateRapidos = "UPDATE INVENTARIO SET NUM_DADOSRAPIDOS = " + (nDadosRapidos + 1)
							+ " WHERE idPropietario = " + pingu.getId() + " AND id_partida = " + idPartida;
					bbdd.update(con, sqlUpdateRapidos);
					con.commit();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					Connection con = GuardarConBD.getConexion();
					System.out.println("Jugador " + pingu.getNombre() + " ha obtenido un dado lento");
					pingu.getInventario().setDadosL(pingu.getInventario().getDadosL() + 1);
					tipoDado = "dado lento";

					String sqlUpdateLentos = "UPDATE INVENTARIO SET NUM_DADOSLENTOS = " + (nDadosLentos + 1)
							+ " WHERE idPropietario = " + pingu.getId() + " AND id_partida = " + idPartida;
					bbdd.update(con, sqlUpdateLentos);
					con.commit();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			try {
				Connection con = GuardarConBD.getConexion();
				String sqlUpdate = "UPDATE INVENTARIO SET NUM_DADOSESP = " + (nDados + 1) + " WHERE idPropietario = "
						+ pingu.getId() + " AND id_partida = " + idPartida;
				bbdd.update(con, sqlUpdate);
				con.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}

			pingu.getInventario().setDados(pingu.getInventario().getDados() + 1);

			alerta = new Alert(AlertType.INFORMATION);
			alerta.setTitle("Dado añadido al inventario");
			alerta.setHeaderText(null);
			alerta.setContentText("¡Has conseguido un " + tipoDado + "!\n" + "Dados Rápidos disponibles: "
					+ pingu.getInventario().getDadosR() + "\n" + "Dados Lentos disponibles: "
					+ pingu.getInventario().getDadosL() + "\n" + "Dados especiales totales: "
					+ pingu.getInventario().getDados());
			alerta.showAndWait();
		}
	}

	public void lineaDeMeta(Pinguino pingu) {
		idPartida = GuardarConBD.getIdPartidaCargada();
		try {
			Connection con = GuardarConBD.getConexion();
			if (pingu.getPosicion() == 49) {
				
				String sqlUpdate = "UPDATE partida SET estado = 'finalizada' WHERE idPartida = " + idPartida + " AND idCreador = " + pingu.getId();
				bbdd.update(con, sqlUpdate);
				con.commit();
				
				Alert alerta = new Alert(AlertType.INFORMATION);
				alerta.setTitle("Ganador");
				alerta.setHeaderText(null);
				alerta.setContentText(pingu.getNombre() + " ha ganado!\n\nPulse en el desplegable superior izquierdo para cargar otra partida o cerrar el juego");
				alerta.showAndWait();
				
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
