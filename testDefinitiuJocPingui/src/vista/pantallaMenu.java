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
			con.setAutoCommit(false);
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

					String updatePJugadas = "UPDATE JUGADOR SET NUM_PARTIDAS_JUGADAS = NUM_PARTIDAS_JUGADAS + 1 WHERE id_Jugador = "
							+ pingu.getId();
					bbdd.update(con, updatePJugadas);
					con.commit();

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
		case "Moto de nieve":
			evt = new Evento(10, "Moto de nieve");
			break;
		case "Suelo Quebradizo":
			evt = new Evento(11, "Suelo Quebradizo");
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
			pingu.setPosicion(0);
			controladorJuego.tablero = tablero;
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

			String updatePJugadas = "UPDATE JUGADOR SET NUM_PARTIDAS_JUGADAS = NUM_PARTIDAS_JUGADAS + 1 WHERE id_Jugador = "
					+ pingu.getId();
			bbdd.update(con, updatePJugadas);
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

	@FXML
	public void handleMostrarReglas(ActionEvent Event) {

		Alert alerta = new Alert(AlertType.INFORMATION);
		alerta.setTitle("COMO JUGAR / REGLAS DEL JUEGO");
		alerta.setHeaderText(null);

		String reglas = "¡BIENVENIDO AL JUEGO DEL PINGUINO, AQUÍ APRENDERÁS A COMO JUGAR Y LAS REGLAS DEL JUEGO!\n\n1. OBJETIVO PRINCIPAL\n -El objetivo principal del juego es llegar a la línea de meta"
				+ " (representada con una bandera a cuadros) evitando distintos obstaculos como un Oso o Agujeros de hielo en el tablero, pero también cuentas con ayudas como la obtención de pescados o trineos.\n\n"
				+ "2. EVENTOS\n -Los obstaculos mencionados en el apartado 1 se representan en el juego como eventos, al caer sobre ellos se activaran y ocurrirá dicho evento, sus funciones son las siguietes:\n "
				+ " 1. OSO:\n Representado con el icono de un oso.\n Tendrás un encuentro con un oso, donde este te devolverá al inicio del tablero, puedes sobornarlo con 2 pescados para no ser penalizado.\n"
				+ "	2. AGUJERO DE HIELO:\n Representado con un icono de un agujero azul.\n Si caes en uno de ellos serás enviado al anterior agujreo de hielo que se encuentre en el tablero, en caso de no"
				+ " haber ningúno anterior, no serás penalizado.\n"
				+ "	3. TRINEO:\n Representado con el icono de un trineo.\n Tendrás la oportunidad de avanzar inmediatamente hasta el siguiente trineo que haya en el tablero, en caso de no haber ningún trineo posterior"
				+ " tu posición no cambiará y actuará como una casilla normal.\n"
				+ " 4. OBTENER BOLAS DE NIEVE:\n Representado con el icono de tres bolas de nieve.\n En esta casilla obtendrás aleatoriamente entre 1 a 3 bolas de nieve y puedes almacenar hasta un máximo de 6 bolas de nieve"
				+ ", estas sirven para retroceder a la foca o jugadores (únicamente"
				+ " en modo multijugador, en individual no tienen ningúna utilidad.\n"
				+ " 5. OBTENER PEZ:\n Representado con el icono de un pez.\n En esta casilla obtendrás un pez, podrás almacenar hasta 2 peces, estos podrás usarlos para sobornar al oso y no ser penalizado.\n"
				+ " 6. OBTENER DADO ALEATORIO:\n Representado con el icono de 2 dados.\n En esta casilla tendrás la probabilidad de conseguir un dado rápido (poder avanzar de 5 a 10 casillas) o un dado lento (poder avanzar"
				+ " de 1 a 3 casillas), su probabilidad es de 30/70% de obtención (Rápido 30% || Lento 40%), podrás almacenar hasta 3 dados de estos conjuntamente.\n"
				+ " 7. EVENTO ALEATORIO:\n Representado con el icono de un signo de interrogación. \n En esta casilla se activará un evento aleatorio (entre ellos: Obtener Pez, Obtener Bolas de Nieve y Obtener Dado Especial)."
				+ "\n\nTodo tu progreso será almacenado automáticamente con el sistema de autoguardado en nuestra base de datos, desde este menú podrás crear una partida completamente nueva o una que hayas jugado"
				+ " anteriormente y quieras retomarla, también podrás ver el ranking de jugadores con más partidas jugadas. Por último podrás cerrar sesión.\n\n *Cuando te encuentres dentro de la ventana de juego, pulsando en el"
				+ " desplegable superior izquierdo podrás pulsar en la opción 'Salir' para volver a este menú.";

		TextArea area = new TextArea(reglas);
		area.setWrapText(true);
		area.setEditable(false);

		alerta.getDialogPane().setContent(area);
		alerta.setResizable(true);
		alerta.setHeaderText(null);
		alerta.showAndWait();

	}

	@FXML
	public void handleMostrarRankingPartidas(ActionEvent Event) {
		try {
			Connection con = GuardarConBD.getConexion();
			
			String sqlConsulta = "SELECT nickname, num_partidas_jugadas FROM JUGADOR ORDER BY num_partidas_jugadas DESC";
			ResultSet rs = bbdd.select(con, sqlConsulta);
			
			String base = "\n";
			while (rs.next()) {
				String nombre = rs.getString("NICKNAME");
				int partidasJugadas = rs.getInt("NUM_PARTIDAS_JUGADAS");
				
				base += nombre + " -> " + partidasJugadas + " partidas jugadas\n";
				
			}
			
			rs.close();
			
			Alert alerta = new Alert(AlertType.INFORMATION);
			alerta.setTitle("Ranking de jugadores");
			alerta.setHeaderText("Ranking de jugadores con más partidas jugadas");
			alerta.setContentText(base);
			alerta.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	

}
