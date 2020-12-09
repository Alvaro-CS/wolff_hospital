/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wolff_hospital;

import POJOS.Patient;
import POJOS.Patient_list;
import java.io.EOFException;
import java.io.File;
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
    private static String filename = "./files/patientData";

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

                                tmp = objectInputStream.readObject();//we receive the new patient from client
                                Patient p = (Patient) tmp;

                                System.out.println("Patient received:" + p.getDNI());
                                registerPatient(p);
                                break;
                            }
                            case "SEARCH_PATIENT": {
                                System.out.println(instruction + " option running");

                                String[] data = new String[2];
                                int i = 0;
                                /*while ((tmp = objectInputStream.readObject()) != null) {//we receive the DNI+password combination
                                    data[i] = (String) tmp;
                                    System.out.println(data[i]);
                                    i++;
                                }*/
                                data[0] = (String) objectInputStream.readObject();
                                data[1] = (String) objectInputStream.readObject();
                                searchPatient(data);

                                break;
                            }
                            case "UPDATE": {
                                System.out.println(instruction + " option running");

                                tmp = objectInputStream.readObject();//we receive the new patient from client
                                Patient p = (Patient) tmp;

                                System.out.println("Patient received:" + p.getDNI());
                                removePatient(p);
                                break;
                            }
                            default: {
                                System.out.println("Error");
                                break;
                            }
                        }

                    } catch (IOException | ClassNotFoundException e) {
                        Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, e);

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
            System.out.println("Getting old patients...");
            ArrayList<Patient> patients = getPatients();
            System.out.println("Actual patients:\n" + patients);
            patients.add(p); //we add the patient to the list of patients
            System.out.println(p + " added.");
            System.out.println("Updated patients:\n" + patients);
            Patient_list patient_list = new Patient_list(patients);
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));

            os.writeObject(patient_list); //TODO how do we write only dni and passw? 2 different files?
            System.out.println("All patients saved into file.");
            os.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void searchPatient(String[] data) {
        Patient patient = null;
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(filename));
            System.out.println("Before taking patients");
            ArrayList<Patient> patients = getPatients();//method to return all patients
            System.out.println("Actual patients:\n" + patients);
            for (int i = 0; i < patients.size(); i++) {
                if (patients.get(i).getDNI().equalsIgnoreCase(data[0])
                        && patients.get(i).getPassword().equals(data[1])) {
                    patient = patients.get(i);
                    System.out.println("Patient found:" + patient.getDNI());
                }
            }
            is.close();
            //Send patient object to client
            sendPatientToClient(patient);

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

    //Return patient for update (use)
    private static Patient searchPatientID(String id) {
        Patient patient = null;
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(filename));
            System.out.println("Before taking patients");
            ArrayList<Patient> patients = getPatients();//method to return all patients
            System.out.println("Actual patients:\n" + patients);
            for (int i = 0; i < patients.size(); i++) {
                if (patients.get(i).getDNI().equalsIgnoreCase(id)) {
                    patient = patients.get(i);
                    System.out.println("Patient found:" + patient.getDNI());
                }
            }
            is.close();

        } catch (EOFException ex) {
            System.out.println("All data have been correctly read.");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return patient;
    }

    private static ArrayList<Patient> getPatients() throws ClassNotFoundException, FileNotFoundException {
        ArrayList<Patient> patients = new ArrayList<>();
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(new FileInputStream(filename));
            System.out.println(filename);
            Patient_list patient_list = (Patient_list) is.readObject();
            patients = patient_list.getPatients();
            System.out.println(patients);

        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return patients;
    }

    private static void removePatient(Patient patientNew) throws ClassNotFoundException, FileNotFoundException {
        ArrayList<Patient> patients = getPatients();
        // patients.remove;
        String id = patientNew.getDNI();
        Patient patientOld = searchPatientID(id);
        patients.remove(patientOld);
        patients.add(patientNew);
        updatePatients(patients);

    }

    private static void updatePatients(ArrayList<Patient> patients) throws ClassNotFoundException {
        try {

            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
            Patient_list patient_list = new Patient_list(patients);
            os.writeObject(patient_list); //TODO how do we write only dni and passw? 2 different files?
            System.out.println("All patients updated.");
            os.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void sendPatientToClient(Patient patient) {
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        Socket socket = null;
        try {
            socket = new Socket("localhost", 9001);
            outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);

            //Sending order
            String order = "RECEIVE_PATIENT";
            objectOutputStream.writeObject(order);
            System.out.println("Order" + order + "sent");

            //Sending patient
            objectOutputStream.writeObject(patient);
            System.out.println("Patient data sent to client");

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
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
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
