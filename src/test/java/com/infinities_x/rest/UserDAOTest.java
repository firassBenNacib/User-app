package com.infinities_x.rest;

import com.infinities_x.rest.dao.UserDAO;
import com.infinities_x.rest.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDAOTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(preparedStatement.executeUpdate()).thenReturn(1); 
    }

    @Test
    public void getAllUsers_ReturnsListOfUsers() throws SQLException {
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("id")).thenReturn(1).thenReturn(2);
        when(resultSet.getString("username")).thenReturn("User1").thenReturn("User2");
        when(resultSet.getString("password")).thenReturn("User1Pass").thenReturn("User2Pass");
        when(resultSet.getString("email")).thenReturn("User1@example.com").thenReturn("User2@example.com");
        when(resultSet.getInt("age")).thenReturn(30).thenReturn(25);

        List<User> users = userDAO.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("User1", users.get(0).getUsername());
        assertEquals("User2", users.get(1).getUsername());
    }

    @Test
    public void getUserById_ReturnsCorrectUser() throws SQLException {
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("username")).thenReturn("User1");
        when(resultSet.getString("password")).thenReturn("UserPass1");
        when(resultSet.getString("email")).thenReturn("User1@example.com");
        when(resultSet.getInt("age")).thenReturn(30);

        User user = userDAO.getUserById(1);

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("User1", user.getUsername());
    }

    @Test
    public void usernameExists_ReturnsTrueWhenUsernameExists() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1); 
        boolean exists = userDAO.usernameExists("User1");

        assertTrue(exists);
    }

    @Test
    public void createUser_AddsUserSuccessfully() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(resultSet.next()).thenReturn(false); 
        User newUser = new User();
        newUser.setUsername("NewUser");
        newUser.setPassword("password");
        newUser.setEmail("newuser@example.com");
        newUser.setAge(25);

        assertDoesNotThrow(() -> userDAO.createUser(newUser));
    }

    @Test
    public void updateUser_UpdatesUserSuccessfully() throws SQLException {
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("ExistingUser");
        existingUser.setPassword("newpass");
        existingUser.setEmail("existinguser@example.com");
        existingUser.setAge(26);

        assertDoesNotThrow(() -> userDAO.updateUser(existingUser));
    }

    @Test
    public void deleteUser_DeletesUserSuccessfully() throws SQLException {
        doNothing().when(preparedStatement).setInt(anyInt(), anyInt());
        when(preparedStatement.executeUpdate()).thenReturn(1); 

        assertDoesNotThrow(() -> userDAO.deleteUser(1));
    }
}
