package com.infinities_x.rest.service;

import com.infinities_x.rest.config.FeatureToggleConfig;
import javax.enterprise.context.ApplicationScoped;

import java.util.Map;

@ApplicationScoped
public class FeatureToggleService {

    public Map<String, Boolean> getFeatureToggles() {

        return FeatureToggleConfig.getFeatureToggles();
    }
}
