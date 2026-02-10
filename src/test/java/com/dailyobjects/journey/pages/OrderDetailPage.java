package com.dailyobjects.journey.pages;

import com.dailyobjects.journey.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class OrderDetailPage {
    WebDriver driver;
    WebDriverWait wait;

    @FindBy(xpath ="//span[normalize-space()='CANCEL']" )
    WebElement cancelButton;

    @FindBy(xpath = "//input[@type='checkbox']")
    WebElement checkbox;

    @FindBy(xpath = "//mat-label[normalize-space()='Select your concern *']")
    WebElement form;

    @FindBy(xpath = "//p[normalize-space()='Order Cancellation']")
    WebElement concern;

    @FindBy(xpath = "//p[normalize-space()='Cancel complete order']")
    WebElement reason;

    @FindBy(xpath = "//p[normalize-space()='Order placed by mistake']")
    WebElement sub_reason;

    @FindBy(xpath = "//span[@class='mdc-button__label']")
    WebElement submitCancel;

    @FindBy(xpath = "//h1[normalize-space()='REQUEST SENT SUCCESSFULLY!']")
    WebElement successfulCancel;

    public OrderDetailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.WAIT_TIMEOUT));
        PageFactory.initElements(driver, this);
    }

    public void clickCancel() {
        wait.until(ExpectedConditions.visibilityOf(cancelButton));
        cancelButton.click();
    }

    public void setCheckbox() {
        wait.until(ExpectedConditions.visibilityOf(checkbox));
        checkbox.click();
    }

    public void setForm() {
        wait.until(ExpectedConditions.visibilityOf(form));
        form.click();
    }

    public void setConcern() {
        wait.until(ExpectedConditions.visibilityOf(concern));
        concern.click();
    }

    public void setReason() {
        wait.until(ExpectedConditions.visibilityOf(reason));
        reason.click();
    }

    public void setSub_reason() {
        wait.until(ExpectedConditions.visibilityOf(sub_reason));
        sub_reason.click();
    }

    public void clickSubmitCancel() {
        wait.until(ExpectedConditions.visibilityOf(sub_reason));
        sub_reason.click();
    }

    public void submitCancel() {
        wait.until(ExpectedConditions.visibilityOf(submitCancel));
        submitCancel.click();
    }

    public String cancelMsg() {
        wait.until(ExpectedConditions.visibilityOf(successfulCancel));
        return successfulCancel.getText();
    }
}
