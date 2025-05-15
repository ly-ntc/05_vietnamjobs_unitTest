package com.demo.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class RegisterTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:8087";

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testRegisterSuccess_RG001() {
        driver.get(BASE_URL + "/account/register");

        // Chọn radio button "Seeker"
        WebElement seekerRadio = driver.findElement(By.cssSelector("input[type='radio'][value='ROLE_SEEKER']"));
        seekerRadio.click();

        // Nhập thông tin
        driver.findElement(By.id("name")).sendKeys("newuser1");
        driver.findElement(By.id("email")).sendKeys("new1@example.com");
        driver.findElement(By.id("newPassword")).sendKeys("Test@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("Test@123");

        // Submit form
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // Chờ redirect tới trang login
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/account/login"));

        // Kiểm tra URL
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/account/login"));

        // Kiểm tra thông báo SweetAlert2
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".swal2-toast .swal2-html-container")
        ));
        String messageText = toast.getText();
        Assert.assertTrue(messageText.contains("Check your email to activate your account"));
    }

    @Test
    public void testRegisterExistingUsername_RG002() {
        driver.get(BASE_URL + "/account/register");

        // Chọn radio button "Seeker"
        WebElement seekerRadio = driver.findElement(By.cssSelector("input[type='radio'][value='ROLE_SEEKER']"));
        seekerRadio.click();

        // Nhập dữ liệu: Username đã tồn tại (acc1)
        driver.findElement(By.id("name")).sendKeys("newuser1");
        driver.findElement(By.id("email")).sendKeys("new1@example.com");
        driver.findElement(By.id("newPassword")).sendKeys("Test@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("Test@123");

        // Submit form
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // Chờ trang reload lại ở /account/register do đăng ký thất bại
        wait.until(ExpectedConditions.urlContains("/account/register"));

        // Kiểm tra thông báo lỗi (giả sử được render bằng popup hoặc thẻ chứa dòng chữ "Failed 3")
        // Cách đơn giản: kiểm tra có chứa nội dung thông báo thất bại nào đó
        Assert.assertTrue(driver.getPageSource().contains("Failed 3"));
    }


    @Test
    public void testRegisterInvalidEmail_RG003() {
        driver.get(BASE_URL + "/account/register");

        WebElement seekerRadio = driver.findElement(By.cssSelector("input[type='radio'][value='ROLE_SEEKER']"));
        seekerRadio.click();

        WebElement emailInput = driver.findElement(By.id("email"));
        driver.findElement(By.id("name")).sendKeys("newuser3");
        emailInput.sendKeys("invalid-email"); // email sai định dạng
        driver.findElement(By.id("newPassword")).sendKeys("Test@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("Test@123");

        // Thử submit form
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // Lấy message lỗi HTML5 validation trên trường email
        String validationMessage = (String) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].validationMessage;", emailInput);

        Assert.assertFalse(validationMessage.isEmpty());
        System.out.println("Validation message: " + validationMessage);
    }

    @Test
    public void testRegisterInvalidPassword_RG004() {
        driver.get(BASE_URL + "/account/register");

        // Chọn radio button "Seeker"
        WebElement seekerRadio = driver.findElement(By.cssSelector("input[type='radio'][value='ROLE_SEEKER']"));
        seekerRadio.click();

        // Nhập dữ liệu với mật khẩu không hợp lệ
        driver.findElement(By.id("name")).sendKeys("newuser5");
        driver.findElement(By.id("email")).sendKeys("new5@example.com");
        driver.findElement(By.id("newPassword")).sendKeys("password");  // Mật khẩu thiếu ký tự đặc biệt, viết hoa, số ...
        driver.findElement(By.id("confirmPassword")).sendKeys("password");

        // Submit form
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // Chờ span lỗi mật khẩu hiện lên
        WebElement errorNewPass = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorNewPass")));

        // Kiểm tra span lỗi mật khẩu có text (bất kỳ, không rỗng)
        Assert.assertFalse(errorNewPass.getText().trim().isEmpty());
    }

    @Test
    public void testRegisterPasswordMismatch_RG005() {
        driver.get(BASE_URL + "/account/register");

        // Chọn radio button "Seeker"
        WebElement seekerRadio = driver.findElement(By.cssSelector("input[type='radio'][value='ROLE_SEEKER']"));
        seekerRadio.click();

        // Nhập dữ liệu
        driver.findElement(By.id("name")).sendKeys("newuser4");
        driver.findElement(By.id("email")).sendKeys("new4@example.com");
        driver.findElement(By.id("newPassword")).sendKeys("Test@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("Test@124"); // Mismatch password

        // Submit form
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // Chờ span lỗi hiển thị
        WebElement errorConfirmPass = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorConfirmPass")));

        // Kiểm tra nội dung thông báo lỗi
        Assert.assertEquals(errorConfirmPass.getText(), "Mật khẩu xác nhận không giống với mật khẩu bạn vừa mới tạo.");
    }

    @Test
    public void testRegisterEmptyUsername_RG006() {
        driver.get(BASE_URL + "/account/register");

        // Chọn loại tài khoản
        WebElement seekerRadio = driver.findElement(By.cssSelector("input[type='radio'][value='ROLE_SEEKER']"));
        seekerRadio.click();

        // Không nhập username
        WebElement nameInput = driver.findElement(By.id("name")); // để trống
        driver.findElement(By.id("email")).sendKeys("new5@example.com");
        driver.findElement(By.id("newPassword")).sendKeys("Test@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("Test@123");

        // Submit form
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // Lấy lỗi HTML5 validation
        String validationMessage = (String) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].validationMessage;", nameInput);

        // Kiểm tra có thông báo lỗi (không cần đúng nội dung)
        Assert.assertFalse(validationMessage.isEmpty());
    }

    @Test
    public void testRegisterEmptyEmail_RG007() {
        driver.get(BASE_URL + "/account/register");

        // Chọn radio button "Seeker"
        WebElement seekerRadio = driver.findElement(By.cssSelector("input[type='radio'][value='ROLE_SEEKER']"));
        seekerRadio.click();

        // Nhập các thông tin khác, để trống email
        driver.findElement(By.id("name")).sendKeys("newuser6");
        driver.findElement(By.id("newPassword")).sendKeys("Test@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("Test@123");

        // Submit form
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // Kiểm tra thông báo trong modal
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement warningMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("empty")
        ));
        String messageText = warningMessage.getText();
        System.out.println(">> Nội dung thông báo: " + messageText);
        Assert.assertTrue(messageText.contains("Vui lòng nhập đầy đủ thông tin. Không được bỏ trống"));
    }

    @Test
    public void testRegisterEmptyPassword_RG008() {
        driver.get(BASE_URL + "/account/register");

        // Chọn radio button Seeker
        WebElement seekerRadio = driver.findElement(By.cssSelector("input[type='radio'][value='ROLE_SEEKER']"));
        seekerRadio.click();

        // Điền các thông tin trừ password
        driver.findElement(By.id("name")).sendKeys("newuser7");
        driver.findElement(By.id("email")).sendKeys("new7@example.com");

        // Submit form
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // Chờ cảnh báo xuất hiện (ví dụ là modal hoặc toast với id="empty")
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement warningMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("empty") // ✅ đảm bảo id="empty" đúng với HTML bạn đang dùng
        ));

        String messageText = warningMessage.getText();
        System.out.println(">> Nội dung thông báo: " + messageText);

        // Kiểm tra thông báo có đúng như kỳ vọng (không cần chính xác tuyệt đối nếu bạn không fix cứng)
        Assert.assertTrue(messageText.contains("Vui lòng nhập đầy đủ thông tin"));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}