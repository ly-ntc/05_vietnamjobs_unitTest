package com.demo.managerPost;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
public class ManagerPostTest {
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup(); // Tự động tải ChromeDriver phù hợp
        driver = new ChromeDriver();
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    private void loginAndGoToPostingList() {
        driver.get("http://localhost:8087/login");
        driver.findElement(By.name("username")).sendKeys("lycuyt");
        driver.findElement(By.name("password")).sendKeys("Camly123@");
        driver.findElement(By.id("submit")).click();

        WebElement postingMenu = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='nav-link']//p[normalize-space()='Posting']")));
        postingMenu.click();

        WebElement listMenu = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/employer/job']")));
        listMenu.click();
    }



    private void selectFirstOptionInDropdown(WebElement selectElement) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new Event('select2:open'));", selectElement);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        WebElement firstOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".select2-results__option:first-child")));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new Event('mouseup'));", firstOption);
    }
    @Test
    public void QLBD_01_testLoginAndAccessRecruitmentPostList() throws InterruptedException {
        driver.get("http://localhost:8087/login");

        // Điền thông tin đăng nhập
        driver.findElement(By.name("username")).sendKeys("lycuyt");
        driver.findElement(By.name("password")).sendKeys("Camly123@");
        driver.findElement(By.id("submit")).click();

        // Đợi trang dashboard load (tối thiểu 2s hoặc có thể dùng WebDriverWait)
        WebElement postingMenu = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='nav-link']//p[normalize-space()='Posting']")));
        postingMenu.click();

        // Đợi menu con "List" hiện ra
        WebElement listMenu = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/employer/job']")));
        listMenu.click();

        // Kiểm tra tiêu đề trang
        String title = driver.getTitle();
        Assertions.assertEquals("VietnamJobs - Recruitment Posts", title);


    }

    @Test
    public void QLBD_02_testTableHeadersDisplayedCorrectly() throws InterruptedException {
        // Điều hướng như test trước
        loginAndGoToPostingList();

        // Kiểm tra các tiêu đề cột
        List<WebElement> headers = driver.findElements(By.cssSelector("#localTable thead th"));
        List<String> expectedHeaders = List.of("ID", "Title","Company", "Date created", "Deadline","Recruitment Posts", "Status", "Action");

        for (int i = 0; i < expectedHeaders.size(); i++) {
            Assertions.assertEquals(expectedHeaders.get(i), headers.get(i).getText());
        }
    }

    @Test
    public void QLBD_03_testAtLeastOnePostingDisplayed() throws InterruptedException {
        loginAndGoToPostingList();

        List<WebElement> rows = driver.findElements(By.cssSelector("#localTable tbody tr"));
        Assertions.assertTrue(rows.size() > 0, "Should have at least one posting row.");
    }

    @Test
    public void QLBD_04_testPostingStatusDisplayedCorrectly() throws InterruptedException {
        loginAndGoToPostingList();

        List<WebElement> statusCells = driver.findElements(By.cssSelector("#localTable tbody tr td.status"));
        for (WebElement statusCell : statusCells) {
            String statusText = statusCell.getText();
            Assertions.assertTrue(
                    statusText.equals("Still active") || statusText.equals("Closed"),
                    "Status must be either 'Still active' or 'Closed'"
            );
        }
    }
    @Test
    public void QLBD_05_testActionButtonsExist() throws InterruptedException {
        loginAndGoToPostingList();

        List<WebElement> rows = driver.findElements(By.cssSelector("#localTable tbody tr"));
        for (WebElement row : rows) {
            List<WebElement> buttons = row.findElements(By.cssSelector("td:last-child a > button"));
            Assertions.assertEquals(2, buttons.size()); // Kiểm tra đủ 3 nút

            // Kiểm tra class từng nút
            Assertions.assertTrue(buttons.get(0).getAttribute("class").contains("btn-danger")); // Delete
            Assertions.assertTrue(buttons.get(1).getAttribute("class").contains("btn-info"));   // Edit
        }

    }

    @Test
    public void QLBD_06_testDeleteRecruitmentPost() {
        loginAndGoToPostingList();

        // Chờ bảng load xong ít nhất 1 dòng
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#localTable tbody tr")));

        List<WebElement> rowsBefore = driver.findElements(By.cssSelector("#localTable tbody tr"));
        int initialCount = rowsBefore.size();

        Assertions.assertTrue(initialCount > 0, "Phải có ít nhất 1 dòng trong bảng để xóa");

        WebElement firstRow = rowsBefore.get(0);

        // Tìm nút XÓA: trong cột cuối cùng của dòng đầu tiên, tìm button có class btn-danger
        WebElement deleteButton = firstRow.findElement(By.cssSelector("#localTable tbody tr:first-child a.btn-danger"));

        // Click vào nút xóa
        deleteButton.click();
        // Selenium cần xử lý alert này trước khi tiếp tục
        WebDriverWait waitAlert = new WebDriverWait(driver, Duration.ofSeconds(5));
        waitAlert.until(ExpectedConditions.alertIsPresent());

        Alert alert = driver.switchTo().alert();
        alert.accept(); // Đóng alert thông báo (alert)

        // Chờ trang reload hoặc DOM cập nhật (chờ số dòng giảm xuống 1)
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.numberOfElementsToBeLessThan(By.cssSelector("#localTable tbody tr"), initialCount));

        List<WebElement> rowsAfter = driver.findElements(By.cssSelector("#localTable tbody tr"));
        Assertions.assertEquals(initialCount - 1, rowsAfter.size(), "Số dòng sau khi xóa phải giảm đi 1");
    }


    @Test
    public void QLBD_07_testEditRedirect() throws InterruptedException {
        loginAndGoToPostingList();

        WebElement editButton = driver.findElement(
                By.cssSelector("#localTable tbody tr:first-child a.btn-info")
        );

        // Click vào nút Edit
        editButton.click();

        // Lấy URL sau khi chuyển trang
        String currentUrl = driver.getCurrentUrl();

        // Kiểm tra xem URL có chứa đường dẫn đúng
        assertTrue(currentUrl.contains("/employer/job/update/"),
                "URL phải chứa /employer/job/update/. Thực tế: " + currentUrl);
    }

    @Test
    public void QLBD_08_testAddNewRedirect() throws InterruptedException {
        loginAndGoToPostingList();

        // Giả sử có nút Add New với id hoặc class rõ ràng, ví dụ id="btnAddNew"
        WebElement addNewButton = driver.findElement(
                By.cssSelector(".btn-success i.fa-plus")
        );

        // Click vào icon (hoặc click vào thẻ cha nếu cần)
        addNewButton.click();

        // Lấy URL hiện tại sau khi chuyển trang
        String currentUrl = driver.getCurrentUrl();

        // Kiểm tra url chính xác
        assertTrue(currentUrl.endsWith("/employer/job/add"),
                "URL phải là /employer/job/add. Thực tế: " + currentUrl);
    }

    //thêm mới bài đăng
    @Test
    public void QLBD_09_testAddNewFormDisplay() throws InterruptedException {
        loginAndGoToPostingList();

        // Navigate to Add New form
        WebElement addNewButton = driver.findElement(By.cssSelector(".btn-success i.fa-plus"));
        addNewButton.click();

        // Verify form title
        WebElement formTitle = driver.findElement(By.cssSelector(".modal-title"));
        assertEquals("Recruitment Posts Details", formTitle.getText(),
                "Form title should be 'Recruitment Posts Details'");

        // Verify breadcrumb navigation
        WebElement breadcrumb = driver.findElement(By.cssSelector(".breadcrumb"));
        assertTrue(breadcrumb.getText().contains("Dashboard") &&
                        breadcrumb.getText().contains("Recruitment Posts") &&
                        breadcrumb.getText().contains("Add New"),
                "Breadcrumb navigation is incorrect");
    }
    private void loginAndGoToAddNewForm() throws InterruptedException {
        loginAndGoToPostingList();

        // Navigate to Add New form
        WebElement addNewButton = driver.findElement(By.cssSelector(".btn-success i.fa-plus"));
        addNewButton.click();

        // Wait for form to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".modal-title")));
    }
    @Test
    public void QLBD_10_testRequiredFieldsValidation() throws InterruptedException {
        loginAndGoToAddNewForm();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click nút Submit mà không nhập dữ liệu
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit'].btn-success"));
        submitButton.click();

        // Chờ khoảng 1 giây để trình duyệt xử lý validation HTML5
        Thread.sleep(1000);

        // Lấy từng field để kiểm tra thuộc tính validation
        WebElement titleField = driver.findElement(By.cssSelector("input[placeholder='Enter title...']"));
        WebElement genderField = driver.findElement(By.cssSelector("input[placeholder='EX: Male, Female']"));
        WebElement quantityField = driver.findElement(By.cssSelector("input[placeholder='Enter quantity...']"));
        WebElement deadlineField = driver.findElement(By.cssSelector("input[th\\:field='${newJob.deadline}']"));
        WebElement descriptionField = driver.findElement(By.cssSelector("textarea[placeholder='Enter a description...']"));

        // Kiểm tra validation: phương thức submit sẽ fail nếu required field để trống, nên trình duyệt sẽ không cho submit
        // Với Selenium, bạn có thể dùng .getAttribute("validationMessage") để xem thông báo
        String titleError = titleField.getAttribute("validationMessage");
        String genderError = genderField.getAttribute("validationMessage");
        String quantityError = quantityField.getAttribute("validationMessage");
        String deadlineError = deadlineField.getAttribute("validationMessage");
        String descriptionError = descriptionField.getAttribute("validationMessage");

        // In ra nếu muốn xem chi tiết
        System.out.println("Title error: " + titleError);
        System.out.println("Gender error: " + genderError);
        System.out.println("Quantity error: " + quantityError);
        System.out.println("Deadline error: " + deadlineError);
        System.out.println("Description error: " + descriptionError);

        // Assert: miễn là validationMessage không rỗng → HTML5 form đã bắt lỗi thành công
        assertFalse(titleError.isEmpty(), "Title should be required");
        assertFalse(genderError.isEmpty(), "Gender should be required");
        assertFalse(quantityError.isEmpty(), "Quantity should be required");
        assertFalse(deadlineError.isEmpty(), "Deadline should be required");
        assertFalse(descriptionError.isEmpty(), "Description should be required");
    }

    @Test
    public void QLBD_11_testAddNewPostSuccessfully() throws InterruptedException {
        loginAndGoToAddNewForm();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Nhập Title
        WebElement titleField = driver.findElement(By.cssSelector("input[placeholder='Enter title...']"));
        titleField.sendKeys("Nhân viên bán hàng");

        // Nhập Gender
        WebElement genderField = driver.findElement(By.cssSelector("input[placeholder='EX: Male, Female']"));
        genderField.sendKeys("Any");

        // Nhập Quantity
        WebElement quantityField = driver.findElement(By.cssSelector("input[placeholder='Enter quantity...']"));
        quantityField.sendKeys("5");

        // Nhập Deadline - giả sử format là DD/MM/YYYY
        WebElement deadlineField = driver.findElement(By.cssSelector("input[class*='datetimepicker-input']"));
        deadlineField.sendKeys("31/12/2025");

        // Nhập Description
        WebElement descriptionField = driver.findElement(By.cssSelector("textarea[placeholder='Enter a description...']"));
        descriptionField.sendKeys("Tuyển 5 nhân viên bán hàng làm việc toàn thời gian tại HCM.");

        // Các dropdown có sẵn chọn mặc định, có thể bỏ qua nếu không cần chọn lại

        // Bấm nút Create new
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit'].btn-success"));
        submitButton.click();

        // Chờ Toast thành công hoặc URL redirect
        // Cách 1: Chờ Toast
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".swal2-popup.swal2-toast")));
        String toastText = toast.getText().toLowerCase();

        assertTrue(toastText.contains("success") || toastText.contains("thành công"),
                "Thông báo thành công không hiển thị hoặc không đúng");

        // Cách 2 (nếu không dùng toast): Chờ chuyển hướng sang danh sách
        // wait.until(ExpectedConditions.urlContains("/employer/job"));
        // String currentUrl = driver.getCurrentUrl();
        // assertTrue(currentUrl.contains("/employer/job"), "Không chuyển về trang danh sách bài đăng sau khi thêm");

        // Ghi log nếu muốn
        System.out.println("✅ Thêm bài đăng mới thành công.");
    }



    @Test
    public void QLBD_12_testInvalidDeadlineFormat() throws InterruptedException {
        loginAndGoToAddNewForm();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.cssSelector("input[placeholder='Enter title...']")).sendKeys("Nhân viên kế toán");
        driver.findElement(By.cssSelector("input[placeholder='EX: Male, Female']")).sendKeys("Female");
        driver.findElement(By.cssSelector("input[placeholder='Enter quantity...']")).sendKeys("3");

        // Deadline sai format
        WebElement deadlineField = driver.findElement(By.cssSelector("input[class*='datetimepicker-input']"));
        deadlineField.sendKeys("abc");

        driver.findElement(By.cssSelector("textarea[placeholder='Enter a description...']"))
                .sendKeys("Tuyển nhân viên kế toán có kinh nghiệm");

        driver.findElement(By.cssSelector("button[type='submit'].btn-success")).click();

        Thread.sleep(1000);

        // Kiểm tra nếu validation của trình duyệt bắt lỗi định dạng
        String deadlineValidationMsg = deadlineField.getAttribute("validationMessage");
        System.out.println("Deadline validation: " + deadlineValidationMsg);

        // Nếu có message nghĩa là bị chặn submit do định dạng sai
        assertFalse(deadlineValidationMsg.isEmpty(), "Trình duyệt phải cảnh báo định dạng sai");
    }

    @Test
    public void QLBD_13_testNegativeQuantity() throws InterruptedException {
        loginAndGoToAddNewForm();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.cssSelector("input[placeholder='Enter title...']")).sendKeys("Nhân viên kho");
        driver.findElement(By.cssSelector("input[placeholder='EX: Male, Female']")).sendKeys("Any");
        driver.findElement(By.cssSelector("input[placeholder='Enter quantity...']")).sendKeys("-5");

        driver.findElement(By.cssSelector("input[class*='datetimepicker-input']")).sendKeys("31/12/2025");
        driver.findElement(By.cssSelector("textarea[placeholder='Enter a description...']"))
                .sendKeys("Tuyển gấp nhân viên kho làm việc tại HCM");

        driver.findElement(By.cssSelector("button[type='submit'].btn-success")).click();

        Thread.sleep(1000);

        // Kiểm tra có hiện toast lỗi hoặc không cho submit
        List<WebElement> errorToast = driver.findElements(By.cssSelector(".swal2-popup.swal2-toast.swal2-error"));
        boolean hasError = !errorToast.isEmpty();

        assertTrue(hasError, "Hệ thống phải hiển thị lỗi khi số lượng là số âm");
    }

}
