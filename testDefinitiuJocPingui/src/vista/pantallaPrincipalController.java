package vista;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

import javax.naming.spi.DirStateFactory.Result;

import controlador.*;
import modelo.*;

public class pantallaPrincipalController {

    @FXML private MenuItem newGame;
    @FXML private MenuItem saveGame;
    @FXML private MenuItem loadGame;
    @FXML private MenuItem quitGame;

    @FXML private TextField userField;
    @FXML private PasswordField passField;
    @FXML private ComboBox<String> colorField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    	  private Connection con;

    @FXML
    private void initialize() {
        // This method is called automatically after the FXML is loaded
        // You can set initial values or add listeners here
    	 colorField.setItems(FXCollections.observableArrayList(
   	          "Amarillo", "Azul", "Morado", "Naranja", "Rojo", "Rosa", "Verde"
   	      ));

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
	        case "amarillo": return "yellow";            
	        case "azul": return "#87CEFA";               
	        case "morado": return "#D8BFD8";             
	        case "naranja": return "orange";             
	        case "rojo": return "#F08080";               
	        case "rosa": return "pink";                  
	        case "verde": return "#90EE90";              
	        default: return "white";
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
        System.out.println("Quit Game clicked");
        // TODO
        System.exit(0);
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String usuario = userField.getText().trim();
        String contrasenya = passField.getText().trim();

        System.out.println("Login pressed: " + usuario + " / " + contrasenya);

        // Basic check (just for demo, replace with real login logic)
        if (!usuario.isEmpty() && !contrasenya.isEmpty()) {
            try {
            	Connection con = GuardarConBD.getConexion();
        		     	
            	String sqlSelect = "SELECT * FROM JUGADOR WHERE NICKNAME = '" + usuario + "' AND CONTRASENYA = '" + contrasenya + "'";
            	ResultSet rs = bbdd.select(con, sqlSelect);
            	
            	if (rs.next()) {
            		int id = rs.getInt("ID_JUGADOR");
            		String nickname = rs.getString("NICKNAME");
            		String color = rs.getString("COLOR");
            		Inventario inventario = new Inventario(0, 0, 0, 0, 0);
                    Pinguino pingu = new Pinguino(nickname, color, id, 0, inventario, "Jugador");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/pantallaJuego.fxml"));
                    Parent pantallaJuegoRoot = loader.load();
                    pantallaJuegoController controladorJuego = loader.getController();
                    
                    controladorJuego.setPinguino(pingu);
                    
                    
                    Scene pantallaJuegoScene = new Scene(pantallaJuegoRoot);

                    // Get the current stage using the event
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(pantallaJuegoScene);
                    stage.setTitle("Pantalla de Juego");
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
        
        if (!usuario.isEmpty() && !contrasenya.isEmpty() && !color.isEmpty()) {
        	try {
        		Connection con = GuardarConBD.getConexion();
        		
        		if (nUsuarioDisponible(con, usuario)) {
        			String insert = "INSERT INTO JUGADOR (ID_JUGADOR, NICKNAME, CONTRASENYA, NUM_PARTIDAS_JUGADAS, COLOR) "
        					+ "VALUES (idJugadores.NEXTVAL, '" + usuario + "', '" + contrasenya + "', 0, '" + color + "')";
        		bbdd.insert(con, insert);
        		
        		int id = obtenerID(con, usuario);
        		
        		Inventario inventario = new Inventario(0, 0, 0, 0, 0);
        		String tipo = "Jugador";
        		int posicion = 0;
        		
        		Pinguino pingu = new Pinguino (usuario, color, id, posicion, inventario, tipo);
        		
        		mostrarAlerta("Éxito en el registro", "Se ha registrado correctamente! Bienvenido!");
        		} else {
        			mostrarAlerta("Error", "El nombre de usuario introducido ya está en uso");
        		}
        	} catch (Exception e) {
				e.printStackTrace();
				mostrarAlerta("", "Por favor, rellena todos los campos");
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
    
    private boolean nUsuarioDisponible (Connection con, String usuario) {
    	String consulta = "SELECT COUNT(*) FROM JUGADOR WHERE NICKNAME = '" + usuario + "'";
    	try {
    		ResultSet rs = bbdd.select(con, consulta);
    		if (rs != null && rs.next()) {
    			return rs.getInt(1) == 0;
    		}
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return false;
    } 
    
    private void mostrarAlerta (String titulo, String msg) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle(titulo);
    	alert.setHeaderText(null);
    	alert.setContentText(msg);
    	alert.showAndWait();
    }
    
}