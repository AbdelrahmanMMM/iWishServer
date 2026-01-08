package com.iwish.db;

import com.iwish.models.User;
import com.iwish.models.MyWishlistItemDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=project;user=project;password=1234;encrypt=true;trustServerCertificate=true;";
    private static final String DB_USER = "project";
    private static final String DB_PASS = "1234";

    private Connection connection;

    public DatabaseManager() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(URL, DB_USER, DB_PASS);

            System.out.println("SQL Server Connected successfully!");
            connection.setAutoCommit(true);

        } catch (Exception e) {
            System.err.println("Connection Failed! Check your SQL Server configuration.");
            e.printStackTrace();
        }
    }

    // ======================
    // USER AUTH
    // ======================

    public boolean registerUser(User user) {
        String query =
                "INSERT INTO users (username, password, email, balance) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUsername().trim());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail().trim());
            pstmt.setDouble(4, user.getBalance());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User loginUser(String username, String password) {
        String query =
                "SELECT * FROM users WHERE LTRIM(RTRIM(username)) = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username").trim());
                user.setEmail(rs.getString("email").trim());
                user.setBalance(rs.getDouble("balance"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ======================
    // WISHLIST
    // ======================

    public List<MyWishlistItemDTO> getUserWishlist(String username) {
        List<MyWishlistItemDTO> wishlist = new ArrayList<>();

        String query =
                "SELECT w.wishlist_id, ISNULL(i.name,'Sample Item') AS name, " +
                "ISNULL(i.price,0) AS price, w.current_amount " +
                "FROM wishlists w " +
                "LEFT JOIN items i ON w.item_id = i.item_id " +
                "WHERE LTRIM(RTRIM(w.username)) = ? AND w.status = 'PENDING'";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username.trim());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                wishlist.add(new MyWishlistItemDTO(
                        rs.getInt("wishlist_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getDouble("current_amount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishlist;
    }

    public boolean addToWishlist(String username, int itemId) {
        String query =
                "INSERT INTO wishlists (username, item_id, current_amount, status) " +
                "VALUES (?, ?, 0.0, 'PENDING')";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username.trim());
            pstmt.setInt(2, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteWishlistItem(int wishlistId) {
        String sql = "DELETE FROM wishlists WHERE wishlist_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, wishlistId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ======================
    // ✅ NEW: RECHARGE BALANCE
    // ======================

    public double rechargeBalance(String username, double amount) {

        String updateSql =
                "UPDATE users SET balance = balance + ? WHERE LTRIM(RTRIM(username)) = ?";

        String selectSql =
                "SELECT balance FROM users WHERE LTRIM(RTRIM(username)) = ?";

        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql);
             PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {

            updateStmt.setDouble(1, amount);
            updateStmt.setString(2, username.trim());
            updateStmt.executeUpdate();

            selectStmt.setString(1, username.trim());
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ======================
    // SOCIAL
    // ======================

    public List<User> searchUsers(String query, String myUsername) {
        List<User> results = new ArrayList<>();
        String sql =
                "SELECT username, email, balance FROM users " +
                "WHERE username LIKE ? AND LTRIM(RTRIM(username)) != ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            pstmt.setString(2, myUsername.trim());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User u = new User();
                u.setUsername(rs.getString("username").trim());
                u.setEmail(rs.getString("email").trim());
                u.setBalance(rs.getDouble("balance"));
                results.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public boolean sendFriendRequest(String sender, String receiver) {
        String sql =
                "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, 'PENDING')";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sender.trim());
            pstmt.setString(2, receiver.trim());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Friendship Error: " + e.getMessage());
            return false;
        }
    }
}
