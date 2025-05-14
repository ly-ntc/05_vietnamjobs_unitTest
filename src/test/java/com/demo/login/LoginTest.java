package com.demo.login;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;
public class LoginTest {
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

    @Test
    void testHomePageTitle() {
        driver.get("http://localhost:8087"); // URL trang bạn muốn test
        String title = driver.getTitle();
        assertThat(title).contains("Dashboard"); // Kiểm tra tiêu đề có chứa từ "Trang chủ"
    }


}
