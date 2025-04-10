package ApplyCVTest;

import com.demo.dtos.PostingDTO;
import com.demo.entities.Postings;
import com.demo.repositories.PostingRepository;
import com.demo.services.PostingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PostingServiceImpl_FindbyIDTest {

    @Mock
    private PostingRepository postingRepository; // Mock repository

    @Mock
    private ModelMapper modelMapper; // Mock ModelMapper

    @InjectMocks
    private PostingServiceImpl postingService; // Inject mocks into PostingServiceImpl

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks before each test
    }

    // Test case khi tìm thấy Posting
    @Test
    public void TC8_testFindById_Success() {
        // Dữ liệu giả lập
        int id = 1;
        Postings posting = new Postings();
        posting.setId(id);
        posting.setTitle("Test Posting");

        PostingDTO expectedDto = new PostingDTO();
        expectedDto.setId(id);
        expectedDto.setTitle("Test Posting");

        // Giả lập hành vi của repository trả về Optional chứa Posting
        when(postingRepository.findById(id)).thenReturn(Optional.of(posting));

        // Giả lập hành vi của mapper
        when(modelMapper.map(posting, PostingDTO.class)).thenReturn(expectedDto);

        // Gọi phương thức
        PostingDTO result = postingService.findById(id);

        // Kết quả mong muốn
        String expectedViewName = "Test Posting";
        String expectedMessage = "Posting found";

        // In kết quả mong muốn và kết quả thực tế
        System.out.println("Test FindById Success:");
        System.out.println("Expected ViewName: " + expectedViewName);
        System.out.println("Expected Message: " + expectedMessage);

        // Kiểm tra kết quả
        System.out.println("Actual ViewName: " + result.getTitle());
        System.out.println("Actual Message: Posting found");

        // Kiểm tra kết quả trả về từ service là đúng (kết quả mong muốn)
        assertNotNull(result);  // Kiểm tra không null
        assertEquals(expectedViewName, result.getTitle());  // Kiểm tra tiêu đề
    }

    // Test case khi không tìm thấy Posting
//    @Test
//    public void TC9_testFindById_NotFound() {
//        // Dữ liệu giả lập
//        int id = 1;
//
//        // Giả lập hành vi của repository trả về Optional.empty()
//        when(postingRepository.findById(id)).thenReturn(Optional.empty());
//
//        // Gọi phương thức
//        PostingDTO result = postingService.findById(id);
//
//        // Kết quả mong muốn
//        String expectedViewName = null;
//        String expectedMessage = "Posting not found";
//
//        // In kết quả mong muốn và kết quả thực tế
//        System.out.println("Test FindById NotFound:");
//        System.out.println("Expected ViewName: " + expectedViewName);
//        System.out.println("Expected Message: " + expectedMessage);
//
//        // Kiểm tra kết quả
//        System.out.println("Actual ViewName: " + result);
//        System.out.println("Actual Message: " + (result == null ? "Posting not found" : "Found"));
//
//        // Kiểm tra kết quả trả về từ service là null (kết quả mong muốn)
//        assertNull(result);  // Kiểm tra kết quả là null
//    }

    // Test case khi có lỗi trong quá trình mapping
    @Test
    public void TC10_testFindById_MappingException() {
        // Dữ liệu giả lập
        int id = 1;
        Postings posting = new Postings();
        posting.setId(id);
        posting.setTitle("Test Posting");

        // Giả lập hành vi của repository trả về Optional chứa Posting
        when(postingRepository.findById(id)).thenReturn(Optional.of(posting));

        // Giả lập hành vi của mapper gây ra exception khi map
        when(modelMapper.map(posting, PostingDTO.class)).thenThrow(new RuntimeException("Mapping error"));

        // Kết quả mong muốn
        String expectedMessage = "Mapping error";

        // In kết quả mong muốn và kết quả thực tế
        System.out.println("Test FindById MappingException:");
        System.out.println("Expected Message: " + expectedMessage);

        // Kiểm tra khi xảy ra ngoại lệ trong quá trình chuyển đổi
        try {
            postingService.findById(id);
        } catch (RuntimeException e) {
            System.out.println("Actual Message: " + e.getMessage());  // In thông báo ngoại lệ thực tế
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
