/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wolff_hospital;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

/**
 *
 * @author ALVARO
 */
public class FXMLServerPassController implements Initializable {
    
    @FXML 
    private Label errorLabel;
    @FXML
    private PasswordField passwordField;
    
    private final String password="TELEMEDICINERULES";
    
    /**
     *This method logins the patient
     * @param event
     * @throws java.io.IOException
     */
    public void enterButtonOnAction(ActionEvent event) throws IOException {

        if (!passwordField.getText().isEmpty() && passwordField.getText().equals(password)) {
            openServerMenu(event);

        } else {
            //if Fields are empty
            errorLabel.setText("Please enter the correct password.");
        }
    }
    /**
     * This method opens the main menu for the server
     *
     * @param event
     * @throws IOException
     */
    public void openServerMenu(ActionEvent event) throws IOException {
        Parent registrationViewParent = FXMLLoader.load(getClass().getResource("FXMLServer.fxml"));
        Scene registrationViewScene = new Scene(registrationViewParent);
        //this line gets the Stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(registrationViewScene);
        window.centerOnScreen();
        window.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
