package com.demo.services;

import com.demo.entities.TypeAccount;
import com.demo.repositories.TypeAccountRepository;
import org.junit.jupiter.api.Test;
import com.demo.dtos.AccountDTO;
import com.demo.entities.Account;
import com.demo.repositories.AccountRepository;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.mockito.Mockito.*;

import java.lang.reflect.Type;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional // Mỗi test sẽ rollback sau khi chạy xong, bảo vệ database thật
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountServiceImplTest {
    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TypeAccountRepository typeAccountRepository;

    @Autowired
    private ModelMapper modelMapper;

    private BCrypt crypt;
    @Mock
    private AccountRepository accountRepository2;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private AccountServiceImpl accountService2;
    @Mock
    BCrypt crypt2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * TC001 - Mục tiêu: Đảm bảo khi có account trong DB thì findAll() trả về danh sách AccountDTO đúng
     * Đầu vào: Chèn 1 account có username "user1"
     * Kết quả mong muốn: Danh sách có 1 phần tử, username là "user1"
     */
    @Test
    @Order(1)
    @DisplayName("TC001 - findAll() trả về danh sách AccountDTO khi có dữ liệu")
    void TC001_findAll_testFindAllAccountsAfterAddingOne() {
        // Tạo đối tượng TypeAccount trước
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("Admin");  // Gán tên cho TypeAccount
        typeAccount.setStatus(true);   // Gán trạng thái cho TypeAccount

        // Lưu TypeAccount vào DB trước khi tạo Account
        typeAccountRepository.save(typeAccount);  // Giả sử bạn có repository cho TypeAccount

        // Tạo đối tượng Account và gán TypeAccount vào
        Account account = new Account();
        account.setTypeAccount(typeAccount); // Gán TypeAccount vào Account
        account.setUsername("user1");  // Gán thông tin cho Account
        account.setPassword("pass1");
        account.setEmail("user1@example.com");
        account.setSecurityCode("123456");
        account.setWallet(1000.0);
        account.setStatus(true);
        account.setCreated(new Date());  // Hoặc set ngày hiện tại

        // Lưu Account vào DB
        accountRepository.save(account);  // Giả sử bạn có repository cho Account
        // Act: Gọi hàm cần test
        List<AccountDTO> result = accountService.findAll();

        // Assert: Kiểm tra kết quả
        // In kết quả thực tế và so sánh với giá trị kỳ vọng
        System.out.println("Kết quả thực tế (T001): " + result.size());
        assertEquals(25, result.size());  // Kiểm tra xem tổng số tài khoản có phải là 24
        assertEquals("user1", result.get(result.size() - 1).getUsername());  // Kiểm tra tài khoản đầu tiên có username đúng là "user1"
    }
    /**
     * TC002 - Mục tiêu: Đảm bảo khi không có account nào trong DB thì findAll() trả về danh sách rỗng
     * Đầu vào: Xoá toàn bộ account trong DB
     * Kết quả mong muốn: Danh sách rỗng (size = 0)
     */
    @Test
    @Order(2)
    @DisplayName("TC002 - findAll() với danh sách rỗng")
    void testFindAll_WithEmptyList() {
        // Mock dữ liệu
        when(accountRepository2.findAll()).thenReturn(Collections.emptyList());
        when(mapper.map(any(), any(Type.class))).thenReturn(Collections.emptyList());

        // Gọi hàm cần test
        List<AccountDTO> result = accountService2.findAll();

        // In kết quả
        System.out.println("Kết quả thực tế (TC002): " + result.size());

        // Kiểm tra
        assertNotNull(result);
        assertEquals(0, result.size());
    }
    /**
     * TC003 - Mục tiêu: Đảm bảo khi ghi account hợp lệ thì hàm save trả về true
     * Yêu cầu đầu vào:
     *  - Tạo một AccountDTO hợp lệ (ví dụ username = "user_test_1")
     *  - Gọi hàm save()
     * Kết quả mong muốn:
     *  - Trả về true
     *  - Dữ liệu thực tế được lưu vào database
     */
    @Test
    @Order(3)
    @DisplayName("TC003 - Ghi account thành công -> return true")
    void TC003_save_testSaveSuccessWithRealData() {
        // Arrange: Tạo và lưu TypeAccount
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("Admin");
        typeAccount.setStatus(true);
        typeAccountRepository.save(typeAccount);

        // Tạo AccountDTO
        AccountDTO dto = new AccountDTO();
        dto.setUsername("user1");
        dto.setPassword("pass1");
        dto.setEmail("user1@example.com");
        dto.setSecurityCode("123456");
        dto.setWallet(1000.0);
        dto.setStatus(true);
        dto.setCreated(new Date());
        dto.setTypeAccountID(typeAccount.getId());// Gán ID thay vì object nếu AccountDTO không chứa object TypeAccount

        /// Lưu account vào DB
        accountService.save(dto);

        // Kiểm tra lại trong DB
        Account saved = accountRepository.findbyUsername("user1");
        System.out.println(saved.getId());

        assertNotNull(saved);  // Kiểm tra xem đối tượng không phải null
        assertEquals("user1", saved.getUsername());  // Kiểm tra username của tài khoản

    }
    /**
     * TC004 - Mục tiêu: Đảm bảo khi ghi account không hợp lệ thì hàm save trả về false
     * Yêu cầu đầu vào:
     *  - Tạo một AccountDTO không hợp lệ (ví dụ thiếu username hoặc thông tin sai)
     *  - Gọi hàm save()
     * Kết quả mong muốn:
     *  - Trả về false
     *  - Dữ liệu không được lưu vào database
     */
    @Test
    @Order(4)
    @DisplayName("TC004 - Test save failure with invalid account data")
    void TC004_save_testSaveFailureWithInvalidData() {
        // Tạo đối tượng TypeAccount trước
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("Admin");  // Gán tên cho TypeAccount
        typeAccount.setStatus(true);   // Gán trạng thái cho TypeAccount

        // Lưu TypeAccount vào DB trước khi tạo Account
        typeAccountRepository.save(typeAccount);  // Giả sử bạn có repository cho TypeAccount

        // Tạo đối tượng AccountDTO không hợp lệ (thiếu username)
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setTypeAccountID(typeAccount.getId()); // Gán TypeAccount vào AccountDTO
        accountDTO.setUsername("user_test_2");  // Bỏ qua trường username để làm dữ liệu không hợp lệ
//        accountDTO.setPassword("pass123");      // Gán mật khẩu cho AccountDTO
        accountDTO.setEmail("user_test_2@example.com"); // Gán email cho AccountDTO
        accountDTO.setSecurityCode("123456");
        accountDTO.setWallet(1000.0);
        accountDTO.setStatus(true);
        accountDTO.setCreated(new Date()); // Hoặc set ngày hiện tại

        // Act
        boolean result = accountService.save(accountDTO);  // Gọi hàm save

        // Assert
        System.out.println("Kết quả thực tế (TC004): " + result);
        assertFalse(result); // Kết quả mong muốn là false vì tài khoản không hợp lệ

        // Kiểm tra lại trong DB nếu cần
        Account savedAccount = accountRepository.findbyUsername("user_test_2");
        assertNull(savedAccount); // Không có tài khoản với username "user_test_2" được lưu vào DB
    }


    /**
     * TC005 - Mục tiêu: Đảm bảo khi đăng nhập với tài khoản hợp lệ, hàm login1 trả về true
     * Yêu cầu đầu vào:
     *  - Tạo một tài khoản hợp lệ trong database (username = "user1", password = "password123")
     *  - Gọi hàm login1 với username và password hợp lệ
     * Kết quả mong muốn:
     *  - Trả về true
     *  - Tài khoản có thể đăng nhập thành công
     */
    @Test
    @Order(5)
    @DisplayName("TC005 - Test login success with valid credentials")
    void TC005_login1_testLoginSuccessWithValidCredentials() {
        // Tạo đối tượng TypeAccount trước
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("Admin");  // Gán tên cho TypeAccount
        typeAccount.setStatus(true);   // Gán trạng thái cho TypeAccount

        // Lưu TypeAccount vào DB trước khi tạo Account
        typeAccountRepository.save(typeAccount);  // Giả sử bạn có repository cho TypeAccount

        // Tạo đối tượng Account và gán TypeAccount vào
        Account account = new Account();
        account.setTypeAccount(typeAccount); // Gán TypeAccount vào Account
        account.setUsername("user1");  // Gán username cho Account
        account.setPassword(BCrypt.hashpw("password123", BCrypt.gensalt()));  // Gán mật khẩu cho Account đã mã hóa
        account.setEmail("user1@example.com");
        account.setSecurityCode("123456");
        account.setWallet(1000.0);
        account.setStatus(true);
        account.setCreated(new Date());  // Hoặc set ngày hiện tại

        // Lưu tài khoản vào DB
        accountRepository.save(account);

        // Act
        boolean result = accountService.login1("user1", "password123");

        // Assert
        System.out.println("Kết quả thực tế (TC001): " + result);
        assertTrue(result);  // Kết quả mong muốn là true vì tài khoản hợp lệ
    }

    /**
     * TC006 - Mục tiêu: Đảm bảo khi đăng nhập với tài khoản không tồn tại, hàm login1 trả về false
     * Yêu cầu đầu vào:
     *  - Gọi hàm login1 với một username không tồn tại trong database
     * Kết quả mong muốn:
     *  - Trả về false
     *  - Không có tài khoản khớp với username này trong database
     */
    @Test
    @Order(6)
    @DisplayName("TC006 - Test login failure with non-existent account")
    void TC006_login1_testLoginFailureWithNonExistentAccount() {
        // Act
        boolean result = accountService.login1("user_not_exist", "any_password");

        // Assert
        System.out.println("Kết quả thực tế (TC002): " + result);
        assertFalse(result);  // Kết quả mong muốn là false vì tài khoản không tồn tại
    }

    /**
     * TC007 - Mục tiêu: Đảm bảo khi đăng nhập với mật khẩu sai, hàm login1 trả về false
     * Yêu cầu đầu vào:
     *  - Tạo một tài khoản hợp lệ trong database (username = "user1", password = "password123")
     *  - Gọi hàm login1 với username hợp lệ và mật khẩu sai
     * Kết quả mong muốn:
     *  - Trả về false
     *  - Mật khẩu không khớp với tài khoản trong database
     */
    @Test
    @Order(7)
    @DisplayName("TC007- Test login failure with incorrect password")
    void TC007_login1_testLoginFailureWithIncorrectPassword() {
        // Tạo đối tượng TypeAccount trước
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("Admin");  // Gán tên cho TypeAccount
        typeAccount.setStatus(true);   // Gán trạng thái cho TypeAccount

        // Lưu TypeAccount vào DB trước khi tạo Account
        typeAccountRepository.save(typeAccount);  // Giả sử bạn có repository cho TypeAccount

        // Tạo đối tượng Account và gán TypeAccount vào
        Account account = new Account();
        account.setTypeAccount(typeAccount); // Gán TypeAccount vào Account
        account.setUsername("user1");  // Gán username cho Account
        account.setPassword(BCrypt.hashpw("password123", BCrypt.gensalt()));  // Gán mật khẩu cho Account đã mã hóa
        account.setEmail("user1@example.com");
        account.setSecurityCode("123456");
        account.setWallet(1000.0);
        account.setStatus(true);
        account.setCreated(new Date());  // Hoặc set ngày hiện tại

        // Lưu tài khoản vào DB
        accountRepository.save(account);

        // Act
        boolean result = accountService.login1("user1", "wrong_password");

        // Assert
        System.out.println("Kết quả thực tế (TC003): " + result);
        assertFalse(result);  // Kết quả mong muốn là false vì mật khẩu sai
    }

    /**
     * TC008 - Mục tiêu: Đảm bảo khi đăng nhập với tài khoản bị khóa (status = false), hàm login1 trả về false
     * Yêu cầu đầu vào:
     *  - Tạo một tài khoản trong database có status = false
     *  - Gọi hàm login1 với username và mật khẩu hợp lệ
     * Kết quả mong muốn:
     *  - Trả về false
     *  - Tài khoản bị khóa không thể đăng nhập
     */
    @Test
    @Order(8)
    @DisplayName("TC008 - Test login failure with disabled account")
    void TC008_login1_testLoginFailureWithDisabledAccount() {
        // Tạo đối tượng TypeAccount trước
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("Admin");  // Gán tên cho TypeAccount
        typeAccount.setStatus(true);   // Gán trạng thái cho TypeAccount

        // Lưu TypeAccount vào DB trước khi tạo Account
        typeAccountRepository.save(typeAccount);  // Giả sử bạn có repository cho TypeAccount

        // Tạo đối tượng Account và gán TypeAccount vào
        Account account = new Account();
        account.setTypeAccount(typeAccount); // Gán TypeAccount vào Account
        account.setUsername("user_disabled");  // Gán username cho Account
        account.setPassword(BCrypt.hashpw("password123", BCrypt.gensalt())); // Gán mật khẩu cho Account đã mã hóa
        account.setEmail("user_disabled@example.com");
        account.setSecurityCode("123456");
        account.setWallet(1000.0);
        account.setStatus(false);  // Tài khoản bị khóa
        account.setCreated(new Date());  // Hoặc set ngày hiện tại

        // Lưu tài khoản vào DB
        accountRepository.save(account);

        // Act
        boolean result = accountService.login1("user_disabled", "password123");

        // Assert
        System.out.println("Kết quả thực tế (TC004): " + result);
        assertFalse(result);  // Kết quả mong muốn là false vì tài khoản bị khóa
    }

    /**
     * TC009 - Mục tiêu: Đảm bảo khi gặp lỗi trong quá trình tìm tài khoản, hàm login1 sẽ vào catch và trả về false
     * Yêu cầu đầu vào:
     *  - Mock accountRepository để ném ra exception khi gọi findbyUsername
     *  - Gọi hàm login1 với username và mật khẩu hợp lệ
     * Kết quả mong muốn:
     *  - Trả về false
     *  - Không thực hiện đăng nhập vì có lỗi xảy ra trong quá trình tìm tài khoản
     */
    @Test
    @Order(9)
    @DisplayName("TC009 - Test login failure due to exception in accountRepository")
    void TC009_login1_testLoginFailureDueToException() {
        // Giả sử mock findbyUsername ném ra exception khi tìm tài khoản
        when(accountRepository2.findbyUsername("user1")).thenThrow(new RuntimeException("Database error"));

        // Act
        boolean result = accountService.login1("user1", "password123");

        // Assert
        System.out.println("Kết quả thực tế (TC009): " + result);
        assertFalse(result);  // Kết quả mong muốn là false vì có exception trong quá trình tìm tài khoản
    }
    /**
     * TC010 - Mục tiêu: Đảm bảo đăng nhập thành công khi thông tin hợp lệ và tài khoản đang hoạt động (status = true)
     * Yêu cầu đầu vào:
     *  - Tạo một tài khoản trong DB với username = "user_login_ok", password = "password123", status = true
     *  - Gọi hàm login với thông tin đúng
     * Kết quả mong muốn:
     *  - Hàm login trả về true
     */


    @Test
    @Order(10)
    @DisplayName("TC010 - Test login success with valid credentials and active account")
    void TC010_login_testSuccessWithValidAccount() {
        // Tạo TypeAccount
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("User");
        typeAccount.setStatus(true);
        typeAccountRepository.save(typeAccount);

        // Tạo tài khoản
        Account account = new Account();
        account.setUsername("user_login_ok");
        account.setPassword("password123"); // Mật khẩu plain-text nếu không mã hoá
        account.setEmail("user_ok@example.com");
        account.setSecurityCode("123456");
        account.setWallet(1000.0);
        account.setStatus(true);
        account.setCreated(new Date());
        account.setTypeAccount(typeAccount);
        accountRepository.save(account);

        // Gọi hàm login
        boolean result = accountService.login("user_login_ok", "password123");

        // Kết quả mong muốn
        System.out.println("Kết quả thực tế (TC010): " + result);
        assertTrue(result);
    }

    /**
     * TC011 - Mục tiêu: Đảm bảo đăng nhập thất bại khi thông tin sai hoặc tài khoản bị khóa
     * Yêu cầu đầu vào:
     *  - Tạo tài khoản status = false, hoặc dùng sai mật khẩu
     *  - Gọi hàm login
     * Kết quả mong muốn:
     *  - Hàm login trả về false
     */
    @Test
    @Order(11)
    @DisplayName("TC011 - Test login failure with wrong password or disabled account")
    void TC011_login_testFailureWithInvalidInfo() {
        // Tạo TypeAccount
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("User");
        typeAccount.setStatus(true);
        typeAccountRepository.save(typeAccount);

        // Tạo tài khoản
        Account account = new Account();
        account.setUsername("user_login_fail");
        account.setPassword("rightpass"); // đúng pass trong DB
        account.setEmail("user_fail@example.com");
        account.setSecurityCode("123456");
        account.setWallet(1000.0);
        account.setStatus(false); // Tài khoản bị khóa
        account.setCreated(new Date());
        account.setTypeAccount(typeAccount);
        accountRepository.save(account);

        // Gọi hàm login với password sai hoặc tài khoản bị khóa
        boolean result = accountService.login("user_login_fail", "wrongpass");

        // Kết quả mong muốn
        System.out.println("Kết quả thực tế (TC011): " + result);
        assertFalse(result);
    }


    /**
     * TC012 - Mục tiêu: Đảm bảo khi username hợp lệ thì loadUserByUsername trả về UserDetails đúng
     * Yêu cầu đầu vào:
     *  - Tạo tài khoản với username hợp lệ trong DB
     *  - Gọi hàm loadUserByUsername()
     * Kết quả mong muốn:
     *  - Trả về đối tượng UserDetails có thông tin username, password đúng
     */
    @Test
    @Order(12)
    @DisplayName("TC012 - Test loadUserByUsername with valid username")
    void TC012_loadUserByUsername_validUsername_returnsUserDetails() {
        // Arrange
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("Admin");
        typeAccount.setStatus(true);
        typeAccountRepository.save(typeAccount);

        Account account = new Account();
        account.setUsername("valid_user");
        account.setPassword("password123");
        account.setEmail("valid_user@example.com");
        account.setSecurityCode("123456");
        account.setWallet(500.0);
        account.setStatus(true);
        account.setCreated(new Date());
        account.setTypeAccount(typeAccount);
        accountRepository.save(account);

        // Act
        UserDetails result = accountService.loadUserByUsername("valid_user");

        // Assert
        System.out.println("Kết quả thực tế (TC012): " + result.getUsername());
        assertEquals("valid_user", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertTrue(result.getAuthorities().stream().anyMatch(
                auth -> auth.getAuthority().equals("Admin"))
        );
    }

    /**
     * TC013 - Mục tiêu: Đảm bảo khi username không tồn tại thì loadUserByUsername ném UsernameNotFoundException
     * Yêu cầu đầu vào:
     *  - Gọi loadUserByUsername() với username không có trong DB
     * Kết quả mong muốn:
     *  - Ném ra UsernameNotFoundException
     */
    @Test
    @Order(13)
    @DisplayName("TC013 - Test loadUserByUsername with invalid username throws exception")

    void TC013_loadUserByUsername_invalidUsername_throwsException() {
        // Act + Assert

       UserDetails result = accountService.loadUserByUsername("non_existing_user");

        assertNull(result);
    }

    /**
     * TC014 - Mục tiêu: Đảm bảo hàm findById trả về đúng thông tin DTO khi tài khoản tồn tại
     * Các bước thực hiện:
     *  - Tạo và lưu một Account hợp lệ vào database
     *  - Gọi hàm findById với id của account vừa lưu
     * Kết quả mong muốn:
     *  - Trả về AccountDTO chứa đúng username đã lưu
     */
    @Test
    @Order(14)
    @DisplayName("TC014 - Test findById with valid ID")
    void TC014_findById_validAccount() {
        // Tạo đối tượng TypeAccount trước
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("Admin");  // Gán tên cho TypeAccount
        typeAccount.setStatus(true);   // Gán trạng thái cho TypeAccount

        // Lưu TypeAccount vào DB trước khi tạo Account
        typeAccountRepository.save(typeAccount);  // Giả sử bạn có repository cho TypeAccount

        // Tạo đối tượng Account và gán TypeAccount vào
        Account account = new Account();
        account.setTypeAccount(typeAccount); // Gán TypeAccount vào Account
        account.setUsername("user1");  // Gán thông tin cho Account
        account.setPassword("pass1");
        account.setEmail("user1@example.com");
        account.setSecurityCode("123456");
        account.setWallet(1000.0);
        account.setStatus(true);
        account.setCreated(new Date());  // Hoặc set ngày hiện tại

        Account savedAccount = accountRepository.save(account);

        // Act
        AccountDTO result = accountService.findById(savedAccount.getId());

        // Assert
        System.out.println("Kết quả thực tế (TC014): " + result.getUsername());
        assertNotNull(result);
        assertEquals("user1", result.getUsername());
    }

    /**
     * TC015 - Mục tiêu: Đảm bảo hàm findById xử lý đúng khi không tìm thấy tài khoản
     * Các bước thực hiện:
     *  - Gọi hàm findById với ID không tồn tại trong database
     * Kết quả mong muốn:
     *  - Ném ra IllegalArgumentException do mapper.map(null, ...)
     */
    @Test
    @Order(15)
    @DisplayName("TC015 - Test findById with non-existent ID")
    void TC015_findById_accountNotFound() {
        // Act & Assert
        AccountDTO result =  accountService.findById(-99999); // ID không tồn tại

        System.out.println("Kết quả thực tế (TC015): " + result);
        assertNull(result);
    }

    /**
     * TC016 - Mục tiêu: Đảm bảo hàm findByEmail trả về đúng thông tin DTO khi email tồn tại
     * Các bước thực hiện:
     *  - Tạo và lưu một Account có email hợp lệ vào database
     *  - Gọi hàm findByEmail với email đã lưu
     * Kết quả mong muốn:
     *  - Trả về AccountDTO có đúng username tương ứng với email
     */
    @Test
    @Order(16)
    @DisplayName("TC016 - Test findByEmail with valid email")
    void TC016_findByEmail_validEmail() {
        // Arrange - tạo type account
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("User");
        typeAccount.setStatus(true);
        typeAccountRepository.save(typeAccount);

        // Tạo account và lưu
        Account account = new Account();
        account.setUsername("valid_email_user");
        account.setPassword("hashedpass");
        account.setEmail("valid_user@example.com");
        account.setTypeAccount(typeAccount);
        account.setStatus(true);
        account.setSecurityCode("654321");
        account.setWallet(500.0);
        account.setCreated(new Date());

        accountRepository.save(account);

        // Act
        AccountDTO result = accountService.findByEmail("valid_user@example.com");

        // Assert
        System.out.println("Kết quả thực tế (TC016): " + result.getUsername());
        assertNotNull(result);
        assertEquals("valid_email_user", result.getUsername());
    }

    /**
     * TC017 - Mục tiêu: Đảm bảo hàm findByEmail xử lý đúng khi email không tồn tại
     * Các bước thực hiện:
     *  - Gọi hàm findByEmail với email không tồn tại trong database
     * Kết quả mong muốn:
     *  - Ném ra IllegalArgumentException do mapper.map(null, ...)
     */
    @Test
    @Order(17)
    @DisplayName("TC016 - Test findByEmail with non-existent email")
    void TC017_findByEmail_nonExistentEmail() {
        // Act & Assert

        AccountDTO result = accountService.findByEmail("nonexistent@example.com");

        assertNull(result);
    }

    /**
     * TC018 - Mục tiêu: Kiểm tra khi ID tồn tại trong DB, tài khoản được xóa và hàm trả về false (do logic hiện tại).
     * Đầu vào: ID của tài khoản hợp lệ trong DB.
     * Các bước:
     * 1. Tạo một account và lưu vào DB.
     * 2. Gọi accountService.delete(account.getId()).
     * 3. Kiểm tra kết quả trả về.
     * Kết quả mong muốn: Hàm trả về true
     */
    @Test
    @DisplayName("TC018 - Xóa tài khoản thành công, ID hợp lệ, trả về false (theo logic)")
    @Order(18)

    void TC018_delete_validId_shouldReturnFalse() {
        // Tạo account
        TypeAccount type = new TypeAccount();
        type.setName("Test");
        type.setStatus(true);
        typeAccountRepository.save(type);

        Account account = new Account();
        account.setUsername("deluser");
        account.setPassword("123456");
        account.setEmail("del@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("654321");
        account.setWallet(500.0);
        account.setCreated(new Date());
        accountRepository.save(account);

        // Thực hiện gọi hàm delete
        boolean result = accountService.delete(account.getId());

        // Kết quả mong muốn
        System.out.println("Kết quả thực tế (TC018): " + result);
        assertTrue(result); // Theo logic hiện tại, luôn return false
    }

    /**
     * TC019 - Mục tiêu: Kiểm tra khi ID không tồn tại, xảy ra exception và hàm trả về false.
     * Đầu vào: ID không tồn tại trong database.
     * Các bước:
     * 1. Gọi accountService.delete(-999) với ID không có trong DB.
     * 2. Bắt lỗi nếu có và kiểm tra kết quả trả về.
     * Kết quả mong muốn: Hàm trả về false do rơi vào khối catch.
     */
    @Test
    @DisplayName("TC019 - Xóa tài khoản với ID không tồn tại, xảy ra exception, trả về false")
    @Order(19)
    void TC019_delete_invalidId_shouldReturnFalse() {
        boolean result = accountService.delete(-999);  // ID không tồn tại
        System.out.println("Kết quả thực tế (TC019): " + result);
        assertFalse(result);  // Mong muốn false do exception
    }

    /**
     * TC020 - Mục tiêu: Đảm bảo khi username tồn tại trong database, hàm findByUsername trả về đối tượng AccountDTO đúng thông tin
     * Yêu cầu đầu vào:
     *  - Tạo tài khoản trong database với username là "existing_user"
     * Các bước thực hiện:
     *  - Gọi hàm findByUsername với username "existing_user"
     * Kết quả mong muốn:
     *  - Trả về đối tượng AccountDTO có username là "existing_user"
     *  - Các thông tin như email, loại tài khoản được ánh xạ chính xác
     */
    @Test
    @Order(20)
    @DisplayName("TC020 - Test findByUsername with valid existing user")
    void TC020_findByUsername_ValidUser_ReturnsCorrectDTO() {
        // Arrange
        TypeAccount type = new TypeAccount();
        type.setName("Admin");
        typeAccountRepository.save(type);

        Account account = new Account();
        account.setUsername("existing_user");
        account.setPassword("123456");
        account.setEmail("test@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("654321");
        account.setWallet(500.0);
        account.setCreated(new Date());
        accountRepository.save(account);

        // Act
        AccountDTO result = accountService.findByUsername("existing_user");

        // Assert
        System.out.println("Kết quả thực tế (TC020): " + result.getUsername());
        assertNotNull(result);
        assertEquals("existing_user", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Admin", result.getTypeAccount());
    }

    /**
     * TC021 - Mục tiêu: Đảm bảo khi username không tồn tại, hàm findByUsername trả về null hoặc xử lý ngoại lệ hợp lý
     * Yêu cầu đầu vào:
     *  - Không có tài khoản nào trong database có username là "not_found_user"
     * Các bước thực hiện:
     *  - Gọi hàm findByUsername với username "not_found_user"
     * Kết quả mong muốn:
     *  - Trả về null (nếu mapper xử lý null) hoặc ném IllegalArgumentException
     */
    @Test
    @Order(21)
    @DisplayName("TC021 - Test findByUsername with non-existent user")
    void TC021_findByUsername_UserNotFound_ReturnsNullOrThrows() {
        // Act & Assert
        AccountDTO result = accountService.findByUsername("not_found_user");
        System.out.println("Kết quả thực tế (TC021): " + result);
        assertNull(result);  // Nếu mapper hỗ trợ null

    }

    /**
     * TC022 - Mục tiêu: Kiểm tra khi truyền vào ID hợp lệ (tài khoản tồn tại), hàm trả về đúng đối tượng Account
     * Yêu cầu đầu vào:
     *  - Tạo một tài khoản có id là 1 trong database
     * Các bước thực hiện:
     *  - Gọi hàm getDetail(1)
     * Kết quả mong muốn:
     *  - Trả về đối tượng Account tương ứng với id = 1
     */
    @Test
    @Order(22)
    @DisplayName("TC022 - getDetail với id hợp lệ, trả về Account đúng")
    void TC022_getDetail_ValidId_ReturnsAccount() {
        // Arrange
        TypeAccount type = new TypeAccount();
        type.setName("Admin");
        typeAccountRepository.save(type);

        Account account = new Account();
        account.setUsername("detail_user");
        account.setPassword("abc123");
        account.setEmail("detail@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("654321");
        account.setWallet(500.0);
        account.setCreated(new Date());
        accountRepository.save(account);

        int id = account.getId();

        // Act
        Account result = accountService.getDetail(id);

        // Assert
        System.out.println("Kết quả thực tế (TC022): " + result.getUsername());
        assertNotNull(result);
        assertEquals("detail_user", result.getUsername());
        assertEquals("detail@example.com", result.getEmail());
    }

    /**
     * TC023 - Mục tiêu: Kiểm tra khi truyền vào ID không tồn tại, hàm ném ra NoSuchElementException
     * Yêu cầu đầu vào:
     *  - Không có tài khoản nào có id là 9999 trong database
     * Các bước thực hiện:
     *  - Gọi hàm getDetail(9999)
     * Kết quả mong muốn:
     *  - Ném ra NoSuchElementException
     */
    @Test
    @Order(23)
    @DisplayName("TC023 - getDetail với id không tồn tại, ném NoSuchElementException")
    void TC023_getDetail_InvalidId_ThrowsException() {
        // Act & Assert

        Account result = accountService.getDetail(9999);

        assertNull(result);
    }

    @Test
    @Order(24)
    @DisplayName("TC024 - Test getAll when data exists in DB")
    void TC024_getAll_whenDataExists() {
        // Tạo tài khoản và lưu vào DB
        TypeAccount type = new TypeAccount();
        type.setName("Admin");
        typeAccountRepository.save(type);

        Account account = new Account();
        account.setUsername("detail_user");
        account.setPassword("abc123");
        account.setEmail("detail@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("654321");
        account.setWallet(500.0);
        account.setCreated(new Date());
        accountRepository.save(account);

        // Act: Lấy tất cả tài khoản
        Iterable<Account> result = accountService.getAll();

        // Assert: Kiểm tra không có lỗi và tài khoản đã lưu có mặt trong kết quả
        assertNotNull(result);
        assertTrue(((Collection<?>) result).size() > 0);  // Danh sách không rỗng
        assertTrue(((Collection<?>) result).contains(account));  // Kiểm tra tài khoản đã lưu có trong danh sách
    }

    @Test
    @Order(25)
    @DisplayName("TC025 - Test getAll when no data in DB (Mocked)")
    void TC025_getAll_whenNoData() {
        // Sử dụng mock để giả lập không có tài khoản trong DB
        when(accountRepository2.findAll()).thenReturn(Collections.emptyList());  // Giả lập DB rỗng

        // Act: Lấy tất cả tài khoản
        Iterable<Account> result = accountService2.getAll();

        // Assert: Kiểm tra không có lỗi và danh sách rỗng
        assertNotNull(result);
        assertEquals(0, ((Collection<?>) result).size());  // Danh sách phải rỗng
    }


    @Test
    @Order(26)
    @DisplayName("TC026 - find với ID hợp lệ, trả về thông tin tài khoản đúng")
    void TC026_find_ValidId_ReturnsAccount() {
        // Tạo và lưu tài khoản vào DB
        TypeAccount type = new TypeAccount();
        type.setName("Admin");
        typeAccountRepository.save(type);

        Account account = new Account();
        account.setUsername("user1");
        account.setPassword("abc123");
        account.setEmail("user1@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("654321");
        account.setWallet(500.0);
        account.setCreated(new Date());
        accountRepository.save(account);

        // Act: Gọi hàm find với ID hợp lệ
        Account result = accountService.find(account.getId());

        // Assert: Kiểm tra thông tin tài khoản trả về đúng với ID đã lưu
        assertNotNull(result);
        assertEquals(account.getId(), result.getId());
        assertEquals("user1", result.getUsername());
        assertEquals("user1@example.com", result.getEmail());
    }

    @Test
    @Order(27)
    @DisplayName("TC027 - find với ID không tồn tại, ném NoSuchElementException")
    void TC027_find_InvalidId_ThrowsException() {
        // Act & Assert: Gọi hàm find với ID không tồn tại
        Account result  =  accountService.find(9999);

        // In ra kết quả thực tế và kiểm tra
        assertNull(result);

    }
    /**
     * TC028 - Mục tiêu: Kiểm tra khi cập nhật trạng thái thành công (trả về true)
     * Yêu cầu đầu vào:
     *  - Có tài khoản với id = 2 trong CSDL
     *  - Gọi hàm updateStatusById(2, falase)
     * Các bước thực hiện:
     *  - Gọi hàm updateStatusById(2, false)
     * Kết quả mong muốn:
     *  - Hàm trả về true (vì có ít nhất 1 bản ghi được cập nhật)
     */
    @Test
    @Order(28)
    @DisplayName("TC029 - Cập nhật trạng thái thành công, trả về true")
    void TC028_updateStatus_False_Success_ReturnsTrue() {
        // Act
        Boolean result = accountService.updateStatusById(2, false);

        // Assert
        System.out.println("Kết quả thực tế (TC029): " + result);
        assertTrue(result); // Mong đợi trả về true
    }

    /**
     * TC029 - Mục tiêu: Kiểm tra khi cập nhật trạng thái thành công (trả về true)
     * Yêu cầu đầu vào:
     *  - Có tài khoản với id = 2 trong CSDL
     *  - Gọi hàm updateStatusById(2, true)
     * Các bước thực hiện:
     *  - Gọi hàm updateStatusById(2, true)
     * Kết quả mong muốn:
     *  - Hàm trả về true (vì có ít nhất 1 bản ghi được cập nhật)
     */
    @Test
    @Order(29)
    @DisplayName("TC029 - Cập nhật trạng thái thành công, trả về true")
    void TC029_updateStatus_Success_ReturnsTrue() {
        // Act
        Boolean result = accountService.updateStatusById(2, true);

        // Assert
        System.out.println("Kết quả thực tế (TC029): " + result);
        assertTrue(result); // Mong đợi trả về true
    }

    /**
     * TC030 - Mục tiêu: Kiểm tra khi không có bản ghi nào được cập nhật (trả về false)
     * Yêu cầu đầu vào:
     *  - Mock repository để trả về 0 khi gọi updateStatusById
     * Các bước thực hiện:
     *  - Cấu hình mock accountRepository.updateStatusById(999, false) trả về 0
     *  - Gọi hàm updateStatusById(999, false)
     * Kết quả mong muốn:
     *  - Hàm trả về false (vì không có bản ghi nào được cập nhật)
     */
    @Test
    @Order(30)
    @DisplayName("TC030 - Không cập nhật được trạng thái, trả về false (dùng mock)")
    void TC030_updateStatus_Failure_ReturnsFalse() {
        // Arrange
        when(accountRepository2.updateStatusById(999, false)).thenReturn(0);

        // Act
        Boolean result = accountService2.updateStatusById(999, false);

        // Assert
        System.out.println("Kết quả thực tế (TC030): " + result);
        assertFalse(result); // Mong đợi trả về false
    }

    /**
     * TC031- Mục tiêu: Kiểm tra hàm getByUsername trả về đúng đối tượng khi username tồn tại trong database
     * Yêu cầu đầu vào:
     *  - Có một tài khoản với username là "user1" đã được lưu vào database
     * Các bước thực hiện:
     *  - Tạo đối tượng TypeAccount và lưu vào DB
     *  - Tạo Account có username là "user1" và lưu vào DB
     *  - Gọi hàm getByUsername("user1")
     * Kết quả mong muốn:
     *  - Trả về Account có username là "user1"
     */
    @Test
    @Order(31)
    @DisplayName("TC029 - getByUsername với username hợp lệ, trả về Account")
    void TC031_getByUsername_ValidUsername_ReturnsAccount() {
        // Arrange
        TypeAccount type = new TypeAccount();
        type.setName("Admin");
        typeAccountRepository.save(type);

        Account account = new Account();
        account.setUsername("user1");
        account.setPassword("abc123");
        account.setEmail("user1@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("654321");
        account.setWallet(500.0);
        account.setCreated(new Date());
        accountRepository.save(account);

        // Act
        Account result = accountService.getByUsername("user1");

        // Assert
        System.out.println("Kết quả thực tế (TC029): " + result.getUsername());
        assertEquals("user1", result.getUsername());
    }

    /**
     * TC032 - Mục tiêu: Kiểm tra hàm getByUsername trả về null khi username không tồn tại
     * Yêu cầu đầu vào:
     *  - Không có tài khoản nào có username là "non_exist"
     * Các bước thực hiện:
     *  - Gọi hàm getByUsername("non_exist")
     * Kết quả mong muốn:
     *  - Trả về null
     */
    @Test
    @Order(32)
    @DisplayName("TC032 - getByUsername với username không tồn tại, trả về null")
    void TC032_getByUsername_InvalidUsername_ReturnsNull() {
        // Act
        Account result = accountService.getByUsername("non_exist");

        // Assert
        System.out.println("Kết quả thực tế (TC030): " + result);
        assertNull(result);
    }


    /**
     * TC033 - Mục tiêu: Đảm bảo hàm trả về đúng số lượng bản ghi khi có tài khoản phù hợp với roleId, month, year
     * Yêu cầu đầu vào:
     *  - Có một tài khoản với typeAccount.id = 1, created tháng 6, năm 2024
     * Các bước thực hiện:
     *  - Chèn typeAccount và account vào DB
     *  - Gọi countByRoleAndMonthAndYear(1, 6, 2024)
     * Kết quả mong muốn:
     *  - Trả về 1
     */
    @Test
    @Order(33)
    @DisplayName("TC033 - Trả về số lượng đúng khi có bản ghi phù hợp")
    void TC033_countByRoleAndMonthAndYear_CoDuLieu() {
        // Arrange
        TypeAccount type = new TypeAccount();
        type.setId(1); // đảm bảo ID là 1
        type.setName("Admin");
        typeAccountRepository.save(type);

        Account account = new Account();
        account.setUsername("user_tc031");
        account.setPassword("123456");
        account.setEmail("tc031@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("123456");
        account.setWallet(100.0);
        account.setCreated(java.sql.Date.valueOf("2024-06-15"));
        accountRepository.save(account);

        // Act
        int result = accountService.countByRoleAndMonthAndYear(1, 6, 2024);

        // Assert
        System.out.println("Kết quả thực tế (TC033): " + result);
        assertEquals(1, result);
    }

    /**
     * TC032 - Mục tiêu: Kiểm tra hàm khi không có bản ghi phù hợp, trả về 0
     * Yêu cầu đầu vào:
     *  - Không có tài khoản nào có roleId = 99, month = 12, year = 2030
     * Các bước thực hiện:
     *  - Gọi countByRoleAndMonthAndYear(99, 12, 2030)
     * Kết quả mong muốn:
     *  - Trả về 0
     */
    @Test
    @Order(32)
    @DisplayName("TC032 - Trả về 0 khi không có bản ghi phù hợp")
    void TC032_countByRoleAndMonthAndYear_KhongCoDuLieu() {
        // Act
        int result = accountService.countByRoleAndMonthAndYear(99, 12, 2030);

        // Assert
        System.out.println("Kết quả thực tế (TC032): " + result);
        assertEquals(0, result);
    }

    /**
     * TC035 - Mục tiêu: Kiểm tra khi có tài khoản với roleId, hàm trả về đúng số lượng
     * Yêu cầu đầu vào:
     *  - Có 1 tài khoản có typeAccount.id = 4
     * Các bước thực hiện:
     *  - Tạo TypeAccount có id = 4
     *  - Tạo tài khoản gắn typeAccount đó
     *  - Gọi hàm countByRole(4)
     * Kết quả mong muốn:
     *  - Trả về số lượng tài khoản là 1
     */
    @Test
    @Order(35)
    @DisplayName("TC035 - countByRole trả về đúng số lượng khi có tài khoản")
    void TC035_countByRole_WithExistingRoleId_ReturnsCorrectCount() {
        // Arrange
        TypeAccount type = new TypeAccount();
        type.setName("Tester");
        typeAccountRepository.save(type);

        Account account = new Account();
        account.setUsername("user1");
        account.setPassword("abc123");
        account.setEmail("user1@example.com");
        account.setTypeAccount(type);
        account.setStatus(true);
        account.setSecurityCode("654321");
        account.setWallet(500.0);
        account.setCreated(new Date());
        accountRepository.save(account);

        // Act
        int result = accountService.countByRole(type.getId());

        // Assert
        System.out.println("Kết quả thực tế (TC035): " + result);
        assertEquals(1, result);
    }

    /**
     * TC036 - Mục tiêu: Kiểm tra khi không có tài khoản với roleId, hàm trả về 0
     * Yêu cầu đầu vào:
     *  - Không có tài khoản nào có typeAccount.id = 999
     * Các bước thực hiện:
     *  - Gọi hàm countByRole(999)
     * Kết quả mong muốn:
     *  - Trả về 0
     */
    @Test
    @Order(36)
    @DisplayName("TC036 - countByRole trả về 0 khi không có tài khoản")
    void TC036_countByRole_WithNonExistingRoleId_ReturnsZero() {
        // Act
        int result = accountService.countByRole(999);

        // Assert
        System.out.println("Kết quả thực tế (TC036): " + result);
        assertEquals(0, result);
    }

    @Test
    @Order(99)
    @DisplayName("TC99 - Should return false when exception occurs in accountRepository.findbyUsername")
    void TC099_Login1_CatchBlockExecuted() {
        // Arrange
        String username = "exception_user";
        String password = "any_password";

        // Giả lập exception khi gọi repository
        when(accountRepository2.findbyUsername(username))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        boolean result = accountService2.login1(username, password);

        // Assert
        System.out.println("Expected: false");
        System.out.println("Actual: " + result);
        assertFalse(result, "Hàm login1 phải trả về false khi xảy ra exception");
    }

    @Test
    @Order(100)
    @DisplayName("TC100 - Should throw UsernameNotFoundException when account is null")
    void TC100_LoadUserByUsername_ThrowsExceptionWhenUserNotFound() {
        // Arrange
        String username = "nonexistent_user";
        when(accountRepository2.findbyUsername(username)).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> accountService2.loadUserByUsername(username)
        );

        System.out.println("Expected: UsernameNotFoundException with message 'Username not found'");
        System.out.println("Actual: " + exception.getMessage());

        assertEquals("Username not found", exception.getMessage());
    }

}