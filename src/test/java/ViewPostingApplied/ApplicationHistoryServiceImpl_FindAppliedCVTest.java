package ViewPostingApplied;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.entities.ApplicationHistory;
import com.demo.entities.Postings;
import com.demo.repositories.ApplicationHistoryRepository;
import com.demo.services.ApplicationHistoryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class ApplicationHistoryServiceImpl_FindAppliedCVTest {
    @Mock
    private ApplicationHistoryRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ApplicationHistoryServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void TC55_findAppliedCV_WithValidId_ReturnsDTOList() {
        // Arrange
        int seekerId = 1;
        Postings posting = new Postings();
        posting.setId(101); // Nếu Postings có hàm setId()

        // Mock dữ liệu entity
        ApplicationHistory history = new ApplicationHistory();
        history.setId(10);
        history.setPostings(posting);

        List<ApplicationHistory> entityList = List.of(history);

        // Mock dữ liệu DTO
        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setId(10);
        dto.setPostingID(101);

        List<ApplicationHistoryDTO> dtoList = List.of(dto);

        // Mock hành vi
        when(repository.findAppliedCV(seekerId)).thenReturn(entityList);
        when(modelMapper.map(eq(entityList), any(Type.class))).thenReturn(dtoList);

        // Act
        List<ApplicationHistoryDTO> result = service.findAppliedCV(seekerId);

        // Assert
        System.out.println("✅ Mong muốn: Kích thước = 1, PostingID = 101");
        System.out.println("📤 Thực tế: Kích thước = " + result.size() + ", PostingID = " + result.get(0).getPostingID());

        assertEquals(1, result.size());
        assertEquals(101, result.get(0).getPostingID());
    }

    /**
     * ✅ TC56
     * Mục tiêu: Trường hợp không có ApplicationHistory nào
     * Đầu vào: seekerId = 2, DB trả về list rỗng
     * Kết quả mong muốn: Trả về danh sách rỗng
     */
    @Test
    void TC56_findAppliedCV_WithEmptyList_ReturnsEmptyList() {
        int seekerId = 2;
        List<ApplicationHistory> emptyList = Collections.emptyList();
        List<ApplicationHistoryDTO> dtoList = Collections.emptyList();

        when(repository.findAppliedCV(seekerId)).thenReturn(emptyList);
        when(modelMapper.map(eq(emptyList), any(Type.class))).thenReturn(dtoList);

        List<ApplicationHistoryDTO> result = service.findAppliedCV(seekerId);

        System.out.println("✅ TC56 Mong muốn: Danh sách rỗng");
        System.out.println("📤 Thực tế: Kích thước = " + result.size());

        assertTrue(result.isEmpty());
    }

    /**
     * ✅ TC57
     * Mục tiêu: Kiểm tra khi ApplicationHistory có posting là null (bài đăng bị xoá)
     * Đầu vào: seekerId = 3, ApplicationHistory.postings = null
     * Kết quả mong muốn: Mapping vẫn thực hiện, không ném exception, và trả về danh sách đúng kích thước
     */
    @Test
    void TC57_findAppliedCV_WithNullPosting_NoException() {
        int seekerId = 3;

        ApplicationHistory history = new ApplicationHistory();
        history.setId(20);
        history.setPostings(null); // Mô phỏng bài đăng đã bị xoá

        List<ApplicationHistory> entityList = List.of(history);

        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setId(20);
        dto.setPostingID(0); // giả định DTO vẫn được tạo

        List<ApplicationHistoryDTO> dtoList = List.of(dto);

        when(repository.findAppliedCV(seekerId)).thenReturn(entityList);
        when(modelMapper.map(eq(entityList), any(Type.class))).thenReturn(dtoList);

        List<ApplicationHistoryDTO> result = service.findAppliedCV(seekerId);

        System.out.println("✅ TC57 Mong muốn: Không exception, Kích thước = 1, PostingID = 0");
        System.out.println("📤 Thực tế: Kích thước = " + result.size() + ", PostingID = " + result.get(0).getPostingID());

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getPostingID());
    }
}
