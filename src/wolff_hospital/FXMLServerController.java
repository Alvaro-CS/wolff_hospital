/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wolff_hospital;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
/**
 *
 * @author ALVARO
 */
public class FXMLServerController implements Initializable {

    @FXML
    private Label label1;
    public static volatile boolean open = false;
    private ServerThreadUI serverThreadUI; //we create a reference for accesing different methods
    public static volatile boolean verify_server = false;

    /**
     * This method creates the server thread. It will start waiting for clients.
     */
    
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLServer.fxml"));

    
    
    
    @FXML
    private void handleOpenServer(ActionEvent event) throws IOException {
     
        if (!open) {
            label1.setText("Server opened!");
            open = true;
            
            //We execute a thread that will wait for clients, so UI continous working.
            serverThreadUI = new ServerThreadUI();
            new Thread(serverThreadUI).start();
            
            
            if(verify_server==false){
        
            Parent ViewParent = loader.load();
            verify_server=true;
            

            //Cargamos el controlador
            FXMLServerController controller = loader.getController();
          
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            
            window.setOnCloseRequest(e->{
                try {
                    controller.closeWindows(serverThreadUI);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLServerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            }else{
                System.out.println("Server is opened again");
                label1.setText("Server is opened again");
            }
            
    
        } else {
              
            label1.setText("Server is already opened!");
           
        }
       
      
    }


    /**
     * This method closes the server thread.
     */
    @FXML
    private void handleCloseServer(ActionEvent event) {
        
        if (open) {
            label1.setText("Server closed!");
            serverThreadUI.closeServer();
            open = false;

        } else {
            label1.setText("Server is already closed!");

        }
        
        
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    
    private void closeWindows(ServerThreadUI s) throws IOException {
        
            try{
  
                 s.closeServer();
                 open=false;
                 
            }catch(NullPointerException e){
                System.out.println("Exception caught");
            }

 
}


    
}
