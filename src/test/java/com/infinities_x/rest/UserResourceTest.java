package com.infinities_x.rest;

import com.infinities_x.rest.dao.UserDAO;
import com.infinities_x.rest.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserResourceTest {

    @Mock
    private UserDAO userDao;

    private UserResource userResource;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userResource = new UserResource(userDao);  
    }

    @Test
    public void getUsers_Success_ReturnsAllUsers() throws Exception {
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("User1");
        User user2 = new User();
        user2.setId(2);
        user2.setUsername("User2");

        when(userDao.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        Response response = userResource.getUsers();

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof List);
        assertEquals(2, ((List<?>) response.getEntity()).size());
    }

    @Test
    public void getUser_Found_ReturnsUser() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("User1");

        when(userDao.getUserById(1)).thenReturn(user);

        Response response = userResource.getUser(1);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(user, response.getEntity());
    }

    @Test
    public void getUser_NotFound_Returns404() throws Exception {
        when(userDao.getUserById(1)).thenReturn(null);

        Response response = userResource.getUser(1);

        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void createUser_Success_ReturnsCreated() throws Exception {
        User newUser = new User();
        newUser.setUsername("NewUser");

        doNothing().when(userDao).createUser(any(User.class));

        Response response = userResource.createUser(newUser);

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(newUser, response.getEntity());
    }

    @Test
    public void updateUser_Success_ReturnsUpdatedUser() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setUsername("UpdatedName");

        doNothing().when(userDao).updateUser(any(User.class));

        Response response = userResource.updateUser(1, updatedUser);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(updatedUser, response.getEntity());
    }

    @Test
    public void deleteUser_Success_ReturnsNoContent() throws Exception {
        doNothing().when(userDao).deleteUser(1);

        Response response = userResource.deleteUser(1);

        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void operations_HandleException_ReturnsInternalServerError() throws Exception {
        
        when(userDao.getUserById(1)).thenThrow(new RuntimeException("Database error"));

        Response response = userResource.getUser(1);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }
}
