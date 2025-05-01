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
	public void handleNewGame(ActionEvent Event) {
		try {

			Connection con = GuardarConBD.getConexion();
			Pinguino pingu = GuardarConBD.getPinguino();
			// Crear tablero y obtener casillas

			Tablero tablero = new Tablero(50);
			ArrayList<Evento> casillas = tablero.creacionTablero();
			System.out.println("Casillas creadas: " + casillas.size());
			// Conversión compatible del tablero para la base de datos
			StringBuilder casillasBD = new StringBuilder("ARRAYTAULELL(");
			for (int i = 0; i < casillas.size(); i++) {
				casillasBD.append("'").append(casillas.get(i).getInfoEvento()).append("'");
				if (i < casillas.size() - 1) {
					casillasBD.append(", ");
				}
			}

			casillasBD.append(")");

			// Insert inventario + obtención de ID de la partida

			String insertInventario = "INSERT INTO inventario (id_inventario, num_peces, num_dadosesp, num_dadoslentos, num_dadosrapidos, num_bolasnieve, posicion_jugador, idpropietario) "
					+ "VALUES (idInventarios.nextval, 0, 0, 0, 0, 0, 0, " + pingu.getId() + ")";

			bbdd.insert(con, insertInventario);
			
			int idInventario = 0;
			ResultSet rsInv = bbdd.select(con, "SELECT idInventarios.currval FROM dual");
			if (rsInv.next()) {
				idInventario = rsInv.getInt(1);
			}
			rsInv.close();
			
			//Insert información de la partida + obtención de ID de la partida
			
			String insertPartida = "INSERT INTO partida (idpartida, fecha, tablero, estado, idInventario, idCreador) " +
                    "VALUES (idPartidas.nextval, SYSDATE, " + casillasBD.toString() + ", 'en curso', " + idInventario + ", " + pingu.getId() + ")";
			
			bbdd.insert(con, insertPartida);
			
			int idPartida = 0;
			ResultSet rsPartida = bbdd.select(con, "SELECT idPartidas.currval FROM dual");
			if (rsPartida.next()) {
				idPartida = rsPartida.getInt(1);
			}
			rsPartida.close();
			
			//Actualizar la tabla inventario con la id de la partida correspondiente
			
			String actualizarInventario = "UPDATE inventario SET id_partida = " + idPartida + " WHERE id_inventario = " + idInventario;
			bbdd.update(con, actualizarInventario);
			
			
			
			// Carga de la ventana del tablero

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/pantallaJuego.fxml"));
			Parent pantallaJuegoRoot = loader.load();
			pantallaJuegoController controladorJuego = loader.getController();
			Scene pantallaJuego = new Scene(pantallaJuegoRoot);
			Stage stage = (Stage) ((Node) Event.getSource()).getScene().getWindow();
			stage.setScene(pantallaJuego);
			stage.setTitle("Pantalla del tablero");
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
