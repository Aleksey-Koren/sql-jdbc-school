package com.foxminded.sql_jdbc_runner.dao.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.foxminded.sql_jdbc_runner.dao.DaoRuntimeException;


public final class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private PropertiesUtil() {

    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        try(InputStream stream = PropertiesUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")){
            if(stream == null) {
                throw new DaoRuntimeException("properties file is not readed");
            }
            PROPERTIES.load(stream);
        } catch (IOException e) {
            throw new DaoRuntimeException("PROPERTIES is not initialized", e);
        }
    }
}