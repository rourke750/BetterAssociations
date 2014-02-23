package com.sandislandserv.rourke750.Gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class MainGuiWindow extends Application{
	
	public void start(Stage stage) throws Exception {
	    Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));
	    GuiController control = new GuiController();
	    Scene scene = new Scene(root, 700, 500);
	    stage.setTitle("Better Associations GUI");
	    stage.setScene(scene);
	    stage.show();
	    }
		
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
