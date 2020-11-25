/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wolff_hospital;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 *
 * @author ALVARO
 */
public class FXMLServerController implements Initializable {

    @FXML
    private Label label1;
   // ServerSocket serverSocket = null;
    private boolean open = false;

    @FXML
    private void handleOpenServer(ActionEvent event) {
        if (!open) {
            //try {
                //Create a service that is waiting in port 9000
              //  serverSocket = new ServerSocket(9000);
                label1.setText("Server opened!");
                open = true;
                //We execute a thread that will wait for clients, so UI continous working.
                new Thread(new ServerThreadsClient()).start();
                //Read from the client
           /* } catch (IOException ex) {
                Logger.getLogger(FXMLServerController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                releaseResourcesServer(serverSocket);
            }*/
        } else {
            label1.setText("Server is already opened!");

        }
    }

    private static void releaseResources(InputStream inputStream, Socket socket) {

        try {
            inputStream.close();
        } catch (IOException ex) {
        }

        try {
            socket.close();
        } catch (IOException ex) {
        }
    }

  /*  @FXML
    private void handleCloseServer(ActionEvent event) {
        if (open) {
            label1.setText("Server closed!");
            try {
                serverSocket.close();
                open = false;
            } catch (IOException ex) {
            }
        } else {
            label1.setText("Server is already closed!");

        }
    }*/

    private static void releaseResourcesServer(ServerSocket serverSocket) {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(FXMLServerController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    /*    Pane paneChart;

    @FXML
    public void showECG(int[] ECG_data) {
        XYChart.Series series = new XYChart.Series();

        //populating the series with data
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (int i = 0; i < ECG_data.length; i++) {
            series.getData().add(new XYChart.Data(i, ECG_data[i]));
            if (min > ECG_data[i]) {
                min = ECG_data[i];
            }
            if (max < ECG_data[i]) {
                max = ECG_data[i];
            }
        }

        paneChart.getChildren().clear();

        final NumberAxis xAxis = new NumberAxis(0, ECG_data.length, 1);
        final NumberAxis yAxis = new NumberAxis(min - 5, max + 5, 0.1);//lower, upper, tick
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.getXAxis().setLabel("Time");
        lineChart.getYAxis().setLabel("Amplitude");

        //creating the chart
        lineChart.setTitle("ECG");
        //defining a series
        lineChart.getData().add(series);
        paneChart.getChildren().add(lineChart);
        System.out.println("Shown");
    }*/
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
