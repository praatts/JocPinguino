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
			//Crear tablero y obtener casillas
			
			Tablero tablero = new Tablero(50);
			ArrayList<Evento> casillas = tablero.creacionTablero();
			System.out.println("Casillas creadas: " + casillas.size());
			//Conversión compatible del tablero para la base de datos
			StringBuilder casillasBD = new StringBuilder("ARRAYTAULELL(");
			for (int i = 0; i < casillas.size(); i++) {
				casillasBD.append("'").append(casillas.get(i).getInfoEvento()).append("'");
				if (i < casillas.size() - 1) {
					casillasBD.append(", ");
				}
			}
			
			casillasBD.append(")");
			
			
			
			//Insert de inventario e información de la partida a la base de datos
			
			String insertPartida = "INSERT INTO partida (idpartida, fecha, tablero, estado, idInventario, idCreador) "
			        + "VALUES (idPartidas.nextval, SYSDATE, " + casillasBD + ", 'en curso', NULL, " + pingu.getId() + ")";

			bbdd.insert(con, insertPartida);
			
			
			String selectID = "SELECT idPartidas.currval FROM dual";
			ResultSet rs = bbdd.select(con, selectID);
			int idPartida = 0;
			if (rs.next()) {
				idPartida = rs.getInt("currval");
			}
			
			String insertInventario = "INSERT INTO inventario (id_inventario, num_peces, num_dadosesp, num_dadoslentos, num_dadosrapidos, num_bolasnieve, posicion_jugador, idpropietario, id_partida) "
			        + "VALUES (idInventarios.nextval, 0, 0, 0, 0, 0, 0, " + pingu.getId() + ", " + idPartida + ")"; 
			
			bbdd.insert(con, insertInventario);

		
			
			//Carga de la ventana del tablero
			
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
