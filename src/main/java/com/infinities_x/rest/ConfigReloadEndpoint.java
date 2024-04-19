package com.infinities_x.rest;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.infinities_x.rest.config.ConfigAgeLoader;
import com.infinities_x.rest.config.FeatureToggleConfig;
import com.infinities_x.rest.dao.UserDAO;

@Path("/config")
public class ConfigReloadEndpoint {

    @GET
    @Path("/reload")
    @Produces(MediaType.APPLICATION_JSON)
    public Response reloadConfigurations(@QueryParam("path") String path, @QueryParam("agepath") String agePath) {
        
        if (path != null && !path.isEmpty()) {
            FeatureToggleConfig.loadConfigurations(path);
        }
        

        if (agePath != null && !agePath.isEmpty()) {
            ConfigAgeLoader.loadConfigurations(agePath);
            try {
                DataSource dataSource = lookupDataSource(new InitialContext());
                UserDAO userDao = new UserDAO(dataSource);
                userDao.updateUserAgesFromConfig(); 
                return Response.ok("Configurations reloaded and user ages updated.").build();
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating user ages: " + e.getMessage()).build();
            }
        }
        
        return Response.ok("Configurations reloaded.").build();
    }

    private DataSource lookupDataSource(InitialContext ctx) throws NamingException {
        try {

            return (DataSource) ctx.lookup("jdbc/OracleDS");
        } catch (NamingException e) {

            return (DataSource) ctx.lookup("java:/OracleDS");
        }
    }
}
