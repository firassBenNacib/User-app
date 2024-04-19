package com.infinities_x.rest.config;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ConfigAgeLoader {

    private static final String DEFAULT_CONFIG_FILE_PATH = "src/main/resources/configAge.xml";
    private static Map<Integer, Integer> userAges = new HashMap<>();
    private static int defaultAge;

    public static void loadConfigurations(String filePath) {
        String configFile = filePath != null ? filePath : DEFAULT_CONFIG_FILE_PATH;
        userAges.clear(); 

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream configFileStream = new FileInputStream(new File(configFile));

            Document doc = dBuilder.parse(configFileStream);
            doc.getDocumentElement().normalize();


            Element defaultAgeElement = (Element) doc.getElementsByTagName("defaultAge").item(0);
            defaultAge = Integer.parseInt(defaultAgeElement.getTextContent());

     
            NodeList nList = doc.getElementsByTagName("user");
            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                int id = Integer.parseInt(element.getAttribute("id"));
                int age = Integer.parseInt(element.getAttribute("age"));
                userAges.put(id, age);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer getAgeForUserId(int userId) {
        return userAges.containsKey(userId) ? userAges.get(userId) : null;
    }

    public static int getDefaultAge() {
        return defaultAge;
    }


  
    public static void reloadDefaultConfigurations() {
        loadConfigurations(DEFAULT_CONFIG_FILE_PATH);
    }
}
