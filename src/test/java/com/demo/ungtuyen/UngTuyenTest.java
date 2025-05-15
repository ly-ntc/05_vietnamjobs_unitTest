package com.demo.ungtuyen;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Duration;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UngTuyenTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.manage().window().maximize();
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }




    @Test
    @Order(1)
    public void UT001() {
        // 1. Đăng nhập
        driver.get("http://localhost:8087/login");
        driver.findElement(By.id("username")).sendKeys("acc1");
        driver.findElement(By.id("password")).sendKeys("Camly123@");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        loginBtn.click();

        wait.until(ExpectedConditions.urlContains("/home"));

        // 2. Truy cập chi tiết công việc
        driver.get("http://localhost:8087/home/posting/15");

        // 3. Nhấn nút “Apply now”
        WebElement applyNowButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Apply now')]")));
        // Scroll đến nút để chắc chắn hiển thị đúng vị trí
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", applyNowButton);
        // Click nút bằng JS để tránh bị che khuất
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyNowButton);

        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(),'Apply')]")));
        confirmButton.click();

        // 5. Đợi toast message "Submitted successfully!" hiện ra
        WebElement successMsg = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".swal2-toast.swal2-show")));
        // 6. Kiểm tra nội dung thông báo
        String actualMessage = successMsg.getText();
        System.out.println("Toast message: " + actualMessage);
        Assertions.assertTrue(actualMessage.contains("Thực hiện ứng tuyển  thành công"), "Toast không chứa thông báo mong đợi.");

    }

    @Test
    @Order(2)
    public void UT002() {
        // 1. Đăng nhập
        driver.get("http://localhost:8087/login");
        driver.findElement(By.id("username")).sendKeys("acc5"); // Tài khoản đã có CV
        driver.findElement(By.id("password")).sendKeys("Camly123@");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        loginBtn.click();

        wait.until(ExpectedConditions.urlContains("/home"));

        // 2. Truy cập chi tiết công việc với jobId = 15
        driver.get("http://localhost:8087/home/posting/15");

        // 3. Nhấn nút “Apply now”
        WebElement applyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Apply now')]")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", applyNowButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyNowButton);

        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(),'Apply')]")));
        confirmButton.click();

        // 4. Chờ toast thông báo hiện ra
        WebElement successToast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".swal2-toast.swal2-show")));

        // 5. Kiểm tra nội dung thông báo
        String actualMessage = successToast.getText();
        System.out.println("Toast message: " + actualMessage);
        Assertions.assertTrue(actualMessage.contains("Thực hiện ứng tuyển thất bại"),
                "Không thấy thông báo thành công khi ứng tuyển.");
    }

    @Test
    @Order(3)
    public void UT003() {
        try {
            // 1. Đăng nhập
            driver.get("http://localhost:8087/login");
            driver.findElement(By.id("username")).sendKeys("acc1");
            driver.findElement(By.id("password")).sendKeys("Camly123@");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
            loginBtn.click();
            wait.until(ExpectedConditions.urlContains("/home"));

            // 2. Truy cập chi tiết công việc (job ID 34)
            driver.get("http://localhost:8087/home/posting/15");

            // 3. Nhấn "Apply now"
            WebElement applyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Apply now')]")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", applyNowButton);
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyNowButton);

            // 4. Xác nhận ứng tuyển
            WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@type='submit' and contains(.,'Apply')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmButton);

            // Chờ thông báo thành công
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".swal2-toast.swal2-show")));

            // 5. Truy cập "Công việc đã ứng tuyển"
            WebElement userDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a.dropdown-user-link")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", userDropdown);

            Thread.sleep(1000); // Chờ dropdown mở

            WebElement jobAppliedLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(@href,'/seeker/profile/postingapplied')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", jobAppliedLink);

            // Chờ trang tải xong
            wait.until(ExpectedConditions.urlContains("/postingapplied"));
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));

            // 6. Kiểm tra job ID 34 có trong danh sách
            // Tìm thẻ a chứa link đến job ID 34
            WebElement jobLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(@href,'/home/posting/15')]")));

            // Lấy toàn bộ card chứa job
            WebElement jobCard = jobLink.findElement(By.xpath("./ancestor::div[contains(@class,'card mb-md-1')]"));

            // Verify các thông tin:
            // - Nút "Applied"
            WebElement appliedButton = jobCard.findElement(
                    By.xpath(".//button[contains(@class,'btn-secondary') and contains(.,'Applied')]"));

            // - Thông báo "Your CV has been sent to the employer"
            WebElement statusMessage = jobCard.findElement(
                    By.xpath(".//div[contains(.,'Your CV has been sent to the employer')]"));

            // - Tên công việc (FE developer)
            WebElement jobTitle = jobCard.findElement(By.xpath(".//a/b[contains(.,'FE developer')]"));

            // - Tên công ty
            WebElement companyName = jobCard.findElement(By.xpath(".//div[contains(.,'Edupia')]"));

            // - Thông tin lương
            WebElement salaryInfo = jobCard.findElement(By.xpath(".//span[contains(@class,'text-warning')]"));

            Assertions.assertTrue(appliedButton.isDisplayed(), "Nút Applied không hiển thị");
            Assertions.assertTrue(statusMessage.isDisplayed(), "Thông báo trạng thái không hiển thị");
            Assertions.assertTrue(jobTitle.isDisplayed(), "Tên công việc không hiển thị");
            Assertions.assertTrue(companyName.isDisplayed(), "Tên công ty không hiển thị");
            Assertions.assertTrue(salaryInfo.isDisplayed(), "Thông tin lương không hiển thị");
            Assertions.assertEquals("Đăng tuyển thành công", appliedButton.getText(), "Button text không đúng!");
            System.out.println(" Job đã ứng tuyển xuất hiện trong danh sách, trạng thái: Đã ứng tuyển");

        } catch (Exception e) {
            // Chụp màn hình nếu fail

            throw new RuntimeException("Test thất bại: " + e.getMessage(), e);
        }
    }

    @Test
    @Order(4)
    public void UT004() {
        // 1. Đăng nhập
        driver.get("http://localhost:8087/login");
        driver.findElement(By.id("username")).sendKeys("acc1");
        driver.findElement(By.id("password")).sendKeys("Camly123@");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        loginBtn.click();
        wait.until(ExpectedConditions.urlContains("/home"));

        // 2. Ứng tuyển công việc 3 lần (jobId = 123)
        for (int i = 0; i < 3; i++) {
            driver.get("http://localhost:8087/home/posting/15");

            WebElement applyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Apply now')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", applyNowButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyNowButton);

            WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@type='submit' and contains(text(),'Apply')]")));
            confirmButton.click();

            // Đợi thông báo thành công
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".swal2-toast.swal2-show")));

            // Đợi một chút giữa các lần ứng tuyển
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 3. Thử ứng tuyển lần thứ 4
        driver.get("http://localhost:8087/home/posting/15");

        WebElement applyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Apply now')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", applyNowButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyNowButton);

        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(),'Apply')]")));
        confirmButton.click();

        // Kiểm tra thông báo lỗi
        WebElement errorMsg = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".swal2-toast.swal2-show")));

        String actualMessage = errorMsg.getText();
        System.out.println("Toast message: " + actualMessage);
        Assertions.assertTrue(actualMessage.contains("Bạn đã hết lượt ứng tuyển cho công việc này"),
                "Không hiển thị thông báo giới hạn ứng tuyển đúng");
    }


    @Test
    @Order(5)
    public void UT005() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Đăng nhập với tài khoản có CV
        driver.get("http://localhost:8087/login");
        driver.findElement(By.id("username")).sendKeys("acc1");
        driver.findElement(By.id("password")).sendKeys("Camly123@");
        driver.findElement(By.id("submit")).click();
        wait.until(ExpectedConditions.urlContains("/home"));

        // 2. Truy cập chi tiết công việc
        driver.get("http://localhost:8087/home/posting/15");

        // 3. Nhấn "Apply now"
        WebElement applyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Apply now')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyNowButton);

        // 4. Kiểm tra popup hiển thị CV
        try {
            // Kiểm tra popup hiển thị
            WebElement cvPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".modal-content")));
            Assertions.assertTrue(cvPopup.isDisplayed(), "Popup xem CV không hiển thị");

            // Kiểm tra nút đóng (chỉ cần tồn tại)
            WebElement closeButton = cvPopup.findElement(By.cssSelector("button.btn-close"));
            Assertions.assertTrue(closeButton.isDisplayed(), "Nút đóng không hiển thị");

            // Kiểm tra nút "View CV" (không cần kiểm tra text chính xác)
            WebElement viewCvButton = cvPopup.findElement(
                    By.xpath(".//button[contains(@class, 'btn-success') and contains(@class, 'me-md-1')]"));
            Assertions.assertTrue(viewCvButton.isDisplayed(), "Nút xem CV không hiển thị");
            System.out.println("Text nút xem CV: " + viewCvButton.getText());

            // Kiểm tra nút "Apply" (không cần kiểm tra text chính xác)
            WebElement applyButton = cvPopup.findElement(
                    By.xpath(".//button[@type='submit' and contains(@class, 'btn-success')]"));
            Assertions.assertTrue(applyButton.isDisplayed(), "Nút ứng tuyển không hiển thị");
            System.out.println("Text nút ứng tuyển: " + applyButton.getText());

        } catch (TimeoutException e) {
            Assertions.fail("Không tìm thấy popup xem CV: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    public void UT006() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        int rejectedJobId = 35; // Thay bằng job ID đã bị từ chối

        try {
            // 1. Đăng nhập
            driver.get("http://localhost:8087/login");
            driver.findElement(By.id("username")).sendKeys("acc1");
            driver.findElement(By.id("password")).sendKeys("Camly123@");
            driver.findElement(By.id("submit")).click();
            wait.until(ExpectedConditions.urlContains("/home"));

            // 2. Truy cập trang công việc đã ứng tuyển
            driver.get("http://localhost:8087/seeker/profile/postingapplied");

            // 3. Tìm công việc đã bị từ chối
            WebElement rejectedJobCard = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(@href,'/home/posting/" + rejectedJobId + "')]/ancestor::div[contains(@class,'card')]")));

            // 4. Kiểm tra trạng thái
            WebElement statusElement = rejectedJobCard.findElement(
                    By.xpath(".//button[contains(@class,'btn-danger')] | .//span[contains(@class,'status-text')]"));

            String actualStatus = statusElement.getText().trim();
            System.out.println("Trạng thái thực tế: " + actualStatus);

            Assertions.assertEquals("Đã từ chối", actualStatus, "Trạng thái không hiển thị đúng");

        } catch (TimeoutException e) {
            File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            try {
                FileUtils.copyFile(screenshot, new File("./screenshots/UT006_fail_" + System.currentTimeMillis() + ".png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Assertions.fail("Không tìm thấy công việc đã từ chối với ID: " + rejectedJobId);
        }
    }

    @Test
    @Order(7)
    public void UT007() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        int acceptedJobId = 37; // Thay bằng job ID đã được chấp nhận

        try {
            // 1. Đăng nhập
            driver.get("http://localhost:8087/login");
            driver.findElement(By.id("username")).sendKeys("acc1");
            driver.findElement(By.id("password")).sendKeys("Camly123@");
            driver.findElement(By.id("submit")).click();
            wait.until(ExpectedConditions.urlContains("/home"));

            // 2. Truy cập trang "Công việc đã ứng tuyển"
            driver.get("http://localhost:8087/seeker/profile/postingapplied");

            // 3. Tìm công việc đã được chấp nhận
            WebElement acceptedJobCard = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(@href,'/home/posting/" + acceptedJobId + "')]/ancestor::div[contains(@class,'card')]")));

            // 4. Kiểm tra trạng thái hiển thị là "Đã chấp nhận"
            WebElement statusElement = acceptedJobCard.findElement(
                    By.xpath(".//button[contains(@class,'btn-success')] | .//span[contains(@class,'status-text')]"));

            String actualStatus = statusElement.getText().trim();
            System.out.println("Trạng thái thực tế: " + actualStatus);

            Assertions.assertEquals("Đã chấp nhận", actualStatus, "Trạng thái không hiển thị đúng");

        } catch (TimeoutException e) {
            // Chụp màn hình khi không tìm thấy công việc hoặc trạng thái

            Assertions.fail("Không tìm thấy công việc đã được chấp nhận với ID: " + acceptedJobId);
        }
    }

    @Test
    @Order(8)
    public void TC008() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Truy cập chi tiết công việc khi chưa đăng nhập
        driver.get("http://localhost:8087/home/posting/15");

        // 2. Nhấn nút “Apply now”
        WebElement applyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Apply now')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", applyNowButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyNowButton);

        // 3. Kiểm tra trình duyệt chuyển hướng về trang login
        boolean redirectedToLogin = wait.until(ExpectedConditions.urlContains("/login"));
        Assertions.assertTrue(redirectedToLogin, "Không chuyển hướng về trang login khi chưa đăng nhập.");

        System.out.println("Chuyển hướng về trang login thành công khi chưa đăng nhập.");
    }

    @Test
    @Order(1)
    public void UT009() {
        // 1. Đăng nhập
        driver.get("http://localhost:8087/login");
        driver.findElement(By.id("username")).sendKeys("acc1");
        driver.findElement(By.id("password")).sendKeys("Camly123@");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        loginBtn.click();

        wait.until(ExpectedConditions.urlContains("/home"));

        // 2. Truy cập chi tiết công việc
        driver.get("http://localhost:8087/home/posting/15");

        // 3. Nhấn nút “Apply now”
        WebElement applyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Apply now')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", applyNowButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyNowButton);

        // 4. Xác nhận ứng tuyển
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(),'Apply')]")));
        confirmButton.click();

        // 5. Đợi toast message xuất hiện
        WebElement successMsg = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".swal2-toast.swal2-show")));

        // 6. Kiểm tra nội dung thông báo
        String actualMessage = successMsg.getText();
        System.out.println("Toast message: " + actualMessage);

        // Sửa thông báo kỳ vọng tại đây
        String expectedMessage = "Bài đăng đã hết hạn không thể ứng tuyển"; // <-- Bạn sửa nội dung mong đợi tại đây
        Assertions.assertTrue(actualMessage.contains(expectedMessage),
                "Toast không chứa thông báo mong đợi: " + expectedMessage);
    }

    @Test
    @Order(10)
    public void UT010() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        int reappliedJobId = 35; // <-- Thay bằng job ID đã từng bị từ chối nhưng giờ muốn ứng tuyển lại

        try {
            // 1. Đăng nhập
            driver.get("http://localhost:8087/login");
            driver.findElement(By.id("username")).sendKeys("acc1");
            driver.findElement(By.id("password")).sendKeys("Camly123@");
            driver.findElement(By.id("submit")).click();
            wait.until(ExpectedConditions.urlContains("/home"));

            // 2. Truy cập chi tiết công việc
            driver.get("http://localhost:8087/home/posting/" + reappliedJobId);

            // 3. Nhấn "Apply now"
            WebElement applyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Apply now') or contains(text(),'Ứng tuyển ngay')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyNowButton);

            // 4. Đợi popup hiện ra, kiểm tra có form CV và điều khoản
            WebElement cvPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".modal-content")));

            // 5. Nhấn nút Apply trong popup
            WebElement confirmApplyButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@type='submit' and (contains(text(),'Apply') or contains(text(),'Ứng tuyển'))]")));
            confirmApplyButton.click();

            // 6. Kiểm tra thông báo thành công
            WebElement successToast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".swal2-toast.swal2-show")));
            String successMsg = successToast.getText();
            System.out.println("Thông báo ứng tuyển: " + successMsg);
            Assertions.assertTrue(successMsg.contains("thành công") || successMsg.contains("success"),
                    "Ứng tuyển không thành công");

            // 7. Truy cập trang "Công việc đã ứng tuyển"
            driver.get("http://localhost:8087/seeker/profile/postingapplied");

            // 3. Tìm công việc
            WebElement acceptedJobCard = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(@href,'/home/posting/" + reappliedJobId + "')]/ancestor::div[contains(@class,'card')]")));

            // 4. Kiểm tra trạng thái hiển thị là "Đã chấp nhận"
            WebElement statusElement = acceptedJobCard.findElement(
                    By.xpath(".//button[contains(@class,'btn-danger')] | .//span[contains(@class,'status-text')]"));

            String actualStatus = statusElement.getText().trim();
            System.out.println("Trạng thái thực tế: " + actualStatus);

            Assertions.assertEquals("Đã ứng tuyển", actualStatus, "Trạng thái không hiển thị đúng");


        } catch (Exception e) {
            // Chụp màn hình nếu lỗi


                Assertions.fail("Test UT010 thất bại: " + e.getMessage());
        }
    }

    @Test
    @Order(11)
    public void UT011() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        int previouslyAcceptedJobId = 37; // ← Job ID đã từng được chấp nhận trước đó

        try {
            // 1. Đăng nhập
            driver.get("http://localhost:8087/login");
            driver.findElement(By.id("username")).sendKeys("acc1");
            driver.findElement(By.id("password")).sendKeys("Camly123@");
            driver.findElement(By.id("submit")).click();
            wait.until(ExpectedConditions.urlContains("/home"));

            // 2. Truy cập chi tiết công việc
            driver.get("http://localhost:8087/home/posting/" + previouslyAcceptedJobId);

            // 3. Nhấn "Apply now"
            WebElement applyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Apply now') or contains(text(),'Ứng tuyển ngay')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyNowButton);

            // 4. Đợi popup hiển thị
            WebElement cvPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".modal-content")));

            // 5. Nhấn "Apply" trong popup
            WebElement confirmApplyButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@type='submit' and (contains(text(),'Apply') or contains(text(),'Ứng tuyển'))]")));
            confirmApplyButton.click();

            // 6. Kiểm tra toast thông báo
            WebElement successToast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".swal2-toast.swal2-show")));
            String successMsg = successToast.getText();
            System.out.println("Thông báo ứng tuyển: " + successMsg);
            Assertions.assertTrue(successMsg.contains("thành công") || successMsg.contains("success"),
                    "Thông báo ứng tuyển không thành công");

            // 7. Truy cập trang "Công việc đã ứng tuyển"
            // 7. Truy cập trang "Công việc đã ứng tuyển"
            driver.get("http://localhost:8087/seeker/profile/postingapplied");

            // 3. Tìm công việc
            WebElement acceptedJobCard = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(@href,'/home/posting/" + previouslyAcceptedJobId + "')]/ancestor::div[contains(@class,'card')]")));

            // 4. Kiểm tra trạng thái hiển thị là "Đã chấp nhận"
            WebElement statusElement = acceptedJobCard.findElement(
                    By.xpath(".//button[contains(@class,'btn-success')] | .//span[contains(@class,'status-text')]"));

            String actualStatus = statusElement.getText().trim();
            System.out.println("Trạng thái thực tế: " + actualStatus);

            Assertions.assertEquals("Đã ứng tuyển", actualStatus, "Trạng thái không hiển thị đúng");


        } catch (Exception e) {
            Assertions.fail("Test UT011 thất bại: " + e.getMessage());
        }
    }


}
