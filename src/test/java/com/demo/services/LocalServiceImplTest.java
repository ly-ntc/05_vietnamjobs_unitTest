package com.demo.services;
import static org.mockito.ArgumentMatchers.any;

import com.demo.entities.Local;
import com.demo.repositories.LocalRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class LocalServiceImplTest {

    @Autowired
    private LocalServiceImpl localServiceImpl;

    @Autowired
    private LocalRepository localRepository;



    @Mock
    private LocalRepository mockLocalRepo;


    @InjectMocks
    private LocalServiceImpl localServiceImplMock;


    /**
     * TC036 - Lưu thành công một đối tượng Local hợp lệ
     */
    @Test
    void TC036() {
        Local local = new Local("Hà Nội", true);

        boolean result = localServiceImpl.save(local);
        System.out.println("TC036 - Expected: true | Actual: " + result);

        assertTrue(result);


    }

    /**
     * TC037 - Ném exception trong quá trình save (mock lỗi)
     */
    @Test
    void TC037() {
        // Tạo instance sử dụng mock repo
        LocalServiceImpl serviceWithMock = new LocalServiceImpl();
        ReflectionTestUtils.setField(serviceWithMock, "localRepository", mockLocalRepo);

        Local local = new Local("Hà Nội", true);

        // Giả lập lỗi khi save
        Mockito.doThrow(new RuntimeException("DB error")).when(mockLocalRepo).save(any(Local.class));

        boolean result = serviceWithMock.save(local);
        System.out.println("TC037 - Expected: false | Actual: " + result);

        assertFalse(result);
    }

    /**
     * TC038 - Lưu thất bại do vi phạm ràng buộc name=null
     */
    @Test
    void TC038() {
        Local local = new Local(null, true); // Vi phạm @Column(nullable = false)

        boolean result = localServiceImpl.save(local);
        System.out.println("TC038 - Expected: false | Actual: " + result);

        assertFalse(result);
        assertNull(local.getId());
    }

    @Test
    void TC039() {
        // Setup: tạo và lưu Local hợp lệ
        Local local = new Local("Đà Nẵng", true);
        local = localRepository.save(local); // Lưu và nhận lại đối tượng đã có ID

        Integer id = local.getId(); // Lấy ID do JPA gán sau khi lưu
        System.out.println("TC039 - ID vừa lưu: " + id);

        boolean result = localServiceImpl.delete(id);
        System.out.println("TC039 - Expected: true | Actual: " + result);

        assertTrue(result);
        assertFalse(localRepository.findById(id).isPresent());
    }


    /**
     * TC040 - Xoá thất bại vì không tìm thấy ID
     */
    @Test
    void TC040() {
        int invalidId = 999999;

        boolean result = localServiceImpl.delete(invalidId);
        System.out.println("TC040 - Expected: false | Actual: " + result);

        assertFalse(result);
    }

    /**
     * TC041 - Xoá thất bại vì ID âm
     */
    @Test
    void TC041() {
        int negativeId = -1;

        boolean result = localServiceImpl.delete(negativeId);
        System.out.println("TC041 - Expected: false | Actual: " + result);

        assertFalse(result);
    }

    @Test
    void TC042() {
        String name = "Hà Nội";
        int id = 1; // ID is arbitrary, we focus on name

        // Insert a local with the name "Hà Nội"
        Local local = new Local(name, true);
        localRepository.save(local); // Save to DB

        // Ensure the name already exists in the database
        boolean result = localServiceImpl.exists(name, id);

        System.out.println("TC042 - Expected: true | Actual: " + result);

        assertTrue(result);

        // Clean up (optional): delete the local record if needed
        localRepository.delete(local);
    }

    /**
     * TC043 - Kiểm tra tên địa điểm không trùng
     */
    @Test
    void TC043() {
        String name = "Hồ Chí Minh";
        int id = 1; // ID is arbitrary, we focus on name

        // Ensure the name does not exist in the database
        boolean result = localServiceImpl.exists(name, id);

        System.out.println("TC043 - Expected: false | Actual: " + result);

        assertFalse(result);
    }

    /**
     * TC044 - Tìm thành công khi ID tồn tại trong database
     * Giải thích: Thêm bản ghi Local, sau đó truy xuất bằng ID vừa lưu.
     */
    @Test
    void TC044() {
        Local saved = localRepository.save(new Local("Huế", true));
        Integer id = saved.getId();

        Local result = localServiceImpl.find(id);
        System.out.println("TC044 - Expected name: Huế | Actual: " + result.getName());

        assertNotNull(result);
        assertEquals("Huế", result.getName());
    }

    /**
     * TC045 - Gây lỗi khi ID không tồn tại trong database
     * Giải thích: Truy xuất ID không tồn tại, kỳ vọng ném ra NoSuchElementException.
     */
    @Test
    void TC045() {
        int nonExistentId = 999999;

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            localServiceImpl.find(nonExistentId);
        });

        System.out.println("TC045 - Expected: NoSuchElementException | Actual: " + exception.getClass().getSimpleName());
    }

    /**
     * TC046 - Gây lỗi khi truyền ID âm (không hợp lệ logic)
     * Giải thích: ID âm không hợp lệ, nên cũng kỳ vọng ném ra NoSuchElementException hoặc lỗi tương tự.
     */
    @Test
    void TC046() {
        int invalidId = -1;

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            localServiceImpl.find(invalidId);
        });

        System.out.println("TC046 - Expected: NoSuchElementException | Actual: " + exception.getClass().getSimpleName());
    }

    /**
     * TC047 - Trả về danh sách chứa phần tử khi DB có bản ghi
     * Giải thích: thêm bản ghi trước, sau đó gọi findAll và kiểm tra danh sách có phần tử.
     */
    @Test
    void TC047() {
        // Setup: đảm bảo có ít nhất 1 bản ghi
        Local local1 = new Local("Hải Phòng", true);
        localRepository.save(local1);

        Iterable<Local> result = localServiceImpl.findAll();
        List<Local> resultList = new ArrayList<>();
        result.forEach(resultList::add);

        System.out.println("TC047 - Expected size > 0 | Actual size: " + resultList.size());
        assertFalse(resultList.isEmpty());
    }

    /**
     * TC048 - Trả về danh sách rỗng khi DB không có bản ghi
     * Giải thích: xoá tất cả bản ghi trước khi gọi findAll.
     */
    @Test
    void TC048() {
        // Setup: Xoá hết dữ liệu
        localRepository.deleteAll();

        Iterable<Local> result = localServiceImpl.findAll();
        List<Local> resultList = new ArrayList<>();
        result.forEach(resultList::add);

        System.out.println("TC048 - Expected size = 0 | Actual size: " + resultList.size());
        assertTrue(resultList.isEmpty());
    }

    /**
     * TC049 - Trả về danh sách có status = true
     * Giải thích: thêm dữ liệu có status=true, gọi findAllByStatus(true) và kiểm tra kết quả.
     */
    @Test
    void TC049() {
        Local local1 = new Local("Đà Nẵng", true);
        Local local2 = new Local("Hà Nội", true);
        localRepository.save(local1);
        localRepository.save(local2);

        List<Local> result = localServiceImpl.findAllByStatus(true);

        System.out.println("TC049 - Expected non-empty list | Actual size: " + result.size());
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(Local::isStatus));
    }

    /**
     * TC050 - Trả về danh sách có status = false
     * Giải thích: thêm dữ liệu có status=false, gọi findAllByStatus(false) và kiểm tra kết quả.
     */
    @Test
    void TC050() {
        Local local1 = new Local("Huế", false);
        localRepository.save(local1);

        List<Local> result = localServiceImpl.findAllByStatus(false);

        System.out.println("TC050 - Expected non-empty list | Actual size: " + result.size());
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(l -> !l.isStatus()));
    }

    /**
     * TC051 - Không có bản ghi nào phù hợp
     * Giải thích: xoá hết các bản ghi có status=false, rồi gọi findAllByStatus(false)
     */
    @Test
    void TC051() {
        // Dọn sạch status=false để đảm bảo không có dữ liệu
        List<Local> allFalse = localRepository.findAllByStatus(false);
        localRepository.deleteAll(allFalse);

        List<Local> result = localServiceImpl.findAllByStatus(false);

        System.out.println("TC051 - Expected empty list | Actual size: " + result.size());
        assertTrue(result.isEmpty());
    }
}
