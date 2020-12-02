/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wolff_hospital;

import POJOS.Patient;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import static java.lang.System.exit;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
    boolean open = true;

    // private static ArrayList<Patient> patients = new ArrayList<>();
    private static String filename = "patientList";

    /**
     * Empty (default) constructor.
     */
    public ServerThreadsClient() {
    }

    /**
     * Run method for the server, which waits for a client to connect, and when
     * it does, receives the ECG that has been sent.
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(9000);

            while (open) {
                try {
                    System.out.println("Before accepting");
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected");
                    InputStream inputStream;
                    ObjectInputStream objectInputStream = null;

                    try {
                        inputStream = socket.getInputStream();
                        objectInputStream = new ObjectInputStream(inputStream);
                        Object tmp;
                        System.out.println("Before order");
                        //Instruction received
                        String instruction;
                        tmp = objectInputStream.readObject();//we receive the instruction
                        instruction = (String) tmp;

                        System.out.println("Order received");
                        switch (instruction) {
                            case "REGISTER": {
                                System.out.println(instruction + " option running");
                                Patient p = null;
                                while ((tmp = objectInputStream.readObject()) != null) {//we receive the patient
                                    p = (Patient) tmp;
                                }
                                registerPatient(p);
                                break;
                            }
                            case "SEARCH_PATIENT": {
                                System.out.println(instruction + " option running");

                                String[] data = new String[2];
                                int i = 0;
                                while ((tmp = objectInputStream.readObject()) != null) {//we receive the DNI+password combination
                                    data[i] = (String) tmp;
                                    System.out.println(data[i]);
                                    i++;
                                }
                                searchPatient(data);
                                //
                                break;
                            }
                            default: {
                                System.out.println("Error");
                                break;
                            }
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

    private static void registerPatient(Patient p) throws ClassNotFoundException {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
            ArrayList<Patient> patients = getPatients();

            patients.add(p); //we add the patient to the list of patients
            os.writeObject(patients); //TODO how do we write only dni and passw? 2 different files?
            os.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void searchPatient(String[] data) {
        Patient patient = null;
        String filename = "patientFiles";
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(filename));
            ArrayList<Patient> patients = getPatients();//method to return all patients
            for (int i = 0; i < patients.size(); i++) {
                if (patients.get(i).getDNI().equalsIgnoreCase(data[0])
                        && patients.get(i).getPassword().equals(data[1])) {
                    patient = patients.get(i);
                    System.out.println("Patient found.");
                }
            }
            is.close();
            //Send patient object to client
            sendPatient(patient);

        } catch (EOFException ex) {
            System.out.println("All data have been correctly read.");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static ArrayList<Patient> getPatients() throws ClassNotFoundException {
        ArrayList<Patient> patients = null;
        ObjectInputStream is;
        try {
            is = new ObjectInputStream(new FileInputStream(filename));
            patients = (ArrayList<Patient>) is.readObject();
            for (int i = 0; i < patients.size(); i++) {
                System.out.println(patients.get(i).toString());
            }
            is.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return patients;
    }

    private static void sendPatient(Patient patient) {
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        Socket socket = null;
        try {
            socket = new Socket("localhost", 9000);
            outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            //TODO
            /*
            //Sending order
            String order="REGISTER";
            objectOutputStream.writeObject(order);
            System.out.println("Order"+ order+ "sent");
            
            //Sending patient
            objectOutputStream.writeObject(patient);
            System.out.println("Patient data sent to client");
             */
        } catch (IOException ex) {
            System.out.println("Unable to write the object on the server.");
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            releaseResources(outputStream, socket);

        }
    }

    private static void releaseResources(OutputStream outputStream, Socket socket) {
        try {
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            socket.close();
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
     * Method that closes the serversocket and exits the thread, thus the
     * server.
     */
    public void closeServer() {
        try {
            open = false;
            serverSocket.close();
            exit(0);
        } catch (IOException ex) {
            Logger.getLogger(FXMLServerController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Returns ECG data for managing the data from the ServerController.
     *
     * @return ECG_data that the server has received.
     */
    public int[] getEcg_data() {
        return ecg_data;
    }

}
