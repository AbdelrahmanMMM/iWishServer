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
import java.util.List;

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
                
                if (!(request instanceof String )) continue;
                String command = (String)request;
                System.out.println("Command recieved: " + command);

                if (command.equals("LOGIN")||command.equals("SIGNUP")){
                    User userData = (User) in.readObject();
                    if (command.equals("LOGIN")){
                        User authenticated = dbManager.loginUser(userData.getUsername(), userData.getPassword());
                        if (authenticated != null){
                            out.writeObject("LOGIN_SUCCESS");
                            out.writeObject(authenticated);
                        }else{
                            out.writeObject("LOGIN_FAILED");
                        }
                    }
                    else{
                        boolean success = dbManager.registerUser(userData);
                        out.writeObject(success ? "REGISTRATION_SUCCESS" : "REGISTRATION_FAILED");
                        }
                }
                else if (command.equals("SEARCH_USER")){
                    String query = (String) in.readObject();
                    String myUsername = (String) in.readObject();
                    List<User> results = dbManager.searchUsers(query, myUsername);
                    out.writeObject(results);
                    out.flush();
                    out.reset();
                }else if (command.equals("SEND_FRIEND_REQUEST")){
                    String targetUser = (String) in.readObject();
                    String myUsername = (String) in.readObject();
                    boolean success = dbManager.sendFriendRequest(myUsername, targetUser);
                    out.writeObject(success? "SUCCESS":"FAILED");
                    out.flush();
                    out.reset();
                }else if (command.equals("GET_PENDING_REQUESTS")){
                    String myUsername = (String) in.readObject();
                    List<String> requests = dbManager.getPendingRequests(myUsername);
                    out.writeObject(requests);
                    out.flush();
                    out.reset();
                }else if (command.equals("ACCEPT_FRIEND")){
                    String sender = (String) in.readObject();
                    String receiver = (String) in.readObject();
                    boolean ok = dbManager.updateFriendshipStatus(sender, receiver, "ACCEPTED");
                    out.writeObject(ok? "SUCCESS" : "FAILED");
                    out.flush();
                    out.reset();
                }else if (command.equals("DECLINE_FRIEND")){
                    String sender = (String) in.readObject();
                    String receiver = (String) in.readObject();
                    boolean ok = dbManager.deleteFriendship(sender, receiver);
                    out.writeObject(ok? "SUCCESS" : "FAILED");
                    out.flush();
                    out.reset();
                }else if (command.equals("GET_ACCEPTED_FRIENDS")){
                    String myUsername = (String) in.readObject();
                    List<String> friends = dbManager.getAcceptedFriends(myUsername);
                    out.writeObject(friends);
                    out.flush();
                    out.reset();
                }

            out.flush();
            out.reset();
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
