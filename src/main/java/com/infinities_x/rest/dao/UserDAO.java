package com.infinities_x.rest.dao;

import javax.sql.DataSource;

import com.infinities_x.rest.config.ConfigAgeLoader;
import com.infinities_x.rest.config.FeatureToggleConfig;
import com.infinities_x.rest.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private DataSource dataSource;

    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, email, age FROM users";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));

               
                Integer ageFromConfig = ConfigAgeLoader.getAgeForUserId(user.getId());
                if (ageFromConfig != null) {
                    user.setAge(ageFromConfig);
                } else {
                   
                    user.setAge(rs.getInt("age"));
                }

                users.add(user);
            }
        }
        return users;
    }

    
    private int getCurrentAgeById(int id) throws SQLException {
        String sql = "SELECT age FROM users WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("age");
                }
            }
        }
        return 0;
    }


    
    public User getUserById(int id) throws SQLException {
        User user = null;
        String sql = "SELECT id, username, password, email, age FROM users WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setAge(rs.getInt("age")); 
                }
            }
        }
        return user;
    }

    
    
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; 
                }
            }
        }
        return false;
    }

    public void createUser(User user) throws SQLException {
        if (usernameExists(user.getUsername())) {
            throw new SQLException("Username already exists.");
        }

        
        Integer ageFromConfig = ConfigAgeLoader.getAgeForUserId(user.getId());
        int ageToSet = (user.getAge() > 0) ? user.getAge() : (ageFromConfig != null ? ageFromConfig : ConfigAgeLoader.getDefaultAge());

        String sql = "INSERT INTO users (username, password, email, age) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setInt(4, ageToSet);
            pstmt.executeUpdate();
        }
    }



    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, age = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());

            int currentAge = getCurrentAgeById(user.getId());
            Integer ageFromConfig = ConfigAgeLoader.getAgeForUserId(user.getId());
            int ageToSet = (currentAge == 0 && ageFromConfig != null) ? ageFromConfig : user.getAge();

            pstmt.setInt(4, ageToSet);
            pstmt.setInt(5, user.getId());
            pstmt.executeUpdate();
        }
    }


    public void deleteUser(int id) throws SQLException {
        if (!FeatureToggleConfig.isFeatureEnabled("allowUserDeletion")) {
            System.out.println("User deletion feature is disabled.");
            return;
        }

        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) deleted.");
        }
    }
    
    public void updateUserAgesFromConfig() throws SQLException {
        String sql = "SELECT id, age FROM users";
        String updateSql = "UPDATE users SET age = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery();
             PreparedStatement updatePstmt = connection.prepareStatement(updateSql)) {

            while (rs.next()) {
                int userId = rs.getInt("id");
                int currentAge = rs.getInt("age");
                Integer newAge = ConfigAgeLoader.getAgeForUserId(userId);

                if (newAge == null && currentAge == 0) {
                    newAge = ConfigAgeLoader.getDefaultAge();
                }

                if (newAge != null) {
                    updatePstmt.setInt(1, newAge);
                    updatePstmt.setInt(2, userId);
                    updatePstmt.executeUpdate();
                }
            }
        }
    }

}