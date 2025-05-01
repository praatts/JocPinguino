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
			
			
			
			//Coger la id de la partida correspondiente de la secuencia.
			ResultSet rs = bbdd.select(con, "SELECT idPartidas.nextval FROM dual");
			rs.next();
			int idPartida = rs.getInt(1);
			
			//Coger la id de inventario correspondiente de la secuencia.
			
			ResultSet rsInventario = bbdd.select(con, "SELECT idInventarios.nextval FROM dual");
			rsInventario.next();
			int idInventario = rsInventario.getInt(1);
			
			//Insert del inventario a la base de datos
			
			String insertInventario = "INSERT INTO (id_inventario, num_peces, num_dadosesp, num_dadoslentos, num_dadosrapidos, num_bolasnieve, posicion_jugador, idpropietario, id_partida" + 
			"VALUES (" + idInventario + ", 0, 0, 0, 0, 0, 0, " + pingu.getId() + ", " + idPartida + ")"; 
			
			bbdd.insert(con, insertInventario);
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/pantallaJuego.fxml"));
			Parent pantallaJuegoRoot = loader.load();
			pantallaJuegoController controladorJuego = loader.getController();
			controladorJuego.setPinguino(pingu);
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
