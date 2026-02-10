package com.dailyobjects.journey.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    public static final String Base_url = getValuePropertyFromConfig("BASE_URL");
    public static final String PROFILE_PATH =getValuePropertyFromConfig("Browser_profile_path") ;
    public static String BROWSER = getValuePropertyFromConfig("Browser");
    public static String HEADLESS = getValuePropertyFromConfig("Browser_headless");
    public static Integer WAIT_TIMEOUT = Integer.valueOf(getValuePropertyFromConfig("Wait_timeout"));
    public static String PROFILE = getValuePropertyFromConfig("User_browser_profile");
    public static String LOGGED_IN_MARKER_CSS = getValuePropertyFromConfig("Logged_in_marker_css");


    public static String getValuePropertyFromConfig(String key) {
        Properties prop = new Properties();
        try {
            FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\src/test/resources/config.properties");
            prop.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty(key);
    }



}
