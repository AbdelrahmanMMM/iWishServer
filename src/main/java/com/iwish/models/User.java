/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.iwish.models;

import java.io.Serializable;

/**
 *
 * @author ITEi
 */
public class User implements Serializable {
    private String username;
    private String password;
    private String email;
    private double balance;

    public User(){}    
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    public User(String username, String password, String email, double balance){
        this.username = username;
        this.password = password;
        this.email = email;
        this.balance = balance;
    }
    
    //Getters
    public String getUsername(){return username;}
    public String getPassword(){return password;}
    public String getEmail(){return email;}
    public double getBalance(){return balance;}
    public void setUsername(String username){this.username = username;}
    public void setPassword(String password){this.password = password;}
    public void setEmail(String email){this.email = email;}
    public void setBalance(double balance){this.balance = balance;}

}
