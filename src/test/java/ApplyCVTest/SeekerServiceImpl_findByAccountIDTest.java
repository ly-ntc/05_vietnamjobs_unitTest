package ApplyCVTest;

import com.demo.dtos.SeekerDTO;
import com.demo.entities.Seeker;
import com.demo.repositories.SeekerRepository;
import com.demo.services.SeekerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SeekerServiceImpl_findByAccountIDTest {

    @Mock
    private SeekerRepository seekerRepository;  // Mock SeekerRepository

    @Mock
    private ModelMapper modelMapper;  // Mock ModelMapper

    @InjectMocks
    private SeekerServiceImpl seekerService;  // Inject mocks into SeekerServiceImpl

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks before each test
    }

    // Test case khi tìm thấy Seeker
    @Test
    public void TC11_testFindByAccountID_Success() {
        // Dữ liệu giả lập
        int accountId = 1;
        Seeker seeker = new Seeker();
        seeker.setId(1);
        seeker.setFullname("Test Seeker");

        SeekerDTO expectedSeekerDTO = new SeekerDTO();
        expectedSeekerDTO.setId(1);
        expectedSeekerDTO.setFullName("Test Seeker");

        // Giả lập hành vi của seekerRepository trả về Seeker khi tìm thấy
        when(seekerRepository.findByAccountID(accountId)).thenReturn(seeker);

        // Giả lập hành vi của modelMapper
        when(modelMapper.map(seeker, SeekerDTO.class)).thenReturn(expectedSeekerDTO);

        // Gọi phương thức
        SeekerDTO result = seekerService.findByAccountID(accountId);

        // Kết quả mong muốn
        System.out.println("Test FindByAccountID Success:");
        System.out.println("Expected SeekerDTO: " + expectedSeekerDTO);
        System.out.println("Actual SeekerDTO: " + result);

        // Kiểm tra kết quả trả về
        assertNotNull(result);  // Kiểm tra không null
        assertEquals(expectedSeekerDTO.getId(), result.getId());  // Kiểm tra ID
        assertEquals(expectedSeekerDTO.getFullName(), result.getFullName());  // Kiểm tra tên
    }

    // Test case khi không tìm thấy Seeker
    @Test
    public void TC12_testFindByAccountID_NotFound() {
        int accountId = 1;

        // Giả lập hành vi của seekerRepository trả về null khi không tìm thấy seeker
        when(seekerRepository.findByAccountID(accountId)).thenReturn(null);

        // Gọi phương thức
        SeekerDTO result = seekerService.findByAccountID(accountId);

        // Kết quả mong muốn
        System.out.println("Test FindByAccountID NotFound:");
        System.out.println("Expected SeekerDTO: null");
        System.out.println("Actual SeekerDTO: " + result);

        // Kiểm tra kết quả trả về là null (kết quả mong muốn)
        assertNull(result);  // Kiểm tra kết quả là null
    }

    // Test case khi có lỗi xảy ra trong quá trình mapping
    @Test
    public void TC13_testFindByAccountID_MappingException() {
        int accountId = 1;
        Seeker seeker = new Seeker();
        seeker.setId(1);
        seeker.setFullname("Test Seeker");

        // Giả lập hành vi của seekerRepository trả về Seeker khi tìm thấy
        when(seekerRepository.findByAccountID(accountId)).thenReturn(seeker);

        // Giả lập hành vi của modelMapper gây ra exception khi map
        when(modelMapper.map(seeker, SeekerDTO.class)).thenThrow(new RuntimeException("Mapping error"));

        // Kết quả mong muốn
        String expectedMessage = "Mapping error";

        // Kiểm tra khi xảy ra ngoại lệ trong quá trình chuyển đổi
        try {
            seekerService.findByAccountID(accountId);
        } catch (RuntimeException e) {
            System.out.println("Test FindByAccountID MappingException:");
            System.out.println("Expected Message: " + expectedMessage);
            System.out.println("Actual Message: " + e.getMessage());  // In thông báo ngoại lệ thực tế
            assertEquals(expectedMessage, e.getMessage());  // Kiểm tra thông báo ngoại lệ
        }
    }
}
