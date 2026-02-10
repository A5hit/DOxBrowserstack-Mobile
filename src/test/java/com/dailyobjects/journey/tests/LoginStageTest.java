// src/test/java/com/dailyobjects/journey/tests/LoginStageTest.java
package com.dailyobjects.journey.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Cookie;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;

public class LoginStageTest {

    WebDriver driver;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("mobileEmulation",
                java.util.Collections.singletonMap("deviceName", "iPhone X"));
        driver = new ChromeDriver(options);
    }

    @Test
    public void manualLoginTest() throws InterruptedException, IOException {
        driver.get("https://dev.marche.dailyobjects.com/login");
        // Wait for manual login (e.g., 2 minutes)
        Thread.sleep(120_000);

        // Save cookies to file after login
        Set<Cookie> cookies = driver.manage().getCookies();
        try (FileOutputStream fos = new FileOutputStream("cookies.data");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(cookies);
        }
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
