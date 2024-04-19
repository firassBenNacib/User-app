package com.infinities_x.rest;



import com.infinities_x.rest.service.FeatureToggleService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/feature-toggles")
public class FeatureToggleEndpoint {

	@Inject
	private FeatureToggleService featureToggleService;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFeatureToggles() {
        Map<String, Boolean> toggles = featureToggleService.getFeatureToggles();
        return Response.ok(toggles).build();
    }
}
