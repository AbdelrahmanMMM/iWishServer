/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.iwish.server;

import com.iwish.db.DatabaseManager;
import com.iwish.models.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author ITEi
 */
public class ClientHandler extends Thread{
    private Socket socket;
    private DatabaseManager dbManager;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public ClientHandler(Socket socket, DatabaseManager dbManager){
        this.socket = socket;
        this.dbManager = dbManager;
    }
    
    @Override
    public void run(){
        try{
            //Initialize Streams
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            while(true){
                //Wait for an object from the client
                Object request = in.readObject();
                
                if (request instanceof String ){
                    String command = (String)request;
                    System.out.println("Command recieved: " + command);
                    
                    //Listening to User data object
                    Object data = in.readObject();
                    if(data instanceof User){
                        User userData = (User) data;
                        
                        if (command.equals("LOGIN")){
                            User authenticated = dbManager.loginUser(userData.getUsername(), userData.getPassword());
                            if (authenticated != null){
                                out.writeObject("LOGIN_SUCCESS");
                                out.writeObject(authenticated);
                            }else{
                                out.writeObject("LOGIN_FAILED");
                            }
                        }else if (command.equals("SIGNUP")){
                            boolean success = dbManager.registerUser(userData);
                            out.writeObject(success ? "REGISTRATION_SUCCESS" : "REGISTRATION_FAILED");
                        }
                    }
                    out.flush();
                }
            }
        }catch(IOException | ClassNotFoundException e){
            System.out.println("Client disconnected: "+ socket.getInetAddress());
        }finally{
            closeConnection();
        }
    }
    
    private void closeConnection(){
        try{
            if (in!=null) in.close();
            if (out!=null) out.close();
            if (socket!=null) socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
