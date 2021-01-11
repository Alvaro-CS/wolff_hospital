/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wolff_hospital;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ALVARO
 */
public class ServerThreadUI implements Runnable {

    ServerSocket serverSocket;
    ArrayList<ServerThreadsClient> clientThreads;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(9000);
            clientThreads = new ArrayList<>();
            while (FXMLServerController.open) {
                //Thie executes when we have a client
                try {
                    Socket socket = serverSocket.accept();
                    ServerThreadsClient s = new ServerThreadsClient(socket);//each client will have its own server thread
                    clientThreads.add(s); //We add to an arraylist each thread, so we can close them when server closes.
                    new Thread(s).start(); //We start the thread, run method.
                } catch (SocketException s) {
                    System.out.println("Socket acceptance interrupted");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadUI.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    /**
     * Method that closes the serversocket and exits the thread, thus the
     * server.
     */
    public void closeServer() {
        try {     
            serverSocket.close();
            System.out.println("Server closed.");
        } catch (IOException ex) {
            Logger.getLogger(FXMLServerController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
