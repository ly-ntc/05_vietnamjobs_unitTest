package com.demo.services;

import com.demo.entities.Account;
import com.demo.entities.TypeAccount;
import com.demo.repositories.AccountRepository;
import com.demo.repositories.TypeAccountRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import com.demo.entities.Admin;
import com.demo.dtos.AdminDTO;
import com.demo.repositories.AdminRepository;
import com.demo.services.AdminService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class AdminServiceImpTest {

    @Autowired
    private AdminServiceImp adminService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TypeAccountRepository typeAccountRepository;

    @Autowired
    private ModelMapper mapper;

    private Admin insertSampleAdmin() {
        // Tạo TypeAccount
        TypeAccount type = new TypeAccount();
        type.setName("Admin");
        typeAccountRepository.save(type);

        // Tạo Account
        Account account = new Account();
        account.setUsername("admin_user");
        account.setPassword("admin123");
        account.setEmail("admin@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("999999");
        account.setWallet(0.0);
        account.setCreated(new Date());
        accountRepository.save(account);

        // Tạo Admin
        Admin admin = new Admin();
        admin.setAccount(account);
        admin.setFullname("Nguyễn Văn Quản Trị");
        admin.setPhone("0987654321");
        admin.setPhoto("photo.jpg");

        return adminRepository.save(admin);
    }

    /**
     * TC037 - Mục tiêu: Kiểm tra khi truyền vào accountId hợp lệ, hàm trả về đúng AdminDTO
     * Yêu cầu đầu vào:
     *  - Một Admin tồn tại với accountId là 10
     * Các bước thực hiện:
     *  - Tạo Admin entity với accountId = 10
     *  - Gọi hàm findByAccountId(10)
     * Kết quả mong muốn:
     *  - Trả về đối tượng AdminDTO không null, đúng accountId
     */
    @Test
    @Order(37)
    @DisplayName("TC037 - findByAccountId với accountId hợp lệ")
    void TC037_findByAccountId_ValidId_ReturnsAdminDTO() {
        // Arrange
        Admin inserted = insertSampleAdmin();

        // Act
        AdminDTO result = adminService.findByAccountId(inserted.getAccount().getId());

        // Assert
        System.out.println("Kết quả thực tế (TC037): " + result.getFullname());
        assertNotNull(result);
        assertEquals(inserted.getFullname(), result.getFullname());
    }

    /**
     * TC038 - Mục tiêu: Kiểm tra khi truyền vào accountId không tồn tại, mapper vẫn trả về object rỗng
     * Yêu cầu đầu vào:
     *  - Không có Admin nào có accountId = 9999
     * Các bước thực hiện:
     *  - Gọi hàm findByAccountId(9999)
     * Kết quả mong muốn:
     *  - Không ném exception (mapper vẫn trả object dù null source)
     */
    @Test
    @Order(38)
    @DisplayName("TC038 - findByAccountId với accountId không tồn tại")
    void TC038_findByAccountId_InvalidId_ReturnsNullObject() {
        // Act
        AdminDTO result = adminService.findByAccountId(9999);

        // Assert
        System.out.println("Kết quả thực tế (TC038): " + result);
        assertNotNull(result); // mapper vẫn tạo object dù source null
        assertEquals(0, result.getId()); // hoặc một thuộc tính mặc định
    }

    /**
     * TC039 - Mục tiêu: Kiểm tra khi truyền vào AdminDTO hợp lệ, lưu thành công và trả về true
     * Yêu cầu đầu vào:
     *  - AdminDTO có đầy đủ thông tin
     * Các bước thực hiện:
     *  - Tạo AdminDTO
     *  - Gọi hàm save(adminDTO)
     * Kết quả mong muốn:
     *  - Hàm trả về true
     */
    @Test
    @Order(39)
    @DisplayName("TC039 - save với AdminDTO hợp lệ")
    void TC039_save_ValidAdminDTO_ReturnsTrue() {
        // Arrange
        TypeAccount type = new TypeAccount();
        type.setName("Admin");
        typeAccountRepository.save(type);

        Account account = new Account();
        account.setUsername("test_admin");
        account.setPassword("123456");
        account.setEmail("test@admin.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("000000");
        account.setWallet(100.0);
        account.setCreated(new Date());
        accountRepository.save(account);

        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setAccountId(account.getId());
        adminDTO.setAccountName(account.getUsername());
        adminDTO.setFullname("Trần Thị Admin");
        adminDTO.setPhone("0912345678");
        adminDTO.setPhoto("admin.jpg");

        // Act
        boolean result = adminService.save(adminDTO);

        // Assert
        System.out.println("Kết quả thực tế (TC039): " + result);
        assertTrue(result);
    }
    /**
     * TC040 - Mục tiêu: Kiểm tra khi AdminDTO không có accountId (hoặc accountId không tồn tại), hàm trả về false
     * Các bước thực hiện:
     *  - Tạo AdminDTO không hợp lệ (accountId không tồn tại)
     *  - Gọi hàm save(AdminDTO)
     * Kết quả mong muốn:
     *  - Trả về false
     */
    @Test
    @Order(40)
    @DisplayName("TC040 - Lưu admin thất bại khi thiếu accountId")
    void TC040_save_AdminDTOWithoutAccountId_ReturnsFalse() {
        // Arrange
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setAccountId(9999); // ID không tồn tại
        adminDTO.setAccountName("ghost");
        adminDTO.setFullname("Không Có Tài Khoản");
        adminDTO.setPhone("0000000000");
        adminDTO.setPhoto("ghost.jpg");

        // Act
        boolean result = adminService.save(adminDTO);

        // Assert
        System.out.println("Kết quả thực tế (TC040): " + result);
        assertFalse(result);
    }



}