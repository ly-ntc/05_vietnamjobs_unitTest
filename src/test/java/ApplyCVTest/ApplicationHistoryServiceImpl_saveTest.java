package ApplyCVTest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.entities.ApplicationHistory;
import com.demo.entities.Postings;
import com.demo.entities.Seeker;
import com.demo.repositories.ApplicationHistoryRepository;
import com.demo.services.ApplicationHistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.util.Date;

class ApplicationHistoryServiceImpl_saveTest {

    @Mock
    private ApplicationHistoryRepository applicationHistoryRepository;  // Mock repository

    @Mock
    private ModelMapper modelMapper;  // Mock ModelMapper

    @InjectMocks
    private ApplicationHistoryServiceImpl applicationHistoryService;  // Service cần test

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Khởi tạo mocks trước mỗi test
    }

    @Test
    void TC6_testSave_Success() {
        // Tạo dữ liệu DTO giả lập
        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setPostingID(1);
        dto.setSeekerID(2);
        dto.setStatus(0);
        dto.setResult(0);

        // Tạo dữ liệu entity giả lập
        ApplicationHistory applicationHistory = new ApplicationHistory();
        applicationHistory.setPostings(new Postings());
        applicationHistory.setSeeker(new Seeker());
        applicationHistory.setCreated(new Date());

        // Giả lập hành vi của ModelMapper
        when(modelMapper.map(dto, ApplicationHistory.class)).thenReturn(applicationHistory);

        // Giả lập hành vi của repository (lưu thành công)
        when(applicationHistoryRepository.save(any(ApplicationHistory.class))).thenReturn(applicationHistory);

        // Kiểm tra lưu thành công
        boolean result = applicationHistoryService.save(dto);

        // Kết quả mong muốn
        boolean expected = true;

        // In kết quả mong muốn và kết quả thực tế
        System.out.println("Test Save Success:");
        System.out.println("Mong muốn: " + expected);
        System.out.println("Kết quả thực tế: " + result);

        // Kiểm tra kết quả trả về từ service
        assertTrue(result);  // Kiểm tra kết quả trả về từ service là true (lưu thành công)

        // Kiểm tra phương thức save của repository được gọi 1 lần với đúng đối tượng
        verify(applicationHistoryRepository, times(1)).save(any(ApplicationHistory.class));
    }

    @Test
    void TC7_testSave_Failure() {
        // Tạo dữ liệu DTO giả lập
        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setPostingID(1);
        dto.setSeekerID(2);
        dto.setStatus(0);
        dto.setResult(0);

        // Giả lập hành vi của ModelMapper
        ApplicationHistory applicationHistory = new ApplicationHistory();
        applicationHistory.setPostings(new Postings());
        applicationHistory.setSeeker(new Seeker());
        applicationHistory.setCreated(new Date());

        // Giả lập ModelMapper chuyển đổi DTO thành entity
        when(modelMapper.map(dto, ApplicationHistory.class)).thenReturn(applicationHistory);

        // Giả lập exception khi lưu vào repository
        when(applicationHistoryRepository.save(any(ApplicationHistory.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Kiểm tra khi có lỗi xảy ra
        boolean result = applicationHistoryService.save(dto);

        // Kết quả mong muốn
        boolean expected = false;

        // In kết quả mong muốn và kết quả thực tế
        System.out.println("Test Save Failure:");
        System.out.println("Mong muốn: " + expected);
        System.out.println("Kết quả thực tế: " + result);

        // Kiểm tra kết quả trả về từ service là false (lưu thất bại)
        assertFalse(result);  // Kiểm tra kết quả trả về từ service là false (lưu thất bại)

        // Kiểm tra phương thức save của repository được gọi 1 lần
        verify(applicationHistoryRepository, times(1)).save(any(ApplicationHistory.class));

        // Kiểm tra xem exception thực sự được ném ra (nếu cần kiểm tra cụ thể exception)
        try {
            applicationHistoryService.save(dto);
        } catch (RuntimeException e) {
            // Kiểm tra xem exception đã được ném ra và có thông báo phù hợp
            assertEquals("Database error", e.getMessage());
        }
    }


}
