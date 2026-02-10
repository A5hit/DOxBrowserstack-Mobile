package com.dailyobjects.journey.pages;

import com.dailyobjects.journey.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class OrderSummaryPage {

    WebDriver driver;
    WebDriverWait wait;

    @FindBy(xpath = "//input[@placeholder='Full Name']")
    private WebElement fullNameInput; // Full Name input field on Order Summary Page

    @FindBy(xpath = "//input[@placeholder='Mobile']")
    private WebElement mobileInput; // Mobile input field on Order Summary Page

    @FindBy(xpath = "//input[@placeholder='Email Address']")
    private WebElement emailInput; // Email input field on Order Summary Page

    @FindBy(xpath = "//input[@placeholder='Pincode']")
    private WebElement pincodeInput; // Pincode input field on Order Summary Page

    @FindBy(xpath = "//input[@placeholder='City']")
    private WebElement cityInput; // City input field on Order Summary Page

    @FindBy(xpath = "//input[@placeholder='State']")
    private WebElement stateInput; // State input field on Order Summary Page

    @FindBy(xpath = "//input[@placeholder='Flat No/Building, Street Name']")
    private WebElement addressInput; // Address input field on Order Summary Page

    @FindBy(xpath = "//input[@placeholder='Area/Locality']")
    private WebElement areaInput; // Area/Locality input field on Order Summary Page

    @FindBy(xpath = "//span[contains(normalize-space(),'ADD ADDRESS')]")
    private WebElement addAddressButton; // ADD ADDRESS button on Order Summary Page

    @FindBy(xpath = "//span[contains(normalize-space(),'CONTINUE')]")
    private WebElement continueButton; // CONTINUE TO PAYMENT button on Order Summary Page

    @FindBy(xpath = "//p[normalize-space()='COD']")
    private WebElement codOption; // COD payment option on Order Summary Page

    @FindBy(xpath = "//span[normalize-space()='PLACE ORDER']")
    private WebElement placeOrderButton; // PLACE ORDER button on Order Summary Page

    

    public OrderSummaryPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.WAIT_TIMEOUT));
        PageFactory.initElements(driver, this);
    }   

    public void fillShippingAddress() {
        fullNameInput.sendKeys("Test User");
        mobileInput.sendKeys("9350342053");
        emailInput.sendKeys("ashit@dailyobjects.com");
        pincodeInput.sendKeys("110047");
        cityInput.sendKeys("New Delhi");
        stateInput.sendKeys("Delhi");
        addressInput.sendKeys("123 Test Street");
        areaInput.sendKeys("Test Area");
    }

    public void clickAddAddressButton() {
        wait.until(ExpectedConditions.elementToBeClickable(addAddressButton));
        addAddressButton.click();
    }

    public void clickContinueButton() {
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        continueButton.click();
    }

    public void selectCODPaymentOption() {
        wait.until(ExpectedConditions.elementToBeClickable(codOption));
        codOption.click();
    }

    public void clickPlaceOrderButton() {
        wait.until(ExpectedConditions.elementToBeClickable(placeOrderButton));
        placeOrderButton.click();
    }


}
