package ApplyCVTest;

import com.demo.entities.Account;
import com.demo.entities.Seeker;
import com.demo.entities.TypeAccount;
import com.demo.repositories.SeekerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SeekerRepository_findByAccountIDTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SeekerRepository seekerRepository;

    @Test
    void TC14_testFindByAccountID_ReturnsSeeker() {
        // Tạo TypeAccount vì Account yêu cầu không null
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("Seeker");
        entityManager.persist(typeAccount);

        // Khởi tạo đầy đủ Account
        Account account = new Account();
        account.setUsername("testuser");
        account.setPassword("password");
        account.setTypeAccount(typeAccount);
        account.setCreated(new Date());
        account.setEmail("test@example.com");
        account.setStatus(true);
        account.setSecurityCode("ABC123");
        account.setWallet(500.0);
        entityManager.persist(account);

        // Tạo Seeker gắn với Account
        Seeker seeker = new Seeker();
        seeker.setFullname("Nguyễn Văn A");
        seeker.setAccount(account);
        entityManager.persist(seeker);

        entityManager.flush(); // Đẩy dữ liệu vào DB để test

        // Gọi phương thức cần test
        Seeker found = seekerRepository.findByAccountID(account.getId());

        // === LOG kết quả ===
        System.out.println("EXPECTED fullname: Nguyễn Văn A");
        System.out.println("ACTUAL fullname  : " + found.getFullname());
        System.out.println("EXPECTED account ID: " + account.getId());
        System.out.println("ACTUAL account ID  : " + found.getAccount().getId());

        // Kiểm tra kết quả
        assertNotNull(found);
        assertEquals("Nguyễn Văn A", found.getFullname());
        assertEquals(account.getId(), found.getAccount().getId());
    }

    @Test
    void TC15_testApplyCV_AccountNotFound_ReturnsNullSeeker() {
        // Gọi repository với ID không tồn tại
        Long nonExistentAccountId = 999L;

        Seeker found = seekerRepository.findByAccountID(Math.toIntExact(nonExistentAccountId));

        // === LOG ===
        System.out.println("EXPECTED seeker: null");
        System.out.println("ACTUAL seeker  : " + found);

        // === ASSERT ===
        assertEquals(null, found);
    }

    @Test
    void TC16_testFindByAccountID_AccountExistsButNoSeeker_ReturnsNull() {
        // Tạo TypeAccount
        TypeAccount typeAccount = new TypeAccount();
        typeAccount.setName("Seeker");
        entityManager.persist(typeAccount);

        // Tạo Account nhưng không gắn với Seeker
        Account account = new Account();
        account.setUsername("lonelyAccount");
        account.setPassword("nopass");
        account.setTypeAccount(typeAccount);
        account.setCreated(new Date());
        account.setEmail("lonely@example.com");
        account.setStatus(true);
        account.setSecurityCode("NOSEEKER123");
        account.setWallet(0.0);
        entityManager.persist(account);

        entityManager.flush();

        // Gọi repository
        Seeker found = seekerRepository.findByAccountID(account.getId());

        // === LOG ===
        System.out.println("EXPECTED seeker: null (Account exists, but no Seeker)");
        System.out.println("ACTUAL seeker  : " + found);

        // === ASSERT ===
        assertEquals(null, found);
    }

    @Test
    void TC17_testFindByAccountID_WithNegativeId_ReturnsNull() {
        Integer invalidId = -1;

        // Gọi repository với ID âm
        Seeker found = seekerRepository.findByAccountID(invalidId);

        // === LOG ===
        System.out.println("EXPECTED seeker: null (ID âm không hợp lệ)");
        System.out.println("ACTUAL seeker  : " + found);

        // === ASSERT ===
        assertEquals(null, found);
    }


}
