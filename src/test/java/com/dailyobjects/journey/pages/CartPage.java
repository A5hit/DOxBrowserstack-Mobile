package com.dailyobjects.journey.pages;

import com.dailyobjects.journey.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CartPage {
    WebDriver driver;
    WebDriverWait wait;

    private final String CartPageURL = ConfigReader.Base_url + "/bp";

    @FindBy(xpath = "//div[@class='product-wrapper row-start-stretch']")
    private WebElement ProductCard;

    @FindBy(xpath = "//p[@class='product-title']")
    private WebElement productTitlesBP;  // Product Titles on Cart Page

    @FindBy(xpath = "//span[@class=\"mdc-button__label\"][text()=' CHECKOUT ']")
    private WebElement proceedToCheckoutButton; // Proceed to Checkout button on Cart Page

    @FindBy(xpath = "//button/img[@src=\"https://images.dailyobjects.com/marche/icons/bin.png?tr=cm-pad_resize,v-3\"]")
    private WebElement removeProductButton;

    @FindBy(xpath = "//div[@class='mat-mdc-snack-bar-label mdc-snackbar__label']")
    private WebElement removeProductConfirmMessage;

    @FindBy(xpath = "//h2[normalize-space()='YOUR SHOPPING CART IS EMPTY']")
    private WebElement emptyCartMessage;        // Empty Cart Message

    @FindBy(xpath = "//span[normalize-space()='REMOVE']")
    private WebElement confirmRemoveButton;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.WAIT_TIMEOUT));

        PageFactory.initElements(driver, this);
    }

    public void getRemoveProductConfirmMessage(){
        wait.until(ExpectedConditions.visibilityOf(removeProductConfirmMessage));
    }

    public String getProductTitleBP() {
        wait.until(ExpectedConditions.visibilityOf(productTitlesBP));
        return productTitlesBP.getText();
    }

    public void clickProceedToCheckoutButton() {
        wait.until(ExpectedConditions.elementToBeClickable(proceedToCheckoutButton));
        proceedToCheckoutButton.click();
    }

    public boolean isRemoveButtonVisible() {
        wait.until(ExpectedConditions.elementToBeClickable(removeProductButton));
        return removeProductButton.isDisplayed();

    }

    public WebElement getConfirmRemoveButton() {
        wait.until(ExpectedConditions.elementToBeClickable(confirmRemoveButton));
        return confirmRemoveButton;
    }

    public void getRemoveButton() {
        wait.until(ExpectedConditions.elementToBeClickable(removeProductButton));
    }

    public WebElement getEmptyCartMessage() {
        wait.until(ExpectedConditions.elementToBeClickable(emptyCartMessage));
        return emptyCartMessage;
    }

    public void removeProductFromCart() {
            wait.until(ExpectedConditions.elementToBeClickable(removeProductButton)).click();
            wait.until(ExpectedConditions.elementToBeClickable(getConfirmRemoveButton())).click();
            getRemoveProductConfirmMessage();

    }

    public boolean isCartEmpty() {
        if(removeProductButton.isDisplayed() || getEmptyCartMessage().isDisplayed()) {
            return true;
        }else {
            return false;
        }
    }

    public void getProductAddedToCart() {
    }
}
