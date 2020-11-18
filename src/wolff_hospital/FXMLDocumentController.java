/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wolff_hospital;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 *
 * @author ALVARO
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label1;
    
    @FXML
    private void handleOpenServer(ActionEvent event) {
        label1.setText("Server opened!");
    }
    
    @FXML
    private void handleCloseServer(ActionEvent event) {
        label1.setText("Server closed!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
