/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.iwish.db;
import com.iwish.models.User;
import java.sql.*;
/**
 *
 * @author ITEi
 */
public class DatabaseManager {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=IWishDB;user=db_user;password=123;encrypt=true;trustServerCertificate=true;";
    private static final String USERNAME = "db_user";
    private static final String PASSWORD = "123";
    
    private Connection connection;
    
    public DatabaseManager(){
        try{
            //Load the Microsoft SQL Server Driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("SQL Server Connected successfully!");
        }catch(Exception e){
            System.err.println("Connection Failed! Check your URL, login or Firewall.");
            e.printStackTrace();
        }
    }
    
    public boolean registerUser(User user){
        String query = "Insert into users (username, password, email, balance) values (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection .prepareStatement(query)){
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setDouble(4, user.getBalance());
            
            return pstmt.executeUpdate() > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
