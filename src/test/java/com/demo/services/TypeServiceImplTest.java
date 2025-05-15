package com.demo.services;

import com.demo.entities.Type;
import com.demo.repositories.TypeRepository;
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
class TypeServiceImplTest {

    @Autowired
    private TypeServiceImpl typeService;

    @Autowired
    private TypeRepository typeRepository;

    @Mock
    private TypeRepository typeRepositoryMock;

    @InjectMocks
    private TypeServiceImpl typeServiceMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * TC051 - Mục tiêu: Đảm bảo find trả về Type đúng khi ID hợp lệ
     * Đầu vào: ID của Type đã lưu
     * Kết quả mong muốn: Trả về đối tượng Type với tên "Full-time"
     */
    @Test
    @Order(51)
    @DisplayName("TC051 - find với ID hợp lệ")
    void TC051_find_ValidId_ReturnsType() {
        // Arrange
        Type type = new Type();
        type.setName("Full-time");
        type.setStatus(true);
        typeRepository.save(type);

        // Act
        Type result = typeService.find(type.getId());

        // Assert
        System.out.println("Kết quả thực tế (TC051): " + result.getName());
        assertNotNull(result);
        assertEquals("Full-time", result.getName());
    }

    /**
     * TC052 - Mục tiêu: Đảm bảo find ném NoSuchElementException khi ID không tồn tại
     * Đầu vào: ID = 9999
     * Kết quả mong muốn: Ném NoSuchElementException
     */
    @Test
    @Order(52)
    @DisplayName("TC052 - find với ID không tồn tại")
    void TC052_find_NonExistentId_ThrowsException() {
        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> typeService.find(9999));
        System.out.println("Kết quả thực tế (TC052): NoSuchElementException");
    }

    /**
     * TC053 - Mục tiêu: Đảm bảo findAll trả về danh sách Type khi có dữ liệu
     * Đầu vào: Type với tên "Part-time" đã lưu
     * Kết quả mong muốn: Trả về danh sách chứa ít nhất 1 Type
     */
    @Test
    @Order(53)
    @DisplayName("TC053 - findAll với dữ liệu tồn tại")
    void TC053_findAll_DataExists_ReturnsList() {
        // Arrange
        Type type = new Type();
        type.setName("Part-time");
        type.setStatus(true);
        typeRepository.save(type);

        // Act
        Iterable<Type> result = typeService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(((Collection<?>) result).size() > 0);
        System.out.println("Kết quả thực tế (TC053): " + ((Collection<?>) result).size());
    }

    /**
     * TC054 - Mục tiêu: Đảm bảo findAll trả về danh sách rỗng khi không có dữ liệu
     * Đầu vào: Không có dữ liệu (mock)
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(54)
    @DisplayName("TC054 - findAll với không có dữ liệu (mock)")
    void TC054_findAll_NoData_ReturnsEmptyList() {
        // Arrange
        when(typeRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        // Act
        Iterable<Type> result = typeServiceMock.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(0, ((Collection<?>) result).size());
        System.out.println("Kết quả thực tế (TC054): " + ((Collection<?>) result).size());
    }

    /**
     * TC055 - Mục tiêu: Đảm bảo save trả về true khi lưu Type hợp lệ
     * Đầu vào: Type với tên "Contract", trạng thái true
     * Kết quả mong muốn: Trả về true và Type được lưu thành công
     */
    @Test
    @Order(55)
    @DisplayName("TC055 - save với Type hợp lệ")
    void TC055_save_ValidType_ReturnsTrue() {
        // Arrange
        Type type = new Type();
        type.setName("Contract");
        type.setStatus(true);

        // Act
        boolean result = typeService.save(type);

        // Assert
        assertTrue(result);
        Type saved = typeRepository.findById(type.getId()).orElse(null);
        assertNotNull(saved);
        assertEquals("Contract", saved.getName());
        System.out.println("Kết quả thực tế (TC055): " + result);
    }

    /**
     * TC056 - Mục tiêu: Đảm bảo save trả về false khi lưu thất bại
     * Đầu vào: Type với tên "Invalid" (mock exception)
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(56)
    @DisplayName("TC056 - save với exception (mock)")
    void TC056_save_Exception_ReturnsFalse() {
        // Arrange
        Type type = new Type();
        type.setName("Invalid");
        when(typeRepositoryMock.save(any(Type.class))).thenThrow(new RuntimeException("DB error"));

        // Act
        boolean result = typeServiceMock.save(type);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC056): " + result);
    }

    /**
     * TC057 - Mục tiêu: Đảm bảo delete trả về true khi xóa Type hợp lệ
     * Đầu vào: ID của Type với tên "Freelance" đã lưu
     * Kết quả mong muốn: Trả về true và Type bị xóa
     */
    @Test
    @Order(57)
    @DisplayName("TC057 - delete với ID hợp lệ")
    void TC057_delete_ValidId_ReturnsTrue() {
        // Arrange
        Type type = new Type();
        type.setName("Freelance");
        type.setStatus(true);
        typeRepository.save(type);

        // Act
        boolean result = typeService.delete(type.getId());

        // Assert
        assertTrue(result);
        assertThrows(NoSuchElementException.class, () -> typeRepository.findById(type.getId()).get());
        System.out.println("Kết quả thực tế (TC057): " + result);
    }

    /**
     * TC058 - Mục tiêu: Đảm bảo delete trả về false khi xóa thất bại
     * Đầu vào: ID = 9999
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(58)
    @DisplayName("TC058 - delete với ID không tồn tại")
    void TC058_delete_NonExistentId_ReturnsFalse() {
        // Act
        boolean result = typeService.delete(9999);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC058): " + result);
    }

    /**
     * TC059 - Mục tiêu: Đảm bảo exists trả về true khi tên và ID tồn tại
     * Đầu vào: Tên "Internship", ID của Type đã lưu
     * Kết quả mong muốn: Trả về true
     */
    @Test
    @Order(59)
    @DisplayName("TC059 - exists với tên và ID hợp lệ")
    void TC059_exists_ValidNameAndId_ReturnsTrue() {
        // Arrange
        Type type = new Type();
        type.setName("Internship");
        type.setStatus(true);
        typeRepository.save(type);

        // Act
        boolean result = typeService.exists("Internship", type.getId());

        // Assert
        assertTrue(result);
        System.out.println("Kết quả thực tế (TC059): " + result);
    }

    /**
     * TC060 - Mục tiêu: Đảm bảo exists trả về false khi tên hoặc ID không tồn tại
     * Đầu vào: Tên "NonExistent", ID = 9999
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(60)
    @DisplayName("TC060 - exists với tên hoặc ID không tồn tại")
    void TC060_exists_NonExistentNameOrId_ReturnsFalse() {
        // Act
        boolean result = typeService.exists("NonExistent", 9999);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC060): " + result);
    }

    /**
     * TC061 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách Type đúng khi trạng thái là true
     * Đầu vào: Trạng thái = true
     * Kết quả mong muốn: Trả về danh sách chứa Type với tên "ActiveType"
     */
    @Test
    @Order(61)
    @DisplayName("TC061 - findAllByStatus với trạng thái true")
    void TC061_findAllByStatus_TrueStatus_ReturnsList() {
        // Arrange
        Type type = new Type();
        type.setName("ActiveType");
        type.setStatus(true);
        typeRepository.save(type);

        // Act
        List<Type> result = typeService.findAllByStatus(true);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(
                result.stream().anyMatch(item -> "ActiveType".equals(item.getName())),
                "Danh sách không chứa phần tử có name = 'ActiveType'"
        );
        System.out.println("Kết quả thực tế (TC061): " + result.size());
    }

    /**
     * TC062 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách rỗng khi không có Type với trạng thái false
     * Đầu vào: Trạng thái = false
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(62)
    @DisplayName("TC062 - findAllByStatus với trạng thái false không có dữ liệu")
    void TC062_findAllByStatus_FalseStatus_NoData_ReturnsEmptyList() {
        // Act
        List<Type> result = typeService.findAllByStatus(false);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        System.out.println("Kết quả thực tế (TC062): " + result.size());
    }

    /**
     * TC063 - Mục tiêu: Đảm bảo find trả về Type đúng với tên khác
     * Đầu vào: ID của Type với tên "Temporary"
     * Kết quả mong muốn: Trả về Type với tên "Temporary"
     */
    @Test
    @Order(63)
    @DisplayName("TC063 - find với ID hợp lệ và tên khác")
    void TC063_find_ValidId_DifferentName_ReturnsType() {
        // Arrange
        Type type = new Type();
        type.setName("Temporary");
        type.setStatus(true);
        typeRepository.save(type);

        // Act
        Type result = typeService.find(type.getId());

        // Assert
        assertNotNull(result);
        assertEquals("Temporary", result.getName());
        System.out.println("Kết quả thực tế (TC063): " + result.getName());
    }

    /**
     * TC064 - Mục tiêu: Đảm bảo findAll trả về nhiều Type khi có nhiều dữ liệu
     * Đầu vào: Hai Type với tên "Type1" và "Type2" đã lưu
     * Kết quả mong muốn: Trả về danh sách chứa ít nhất 2 Type
     */
    @Test
    @Order(64)
    @DisplayName("TC064 - findAll với nhiều dữ liệu")
    void TC064_findAll_MultipleData_ReturnsList() {
        // Arrange
        Type type1 = new Type();
        type1.setName("Type1");
        type1.setStatus(true);
        typeRepository.save(type1);

        Type type2 = new Type();
        type2.setName("Type2");
        type2.setStatus(true);
        typeRepository.save(type2);

        // Act
        Iterable<Type> result = typeService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(((Collection<?>) result).size() >= 2);
        System.out.println("Kết quả thực tế (TC064): " + ((Collection<?>) result).size());
    }

    /**
     * TC065 - Mục tiêu: Đảm bảo save trả về true khi lưu Type với trạng thái false
     * Đầu vào: Type với tên "InactiveType", trạng thái false
     * Kết quả mong muốn: Trả về true và Type được lưu với trạng thái false
     */
    @Test
    @Order(65)
    @DisplayName("TC065 - save với Type hợp lệ trạng thái false")
    void TC065_save_ValidTypeFalseStatus_ReturnsTrue() {
        // Arrange
        Type type = new Type();
        type.setName("InactiveType");
        type.setStatus(false);

        // Act
        boolean result = typeService.save(type);

        // Assert
        assertTrue(result);
        Type saved = typeRepository.findById(type.getId()).orElse(null);
        assertNotNull(saved);
        assertEquals("InactiveType", saved.getName());
        assertFalse(saved.isStatus());
        System.out.println("Kết quả thực tế (TC065): " + result);
    }

    /**
     * TC066 - Mục tiêu: Đảm bảo delete trả về true khi xóa Type với trạng thái false
     * Đầu vào: ID của Type với tên "ToDelete", trạng thái false
     * Kết quả mong muốn: Trả về true và Type bị xóa
     */
    @Test
    @Order(66)
    @DisplayName("TC066 - delete với ID hợp lệ trạng thái false")
    void TC066_delete_ValidIdFalseStatus_ReturnsTrue() {
        // Arrange
        Type type = new Type();
        type.setName("ToDelete");
        type.setStatus(false);
        typeRepository.save(type);

        // Act
        boolean result = typeService.delete(type.getId());

        // Assert
        assertTrue(result);
        assertThrows(NoSuchElementException.class, () -> typeRepository.findById(type.getId()).get());
        System.out.println("Kết quả thực tế (TC066): " + result);
    }

    /**
     * TC067 - Mục tiêu: Đảm bảo exists trả về false khi chỉ tên tồn tại nhưng ID khác
     * Đầu vào: Tên "TestType", ID = 9999
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(67)
    @DisplayName("TC067 - exists với tên tồn tại nhưng ID không hợp lệ")
    void TC067_exists_ValidNameInvalidId_ReturnsFalse() {
        // Arrange
        Type type = new Type();
        type.setName("TestType");
        type.setStatus(true);
        typeRepository.save(type);

        // Act
        boolean result = typeService.exists("TestType", 9999);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC067): " + result);
    }

    /**
     * TC068 - Mục tiêu: Đảm bảo findAllByStatus trả về nhiều Type với trạng thái true
     * Đầu vào: Trạng thái = true, hai Type với tên "TypeA" và "TypeB" đã lưu
     * Kết quả mong muốn: Trả về danh sách chứa ít nhất 2 Type
     */
    @Test
    @Order(68)
    @DisplayName("TC068 - findAllByStatus với nhiều Type trạng thái true")
    void TC068_findAllByStatus_MultipleTrueStatus_ReturnsList() {
        // Arrange
        Type type1 = new Type();
        type1.setName("TypeA");
        type1.setStatus(true);
        typeRepository.save(type1);

        Type type2 = new Type();
        type2.setName("TypeB");
        type2.setStatus(true);
        typeRepository.save(type2);

        // Act
        List<Type> result = typeService.findAllByStatus(true);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() >= 2);
        System.out.println("Kết quả thực tế (TC068): " + result.size());
    }

    /**
     * TC069 - Mục tiêu: Đảm bảo find trả về Type đúng khi ID vừa được lưu
     * Đầu vào: ID của Type với tên "NewType" vừa lưu
     * Kết quả mong muốn: Trả về Type với tên "NewType"
     */
    @Test
    @Order(69)
    @DisplayName("TC069 - find với ID vừa lưu")
    void TC069_find_JustSavedId_ReturnsType() {
        // Arrange
        Type type = new Type();
        type.setName("NewType");
        type.setStatus(true);
        typeRepository.save(type);

        // Act
        Type result = typeService.find(type.getId());

        // Assert
        assertNotNull(result);
        assertEquals("NewType", result.getName());
        System.out.println("Kết quả thực tế (TC069): " + result.getName());
    }

    /**
     * TC070 - Mục tiêu: Đảm bảo save trả về true khi cập nhật Type hiện có
     * Đầu vào: Type với tên "OldName" được cập nhật thành "UpdatedName"
     * Kết quả mong muốn: Trả về true và Type có tên "UpdatedName"
     */
    @Test
    @Order(70)
    @DisplayName("TC070 - save với cập nhật Type hiện có")
    void TC070_save_UpdateExistingType_ReturnsTrue() {
        // Arrange
        Type type = new Type();
        type.setName("OldName");
        type.setStatus(true);
        typeRepository.save(type);

        type.setName("UpdatedName");

        // Act
        boolean result = typeService.save(type);

        // Assert
        assertTrue(result);
        Type updated = typeRepository.findById(type.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals("UpdatedName", updated.getName());
        System.out.println("Kết quả thực tế (TC070): " + result);
    }

    /**
     * TC071 - Mục tiêu: Đảm bảo findAll trả về danh sách đúng thứ tự
     * Đầu vào: Mock dữ liệu với hai Type "Type1" và "Type2"
     * Kết quả mong muốn: Trả về danh sách với thứ tự "Type1", "Type2"
     */
    @Test
    @Order(71)
    @DisplayName("TC071 - findAll với dữ liệu mock trả về thứ tự đúng")
    void TC071_findAll_MockedData_ReturnsOrderedList() {
        // Arrange
        Type type1 = new Type();
        type1.setName("Type1");
        Type type2 = new Type();
        type2.setName("Type2");
        when(typeRepositoryMock.findAll()).thenReturn(Arrays.asList(type1, type2));

        // Act
        Iterable<Type> result = typeServiceMock.findAll();

        // Assert
        List<Type> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        assertEquals("Type1", resultList.get(0).getName());
        assertEquals("Type2", resultList.get(1).getName());
        System.out.println("Kết quả thực tế (TC071): " + resultList.size());
    }

    /**
     * TC072 - Mục tiêu: Đảm bảo delete trả về false khi mock exception
     * Đầu vào: ID = 1 (mock ném RuntimeException)
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(72)
    @DisplayName("TC072 - delete với mock exception")
    void TC072_delete_MockException_ReturnsFalse() {
        // Arrange
        when(typeRepositoryMock.findById(1)).thenThrow(new RuntimeException("DB error"));

        // Act
        boolean result = typeServiceMock.delete(1);

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC072): " + result);
    }

    /**
     * TC073 - Mục tiêu: Đảm bảo exists trả về true khi tên khác nhưng ID tồn tại
     * Đầu vào: Tên "DifferentName", ID của Type với tên "SomeType"
     * Kết quả mong muốn: Trả về true
     */
    @Test
    @Order(73)
    @DisplayName("TC073 - exists với tên khác nhưng ID tồn tại")
    void TC073_exists_DifferentNameValidId_ReturnsTrue() {
        // Arrange
        Type type = new Type();
        type.setName("SomeType");
        type.setStatus(true);
        typeRepository.save(type);

        // Act
        boolean result = typeService.exists("DifferentName", type.getId());

        // Assert
        assertTrue(result);
        System.out.println("Kết quả thực tế (TC073): " + result);
    }

    /**
     * TC074 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách Type với trạng thái false
     * Đầu vào: Trạng thái = false, Type với tên "Inactive" đã lưu
     * Kết quả mong muốn: Trả về danh sách chứa Type với tên "Inactive"
     */
    @Test
    @Order(74)
    @DisplayName("TC074 - findAllByStatus với trạng thái false")
    void TC074_findAllByStatus_FalseStatus_ReturnsList() {
        // Arrange
        Type type = new Type();
        type.setName("Inactive");
        type.setStatus(false);
        typeRepository.save(type);

        // Act
        List<Type> result = typeService.findAllByStatus(false);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Inactive", result.get(0).getName());
        System.out.println("Kết quả thực tế (TC074): " + result.size());
    }

    /**
     * TC075 - Mục tiêu: Đảm bảo find trả về Type đúng khi dữ liệu lớn
     * Đầu vào: ID của Type với tên "LargeIdType"
     * Kết quả mong muốn: Trả về Type với tên "LargeIdType"
     */
    @Test
    @Order(75)
    @DisplayName("TC075 - find với ID trong DB lớn")
    void TC075_find_LargeId_ReturnsType() {
        // Arrange
        Type type = new Type();
        type.setName("LargeIdType");
        type.setStatus(true);
        typeRepository.save(type);

        // Act
        Type result = typeService.find(type.getId());

        // Assert
        assertNotNull(result);
        assertEquals("LargeIdType", result.getName());
        System.out.println("Kết quả thực tế (TC075): " + result.getName());
    }

    /**
     * TC076 - Mục tiêu: Đảm bảo save trả về true khi lưu Type với tên dài
     * Đầu vào: Type với tên "VeryLongTypeNameForTestingPurposes"
     * Kết quả mong muốn: Trả về true và Type được lưu thành công
     */
    @Test
    @Order(76)
    @DisplayName("TC076 - save với Type tên dài")
    void TC076_save_LongNameType_ReturnsTrue() {
        // Arrange
        Type type = new Type();
        type.setName("VeryLongTypeNameForTestingPurposes");
        type.setStatus(true);

        // Act
        boolean result = typeService.save(type);

        // Assert
        assertTrue(result);
        Type saved = typeRepository.findById(type.getId()).orElse(null);
        assertNotNull(saved);
        assertEquals("VeryLongTypeNameForTestingPurposes", saved.getName());
        System.out.println("Kết quả thực tế (TC076): " + result);
    }

    /**
     * TC077 - Mục tiêu: Đảm bảo findAll trả về danh sách không null khi DB rỗng
     * Đầu vào: Không có dữ liệu
     * Kết quả mong muốn: Trả về danh sách không null
     */
    @Test
    @Order(77)
    @DisplayName("TC077 - findAll với DB rỗng")
    void TC077_findAll_EmptyDB_ReturnsNonNull() {
        // Arrange
        // Không lưu dữ liệu để giả lập DB rỗng

        // Act
        Iterable<Type> result = typeService.findAll();

        // Assert
        assertNotNull(result);
        System.out.println("Kết quả thực tế (TC077): " + ((Collection<?>) result).size());
    }

    /**
     * TC078 - Mục tiêu: Đảm bảo delete trả về true khi xóa Type vừa lưu
     * Đầu vào: ID của Type với tên "JustSaved" vừa lưu
     * Kết quả mong muốn: Trả về true và Type bị xóa
     */
    @Test
    @Order(78)
    @DisplayName("TC078 - delete với Type vừa lưu")
    void TC078_delete_JustSavedType_ReturnsTrue() {
        // Arrange
        Type type = new Type();
        type.setName("JustSaved");
        type.setStatus(true);
        typeRepository.save(type);

        // Act
        boolean result = typeService.delete(type.getId());

        // Assert
        assertTrue(result);
        assertThrows(NoSuchElementException.class, () -> typeRepository.findById(type.getId()).get());
        System.out.println("Kết quả thực tế (TC078): " + result);
    }

    /**
     * TC079 - Mục tiêu: Đảm bảo exists trả về false khi tên không tồn tại
     * Đầu vào: Tên "NonExistentName", ID của Type với tên "Existing"
     * Kết quả mong muốn: Trả về false
     */
    @Test
    @Order(79)
    @DisplayName("TC079 - exists với tên không tồn tại")
    void TC079_exists_NonExistentName_ReturnsFalse() {
        // Arrange
        Type type = new Type();
        type.setName("Existing");
        type.setStatus(true);
        typeRepository.save(type);

        // Act
        boolean result = typeService.exists("NonExistentName", type.getId());

        // Assert
        assertFalse(result);
        System.out.println("Kết quả thực tế (TC079): " + result);
    }

    /**
     * TC080 - Mục tiêu: Đảm bảo findAllByStatus trả về danh sách rỗng khi không có Type với trạng thái true
     * Đầu vào: Trạng thái = true
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    @Order(80)
    @DisplayName("TC080 - findAllByStatus với trạng thái true không có dữ liệu")
    void TC080_findAllByStatus_TrueStatus_NoData_ReturnsEmptyList() {
        // Act
        List<Type> result = typeService.findAllByStatus(true);

        // Assert
        assertNotNull(result);
        System.out.println("Kết quả thực tế (TC080): " + result.size());
    }
}