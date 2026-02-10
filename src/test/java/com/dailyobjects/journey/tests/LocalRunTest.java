package com.dailyobjects.journey.tests;


import com.dailyobjects.journey.auth.AuthSession;
import com.dailyobjects.journey.auth.AuthSessionApplier;
import com.dailyobjects.journey.auth.AuthSessionManager;
import com.dailyobjects.journey.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.SkipException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class LocalRunTest extends DriverFactory {

    WebDriver driver;
    WebDriverWait wait;

    String BaseURL = "https://dev.marche.dailyobjects.com/";

    @Parameters({"platform"})
    @BeforeClass
    public void setup(@Optional("LOCAL_CHROME") String platform) throws InterruptedException {
        // Ensure we have a non-expired Cognito session for THIS origin
        AuthSession session = AuthSessionManager.ensureValid(BaseURL);

        this.driver = initializeDriver(platform);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(180));

        // Apply localStorage + cookies into the new browser/device session
        AuthSessionApplier.apply(driver, session);

        // Validate session by URL behavior:
        // Logged-in: BaseURL/map stays on /map
        // Logged-out: BaseURL/map redirects to /ap
//        String base = BaseURL.endsWith("/") ? BaseURL.substring(0, BaseURL.length() - 1) : BaseURL;
//        String mapUrl = base + "/map";
//        String apUrl = base + "/ap";
//
//        driver.get(mapUrl);
//        dismissStayOnThisSiteIfPresent();

//        boolean reachedAuthDecision;
//        try {
//            new WebDriverWait(driver, Duration.ofSeconds(180)).until(d -> {
//                String url = d.getCurrentUrl();
//                return url != null && (url.startsWith(mapUrl) || url.startsWith(apUrl));
//            });
//            reachedAuthDecision = true;
//        } catch (Exception ignored) {
//            reachedAuthDecision = false;
//        }

        navigateWithRefresh(BaseURL);
        String current = driver.getCurrentUrl();

        if (current.contains("CertificateWarning")) {
            throw new SkipException("iOS Safari certificate warning for dev URL. Current URL: " + current);
        }

//        if (!reachedAuthDecision) {
//            throw new RuntimeException("Auth check timed out. Current URL: " + current);
//        }

//        if (current != null && current.startsWith(apUrl)) {
//            if (isIOS()) {
//                throw new SkipException("iOS Safari cannot access localStorage on BrowserStack; session not applied. Redirected to " + current);
//            }
//            throw new RuntimeException("Auth session not applied. Redirected to " + current + " from " + mapUrl);
//        }
    }

    @AfterClass(alwaysRun = true)
    public void teardown() {
        quitDriver();
    }

    @Test
    public void orderPurchaseTest() throws InterruptedException {
        dismissStayOnThisSiteIfPresent();
        navigateWithRefresh(BaseURL+"uap");
        handleAddress();
        navigateWithRefresh(BaseURL + "cable-protector/dp?f=pid~CABLE-PROTECTOR");
        dismissStayOnThisSiteIfPresent();
        Thread.sleep(1000);
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[normalize-space()='ADD TO CART']")));
        List<WebElement> clickAddToCart = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[normalize-space()='ADD TO CART']")));
        for (WebElement element : clickAddToCart) {
           try{
               wait.until(ExpectedConditions.elementToBeClickable(element)).click();  // Click Add to cart
           }catch (Exception e){
               System.out.println();
           }
        }
        waitForDocumentReady();

        Thread.sleep(1000);
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[normalize-space()='GO TO CART']")));
        List<WebElement> clickGoToCart = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[normalize-space()='GO TO CART']")));
        for (WebElement element : clickGoToCart) {
            try{
               wait.until(ExpectedConditions.elementToBeClickable(element)).click();   // Click Go to Cart
            }catch (Exception e){
                System.out.println();
            }
        }

        waitForDocumentReady();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[normalize-space()='CHECKOUT']")));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='CHECKOUT']"))).click();

//        WebElement addressTitleEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[normalize-space()='ADD NEW ADDRESS']")));
//        String AddressPageTitle = addressTitleEl.getText();
//        String testPageTitle = "ADD NEW ADDRESS";
//        Assert.assertEquals(AddressPageTitle.strip(), testPageTitle);
        waitForDocumentReady();
        fillAddress();

        Thread.sleep(3000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='CONTINUE']"))).click();
        Thread.sleep(1000);
//        driver.navigate().refresh();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='COD']"))).click();
        Thread.sleep(1000);
//        driver.navigate().refresh();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='PLACE ORDER']"))).click();
        Thread.sleep(3000);
        waitForDocumentReady();
//        driver.navigate().refresh();
        String thankYouMsg = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1"))).getText();
        System.out.println(thankYouMsg);
        //Assert.assertEquals(thankYouMsg, "ORDER RECEIVED", "Order is not Placed");

    }

    private void fillAddress(){
        waitForDocumentReady();
        waitAndType(By.xpath("//input[@placeholder='Full Name']"), "Test");
        waitAndType(By.xpath("//input[@placeholder='Email Address']"), "ashit@dailyobjects.com");
        waitAndType(By.xpath("//input[@placeholder='Pincode']"), "110047");
        WebElement address1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Flat No/Building, Street Name']")));
        address1.clear();
        address1.sendKeys("Test Flat No/Building, Street Name");
        waitAndType(By.xpath("//input[@placeholder='Area/Locality']"), "Test Street");

        List<WebElement> AddAddressButton = driver.findElements(By.xpath("//span[normalize-space()='ADD ADDRESS']"));
        for (WebElement ele : AddAddressButton) {
            try{
                wait.until(ExpectedConditions.elementToBeClickable(ele)).click();
            }
            catch (Exception e){
                System.out.println();
            }
        }
    }

    private void handleAddress() {

        WebElement addressOption =  driver.findElement(By.xpath("//a[@href='/map']"));
        wait.until(ExpectedConditions.elementToBeClickable(addressOption)).click();

        By addressForm = By.xpath("//h1[normalize-space()='ADD NEW ADDRESS']");
        By addressBook = By.xpath("//h1[normalize-space()='ADDRESS BOOK']");
        By freshAddress = By.xpath("//span[.=' ADD NEW ADDRESS ']");
        By addNewAddress = By.xpath("//span[.=' ADD A NEW ADDRESS ']");
        By delete = By.xpath("//span[normalize-space()='Delete']");
        By deleteConfirm = By.xpath("//span[normalize-space()='DELETE']");

        // On address page only one state should be present: NO ADDRESS or DEFAULT ADDRESS
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(addressBook),
                ExpectedConditions.presenceOfElementLocated(freshAddress)
        ));

        while (!driver.findElements(addressBook).isEmpty()){
            waitForDocumentReady();
            if (!driver.findElements(addressBook).isEmpty()) {
                wait.until(ExpectedConditions.elementToBeClickable(delete)).click();
                waitForDocumentReady();
                wait.until(ExpectedConditions.elementToBeClickable(deleteConfirm)).click();
                waitForDocumentReady();
            }
            if (!driver.findElements(freshAddress).isEmpty()) {
                waitForDocumentReady();
            }
        }
    }

    private void waitAndType(By locator, String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        field.click();
        field.clear();
        field.sendKeys(value);
    }

    private void navigateWithRefresh(String url){
        driver.navigate().to(url);
        // Refresh only after the first load is complete to avoid racing hydration
        waitForDocumentReady();
        dismissStayOnThisSiteIfPresent();
    }

    private void waitForDocumentReady() {
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(d -> {
            Object state = ((org.openqa.selenium.JavascriptExecutor) d).executeScript("return document.readyState");
            return Objects.equals("complete", state);
        });
    }

    private void dismissStayOnThisSiteIfPresent() {
        By stayOnSite = By.xpath("//span[normalize-space()=\"STAY ON THIS SITE\"]");
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement button = shortWait.until(ExpectedConditions.presenceOfElementLocated(stayOnSite));
            button.click();
        } catch (Exception ignored) {
        }
    }

}


