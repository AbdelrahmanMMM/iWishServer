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
public class Item implements Serializable {
    private int id;
    private String name;
    private double price;
    private double currentAmount;
    
    public Item(int id, String name, double price, double currentAmount){
        this.id = id;
        this.name = name;
        this.price = price;
        this.currentAmount = currentAmount;
    }
    
    public String getName(){return name;}
    public double getPrice(){return price;}
    public double getCurrentAmount(){return currentAmount;}
    
    //logic for Progress bar: return a value between 0.0 and 1.0
    public double getProgress(){
        if (price <= 0) return 0;
        return currentAmount / price;
    }
}
