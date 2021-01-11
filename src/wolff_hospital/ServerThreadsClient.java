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
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ALVARO
 */
public class ServerThreadsClient implements Runnable {

    Socket socket;
    private int[] ecg_data;
    private boolean client_connected = true;

    private static final String FILENAME = "./files/patientData";

    /**
     * We create this thread for this specific socket/client.
     *
     * @param socket
     */
    public ServerThreadsClient(Socket socket) {
        this.socket = socket;
    }

    /**
     * Run method for the server, which waits for a client to connect, and when
     * it does, receives the ECG that has been sent.
     */
    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            while (client_connected) {
                try {
                    //We first send a 1 to the client to check if connections are working.
                    //If client doesn't receive the 1, but a -1, server would be offline.
                   // objectOutputStream.writeByte(1);
                    //objectOutputStream.flush();

                    Object tmp;
                    System.out.println("Before order");
                    //Instruction received
                    String instruction;
                    tmp = objectInputStream.readObject();//we receive the instruction.                 
                    if (FXMLServerController.open) {//if server is open
                        instruction = (String) tmp;
                        System.out.println("Order received");
                        //Now, double check for the client to check if connections
                        //are working (otherwise, client can perform at least 1 action without response).
                        objectOutputStream.writeByte(1);
                        objectOutputStream.flush();

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
                                data[0] = (String) objectInputStream.readObject();
                                data[1] = (String) objectInputStream.readObject();
                                searchPatient(data, objectOutputStream);

                                break;
                            }
                            case "UPDATE": {
                                System.out.println(instruction + " option running");
                                tmp = objectInputStream.readObject();//we receive the new patient from client
                                Patient p = (Patient) tmp;

                                System.out.println("Patient received:" + p.getDNI());
                                replacePatient(p);
                                break;
                            }
                            case "EXISTS": {
                                System.out.println(instruction + " option running");

                                tmp = objectInputStream.readObject();//we receive the name we want to check if its free from client
                                String id = (String) tmp;

                                System.out.println("ID received:" + id);
                                Patient p = searchPatientID(id);
                                sendPatientToClient(p, objectOutputStream);
                                break;
                            }
                            case "GET_PATIENTS": {
                                ArrayList<Patient> patients = getPatients();
                                sendPatientListToClient(patients, objectOutputStream);
                                break;
                            }
                            default: {
                                System.out.println("Error");
                                break;
                            }
                        }
                    } else {//If server closes, stop instruction flow.
                        System.out.println("Server closed");
                        break;
                    }
                } catch (EOFException | ClassNotFoundException e) {
                    Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, e);
                    System.out.println("Client closed");
                    client_connected = false;
                    releaseResources(socket, outputStream, objectOutputStream, inputStream, objectInputStream);
                }
            }
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
            File directory = new File("./files/");

            if (directory.exists()) {
                System.out.println("File is a Directory");
            } else {
                System.out.println("Directory doesn't exist!!");
                directory.mkdirs();
                System.out.println("directory created");

            }

            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(FILENAME));

            os.writeObject(patient_list); //TODO how do we write only dni and passw? 2 different files?
            System.out.println("All patients saved into file.");
            os.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void searchPatient(String[] data, ObjectOutputStream objectOutputStream) {
        Patient patient = null;
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILENAME));
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
            sendPatientToClient(patient, objectOutputStream);

        } catch (EOFException ex) {
            System.out.println("All data have been correctly read.");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //To check if patient exists (register)
    private static Patient searchPatientID(String id) {
        Patient patient = null;
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILENAME));
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
        return patient; //if not found, it will be null
    }

    private static ArrayList<Patient> getPatients() throws ClassNotFoundException, FileNotFoundException {
        ArrayList<Patient> patients = new ArrayList<>();
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(new FileInputStream(FILENAME));
            System.out.println(FILENAME);
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

    private static void replacePatient(Patient patientNew) throws ClassNotFoundException, FileNotFoundException {
        ArrayList<Patient> patients = getPatients();
        System.out.println("Antes" + patients.size());
        System.out.println("Nº records NEW: " + patientNew.getClinical_record_list().size());

        String id = patientNew.getDNI();
        int index = 0;
        //removing "old" patient
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getDNI().equals(id)) {
                System.out.println("Nº records OLD: " + patients.get(i).getClinical_record_list().size());
                index = i;
                patients.remove(patients.get(i));
            }
        }

        System.out.println("Quitamos" + patients.size());

        patients.add(index, patientNew);
        System.out.println("Nº records actualizado: " + patients.get(index).getClinical_record_list().size());

        System.out.println("Actualizamos" + patients.size());
        updatePatients(patients);

    }

    private static void updatePatients(ArrayList<Patient> patients) throws ClassNotFoundException {
        try {

            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(FILENAME));
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

    private static void sendPatientToClient(Patient patient, ObjectOutputStream objectOutputStream) {

        try {
            //Sending order
            String order = "RECEIVE_PATIENT";
            objectOutputStream.writeObject(order);
            System.out.println("Order" + order + "sent");

            //Sending patient
            objectOutputStream.writeObject(patient);
            System.out.println("Patient data sent to client");

        } catch (IOException ex) {
            System.out.println("Unable to write the object on the client.");
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void sendPatientListToClient(ArrayList<Patient> patients, ObjectOutputStream objectOutputStream) {

        try {
            //Sending order
            String order = "RECEIVE_PATIENT_LIST";
            objectOutputStream.writeObject(order);
            System.out.println("Order" + order + "sent");

            //Sending patient
            objectOutputStream.writeObject(patients);
            System.out.println("ALL patients data sent to client");

        } catch (IOException ex) {
            System.out.println("Unable to write the object on the client.");
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void releaseResources(Socket socket, OutputStream outputStream, ObjectOutputStream objectOutputStream,
            InputStream inputStream, ObjectInputStream objectInputStream) {
        try {
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            objectOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
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
     * Returns ECG data for managing the data from the ServerController.
     *
     * @return ECG_data that the server has received.
     */
    public int[] getEcg_data() {
        return ecg_data;
    }

}
