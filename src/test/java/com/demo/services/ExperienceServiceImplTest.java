package com.demo.services;

import com.demo.entities.Experience;
import com.demo.repositories.ApplicationHistoryRepository;
import com.demo.repositories.ExperienceRepository;
import com.demo.repositories.PostingRepository;
import com.demo.services.ExperienceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ExperienceServiceImplTest {

    @Autowired
    private ExperienceServiceImpl experienceService;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private PostingRepository postingRepository;

    @Autowired
    private ApplicationHistoryRepository applicationHistoryRepository;

    @Mock
    private ExperienceRepository experienceRepoMock;

    @InjectMocks
    private ExperienceServiceImpl experienceServiceMock;
    // TC001: Lưu thành công Experience hợp lệ
    @BeforeEach
    @Transactional
    void setUp() {
        applicationHistoryRepository.deleteAll();
        postingRepository.deleteAll();
        experienceRepository.deleteAll();
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @Transactional
    void TC001() {
        // Tạo đối tượng Experience mới
        Experience exp = new Experience();
        exp.setName("3 năm Java");
        exp.setStatus(true);

        // Lưu vào DB
        boolean result = experienceService.save(exp);

        // Kiểm tra kết quả
        assertTrue(result);

        // Kiểm tra dữ liệu đã được lưu vào DB
        Experience savedExperience = experienceRepository.findById(exp.getId()).orElse(null);
        assertNotNull(savedExperience);
        assertEquals("3 năm Java", savedExperience.getName());
        assertTrue(savedExperience.isStatus());
    }

    // TC002: Repository ném lỗi -> save trả về false

    @Test
    void TC002() {
        Experience exp = new Experience();
        exp.setName("Lỗi DB");
        exp.setStatus(false);

        // Sử dụng doThrow() để mock exception cho save
        doThrow(new RuntimeException("DB error")).when(experienceRepoMock).save(exp);

        boolean result = experienceServiceMock.save(exp);

        System.out.println("TC002 - Expected: false | Actual: " + result);
        assertFalse(result);
    }

    // TC003: Truyền null vào hàm save
    @Test
    @Transactional
    void TC003() {
        // Kiểm tra khi truyền null vào hàm save
        boolean result = experienceService.save(null);

        // Kiểm tra kết quả trả về
        System.out.println("TC003 - Expected: false | Actual: " + result);
        assertFalse(result);
    }

    // TC004: Kiểm tra khi name là null
    @Test
    @Transactional
    void TC004() {
        Experience exp = new Experience();
        exp.setName(null);
        exp.setStatus(true);

        boolean result = experienceService.save(exp);

        System.out.println("TC004 - Expected: false | Actual: " + result);
        assertFalse(result);
    }

    // TC005: Kiểm tra khi name là chuỗi rỗng
    @Test
    @Transactional
    void TC005() {
        Experience exp = new Experience();
        exp.setName("");
        exp.setStatus(true);

        boolean result = experienceService.save(exp);

        System.out.println("TC005 - Expected: false | Actual: " + result);
        assertFalse(result);
    }

    @Test
    @Transactional
    void TC006() {
        // Tạo và lưu một đối tượng để đảm bảo có ID hợp lệ
        Experience exp = new Experience();
        exp.setName("Xóa thành công");
        exp.setStatus(true);
        experienceService.save(exp);
        Integer savedId = exp.getId();

        // Gọi delete
        boolean result = experienceService.delete(savedId);

        System.out.println("TC007 - Expected: true | Actual: " + result);
        assertTrue(result);
        assertFalse(experienceRepository.findById(savedId).isPresent());
    }

    @Test
    @Transactional
    void TC007() {
        int invalidId = 999999; // ID chắc chắn không tồn tại trong DB

        boolean result = experienceService.delete(invalidId);

        System.out.println("TC008 - Expected: false | Actual: " + result);
        assertFalse(result);
    }

    @Test
    @Transactional
    void TC008() {
        // Chuyển ID về null để gây lỗi từ `findById(id).get()` (vì get() trên Optional.empty() sẽ ném NoSuchElementException)
        Integer invalidId = null;

        boolean result;
        try {
            result = experienceService.delete(invalidId);
        } catch (Exception e) {
            result = false;
        }

        System.out.println("TC009 - Expected: false | Actual: " + result);
        assertFalse(result);
    }

    // TC009: Tìm kinh nghiệm theo ID hợp lệ có tồn tại trong DB
    @Test
    @Transactional
    void TC009() {
        // Arrange - tạo bản ghi
        Experience exp = new Experience();
        exp.setName("Senior Java Dev");
        exp.setStatus(true);
        experienceRepository.save(exp);

        // Act - gọi hàm find()
        Experience result = experienceService.find(exp.getId());

        // Assert
        System.out.println("TC009 - Expected: Senior Java Dev | Actual: " + result.getName());
        assertNotNull(result);
        assertEquals("Senior Java Dev", result.getName());
        assertTrue(result.isStatus());
    }

    // TC010: Tìm kinh nghiệm theo ID không tồn tại
    @Test
    @Transactional
    void TC010() {
        int invalidId = 999999;

        System.out.println("TC010 - Expected: NoSuchElementException");

        assertThrows(NoSuchElementException.class, () -> {
            experienceService.find(invalidId);
        });
    }

    // TC011: Tìm kinh nghiệm theo ID âm
    @Test
    @Transactional
    void TC011() {
        int negativeId = -1;

        System.out.println("TC011 - Expected: NoSuchElementException");

        assertThrows(NoSuchElementException.class, () -> {
            experienceService.find(negativeId);
        });
    }

//     TC012: Kiểm tra tồn tại bản ghi theo tên và id
    @Test
    @Transactional
    void TC012() {
        // Arrange: Tạo 2 bản ghi trùng tên, khác id
        Experience e1 = new Experience("Java", true);
        experienceRepository.save(e1);

        Experience e2 = new Experience("Java", false);
        experienceRepository.save(e2);

        // Act: Kiểm tra phương thức exists
        boolean exists = experienceService.exists("Java", e1.getId());

        // Assert: Kết quả mong đợi là true vì có bản ghi trùng tên và id khác
        System.out.println("TC012 - Expected: true | Actual: " + exists);
        assertTrue(exists);
    }

//     TC013: Kiểm tra khi không có bản ghi trùng tên
    @Test
    @Transactional
    void TC013() {
        // Act: Kiểm tra phương thức exists với tên không tồn tại trong DB
        boolean exists = experienceService.exists("Không tồn tại", 1);

        // Assert: Kết quả mong đợi là false
        System.out.println("TC013 - Expected: false | Actual: " + exists);
        assertFalse(exists);
    }

//     TC014: Trả về danh sách có phần tử khi có dữ liệu
    @Test
    @Transactional
    void TC014() {
        // Arrange: tạo 2 bản ghi trong DB
        Experience exp1 = new Experience("Intern", true);
        Experience exp2 = new Experience("Senior", true);
        experienceRepository.save(exp1);
        experienceRepository.save(exp2);

        // Act
        Iterable<Experience> all = experienceService.findAll();

        // Assert
        List<Experience> list = new ArrayList<>();
        all.forEach(list::add);

        System.out.println("TC014 - Expected: >= 2 | Actual: " + list.size());
        assertTrue(list.size() >= 2);
    }

//     TC015: Trả về danh sách rỗng khi DB không có dữ liệu
    @Test
    @Transactional
    void TC015() {
        // Act
        Iterable<Experience> all = experienceService.findAll();

        // Assert
        List<Experience> list = new ArrayList<>();
        all.forEach(list::add);

        System.out.println("TC015 - Expected: 0 | Actual: " + list.size());
        assertEquals(0, list.size());
    }

    // TC016: Lấy danh sách có status = true
    @Test
    @Transactional
    void TC016() {
        postingRepository.deleteAll(); // Xóa tất cả postings
        experienceRepository.deleteAll(); // Bây giờ có thể xóa experience

        Experience exp1 = new Experience("Intern", true);
        Experience exp2 = new Experience("Senior", true);
        Experience exp3 = new Experience("Hidden", false);

        experienceRepository.saveAll(List.of(exp1, exp2, exp3));

        List<Experience> results = experienceService.findAllByStatus(true);

        System.out.println("TC016 - Expected: 2 | Actual: " + results.size());
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(Experience::isStatus));
    }

    // TC017: Không có bản ghi nào có status = false
    @Test
    @Transactional
    void TC017() {
        postingRepository.deleteAll(); // Xóa tất cả postings
        experienceRepository.deleteAll(); // Bây giờ có thể xóa experience

        Experience exp1 = new Experience("Intern", true);
        Experience exp2 = new Experience("Senior", true);
        experienceRepository.saveAll(List.of(exp1, exp2));

        List<Experience> results = experienceService.findAllByStatus(false);

        System.out.println("TC017 - Expected: 0 | Actual: " + results.size());
        assertEquals(0, results.size());
    }

}
