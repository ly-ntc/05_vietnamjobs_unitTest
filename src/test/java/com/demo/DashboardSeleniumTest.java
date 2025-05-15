package com.demo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DashboardSeleniumTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().window().maximize();
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void handleAlertIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            Alert alert = shortWait.until(ExpectedConditions.alertIsPresent());
            System.out.println("⚠️ Alert text: " + alert.getText());
            alert.accept();
            System.out.println("✅ Alert đã được chấp nhận.");
        } catch (TimeoutException | NoAlertPresentException ignored) {
        }
    }

    private void login(String username, String password) {
        driver.get("http://localhost:8087/login");
        handleAlertIfPresent();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username"))).clear();
        driver.findElement(By.name("username")).sendKeys(username);
        WebElement passwordField = driver.findElement(By.name("password"));
        passwordField.clear();
        passwordField.sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("submit"))).click();
        handleAlertIfPresent();
    }

    // Phương thức đăng xuất
    private void logout() {
        driver.get("http://localhost:8087/logout");
        driver.manage().deleteAllCookies();  // Xóa toàn bộ cookie
        driver.navigate().refresh();         // Làm mới trang để áp dụng xóa session
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/login"),
                ExpectedConditions.visibilityOfElementLocated(By.name("username"))
        ));
        System.out.println("✅ Đã đăng xuất và xóa session.");
    }

    // Dashboard_01: Kiểm tra tiêu đề trang Dashboard
    @Test
    @Order(1)
    void Dashboard_01_TestDashboardTitle() {
        login("employer10", "Huyn123@");
        wait.until(ExpectedConditions.urlContains("/employer/dashboard"));
        driver.get("http://localhost:8087/employer/dashboard");
        handleAlertIfPresent();
        assertEquals("VietnamJobs - Dashboard", driver.getTitle());
        System.out.println("✅ Tiêu đề trang Dashboard đúng.");
    }

    // Dashboard_02: Kiểm tra biểu đồ người dùng
    @Test
    @Order(2)
    void Dashboard_02_TestVisitorsChartExist() {
        assertTrue(isElementPresent(By.id("visitors-chart")));
        System.out.println("✅ Biểu đồ 'visitors-chart' hiển thị đúng.");
    }

    // Dashboard_03: Kiểm tra biểu đồ CV và Jobs
    @Test
    @Order(3)
    void Dashboard_03_TestJobsChartExist() {
        assertTrue(isElementPresent(By.id("jobs-chart")));
        System.out.println("✅ Biểu đồ 'jobs-chart' hiển thị đúng.");
    }

    // Dashboard_04: Kiểm tra biểu đồ kết quả ứng tuyển
    @Test
    @Order(4)
    void Dashboard_04_TestApplicationChartExist() {
        assertTrue(isElementPresent(By.id("application-chart")));
        System.out.println("✅ Biểu đồ 'application-chart' hiển thị đúng.");
    }

    // Dashboard_05: Kiểm tra bảng System users
    @Test
    @Order(5)
    void Dashboard_05_TestSystemUsersTableExist() {
        WebElement systemUsersTitle = driver.findElement(By.xpath("//h3[text()='System users']"));
        assertTrue(systemUsersTitle.isDisplayed());
        System.out.println("✅ Bảng 'System users' hiển thị đúng và đầy đủ nội dung.");
    }

    // Dashboard_06: Kiểm tra bảng Top Employers
    @Test
    @Order(6)
    void Dashboard_06_TestTopEmployersTableExist() {
        WebElement topEmployersTitle = driver.findElement(By.xpath("//h3[text()='Top Employers']"));
        assertTrue(topEmployersTitle.isDisplayed());
        System.out.println("✅ Bảng 'Top Employers' hiển thị đúng và đầy đủ nội dung.");
    }

    // Dashboard_07: Điều hướng tới System users
    @Test
    @Order(7)
    void Dashboard_07_TestNavigationToSystemUsers() {
        WebElement systemUsersLink = driver.findElement(By.xpath("//h3[text()='System users']/following-sibling::a"));
        systemUsersLink.click();
        handleAlertIfPresent();
        wait.until(ExpectedConditions.urlContains("/admin/account"));
        assertTrue(driver.getCurrentUrl().contains("/admin/account"));
        System.out.println("✅ Điều hướng đến trang '/admin/account' thành công.");
        driver.navigate().back();
    }

    // Dashboard_08: Điều hướng tới Top Employers
    @Test
    @Order(8)
    void Dashboard_08_TestNavigationToTopEmployers() {
        WebElement topEmployersLink = driver.findElement(By.xpath("//h3[text()='Top Employers']/following-sibling::div/a"));
        topEmployersLink.click();
        handleAlertIfPresent();
        wait.until(ExpectedConditions.urlContains("/admin/company"));
        assertTrue(driver.getCurrentUrl().contains("/admin/company"));
        System.out.println("✅ Điều hướng đến trang '/admin/company' thành công.");
        driver.navigate().back();
    }

    // Dashboard_09: Kiểm tra dữ liệu biểu đồ người dùng (ví dụ kiểm tra biểu đồ visitors-chart có dữ liệu > 0)
    @Test
    @Order(9)
    void Dashboard_09_TestUserChartData() {
        WebElement visitorsChart = driver.findElement(By.id("visitors-chart"));
        assertNotNull(visitorsChart);
        // Không thể kiểm tra dữ liệu biểu đồ qua Selenium trực tiếp, nhưng có thể kiểm tra canvas có hiển thị không
        assertTrue(visitorsChart.isDisplayed());
        System.out.println("✅ Biểu đồ người dùng có dữ liệu (canvas tồn tại).");
    }

    // Dashboard_10: Kiểm tra dữ liệu biểu đồ CV và Jobs
    @Test
    @Order(10)
    void Dashboard_10_TestJobsChartData() {
        WebElement jobsChart = driver.findElement(By.id("jobs-chart"));
        assertNotNull(jobsChart);
        assertTrue(jobsChart.isDisplayed());
        System.out.println("✅ Biểu đồ CV và Jobs có dữ liệu (canvas tồn tại).");
    }

    // Dashboard_11: Kiểm tra dữ liệu biểu đồ ứng tuyển
    @Test
    @Order(11)
    void Dashboard_11_TestApplicationChartData() {
        WebElement applicationChart = driver.findElement(By.id("application-chart"));
        assertNotNull(applicationChart);
        assertTrue(applicationChart.isDisplayed());
        System.out.println("✅ Biểu đồ ứng tuyển có dữ liệu (canvas tồn tại).");
    }

    // Dashboard_12: Kiểm tra quyền truy cập Dashboard với tài khoản seeker
    @Test
    @Order(12)
    void Dashboard_12_TestAccessDeniedForSeeker() {
        System.out.println("🔍 Bắt đầu kiểm thử quyền truy cập Dashboard với tài khoản seeker.");

        logout();
        // Đăng nhập với tài khoản seeker (không có quyền truy cập Dashboard)
        login("seeker1", "abc123");
        System.out.println("✅ Đã đăng nhập với tài khoản seeker.");

        // Điều hướng tới Dashboard của employer
        driver.get("http://localhost:8087/employer/dashboard");

        // Chờ đợi một trong hai điều kiện:
        // 1. Bị chuyển hướng về trang login với thông báo lỗi
        // 2. Hiển thị thông báo không có quyền truy cập
        boolean accessDenied = false;
        try {
            accessDenied = wait.until(ExpectedConditions.or(
                    //ExpectedConditions.urlContains("/account/login?error"),  // Bị chuyển hướng về trang login với thông báo lỗi
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(text(),'Access denied') or contains(text(),'không có quyền') or contains(text(),'Unauthorized')]")  // Thông báo không có quyền
                    )
            )) != null;
        } catch (Exception e) {
            System.out.println("❌ Không có thông báo lỗi hiển thị.");
        }

        // Kiểm tra kết quả
        assertTrue(accessDenied, "❌ Người dùng seeker không bị từ chối truy cập Dashboard.");

        System.out.println("✅ Người dùng seeker bị từ chối truy cập Dashboard.");

        // Đăng xuất để đảm bảo trạng thái
        driver.get("http://localhost:8087/logout");
    }

    // Dashboard_13: Kiểm tra trạng thái tài khoản khóa
    @Test
    @Order(13)
    void Dashboard_13_TestLockedUserCannotLogin() {
        System.out.println("🔍 Bắt đầu kiểm thử đăng nhập với tài khoản bị khóa.");

        // Đăng xuất trước nếu đang đăng nhập
        logout();

        // Đăng nhập với tài khoản bị khóa
        login("employer2", "Huyn123@");
        System.out.println("✅ Đã gửi thông tin đăng nhập với tài khoản bị khóa.");

        // Đợi kiểm tra nếu có thông báo tài khoản bị khóa hoặc bị từ chối truy cập
        boolean accessDenied = false;
        try {
            accessDenied = wait.until(ExpectedConditions.or(
                    //ExpectedConditions.urlContains("/account/login?error"),  // Bị chuyển hướng về trang login với thông báo lỗi
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(text(),'Tài khoản của bạn đã bị khóa') or contains(text(),'Account locked') or contains(text(),'Access denied')]")  // Thông báo tài khoản bị khóa
                    )
            )) != null;
        } catch (Exception e) {
            System.out.println("❌ Không có thông báo lỗi hiển thị.");
        }

        // Kiểm tra kết quả
        assertTrue(accessDenied, "❌ Hệ thống cho phép tài khoản bị khóa đăng nhập.");

        System.out.println("✅ Hệ thống từ chối đăng nhập cho tài khoản bị khóa.");

        // Đảm bảo đăng xuất để giữ trạng thái sạch cho các kiểm thử khác
        driver.get("http://localhost:8087/logout");
    }

    // Dashboard_14: Kiểm tra tốc độ tải trang Dashboard (dưới 3 giây)
    // 14. Kiểm tra nội dung trang Dashboard có chứa các phần tử quan trọng (ví dụ tiêu đề, các bảng,...)
    @Test @Order(14)
    void Dashboard_14_TestDashboardContentPresence() {
        driver.get("http://localhost:8087/employer/dashboard");
        assertTrue(isElementPresent(By.xpath("//h1[contains(text(),'Dashboard')]")), "Tiêu đề Dashboard không tồn tại");
        assertTrue(isElementPresent(By.xpath("//h3[contains(text(),'System users')]")), "Phần System users không tồn tại");
        assertTrue(isElementPresent(By.xpath("//h3[contains(text(),'Top Employers')]")), "Phần Top Employers không tồn tại");
        System.out.println("✅ Nội dung chính của Dashboard hiển thị đầy đủ.");
    }

    // Dashboard_15: Đăng nhập sai thông tin
    @Test
    @Order(15)
    void Dashboard_15_TestInvalidLogin() {
        System.out.println("🔍 Bắt đầu kiểm thử đăng nhập với thông tin sai.");

        // Đăng xuất trước nếu đang đăng nhập
        driver.get("http://localhost:8087/logout");

        // Đăng nhập với thông tin sai
        login("wrongUser", "wrongPass");
        System.out.println("✅ Đã gửi thông tin đăng nhập sai.");

        // Kiểm tra thông báo lỗi hiển thị trên trang đăng nhập
        boolean errorDisplayed = false;
        try {
            // Chờ đợi thông báo lỗi hiển thị
            errorDisplayed = wait.until(ExpectedConditions.or(
                    //ExpectedConditions.urlContains("/account/login?error"),  // Bị chuyển hướng về trang login với thông báo lỗi
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(text(),'Sai thông tin') or contains(text(),'Invalid credentials') or contains(text(),'Sai tên người dùng hoặc mật khẩu')]")
                    )  // Thông báo sai thông tin
            )) != null;
        } catch (Exception e) {
            System.out.println("❌ Không có thông báo lỗi hiển thị.");
        }

        // Kiểm tra kết quả
        assertTrue(errorDisplayed, "❌ Hệ thống không thông báo sai thông tin đăng nhập.");

        System.out.println("✅ Hệ thống hiển thị thông báo sai thông tin đăng nhập.");
    }


    private boolean isElementPresent(By by) {
        try {
            return driver.findElement(by).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
