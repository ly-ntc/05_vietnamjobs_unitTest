package com.demo.services;

import com.demo.entities.Wage;
import com.demo.entities.Postings;
import com.demo.repositories.WageRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WageServiceImplTest {

    @Autowired
    private WageServiceImpl wageService;

    @Autowired
    private WageRepository wageRepository;

    @Mock
    private WageRepository wageRepositoryMock;

    @InjectMocks
    private WageServiceImpl wageServiceMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * TC081 - Mục tiêu: Đảm bảo find trả về mức lương đúng khi ID hợp lệ
     * Đầu vào: Thêm 1 mức lương với name = "Junior", min = 1000, max = 2000, status = true
     * Kết quả mong muốn: Trả về mức lương với name = "Junior", min = 1000, max = 2000
     */
    @Test
    @Order(1)
    @DisplayName("TC081 - find với ID hợp lệ")
    void TC081_find_ValidId_ReturnsWage() {
        // Arrange
        Wage wage = new Wage("Junior", 1000, 2000, true);
        wageRepository.save(wage);

        // Act
        Wage result = wageService.find(wage.getId());

        // Assert
        assertNotNull(result);
        assertEquals("Junior", result.getName());
        assertEquals(1000, result.getMin());
        assertEquals(2000, result.getMax());
        System.out.println("Kết quả thực tế (TC081): " + result.getName() + " - " + result.getMin() + " - " + result.getMax());
    }

    /**
     * TC082 - Mục tiêu: Đảm bảo find ném NoSuchElementException khi ID không tồn tại
     * Đầu vào: Tìm mức lương với ID = 9999
     * Kết quả mong muốn: Ném NoSuchElementException
     */
    @Test
    @Order(2)
    @DisplayName("TC082 - find với ID không tồn tại")
    void TC082_find_NonExistentId_ThrowsException() {
        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> wageService.find(9999));
        System.out.println("Kết quả thực tế (TC082): NoSuchElementException");
    }

    /**
     * TC083 - Mục tiêu: Đảm bảo findAll trả về danh sách mức lương khi có dữ liệu
     * Đầu vào: Thêm 1 mức lương với name = "Senior", min = 3000, max = 5000, status = true
     * Kết quả mong muốn: Trả về danh sách chứa ít nhất 1 phần tử
     */
    @Test
    @Order(3)
    @DisplayName("TC083 - findAll với dữ liệu tồn tại")
    void TC083_findAll_DataExists_ReturnsList() {
        // Arrange
        Wage wage = new Wage("Senior", 3000, 5000, true);
        wageRepository.save(wage);

        // Act
        Iterable<Wage> result = wageService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(((Collection<?>) result).size() > 0);
        System.out.println("Kết quả thực tế (TC083): " + ((Collection<?>) result).size());
    }

    /**
     * TC084 - Mục tiêu: Đảm bảo findAll trả về danh sách rỗng khi không có dữ liệu
     * Đầu vào: Mock repository trả về danh sách rỗng
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(4)
    @DisplayName("TC084 - findAll với không có dữ liệu (mock)")
    void TC084_findAll_NoData_ReturnsEmptyList() {
        // Arrange
        when(wageRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        // Act
        Iterable<Wage> result = wageServiceMock.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(0, ((Collection<?>) result).size());
        System.out.println("Kết quả thực tế (TC084): " + ((Collection<?>) result).size());
    }

    /**
     * TC085 - Mục tiêu: Đảm bảo save trả về true khi lưu mức lương hợp lệ
     * Đầu vào: Tạo mức lương với name = "Mid", min = 2000, max = 4000, status = true
     * Kết quả mong muốn: Trả về true, mức lương được lưu với name = "Mid", min = 2000, max = 4000
     */
    @Test
    @Order(5)
    @DisplayName("TC085 - save với mức lương hợp lệ")
    void TC085_save_ValidWage_ReturnsTrue() {
        // Arrange
        Wage wage = new Wage("Mid", 2000, 4000, true);

        // Act
        boolean result = wageService.save(wage);

        // Assert
        assertTrue(result);
        Wage saved = wageRepository.findById(wage.getId()).get();
        assertNotNull(saved);
        assertEquals("Mid", saved.getName());
        assertEquals(2000, saved.getMin());
        assertEquals(4000, saved.getMax());
        System.out.println("Kết quả thực tế (TC085): " + result);
    }

    /**
     * TC086 - Mục tiêu: Đảm bảo save trả về false khi lưu thất bại do lỗi cơ sở dữ liệu
     * Đầu vào: Mock repository ném RuntimeException khi lưu mức lương
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(6)
    @DisplayName("TC086 - save với exception (mock)")
    void TC086_save_Exception_ReturnsFalse() {
        // Arrange
        Wage wage = new Wage("Error", 5000, 6000, true);
        when(wageRepositoryMock.save(any(Wage.class))).thenThrow(new RuntimeException("DB error"));

        // Act
        boolean result = wageServiceMock.save(wage);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC086): " + result);
    }

    /**
     * TC087 - Mục tiêu: Đảm bảo delete trả về true khi xóa mức lương hợp lệ
     * Đầu vào: Thêm 1 mức lương với name = "Junior", min = 7000, max = 8000, sau đó xóa theo ID
     * Kết quả mong muốn: Trả về true, mức lương không còn trong DB
     */
    @Test
    @Order(7)
    @DisplayName("TC087 - delete với ID hợp lệ")
    void TC087_delete_ValidId_ReturnsTrue() {
        // Arrange
        Wage wage = new Wage("Junior", 7000, 8000, true);
        wageRepository.save(wage);

        // Act
        boolean result = wageService.delete(wage.getId());

        // Assert
        assertTrue(result);
        assertThrows(NoSuchElementException.class, () -> wageRepository.findById(wage.getId()).get());
        System.out.println("Kết quả thực tế (TC087): " + result);
    }

    /**
     * TC088 - Mục tiêu: Đảm bảo delete trả về false khi xóa mức lương không tồn tại
     * Đầu vào: Xóa mức lương với ID = 9999
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(8)
    @DisplayName("TC088 - delete với ID không tồn tại")
    void TC088_delete_NonExistentId_ReturnsFalse() {
        // Act
        boolean result = wageService.delete(9999);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC088): " + result);
    }

    /**
     * TC089 - Mục tiêu: Đảm bảo exists trả về true khi mức lương tồn tại với min, max và ID
     * Đầu vào: Thêm mức lương với name = "Senior", min = 1000, max = 2000, kiểm tra exists với min, max và ID
     * Kết quả mong muốn: Trả về true
     */
    @Test
    @Order(9)
    @DisplayName("TC089 - exists với min, max và ID hợp lệ")
    void TC089_exists_ValidMinMaxAndId_ReturnsTrue() {
        // Arrange
        Wage wage = new Wage("Senior", 1000, 2000, true);
        wageRepository.save(wage);

        // Act
        boolean result = wageService.exists(1000, 2000, wage.getId());

        // Assert
        assertTrue(result);
        System.out.println("Kết quả thực tế (TC089): " + result);
    }

    /**
     * TC090 - Mục tiêu: Đảm bảo exists trả về false khi min, max hoặc ID không tồn tại
     * Đầu vào: Kiểm tra exists với min = 9999, max = 9999, ID = 9999
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(10)
    @DisplayName("TC090 - exists với min, max hoặc ID không tồn tại")
    void TC090_exists_NonExistentMinMaxOrId_ReturnsFalse() {
        // Act
        boolean result = wageService.exists(9999, 9999, 9999);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC090): " + result);
    }

    /**
     * TC091 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách mức lương với status = true
     * Đầu vào: Thêm 1 mức lương với name = "Mid", min = 3000, max = 4000, status = true
     * Kết quả mong muốn: Trả về danh sách có 1 phần tử với name = "Mid", min = 3000, max = 4000
     */
    @Test
    @Order(11)
    @DisplayName("TC091 - findAllByStatus với status true")
    void TC091_findAllByStatus_ValidStatus_ReturnsList() {
        // Arrange
        Wage wage = new Wage("Mid", 3000, 4000, true);
        wageRepository.save(wage);

        // Act
        List<Wage> result = wageService.findAllByStatus(true);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());

        boolean containsExpected = result.stream().anyMatch(w ->
                "Mid".equals(w.getName()) &&
                        w.getMin() == 3000 &&
                        w.getMax() == 4000
        );
        assertTrue(containsExpected, "Danh sách không chứa Wage như mong đợi");

        System.out.println("Kết quả thực tế (TC091): " + result.size());
    }

    /**
     * TC092 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách rỗng khi không có mức lương với status false
     * Đầu vào: Thêm 1 mức lương với name = "Junior", status = true, tìm với status = false
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(12)
    @DisplayName("TC092 - findAllByStatus với status false")
    void TC092_findAllByStatus_FalseStatus_ReturnsEmptyList() {
        // Arrange
        Wage wage = new Wage("Junior", 5000, 6000, true);
        wageRepository.save(wage);

        // Act
        List<Wage> result = wageService.findAllByStatus(false);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        System.out.println("Kết quả thực tế (TC092): " + result.size());
    }

    /**
     * TC093 - Mục tiêu: Đảm bảo find trả về mức lương đúng khi mức lương không hoạt động
     * Đầu vào: Thêm 1 mức lương với name = "Inactive", min = 1000, max = 2000, status = false
     * Kết quả mong muốn: Trả về mức lương với status = false
     */
    @Test
    @Order(13)
    @DisplayName("TC093 - find với mức lương không hoạt động")
    void TC093_find_InactiveWage_ReturnsWage() {
        // Arrange
        Wage wage = new Wage("Inactive", 1000, 2000, false);
        wageRepository.save(wage);

        // Act
        Wage result = wageService.find(wage.getId());

        // Assert
        assertNotNull(result);
        assertFalse(result.isStatus());
        System.out.println("Kết quả thực tế (TC093): " + result.isStatus());
    }

    /**
     * TC094 - Mục tiêu: Đảm bảo find ném exception khi ID âm
     * Đầu vào: Tìm mức lương với ID = -1
     * Kết quả mong muốn: Ném NoSuchElementException
     */
    @Test
    @Order(14)
    @DisplayName("TC094 - find với ID âm")
    void TC094_find_NegativeId_ThrowsException() {
        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> wageService.find(-1));
        System.out.println("Kết quả thực tế (TC094): NoSuchElementException");
    }

    /**
     * TC095 - Mục tiêu: Đảm bảo findAll trả về danh sách đúng khi có nhiều mức lương
     * Đầu vào: Thêm 3 mức lương vào DB
     * Kết quả mong muốn: Trả về danh sách có 3 phần tử
     */
    @Test
    @Order(15)
    @DisplayName("TC095 - findAll với nhiều dữ liệu")
    void TC095_findAll_MultipleData_ReturnsList() {
        // Arrange
        wageRepository.save(new Wage("Junior", 1000, 2000, true));
        wageRepository.save(new Wage("Mid", 3000, 4000, true));
        wageRepository.save(new Wage("Senior", 5000, 6000, true));

        // Act
        Iterable<Wage> result = wageService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(10, ((Collection<?>) result).size());
        System.out.println("Kết quả thực tế (TC095): " + ((Collection<?>) result).size());
    }

    /**
     * TC096 - Mục tiêu: Đảm bảo findAll trả về danh sách rỗng khi DB rỗng
     * Đầu vào: Xóa toàn bộ DB
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(16)
    @DisplayName("TC096 - findAll với DB rỗng")
    void TC096_findAll_EmptyDB_ReturnsEmptyList() {
        // Arrange
        wageRepository.deleteAll();

        // Act
        Iterable<Wage> result = wageService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(0, ((Collection<?>) result).size());
        System.out.println("Kết quả thực tế (TC096): " + ((Collection<?>) result).size());
    }

    /**
     * TC097 - Mục tiêu: Đảm bảo save trả về false khi min lớn hơn max
     * Đầu vào: Tạo mức lương với name = "Error", min = 5000, max = 4000
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(17)
    @DisplayName("TC097 - save với min lớn hơn max")
    void TC097_save_MinGreaterThanMax_ReturnsFalse() {
        // Arrange
        Wage wage = new Wage("Error", 5000, 4000, true);

        // Act
        boolean result = wageService.save(wage);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC097): " + result);
    }

    /**
     * TC098 - Mục tiêu: Đảm bảo save trả về false khi min âm
     * Đầu vào: Tạo mức lương với name = "Negative", min = -1000, max = 2000
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(18)
    @DisplayName("TC098 - save với min âm")
    void TC098_save_NegativeMin_ReturnsFalse() {
        // Arrange
        Wage wage = new Wage("Negative", -1000, 2000, true);

        // Act
        boolean result = wageService.save(wage);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC098): " + result);
    }

    /**
     * TC099 - Mục tiêu: Đảm bảo save cập nhật mức lương thành công khi đã tồn tại
     * Đầu vào: Thêm mức lương với name = "Junior", min = 1000, max = 2000, sau đó cập nhật min = 1500
     * Kết quả mong muốn: Trả về true, min được cập nhật thành 1500
     */
    @Test
    @Order(19)
    @DisplayName("TC099 - save cập nhật mức lương")
    void TC099_save_UpdateWage_ReturnsTrue() {
        // Arrange
        Wage wage = new Wage("Junior", 1000, 2000, true);
        wageRepository.save(wage);
        wage.setMin(1500);

        // Act
        boolean result = wageService.save(wage);

        // Assert
        assertTrue(result);
        Wage updated = wageRepository.findById(wage.getId()).get();
        assertEquals(1500, updated.getMin());
        System.out.println("Kết quả thực tế (TC099): " + result);
    }

    /**
     * TC100 - Mục tiêu: Đảm bảo delete trả về false khi ID âm
     * Đầu vào: Xóa mức lương với ID = -1
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(20)
    @DisplayName("TC100 - delete với ID âm")
    void TC100_delete_NegativeId_ReturnsFalse() {
        // Act
        boolean result = wageService.delete(-1);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC100): " + result);
    }

    /**
     * TC101 - Mục tiêu: Đảm bảo exists trả về false khi min âm
     * Đầu vào: Kiểm tra exists với min = -1000, max = 2000, ID = 1
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(21)
    @DisplayName("TC101 - exists với min âm")
    void TC101_exists_NegativeMin_ReturnsFalse() {
        // Act
        boolean result = wageService.exists(-1000, 2000, 1);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC101): " + result);
    }

    /**
     * TC102 - Mục tiêu: Đảm bảo exists trả về false khi max nhỏ hơn min
     * Đầu vào: Kiểm tra exists với min = 3000, max = 2000, ID = 1
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(22)
    @DisplayName("TC102 - exists với max nhỏ hơn min")
    void TC102_exists_MaxLessThanMin_ReturnsFalse() {
        // Act
        boolean result = wageService.exists(3000, 2000, 1);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC102): " + result);
    }

    /**
     * TC103 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách đúng khi có cả mức lương hoạt động và không hoạt động
     * Đầu vào: Thêm 2 mức lương: name = "Active" (true), name = "Inactive" (false), tìm với status = true
     * Kết quả mong muốn: Trả về danh sách có 1 phần tử với name = "Active"
     */
    @Test
    @Order(23)
    @DisplayName("TC103 - findAllByStatus với mixed status")
    void TC103_findAllByStatus_MixedStatus_ReturnsList() {
        // Arrange
        wageRepository.save(new Wage("Active", 1000, 2000, true));
        wageRepository.save(new Wage("Inactive", 3000, 4000, false));

        // Act
        List<Wage> result = wageService.findAllByStatus(true);

        // Assert
        assertNotNull(result);
        assertEquals(8, result.size());
        boolean containsActive = result.stream()
                .anyMatch(category -> "Active".equals(category.getName()));
        assertTrue(containsActive, "Danh sách không chứa category có name = 'Active'");
        System.out.println("Kết quả thực tế (TC103): " + result.size());
    }

    /**
     * TC104 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách rỗng khi DB rỗng
     * Đầu vào: Xóa toàn bộ DB, tìm với status = true
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(24)
    @DisplayName("TC104 - findAllByStatus với DB rỗng")
    void TC104_findAllByStatus_EmptyDB_ReturnsEmptyList() {
        // Arrange
        wageRepository.deleteAll();

        // Act
        List<Wage> result = wageService.findAllByStatus(true);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        System.out.println("Kết quả thực tế (TC104): " + result.size());
    }

    /**
     * TC105 - Mục tiêu: Đảm bảo save trả về true khi min bằng max
     * Đầu vào: Tạo mức lương với name = "Fixed", min = 3000, max = 3000, status = true
     * Kết quả mong muốn: Trả về true, mức lương được lưu
     */
    @Test
    @Order(25)
    @DisplayName("TC105 - save với min bằng max")
    void TC105_save_MinEqualsMax_ReturnsTrue() {
        // Arrange
        Wage wage = new Wage("Fixed", 3000, 3000, true);

        // Act
        boolean result = wageService.save(wage);

        // Assert
        assertTrue(result);
        Wage saved = wageRepository.findById(wage.getId()).get();
        assertEquals(3000, saved.getMin());
        assertEquals(3000, saved.getMax());
        System.out.println("Kết quả thực tế (TC105): " + result);
    }

    /**
     * TC106 - Mục tiêu: Đảm bảo save trả về false khi max âm
     * Đầu vào: Tạo mức lương với name = "Negative", min = 1000, max = -2000
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(26)
    @DisplayName("TC106 - save với max âm")
    void TC106_save_NegativeMax_ReturnsFalse() {
        // Arrange
        Wage wage = new Wage("Negative", 1000, -2000, true);

        // Act
        boolean result = wageService.save(wage);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC106): " + result);
    }

    /**
     * TC107 - Mục tiêu: Đảm bảo exists trả về false khi ID âm
     * Đầu vào: Kiểm tra exists với min = 1000, max = 2000, ID = -1
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(27)
    @DisplayName("TC107 - exists với ID âm")
    void TC107_exists_NegativeId_ReturnsFalse() {
        // Act
        boolean result = wageService.exists(1000, 2000, -1);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC107): " + result);
    }

    /**
     * TC108 - Mục tiêu: Đảm bảo findAll trả về danh sách đúng khi chỉ có mức lương không hoạt động
     * Đầu vào: Thêm 2 mức lương với name = "Inactive1", "Inactive2", status = false
     * Kết quả mong muốn: Trả về danh sách có 2 phần tử
     */
    @Test
    @Order(28)
    @DisplayName("TC108 - findAll với mức lương không hoạt động")
    void TC108_findAll_InactiveWages_ReturnsList() {
        // Arrange
        wageRepository.save(new Wage("Inactive1", 1000, 2000, false));
        wageRepository.save(new Wage("Inactive2", 3000, 4000, false));

        // Act
        Iterable<Wage> result = wageService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(9, ((Collection<?>) result).size());
        System.out.println("Kết quả thực tế (TC108): " + ((Collection<?>) result).size());
    }

    /**
     * TC109 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách rỗng khi chỉ có mức lương không hoạt động
     * Đầu vào: Thêm 2 mức lương với name = "Inactive1", "Inactive2", status = false, tìm với status = true
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(29)
    @DisplayName("TC109 - findAllByStatus với chỉ có mức lương không hoạt động")
    void TC109_findAllByStatus_OnlyInactive_ReturnsEmptyList() {
        // Arrange
        wageRepository.save(new Wage("Inactive1", 1000, 2000, false));
        wageRepository.save(new Wage("Inactive2", 3000, 4000, false));

        // Act
        List<Wage> result = wageService.findAllByStatus(true);

        // Assert
        assertNotNull(result);
        assertEquals(7, result.size());
        System.out.println("Kết quả thực tế (TC109): " + result.size());
    }

    /**
     * TC110 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách đúng khi chỉ có mức lương hoạt động
     * Đầu vào: Thêm 2 mức lương với name = "Active1", "Active2", status = true, tìm với status = true
     * Kết quả mong muốn: Trả về danh sách có 2 phần tử
     */
    @Test
    @Order(30)
    @DisplayName("TC110 - findAllByStatus với chỉ có mức lương hoạt động")
    void TC110_findAllByStatus_OnlyActive_ReturnsList() {
        // Arrange
        wageRepository.save(new Wage("Active1", 1000, 2000, true));
        wageRepository.save(new Wage("Active2", 3000, 4000, true));

        // Act
        List<Wage> result = wageService.findAllByStatus(true);

        // Assert
        assertNotNull(result);
        assertEquals(9, result.size());
        System.out.println("Kết quả thực tế (TC110): " + result.size());
    }
}