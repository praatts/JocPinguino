package vista;

import javafx.application.Platform;
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
import javafx.stage.Window;
import modelo.*;
import controlador.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.application.Platform;

public class pantallaJuegoController {

	// Menu items
	@FXML
	private MenuItem newGame;
	@FXML
	private MenuItem saveGame;
	@FXML
	private MenuItem loadGame;
	@FXML
	private MenuItem quitGame;

	// Buttons
	@FXML
	private Button dado;
	@FXML
	private Button rapido;
	@FXML
	private Button lento;
	@FXML
	private Button peces;
	@FXML
	private Button nieve;

	// Texts
	@FXML
	private Text dadoResultText;
	@FXML
	private Text rapido_t;
	@FXML
	private Text lento_t;
	@FXML
	private Text peces_t;
	@FXML
	private Text nieve_t;
	@FXML
	private Text eventos;

	// Game board and player pieces
	@FXML
	private GridPane gridPaneTablero;
	@FXML
	private Circle P1;
	@FXML
	private Circle P2;
	@FXML
	private Circle P3;
	@FXML
	private Circle P4;

	// ONLY FOR TESTING!!!
	private int p1Position = 0; // Tracks current position (from 0 to 49 in a 5x10 grid)
	private final int COLUMNS = 5;
	public Pinguino pingu;
	public int idPartida;
	public Tablero tablero;

	@FXML
	private void initialize() {
		// This method is called automatically after the FXML is loaded
		// You can set initial values or add listeners here

	}

	public void colocarIconos() {

		tablero = GuardarConBD.getTableroCargado();
		Evento evento = null;
		int fila = 0;
		int columna = 0;
		for (int i = 0; i < tablero.getCasillas().size(); i++) {
			evento = tablero.getCasillas().get(i);

			fila = i / COLUMNS;
			columna = i % COLUMNS;

			ImageView iconoEvento = null;

			switch (evento.getIDEvento()) {
			case 2:
				iconoEvento = setIcono("/resources/iconoOso.png");
				break;
			case 3:
				iconoEvento = setIcono("/resources/iconoHielo.png");
				break;
			case 4:
				iconoEvento = setIcono("/resources/iconoTrineo.png");
				break;
			case 5:
				iconoEvento = setIcono("/resources/iconoInterrogante.png");
				break;
			case 6:
				iconoEvento = setIcono("/resources/iconoPez.png");
				break;
			case 7:
				iconoEvento = setIcono("/resources/iconoBDN.png");
				break;
			case 8:
				iconoEvento = setIcono("/resources/iconoDados.png");
				break;
			case 9:
				iconoEvento = setIcono("/resources/finish.png");
				break;
			default:
				break;
			}

			if (iconoEvento != null) {
				GridPane.setRowIndex(iconoEvento, fila);
				GridPane.setColumnIndex(iconoEvento, columna);
				GridPane.setHalignment(iconoEvento, HPos.CENTER);
				GridPane.setValignment(iconoEvento, VPos.CENTER);
				gridPaneTablero.getChildren().add(iconoEvento);

			}
		}
	}

	private ImageView setIcono(String rutaIcono) {
		Image imagen = new Image(getClass().getResourceAsStream(rutaIcono));
		ImageView icono = new ImageView(imagen);
		icono.setFitWidth(30);
		icono.setFitHeight(30);
		return icono;

	}

	// Button and menu actions

	@FXML
	private void handleQuitGame(ActionEvent Event) {
		Alert alerta = new Alert(Alert.AlertType.INFORMATION);
		alerta.setTitle("Volviendo al menu");
		alerta.setHeaderText(null);
		alerta.setContentText(
				"Se ha cerrado esta partida, gracias por jugar!\nVolverás al menu principal para crear, cargar una partida o cerrar el juego.");

		alerta.showAndWait();

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/pantallaMenu1.fxml"));
			Parent pantallaMenuPartidasRoot = loader.load();

			pantallaMenu controladorPartidas = loader.getController();
			controladorPartidas.mostrarNombreLogin(pingu);

			Scene pantallaMPartidasScene = new Scene(pantallaMenuPartidasRoot);
			// Get the current stage using the event
			MenuItem menuItem = (MenuItem) Event.getSource();
			Window window = menuItem.getParentPopup().getOwnerWindow();
			Stage stage = (Stage) window;
			System.out.println("Cambiando escena...");
			stage.setScene(pantallaMPartidasScene);
			stage.setTitle("Pantalla de Selección de partida");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleDado(ActionEvent event) {
		Random rand = new Random();
		int diceResult = rand.nextInt(6) + 1;

		// Update the Text
		dadoResultText.setText("Ha salido: " + diceResult);

		// Update the position
		moveP1(diceResult);
	}

	public void moveP1(int steps) {
		if (pingu != null) {
			pingu.setPosicion(pingu.getPosicion() + steps);

			if (pingu.getPosicion() >= 50) {
				pingu.setPosicion(49); // 5 columns * 10 rows = 50 cells (index 0 to 49)
			}

			actualizarPosicionBaseDeDatos(pingu, idPartida);
		}

		// Check row and column
		int row = pingu.getPosicion() / COLUMNS;
		int col = pingu.getPosicion() % COLUMNS;

		// Change P1 property to match row and column
		GridPane.setRowIndex(P1, row);
		GridPane.setColumnIndex(P1, col);

		administrarEventos(pingu.getPosicion());
	}

	private void administrarEventos(int posicion) {
		tablero = GuardarConBD.getTableroCargado();
		Evento evento = tablero.getCasillas().get(posicion);
		TipoCasilla tipoCasilla = new TipoCasilla("vacío", 0);
		switch (evento.getIDEvento()) {
		case 2:
			tipoCasilla = new TipoCasilla("Oso", 2);
			tipoCasilla.casillaOso(pingu, this);
			break;
		case 3:
			tipoCasilla = new TipoCasilla("Agujero de Hielo", 3);
			tipoCasilla.casillaAgujeroHielo(pingu, tablero.getCasillas(), this);
			break;
		case 4:
			tipoCasilla = new TipoCasilla("Trineo", 4);
			tipoCasilla.casillaTrineo(pingu, tablero.getCasillas(), this);
			break;
		case 5:
			tipoCasilla = new TipoCasilla("Interrogante", 5);
			tipoCasilla.accionInterrogante(pingu);
			break;
		case 6:
			tipoCasilla = new TipoCasilla("Obtener Pez", 6);
			tipoCasilla.obtenerPescado(pingu);
			break;
		case 7:
			tipoCasilla = new TipoCasilla("Obtener Bolas de Nieve", 7);
			tipoCasilla.obtenerBolasdeNieve(pingu);
			break;
		case 8:
			tipoCasilla = new TipoCasilla("Obtener Dado", 8);
			tipoCasilla.obtenerDadoAleatorio(pingu);
			break;
		case 9:
			tipoCasilla = new TipoCasilla("Línea de meta", 9);
			tipoCasilla.lineaDeMeta(pingu);
			break;

		}
	}

	public void colocarPinguino(int posicion) {
		p1Position = posicion;

		int row = p1Position / COLUMNS;
		int col = p1Position % COLUMNS;

		GridPane.setRowIndex(P1, row);
		GridPane.setColumnIndex(P1, col);

	}

	@FXML
	private void handleRapido(ActionEvent Event) {
		Inventario inv = pingu.getInventario();

		if (inv.getDadosR() <= 0) {
			Alert alerta = new Alert(Alert.AlertType.WARNING);
			alerta.setTitle("Cantidad de dados insuficientes");
			alerta.setHeaderText(null);
			alerta.setContentText("No tienes ningún dado lento disponible");
		} else {
			try {
				Connection con = GuardarConBD.getConexion();
				int idPartida = GuardarConBD.getIdPartidaCargada();
				
				Random r = new Random();
				int valor = r.nextInt(6) + 5;

				// Actualización en la base de datos al usar un dado RÁPIDO
				String sqlUpdatenDados = "UPDATE INVENTARIO SET NUM_DADOESP = NUM_DADOSESP - 1 WHERE idPropietrio = "
						+ pingu.getId() + " AND id_partida = " + idPartida;
				bbdd.update(con, sqlUpdatenDados);

				String sqlUpdateDLentos = "UPDATE INVENTARIO SET NUM_DADOSRAPIDOS = NUM_DADOSRAPIDOS - 1 WHERE idPropietario = "
						+ pingu.getId() + " AND id_partida = " + idPartida;
				bbdd.update(con, sqlUpdateDLentos);
				con.commit();
				
				// Resta al objeto pinguino del inventario 1 dado rápido y 1 dado a la cantidad máxima
				// de dados a almacenar
				
				inv.setDadosR(inv.getDadosR() - 1);
				inv.setDados(inv.getDados() - 1);

				rapido_t.setText("Dado rápido: " + valor);

				pingu.setPosicion(pingu.getPosicion() + valor);
				moveP1(0);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		actualizarPosicionBaseDeDatos(pingu, idPartida);
	}

	@FXML
	private void handleLento(ActionEvent Event) {
		Inventario inv = pingu.getInventario();

		if (inv.getDadosL() <= 0) {
			Alert alerta = new Alert(Alert.AlertType.WARNING);
			alerta.setTitle("Cantidad de dados insuficientes");
			alerta.setHeaderText(null);
			alerta.setContentText("No tienes ningún dado lento en el inventario");
		} else {
			try {
				Connection con = GuardarConBD.getConexion();
				int idPartida = GuardarConBD.getIdPartidaCargada();

				Random r = new Random();
				int valor = r.nextInt(3) + 1;

				// Actualización en la base de datos al usar un dado lento

				String sqlUpdatenDados = "UPDATE INVENTARIO SET NUM_DADOSESP = NUM_DADOSESP - 1 WHERE idPropietrio = "
						+ pingu.getId() + " AND id_partida = " + idPartida;
				bbdd.update(con, sqlUpdatenDados);

				String sqlUpdateDLentos = "UPDATE INVENTARIO SET NUM_DADOSLENTOS = NUM_DADOSLENTOS - 1 WHERE idPropietario = "
						+ pingu.getId() + " AND id_partida = " + idPartida;
				bbdd.update(con, sqlUpdateDLentos);
				con.commit();

				// Resta al objeto pinguino del inventario 1 dado lento y 1 dado a la cantidad
				// máxima
				// de dados a almacenar

				inv.setDadosL(inv.getDadosL() - 1);
				inv.setDados(inv.getDados() - 1);

				lento_t.setText("Dado lento: " + valor);

				pingu.setPosicion(pingu.getPosicion() + valor);
				moveP1(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		actualizarPosicionBaseDeDatos(pingu, idPartida);

	}

	@FXML
	private void handleMostrarInventario(ActionEvent Event) {
		Alert alerta = new Alert(Alert.AlertType.INFORMATION);
		alerta.setHeaderText(null);
		alerta.setTitle("Inventario de " + pingu.getNombre());
		alerta.setContentText("Dados especiales totales: " + pingu.getInventario().getDados() + "\n -Dados lentos: "
				+ pingu.getInventario().getDadosL() + "\n -Dados rápidos: " + pingu.getInventario().getDadosR()
				+ "\nBolas de nieve: " + pingu.getInventario().getBolasDeNieve() + "\nPeces: "
				+ pingu.getInventario().getPeces());
		alerta.showAndWait();

	}

	public void actualizarPosicionBaseDeDatos(Pinguino pingu, int idPartida) {
		try {
			Connection con = GuardarConBD.getConexion();
			con.setAutoCommit(false);
			idPartida = GuardarConBD.getIdPartidaCargada();

			// Actualizar posicion del jugador

			String actualizarPosicion = "UPDATE inventario SET posicion_jugador = " + pingu.getPosicion()
					+ " WHERE idpropietario = " + pingu.getId() + " AND id_partida = " + idPartida;

			bbdd.update(con, actualizarPosicion);
			con.commit();
			System.out.println("Posicion actualizada en la base de datos a: " + pingu.getPosicion());

			System.out.println("ID Partida: " + idPartida);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
