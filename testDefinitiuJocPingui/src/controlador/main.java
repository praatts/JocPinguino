package controlador;
import java.sql.Connection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import controlador.*;
public class main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		//System.out.println(getClass().getResource("/pantallaPrincipal.fxml"));
		Connection con = bbdd.conectarBaseDatos();
		GuardarConBD.setConexion(con);
		
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/pantallaPrincipal.fxml"));
	    Parent root = loader.load();
	    Scene scene = new Scene(root);
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("El Juego del Pingüino");
	    primaryStage.show();
	}
    public static void main(String[] args) {
        launch(args);
    }
}
