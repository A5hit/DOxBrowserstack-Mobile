package com.dailyobjects.journey.pages;

import com.dailyobjects.journey.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AddressPage {

    WebDriver driver;
    WebDriverWait wait;

    @FindBy(xpath = "//span[normalize-space()='Delete']")
    WebElement deleteButton;

    @FindBy(xpath = "//span[normalize-space()='DELETE']")
    WebElement deleteConfirmButton;

    @FindBy(xpath = "//h2[normalize-space()='NO ADDRESS']")
    WebElement noAddress;

    public AddressPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.WAIT_TIMEOUT));
        PageFactory.initElements(driver, this);
    }

    public void clickDeleteButton() {
        wait.until(ExpectedConditions.visibilityOf(deleteButton));
        deleteButton.click();
    }
    public void clickDeleteConfirmButton() {
        wait.until(ExpectedConditions.visibilityOf(deleteConfirmButton));
        deleteConfirmButton.click();
    }

    public boolean checkNoAddress() {
        wait.until(ExpectedConditions.visibilityOf(noAddress));
         return noAddress.isDisplayed();
    }




}
