package vista;

import modelo.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import modelo.*;
import controlador.*;

import java.sql.*;
import java.util.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.application.Platform;

public class pantallaMenu {

	@FXML
	private Text bienvenidoTexto;
	private Pinguino pingu;

	@FXML
	public void mostrarNombreLogin(Pinguino p) {
		bienvenidoTexto.setText("¡Bienvenido, " + p.getNombre() + "!");
	}

	@FXML

	public void handleLoadGame(ActionEvent Event) {
		pantallaJuegoController ctrl = new pantallaJuegoController();
		try {
			Connection con = GuardarConBD.getConexion();
			Pinguino pingu = GuardarConBD.getPinguino();

			String sqlConsulta = "SELECT idPartida FROM partida WHERE estado = 'en curso' AND idcreador = "
					+ pingu.getId() + " ORDER BY idPartida ASC";
			ResultSet rs = bbdd.select(con, sqlConsulta);

			List<Integer> partidasJugador = new ArrayList<>();

			while (rs.next()) {
				int id = rs.getInt("idPartida");
				partidasJugador.add(id);
			}
			rs.close();

			if (partidasJugador.isEmpty()) {
				Alert alerta = new Alert(Alert.AlertType.INFORMATION);
				alerta.setTitle("No hay partidas");
				alerta.setHeaderText(null);
				alerta.setContentText("No tienes ninguna partida almacenada que se encuentre en curso");
				alerta.showAndWait();
			} else {
				ChoiceDialog<Integer> seleccionarPartida = new ChoiceDialog<>(partidasJugador.get(0), partidasJugador);
				seleccionarPartida.setTitle("Seleccionar partida en curso");
				seleccionarPartida.setHeaderText(null);
				seleccionarPartida.setContentText("ID Partida: ");

				Optional<Integer> partidaACargar = seleccionarPartida.showAndWait();
				if (partidaACargar.isPresent()) {
					int idSeleccionada = partidaACargar.get();
					GuardarConBD.setIdPartidaCargada(idSeleccionada);
					System.out.println("ID de la partida a cargar: " + idSeleccionada);

					String sqlObtenerInventario = "SELECT ID_INVENTARIO, NUM_PECES, NUM_DADOSESP, NUM_DADOSLENTOS, NUM_DADOSRAPIDOS, NUM_BOLASNIEVE, POSICION_JUGADOR FROM INVENTARIO "
							+ "WHERE idPropietario = " + pingu.getId() + " AND id_partida = " + idSeleccionada;

					ResultSet rsInv = bbdd.select(con, sqlObtenerInventario);

					if (rsInv.next()) {
						int idInv = rsInv.getInt("ID_INVENTARIO");
						int nPeces = rsInv.getInt("NUM_PECES");
						int nDados = rsInv.getInt("NUM_DADOSESP");
						int nDadosL = rsInv.getInt("NUM_DADOSLENTOS");
						int nDadosR = rsInv.getInt("NUM_DADOSRAPIDOS");
						int nBolasNieve = rsInv.getInt("NUM_BOLASNIEVE");
						int posicion = rsInv.getInt("POSICION_JUGADOR");

						Inventario inv = new Inventario(idInv, nPeces, nDados, nDadosL, nDadosR, nBolasNieve);
						pingu.setInventario(inv);
						pingu.setPosicion(posicion);
						
					}

					rsInv.close();

					String sqlObtenerTablero = "SELECT tablero FROM partida WHERE idpartida = " + idSeleccionada
							+ " AND idCreador = " + pingu.getId();
					ResultSet rsTablero = bbdd.select(con, sqlObtenerTablero);
					Tablero tablero = new Tablero();

					if (rsTablero.next()) {
						Array sqlArray = rsTablero.getArray("tablero");
						Object[] arrayDatos = (Object[]) sqlArray.getArray();

						ArrayList<Evento> listaCasillas = new ArrayList<>();

						for (int i = 0; i < arrayDatos.length; i++) {
							String eventoString = (String) arrayDatos[i];
							Evento evento = convertirStringAEvento(eventoString);
							listaCasillas.add(evento);
						}

						tablero.setCasillas(listaCasillas);
						System.out.println("Tablero cargado con " + listaCasillas.size() + " casillas.");
					}

					GuardarConBD.setTableroCargado(tablero);
					rsTablero.close();

					try {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/pantallaJuego.fxml"));
						Parent pantallaJuegoRoot = loader.load();

						pantallaJuegoController controladorJuego = loader.getController();
						controladorJuego.tablero = tablero;
						controladorJuego.pingu = pingu;
						controladorJuego.colocarIconos();
						controladorJuego.moveP1(0);
						Scene pantallaJuego = new Scene(pantallaJuegoRoot);
						Stage stage = (Stage) ((Node) Event.getSource()).getScene().getWindow();
						stage.setScene(pantallaJuego);
						stage.setTitle("Pantalla del tablero");
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Evento convertirStringAEvento(String eventoString) {
		Evento evt = null;
		switch (eventoString) {
		case "Casilla vacía":
			evt = new Evento(1, "Casilla vacía");
			break;
		case "Oso":
			evt = new Evento(2, "Oso");
			break;
		case "Agujero en el hielo":
			evt = new Evento(3, "Agujero en el hielo");
			break;
		case "Trineo":
			evt = new Evento(4, "Trineo");
			break;
		case "Interrogante":
			evt = new Evento(5, "Interrogante");
			break;
		case "Obtener Pez":
			evt = new Evento(6, "Obtener Pez");
			break;
		case "Obtener bolas de nieve":
			evt = new Evento(7, "Obtener bolas de nieve");
			break;
		case "Obtener dado":
			evt = new Evento(8, "Obtener dado");
			break;
		case "Línea de meta":
			evt = new Evento(9, "Línea de meta");
			break;
		default:
			break;
		}

		return evt;
	}

	@FXML

	public void handleNewGame(ActionEvent Event) {
		try {

			Connection con = GuardarConBD.getConexion();
			con.setAutoCommit(false);
			Pinguino pingu = GuardarConBD.getPinguino();

			// Crear tablero y obtener casillas

			Tablero tablero = new Tablero();
			tablero.creacionTablero();
			GuardarConBD.setTableroCargado(tablero);
			// Carga de la ventana del tablero

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/pantallaJuego.fxml"));
			Parent pantallaJuegoRoot = loader.load();

			pantallaJuegoController controladorJuego = loader.getController();
			controladorJuego.tablero = tablero; // Asegúrate de que "tablero" está definido
			controladorJuego.pingu = pingu;
			controladorJuego.colocarIconos();
			Scene pantallaJuego = new Scene(pantallaJuegoRoot);
			Stage stage = (Stage) ((Node) Event.getSource()).getScene().getWindow();
			stage.setScene(pantallaJuego);
			stage.setTitle("Pantalla del tablero");

			// Conversión compatible del tablero para la base de datos

			String tableroString = "ARRAYTAULELL_V2(";
			for (int i = 0; i < tablero.getCasillas().size(); i++) {
				tableroString += "'" + tablero.getCasillas().get(i).getInfoEvento().replace("'", "''") + "'";
				if (i < tablero.getCasillas().size() - 1)
					tableroString += ",";
			}
			tableroString += ")";

			// Insert información de la partida + obtención de ID de la partida

			String insertPartida = "INSERT INTO partida (idpartida, fecha, estado, idCreador, tablero) "
					+ "VALUES (idPartidas.nextval, SYSDATE, 'en curso', " + pingu.getId() + ", " + tableroString + ")";

			bbdd.insert(con, insertPartida);
			System.out.println("SQL: " + insertPartida);
			con.commit();

			int idPartida = 0;
			ResultSet rsPartida = bbdd.select(con, "SELECT idPartidas.currval FROM dual");
			if (rsPartida.next()) {
				idPartida = rsPartida.getInt(1);
				System.out.println("ID partida generada: " + idPartida);
			}

			rsPartida.close();
			GuardarConBD.setIdPartidaCargada(idPartida);

			// Insert inventario + obtención de ID de la partida

			String insertInventario = "INSERT INTO inventario (id_inventario, num_peces, num_dadosesp, num_dadoslentos, num_dadosrapidos, num_bolasnieve, posicion_jugador, idpropietario, id_partida) "
					+ "VALUES (idInventarios.nextval, 0, 0, 0, 0, 0, 0, " + pingu.getId() + ", " + idPartida + ")";

			bbdd.insert(con, insertInventario);
			con.commit();

			// Obtener la ID del inventario creado

			int idInventario = 0;
			String sqlSelect = "SELECT idInventarios.currval FROM dual";
			ResultSet rsInventario = bbdd.select(con, sqlSelect);
			if (rsInventario.next()) {
				idInventario = rsInventario.getInt(1);
			}
			rsInventario.close();

			// Asignar inventario al objeto pinguino

			Inventario inv = new Inventario(idInventario, 0, 0, 0, 0, 0);
			pingu.setInventario(inv);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void handleQuitGame(ActionEvent Event) {
		Alert alerta = new Alert(Alert.AlertType.INFORMATION);
		alerta.setTitle("Cierre del juego");
		alerta.setHeaderText(null);
		alerta.setContentText("Se va a cerrar el juego, gracias por jugar!");
		alerta.showAndWait();

		Platform.exit();
	}

}
