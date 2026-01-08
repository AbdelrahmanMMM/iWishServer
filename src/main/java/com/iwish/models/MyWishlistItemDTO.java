/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.iwish.models;
import java.io.Serializable;
/**
 *
 * @author Bassam Ahmed
 */

public class MyWishlistItemDTO implements Serializable {
    private static final long serialVersionUID = 1L; // مهم عشان الـ Networking

    private int wishlistId;      // ID بتاع الصف في جدول الـ wishlist
    private String itemName;     // اسم المنتج من جدول الـ items
    private double itemPrice;    // سعر المنتج الأصلي
    private double currentAmount; // المبلغ اللي اتجمع لحد دلوقتي

    public MyWishlistItemDTO(int wishlistId, String itemName, double itemPrice, double currentAmount) {
        this.wishlistId = wishlistId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.currentAmount = currentAmount;
    }

    // Getters and Setters
    public int getWishlistId() { return wishlistId; }
    public String getItemName() { return itemName; }
    public double getItemPrice() { return itemPrice; }
    public double getCurrentAmount() { return currentAmount; }
    
    // ميزة إضافية: حساب النسبة المئوية اللي كملت
    public double getCompletionPercentage() {
        return (currentAmount / itemPrice) * 100;
    }
}