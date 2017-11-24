package application;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

public class HBoxExample extends Application {   
	   @Override 
	   public void start(Stage stage) {       
	      //creating a text field   
	      TextField textField = new TextField();       
	      
	      //Creating the play button 
	      Button playButton = new Button("Play");       
	      
	      //Creating the stop button 
	      Button stopButton = new Button("stop"); 
	      
	      //Instantiating the HBox class  
	      HBox hbox = new HBox();    
	      
	      //Setting the space between the nodes of a HBox pane 
	      hbox.setSpacing(100);
	      hbox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
	    	        + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
	    	        + "-fx-border-radius: 5;" + "-fx-border-color: blue;");	      
	      //Setting the margin to the nodes 
	      HBox.setMargin(textField, new Insets(20, 20, 20, 20)); 
	      HBox.setMargin(playButton, new Insets(20, 20, 20, 20)); 
	      HBox.setMargin(stopButton, new Insets(20, 20, 20, 20));  
	      
	      //retrieving the observable list of the HBox 
	      ObservableList<Node> list = hbox.getChildren();  
	      
	      //Adding all the nodes to the observable list (HBox) 
	      list.addAll(textField, playButton, stopButton);       
	      
	      //Creating a scene object
	      Scene scene = new Scene(hbox);  
	      
	      //Setting title to the Stage 
	      stage.setTitle("Hbox Example"); 
	         
	      //Adding scene to the stage 
	      stage.setScene(scene); 
	         
	      //Displaying the contents of the stage 
	      stage.show(); 
	   } 
	   public static void main(String args[]){ 
	      launch(args); 
	   } 
}
