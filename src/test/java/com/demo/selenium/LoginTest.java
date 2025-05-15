package com.demo.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.nio.file.Files;
import java.io.File;

public class LoginTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:8087";

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Tăng thời gian chờ lên 15s
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30)); // Đảm bảo trang tải trong 30s
    }

    @Test
    public void testLoginSuccessSeeker_LG001() {
        driver.get(BASE_URL + "/account/login");

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameField.sendKeys("mimi");

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        passwordField.sendKeys("Trami2@3");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/home/index"));
        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL + "/home/index");
    }

    @Test
    public void testLoginSuccessEmployer_LG002() {
        driver.get(BASE_URL + "/account/login");

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameField.sendKeys("emmi");

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        passwordField.sendKeys("Trami2@3");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/employer/dashboard"));
        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL + "/employer/dashboard");
    }

    @Test
    public void testLoginWrongPassword_LG003() {
        driver.get(BASE_URL + "/account/login");

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameField.sendKeys("mimi");

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        passwordField.sendKeys("WrongPass@123");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/account/login?error"));
        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL + "/account/login?error");
    }

    @Test
    public void testLoginNonExistentUsername_LG004() {
        driver.get(BASE_URL + "/account/login");

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameField.sendKeys("nonexistent");

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        passwordField.sendKeys("Test@123");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/account/login?error"));
        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL + "/account/login?error");
    }

    @Test
    public void testLoginEmptyUsername_LG005() {
        driver.get(BASE_URL + "/account/login");

        // Gỡ bỏ required để có thể submit
        ((JavascriptExecutor) driver).executeScript("document.getElementById('username').removeAttribute('required');");

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        passwordField.sendKeys("Test@123");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/account/login?error"));
        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL + "/account/login?error");
    }


    @Test
    public void testLoginEmptyPassword_LG006() {
        driver.get(BASE_URL + "/account/login");

        ((JavascriptExecutor) driver).executeScript("document.getElementById('password').removeAttribute('required');");

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameField.sendKeys("mimi");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/account/login?error"));
        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL + "/account/login?error");
    }


    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            try {
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Files.copy(screenshot.toPath(), new File("screenshot-" + result.getName() + ".png").toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (driver != null) {
            driver.quit();
        }
    }
}