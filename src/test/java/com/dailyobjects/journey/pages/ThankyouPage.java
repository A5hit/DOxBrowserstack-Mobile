package com.dailyobjects.journey.pages;

import com.dailyobjects.journey.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ThankyouPage {

    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath ="//h1[normalize-space()='ORDER RECEIVED']")
    private WebElement orderReceivedMessage;

    @FindBy(xpath ="//div[@class='summary-section section-wrapper']//p[1]" )
    private WebElement orderId;

    @FindBy(xpath = "//span[contains(text(),'View order details')]")
    private WebElement orderDetails;


    public ThankyouPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.WAIT_TIMEOUT));
        PageFactory.initElements(driver, this);
    }

    public WebElement getOrderReceivedMessage() {
        wait.until(ExpectedConditions.visibilityOf(orderReceivedMessage));
        return orderReceivedMessage;
    }

    public WebElement getOrderId() {
        wait.until(ExpectedConditions.visibilityOf(orderId));
        return orderId;
    }

    public WebElement getOrderDetails() {
        wait.until(ExpectedConditions.visibilityOf(orderDetails));
        return orderDetails;
    }


}
