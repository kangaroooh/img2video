package javafxapplication1;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.text.*;

public class Main extends Application {
    public static TextField outputNameTextField;
    public static TextField framerateTextField;
    public static Text processOrFinish;
    public static String filePath;
    public static int frameRate;
    
    public static void main(String[] args) {
        Application.launch(args);
    }
    
    public static void runExec(String filePath){
        try {
            frameRate = Integer.parseInt(framerateTextField.getText());
            processOrFinish.setText("Processing");

            StringBuilder sb = new StringBuilder();
            
            sb.append("avconv -framerate ");
            sb.append(Integer.toString(frameRate));
            sb.append(" -b 65536k -i ");
            
            /*
            // old style
            sb.append("avconv -framerate 25 -b 65536k -i ");
            */
            
            // to add the sequencial images format 
            // THIS IS IMPORTANT IF TO SET THE FORMAT CORRECTLY
            sb.append(filePath + "out_%d.jpg ");
            
            // to add the output
            sb.append(filePath + outputNameTextField.getText());
            
            System.out.println(sb.toString());
            Process process = Runtime.getRuntime().exec(sb.toString());
            process.waitFor();
            // set processing text back
            processOrFinish.setText("Finish");
        } catch (IOException ex) {
            processOrFinish.setText("IOException");
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            processOrFinish.setText("InterruptedException");
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drag n Drop movie converter WOW");
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 400, 250);
        Group root = new Group();
              
        Text scenetitle = new Text("Drag & Drop here");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);        

        outputNameTextField = new TextField();
        outputNameTextField.setPromptText("Output File Name *.mov");
        grid.add(outputNameTextField, 1, 1);
        
        framerateTextField = new TextField();
        framerateTextField.setText("25");
        grid.add(framerateTextField, 1, 2);
        
        Button genBtn = new Button("Generate");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(genBtn);
        grid.add(hbBtn, 1, 3);

        processOrFinish = new Text("");
        processOrFinish.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(processOrFinish, 1,3);    
        
        genBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                runExec(filePath+'/');
            }
        });

        scene.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
                processOrFinish.setText("Please Drop here");
            }
        });
   
        // Dropping over surface
        scene.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    filePath = null;
                    for (File file:db.getFiles()) {
                        filePath = file.getAbsolutePath();
                        scenetitle.setText("Destination = " + filePath);                   
                    }
                }
                event.setDropCompleted(success);
                event.consume();
                processOrFinish.setText("");
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
