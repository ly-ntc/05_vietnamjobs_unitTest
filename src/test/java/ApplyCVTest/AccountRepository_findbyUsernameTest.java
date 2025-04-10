package ApplyCVTest;
import com.demo.entities.Account;
import com.demo.entities.TypeAccount;
import com.demo.repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepository_findbyUsernameTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    // ✅ Test 1: Tìm kiếm tài khoản với username hợp lệ
    @Test
    void testFindbyUsername_WithValidUsername_ReturnsAccount() {
        // Tạo một TypeAccount để gắn vào Account
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("Seeker");
        entityManager.persist(typeAccount);

        // Khởi tạo đầy đủ các trường của Account
        Account account = new Account();
        account.setUsername("validuser");                            // 🟢 Tên đăng nhập đúng với đầu vào test
        account.setPassword("password123");                          // Mật khẩu
        account.setCreated(new Date());                              // Ngày tạo
        account.setEmail("validuser@mail.com");                      // Email
        account.setStatus(true);                                     // Trạng thái tài khoản
        account.setSecurityCode("ABC123");                           // Mã bảo mật
        account.setWallet(500.0);                                    // Số dư ví
        account.setTypeAccount(typeAccount);                         // Loại tài khoản

        // Lưu dữ liệu vào database test
        entityManager.persist(account);
        entityManager.flush();

        // Gọi phương thức cần test
        Account found = accountRepository.findbyUsername("validuser");

        // In ra để kiểm chứng
        System.out.println("✅ Mong muốn: username = validuser");
        System.out.println("📤 Thực tế: username = " + (found != null ? found.getUsername() : "null"));

        // Kiểm tra kết quả
        assertNotNull(found);                                 // Đảm bảo có account trả về
        assertEquals("validuser", found.getUsername());       // So sánh chính xác username
    }


    // ✅ Test 2: Tìm kiếm tài khoản với username không tồn tại
    @Test
    void testFindbyUsername_WithNonExistentUsername_ReturnsNull() {
        Account found = accountRepository.findbyUsername("ghost");

        System.out.println("✅ Mong muốn: null");
        System.out.println("📤 Thực tế: " + found);

        assertNull(found);
    }

    // ✅ Test 3: Tìm kiếm tài khoản với giá trị null
    @Test
    void testFindbyUsername_WithNullUsername_ReturnsNull() {
        Account found = accountRepository.findbyUsername(null);

        System.out.println("✅ Mong muốn: null");
        System.out.println("📤 Thực tế: " + found);

        assertNull(found);
    }

    // ✅ Test 4: Tìm kiếm tài khoản với chuỗi rỗng
    @Test
    void testFindbyUsername_WithEmptyUsername_ReturnsNull() {
        Account found = accountRepository.findbyUsername("");

        System.out.println("✅ Mong muốn: null");
        System.out.println("📤 Thực tế: " + found);

        assertNull(found);
    }
}