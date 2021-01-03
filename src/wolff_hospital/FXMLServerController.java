/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wolff_hospital;


import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
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
public class FXMLServerController implements Initializable {

    @FXML
    private Label label1;
    private boolean open = false;
    private ServerThreadsClient serverThreadsClient; //we create a reference for accesing different methods

    /**
     * This method creates the server thread. It will start waiting for clients.
     */
    @FXML
    private void handleOpenServer(ActionEvent event) {
        if (!open) {
            label1.setText("Server opened!");
            open = true;
            //We execute a thread that will wait for clients, so UI continous working.
            serverThreadsClient = new ServerThreadsClient();
            new Thread(serverThreadsClient).start();

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
            serverThreadsClient.closeServer();
            open = false;

        } else {
            label1.setText("Server is already closed!");

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
