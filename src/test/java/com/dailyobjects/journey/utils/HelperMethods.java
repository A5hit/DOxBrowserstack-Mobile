package com.dailyobjects.journey.utils;

import com.dailyobjects.journey.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HelperMethods {

    private static WebDriverWait getWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.WAIT_TIMEOUT));
    }

    public static void waitForElementToBeVisible(WebDriver driver, WebElement element) {
        getWait(driver).until(ExpectedConditions.visibilityOf(element));
    }

    public static void waitForElementToBeClickable(WebDriver driver, WebElement element) {
        getWait(driver).until(ExpectedConditions.elementToBeClickable(element));
    }

    public static void clickElement(WebElement element) {
        element.click();
    }

    public static void enterText(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    public static String getTextFromElement(WebElement element) {
        return element.getText();
    }

    public static void selectDropdownByVisibleText(WebElement element, String visibleText) {
        new org.openqa.selenium.support.ui.Select(element).selectByVisibleText(visibleText);
    }

    public static boolean isElementDisplayed(WebElement element) {
        return element.isDisplayed();
    }


}