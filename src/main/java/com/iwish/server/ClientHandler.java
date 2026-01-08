package com.iwish.server;

import com.iwish.db.DatabaseManager;
import com.iwish.models.User;
import com.iwish.models.MyWishlistItemDTO;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {

    private Socket socket;
    private DatabaseManager dbManager;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Socket socket, DatabaseManager dbManager) {
        this.socket = socket;
        this.dbManager = dbManager;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Object request = in.readObject();

                // ======================
                // OBJECT[] BASED COMMANDS
                // ======================
                if (request instanceof Object[]) {
                    Object[] data = (Object[]) request;
                    String command = (String) data[0];

                    if ("GET_MY_WISHLIST".equals(command)) {
                        String username = (String) data[1];
                        List<MyWishlistItemDTO> wishlist =
                                dbManager.getUserWishlist(username);
                        out.writeObject(wishlist);
                        out.flush();
                    }

                    else if ("DELETE_WISHLIST_ITEM".equals(command)) {
                        int wishlistId = (int) data[1];
                        boolean success =
                                dbManager.deleteWishlistItem(wishlistId);
                        out.writeObject(success ? "SUCCESS" : "FAILED");
                        out.flush();
                    }

                    // ======================
                    // ✅ NEW: RECHARGE BALANCE
                    // ======================
                    else if ("RECHARGE_BALANCE".equals(command)) {
                        String username = (String) data[1];
                        double amount = (double) data[2];

                        double newBalance =
                                dbManager.rechargeBalance(username, amount);

                        out.writeObject(newBalance);
                        out.flush();
                    }
                }

                // ======================
                // OLD STRING COMMANDS
                // ======================
                else if (request instanceof String) {
                    String command = (String) request;

                    if (command.equals("LOGIN")) {
                        User userData = (User) in.readObject();
                        User authenticated =
                                dbManager.loginUser(
                                        userData.getUsername(),
                                        userData.getPassword()
                                );

                        if (authenticated != null) {
                            out.writeObject("LOGIN_SUCCESS");
                            out.writeObject(authenticated);
                        } else {
                            out.writeObject("LOGIN_FAILED");
                        }
                        out.flush();
                    }

                    else if (command.equals("SIGNUP")) {
                        User userData = (User) in.readObject();
                        boolean success = dbManager.registerUser(userData);
                        out.writeObject(
                                success ? "REGISTRATION_SUCCESS" : "REGISTRATION_FAILED"
                        );
                        out.flush();
                    }

                    else if (command.equals("SEARCH_USER")) {
                        String query = (String) in.readObject();
                        String myUsername = (String) in.readObject();
                        List<User> results =
                                dbManager.searchUsers(query, myUsername);
                        out.writeObject(results);
                        out.flush();
                    }

                    else if (command.equals("SEND_FRIEND_REQUEST")) {
                        String targetUser = (String) in.readObject();
                        String myUsername = (String) in.readObject();
                        boolean success =
                                dbManager.sendFriendRequest(myUsername, targetUser);
                        out.writeObject(success ? "SUCCESS" : "FAILED");
                        out.flush();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // client disconnected
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
