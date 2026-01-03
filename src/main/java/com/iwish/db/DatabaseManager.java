/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.iwish.db;
import com.iwish.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
    public User loginUser(String username, String password){
        String query = "select * from users where username = ? and password = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(query)){
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.next()){
                // This is just a user to carry the information and display it in the session.
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setBalance(rs.getDouble("balance"));
                return user;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public List<User> searchUsers(String query, String myUsername){
        List<User> results = new ArrayList<>();
        String sql = "Select username, email from users where username like ? and username != ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, "%" + query + "%");
            pstmt.setString(2, myUsername);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                User u = new User();
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                results.add(u);
            }
        }catch(SQLException e){e.printStackTrace();}
        return results;
    }
    
    //Send friend request
    
    public boolean sendFriendRequest(String sender, String receiver){
        String sql = "insert into friendships (user_id, friend_id, status) values (?, ?, 'Pending')";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, sender);
            pstmt.setString(2, receiver);
            return pstmt.executeUpdate() > 0;
        }catch(SQLException e){return false;}
    }
    
    public List<String> getPendingRequests(String myUsername){
        List<String> senders = new ArrayList<>();
        //looking where we are the receiver and status is pending
        String sql = "Select user_id from friendships where friend_id = ? and status = 'PENDING'";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, myUsername);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                senders.add(rs.getString("user_id"));
            }
        }catch(SQLException e){e.printStackTrace();}
        return senders;
    }
    
    public boolean updateFriendshipStatus(String sender, String receiver, String newStatus){
        String sql = "Update friendships set status = ? where user_id = ? and friend_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, newStatus);
            pstmt.setString(2, sender);
            pstmt.setString(3, receiver);
            return pstmt.executeUpdate() >0;
        }catch(SQLException e){return false;}
    }
    
    public boolean deleteFriendship(String sender, String receiver){
        String sql = "Delete from friendships where user_id = ? and friend_id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, sender);
            pstmt.setString(2, receiver);
            return pstmt.executeUpdate()>0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public List<String> getAcceptedFriends(String username){
        List<String> friends = new ArrayList<>();
        //This query finds your currently friends
        String query = "select friend_id from friendships where user_id = ? and status = 'ACCEPTED' " + 
                       " Union " +
                       "select user_id from friendships where friend_id = ? and status = 'ACCEPTED' ";
        try (PreparedStatement pstmt = connection.prepareStatement(query)){
            
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            
            try (ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    friends.add(rs.getString(1));
                }
            }
        }catch(SQLException e){
            System.err.println("Database error in getAcceptedFriends: " + e.getMessage());
        }
        return friends;
    }
    
    
}
