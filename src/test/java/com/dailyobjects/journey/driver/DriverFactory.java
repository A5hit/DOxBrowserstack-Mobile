package com.dailyobjects.journey.driver;

import com.dailyobjects.journey.config.ConfigReader;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    protected WebDriver driver;

    private final String isheadless = ConfigReader.HEADLESS;
    private final Integer waitTimeout = ConfigReader.WAIT_TIMEOUT;

    public WebDriver initializeDriver(String platform) {
        try {
            if (platform == null) platform = "LOCAL_CHROME";

            switch (platform.toUpperCase()) {
                case "IOS_SAFARI":
                    driver = createBrowserStackIOS("iPhone 15", "17");
                    break;

                case "ANDROID_CHROME":
                    driver = createBrowserStackAndroid("Samsung Galaxy S23", "14.0");
                    break;
                case "BROWSERSTACK_SDK":
                    driver = createBrowserStackSdkDriver();
                    break;

                default:
                    driver = initLocalChrome(isheadless);
            }

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            return driver;

        } catch (Exception e) {
            Assert.fail("Driver init failed", e);
            return null;
        }
    }

    private WebDriver initLocalChrome(String isheadless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        if ("true".equalsIgnoreCase(isheadless)) {
            options.addArguments("--headless=new");
        }
        return new ChromeDriver(options);
    }

    private WebDriver createBrowserStackSdkDriver() throws Exception {
        System.setProperty("browserstack.config", "browserstack.yml");
        MutableCapabilities caps = new MutableCapabilities();
        return new RemoteWebDriver(new URL("https://hub-cloud.browserstack.com/wd/hub"), caps);
    }

    private WebDriver createBrowserStackIOS(String deviceName, String osVersion) throws Exception {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("browserName", "safari");

        Map<String, Object> bstack = commonBstackOptions();
        bstack.put("deviceName", deviceName);
        bstack.put("osVersion", osVersion);
        bstack.put("realMobile", "true");
        bstack.put("deviceOrientation", "portrait");

        // Important for SSO / multi-domain flows on iOS:
        // BrowserStack exposes this as preventCrossSiteTracking (default true). :contentReference[oaicite:1]{index=1}
        bstack.put("preventCrossSiteTracking", "false");

        caps.setCapability("bstack:options", bstack);
        return new RemoteWebDriver(new URL("https://hub-cloud.browserstack.com/wd/hub"), caps);
    }

    private WebDriver createBrowserStackAndroid(String deviceName, String osVersion) throws Exception {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("browserName", "chrome");

        Map<String, Object> bstack = commonBstackOptions();
        bstack.put("deviceName", deviceName);
        bstack.put("osVersion", osVersion);
        bstack.put("realMobile", "true");
        bstack.put("deviceOrientation", "portrait");

        caps.setCapability("bstack:options", bstack);
        return new RemoteWebDriver(new URL("https://hub-cloud.browserstack.com/wd/hub"), caps);
    }

    private Map<String, Object> commonBstackOptions() {
        String user = System.getenv("BROWSERSTACK_USERNAME");
        String key = System.getenv("BROWSERSTACK_ACCESS_KEY");
        if (user == null || key == null) {
            throw new RuntimeException("Set BROWSERSTACK_USERNAME and BROWSERSTACK_ACCESS_KEY env vars.");
        }

        Map<String, Object> bstack = new HashMap<>();
        bstack.put("userName", user);
        bstack.put("accessKey", key);

        bstack.put("projectName", "DO-Journey");
        bstack.put("buildName", "DO-Journey-" + System.currentTimeMillis());
        bstack.put("sessionName", "OrderPurchase");

        // (Optional) useful debug toggles
        bstack.put("debug", "true");
        bstack.put("networkLogs", "true");

        return bstack;
    }

    public void quitDriver() {
        if (driver != null) driver.quit();
    }
}
