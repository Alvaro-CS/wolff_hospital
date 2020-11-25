/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wolff_hospital;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;

public class ServerThreadsClient implements Runnable {
    ServerSocket serverSocket;

    public ServerThreadsClient() {
    }
    public ServerThreadsClient(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            serverSocket= new ServerSocket(9000);
            
            while(true){
                try {
                    Socket socket = serverSocket.accept();
                    
                    InputStream inputStream = null;
                    ObjectInputStream objectInputStream = null;
                    
                    try {
                        inputStream = socket.getInputStream();
                        objectInputStream = new ObjectInputStream(inputStream);
                        Object tmp;
                        while ((tmp = objectInputStream.readObject()) != null) {
                            int[] ecg_data = (int[]) tmp;
                            showECG(ecg_data);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Client closed");
                    } finally {
                        releaseResourcesClient(objectInputStream, socket);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
@FXML
Pane paneChart;
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
    }
    private static void releaseResourcesClient(ObjectInputStream objectInputStream, Socket socket) {
        try {
            objectInputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(FXMLServerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(FXMLServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
