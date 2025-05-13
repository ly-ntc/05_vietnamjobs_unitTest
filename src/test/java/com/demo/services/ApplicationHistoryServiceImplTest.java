package com.demo.services;

import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.entities.*;
import com.demo.repositories.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class ApplicationHistoryServiceImplTest {
    @Autowired
    private ApplicationHistoryServiceImpl applicationHistoryService;

    @Autowired
    private ApplicationHistoryRepository applicationHistoryRepository;
    @Autowired
    private TypeAccountRepository typeAccountRepository;

    private ApplicationHistoryDTO applicationHistoryDTO;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SeekerRepository seekerRepository;

    @Autowired
    private PostingRepository postingRepository;
    @Autowired
    private EmployerRepository employerRepository;

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
    private ModelMapper modelMapper;

    @Mock
    private ApplicationHistoryRepository applicationHistoryRepository2;

    @InjectMocks
    private ApplicationHistoryServiceImpl applicationHistoryService2;
    @Mock
    private ModelMapper mockMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    private ApplicationHistory insertSampleApplication() {
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

        // Tạo Employer
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

        Employer  emp = employerRepository.save(employer);
        System.out.println(emp.getId());

        // Tạo seeker
        TypeAccount type2 = new TypeAccount();
        type2.setName("Seeker");
        typeAccountRepository.save(type2);

        // Tạo Account
        Account account2 = new Account();
        account2.setUsername("seeker_user");
        account2.setPassword("seeker123");
        account2.setEmail("seeker@example.com");
        account2.setTypeAccount(type2);
        account2.setStatus(true);
        account2.setSecurityCode("999999");
        account2.setWallet(0.0);
        account2.setCreated(new Date());
        accountRepository.save(account2);

        Seeker seeker = new Seeker(account2, "NTCL", "9185493593","Mô tả", "CV.pdf", true,"avt.png");
        Seeker seek = seekerRepository.save(seeker);
        System.out.println(seek.getId());
        //Tạo posting
        Category category = categoryRepository.save(new Category("CNTT", true));
        Experience experience = experienceRepository.save(new Experience("1-2 năm", true));
        Local local = localRepository.save(new Local("Hà Nội", true));
        Rank rank = rankRepository.save(new Rank("Nhân viên", true));
        com.demo.entities.Type types = typeRepository.save(new com.demo.entities.Type("Full-time", true));
        Wage wage = wageRepository.save(new Wage("10-15 triệu", 10, 15,true));

        Date now = new Date();
        Date deadline = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000); // +7 ngày
        Postings post = new Postings(
                category,
                emp,
                experience,
                local,
                rank,
                types,
                wage,
                "Post " ,
                "Mô tả bài đăng " ,
                now,
                deadline,
                "Nữ",
                2,
                true,
                true
        );
        postingRepository.save(post);
        System.out.println(post.getId());
        //tạo application
        ApplicationHistory applicationHistory = new ApplicationHistory(post, seek, new Date(), 1, 1);
        ApplicationHistory a = applicationHistoryRepository.save(applicationHistory);
        System.out.println(a.getId());
        return  a;
    }

    private ApplicationHistory insertSampleApplicationWithMonthAndYear(int month, int year) {
        // Tạo TypeAccount cho Employer
        TypeAccount type = new TypeAccount();
        type.setName("Employer");
        typeAccountRepository.save(type);

        // Tạo Account cho Employer
        Account account = new Account();
        account.setUsername("employer_user");
        account.setPassword("employer123");
        account.setEmail("employer@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("999999");
        account.setWallet(0.0);
        Date createdDate = getDateWithMonthAndYear(month, year, 1);
        account.setCreated(createdDate);
        accountRepository.save(account);

        // Tạo Employer
        Employer employer = new Employer();
        employer.setAccount(account);
        employer.setName("Công ty A");
        employer.setLogo("abc");
        employer.setCover("abc");
        employer.setScale("Vừa");
        employer.setLink("https://congtya.vn");
        employer.setDescription("Mô tả công ty A");
        employer.setAddress("123 Đường ABC, TP.HCM");
        employer.setMapLink("https://maps.google.com/?q=123+ABC");
        employer.setStatus(true);
        Employer emp = employerRepository.save(employer);

        // Tạo TypeAccount cho Seeker
        TypeAccount type2 = new TypeAccount();
        type2.setName("Seeker");
        typeAccountRepository.save(type2);

        // Tạo Account cho Seeker
        Account account2 = new Account();
        account2.setUsername("seeker_user");
        account2.setPassword("seeker123");
        account2.setEmail("seeker@example.com");
        account2.setTypeAccount(type2);
        account2.setStatus(true);
        account2.setSecurityCode("999999");
        account2.setWallet(0.0);
        account2.setCreated(createdDate);
        accountRepository.save(account2);

        // Tạo Seeker
        Seeker seeker = new Seeker(account2, "NTCL", "9185493593", "Mô tả", "CV.pdf", true, "avt.png");
        Seeker seek = seekerRepository.save(seeker);

        // Tạo các tham chiếu khác
        Category category = categoryRepository.save(new Category("CNTT", true));
        Experience experience = experienceRepository.save(new Experience("1-2 năm", true));
        Local local = localRepository.save(new Local("Hà Nội", true));
        Rank rank = rankRepository.save(new Rank("Nhân viên", true));
        com.demo.entities.Type types = typeRepository.save(new com.demo.entities.Type("Full-time", true));
        Wage wage = wageRepository.save(new Wage("10-15 triệu", 10, 15, true));

        Date postDate = getDateWithMonthAndYear(month, year, 5);
        Date deadline = new Date(postDate.getTime() + 7L * 24 * 60 * 60 * 1000); // +7 ngày

        // Tạo Posting
        Postings post = new Postings(
                category,
                emp,
                experience,
                local,
                rank,
                types,
                wage,
                "Post ",
                "Mô tả bài đăng ",
                postDate,
                deadline,
                "Nữ",
                2,
                true,
                true
        );
        postingRepository.save(post);

        // Tạo ApplicationHistory
        Date appliedDate = getDateWithMonthAndYear(month, year, 6);
        ApplicationHistory applicationHistory = new ApplicationHistory(post, seek, appliedDate, 0, 1);
        ApplicationHistory a = applicationHistoryRepository.save(applicationHistory);
        return a;
    }
    private Date getDateWithMonthAndYear(int month, int year, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Java Calendar tháng bắt đầu từ 0
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }



    @Test
    @Order(71)
    @DisplayName("TC71 - findAll trả về danh sách đúng khi có dữ liệu")
    @Transactional
    void TC071_FindAll_WithData() {
        ApplicationHistory saved = insertSampleApplication();
        List<ApplicationHistoryDTO> result = applicationHistoryService.findAll();

        Assertions.assertEquals(6, result.size());

    }

    @Test
    @Order(72)
    @Transactional
    @DisplayName("TC072 - findAll trả về danh sách rỗng khi không có dữ liệu (mock)")
    void TC072_FindAll_Empty_WithMock() {
        // Arrange: giả lập repository trả về danh sách rỗng
        when(applicationHistoryRepository2.findAll()).thenReturn(Collections.emptyList());
        when(mockMapper.map(any(), any(java.lang.reflect.Type.class))).thenReturn(Collections.emptyList());
        // Act
        List<ApplicationHistoryDTO> result = applicationHistoryService2.findAll();

        // Assert
        assertEquals(0, result.size());

    }
    @Test
    @Order(73)
    @DisplayName("TC073 - findBySeekerID với dữ liệu thật")
    @Transactional
    void TC073_FindBySeekerID_WithRealData() {
        // Arrange: chèn dữ liệu thật
        ApplicationHistory saved = insertSampleApplication(); // phương thức chèn dữ liệu mẫu
        int seekerId = saved.getSeeker().getId();

        // Act
        ApplicationHistoryDTO dto = applicationHistoryService.findBySeekerID(seekerId);

        // Assert
        assertNotNull(dto, "DTO không được null");
        assertEquals(seekerId, dto.getSeekerID(), "Seeker ID phải trùng");
        assertEquals(saved.getPostings().getId(), dto.getPostingID(), "Postings ID phải trùng");
        assertEquals(saved.getStatus(), dto.getStatus(), "Status phải trùng");
        assertEquals(saved.getResult(), dto.getResult(), "Result phải trùng");

        System.out.println("DTO: " + dto.getId() + " - SeekerID: " + dto.getSeekerID());
    }
    @Test
    @Order(74)
    @DisplayName("TC074 - findBySeekerID với seekerId không tồn tại")
    @Transactional
    void TC074_FindBySeekerID_WithInvalidSeeker() {
        // Arrange: ID không tồn tại trong DB
        int invalidId = 999;

        // Act + Assert

        ApplicationHistoryDTO dto = applicationHistoryService.findBySeekerID(invalidId);

        assertNull(dto);

    }

    @Test
    @Order(75)
    @DisplayName("TC075 - save với dữ liệu hợp lệ")
    @Transactional
    void TC075_Save_WithValidData() {
        // Arrange: Chèn dữ liệu mẫu hợp lệ
        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setPostingID(12);
        dto.setSeekerID(1);

        // Act
        boolean result = applicationHistoryService.save(dto);

        // Assert
        assertTrue(result, "Phương thức save phải trả về true khi dữ liệu hợp lệ");
        // Kiểm tra rằng dữ liệu được lưu thành công bằng cách truy vấn lại từ database (hoặc thông qua mock nếu cần)
        ApplicationHistory saved = applicationHistoryRepository.findBySeekerID(dto.getSeekerID());
        assertNotNull(saved, "Dữ liệu lưu vào database không được null");
        assertEquals(dto.getPostingID(), saved.getPostings().getId(), "Postings ID phải trùng");
        assertEquals(dto.getSeekerID(), saved.getSeeker().getId(), "Seeker ID phải trùng");


        System.out.println("Dữ liệu đã lưu thành công: " + saved.getId());
    }

//    @Test
//    @Order(76)
//    @DisplayName("TC076 - save với dữ liệu không hợp lệ (postingID không hợp lệ)")
//    @Transactional
//    void TC076_Save_WithInvalidPostingID() {
//        // Arrange: Chèn dữ liệu không hợp lệ
//        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
//        dto.setPostingID(-1); // postingID không hợp lệ
//        dto.setSeekerID(1);
//
//        // Act
//        boolean result = applicationHistoryService.save(dto);
//
//        // Assert
//        assertFalse(result, "Phương thức save phải trả về false khi postingID không hợp lệ");
//        // Kiểm tra rằng dữ liệu không được lưu vào database
//        ApplicationHistory saved = applicationHistoryRepository.findBySeekerID(dto.getSeekerID());
//        assertNull(saved, "Dữ liệu không được lưu khi postingID không hợp lệ");
//
//        System.out.println("Không thể lưu dữ liệu với postingID không hợp lệ.");
//    }

    @Test
    @Order(77)
    @DisplayName("TC077 - save với lỗi ngoại lệ trong quá trình lưu")
    @Transactional
    void TC077_Save_WithDatabaseException() {
        // Arrange: Chèn dữ liệu hợp lệ và giả lập lỗi database
        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setPostingID(1);
        dto.setSeekerID(1);

        // Giả lập lỗi trong repository
        doThrow(new RuntimeException("Database error")).when(applicationHistoryRepository2).save(any());

        // Act
        boolean result = applicationHistoryService2.save(dto);

        // Assert
        assertFalse(result, "Phương thức save phải trả về false khi xảy ra lỗi trong quá trình lưu");
        // Kiểm tra rằng dữ liệu không được lưu vào database
        ApplicationHistory saved = applicationHistoryRepository2.findBySeekerID(dto.getSeekerID());
        assertNull(saved, "Dữ liệu không được lưu khi có lỗi trong quá trình lưu");

        System.out.println("Lỗi khi lưu dữ liệu vào database.");
    }


    @Test
    @Order(78)
    @DisplayName("TC078 - findBySeekerID1 với seekerID hợp lệ")
    @Transactional
    void TC078_FindBySeekerID1_WithValidSeekerID() {
        // Arrange: Chèn dữ liệu hợp lệ
        ApplicationHistory saved = insertSampleApplication(); // Phương thức chèn dữ liệu hợp lệ
        int seekerId = saved.getSeeker().getId();

        // Act
        List<ApplicationHistoryDTO> result = applicationHistoryService.findBySeekerID1(seekerId);

        // Assert
        assertNotNull(result, "Kết quả trả về không được null");
        assertFalse(result.isEmpty(), "Danh sách không được rỗng khi có dữ liệu");
        assertEquals(seekerId, result.get(0).getSeekerID(), "Seeker ID trong kết quả phải trùng");

        System.out.println("Kết quả tìm kiếm: " + result.size() + " bản ghi.");
    }

    @Test
    @Order(79)
    @DisplayName("TC079 - findBySeekerID1 với seekerID không hợp lệ")
    @Transactional
    void TC079_FindBySeekerID1_WithInvalidSeekerID() {
        // Arrange: Giả sử seekerID không tồn tại trong hệ thống
        int invalidSeekerId = -1;

        // Act
        List<ApplicationHistoryDTO> result = applicationHistoryService.findBySeekerID1(invalidSeekerId);

        // Assert
        assertNotNull(result, "Kết quả trả về không được null");
        assertTrue(result.isEmpty(), "Danh sách phải rỗng khi không có dữ liệu");

        System.out.println("Kết quả tìm kiếm: Không có bản ghi nào.");
    }

    @Test
    @Order(80)
    @DisplayName("TC080 - findBySeekerID1 kiểm tra tính đúng đắn của dữ liệu")
    @Transactional
    void TC080_FindBySeekerID1_DataValidation() {
        // Arrange: Chèn dữ liệu hợp lệ
        ApplicationHistory saved = insertSampleApplication(); // Phương thức chèn dữ liệu hợp lệ
        int seekerId = saved.getSeeker().getId();

        // Act
        List<ApplicationHistoryDTO> result = applicationHistoryService.findBySeekerID1(seekerId);

        // Assert
        assertNotNull(result, "Kết quả trả về không được null");
        assertFalse(result.isEmpty(), "Danh sách không được rỗng khi có dữ liệu");

        ApplicationHistoryDTO dto = result.get(0);
        assertEquals(seekerId, dto.getSeekerID(), "Seeker ID trong kết quả phải trùng");
        assertEquals(saved.getPostings().getId(), dto.getPostingID(), "Posting ID trong kết quả phải trùng");
        assertEquals(saved.getStatus(), dto.getStatus(), "Status trong kết quả phải trùng");

        System.out.println("Dữ liệu trong kết quả: SeekerID - " + dto.getSeekerID() + ", PostingID - " + dto.getPostingID());
    }

    @Test
    @Order(81)
    @DisplayName("TC081 - findBySeekerID1 với nhiều bản ghi cho seekerID")
    @Transactional
    void TC081_FindBySeekerID1_WithMultipleResults() {

        // Act
        List<ApplicationHistoryDTO> result = applicationHistoryService.findBySeekerID1(7);

        // Assert
        assertNotNull(result, "Kết quả trả về không được null");
        assertFalse(result.isEmpty(), "Danh sách không được rỗng khi có dữ liệu");
        assertEquals(4,result.size());

        System.out.println("Kết quả tìm kiếm: " + result.size() + " bản ghi.");
    }

    @Test
    @Order(82)
    @DisplayName("TC082 - findByPostingID với postingID hợp lệ")
    @Transactional
    void TC082_FindByPostingID_WithValidPostingID() {
        // Arrange: Chèn dữ liệu hợp lệ
        ApplicationHistory saved = insertSampleApplication(); // Phương thức chèn dữ liệu hợp lệ
        int postingId = saved.getPostings().getId();

        // Act
        List<ApplicationHistory> result = applicationHistoryService.findByPostingID(postingId);

        // Assert
        assertNotNull(result, "Kết quả trả về không được null");
        assertFalse(result.isEmpty(), "Danh sách không được rỗng khi có dữ liệu");
    }

    @Test
    @Order(83)
    @DisplayName("TC083 - findByPostingID với postingID không hợp lệ")
    @Transactional
    void TC083_FindByPostingID_WithInvalidPostingID() {
        // Arrange: Giả sử postingID không tồn tại trong hệ thống
        int invalidPostingId = -1;

        // Act
        List<ApplicationHistory> result = applicationHistoryService.findByPostingID(invalidPostingId);

        // Assert
        assertNotNull(result, "Kết quả trả về không được null");
        assertTrue(result.isEmpty(), "Danh sách phải rỗng khi không có dữ liệu");

        System.out.println("Kết quả tìm kiếm: Không có bản ghi nào.");
    }


    @Test
    @Order(84)
    @DisplayName("TC084 - findByPostingID với nhiều bản ghi cho postingID")
    @Transactional
    void TC084_FindByPostingID_WithMultipleResults() {

        // Act
        List<ApplicationHistory> result = applicationHistoryService.findByPostingID(18);

        // Assert
        assertNotNull(result, "Kết quả trả về không được null");
        assertFalse(result.isEmpty(), "Danh sách không được rỗng khi có dữ liệu");
        assertEquals(2, result.size());

        System.out.println("Kết quả tìm kiếm: " + result.size() + " bản ghi.");
    }

    @Test
    @Order(85)
    @DisplayName("TC085 - existByPostId với PostId hợp lệ và có dữ liệu")
    @Transactional
    void TC085_ExistByPostId_WithValidData() {
        // Arrange: Chèn dữ liệu hợp lệ vào database
        ApplicationHistory saved = insertSampleApplication(); // Phương thức chèn dữ liệu hợp lệ
        int postId = saved.getPostings().getId(); // Lấy PostId đã chèn

        // Act
        boolean result = applicationHistoryService.existByPostId(postId);

        // Assert
        assertTrue(result, "Phương thức existByPostId phải trả về true khi có ít nhất một bản ghi với PostId");
        System.out.println("ExistByPostId với PostId " + postId + ": " + result);
    }

    @Test
    @Order(86)
    @DisplayName("TC086 - existByPostId với PostId không có dữ liệu")
    @Transactional
    void TC086_ExistByPostId_WithNoData() {
        // Arrange: Lấy PostId không tồn tại trong database
        int postId = 9999; // Giả sử đây là PostId không tồn tại trong database

        // Act
        boolean result = applicationHistoryService.existByPostId(postId);

        // Assert
        assertFalse(result, "Phương thức existByPostId phải trả về false khi không có bản ghi nào với PostId");
        System.out.println("ExistByPostId với PostId " + postId + ": " + result);
    }


    @Test
    @Order(87)
    @DisplayName("TC087 - countAll khi có dữ liệu")
    @Transactional
    void TC087_CountAll_WithData() {
        // Arrange: Chèn dữ liệu hợp lệ vào database
        insertSampleApplication(); // Phương thức chèn một bản ghi mẫu

        // Act
        int result = applicationHistoryService.countAll();

        // Assert
        assertEquals(6, result, "Phương thức countAll phải trả về số lượng bản ghi đúng");
        System.out.println("Số lượng bản ghi trong cơ sở dữ liệu: " + result);
    }

    @Test
    @Order(88)
    @DisplayName("TC088 - countAll khi không có dữ liệu")
    @Transactional
    void TC088_CountAll_WithNoData() {
        // Arrange: Mock phương thức countAll của repository để trả về 0
        when(applicationHistoryRepository2.countAll()).thenReturn(0);

        // Act
        int result = applicationHistoryService2.countAll();

        // Assert
        assertEquals(0, result, "Phương thức countAll phải trả về 0 khi không có bản ghi nào");
        System.out.println("Số lượng bản ghi trong cơ sở dữ liệu: " + result);
    }

    @Test
    @Order(89)
    @DisplayName("TC089 - countByResult với dữ liệu hợp lệ")
    @Transactional
    void TC089_CountByResult_WithValidData() {
        // Arrange: Chèn dữ liệu vào cơ sở dữ liệu với result là 1
        insertSampleApplication(); // Phương thức chèn bản ghi với result = 1

        // Act
        int result = applicationHistoryService.countByResult(1);

        // Assert
        assertEquals(3, result, "Phương thức countByResult phải trả về số lượng bản ghi có result là 1");
        System.out.println("Số lượng bản ghi với result = 1: " + result);
    }

    @Test
    @Order(90)
    @DisplayName("TC090 - countByResult khi không có dữ liệu")
    @Transactional
    void TC090_CountByResult_WithNoData() {

        // Act
        int result = applicationHistoryService.countByResult(5);

        // Assert
        assertEquals(0, result, "Phương thức countByResult phải trả về 0 khi không có bản ghi nào với result = 999");
        System.out.println("Số lượng bản ghi với result = 999: " + result);
    }

    @Test
    @Order(91)
    @DisplayName("TC091 - countByMonthAndYear với dữ liệu hợp lệ")
    @Transactional
    void TC091_CountByMonthAndYear_WithValidData() {
        // Arrange: Chèn dữ liệu vào cơ sở dữ liệu với month = 5 và year = 2023
        insertSampleApplicationWithMonthAndYear(6, 2023); // Phương thức chèn bản ghi với tháng 5, năm 2023

        // Act
        int result = applicationHistoryService.countByMonthAndYear(6, 2023);

        // Assert
        assertEquals(1, result, "Phương thức countByMonthAndYear phải trả về số lượng bản ghi có tháng = 5 và năm = 2023");
        System.out.println("Số lượng bản ghi với tháng = 5 và năm = 2023: " + result);
    }

    @Test
    @Order(92)
    @DisplayName("TC092 - countByMonthAndYear khi không có dữ liệu")
    @Transactional
    void TC092_CountByMonthAndYear_WithNoData() {

        // Act
        int result = applicationHistoryService.countByMonthAndYear(12, 2025);

        // Assert
        assertEquals(0, result, "Phương thức countByMonthAndYear phải trả về 0 khi không có bản ghi nào với tháng = 12 và năm = 2025");
        System.out.println("Số lượng bản ghi với tháng = 12 và năm = 2025: " + result);
    }

    @Test
    @Order(93)
    @DisplayName("TC093 - findByStatusAndResult có dữ liệu khớp")
    @Transactional
    void TC093_FindByStatusAndResult_WithMatchingData() {
        //chèn bản ghi mới
        ApplicationHistory app = insertSampleApplication();

        // Act
        List<ApplicationHistoryDTO> result = applicationHistoryService.findByStatusAndResult(app.getSeeker().getId(), 1, 1);

        // Assert
        assertNotNull(result, "Danh sách trả về không được null");
        assertFalse(result.isEmpty(), "Danh sách không được rỗng khi có dữ liệu");
        assertEquals(1, result.get(0).getStatus(), "Trạng thái phải đúng");
        assertEquals(1, result.get(0).getResult(), "Kết quả phải đúng");
        assertEquals(app.getSeeker().getId(), result.get(0).getSeekerID(), "Seeker ID phải đúng");
    }

    @Test
    @Order(94)
    @DisplayName("TC094 - findByStatusAndResult không có dữ liệu phù hợp")
    @Transactional
    void TC094_FindByStatusAndResult_WithNoMatchingData() {
        // Act
        List<ApplicationHistoryDTO> result = applicationHistoryService.findByStatusAndResult(999, 0, 0);

        // Assert
        assertNotNull(result, "Kết quả trả về không được null");
        assertTrue(result.isEmpty(), "Danh sách phải rỗng khi không có dữ liệu phù hợp");
    }

    @Test
    @Order(95)
    @DisplayName("TC095 - findBySeekerIDAndPosting có dữ liệu phù hợp")
    @Transactional
    void TC095_FindBySeekerIDAndPosting_WithMatchingData() {
        ApplicationHistory app = insertSampleApplication();

        // Act
        ApplicationHistoryDTO result = applicationHistoryService.findBySeekerIDAndPosting(app.getSeeker().getId(), app.getPostings().getId());

        // Assert
        assertNotNull(result, "Kết quả không được null");
        assertEquals(app.getSeeker().getId(), result.getSeekerID(), "Seeker ID phải đúng");
        assertEquals(app.getPostings().getId(), result.getPostingID(), "Posting ID phải đúng");
    }

    @Test
    @Order(96)
    @DisplayName("TC096 - findBySeekerIDAndPosting không có dữ liệu phù hợp")
    @Transactional
    void TC096_FindBySeekerIDAndPosting_WithNoMatchingData() {
        // Act & Assert
        assertThrows(IndexOutOfBoundsException.class, () -> {
            applicationHistoryService.findBySeekerIDAndPosting(999, 999);
        }, "Phải ném ra IndexOutOfBoundsException khi không có dữ liệu");
    }

    @Test
    @Order(97)
    @DisplayName("TC097 - findAppliedCV khi có dữ liệu ứng tuyển")
    @Transactional
    void TC097_FindAppliedCV_WithData() {
        // Arrange
        ApplicationHistory app = insertSampleApplication();

        // Act
        List<ApplicationHistoryDTO> result = applicationHistoryService.findAppliedCV(app.getSeeker().getId());

        // Assert
        assertNotNull(result, "Kết quả không được null");
        assertFalse(result.isEmpty(), "Danh sách không được rỗng");
        assertEquals(app.getSeeker().getId(), result.get(0).getSeekerID(), "Seeker ID phải đúng");
    }

    @Test
    @Order(98)
    @DisplayName("TC098 - findAppliedCV khi không có dữ liệu ứng tuyển")
    @Transactional
    void TC098_FindAppliedCV_NoData() {
        // Act
        List<ApplicationHistoryDTO> result = applicationHistoryService.findAppliedCV(999); // ID không tồn tại

        // Assert
        assertNotNull(result, "Danh sách không được null");
        assertTrue(result.isEmpty(), "Danh sách phải rỗng khi không có dữ liệu");
    }


}