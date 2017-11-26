package com.harman.traveler.visualizer;
	
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage stage) {
		try {
			StackPane holder = new StackPane();
	        Canvas canvas = new Canvas(200,  300);
	        
	        holder.getChildren().add(canvas);
	        holder.setStyle("-fx-background-color: black");

	        TextField textField1 = new TextField();
	        
			GridPane root = new GridPane();
			
			//Setting size for the pane  
		    //root.setMinSize(400, 200); 
		       
		    //Setting the padding  
		    root.setPadding(new Insets(10, 10, 10, 10)); 
		      
		   //Setting the vertical and horizontal gaps between the columns 
		   root.setVgap(1); 
		   root.setHgap(2);       
		      
		   //Setting the Grid alignment 
		   root.setAlignment(Pos.CENTER);
		   ColumnConstraints column = new ColumnConstraints();
		   column.setPercentWidth(50);
		   root.getColumnConstraints().add(column);

		   column = new ColumnConstraints();
		   column.setPercentWidth(50);
		   root.getColumnConstraints().add(column);
		   
		   RowConstraints row = new RowConstraints();
		   row.setPercentHeight(100);
		   root.getRowConstraints().add(row);
		   
		   root.add(holder, 0, 0);
		   root.add(textField1, 1, 0);
		   
		      //Creating a scene object 
		      Scene scene = new Scene(root, 600, 300);  
		      
		      //Setting title to the Stage 
		      stage.setTitle("Canvas example"); 
		      stage.setMinHeight(300);
		      stage.setMinWidth(600);
		         
		      //Adding scene to the stage 
		      stage.setScene(scene);
		      
		      //Displaying the contents of the stage 
		      stage.show(); 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
