/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.iwish.server;

import com.iwish.db.DatabaseManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author ITEi
 */
public class ServerApp {
    private static final int PORT = 5005;
    private DatabaseManager dbManager;
    
    public void startServer(){
        dbManager = new DatabaseManager();
        
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("i-Wish Server is running on port "+ PORT + "...");
            
            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: "+ clientSocket.getInetAddress());
                
                //Start a new Thread for each Client
                //.................................
                new ClientHandler(clientSocket, dbManager).start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        new ServerApp().startServer();
    }
}
