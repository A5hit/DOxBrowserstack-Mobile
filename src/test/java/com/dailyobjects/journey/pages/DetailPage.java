package com.dailyobjects.journey.pages;

import com.dailyobjects.journey.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class DetailPage {
    WebDriver driver;
    WebDriverWait wait;

    private final String product_url = ConfigReader.Base_url + "cable-protector/dp?f=pid~CABLE-PROTECTOR";

    @FindBy(xpath = "//img[@alt='wishlist_black.png']")
    private WebElement addToWishListButton; // Add to Wish List button on Detail Page

    @FindBy(xpath = "//h1[normalize-space()='Cable Protector - Set of 2']")
    private WebElement productTitleDP;

    @FindBy(xpath = "//div[@class='hide-gt-xs bottom-sticky-bar-mobile']//span[@class='mdc-button__label']")
    private WebElement addToCartListButton; // Product Add-to-cart button on Detail Page

    @FindBy(css = "div[class='hide-gt-xs bottom-sticky-bar-mobile'] span[class='mdc-button__label']")
    private WebElement goToCartButton; // Go to Cart button on Overlay


    public DetailPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.WAIT_TIMEOUT));
        PageFactory.initElements(driver, this);
    }

    public WebElement getAddToWishListButtonDP() {
        wait.until(ExpectedConditions.elementToBeClickable(addToWishListButton));
        return addToWishListButton;
    }

    public WebElement getAddToCartButtonDP() {
        wait.until(ExpectedConditions.elementToBeClickable(addToCartListButton));
        return addToCartListButton;
    }

    public WebElement getOverlayGoToCartButton() {
        wait.until(ExpectedConditions.elementToBeClickable(goToCartButton));
        return goToCartButton;
    }

    public void getProductAddedToCart() {
        getAddToCartButtonDP().click();
        try {
            if (getOverlayGoToCartButton().isDisplayed()) {
                getAddToCartButtonDP().click();
            } else {
                getOverlayGoToCartButton().click();
            }
        } catch (Exception ignored) {
            getAddToCartButtonDP().click();
        }
    }

    public String getProductTitleDP() {
        wait.until(ExpectedConditions.visibilityOf(productTitleDP));
        return productTitleDP.getText();
    }



}
