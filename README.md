# wolff_hospital
Created by Susana del Riego, Álvaro Cascajero and Alejandra Aceves

**ADMIN MANUAL**

We have an application for the patient/client, another for the doctor/client and another for the server, the hospital in our case. As we have two machines which exchange information a set of rules and procedures are required to do that; we are using TCP/IP protocols, instead of UDP protocols.

These are the fundamental protocols of the Internet; they present different layers and are more reliable. It helps to create a virtual network between them when multiple computer networks are connected, and its purpose is to allow communication even over long distances.

To do that and to be able to communicate between them, we have created a doctor project which establishes a connection with the server and communicates to the patient and exchanges information with him/her. Both, the doctor, and the patient, must introduce the IP address of the server to connect.

**WOLFFGRAM APP USAGE**

We are going to start explaining the **server app** , called wolff\_hospital. In order to run our application, the user must start running the server.


A screen will pop up and a password must be introduced (password=server by default), if this password is correct, the server will open and we will be able to connect to it with the patient or doctor application; otherwise, the server will not open, a message will inform the user that the passwords do not match, and we will not be able either to run the client.


Once the password written is correct, a new window will pop up with two different options. Once button for opening the server, if all goes smoothly a message will show that the server has opened correctly. If the user tries to open the server again but it is already opened, another message in the same window will indicate to the user that the server is already opened.

If the user wants to close the server, he just must click on the Close Server button and a message to the user will indicate that the server was closed correctly.


In the case that the user forgets to close the server, before pressing the X close button of the window, the server will be automatically closed.

Also, if the user wants to close the server and then open again the server, it will be also possible.

With the Server being open, we can run the clients app. We will start explaining the patient app, **wolff\_patient**.
