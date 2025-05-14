package com.demo.services;

import com.demo.dtos.EmployerDTO;
import com.demo.entities.*;
import com.demo.repositories.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class EmployerServiceImplTest {
    @Autowired
    private EmployerServiceImpl employerService;
    @Autowired
    private EmployerRepository employerRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PostingRepository postingRepository;
    @Autowired
    private TypeAccountRepository typeAccountRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ExperienceRepository experienceRepository;
    @Autowired
    private LocalRepository localRepository;
    @Autowired
    private RankRepository rankRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private WageRepository wageRepository;
    @Autowired
    private ModelMapper mapper;
    @Mock
    private EmployerRepository mockeEmployerRepository;
    @InjectMocks
    private EmployerServiceImpl mockEmployerService;
    @Mock
    private ModelMapper mockMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Employer insertSampleEmployer() {
        // Tạo TypeAccount
        TypeAccount type = new TypeAccount();
        type.setName("Employer");
        typeAccountRepository.save(type);

        // Tạo Account
        Account account = new Account();
        account.setUsername("employer_user");
        account.setPassword("employer123");
        account.setEmail("employer@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("999999");
        account.setWallet(0.0);
        account.setCreated(new Date());
        accountRepository.save(account);

        // Tạo Admin
        Employer employer = new Employer();
        employer.setAccount(account);
        employer.setName("Công ty A");
        employer.setLogo("abc");
        employer.setCover("abc");
        employer.setScale("Vừa"); // ví dụ: "Nhỏ", "Vừa", "Lớn"
        employer.setLink("https://congtya.vn");
        employer.setDescription("Mô tả công ty A");
        employer.setAddress("123 Đường ABC, TP.HCM");
        employer.setMapLink("https://maps.google.com/?q=123+ABC");
        employer.setStatus(true);

        return employerRepository.save(employer);
    }
    /**
     * TC041 - Mục tiêu: Kiểm tra khi truyền vào accountId hợp lệ, hàm trả về đúng EmployerDTO
     * Yêu cầu đầu vào:
     *  - Một Employer tồn tại với accountId là 10
     * Các bước thực hiện:
     *  - Tạo Employer entity với accountId = 10
     *  - Gọi hàm findByAccountId(10)
     * Kết quả mong muốn:
     *  - Trả về đối tượng EmployerDTO không null, đúng accountId
     */
    @Test
    @Order(41)
    @DisplayName("TC041 - findByAccountId với accountId hợp lệ")
    void TC041_findByAccountId_ValidId_ReturnsEmployerDTO() {
        // Arrange
        Employer inserted = insertSampleEmployer(); // tạo entity thật trong DB
        int accountId = inserted.getAccount().getId();

        // Act
        EmployerDTO result = employerService.findByAccountID(accountId);

        // Assert
        System.out.println("Kết quả mong muốn: " + inserted.getName());
        System.out.println("Kết quả thực tế (TC041): " + result.getName());
        assertNotNull(result, "Kết quả trả về không được null");
        assertEquals(inserted.getName(), result.getName(), "Tên công ty không khớp");
    }

    /**
     * TC042 - Mục tiêu: Kiểm tra khi truyền vào accountId không tồn tại, hàm trả về null
     * Yêu cầu đầu vào:
     *  - accountId không tồn tại trong hệ thống (ví dụ: 999999)
     * Các bước thực hiện:
     *  - Gọi hàm findByAccountId(999999)
     * Kết quả mong muốn:
     *  - Trả về null
     */
    @Test
    @Order(42)
    @DisplayName("TC042 - findByAccountId với accountId không tồn tại")
    void TC042_findByAccountId_InvalidId_ReturnsNull() {
        // Arrange
        int nonexistentAccountId = 999999; // giả sử ID này chưa tồn tại

        EmployerDTO result = employerService.findByAccountID(nonexistentAccountId);

        assertNull(result);


    }

    /**
     * TC043 - Mục tiêu: Kiểm tra khi truyền vào id hợp lệ, hàm trả về Employer đúng
     * Yêu cầu đầu vào:
     *  - Một Employer tồn tại với id là 1
     * Các bước thực hiện:
     *  - Tạo Employer entity với id = 1
     *  - Gọi hàm getDetail(1)
     * Kết quả mong muốn:
     *  - Trả về đối tượng Employer không null, và id trùng với id đã tạo
     */
    @Test
    @Order(43)
    @DisplayName("TC043 - getDetail với id hợp lệ")
    void TC043_getDetail_ValidId_ReturnsEmployer() {
        // Arrange
        Employer inserted = insertSampleEmployer();  // Giả sử đã có method này chèn dữ liệu với id = 1

        // Act
        Employer result = employerService.getDetail(inserted.getId());

        // Assert
        System.out.println("Kết quả thực tế (TC043): " + result.getName());
        assertNotNull(result, "Kết quả mong muốn là không null");
        assertEquals(inserted.getId(), result.getId(), "Id của Employer không khớp");
        assertEquals(inserted.getName(), result.getName(), "Tên của Employer không khớp");
    }

    /**
     * TC044 - Mục tiêu: Kiểm tra khi truyền vào id không hợp lệ, hàm ném ra exception
     * Yêu cầu đầu vào:
     *  - id không tồn tại trong cơ sở dữ liệu (ví dụ: 999999)
     * Các bước thực hiện:
     *  - Không tạo Employer với id = 999999
     *  - Gọi hàm getDetail(999999)
     * Kết quả mong muốn:
     *  - Trả về exception NoSuchElementException
     */
    @Test
    @Order(44)
    @DisplayName("TC044 - getDetail với id không hợp lệ")
    void TC044_getDetail_InvalidId_ThrowsNoSuchElementException() {
        // Arrange
        int invalidId = 999999; // Giả sử id này không tồn tại trong cơ sở dữ liệu

        // Act & Assert

        Employer result= employerService.getDetail(invalidId);


        assertNull(result);
    }


    /**
     * TC045 - Mục tiêu: Kiểm tra khi truyền vào EmployerDTO hợp lệ, hàm trả về true
     * Yêu cầu đầu vào:
     *  - EmployerDTO hợp lệ với các trường thông tin đầy đủ
     * Các bước thực hiện:
     *  - Tạo một EmployerDTO hợp lệ với thông tin như tên công ty, logo, mô tả, v.v...
     *  - Gọi hàm save() với EmployerDTO hợp lệ
     * Kết quả mong muốn:
     *  - Trả về true, dữ liệu được lưu thành công vào cơ sở dữ liệu
     */
    @Test
    @Order(45)
    @DisplayName("TC045 - save với EmployerDTO hợp lệ")
    void TC045_save_ValidEmployerDTO_ReturnsTrue() {
        // Arrange
        EmployerDTO employerDTO = new EmployerDTO();
        employerDTO.setAccountId(108);
        employerDTO.setName("Công ty B");
        employerDTO.setLogo("logo_b");
        employerDTO.setCover("cover_b");
        employerDTO.setScale("Lớn");
        employerDTO.setLink("https://congtyb.vn");
        employerDTO.setDescription("Mô tả công ty B");
        employerDTO.setAddress("456 Đường XYZ, TP.HCM");

        // Act
        boolean result = employerService.save(employerDTO);

        // Assert
        System.out.println("Kết quả thực tế (TC045): " + result);
        assertTrue(result, "Kết quả mong muốn là true khi lưu thành công");
    }

    /**
     * TC046 - Mục tiêu: Kiểm tra khi truyền vào EmployerDTO không hợp lệ, hàm trả về false
     * Yêu cầu đầu vào:
     *  - EmployerDTO không hợp lệ (ví dụ thiếu các trường thông tin bắt buộc)
     * Các bước thực hiện:
     *  - Tạo một EmployerDTO không hợp lệ (thiếu thông tin như tên công ty, logo)
     *  - Gọi hàm save() với EmployerDTO không hợp lệ
     * Kết quả mong muốn:
     *  - Trả về false, và không lưu dữ liệu vào cơ sở dữ liệu
     */
    @Test
    @Order(46)
    @DisplayName("TC046 - save với EmployerDTO không hợp lệ")
    void TC046_save_InvalidEmployerDTO_ReturnsFalse() {
        // Arrange
        EmployerDTO employerDTO = new EmployerDTO();
        employerDTO.setName("");  // Thiếu tên công ty, một trường bắt buộc
        employerDTO.setLogo("logo_invalid");
        employerDTO.setCover("cover_invalid");
        employerDTO.setScale("Lớn");
        employerDTO.setLink("https://congtyinvalid.vn");

        // Act
        boolean result = employerService.save(employerDTO);

        // Assert
        System.out.println("Kết quả thực tế (TC046): " + result);
        assertFalse(result, "Kết quả mong muốn là false khi lưu không thành công");
    }


    @Test
    @Order(47)
    @DisplayName("TC047 - findbyname với tên hợp lệ")
    void TC047_findByName_ValidName_ReturnsEmployerDTO() {
        // Arrange - chèn dữ liệu công ty có tên "Công ty B"
        TypeAccount type = new TypeAccount();
        type.setName("Employer");
        typeAccountRepository.save(type);

        Account account = new Account();
        account.setUsername("employer_b");
        account.setPassword("pass123");
        account.setEmail("b@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("999999");
        account.setWallet(0.0);
        account.setCreated(new Date());
        accountRepository.save(account);

        Employer employer = new Employer();
        employer.setAccount(account);
        employer.setName("Công ty B");
        employer.setLogo("logoB.png");
        employer.setCover("coverB.png");
        employer.setScale("Vừa");
        employer.setLink("https://congtyb.vn");
        employer.setDescription("Mô tả công ty B");
        employer.setAddress("123 Đường XYZ, TP.HCM");
        employer.setMapLink("https://maps.google.com/?q=xyz");
        employer.setStatus(true);
        employerRepository.save(employer);

        // Act
        EmployerDTO result = employerService.findbyname("Công ty B");

        // Assert
        System.out.println("Kết quả thực tế (TC047): " + result.getName());
        assertNotNull(result);
        assertEquals("Công ty B", result.getName());
    }

    @Test
    @Order(48)
    @DisplayName("TC048 - findbyname với tên không tồn tại")
    void TC048_findByName_InvalidName_ReturnsNull() {
        // Act
        EmployerDTO result = employerService.findbyname("Không Tồn Tại");

        // Assert
        System.out.println("Kết quả thực tế (TC048): " + result);
        assertNull(result);
    }

    @Test
    @Order(49)
    @DisplayName("TC049 - findbyname với tên rỗng")
    void TC049_findByName_EmptyName_ReturnsNull() {
        // Act
        EmployerDTO result = employerService.findbyname("");

        // Assert
        System.out.println("Kết quả thực tế (TC049): " + result);
        assertNull(result);
    }


    @Test
    @Order(50)
    @DisplayName("TC050 - findAll khi có dữ liệu Employer")
    void TC050_findAll_WhenDataExists_ReturnsEmployerDTOList() {
        // Arrange
        insertSampleEmployer();
        insertSampleEmployer();

        // Act
        List<EmployerDTO> result = employerService.findAll();

        // Assert
        System.out.println("Kết quả thực tế (TC050): Số lượng = " + result.size());
        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }

    @Test
    @Order(51)
    @DisplayName("TC051 - findAll với dữ liệu rỗng, trả về danh sách rỗng")
    void TC051_findAll_WhenNoData_ReturnsEmptyList() {
        // Arrange
        Mockito.when(mockeEmployerRepository.findAll()).thenReturn(Collections.emptyList());
        when(mockMapper.map(any(), any(Type.class))).thenReturn(Collections.emptyList());

        // Act
        List<EmployerDTO> result = mockEmployerService.findAll();

        // Assert
        System.out.println("Kết quả thực tế (TC051): Số lượng employer = " + result.size());
        assertNotNull(result);
        assertEquals(0, result.size());
    }
    @Test
    @Order(52)
    @DisplayName("TC052 - getAll với dữ liệu có sẵn trong database")
    void TC052_getAll_WhenDataExists_ReturnsEmployerList() {
        // Arrange: Chèn dữ liệu thực tế vào database
        Employer employer2 = insertSampleEmployer();
        Employer employer1 = insertSampleEmployer();

        // Act: Gọi phương thức getAll()
        Iterable<Employer> result = employerService.getAll();

        // Assert: Kiểm tra kết quả trả về
        assertNotNull(result);
        assertTrue(((List<Employer>) result).size() > 0);
        assertTrue(((List<Employer>) result).contains(employer1));
    }

    @Test
    @Order(53)
    @DisplayName("TC053 - getAll với dữ liệu không có trong mock")
    void TC053_getAll_WithNoDataInMock_ReturnsEmptyList() {
        // Arrange: Mock repository trả về danh sách rỗng
        when(mockeEmployerRepository.findAll()).thenReturn(Collections.emptyList());
        when(mockMapper.map(any(), any(Type.class))).thenReturn(Collections.emptyList());

        // Act: Gọi phương thức getAll()
        Iterable<Employer> result = mockEmployerService.getAll();

        // Assert: Kiểm tra kết quả trả về
        assertNotNull(result);
        assertEquals(0, ((List<Employer>) result).size());

    }

    @Test
    @Order(54)
    @DisplayName("TC054 - Cập nhật trạng thái với ID hợp lệ và status true")
    @Transactional
    void TC054_updateStatusById_ValidIdAndTrue_ReturnsTrue() {
        // Arrange: Tạo Employer và chèn vào DB
        Employer employer = insertSampleEmployer();

        // Lấy ID của Employer đã tạo
        int employerId = employer.getId();

        // Act: Gọi phương thức updateStatusById
        Boolean result = employerService.updateStatusById(employerId, true);

        // Assert: Kiểm tra kết quả trả về
        assertNotNull(result);
        assertTrue(result);  // Expected: true

        // Kiểm tra trạng thái của Employer trong DB
        Employer updatedEmployer = employerRepository.findById(employerId).get();
        assertTrue(updatedEmployer.isStatus());  // Trạng thái phải là true
    }

    @Test
    @Order(55)
    @DisplayName("TC055 - Cập nhật trạng thái với ID hợp lệ và status false")
    @Transactional
    void TC055_updateStatusById_ValidIdAndFalse_ReturnsTrue() {
        // Arrange: Tạo Employer và chèn vào DB
        Employer employer = insertSampleEmployer();

        // Lấy ID của Employer đã tạo
        int employerId = employer.getId();

        // Act: Gọi phương thức updateStatusById
        Boolean result = employerService.updateStatusById(employerId, false);

        // Assert: Kiểm tra kết quả trả về
        assertNotNull(result);
        assertTrue(result);  // Expected: true

        // Kiểm tra trạng thái của Employer trong DB
        Employer updatedEmployer = employerRepository.findById(employerId).get();
        assertFalse(updatedEmployer.isStatus());  // Trạng thái phải là false
    }

    @Test
    @Order(56)
    @DisplayName("TC056 - Cập nhật trạng thái với ID không tồn tại")
    @Transactional
    void TC056_updateStatusById_InvalidId_ReturnsFalse() {
        // Act: Gọi phương thức updateStatusById với ID không tồn tại
        Boolean result = employerService.updateStatusById(999, true);

        // Assert: Kiểm tra kết quả trả về
        assertNotNull(result);
        assertFalse(result);  // Expected: false
    }

    @Test
    @Order(57)
    @DisplayName("TC057 - Tìm kiếm EmployerDTO với ID hợp lệ")
    @Transactional
    void TC057_findByID_ValidId_ReturnsEmployerDTO() {
        // Arrange: Tạo Employer và chèn vào DB
        Employer employer = insertSampleEmployer();

        // Lấy ID của Employer đã tạo
        int employerId = employer.getId();

        // Act: Gọi phương thức findByID
        EmployerDTO result = employerService.findByID(employerId);

        // Assert: Kiểm tra kết quả trả về
        assertNotNull(result);  // Kết quả không được null
        assertEquals(employer.getName(), result.getName());  // Dữ liệu tên phải khớp

        // Clean up: Xóa bản ghi Employer
        employerRepository.delete(employer);
    }

    @Test
    @Order(58)
    @DisplayName("TC058 - Tìm kiếm EmployerDTO với ID không tồn tại")
    @Transactional
    void TC058_findByID_InvalidId_ReturnsNull() {
        // Act: Gọi phương thức findByID với ID không tồn tại
        EmployerDTO result = employerService.findByID(999);

        // Assert: Kiểm tra kết quả trả về
        assertNull(result);  // Expected: null
    }



    @Test
    @Order(59)
    @DisplayName("TC059 - Tìm kiếm EmployerDTO với username hợp lệ")
    @Transactional
    void TC059_findByUsername_ValidUsername_ReturnsEmployerDTO() {
        // Arrange: Tạo Employer và chèn vào DB
        Employer employer = insertSampleEmployer();

        // Act: Gọi phương thức findByUsername
        EmployerDTO result = employerService.findByUsername(employer.getAccount().getUsername());

        // Assert: Kiểm tra kết quả trả về
        assertNotNull(result);  // Kết quả không được null
        assertEquals(employer.getAccount().getUsername(), result.getAccountName());  // Dữ liệu username phải khớp

    }
    @Test
    @Order(60)
    @DisplayName("TC060 - Tìm kiếm EmployerDTO với username không tồn tại")
    @Transactional
    void TC060_findByUsername_InvalidUsername_ReturnsNull() {
        // Act: Gọi phương thức findByUsername với username không tồn tại
        EmployerDTO result = employerService.findByUsername("non_existing_user");

        // Assert: Kiểm tra kết quả trả về
        assertNull(result);  // Expected: null
    }

    @Test
    @Order(61)
    @DisplayName("TC061 - Tìm kiếm EmployerDTO với tên hợp lệ")
    @Transactional
    void TC061_searchByName_ValidName_ReturnsEmployerDTOList() {
        // Arrange: Tạo và chèn các bản ghi Employer
        Employer employer = insertSampleEmployer();
        Employer employer1 = insertSampleEmployer();

        // Act: Gọi phương thức searchByName với tên "Công ty A"
        List<EmployerDTO> result = employerService.searchByName("ty A");

        // Assert: Kiểm tra kết quả trả về
        assertNotNull(result);  // Kết quả không được null
        assertEquals(2, result.size());  // Kết quả chỉ có 1 đối tượng
        assertEquals("Công ty A", result.get(0).getName());  // Tên phải khớp

        // Clean up: Xóa bản ghi Employer
        employerRepository.deleteAll();
    }

    @Test
    @Order(62)
    @DisplayName("TC062 - Tìm kiếm EmployerDTO với tên không tồn tại")
    @Transactional
    void TC062_searchByName_InvalidName_ReturnsEmptyList() {
        // Act: Gọi phương thức searchByName với tên không tồn tại
        List<EmployerDTO> result = employerService.searchByName("Công ty X");

        // Assert: Kiểm tra kết quả trả về
        assertNotNull(result);  // Kết quả không được null
        assertTrue(result.isEmpty());  // Danh sách phải rỗng
    }


    @Test
    @Order(63)
    @DisplayName("TC063 - getByAccountId trả về Employer khi accountId tồn tại")
    @Transactional
    void TC063_getByAccountId_ValidAccountId_ReturnsEmployer() {
        // Tạo TypeAccount
        TypeAccount type = new TypeAccount();
        type.setName("Employer");
        typeAccountRepository.save(type);

        // Tạo Account
        Account account = new Account();
        account.setUsername("employer_user");
        account.setPassword("employer123");
        account.setEmail("employer@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("999999");
        account.setWallet(0.0);
        account.setCreated(new Date());
        accountRepository.save(account);

        // Tạo Admin
        Employer employer = new Employer();
        employer.setAccount(account);
        employer.setName("Công ty A");
        employer.setLogo("abc");
        employer.setCover("abc");
        employer.setScale("Vừa"); // ví dụ: "Nhỏ", "Vừa", "Lớn"
        employer.setLink("https://congtya.vn");
        employer.setDescription("Mô tả công ty A");
        employer.setAddress("123 Đường ABC, TP.HCM");
        employer.setMapLink("https://maps.google.com/?q=123+ABC");
        employer.setStatus(true);

        employerRepository.save(employer);

        // Act
        Employer result = employerService.getByAccountId(account.getId());

        // Assert
        assertNotNull(result);
        assertEquals(account.getId(), result.getAccount().getId());

    }

    @Test
    @Order(64)
    @DisplayName("TC064 - getByAccountId trả về null khi accountId không tồn tại")
    @Transactional
    void TC064_getByAccountId_InvalidAccountId_ReturnsNull() {
        // Act
        Employer result = employerService.getByAccountId(99999); // ID không tồn tại

        // Assert
        assertNull(result);
    }


    @Test
    @Order(65)
    @DisplayName("TC065 - save trả về true khi lưu Employer hợp lệ")
    @Transactional
    void TC065_SaveValidEmployer_ReturnsTrue() {
        // Arrange
        TypeAccount type = new TypeAccount();
        type.setName("Employer");
        typeAccountRepository.save(type);

        Account account = new Account();
        account.setUsername("employer_test");
        account.setPassword("123456");
        account.setEmail("test@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("1q423435");
        account.setWallet(0.0);
        account.setCreated(new Date());
        accountRepository.save(account);

        Employer employer = new Employer();
        employer.setAccount(account);
        employer.setName("Công ty A");
        employer.setLogo("logo.png");
        employer.setCover("cover.png");
        employer.setScale("Vừa");
        employer.setLink("https://example.com");
        employer.setDescription("Mô tả");
        employer.setAddress("123 Đường ABC");
        employer.setMapLink("https://map.com");
        employer.setStatus(true);

        // Act
        boolean result = employerService.save(employer);

        // Assert
        assertTrue(result);
    }

    @Test
    @Order(66)
    @DisplayName("TC067 - save trả về false khi Employer null")
    void TC067_SaveNullEmployer_ReturnsFalse() {
        // Act
        Employer employer = new Employer();
        boolean result = employerService.save(employer);

        // Assert
        assertFalse(result);
    }



    @Test
    @Order(67)
    @DisplayName("TC067 - Trả về danh sách top employer đúng số lượng khi có dữ liệu")
    @Transactional
    void TC067_FindTopEmployer_WithData_ReturnsTopList() {
        // Arrange
        TypeAccount type = new TypeAccount();
        type.setName("Employer");
        typeAccountRepository.save(type);

        Account acc1 = new Account(type, "top1", "password123", new Date(), "top1@gmail.com", true, "SEC123456", 100.0  );
        Account acc2 = new Account(type, "top2", "password123", new Date(), "top1@gmail.com", true, "SEC123456", 100.0 );
        Account acc3 = new Account(type, "top5", "password123", new Date(), "top1@gmail.com", true, "SEC123456", 100.0 );
        accountRepository.saveAll(List.of(acc1, acc2, acc3));

        Employer emp1 = new Employer(); emp1.setAccount(acc1); emp1.setName("E1");
        Employer emp2 = new Employer(); emp2.setAccount(acc2); emp2.setName("E2");
        Employer emp3 = new Employer(); emp3.setAccount(acc3); emp3.setName("E3");
        employerRepository.saveAll(List.of(emp1, emp2, emp3));

        // Giả sử bạn đã tạo các đối tượng này ở trước đó:
        Category category = categoryRepository.save(new Category("CNTT", true));
        Experience experience = experienceRepository.save(new Experience("1-2 năm", true));
        Local local = localRepository.save(new Local("Hà Nội", true));
        Rank rank = rankRepository.save(new Rank("Nhân viên", true));
        com.demo.entities.Type types = typeRepository.save(new com.demo.entities.Type("Full-time", true));
        Wage wage = wageRepository.save(new Wage("10-15 triệu", 10, 15,true));

        Date now = new Date();
        Date deadline = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000); // +7 ngày

// emp1 có 5 bài đăng
        for (int i = 0; i < 5; i++) {
            Postings post = new Postings(
                    category,
                    emp1,
                    experience,
                    local,
                    rank,
                    types,
                    wage,
                    "Post " + i,
                    "Mô tả bài đăng " + i,
                    now,
                    deadline,
                    "Nam",
                    1,
                    true,
                    true
            );
            postingRepository.save(post);
        }

// emp2 có 3 bài đăng
        for (int i = 0; i < 3; i++) {
            Postings post = new Postings(
                    category,
                    emp2,
                    experience,
                    local,
                    rank,
                    types,
                    wage,
                    "Post " + i,
                    "Mô tả bài đăng " + i,
                    now,
                    deadline,
                    "Nữ",
                    2,
                    true,
                    true
            );
            postingRepository.save(post);
        }

// emp3 có 1 bài đăng
        for (int i = 0; i < 1; i++) {
            Postings post = new Postings(
                    category,
                    emp3,
                    experience,
                    local,
                    rank,
                    types,
                    wage,
                    "Post " + i,
                    "Mô tả bài đăng " + i,
                    now,
                    deadline,
                    "Không yêu cầu",
                    1,
                    true,
                    true
            );
            postingRepository.save(post);
        }


        // Act
        List<Employer> topEmployers = (List<Employer>) employerService.findTop(2);

        // Assert
        assertEquals(2, topEmployers.size());
        assertEquals("E1", topEmployers.get(0).getName()); // nhiều nhất
        assertEquals("E2", topEmployers.get(1).getName()); // kế tiếp
    }

    @Test
    @Order(68)
    @DisplayName("TC68- Trả về danh sách rỗng")
    @Transactional
    void TC068_testFindTop_FindTopEmployer_ReturnsEmptyList() {
        // Giả lập repository trả về danh sách rỗng
        Mockito.when(mockeEmployerRepository.findTop(Mockito.anyInt()))
                .thenReturn(Collections.emptyList());

        // Gọi hàm cần test
        Iterable<Employer> result = mockEmployerService.findTop(3);

        // Kết quả mong muốn: danh sách rỗng
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.iterator().hasNext());

        System.out.println("Kết quả mong muốn: []");
        System.out.print("Kết quả thực tế: ");
        result.forEach(System.out::println);
    }

    @Test
    @DisplayName("TC69 - findAll2 trả về đúng số lượng khi có ít hơn 6 employer")
    @Transactional
    void TC069_FindAll2_UnderSixEmployers() {
        Employer employer1 = insertSampleEmployer();
        Employer employer2 = insertSampleEmployer();
        Employer employer3 = insertSampleEmployer();
        Employer employer4 = insertSampleEmployer();
        Employer employer5 = insertSampleEmployer();
        Employer employer6 = insertSampleEmployer();
        // Gọi hàm test
        List<EmployerDTO> result = employerService.findAll2();

        // Kiểm tra kết quả
        Assertions.assertEquals(6, result.size());
        for (int i = 1; i < result.size(); i++) {
            Assertions.assertTrue(result.get(i - 1).getId() > result.get(i).getId(),
                    "ID tại vị trí " + (i - 1) + " phải lớn hơn vị trí " + i);
        }

        System.out.println("Kết quả mong muốn: 6 employer theo thứ tự id giảm dần");
        result.forEach(e -> System.out.println("ID: " + e.getId()));
    }
}