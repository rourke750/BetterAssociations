package com.sandislandserv.rourke750.Gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.BaseValues;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class GuiController {
	
	@FXML private TextField username;
	@FXML private TextField domain;
	@FXML private PasswordField password;
	@FXML private AnchorPane pane;
	@FXML private Button login;
	@FXML private TableView<Person> listofPlayers;
	@FXML private Label badLogin;
	@FXML private TableColumn<Person, String> columnUUID;
	@FXML private TableColumn<Person, String> columnPlayer;

	private ObservableList<Person> data = FXCollections.observableArrayList();
    public GuiController() {
    }
    
    public AnchorPane getPane(){
    	return pane;
    }
    
    @FXML
    public void Login(){
    	data.clear();
    	badLogin.setVisible(false);
    	System.out.print(username.getText());
    	System.out.print(password.getText());
    	String players = getPlayers(username.getText().toString(), password.getText().toString());
    	String[] player = players.split(" ");
    	for (int x = 0; x < player.length; x=x+2){
    		System.out.println(player[x] + " " + player[x+1]); // makes sure that both values are populated [for debugging]
    		data.add(new Person(player[x], player[x+1])); 
    	}
    	columnUUID.setCellValueFactory(
			    new PropertyValueFactory<Person,String>("different")
		    );
    	columnPlayer.setCellValueFactory(
			    new PropertyValueFactory<Person,String>("player")
			);
		listofPlayers.getItems().setAll(data);
    }
    
    public String getPlayers(String username, String password){
    	StringBuilder builder = new StringBuilder();
    	try {
			@SuppressWarnings("resource")
			Socket socket = new Socket(domain.getText().toString(), 25549);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(username + " " + password);
			if (input.readLine().equals("BAD")) {
				badLogin.setVisible(true);
				return null;
			}
			out.println("Get All Player Info");
	    	String line;
			while ((line = input.readLine()) != null){
				if (line.equals("END")) break;
				builder.append(line + " ");
			}
			out.println("END");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return builder.toString();
    }
}
