package vista;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.sql.*;
import java.util.ArrayList;
import javafx.collections.FXCollections;

import javax.naming.spi.DirStateFactory.Result;

import controlador.*;
import modelo.*;

public class pantallaPrincipalController {

	@FXML
	private MenuItem newGame;
	@FXML
	private MenuItem saveGame;
	@FXML
	private MenuItem loadGame;
	@FXML
	private MenuItem quitGame;

	@FXML
	private TextField userField;
	@FXML
	private PasswordField passField;
	@FXML
	private ComboBox<String> colorField;
	@FXML
	private Button loginButton;
	@FXML
	private Button registerButton;
	private Connection con;

	private Pinguino pingu;
	private Tablero tablero;
	ArrayList<Evento> casillas;

	@FXML
	public void initialize() {
		colorField.setItems(
				FXCollections.observableArrayList("Amarillo", "Azul", "Morado", "Naranja", "Rojo", "Rosa", "Verde"));

		colorField.setOnAction(event -> {
			String selectedColor = colorField.getValue();
			if (selectedColor != null) {
				String cssColor = mapColorToCSS(selectedColor);
				colorField.setStyle("-fx-background-color: " + cssColor + "; -fx-text-fill: white;");
			}
		});

		System.out.println("pantallaPrincipalController initialized");
	}

	private String mapColorToCSS(String color) {
		switch (color.toLowerCase()) {
		case "amarillo":
			return "yellow";
		case "azul":
			return "#87CEFA";
		case "morado":
			return "#D8BFD8";
		case "naranja":
			return "orange";
		case "rojo":
			return "#F08080";
		case "rosa":
			return "pink";
		case "verde":
			return "#90EE90";
		default:
			return "white";
		}
	}

	@FXML
	private void handleNewGame() {
		System.out.println("New Game clicked");
		// TODO
	}

	@FXML
	private void handleSaveGame() {
		System.out.println("Save Game clicked");
		// TODO
	}

	@FXML
	private void handleLoadGame() {
		System.out.println("Load Game clicked");
		// TODO
	}

	@FXML
	private void handleQuitGame() {
		Alert alerta = new Alert(Alert.AlertType.INFORMATION);
		alerta.setTitle("Cierre del juego");
		alerta.setHeaderText(null);
		alerta.setContentText("Se va a cerrar el juego, gracias por jugar!");
		alerta.showAndWait();

		Platform.exit();
	}

	@FXML
	private void handleLogin(ActionEvent event) {
		String usuario = userField.getText().trim();
		String contrasenya = passField.getText().trim();
		GuardarConBD.setNickname(usuario);

		System.out.println("Login pressed: " + usuario + " / " + contrasenya);

		// Basic check (just for demo, replace with real login logic)
		if (!usuario.isEmpty() && !contrasenya.isEmpty()) {
			try {
				Connection con = GuardarConBD.getConexion();

				String sqlSelect = "SELECT * FROM JUGADOR WHERE NICKNAME = '" + usuario + "' AND CONTRASENYA = '"
						+ contrasenya + "'";
				ResultSet rs = bbdd.select(con, sqlSelect);

				if (rs.next()) {
					int id = rs.getInt("ID_JUGADOR");
					String nickname = rs.getString("NICKNAME");
					String color = rs.getString("COLOR");

					Pinguino pingu = new Pinguino(nickname, color, id, 0, null, "Jugador");
					GuardarConBD.setPinguino(pingu);
					// Carga la pantalla para seleccionar partida nueva, existente o cerrar/salir
					// del juego

					FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/pantallaMenu1.fxml"));
					Parent pantallaMenuPartidasRoot = loader.load();

					// Carga del controlador del menú de partidas
					pantallaMenu controladorPartidas = loader.getController();
					controladorPartidas.mostrarNombreLogin(pingu);

					// Scene pantallaJuegoScene = new Scene(pantallaJuegoRoot);
					Scene pantallaMPartidasScene = new Scene(pantallaMenuPartidasRoot);
					// Get the current stage using the event
					Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					// stage.setScene(pantallaJuegoScene);
					stage.setScene(pantallaMPartidasScene);
					stage.setTitle("Pantalla de Selección de partida");
				} else {
					System.out.println("Usuario o contraseña incorrectos");
					mostrarAlerta("Error", "El usuario o contraseña son incorrectos");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(" Porfavor, introduzca el usuario y contraseña");
		}
	}

	@FXML
	private void handleRegister(ActionEvent event) {
		String usuario = userField.getText();
		String contrasenya = passField.getText();
		String color = colorField.getValue();

		if (usuario.isEmpty() || contrasenya.isEmpty() || color.isEmpty()) {
			mostrarAlerta("Error", "Por favor, rellena todos los campos para hacer el registro.");

		} else if (!usuario.isEmpty() && !contrasenya.isEmpty() && !color.isEmpty()) {
			try {
				Connection con = GuardarConBD.getConexion();

				if (nUsuarioDisponible(con, usuario)) {
					String insert = "INSERT INTO JUGADOR (ID_JUGADOR, NICKNAME, CONTRASENYA, NUM_PARTIDAS_JUGADAS, COLOR) "
							+ "VALUES (idJugadores.NEXTVAL, '" + usuario + "', '" + contrasenya + "', 0, '" + color
							+ "')";
					bbdd.insert(con, insert);

					int id = obtenerID(con, usuario);

					Pinguino pingu = new Pinguino(usuario, color, id, 0, null, "Jugador");

					mostrarAlerta("Éxito en el registro", "Se ha registrado correctamente! Bienvenido!");
				} else {
					mostrarAlerta("Error", "El nombre de usuario introducido ya está en uso");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private int obtenerID(Connection con, String usuario) {
		int id = 0;
		try {
			String sqlSelect = "SELECT ID_JUGADOR FROM JUGADOR WHERE NICKNAME = '" + usuario + "'";
			ResultSet rs = bbdd.select(con, sqlSelect);
			if (rs.next()) {
				id = rs.getInt("ID_JUGADOR");
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("Error al obtener la ID del jugador: " + e.getMessage());
		}
		return id;
	}

	private boolean nUsuarioDisponible(Connection con, String usuario) {

		String consulta = "SELECT COUNT(*) FROM JUGADOR WHERE NICKNAME = '" + usuario + "'";
		try {
			ResultSet rs = bbdd.select(con, consulta);
			if (rs.next()) {
				return rs.getInt(1) == 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void mostrarAlerta(String titulo, String msg) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(titulo);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	public String asignarColorCirculo() {
		try {
			Connection con = GuardarConBD.getConexion();
			String nombre = GuardarConBD.getNickname();

			String sql = "SELECT color FROM jugador WHERE nickname = '" + nombre + "'";
	        ResultSet rs = bbdd.select(con, sql);
	        if (rs.next()) {
	            String color = rs.getString("COLOR");

	            switch (color.toLowerCase()) {
	                case "amarillo":
	                    return "#FFFF00";
	                case "azul":
	                    return "#0000FF";
	                case "morado":
	                    return "#572364";
	                case "naranja":
	                    return "#FFA500";
	                case "rojo":
	                    return "#F80000";
	                case "rosa":
	                    return "#FF0080";
	                case "verde":
	                    return "#008F39";
	                default:
	                    return "#FFFFFF";
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return "#FFFFFF";
	}

	}

