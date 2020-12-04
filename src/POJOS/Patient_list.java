/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package POJOS;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author ALVARO
 */
public class Patient_list implements Serializable {

    private ArrayList<Patient> patients = new ArrayList<>();

    public Patient_list() {

    }

    @Override
    public String toString() {
        return "Patient_list{" + "patients=" + patients + '}';
    }

    public Patient_list(ArrayList<Patient> patients) {
        this.patients = patients;
    }

    public ArrayList<Patient> getPatients() {
        return patients;
    }

    public void setPatients(ArrayList<Patient> patients) {
        this.patients = patients;
    }

}
