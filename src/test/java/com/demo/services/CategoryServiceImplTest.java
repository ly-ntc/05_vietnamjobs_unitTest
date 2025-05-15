package com.demo.services;

import com.demo.entities.Category;
import com.demo.repositories.CategoryRepository;
import com.demo.repositories.PostingRepository;
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
class CategoryServiceImplTest {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryRepository categoryRepositoryMock;

    @InjectMocks
    private CategoryServiceImpl categoryServiceMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * TC001 - Mục tiêu: Đảm bảo khi thêm danh mục công việc mới (ví dụ "IT - Phần mềm") vào hệ thống,
     *                  thì hàm findbyname trả về đúng Category theo tên.
     * Đầu vào: Thêm 1 category có tên "IT - Phần mềm" và status = true
     * Kết quả mong muốn: Trả về Category với tên "IT - Phần mềm"
     */
    @Test
    @Order(1)
    @DisplayName("TC001 - findbyname với tên hợp lệ (ngành nghề IT)")
    void TC001_findbyname_ValidJobCategoryName_ReturnsCategory() {
        // Arrange
        Category category = new Category();
        category.setName("IT - Phần mềm");
        category.setStatus(true);
        categoryRepository.save(category);

        // Act
        Category result = categoryService.findbyname("IT - Phần mềm");

        // Assert
        System.out.println("Kết quả thực tế (TC001): " + result.getName());
        assertNotNull(result);
        assertEquals("IT - Phần mềm", result.getName());
    }

    /**
     * TC002 - Mục tiêu: Đảm bảo findbyname trả về null khi tên danh mục công việc không tồn tại
     * Đầu vào: Tìm category với tên "Ngành không tồn tại"
     * Kết quả mong muốn: Trả về null
     */
    @Test
    @Order(2)
    @DisplayName("TC002 - findbyname với tên không tồn tại")
    void TC002_findbyname_NonExistentName_ReturnsNull() {
        // Act
        Category result = categoryService.findbyname("Ngành không tồn tại");

        // Assert
        System.out.println("Kết quả thực tế (TC002): " + result);
        assertNull(result);
    }

    /**
     * TC003 - Mục tiêu: Đảm bảo findbyparentcategoryid trả về Category đúng khi ID cha tồn tại
     * Đầu vào: Thêm 1 parent category "IT" và 1 child category "Lập trình viên" với parentId hợp lệ
     * Kết quả mong muốn: Trả về Category con với tên "Lập trình viên"
     */
    @Test
    @Order(3)
    @DisplayName("TC003 - findbyparentcategoryid với ID hợp lệ")
    void TC003_findbyparentcategoryid_ValidId_ReturnsCategory() {
        // Arrange
        Category parent = new Category();
        parent.setName("IT");
        parent.setStatus(true);
        categoryRepository.save(parent);

        Category child = new Category();
        child.setName("Lập trình viên");
        child.setParentId(parent.getId());
        child.setStatus(true);
        categoryRepository.save(child);

        // Act
        Category result = categoryService.findbyparentcategoryid(parent.getId());

        // Assert
        System.out.println("Kết quả thực tế (TC003): " + result.getName());
        assertNotNull(result);
        assertEquals("Lập trình viên", result.getName());
    }

    /**
     * TC004 - Mục tiêu: Đảm bảo findbyparentcategoryid trả về null khi ID không tồn tại
     * Đầu vào: Tìm category với parentId = 9999
     * Kết quả mong muốn: Trả về null
     */
    @Test
    @Order(4)
    @DisplayName("TC004 - findbyparentcategoryid với ID không tồn tại")
    void TC004_findbyparentcategoryid_NonExistentId_ReturnsNull() {
        // Act
        Category result = categoryService.findbyparentcategoryid(9999);

        // Assert
        System.out.println("Kết quả thực tế (TC004): " + result);
        assertNull(result);
    }

    /**
     * TC005 - Mục tiêu: Đảm bảo getAll trả về danh sách danh mục công việc khi có dữ liệu
     * Đầu vào: Thêm 1 category có tên "Kinh doanh" và status = true
     * Kết quả mong muốn: Trả về danh sách chứa ít nhất 1 phần tử
     */
    @Test
    @Order(5)
    @DisplayName("TC005 - getAll với dữ liệu tồn tại")
    void TC005_getAll_DataExists_ReturnsList() {
        // Arrange
        Category category = new Category();
        category.setName("Kinh doanh");
        category.setStatus(true);
        categoryRepository.save(category);

        // Act
        Iterable<Category> result = categoryService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(((Collection<?>) result).size() > 0);
        System.out.println("Kết quả thực tế (TC005): " + ((Collection<?>) result).size());
    }

    /**
     * TC006 - Mục tiêu: Đảm bảo getAll trả về danh sách rỗng khi không có dữ liệu
     * Đầu vào: Mock repository trả về danh sách rỗng
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(6)
    @DisplayName("TC006 - getAll với không có dữ liệu (mock)")
    void TC006_getAll_NoData_ReturnsEmptyList() {
        // Arrange
        when(categoryRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        // Act
        Iterable<Category> result = categoryServiceMock.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(0, ((Collection<?>) result).size());
        System.out.println("Kết quả thực tế (TC006): " + ((Collection<?>) result).size());
    }

    /**
     * TC007 - Mục tiêu: Đảm bảo getParent trả về danh sách các danh mục công việc cha
     * Đầu vào: Thêm 1 category cha có tên "Marketing" và status = true
     * Kết quả mong muốn: Trả về danh sách chứa ít nhất 1 phần tử
     */
    @Test
    @Order(7)
    @DisplayName("TC007 - getParent với dữ liệu tồn tại")
    void TC007_getParent_DataExists_ReturnsParents() {
        // Arrange
        Category parent = new Category();
        parent.setName("Marketing");
        parent.setStatus(true);
        categoryRepository.save(parent);

        // Act
        Iterable<Category> result = categoryService.getParent();

        // Assert
        assertNotNull(result);
        assertTrue(((Collection<?>) result).size() > 0);
        System.out.println("Kết quả thực tế (TC007): " + ((Collection<?>) result).size());
    }

    /**
     * TC008 - Mục tiêu: Đảm bảo getChildren trả về danh sách danh mục con đúng khi ID hợp lệ
     * Đầu vào: Thêm 1 parent category "Kế toán" và 1 child category "Kiểm toán" với parentId hợp lệ
     * Kết quả mong muốn: Trả về danh sách chứa ít nhất 1 phần tử
     */
    @Test
    @Order(8)
    @DisplayName("TC008 - getChildren với ID hợp lệ")
    void TC008_getChildren_ValidId_ReturnsChildren() {
        // Arrange
        Category parent = new Category();
        parent.setName("Kế toán");
        parent.setStatus(true);
        categoryRepository.save(parent);

        Category child = new Category();
        child.setName("Kiểm toán");
        child.setParentId(parent.getId());
        child.setStatus(true);
        categoryRepository.save(child);

        // Act
        Iterable<Category> result = categoryService.getChildren(parent.getId());

        // Assert
        assertNotNull(result);
        assertTrue(((Collection<?>) result).size() > 0);
        System.out.println("Kết quả thực tế (TC008): " + ((Collection<?>) result).size());
    }

    /**
     * TC009 - Mục tiêu: Đảm bảo getChildren trả về danh sách rỗng khi danh mục không có con
     * Đầu vào: Thêm 1 category có tên "Nhân sự" và status = true
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(9)
    @DisplayName("TC009 - getChildren với ID không có con")
    void TC009_getChildren_NoChildren_ReturnsEmptyList() {
        // Arrange
        Category category = new Category();
        category.setName("Nhân sự");
        category.setStatus(true);
        categoryRepository.save(category);

        // Act
        Iterable<Category> result = categoryService.getChildren(category.getId());

        // Assert
        assertNotNull(result);
        assertEquals(0, ((Collection<?>) result).size());
        System.out.println("Kết quả thực tế (TC009): " + ((Collection<?>) result).size());
    }

    /**
     * TC010 - Mục tiêu: Đảm bảo save trả về true khi lưu danh mục công việc hợp lệ
     * Đầu vào: Thêm 1 category có tên "Tài chính" và status = true
     * Kết quả mong muốn: Trả về true, category được lưu với tên "Tài chính"
     */
    @Test
    @Order(10)
    @DisplayName("TC010 - save với danh mục hợp lệ")
    void TC010_save_ValidCategory_ReturnsTrue() {
        // Arrange
        Category category = new Category();
        category.setName("Tài chính");
        category.setStatus(true);

        // Act
        boolean result = categoryService.save(category);

        // Assert
        assertTrue(result);
        Category saved = categoryRepository.findbyname("Tài chính");
        assertNotNull(saved);
        assertEquals("Tài chính", saved.getName());
        System.out.println("Kết quả thực tế (TC010): " + result);
    }

    /**
     * TC011 - Mục tiêu: Đảm bảo save trả về false khi lưu thất bại do lỗi cơ sở dữ liệu
     * Đầu vào: Mock repository ném RuntimeException khi lưu category có tên "Lỗi"
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(11)
    @DisplayName("TC011 - save với exception (mock)")
    void TC011_save_Exception_ReturnsFalse() {
        // Arrange
        Category category = new Category();
        category.setName("Lỗi");
        when(categoryRepositoryMock.save(any(Category.class))).thenThrow(new RuntimeException("DB error"));

        // Act
        boolean result = categoryServiceMock.save(category);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC011): " + result);
    }

    /**
     * TC012 - Mục tiêu: Đảm bảo delete trả về true khi xóa danh mục công việc hợp lệ
     * Đầu vào: Thêm 1 category có tên "Xóa" và status = true, sau đó xóa theo ID
     * Kết quả mong muốn: Trả về true, category không còn trong DB
     */
    @Test
    @Order(12)
    @DisplayName("TC012 - delete với ID hợp lệ")
    void TC012_delete_ValidId_ReturnsTrue() {
        // Arrange
        Category category = new Category();
        category.setName("Xóa");
        category.setStatus(true);
        categoryRepository.save(category);

        // Act
        boolean result = categoryService.delete(category.getId());

        // Assert
        assertTrue(result);
        assertThrows(NoSuchElementException.class, () -> categoryRepository.findById(category.getId()).get());
        System.out.println("Kết quả thực tế (TC012): " + result);
    }

    /**
     * TC013 - Mục tiêu: Đảm bảo delete trả về false khi xóa danh mục không tồn tại
     * Đầu vào: Xóa category với ID = 9999
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(13)
    @DisplayName("TC013 - delete với ID không tồn tại")
    void TC013_delete_NonExistentId_ReturnsFalse() {
        // Act
        boolean result = categoryService.delete(9999);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC013): " + result);
    }

    /**
     * TC014 - Mục tiêu: Đảm bảo exists trả về true khi tên danh mục và ID tồn tại
     * Đầu vào: Thêm 1 category có tên "Logistics" và status = true, kiểm tra exists với tên và ID
     * Kết quả mong muốn: Trả về true
     */
    @Test
    @Order(14)
    @DisplayName("TC014 - exists với tên và ID hợp lệ")
    void TC014_exists_ValidNameAndId_ReturnsTrue() {
        // Arrange
        Category category = new Category();
        category.setName("Logistics");
        category.setStatus(true);
        categoryRepository.save(category);

        // Act
        boolean result = categoryService.exists("Logistics", category.getId());

        // Assert
        assertTrue(result);
        System.out.println("Kết quả thực tế (TC014): " + result);
    }

    /**
     * TC015 - Mục tiêu: Đảm bảo exists trả về false khi tên hoặc ID danh mục không tồn tại
     * Đầu vào: Kiểm tra exists với tên "Ngành không tồn tại" và ID = 9999
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(15)
    @DisplayName("TC015 - exists với tên hoặc ID không tồn tại")
    void TC015_exists_NonExistentNameOrId_ReturnsFalse() {
        // Act
        boolean result = categoryService.exists("Ngành không tồn tại", 9999);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC015): " + result);
    }

    /**
     * TC016 - Mục tiêu: Đảm bảo find trả về danh mục công việc đúng khi ID hợp lệ
     * Đầu vào: Thêm 1 category có tên "Hành chính" và status = true, tìm theo ID
     * Kết quả mong muốn: Trả về Category với tên "Hành chính"
     */
    @Test
    @Order(16)
    @DisplayName("TC016 - find với ID hợp lệ")
    void TC016_find_ValidId_ReturnsCategory() {
        // Arrange
        Category category = new Category();
        category.setName("Hành chính");
        category.setStatus(true);
        categoryRepository.save(category);

        // Act
        Category result = categoryService.find(category.getId());

        // Assert
        assertNotNull(result);
        assertEquals("Hành chính", result.getName());
        System.out.println("Kết quả thực tế (TC016): " + result.getName());
    }

    /**
     * TC017 - Mục tiêu: Đảm bảo find ném NoSuchElementException khi ID danh mục không tồn tại
     * Đầu vào: Tìm category với ID = 9999
     * Kết quả mong muốn: Ném NoSuchElementException
     */
    @Test
    @Order(17)
    @DisplayName("TC017 - find với ID không tồn tại")
    void TC017_find_NonExistentId_ThrowsException() {
        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> categoryService.find(9999));
        System.out.println("Kết quả thực tế (TC017): NoSuchElementException");
    }

    /**
     * TC018 - Mục tiêu: Đảm bảo getCountPostingsByCategory trả về danh sách số bài đăng theo danh mục
     * Đầu vào: Mock repository trả về danh sách với 1 phần tử ["IT - Phần mềm", 10L]
     * Kết quả mong muốn: Trả về danh sách có 1 phần tử, với tên "IT - Phần mềm" và số bài đăng 10
     */
    @Test
    @Order(18)
    @DisplayName("TC018 - getCountPostingsByCategory với dữ liệu tồn tại")
    void TC018_getCountPostingsByCategory_DataExists_ReturnsList() {
        // Arrange
        List<Object[]> mockData = new ArrayList<>();
        mockData.add(new Object[]{"IT - Phần mềm", 10L});

        when(categoryRepositoryMock.countPostingsByCategory()).thenReturn(mockData);
        // Act
        List<Object[]> result = categoryServiceMock.getCountPostingsByCategory();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("IT - Phần mềm", result.get(0)[0]);
        assertEquals(10L, result.get(0)[1]);
        System.out.println("Kết quả thực tế (TC018): " + result.size());
    }

    /**
     * TC019 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách danh mục theo trạng thái hoạt động
     * Đầu vào: Thêm 1 category có tên "Giáo dục" và status = true
     * Kết quả mong muốn: Trả về danh sách có 1 phần tử với tên "Giáo dục"
     */
    @Test
    @Order(19)
    @DisplayName("TC019 - findAllByStatus với trạng thái true")
    void TC019_findAllByStatus_ValidStatus_ReturnsList() {
        // Arrange
        Category category = new Category();
        category.setName("Giáo dục");
        category.setStatus(true);
        categoryRepository.save(category);

        // Act
        List<Category> result = categoryService.findAllByStatus(true);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(c -> "Giáo dục".equals(c.getName())));
        System.out.println("Kết quả thực tế (TC019): " + result.size());
    }

    /**
     * TC020 - Mục tiêu: Đảm bảo hasChildren trả về true khi danh mục công việc có danh mục con
     * Đầu vào: Thêm 1 parent category "Xây dựng" và 1 child category "Kỹ sư xây dựng" với parentId hợp lệ
     * Kết quả mong muốn: Trả về true
     */
    @Test
    @Order(20)
    @DisplayName("TC020 - hasChildren với ID có con")
    void TC020_hasChildren_HasChildren_ReturnsTrue() {
        // Arrange
        Category parent = new Category();
        parent.setName("Xây dựng");
        parent.setStatus(true);
        categoryRepository.save(parent);

        Category child = new Category();
        child.setName("Kỹ sư xây dựng");
        child.setParentId(parent.getId());
        child.setStatus(true);
        categoryRepository.save(child);

        // Act
        boolean result = categoryService.hasChildren(parent.getId());

        // Assert
        assertTrue(result);
        System.out.println("Kết quả thực tế (TC020): " + result);
    }

    /**
     * TC021 - Mục tiêu: Đảm bảo hasChildren trả về false khi danh mục công việc không có con
     * Đầu vào: Thêm 1 category có tên "Y tế" và status = true
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(21)
    @DisplayName("TC021 - hasChildren với ID không có con")
    void TC021_hasChildren_NoChildren_ReturnsFalse() {
        // Arrange
        Category category = new Category();
        category.setName("Y tế");
        category.setStatus(true);
        categoryRepository.save(category);

        // Act
        boolean result = categoryService.hasChildren(category.getId());

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC021): " + result);
    }

    /**
     * TC022 - Mục tiêu: Đảm bảo findbyname trả về null khi tên danh mục là chuỗi rỗng
     * Đầu vào: Tên category là ""
     * Kết quả mong muốn: Trả về null
     */
    @Test
    @Order(22)
    @DisplayName("TC022 - findbyname với tên rỗng")
    void TC022_findbyname_EmptyName_ReturnsNull() {
        // Act
        Category result = categoryService.findbyname("");

        // Assert
        System.out.println("Kết quả thực tế (TC022): " + result);
        assertNull(result);
    }

    /**
     * TC023 - Mục tiêu: Đảm bảo findbyname trả về null khi tên danh mục là null
     * Đầu vào: Tên category là null
     * Kết quả mong muốn: Trả về null
     */
    @Test
    @Order(23)
    @DisplayName("TC023 - findbyname với tên null")
    void TC023_findbyname_NullName_ReturnsNull() {
        // Act
        Category result = categoryService.findbyname(null);

        // Assert
        System.out.println("Kết quả thực tế (TC023): " + result);
        assertNull(result);
    }

    /**
     * TC024 - Mục tiêu: Đảm bảo findbyparentcategoryid trả về null khi parentId là âm
     * Đầu vào: parentId = -1
     * Kết quả mong muốn: Trả về null
     */
    @Test
    @Order(24)
    @DisplayName("TC024 - findbyparentcategoryid với ID âm")
    void TC024_findbyparentcategoryid_NegativeId_ReturnsNull() {
        // Act
        Category result = categoryService.findbyparentcategoryid(-1);

        // Assert
        System.out.println("Kết quả thực tế (TC024): " + result);
        assertNull(result);
    }

    /**
     * TC025 - Mục tiêu: Đảm bảo getAll trả về danh sách đúng khi có nhiều danh mục công việc
     * Đầu vào: Thêm 3 danh mục công việc vào DB
     * Kết quả mong muốn: Trả về danh sách có 3 phần tử
     */
    @Test
    @Order(25)
    @DisplayName("TC025 - getAll với nhiều dữ liệu")
    void TC025_getAll_MultipleData_ReturnsList() {
        // Arrange
        categoryRepository.save(new Category("CNTT", true));
        categoryRepository.save(new Category("Kế hoạch", true));
        categoryRepository.save(new Category("Sản xuất", true));

        // Act
        Iterable<Category> result = categoryService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(42, ((Collection<?>) result).size());
        System.out.println("Kết quả thực tế (TC025): " + ((Collection<?>) result).size());
    }

    /**
     * TC026 - Mục tiêu: Đảm bảo getAll trả về danh sách rỗng khi DB rỗng
     * Đầu vào: Xóa toàn bộ danh mục trong DB
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(26)
    @DisplayName("TC026 - getAll với DB rỗng")
    void TC026_getAll_EmptyDB_ReturnsEmptyList() {
        // Arrange
        categoryRepository.deleteAll();

        // Act
        Iterable<Category> result = categoryService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(0, ((Collection<?>) result).size());
        System.out.println("Kết quả thực tế (TC026): " + ((Collection<?>) result).size());
    }

    /**
     * TC027 - Mục tiêu: Đảm bảo getChildren trả về danh sách rỗng khi parentId âm
     * Đầu vào: parentId = -1
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(27)
    @DisplayName("TC027 - getChildren với parentId âm")
    void TC027_getChildren_NegativeId_ReturnsEmptyList() {
        // Act
        Iterable<Category> result = categoryService.getChildren(-1);

        // Assert
        assertNotNull(result);
        assertEquals(0, ((Collection<?>) result).size());
        System.out.println("Kết quả thực tế (TC027): " + ((Collection<?>) result).size());
    }

    /**
     * TC028 - Mục tiêu: Đảm bảo save trả về false khi danh mục có tên null
     * Đầu vào: Category với name = null, status = true
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(28)
    @DisplayName("TC028 - save với tên null")
    void TC028_save_NullName_ReturnsFalse() {
        // Arrange
        Category category = new Category();
        category.setStatus(true);

        // Act
        boolean result = categoryService.save(category);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC028): " + result);
    }

    /**
     * TC029 - Mục tiêu: Đảm bảo save trả về false khi danh mục có tên rỗng
     * Đầu vào: Category với name = "", status = true
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(29)
    @DisplayName("TC029 - save với tên rỗng")
    void TC029_save_EmptyName_ReturnsFalse() {
        // Arrange
        Category category = new Category();
        category.setName("");
        category.setStatus(true);

        // Act
        boolean result = categoryService.save(category);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC029): " + result);
    }

    /**
     * TC030 - Mục tiêu: Đảm bảo save cập nhật danh mục công việc thành công khi danh mục đã tồn tại
     * Đầu vào: Thêm 1 danh mục, sau đó cập nhật tên
     * Kết quả mong muốn: Trả về true, tên được cập nhật
     */
    @Test
    @Order(30)
    @DisplayName("TC030 - save cập nhật danh mục")
    void TC030_save_UpdateCategory_ReturnsTrue() {
        // Arrange
        Category category = new Category();
        category.setName("Cũ");
        category.setStatus(true);
        categoryRepository.save(category);

        category.setName("Mới");

        // Act
        boolean result = categoryService.save(category);

        // Assert
        assertTrue(result);
        Category updated = categoryRepository.findbyname("Mới");
        assertNotNull(updated);
        assertEquals("Mới", updated.getName());
        System.out.println("Kết quả thực tế (TC030): " + result);
    }

    /**
     * TC031 - Mục tiêu: Đảm bảo delete trả về false khi ID danh mục âm
     * Đầu vào: ID = -1
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(31)
    @DisplayName("TC031 - delete với ID âm")
    void TC031_delete_NegativeId_ReturnsFalse() {
        // Act
        boolean result = categoryService.delete(-1);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC031): " + result);
    }

    /**
     * TC032 - Mục tiêu: Đảm bảo exists trả về false khi tên danh mục rỗng
     * Đầu vào: name = "", id = 1
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(32)
    @DisplayName("TC032 - exists với tên rỗng")
    void TC032_exists_EmptyName_ReturnsFalse() {
        // Act
        boolean result = categoryService.exists("", 1);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC032): " + result);
    }

    /**
     * TC033 - Mục tiêu: Đảm bảo exists trả về false khi tên danh mục null
     * Đầu vào: name = null, id = 1
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(33)
    @DisplayName("TC033 - exists với tên null")
    void TC033_exists_NullName_ReturnsFalse() {
        // Act
        boolean result = categoryService.exists(null, 1);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC033): " + result);
    }

    /**
     * TC034 - Mục tiêu: Đảm bảo find ném NoSuchElementException khi ID danh mục âm
     * Đầu vào: ID = -1
     * Kết quả mong muốn: Ném NoSuchElementException
     */
    @Test
    @Order(34)
    @DisplayName("TC034 - find với ID âm")
    void TC034_find_NegativeId_ThrowsException() {
        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> categoryService.find(-1));
        System.out.println("Kết quả thực tế (TC034): NoSuchElementException");
    }

    /**
     * TC035 - Mục tiêu: Đảm bảo getCountPostingsByCategory trả về danh sách rỗng khi không có dữ liệu
     * Đầu vào: Mock repository trả về danh sách rỗng
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(35)
    @DisplayName("TC035 - getCountPostingsByCategory không có dữ liệu")
    void TC035_getCountPostingsByCategory_NoData_ReturnsEmptyList() {
        // Arrange
        when(categoryRepositoryMock.countPostingsByCategory()).thenReturn(Collections.emptyList());

        // Act
        List<Object[]> result = categoryServiceMock.getCountPostingsByCategory();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        System.out.println("Kết quả thực tế (TC035): " + result.size());
    }

    /**
     * TC036 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách rỗng khi không có danh mục với trạng thái false
     * Đầu vào: Thêm 1 category với status = true
     * Kết quả mong muốn: Trả về danh sách rỗng khi tìm với status = false
     */
    @Test
    @Order(36)
    @DisplayName("TC036 - findAllByStatus với status false")
    void TC036_findAllByStatus_FalseStatus_ReturnsEmptyList() {
        // Arrange
        Category category = new Category();
        category.setName("Hoạt động");
        category.setStatus(true);
        categoryRepository.save(category);

        // Act
        List<Category> result = categoryService.findAllByStatus(false);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        System.out.println("Kết quả thực tế (TC036): " + result.size());
    }

    /**
     * TC037 - Mục tiêu: Đảm bảo hasChildren trả về false khi ID danh mục âm
     * Đầu vào: ID = -1
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(37)
    @DisplayName("TC037 - hasChildren với ID âm")
    void TC037_hasChildren_NegativeId_ReturnsFalse() {
        // Act
        boolean result = categoryService.hasChildren(-1);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC037): " + result);
    }

    /**
     * TC038 - Mục tiêu: Đảm bảo findbyname trả về đúng danh mục khi mock repository
     * Đầu vào: Mock repository trả về 1 category với tên "Kinh doanh"
     * Kết quả mong muốn: Trả về category với tên "Kinh doanh"
     */
    @Test
    @Order(38)
    @DisplayName("TC038 - findbyname với mock repository")
    void TC038_findbyname_MockRepository_ReturnsCategory() {
        // Arrange
        Category category = new Category();
        category.setName("Kinh doanh");
        category.setStatus(true);
        when(categoryRepositoryMock.findbyname("Kinh doanh")).thenReturn(category);

        // Act
        Category result = categoryServiceMock.findbyname("Kinh doanh");

        // Assert
        assertNotNull(result);
        assertEquals("Kinh doanh", result.getName());
        System.out.println("Kết quả thực tế (TC038): " + result.getName());
    }

    /**
     * TC039 - Mục tiêu: Đảm bảo getAll trả về danh sách đúng khi chỉ có danh mục không hoạt động
     * Đầu vào: Thêm 2 danh mục với status = false
     * Kết quả mong muốn: Trả về danh sách có 2 phần tử
     */
    @Test
    @Order(39)
    @DisplayName("TC039 - getAll với danh mục không hoạt động")
    void TC039_getAll_InactiveCategories_ReturnsList() {
        // Arrange
        categoryRepository.save(new Category("Không hoạt động 1", false));
        categoryRepository.save(new Category("Không hoạt động 2", false));

        // Act
        Iterable<Category> result = categoryService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(41, ((Collection<?>) result).size());
        System.out.println("Kết quả thực tế (TC039): " + ((Collection<?>) result).size());
    }

    /**
     * TC040 - Mục tiêu: Đảm bảo getParent trả về danh sách đúng khi có nhiều danh mục cha
     * Đầu vào: Thêm 2 danh mục cha
     * Kết quả mong muốn: Trả về danh sách có 2 phần tử
     */
    @Test
    @Order(40)
    @DisplayName("TC040 - getParent với nhiều danh mục cha")
    void TC040_getParent_MultipleParents_ReturnsList() {
        // Arrange
        Category parent1 = new Category();
        parent1.setName("Sản xuất");
        parent1.setStatus(true);
        categoryRepository.save(parent1);

        Category parent2 = new Category();
        parent2.setName("Dịch vụ");
        parent2.setStatus(true);
        categoryRepository.save(parent2);

        // Act
        Iterable<Category> result = categoryService.getParent();

        // Assert
        assertNotNull(result);
        assertEquals(17, ((Collection<?>) result).size());
        System.out.println("Kết quả thực tế (TC040): " + ((Collection<?>) result).size());
    }

    /**
     * TC041 - Mục tiêu: Đảm bảo getChildren trả về danh sách đúng khi danh mục cha có con không hoạt động
     * Đầu vào: Thêm 1 danh mục cha và 1 danh mục con với status = false
     * Kết quả mong muốn: Trả về danh sách có 1 phần tử
     */
    @Test
    @Order(41)
    @DisplayName("TC041 - getChildren với danh mục con không hoạt động")
    void TC041_getChildren_InactiveChild_ReturnsList() {
        // Arrange
        Category parent = new Category();
        parent.setName("Du lịch");
        parent.setStatus(true);
        categoryRepository.save(parent);

        Category child = new Category();
        child.setName("Hướng dẫn viên");
        child.setParentId(parent.getId());
        child.setStatus(false);
        categoryRepository.save(child);

        // Act
        Iterable<Category> result = categoryService.getChildren(parent.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, ((Collection<?>) result).size());
        System.out.println("Kết quả thực tế (TC041): " + ((Collection<?>) result).size());
    }

    /**
     * TC042 - Mục tiêu: Đảm bảo save trả về false khi parentId của danh mục không tồn tại
     * Đầu vào: Category với parentId = 9999
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(42)
    @DisplayName("TC042 - save với parentId không tồn tại")
    void TC042_save_NonExistentParentId_ReturnsFalse() {
        // Arrange
        Category category = new Category();
        category.setName("Nhân viên");
        category.setParentId(9999);
        category.setStatus(true);

        // Act
        boolean result = categoryService.save(category);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC042): " + result);
    }

    /**
     * TC043 - Mục tiêu: Đảm bảo delete trả về false khi danh mục có danh mục con
     * Đầu vào: Thêm 1 danh mục cha và 1 danh mục con, xóa danh mục cha
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(43)
    @DisplayName("TC043 - delete danh mục có con")
    void TC043_delete_HasChildren_ReturnsFalse() {
        // Arrange
        Category parent = new Category();
        parent.setName("Công nghệ");
        parent.setStatus(true);
        categoryRepository.save(parent);

        Category child = new Category();
        child.setName("Kỹ thuật viên");
        child.setParentId(parent.getId());
        child.setStatus(true);
        categoryRepository.save(child);

        // Act
        boolean result = categoryService.delete(parent.getId());

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC043): " + result);
    }

    /**
     * TC044 - Mục tiêu: Đảm bảo find trả về danh mục đúng khi danh mục không hoạt động
     * Đầu vào: Thêm 1 danh mục với status = false
     * Kết quả mong muốn: Trả về danh mục với status = false
     */
    @Test
    @Order(44)
    @DisplayName("TC044 - find với danh mục không hoạt động")
    void TC044_find_InactiveCategory_ReturnsCategory() {
        // Arrange
        Category category = new Category();
        category.setName("Không hoạt động");
        category.setStatus(false);
        categoryRepository.save(category);

        // Act
        Category result = categoryService.find(category.getId());

        // Assert
        assertNotNull(result);
        assertEquals("Không hoạt động", result.getName());
        assertFalse(result.isStatus());
        System.out.println("Kết quả thực tế (TC044): " + result.getName());
    }

    /**
     * TC045 - Mục tiêu: Đảm bảo getCountPostingsByCategory trả về danh sách đúng với nhiều danh mục
     * Đầu vào: Mock repository trả về 2 danh mục với số bài đăng
     * Kết quả mong muốn: Trả về danh sách có 2 phần tử
     */
    @Test
    @Order(45)
    @DisplayName("TC045 - getCountPostingsByCategory với nhiều danh mục")
    void TC045_getCountPostingsByCategory_MultipleCategories_ReturnsList() {
        // Arrange
        List<Object[]> mockData = new ArrayList<>();
        mockData.add(new Object[]{"IT - Phần mềm", 10L});
        mockData.add(new Object[]{"Kinh doanh", 5L});
        when(categoryRepositoryMock.countPostingsByCategory()).thenReturn(mockData);

        // Act
        List<Object[]> result = categoryServiceMock.getCountPostingsByCategory();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("IT - Phần mềm", result.get(0)[0]);
        assertEquals(10L, result.get(0)[1]);
        assertEquals("Kinh doanh", result.get(1)[0]);
        assertEquals(5L, result.get(1)[1]);
        System.out.println("Kết quả thực tế (TC045): " + result.size());
    }

    /**
     * TC046 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách đúng khi có cả danh mục hoạt động và không hoạt động
     * Đầu vào: Thêm 2 danh mục với status true và false, tìm với status = true
     * Kết quả mong muốn: Trả về danh sách có 1 phần tử
     */
    @Test
    @Order(46)
    @DisplayName("TC046 - findAllByStatus với mixed status")
    void TC046_findAllByStatus_MixedStatus_ReturnsList() {
        // Arrange
        categoryRepository.save(new Category("Hoạt động", true));
        categoryRepository.save(new Category("Không hoạt động", false));

        // Act
        List<Category> result = categoryService.findAllByStatus(true);

        // Assert
        assertNotNull(result);
        assertEquals(40, result.size());
        assertTrue(
                result.stream().anyMatch(category -> "Hoạt động".equals(category.getName())),
                "Danh sách không chứa category có tên 'Hoạt động'"
        );
        System.out.println("Kết quả thực tế (TC046): " + result.size());
    }

    /**
     * TC047 - Mục tiêu: Đảm bảo hasChildren trả về false khi danh mục không tồn tại
     * Đầu vào: ID = 9999
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(47)
    @DisplayName("TC047 - hasChildren với ID không tồn tại")
    void TC047_hasChildren_NonExistentId_ReturnsFalse() {
        // Act
        boolean result = categoryService.hasChildren(9999);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC047): " + result);
    }

    /**
     * TC048 - Mục tiêu: Đảm bảo save trả về true khi danh mục có parentId hợp lệ
     * Đầu vào: Thêm 1 danh mục cha và 1 danh mục con với parentId hợp lệ
     * Kết quả mong muốn: Trả về true
     */
    @Test
    @Order(48)
    @DisplayName("TC048 - save với parentId hợp lệ")
    void TC048_save_ValidParentId_ReturnsTrue() {
        // Arrange
        Category parent = new Category();
        parent.setName("Bán hàng");
        parent.setStatus(true);
        categoryRepository.save(parent);

        Category child = new Category();
        child.setName("Nhân viên bán hàng");
        child.setParentId(parent.getId());
        child.setStatus(true);

        // Act
        boolean result = categoryService.save(child);

        // Assert
        assertTrue(result);
        Category saved = categoryRepository.findbyname("Nhân viên bán hàng");
        assertNotNull(saved);
        assertEquals(parent.getId(), saved.getParentId());
        System.out.println("Kết quả thực tế (TC048): " + result);
    }

    /**
     * TC049 - Mục tiêu: Đảm bảo findbyname xử lý tên danh mục có dấu cách
     * Đầu vào: Thêm 1 danh mục với tên "Công nghệ thông tin"
     * Kết quả mong muốn: Trả về danh mục với tên "Công nghệ thông tin"
     */
    @Test
    @Order(49)
    @DisplayName("TC049 - findbyname với tên có dấu cách")
    void TC049_findbyname_NameWithSpaces_ReturnsCategory() {
        // Arrange
        Category category = new Category();
        category.setName("Công nghệ thông tin");
        category.setStatus(true);
        categoryRepository.save(category);

        // Act
        Category result = categoryService.findbyname("Công nghệ thông tin");

        // Assert
        assertNotNull(result);
        assertEquals("Công nghệ thông tin", result.getName());
        System.out.println("Kết quả thực tế (TC049): " + result.getName());
    }

    /**
     * TC050 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách đúng khi DB rỗng
     * Đầu vào: Xóa toàn bộ DB, tìm với status = true
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(50)
    @DisplayName("TC050 - findAllByStatus với DB rỗng")
    void TC050_findAllByStatus_EmptyDB_ReturnsEmptyList() {
        // Arrange
        categoryRepository.deleteAll();

        // Act
        List<Category> result = categoryService.findAllByStatus(true);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        System.out.println("Kết quả thực tế (TC050): " + result.size());
    }
}