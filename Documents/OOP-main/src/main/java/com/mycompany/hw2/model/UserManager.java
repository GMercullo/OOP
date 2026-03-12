package com.mycompany.hw2.model;

import java.util.ArrayList;

public class UserManager {

    private ArrayList<User> users = new ArrayList<>();

    public void createUser(String username, String password, String role) {
        users.add(new User(username, password, role));
    }

    public ArrayList<User> viewUsers() {
        return users;
    }

    public void updateUser(String username, String newPassword) {
        for(User u : users){
            if(u.getUsername().equals(username)){
                u.setPassword(newPassword);
            }
        }
    }

    public void deleteUser(String username) {
        users.removeIf(u -> u.getUsername().equals(username));
    }

    public void resetPassword(String username, String newPassword) {
        updateUser(username, newPassword);
    }

    public void assignRole(String username, String role) {
        for(User u : users){
            if(u.getUsername().equals(username)){
                u.setRole(role);
            }
        }
    }
}