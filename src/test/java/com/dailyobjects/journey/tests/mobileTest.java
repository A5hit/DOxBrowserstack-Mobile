package com.dailyobjects.journey.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class mobileTest {
    WebDriver driver;
    String BaseURL = "https://dev.marche.dailyobjects.com/";

    @BeforeClass
    public void setUp() throws MalformedURLException {
        System.setProperty("browserstack.config", "browserstack.yml");

        String user = System.getenv("BROWSERSTACK_USERNAME");
        String key = System.getenv("BROWSERSTACK_ACCESS_KEY");
        if (user == null || key == null) {
            throw new IllegalStateException("Set BROWSERSTACK_USERNAME and BROWSERSTACK_ACCESS_KEY env vars.");
        }

        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("browserName", "chrome");

        Map<String, Object> bstack = new HashMap<>();
        bstack.put("userName", user);
        bstack.put("accessKey", key);
        bstack.put("deviceName", "Samsung Galaxy S23");
        bstack.put("osVersion", "14.0");
        bstack.put("realMobile", "true");
        bstack.put("deviceOrientation", "portrait");
        bstack.put("projectName", "DO-Journey");
        bstack.put("buildName", "DO-Journey-" + System.currentTimeMillis());
        bstack.put("sessionName", "mobileTest");

        caps.setCapability("bstack:options", bstack);
        driver = new RemoteWebDriver(new URL("https://hub-cloud.browserstack.com/wd/hub"), caps);
    }

    @AfterClass
    public void tearDown(){
        if(driver != null){
            driver.quit();
        }
    }

    @Test
    public void mobileRun() throws InterruptedException {
        driver.get(BaseURL + "cable-protector/dp?f=pid~CABLE-PROTECTOR");
        Thread.sleep(5000);
        List<WebElement> clickAddToCart = driver.findElements(By.xpath("//span[normalize-space()='ADD TO CART']"));
        for (WebElement element : clickAddToCart) {
            try{
                element.click();
            }catch (Exception e){
                System.out.println();
            }
        }

    }
}
