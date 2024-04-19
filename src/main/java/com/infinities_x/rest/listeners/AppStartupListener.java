package com.infinities_x.rest.listeners;


import com.infinities_x.rest.config.FeatureToggleConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String configPath = System.getProperty("feature.config.path"); 
        FeatureToggleConfig.loadConfigurations(configPath);
        System.out.println("Feature toggles loaded from " + configPath);
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
       
    }
}
