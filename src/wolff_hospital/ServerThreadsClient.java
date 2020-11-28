/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wolff_hospital;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import static java.lang.System.exit;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;

/**
 *
 * @author ALVARO
 */
public class ServerThreadsClient implements Runnable {

    ServerSocket serverSocket;
    private int[] ecg_data;
    boolean open=true;

    /**
     *Empty (default) constructor.
     */
    public ServerThreadsClient() {
    }

    /**
     *Run method for the server, which waits for a client to connect, and when
     * it does, receives the ECG that has been sent.
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(9000);

            while (open) {
                try {
                    System.out.println("Antes accept");
                    Socket socket = serverSocket.accept();
                    System.out.println("Despues accept");
                    InputStream inputStream = null;
                    ObjectInputStream objectInputStream = null;

                    try {
                        inputStream = socket.getInputStream();
                        objectInputStream = new ObjectInputStream(inputStream);
                        Object tmp;
                        System.out.println("Antes leer");
                        while ((tmp = objectInputStream.readObject()) != null) {//we receive the ecg
                            System.out.println("dentro");
                            ecg_data = (int[]) tmp;
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
           // if(!open)                        {releaseResourcesClient(objectInputStream, socket);}

        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    /**
     *Method that closes the serversocket and exits the thread, thus the server.
     */
    public void closeServer() {
        try {
            open=false;
            serverSocket.close();
            exit(0);
        } catch (IOException ex) {
            Logger.getLogger(FXMLServerController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *Returns ECG data for managing the data from the ServerController.
     * @return ECG_data that the server has received.
     */
    public int[] getEcg_data() {
        return ecg_data;
    }

}
