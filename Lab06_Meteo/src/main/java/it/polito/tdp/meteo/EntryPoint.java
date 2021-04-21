package it.polito.tdp.meteo;

import javafx.application.Application;
import static javafx.application.Application.launch;

import it.polito.tdp.meteo.model.Model;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class EntryPoint extends Application {

    @Override
    public void start(Stage stage) throws Exception {
     
        
    	FXMLController controller;
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        controller = loader.getController();
        
        Model model = new Model();
        controller.setModel(model);
       
        scene.getStylesheets().add("/styles/Styles.css");       
        
        stage.setTitle("Meteo");
        stage.setScene(scene);
        stage.show();
        
        
    }

    public static void main(String[] args) {
        launch(args);
    }

}
