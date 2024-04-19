package com.infinities_x.rest.config;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FeatureToggleConfig {

    private static final String DEFAULT_CONFIG_FILE_PATH = "features-config.xml"; 
    private static String configFile = DEFAULT_CONFIG_FILE_PATH;
    private static final Map<String, Boolean> featureToggles = new HashMap<>();

    public static void loadConfigurations(String filePath) {
        configFile = filePath != null ? filePath : DEFAULT_CONFIG_FILE_PATH; 
        featureToggles.clear(); 

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream configFileStream;

            if (!DEFAULT_CONFIG_FILE_PATH.equals(configFile)) {
                configFileStream = new FileInputStream(new File(configFile));
            } else {
                
                configFileStream = FeatureToggleConfig.class.getClassLoader().getResourceAsStream(configFile);
            }

            Document doc = dBuilder.parse(configFileStream);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("feature");

            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                String name = element.getAttribute("name");
                boolean enabled = Boolean.parseBoolean(element.getAttribute("enabled"));
                featureToggles.put(name, enabled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isFeatureEnabled(String featureName) {
        return featureToggles.getOrDefault(featureName, false);
    }


    public static Map<String, Boolean> getFeatureToggles() {
        return Collections.unmodifiableMap(featureToggles);
    }
    

    public static void reloadDefaultConfigurations() {
        loadConfigurations(DEFAULT_CONFIG_FILE_PATH);
    }
}
