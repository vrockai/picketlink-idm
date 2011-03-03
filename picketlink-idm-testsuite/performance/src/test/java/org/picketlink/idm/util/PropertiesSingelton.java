/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author vrockai
 */
public class PropertiesSingelton {

    private static String propPath = "src/test/resources/picketlink.properties";
    private static PropertiesSingelton instance = null;
    private Properties properties = new Properties();

    protected PropertiesSingelton(Properties p) {
        properties = p;
    }

    public static PropertiesSingelton getInstance() {

        if (instance == null) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(propPath));
                Logger.getLogger(PropertiesSingelton.class.getName()).info("Property file loaded: " + propPath);
            } catch (IOException ex) {
                Logger.getLogger(PropertiesSingelton.class.getName()).error("Unable to read property file: " + propPath);
                Logger.getLogger(PropertiesSingelton.class.getName()).error(ex);
            }

            instance = new PropertiesSingelton(p);
        }
        return instance;
    }

    public void saveProperties() {
        try {
            properties.store(new FileOutputStream(propPath), null);
            Logger.getLogger(PropertiesSingelton.class.getName()).info("Property file saved: " + propPath);
        } catch (IOException ex) {
            Logger.getLogger(PropertiesSingelton.class.getName()).error("Unable to save property file: " + propPath);
            Logger.getLogger(PropertiesSingelton.class.getName()).error(ex);
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
