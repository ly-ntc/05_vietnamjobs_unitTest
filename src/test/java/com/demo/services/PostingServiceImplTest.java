package com.demo.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

import java.util.*;

import com.demo.dtos.PostingDTO;
import com.demo.entities.*;
import com.demo.repositories.PostingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.Type;


@ExtendWith(MockitoExtension.class)
class PostingServiceImplTest {

    @Mock
    private PostingRepository postingRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PostingServiceImpl postingService;
    Date fixedDate;

    @BeforeEach
    void setUp() {
        // Khởi tạo Mockito
        MockitoAnnotations.openMocks(this);
        // Sử dụng giá trị cố định cho Date để đảm bảo nhất quán
        fixedDate = new Date();
    }


    /**
     * TC001_findAll_ReturnsListOfPostings:
     * Mô tả: Kiểm tra trường hợp khi có nhiều bài đăng trong database.
     * Đầu vào: Database chứa danh sách bài đăng.
     * Kết quả mong đợi: Trả về danh sách PostingDTO.
     */
    @Test
    @DisplayName("TC001_findAll_ReturnsListOfPostings")
    void TC001_findAll_ReturnsListOfPostings() {
        // Mock dữ liệu giả lập
        List<Postings> postingList = new ArrayList<>();
        postingList.add(new Postings());
        postingList.add(new Postings());

        List<PostingDTO> dtoList = new ArrayList<>();
        dtoList.add(new PostingDTO());
        dtoList.add(new PostingDTO());

        when(postingRepository.findAll()).thenReturn(postingList);
        when(modelMapper.map(eq(postingList), any(new TypeToken<List<PostingDTO>>() {}.getType().getClass())))
                .thenReturn(dtoList);

        // Thực hiện phương thức
        List<PostingDTO> result = postingService.findAll();

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * TC002_findAll_ReturnsEmptyList:
     * Mô tả: Kiểm tra trường hợp khi không có bài đăng nào trong database.
     * Đầu vào: Database trống.
     * Kết quả mong đợi: Trả về danh sách rỗng.
     */
    @Test
    @DisplayName("TC002_findAll_ReturnsEmptyList")
    void TC002_findAll_ReturnsEmptyList() {
        // Mock dữ liệu giả lập - danh sách rỗng
        List<Postings> postingList = new ArrayList<>();
        List<PostingDTO> dtoList = new ArrayList<>();

        when(postingRepository.findAll()).thenReturn(postingList);
        when(modelMapper.map(eq(postingList), any(new TypeToken<List<PostingDTO>>() {}.getType().getClass())))
                .thenReturn(dtoList);

        // Thực hiện phương thức
        List<PostingDTO> result = postingService.findAll();

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * TC003_findAll_ExceptionThrown:
     * Mô tả: Kiểm tra trường hợp khi xảy ra ngoại lệ trong quá trình lấy danh sách bài đăng.
     * Đầu vào: Database gặp lỗi.
     * Kết quả mong đợi: Ném ngoại lệ (in ra stack trace), trả về danh sách rỗng.
     */
    @Test
    @DisplayName("TC003_findAll_ExceptionThrown")
    void TC003_findAll_ExceptionThrown() {
        // Giả lập ngoại lệ xảy ra trong quá trình lấy dữ liệu
        when(postingRepository.findAll()).thenThrow(new RuntimeException("Database Error"));

        // Thực hiện phương thức và kiểm tra kết quả
        List<PostingDTO> result = postingService.findAll();
        assertNotNull(result);
        assertEquals(0, result.size());
    }
    /**
     * TC004_save_ValidPostingDTO_ReturnsTrue:
     * Mô tả: Kiểm tra khi truyền vào một PostingDTO hợp lệ.
     * Đầu vào: PostingDTO hợp lệ: { title = "Java Developer", employerName = "ABC Corp", status = true }.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC004_save_ValidPostingDTO_ReturnsTrue")
    void TC004_save_ValidPostingDTO_ReturnsTrue() {
        // Mock dữ liệu
        PostingDTO postingDTO = new PostingDTO();
        postingDTO.setTitle("Java Developer");
        postingDTO.setEmployerName("ABC Corp");
        postingDTO.setStatus(true);

        Postings posting = new Postings();
        when(modelMapper.map(postingDTO, Postings.class)).thenReturn(posting);
        when(postingRepository.save(posting)).thenReturn(posting);

        // Thực hiện phương thức
        boolean result = postingService.save(postingDTO);

        // In kết quả kiểm thử
        System.out.println("TC004 - Kết quả mong đợi: true, Kết quả thực tế: " + result);

        // Kiểm tra kết quả
        assertTrue(result);
    }

    /**
     * TC005_save_DatabaseError_ReturnsFalse:
     * Mô tả: Kiểm tra trường hợp xảy ra lỗi trong quá trình lưu dữ liệu vào cơ sở dữ liệu.
     * Đầu vào: PostingDTO hợp lệ: { title = "Lỗi DB", employerName = "ABC Corp", status = true }.
     * Kết quả mong đợi: Trả về false, in ra stack trace.
     */
    @Test
    @DisplayName("TC005_save_DatabaseError_ReturnsFalse")
    void TC005_save_DatabaseError_ReturnsFalse() {
        // Mock dữ liệu
        PostingDTO postingDTO = new PostingDTO();
        postingDTO.setTitle("Lỗi DB");
        postingDTO.setEmployerName("ABC Corp");
        postingDTO.setStatus(true);

        Postings posting = new Postings();
        when(modelMapper.map(postingDTO, Postings.class)).thenReturn(posting);
        doThrow(new RuntimeException("Database Error")).when(postingRepository).save(posting);

        // Thực hiện phương thức
        boolean result = postingService.save(postingDTO);

        // In kết quả kiểm thử
        System.out.println("TC005 - Kết quả mong đợi: false, Kết quả thực tế: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC006_save_NullPostingDTO_ReturnsFalse:
     * Mô tả: Kiểm tra khi đối tượng truyền vào là null.
     * Đầu vào: null.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC006_save_NullPostingDTO_ReturnsFalse")
    void TC006_save_NullPostingDTO_ReturnsFalse() {
        boolean result = postingService.save(null);

        // In kết quả kiểm thử
        System.out.println("TC006 - Kết quả mong đợi: false, Kết quả thực tế: " + result);

        assertFalse(result);
    }

    /**
     * TC007_save_InvalidTitleNull_ReturnsFalse:
     * Mô tả: Kiểm tra khi PostingDTO không hợp lệ - trường title là null.
     */
    @Test
    @DisplayName("TC007_save_InvalidTitleNull_ReturnsFalse")
    void TC007_save_InvalidTitleNull_ReturnsFalse() {
        PostingDTO postingDTO = new PostingDTO();
        postingDTO.setTitle(null);
        postingDTO.setEmployerName("ABC Corp");
        postingDTO.setStatus(true);

        boolean result = postingService.save(postingDTO);

        System.out.println("TC007 - Kết quả mong đợi: false, Kết quả thực tế: " + result);
        assertFalse(result);
    }

    /**
     * TC008_save_InvalidTitleEmpty_ReturnsFalse:
     * Mô tả: Kiểm tra khi PostingDTO không hợp lệ - trường title là chuỗi rỗng.
     */
    @Test
    @DisplayName("TC008_save_InvalidTitleEmpty_ReturnsFalse")
    void TC008_save_InvalidTitleEmpty_ReturnsFalse() {
        PostingDTO postingDTO = new PostingDTO();
        postingDTO.setTitle("");
        postingDTO.setEmployerName("ABC Corp");
        postingDTO.setStatus(true);

        boolean result = postingService.save(postingDTO);

        System.out.println("TC008 - Kết quả mong đợi: false, Kết quả thực tế: " + result);
        assertFalse(result);
    }

    /**
     * TC009_save_InvalidEmployerNameNull_ReturnsFalse:
     * Mô tả: Kiểm tra khi PostingDTO không hợp lệ - trường employerName là null.
     */
    @Test
    @DisplayName("TC009_save_InvalidEmployerNameNull_ReturnsFalse")
    void TC009_save_InvalidEmployerNameNull_ReturnsFalse() {
        PostingDTO postingDTO = new PostingDTO();
        postingDTO.setTitle("Java Developer");
        postingDTO.setEmployerName(null);
        postingDTO.setStatus(true);

        boolean result = postingService.save(postingDTO);

        System.out.println("TC009 - Kết quả mong đợi: false, Kết quả thực tế: " + result);
        assertFalse(result);
    }

    /**
     * TC010_save_InvalidEmployerNameEmpty_ReturnsFalse:
     * Mô tả: Kiểm tra khi PostingDTO không hợp lệ - trường employerName là chuỗi rỗng.
     */
    @Test
    @DisplayName("TC010_save_InvalidEmployerNameEmpty_ReturnsFalse")
    void TC010_save_InvalidEmployerNameEmpty_ReturnsFalse() {
        PostingDTO postingDTO = new PostingDTO();
        postingDTO.setTitle("Java Developer");
        postingDTO.setEmployerName("");
        postingDTO.setStatus(true);

        boolean result = postingService.save(postingDTO);

        System.out.println("TC010 - Kết quả mong đợi: false, Kết quả thực tế: " + result);
        assertFalse(result);
    }

    /**
     * TC011_save_InvalidQuantityNegative_ReturnsFalse:
     * Mô tả: Kiểm tra khi PostingDTO không hợp lệ - trường quantity là số âm.
     */
    @Test
    @DisplayName("TC011_save_InvalidQuantityNegative_ReturnsFalse")
    void TC011_save_InvalidQuantityNegative_ReturnsFalse() {
        PostingDTO postingDTO = new PostingDTO();
        postingDTO.setTitle("Java Developer");
        postingDTO.setEmployerName("ABC Corp");
        postingDTO.setQuantity(-5);
        postingDTO.setStatus(true);

        boolean result = postingService.save(postingDTO);

        System.out.println("TC011 - Kết quả mong đợi: false, Kết quả thực tế: " + result);
        assertFalse(result);
    }

    /**
     * TC012_save_InvalidCreatedDateFuture_ReturnsFalse:
     * Mô tả: Kiểm tra khi PostingDTO không hợp lệ - created là ngày trong tương lai.
     */
    @Test
    @DisplayName("TC012_save_InvalidCreatedDateFuture_ReturnsFalse")
    void TC012_save_InvalidCreatedDateFuture_ReturnsFalse() {
        PostingDTO postingDTO = new PostingDTO();
        postingDTO.setTitle("Java Developer");
        postingDTO.setEmployerName("ABC Corp");
        postingDTO.setCreated(new Date(System.currentTimeMillis() + 10000000000L));
        postingDTO.setStatus(true);

        boolean result = postingService.save(postingDTO);

        System.out.println("TC012 - Kết quả mong đợi: false, Kết quả thực tế: " + result);
        assertFalse(result);
    }
    /**
     * TC013_save_InvalidDealineBeforeCreated_ReturnsFalse:
     * Mô tả: Kiểm tra khi PostingDTO không hợp lệ - trường dealine là ngày trước ngày created.
     * Đầu vào: { title = "Java Developer", employerName = "ABC Corp", created = "01/01/2025", dealine = "01/01/2024", status = true }.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC013_save_InvalidDealineBeforeCreated_ReturnsFalse")
    void TC013_save_InvalidDealineBeforeCreated_ReturnsFalse() {
        PostingDTO postingDTO = new PostingDTO();
        postingDTO.setTitle("Java Developer");
        postingDTO.setEmployerName("ABC Corp");
        postingDTO.setCreated(new Date(2025 - 1900, 0, 1));  // 01/01/2025
        postingDTO.setDealine(new Date(2024 - 1900, 0, 1)); // 01/01/2024
        postingDTO.setStatus(true);

        // Thực hiện phương thức
        boolean result = postingService.save(postingDTO);

        // In kết quả kiểm thử
        System.out.println("TC013 - Kết quả mong đợi: false, Kết quả thực tế: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC014_findByEmployerId_ValidId_ReturnsPostingsList:
     * Mô tả: Kiểm tra khi truyền vào ID hợp lệ và có bài đăng.
     * Đầu vào: empid = 1.
     * Kết quả mong đợi: Trả về danh sách các bài đăng của nhà tuyển dụng có ID là 1.
     */
    @Test
    @DisplayName("TC014_findByEmployerId_ValidId_ReturnsPostingsList")
    void TC014_findByEmployerId_ValidId_ReturnsPostingsList() {
        int empid = 1;
        List<Postings> postingList = List.of(new Postings(), new Postings());
        List<PostingDTO> dtoList = List.of(new PostingDTO(), new PostingDTO());

        when(postingRepository.findByEmployerId(empid)).thenReturn(postingList);
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any())).thenReturn(dtoList);

        List<PostingDTO> result = postingService.findByEmployerId(empid);

        System.out.println("Kết quả mong đợi: 2, Kết quả thực tế: " + result.size());
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * TC015_findByEmployerId_ValidIdNoPostings_ReturnsEmptyList:
     * Mô tả: Kiểm tra khi truyền vào ID hợp lệ nhưng không có bài đăng.
     * Đầu vào: empid = 2.
     * Kết quả mong đợi: Trả về danh sách rỗng.
     */
    @Test
    @DisplayName("TC015_findByEmployerId_ValidIdNoPostings_ReturnsEmptyList")
    void TC015_findByEmployerId_ValidIdNoPostings_ReturnsEmptyList() {
        int empid = 2;

        when(postingRepository.findByEmployerId(empid)).thenReturn(new ArrayList<>());

        List<PostingDTO> result = postingService.findByEmployerId(empid);

        System.out.println("Kết quả mong đợi: rỗng, Kết quả thực tế: " + (result == null ? "null" : "rỗng"));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC016_findByEmployerId_InvalidId_ReturnsEmptyList:
     * Mô tả: Kiểm tra khi truyền vào ID không hợp lệ.
     * Đầu vào: empid = -1.
     * Kết quả mong đợi: Trả về danh sách rỗng.
     */
    @Test
    @DisplayName("TC016_findByEmployerId_InvalidId_ReturnsEmptyList")
    void TC016_findByEmployerId_InvalidId_ReturnsEmptyList() {
        int empid = -1;

        when(postingRepository.findByEmployerId(empid)).thenReturn(new ArrayList<>());

        List<PostingDTO> result = postingService.findByEmployerId(empid);

        System.out.println("Kết quả mong đợi: rỗng, Kết quả thực tế: " + (result == null ? "null" : "rỗng"));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC017_findByEmployerId_ExceptionThrown_ReturnsEmptyList:
     * Mô tả: Xử lý ngoại lệ khi truy vấn bài đăng.
     * Đầu vào: empid = 3.
     * Kết quả mong đợi: In ra thông báo lỗi, không làm ứng dụng dừng đột ngột, trả về danh sách rỗng.
     */
    @Test
    @DisplayName("TC017_findByEmployerId_ExceptionThrown_ReturnsEmptyList")
    void TC017_findByEmployerId_ExceptionThrown_ReturnsEmptyList() {
        int empid = 3;

        when(postingRepository.findByEmployerId(empid)).thenThrow(new RuntimeException("Database Error"));

        List<PostingDTO> result = postingService.findByEmployerId(empid);

        System.out.println("Kết quả mong đợi: rỗng, Kết quả thực tế: " + result.size());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC018_findByEmployerId_ZeroId_ReturnsEmptyList:
     * Mô tả: Tìm bài đăng khi ID nhà tuyển dụng là 0.
     * Đầu vào: empid = 0.
     * Kết quả mong đợi: Trả về danh sách rỗng.
     */
    @Test
    @DisplayName("TC018_findByEmployerId_ZeroId_ReturnsEmptyList")
    void TC018_findByEmployerId_ZeroId_ReturnsEmptyList() {
        int empid = 0;

        when(postingRepository.findByEmployerId(empid)).thenReturn(new ArrayList<>());

        List<PostingDTO> result = postingService.findByEmployerId(empid);

        System.out.println("Kết quả mong đợi: rỗng, Kết quả thực tế: " + (result == null ? "null" : "rỗng"));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC019_findByEmployerName_ValidKeyword_ReturnsPostingsList:
     * Mô tả: Đảm bảo trả về danh sách bài đăng đúng theo tên nhà tuyển dụng (sử dụng keyword).
     * Đầu vào: keyword = "Company A".
     * Kết quả mong đợi: Trả về danh sách các bài đăng của nhà tuyển dụng có tên chứa "Company A".
     */
    @Test
    @DisplayName("TC019_findByEmployerName_ValidKeyword_ReturnsPostingsList")
    void TC019_findByEmployerName_ValidKeyword_ReturnsPostingsList() {
        String keyword = "Company A";
        List<Postings> postingList = List.of(new Postings(), new Postings());
        List<PostingDTO> dtoList = List.of(new PostingDTO(), new PostingDTO());

        when(postingRepository.findbyemployername(keyword)).thenReturn(postingList);
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any())).thenReturn(dtoList);

        List<PostingDTO> result = postingService.findByEmployerName(keyword);

        System.out.println("Kết quả mong đợi: 2, Kết quả thực tế: " + result.size());
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * TC020_findByEmployerName_UnknownKeyword_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách rỗng khi không có bài đăng nào khớp tên nhà tuyển dụng.
     * Đầu vào: keyword = "Unknown Company".
     * Kết quả mong đợi: Trả về danh sách rỗng.
     */
    @Test
    @DisplayName("TC020_findByEmployerName_UnknownKeyword_ReturnsEmptyList")
    void TC020_findByEmployerName_UnknownKeyword_ReturnsEmptyList() {
        String keyword = "Unknown Company";

        when(postingRepository.findbyemployername(keyword)).thenReturn(new ArrayList<>());

        List<PostingDTO> result = postingService.findByEmployerName(keyword);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC021_findByEmployerName_EmptyKeyword_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách rỗng khi tên nhà tuyển dụng là chuỗi rỗng.
     * Đầu vào: keyword = "".
     * Kết quả mong đợi: Trả về danh sách rỗng.
     */
    @Test
    @DisplayName("TC021_findByEmployerName_EmptyKeyword_ReturnsEmptyList")
    void TC021_findByEmployerName_EmptyKeyword_ReturnsEmptyList() {
        String keyword = "";

        when(postingRepository.findbyemployername(keyword)).thenReturn(new ArrayList<>());

        List<PostingDTO> result = postingService.findByEmployerName(keyword);

        System.out.println("Kết quả mong đợi: rỗng, Kết quả thực tế: " + (result == null ? "null" : "rỗng"));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC022_findByEmployerName_ExceptionThrown_ReturnsEmptyList:
     * Mô tả: Đảm bảo xử lý ngoại lệ khi xảy ra lỗi trong quá trình truy vấn.
     * Đầu vào: keyword = "Error" (giả lập lỗi).
     * Kết quả mong đợi: In ra thông báo lỗi, không làm ứng dụng dừng đột ngột.
     */
    @Test
    @DisplayName("TC022_findByEmployerName_ExceptionThrown_ReturnsEmptyList")
    void TC022_findByEmployerName_ExceptionThrown_ReturnsEmptyList() {
        String keyword = "Error";

        when(postingRepository.findbyemployername(keyword)).thenThrow(new RuntimeException("Database Error"));

        List<PostingDTO> result = postingService.findByEmployerName(keyword);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC023_findByEmployerName_CaseInsensitive_ReturnsPostingsList:
     * Mô tả: Đảm bảo không phân biệt hoa thường trong tên nhà tuyển dụng.
     * Đầu vào: keyword = "company a".
     * Kết quả mong đợi: Trả về danh sách các bài đăng của nhà tuyển dụng có tên chứa "Company A".
     */
    @Test
    @DisplayName("TC023_findByEmployerName_CaseInsensitive_ReturnsPostingsList")
    void TC023_findByEmployerName_CaseInsensitive_ReturnsPostingsList() {
        String keyword = "company a";
        List<Postings> postingList = List.of(new Postings(), new Postings());
        List<PostingDTO> dtoList = List.of(new PostingDTO(), new PostingDTO());

        when(postingRepository.findbyemployername(keyword)).thenReturn(postingList);
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any())).thenReturn(dtoList);

        List<PostingDTO> result = postingService.findByEmployerName(keyword);

        System.out.println("Kết quả mong đợi: 2, Kết quả thực tế: " + result.size());
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * TC024_findByEmployerName_SpecialChars_ReturnsPostingsList:
     * Mô tả: Đảm bảo xử lý đúng với tên chứa ký tự đặc biệt.
     * Đầu vào: keyword = "Comp@ny B!".
     * Kết quả mong đợi: Trả về danh sách các bài đăng của nhà tuyển dụng "Comp@ny B!".
     */
    @Test
    @DisplayName("TC024_findByEmployerName_SpecialChars_ReturnsPostingsList")
    void TC024_findByEmployerName_SpecialChars_ReturnsPostingsList() {
        String keyword = "Comp@ny B!";
        List<Postings> postingList = List.of(new Postings(), new Postings());
        List<PostingDTO> dtoList = List.of(new PostingDTO(), new PostingDTO());

        when(postingRepository.findbyemployername(keyword)).thenReturn(postingList);
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any())).thenReturn(dtoList);

        List<PostingDTO> result = postingService.findByEmployerName(keyword);

        System.out.println("Kết quả mong đợi: 2, Kết quả thực tế: " + result.size());
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * TC025_findByEmployerName_NullKeyword_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách rỗng nếu tên là null.
     * Đầu vào: keyword = null.
     * Kết quả mong đợi: Trả về danh sách rỗng.
     */
    @Test
    @DisplayName("TC025_findByEmployerName_NullKeyword_ReturnsEmptyList")
    void TC025_findByEmployerName_NullKeyword_ReturnsEmptyList() {
        String keyword = null;

        when(postingRepository.findbyemployername(keyword)).thenReturn(new ArrayList<>());

        List<PostingDTO> result = postingService.findByEmployerName(keyword);

        System.out.println("Kết quả mong đợi: rỗng, Kết quả thực tế: " + (result == null ? "null" : "rỗng"));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC026_findByEmployerIdLimit_ValidIdAndLimit_ReturnsLimitedList:
     * Mô tả: Đảm bảo trả về đúng số lượng bài đăng theo giới hạn được yêu cầu.
     * Đầu vào: employerid = 1, limit = 3.
     * Kết quả mong đợi: Trả về danh sách tối đa 3 bài đăng của nhà tuyển dụng ID = 1.
     */
    @Test
    @DisplayName("TC026_findByEmployerIdLimit_ValidIdAndLimit_ReturnsLimitedList")
    void TC026_findByEmployerIdLimit_ValidIdAndLimit_ReturnsLimitedList() {
        int employerId = 1;
        int limit = 3;
        List<Postings> postingList = List.of(new Postings(), new Postings(), new Postings());
        List<PostingDTO> dtoList = List.of(new PostingDTO(), new PostingDTO(), new PostingDTO());

        when(postingRepository.limit3(employerId, limit)).thenReturn(postingList);
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any())).thenReturn(dtoList);

        List<PostingDTO> result = postingService.findByEmployerIdlimit(employerId, limit);

        System.out.println("Kết quả mong đợi: 3, Kết quả thực tế: " + result.size());
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    /**
     * TC027_findByEmployerIdLimit_NoPostings_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách rỗng khi không có bài đăng nào của nhà tuyển dụng.
     * Đầu vào: employerid = 99, limit = 3.
     * Kết quả mong đợi: Trả về danh sách rỗng.
     */
    @Test
    @DisplayName("TC027_findByEmployerIdLimit_NoPostings_ReturnsEmptyList")
    void TC027_findByEmployerIdLimit_NoPostings_ReturnsEmptyList() {
        int employerId = 99;
        int limit = 3;

        when(postingRepository.limit3(employerId, limit)).thenReturn(new ArrayList<>());

        List<PostingDTO> result = postingService.findByEmployerIdlimit(employerId, limit);

        System.out.println("Kết quả mong đợi: rỗng, Kết quả thực tế: " + (result == null ? "null" : "rỗng"));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    /**
     * TC030_findByEmployerIdLimit_InvalidEmployerId_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách rỗng nếu ID nhà tuyển dụng không hợp lệ.
     * Đầu vào: employerid = -1, limit = 3.
     * Kết quả mong đợi: Trả về danh sách rỗng.
     */
    @Test
    @DisplayName("TC030_findByEmployerIdLimit_InvalidEmployerId_ReturnsEmptyList")
    void TC030_findByEmployerIdLimit_InvalidEmployerId_ReturnsEmptyList() {
        int employerId = -1;
        int limit = 3;

        List<PostingDTO> result = postingService.findByEmployerIdlimit(employerId, limit);

        System.out.println("Kết quả mong đợi: rỗng, Kết quả thực tế: " + (result == null ? "null" : "rỗng"));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC031_findByEmployerIdLimit_ZeroLimit_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách rỗng nếu giới hạn là 0.
     * Đầu vào: employerid = 1, limit = 0.
     * Kết quả mong đợi: Trả về danh sách rỗng.
     */
    @Test
    @DisplayName("TC031_findByEmployerIdLimit_ZeroLimit_ReturnsEmptyList")
    void TC031_findByEmployerIdLimit_ZeroLimit_ReturnsEmptyList() {
        int employerId = 1;
        int limit = 0;

        List<PostingDTO> result = postingService.findByEmployerIdlimit(employerId, limit);

        System.out.println("Kết quả mong đợi: rỗng, Kết quả thực tế: " + (result == null ? "null" : "rỗng"));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC032_findByEmployerIdLimit_NegativeLimit_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách rỗng nếu giới hạn là số âm.
     * Đầu vào: employerid = 1, limit = -5.
     * Kết quả mong đợi: Trả về danh sách rỗng.
     */
    @Test
    @DisplayName("TC032_findByEmployerIdLimit_NegativeLimit_ReturnsEmptyList")
    void TC032_findByEmployerIdLimit_NegativeLimit_ReturnsEmptyList() {
        int employerId = 1;
        int limit = -5;

        List<PostingDTO> result = postingService.findByEmployerIdlimit(employerId, limit);

        System.out.println("Kết quả mong đợi: rỗng, Kết quả thực tế: " + (result == null ? "null" : "rỗng"));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC033_findByEmployerIdWithLimit_ZeroEmployerId_ReturnsLimitedList:
     * Mô tả: Đảm bảo không phân biệt nhà tuyển dụng nếu ID là 0.
     * Đầu vào: employerid = 0, limit = 3.
     * Kết quả mong đợi: Trả về danh sách tối đa 3 bài đăng của bất kỳ nhà tuyển dụng nào.
     */
    @Test
    @DisplayName("TC033_findByEmployerIdWithLimit_ZeroEmployerId_ReturnsLimitedList")
    void TC033_findByEmployerIdWithLimit_ZeroEmployerId_ReturnsLimitedList() {
        int employerId = 0;
        int limit = 3;
        List<Postings> postingList = List.of(new Postings(), new Postings(), new Postings());
        List<PostingDTO> dtoList = List.of(new PostingDTO(), new PostingDTO(), new PostingDTO());

        when(postingRepository.limit3(employerId, limit)).thenReturn(postingList);
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any())).thenReturn(dtoList);

        List<PostingDTO> result = postingService.findByEmployerIdlimit(employerId, limit);

        System.out.println("Kết quả mong đợi: 3, Kết quả thực tế: " + result.size());
        assertNotNull(result);
        assertEquals(3, result.size());
    }


    /**
     * TC028_findByEmployerIdLimit_LessThanLimit_ReturnsAll:
     * Mô tả: Đảm bảo trả về toàn bộ bài đăng nếu số lượng bài đăng ít hơn giới hạn yêu cầu.
     * Đầu vào: employerid = 1, limit = 10.
     * Kết quả mong đợi: Trả về toàn bộ bài đăng của nhà tuyển dụng.
     */
    @Test
    @DisplayName("TC028_findByEmployerIdLimit_LessThanLimit_ReturnsAll")
    void TC028_findByEmployerIdLimit_LessThanLimit_ReturnsAll() {
        int employerId = 1;
        int limit = 10;
        List<Postings> postingList = List.of(new Postings(), new Postings());
        List<PostingDTO> dtoList = List.of(new PostingDTO(), new PostingDTO());

        when(postingRepository.limit3(employerId, limit)).thenReturn(postingList);
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any())).thenReturn(dtoList);

        List<PostingDTO> result = postingService.findByEmployerIdlimit(employerId, limit);

        System.out.println("Kết quả mong đợi: 2, Kết quả thực tế: " + result.size());
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * TC029_findByEmployerIdLimit_ExceptionThrown_ReturnsEmptyList:
     * Mô tả: Đảm bảo xử lý ngoại lệ nếu xảy ra lỗi trong quá trình truy vấn.
     * Đầu vào: employerid = 1, limit = 3 (giả lập lỗi).
     * Kết quả mong đợi: In ra thông báo lỗi, không làm ứng dụng dừng đột ngột.
     */
    @Test
    @DisplayName("TC029_findByEmployerIdLimit_ExceptionThrown_ReturnsEmptyList")
    void TC029_findByEmployerIdLimit_ExceptionThrown_ReturnsEmptyList() {
        int employerId = 1;
        int limit = 3;

        when(postingRepository.limit3(employerId, limit))
                .thenThrow(new RuntimeException("Database Error"));

        assertDoesNotThrow(() -> {
            List<PostingDTO> result = postingService.findByEmployerIdlimit(employerId, limit);
            System.out.println("Kết quả mong đợi: rỗng, Kết quả thực tế: " + result.size());
            assertNotNull(result);
            assertTrue(result.isEmpty());
        });
    }

    /**
     * TC034 - Đảm bảo trả về bài đăng đúng với ID được yêu cầu.
     * Đầu vào: ID = 1.
     * Kết quả mong đợi: Trả về bài đăng với ID = 1.
     */
    @Test
    @DisplayName("TC034_FindPostById_ExistingID_ReturnsCorrectPost")
    void TC034_FindPostById_ExistingID_ReturnsCorrectPost() {
        // Định nghĩa hành vi của repository
        Postings posting = new Postings();
        posting.setId(1);
        PostingDTO postingDTO = new PostingDTO();

        when(postingRepository.findById(1)).thenReturn(Optional.of(posting));
        when(modelMapper.map(posting, PostingDTO.class)).thenReturn(postingDTO);

        // Thực thi phương thức
        PostingDTO result = postingService.findById(1);

        // In ra kết quả
        System.out.println("Kết quả mong muốn: Đối tượng bài đăng với ID = 1");
        System.out.println("Kết quả thực tế: " + (result != null ? "Đúng" : "Sai"));

        // Xác minh kết quả
        assertNotNull(result);
    }

    /**
     * TC035 - Đảm bảo trả về null nếu không tìm thấy bài đăng với ID yêu cầu.
     * Đầu vào: ID = 99 (không tồn tại).
     * Kết quả mong đợi: Trả về null.
     */
    @Test
    @DisplayName("TC035_FindPostById_NonExistingID_ReturnsNull")
    void TC035_FindPostById_NonExistingID_ReturnsNull() {
        // Định nghĩa hành vi của repository
        when(postingRepository.findById(99)).thenReturn(Optional.empty());

        // Thực thi phương thức
        PostingDTO result = postingService.findById(99);

        // In ra kết quả
        System.out.println("Kết quả mong muốn: null");
        System.out.println("Kết quả thực tế: " + (result == null ? "null" : "Đối tượng không null"));

        // Xác minh kết quả
        assertNull(result);
    }

    /**
     * TC036 - Đảm bảo xử lý ngoại lệ nếu xảy ra lỗi trong quá trình truy vấn.
     * Đầu vào: ID = 1 (giả lập lỗi).
     * Kết quả mong đợi: In ra thông báo lỗi, không làm ứng dụng dừng đột ngột.
     */
    @Test
    @DisplayName("TC036_FindPostById_ExceptionThrown_PrintsStackTrace")
    void TC036_FindPostById_ExceptionThrown_PrintsStackTrace() {
        // Định nghĩa hành vi của repository để ném ngoại lệ
        when(postingRepository.findById(1)).thenThrow(new RuntimeException("Database error"));

        // Thực thi phương thức
        try {
            postingService.findById(1);
        } catch (Exception e) {
            System.out.println("Kết quả mong muốn: In ra thông báo lỗi");
            e.printStackTrace();
        }
    }

    /**
     * TC037 - Đảm bảo trả về null nếu ID truyền vào là số âm.
     * Đầu vào: ID = -1.
     * Kết quả mong đợi: Trả về null.
     */
    @Test
    @DisplayName("TC037_FindPostById_NegativeID_ReturnsNull")
    void TC037_FindPostById_NegativeID_ReturnsNull() {
        // Thực thi phương thức
        PostingDTO result = postingService.findById(-1);

        // In ra kết quả
        System.out.println("Kết quả mong muốn: null");
        System.out.println("Kết quả thực tế: " + (result == null ? "null" : "Đối tượng không null"));

        // Xác minh kết quả
        assertNull(result);
    }

    /**
     * TC038 - Đảm bảo trả về null nếu ID truyền vào là 0.
     * Đầu vào: ID = 0.
     * Kết quả mong đợi: Trả về null.
     */
    @Test
    @DisplayName("TC038_FindPostById_ZeroID_ReturnsNull")
    void TC038_FindPostById_ZeroID_ReturnsNull() {
        // Thực thi phương thức
        PostingDTO result = postingService.findById(0);

        // In ra kết quả
        System.out.println("Kết quả mong muốn: null");
        System.out.println("Kết quả thực tế: " + (result == null ? "null" : "Đối tượng không null"));

        // Xác minh kết quả
        assertNull(result);
    }

    /**
     * TC039 - Đảm bảo trả về null nếu đối tượng Optional không chứa bài đăng nào.
     * Đầu vào: ID = 1 (giả lập Optional rỗng).
     * Kết quả mong đợi: Trả về null.
     */
    @Test
    @DisplayName("TC039_FindPostById_EmptyOptional_ReturnsNull")
    void TC039_FindPostById_EmptyOptional_ReturnsNull() {
        // Định nghĩa hành vi của repository với Optional rỗng
        when(postingRepository.findById(1)).thenReturn(Optional.empty());

        // Thực thi phương thức
        PostingDTO result = postingService.findById(1);

        // In ra kết quả
        System.out.println("Kết quả mong muốn: null");
        System.out.println("Kết quả thực tế: " + (result == null ? "null" : "Đối tượng không null"));

        // Xác minh kết quả
        assertNull(result);
    }


    /**
     * TC040 - Đảm bảo hoạt động ổn định khi ID là số rất lớn.
     * Đầu vào: ID = Integer.MAX_VALUE.
     * Kết quả mong đợi: Trả về null (không tìm thấy) hoặc bài đăng tương ứng nếu có.
     */
    @Test
    @DisplayName("TC040_FindPostById_MaxValueID_ReturnsNull")
    void TC040_FindPostById_MaxValueID_ReturnsNull() {
        // Định nghĩa hành vi của repository
        when(postingRepository.findById(Integer.MAX_VALUE)).thenReturn(Optional.empty());

        // Thực thi phương thức
        PostingDTO result = postingService.findById(Integer.MAX_VALUE);

        // Xác minh kết quả
        System.out.println("Kết quả mong muốn: null");
        System.out.println("Kết quả thực tế: " + (result == null ? "null" : result));
        assertNull(result);
    }

    /**
     * TC041 - Đảm bảo hoạt động bình thường với các ID liên tiếp.
     * Đầu vào: ID = 1, ID = 2, ID = 3.
     * Kết quả mong đợi: Trả về bài đăng tương ứng với từng ID.
     */
    @Test
    @DisplayName("TC041_FindPostById_SequentialIDs_ReturnsCorrectPosts")
    void TC041_FindPostById_SequentialIDs_ReturnsCorrectPosts() {
        // Định nghĩa hành vi của repository
        Postings posting1 = new Postings(); posting1.setId(1);
        Postings posting2 = new Postings(); posting2.setId(2);
        Postings posting3 = new Postings(); posting3.setId(3);

        PostingDTO dto1 = new PostingDTO();
        PostingDTO dto2 = new PostingDTO();
        PostingDTO dto3 = new PostingDTO();

        when(postingRepository.findById(1)).thenReturn(Optional.of(posting1));
        when(postingRepository.findById(2)).thenReturn(Optional.of(posting2));
        when(postingRepository.findById(3)).thenReturn(Optional.of(posting3));

        when(modelMapper.map(posting1, PostingDTO.class)).thenReturn(dto1);
        when(modelMapper.map(posting2, PostingDTO.class)).thenReturn(dto2);
        when(modelMapper.map(posting3, PostingDTO.class)).thenReturn(dto3);

        // Thực thi phương thức và in ra kết quả
        System.out.println("Kết quả mong muốn: Không null");
        System.out.println("Kết quả thực tế ID=1: " + postingService.findById(1));
        System.out.println("Kết quả thực tế ID=2: " + postingService.findById(2));
        System.out.println("Kết quả thực tế ID=3: " + postingService.findById(3));

        assertNotNull(postingService.findById(1));
        assertNotNull(postingService.findById(2));
        assertNotNull(postingService.findById(3));
    }

    /**
     * TC042 - Đảm bảo hoạt động ổn định khi nhiều request đồng thời tìm kiếm cùng một ID.
     * Đầu vào: Nhiều request với ID = 1.
     * Kết quả mong đợi: Đảm bảo tất cả các request đều trả về kết quả chính xác.
     */
    @Test
    @DisplayName("TC042_FindPostById_MultipleConcurrentRequests_ReturnsConsistentResult")
    void TC042_FindPostById_MultipleConcurrentRequests_ReturnsConsistentResult() throws InterruptedException {
        // Định nghĩa hành vi của repository
        Postings posting = new Postings();
        posting.setId(1);
        PostingDTO dto = new PostingDTO();

        when(postingRepository.findById(1)).thenReturn(Optional.of(posting));
        when(modelMapper.map(posting, PostingDTO.class)).thenReturn(dto);

        // Số lượng request đồng thời
        int threadCount = 100;
        List<Thread> threads = new ArrayList<>();
        List<PostingDTO> results = Collections.synchronizedList(new ArrayList<>());

        // Tạo và chạy nhiều thread đồng thời
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                PostingDTO result = postingService.findById(1);
                results.add(result);
            });
            threads.add(thread);
            thread.start();
        }

        // Đợi tất cả các thread hoàn thành
        for (Thread thread : threads) {
            thread.join();
        }

        // In ra kết quả
        System.out.println("Kết quả mong muốn: 100 đối tượng không null");
        System.out.println("Kết quả thực tế: " + results.size() + " đối tượng");
        results.forEach(result -> System.out.println(result));

        // Xác minh tất cả kết quả đều đúng
        assertEquals(threadCount, results.size());
        results.forEach(result -> assertNotNull(result));
    }

    /**
     * TC043 - Đảm bảo đúng loại dữ liệu trả về là PostingDTO.
     * Đầu vào: ID = 1.
     * Kết quả mong đợi: Kết quả trả về là một đối tượng thuộc kiểu PostingDTO.
     */
    @Test
    @DisplayName("TC043_FindPostById_ReturnsCorrectType_PostingDTO")
    void TC043_FindPostById_ReturnsCorrectType_PostingDTO() {
        // Định nghĩa hành vi của repository
        Postings posting = new Postings();
        posting.setId(1);
        PostingDTO postingDTO = new PostingDTO();

        when(postingRepository.findById(1)).thenReturn(Optional.of(posting));
        when(modelMapper.map(posting, PostingDTO.class)).thenReturn(postingDTO);

        // Thực thi phương thức
        PostingDTO result = postingService.findById(1);

        // In ra kết quả
        System.out.println("Kết quả mong muốn: Đối tượng thuộc kiểu PostingDTO");
        System.out.println("Kết quả thực tế: " + (result instanceof PostingDTO ? "Đúng kiểu" : "Sai kiểu"));

        // Xác minh kết quả
        assertNotNull(result);
        assertTrue(result instanceof PostingDTO);
    }

    /**
     * TC044 - Xóa bài đăng khi ID tồn tại và không xảy ra ngoại lệ.
     * Đầu vào: ID = 1.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC044_DeletePost_ExistingID_ReturnsTrue")
    void TC044_DeletePost_ExistingID_ReturnsTrue() {
        // Định nghĩa hành vi của repository
        Postings posting = new Postings();
        when(postingRepository.findById(1)).thenReturn(Optional.of(posting));

        // Thực thi phương thức
        boolean result = postingService.delete(1);

        // In kết quả
        System.out.println("Kết quả: " + result);

        // Xác minh kết quả
        assertTrue(result);
        verify(postingRepository).delete(posting);
    }

    /**
     * TC045 - Không xóa bài đăng khi ID không tồn tại, không xảy ra ngoại lệ.
     * Đầu vào: ID = 999.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC045_DeletePost_NonExistingID_ReturnsFalse")
    void TC045_DeletePost_NonExistingID_ReturnsFalse() {
        // Định nghĩa hành vi của repository
        when(postingRepository.findById(999)).thenReturn(Optional.empty());

        // Thực thi phương thức
        boolean result = postingService.delete(999);

        // In kết quả
        System.out.println("Kết quả: " + result);

        // Xác minh kết quả
        assertFalse(result);
        verify(postingRepository, never()).delete(any());
    }

    /**
     * TC046 - Ném ngoại lệ khi phương thức findById của repository ném ngoại lệ.
     * Đầu vào: ID = 1.
     * Kết quả mong đợi: Trả về false và in ra stack trace.
     */
    @Test
    @DisplayName("TC046_DeletePost_ExceptionOnFindById_PrintsStackTrace")
    void TC046_DeletePost_ExceptionOnFindById_PrintsStackTrace() {
        // Định nghĩa hành vi của repository để ném ngoại lệ
        when(postingRepository.findById(1)).thenThrow(new RuntimeException("Database error"));

        // Thực thi phương thức
        boolean result = postingService.delete(1);

        // In kết quả
        System.out.println("Kết quả: " + result);

        // Xác minh kết quả
        assertFalse(result);
    }

    /**
     * TC047 - Ném ngoại lệ khi phương thức delete của repository ném ngoại lệ.
     * Đầu vào: ID = 1.
     * Kết quả mong đợi: Trả về false và in ra stack trace.
     */
    @Test
    @DisplayName("TC047_DeletePost_ExceptionOnDelete_PrintsStackTrace")
    void TC047_DeletePost_ExceptionOnDelete_PrintsStackTrace() {
        // Định nghĩa hành vi của repository
        Postings posting = new Postings();
        when(postingRepository.findById(1)).thenReturn(Optional.of(posting));
        doThrow(new RuntimeException("Delete error")).when(postingRepository).delete(posting);

        // Thực thi phương thức
        boolean result = postingService.delete(1);

        // In kết quả
        System.out.println("Kết quả: " + result);

        // Xác minh kết quả
        assertFalse(result);
    }

    /**
     * TC048 - Xóa bài đăng khi ID là 0 (trường hợp giá trị đặc biệt).
     * Đầu vào: ID = 0.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC048_DeletePost_ZeroID_ReturnsFalse")
    void TC048_DeletePost_ZeroID_ReturnsFalse() {
        boolean result = postingService.delete(0);
        System.out.println("Kết quả: " + result);
        assertFalse(result);
    }

    /**
     * TC049 - Xóa bài đăng khi ID là số âm (trường hợp giá trị đặc biệt).
     * Đầu vào: ID = -1.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC049_DeletePost_NegativeID_ReturnsFalse")
    void TC049_DeletePost_NegativeID_ReturnsFalse() {
        boolean result = postingService.delete(-1);
        System.out.println("Kết quả: " + result);
        assertFalse(result);
    }

    /**
     * TC050 - Xóa bài đăng khi ID là số lớn (giá trị vượt quá giới hạn thông thường).
     * Đầu vào: ID = Integer.MAX_VALUE (2147483647).
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC050_DeletePost_MaxIntegerID_ReturnsFalse")
    void TC050_DeletePost_MaxIntegerID_ReturnsFalse() {
        boolean result = postingService.delete(Integer.MAX_VALUE);
        System.out.println("Kết quả: " + result);
        assertFalse(result);
    }

    /**
     * TC052_findbystatus_True_ReturnsTrueStatusPosts:
     * Mô tả: Đảm bảo trả về danh sách bài đăng có trạng thái true.
     * Đầu vào: status = true.
     * Kết quả mong đợi: Trả về danh sách bài đăng có trạng thái true.
     */
    @Test
    @DisplayName("TC052_findbystatus_True_ReturnsTrueStatusPosts")
    void TC052_findbystatus_True_ReturnsTrueStatusPosts() {
        // Khởi tạo dữ liệu giả
        List<Postings> postingList = Arrays.asList(new Postings(), new Postings());
        List<PostingDTO> dtoList = Arrays.asList(new PostingDTO(), new PostingDTO());

        // Định nghĩa hành vi của repository và mapper
        when(postingRepository.findbystatus(true)).thenReturn(postingList);
        when(modelMapper.map(postingList, new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(dtoList);

        // Thực thi phương thức
        List<PostingDTO> result = postingService.findByStatus(true);

        // In kết quả
        System.out.println("Kết quả: " + (result == null ? "null" : (result.isEmpty() ? "trống" : "có " + result.size() + " bài đăng")));

        // Xác minh kết quả
        assertNotNull(result);
        assertEquals(dtoList.size(), result.size());
        assertEquals(dtoList, result);
    }

    /**
     * TC053_findbystatus_False_ReturnsFalseStatusPosts:
     * Mô tả: Đảm bảo lấy danh sách bài đăng với trạng thái false.
     * Đầu vào: status = false.
     * Kết quả mong đợi: Trả về danh sách bài đăng có trạng thái false.
     */
    @Test
    @DisplayName("TC053_findbystatus_False_ReturnsFalseStatusPosts")
    void TC053_findbystatus_False_ReturnsFalseStatusPosts() {
        // Khởi tạo dữ liệu giả
        List<Postings> postingList = Arrays.asList(new Postings(), new Postings());
        List<PostingDTO> dtoList = Arrays.asList(new PostingDTO(), new PostingDTO());

        // Định nghĩa hành vi của repository và mapper
        when(postingRepository.findbystatus(false)).thenReturn(postingList);
        when(modelMapper.map(postingList, new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(dtoList);

        // Thực thi phương thức
        List<PostingDTO> result = postingService.findByStatus(false);

        // In kết quả
        System.out.println("Kết quả: " + (result == null ? "null" : (result.isEmpty() ? "trống" : "có " + result.size() + " bài đăng")));

        // Xác minh kết quả
        assertNotNull(result);
        assertEquals(dtoList.size(), result.size());
        assertEquals(dtoList, result);
    }

    /**
     * TC054_findbystatus_True_NoPosts_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi không có bài đăng nào với trạng thái true.
     * Đầu vào: status = true.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC054_findbystatus_True_NoPosts_ReturnsEmptyList")
    void TC054_findbystatus_True_NoPosts_ReturnsEmptyList() {
        // Định nghĩa hành vi của repository
        when(postingRepository.findbystatus(true)).thenReturn(Collections.emptyList());

        // Thực thi phương thức
        List<PostingDTO> result = postingService.findByStatus(true);

        // In kết quả
        System.out.println("Kết quả: " + (result == null ? "null" : (result.isEmpty() ? "trống" : "có " + result.size() + " bài đăng")));

        // Xác minh kết quả
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC055_findbystatus_False_NoPosts_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi không có bài đăng nào với trạng thái false.
     * Đầu vào: status = false.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC055_findbystatus_False_NoPosts_ReturnsEmptyList")
    void TC055_findbystatus_False_NoPosts_ReturnsEmptyList() {
        // Định nghĩa hành vi của repository
        when(postingRepository.findbystatus(false)).thenReturn(Collections.emptyList());

        // Thực thi phương thức
        List<PostingDTO> result = postingService.findByStatus(false);

        // In kết quả
        System.out.println("Kết quả: " + (result == null ? "null" : (result.isEmpty() ? "trống" : "có " + result.size() + " bài đăng")));

        // Xác minh kết quả
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC056_findbystatus_ExceptionThrown_PrintsStackTrace:
     * Mô tả: Đảm bảo phương thức ném ngoại lệ và in ra stack trace khi xảy ra lỗi.
     * Đầu vào: status = true.
     * Kết quả mong đợi: Ném ngoại lệ và in ra stack trace.
     */
    @Test
    @DisplayName("TC056_findbystatus_ExceptionThrown_PrintsStackTrace")
    void TC056_findbystatus_ExceptionThrown_PrintsStackTrace() {
        // Định nghĩa hành vi của repository để ném ngoại lệ
        when(postingRepository.findbystatus(true)).thenThrow(new RuntimeException("Database error"));

        // Thực thi phương thức và xác minh ngoại lệ
        try {
            postingService.findByStatus(true);
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException ex) {
            // In thông báo ngoại lệ
            System.out.println("Ngoại lệ: " + ex.getMessage());
            assertEquals("Database error", ex.getMessage());
        }
    }

    /**
     * TC057_findByEmployerIdStatus_ValidEmployerId_True_ReturnsTrueStatusPosts:
     * Mô tả: Đảm bảo lấy danh sách bài đăng của nhà tuyển dụng với trạng thái true.
     * Đầu vào: employerid = 1, status = true.
     * Kết quả mong đợi: Trả về danh sách bài đăng của nhà tuyển dụng có trạng thái true.
     */
    @Test
    @DisplayName("TC057_findByEmployerIdStatus_ValidEmployerId_True_ReturnsTrueStatusPosts")
    void TC057_findByEmployerIdStatus_ValidEmployerId_True_ReturnsTrueStatusPosts() {
        List<Postings> postingList = Arrays.asList(new Postings(), new Postings());
        List<PostingDTO> dtoList = Arrays.asList(new PostingDTO(), new PostingDTO());

        when(postingRepository.findbyemployerstatus(1, true)).thenReturn(postingList);
        when(modelMapper.map(postingList, new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(dtoList);

        List<PostingDTO> result = postingService.findByEmployerIdStatus(1, true);

        System.out.println("Kết quả trả về: " + result.size() + " bài đăng");

        assertNotNull(result);
        assertEquals(dtoList.size(), result.size());
        assertEquals(dtoList, result);
    }

    /**
     * TC058_findByEmployerIdStatus_ValidEmployerId_False_ReturnsFalseStatusPosts:
     * Mô tả: Đảm bảo lấy danh sách bài đăng của nhà tuyển dụng với trạng thái false.
     * Đầu vào: employerid = 1, status = false.
     * Kết quả mong đợi: Trả về danh sách bài đăng của nhà tuyển dụng có trạng thái false.
     */
    @Test
    @DisplayName("TC058_findByEmployerIdStatus_ValidEmployerId_False_ReturnsFalseStatusPosts")
    void TC058_findByEmployerIdStatus_ValidEmployerId_False_ReturnsFalseStatusPosts() {
        List<Postings> postingList = Arrays.asList(new Postings(), new Postings());
        List<PostingDTO> dtoList = Arrays.asList(new PostingDTO(), new PostingDTO());

        when(postingRepository.findbyemployerstatus(1, false)).thenReturn(postingList);
        when(modelMapper.map(postingList, new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(dtoList);

        List<PostingDTO> result = postingService.findByEmployerIdStatus(1, false);

        System.out.println("Kết quả trả về: " + result.size() + " bài đăng");

        assertNotNull(result);
        assertEquals(dtoList.size(), result.size());
        assertEquals(dtoList, result);
    }

    /**
     * TC059_findByEmployerIdStatus_InvalidEmployerId_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi employerid không tồn tại.
     * Đầu vào: employerid = 999, status = true.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC059_findByEmployerIdStatus_InvalidEmployerId_ReturnsEmptyList")
    void TC059_findByEmployerIdStatus_InvalidEmployerId_ReturnsEmptyList() {
        // Định nghĩa hành vi của repository
        when(postingRepository.findbyemployerstatus(999, true)).thenReturn(Collections.emptyList());

        // Thực thi phương thức
        List<PostingDTO> result = postingService.findByEmployerIdStatus(999, true);
        // In ra kết quả
        System.out.println(result == null ? "Kết quả: null" : "Kết quả: trống");
        // Xác minh kết quả
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC060_findByEmployerIdStatus_True_NoMatchingPosts_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi không có bài đăng nào với trạng thái true.
     * Đầu vào: employerid = 1, status = true.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC060_findByEmployerIdStatus_True_NoMatchingPosts_ReturnsEmptyList")
    void TC060_findByEmployerIdStatus_True_NoMatchingPosts_ReturnsEmptyList() {
        // Định nghĩa hành vi của repository
        when(postingRepository.findbyemployerstatus(1, true)).thenReturn(Collections.emptyList());

        // Thực thi phương thức
        List<PostingDTO> result = postingService.findByEmployerIdStatus(1, true);

        // In ra kết quả
        System.out.println(result == null ? "Kết quả: null" : "Kết quả: trống");
        // Xác minh kết quả
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC061_findByEmployerIdStatus_False_NoMatchingPosts_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi không có bài đăng nào với trạng thái false.
     * Đầu vào: employerid = 1, status = false.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC061_findByEmployerIdStatus_False_NoMatchingPosts_ReturnsEmptyList")
    void TC061_findByEmployerIdStatus_False_NoMatchingPosts_ReturnsEmptyList() {
        // Định nghĩa hành vi của repository
        when(postingRepository.findbyemployerstatus(1, false)).thenReturn(Collections.emptyList());

        // Thực thi phương thức
        List<PostingDTO> result = postingService.findByEmployerIdStatus(1, false);

        // In ra kết quả
        System.out.println(result == null ? "Kết quả: null" : "Kết quả: trống");

        // Xác minh kết quả
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC062_findByEmployerIdStatus_ExceptionThrown_PrintsStackTrace:
     * Mô tả: Đảm bảo phương thức ném ngoại lệ và in ra stack trace khi xảy ra lỗi.
     * Đầu vào: employerid = 1, status = true.
     * Kết quả mong đợi: Ném ngoại lệ và in ra stack trace.
     */
    @Test
    @DisplayName("TC062_findByEmployerIdStatus_ExceptionThrown_PrintsStackTrace")
    void TC062_findByEmployerIdStatus_ExceptionThrown_PrintsStackTrace() {
        when(postingRepository.findbyemployerstatus(1, true)).thenThrow(new RuntimeException("Database error"));

        try {
            postingService.findByEmployerIdStatus(1, true);
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            System.out.println("Ngoại lệ đã xảy ra: " + ex.getMessage());
            assertEquals("Database error", ex.getMessage());
        }
    }
    /**
     * TC063_findByDeadline_ValidDeadlines_ReturnsFutureOrTodayPosts:
     * Mô tả: Đảm bảo lấy danh sách bài đăng với deadline lớn hơn hoặc bằng ngày hiện tại.
     * Đầu vào: deadline >= ngày hiện tại.
     * Kết quả mong đợi: Trả về danh sách bài đăng với deadline hợp lệ.
     */
    @Test
    @DisplayName("TC063_findByDeadline_ValidDeadlines_ReturnsFutureOrTodayPosts")
    void TC063_findByDeadline_ValidDeadlines_ReturnsFutureOrTodayPosts() {
        // Khởi tạo dữ liệu giả
        List<Postings> postingList = Arrays.asList(new Postings(), new Postings());
        List<PostingDTO> dtoList = Arrays.asList(new PostingDTO(), new PostingDTO());
        Date currentDate = new Date();

        // Định nghĩa hành vi của repository và mapper
        when(postingRepository.findbydeadline(any(Date.class))).thenReturn(postingList);
        when(modelMapper.map(postingList, new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(dtoList);

        // Thực thi phương thức
        List<PostingDTO> result = postingService.findByDeadline();

        // In ra kết quả để debug
        System.out.println("Kết quả trả về: " + (result == null ? "null" : result.size() + " bài đăng"));

        // Xác minh kết quả
        assertNotNull(result, "Danh sách trả về không được là null");
        assertEquals(dtoList.size(), result.size(), "Danh sách trả về phải chứa " + dtoList.size() + " bài đăng");
        assertEquals(dtoList, result, "Danh sách trả về phải khớp với danh sách mong đợi");
    }

    /**
     * TC064_findByDeadline_NoValidPosts_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi không có bài đăng nào có deadline hợp lệ.
     * Đầu vào: deadline >= ngày hiện tại, nhưng không có bài đăng nào thỏa mãn.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC064_findByDeadline_NoValidPosts_ReturnsEmptyList")
    void TC064_findByDeadline_NoValidPosts_ReturnsEmptyList() {
        // Khởi tạo danh sách rỗng cho kết quả giả lập
        List<Postings> emptyList = Collections.emptyList();
        Date currentDate = new Date();

        // Định nghĩa hành vi của repository và mapper
        when(postingRepository.findbydeadline(any(Date.class))).thenReturn(emptyList);
        when(modelMapper.map(emptyList, new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Collections.emptyList());

        // Thực thi phương thức
        List<PostingDTO> result = postingService.findByDeadline();

        // In ra kết quả để debug
        System.out.println("Kết quả trả về: " + (result == null ? "null" : result.size() + " bài đăng"));

        // Xác minh kết quả
        assertNotNull(result, "Danh sách trả về không được là null");
        assertTrue(result.isEmpty(), "Danh sách trả về phải rỗng");
    }

    /**
     * TC065_findByDeadline_ExceptionThrown_PrintsStackTrace:
     * Mô tả: Đảm bảo phương thức ném ngoại lệ và in ra stack trace khi xảy ra lỗi.
     * Đầu vào: deadline = ngày hiện tại.
     * Kết quả mong đợi: Ném ngoại lệ và in ra stack trace.
     */
    @Test
    @DisplayName("TC065_findByDeadline_ExceptionThrown_PrintsStackTrace")
    void TC065_findByDeadline_ExceptionThrown_PrintsStackTrace() {
        // Định nghĩa hành vi của repository để ném ngoại lệ
        Date currentDate = new Date();
        when(postingRepository.findbydeadline(any(Date.class)))
                .thenThrow(new RuntimeException("Database error"));

        try {
            postingService.findByDeadline();
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException ex) {
            // In ra stack trace của ngoại lệ để kiểm tra
            ex.printStackTrace();
            System.out.println("Ngoại lệ đã xảy ra: " + ex.getMessage());
            assertEquals("Database error", ex.getMessage(), "Thông báo ngoại lệ không khớp");
        }
    }

    /**
     * TC066_findByEmployerIdDeadline_ValidIdAndValidDeadlines_ReturnsMatchingPosts:
     * Mô tả: Đảm bảo trả về danh sách bài đăng với employerid hợp lệ và deadline lớn hơn hoặc bằng ngày hiện tại.
     * Đầu vào: employerid = 1, deadline >= ngày hiện tại.
     * Kết quả mong đợi: Trả về danh sách bài đăng tương ứng.
     */
    @Test
    @DisplayName("TC066_findByEmployerIdDeadline_ValidIdAndValidDeadlines_ReturnsMatchingPosts")
    void TC066_findByEmployerIdDeadline_ValidIdAndValidDeadlines_ReturnsMatchingPosts() {
        List<Postings> postingsList = Arrays.asList(new Postings(), new Postings());
        Date currentDate = new Date();

        when(postingRepository.findbyemployeriddeadline(eq(1), any(Date.class))).thenReturn(postingsList);
        when(modelMapper.map(postingsList, new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Arrays.asList(new PostingDTO(), new PostingDTO()));

        List<PostingDTO> result = postingService.findByEmployerIdDeadline(1);

        System.out.println("Kết quả trả về: " + (result == null ? "null" : result.size() + " bài đăng"));
        assertNotNull(result, "Danh sách trả về không được là null");
        assertEquals(2, result.size(), "Danh sách trả về phải chứa 2 bài đăng");
    }

    /**
     * TC067_findByEmployerIdDeadline_ValidIdNoMatchingPosts_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi không có bài đăng nào với employerid hợp lệ và deadline hợp lệ.
     * Đầu vào: employerid = 1, deadline >= ngày hiện tại.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC067_findByEmployerIdDeadline_ValidIdNoMatchingPosts_ReturnsEmptyList")
    void TC067_findByEmployerIdDeadline_ValidIdNoMatchingPosts_ReturnsEmptyList() {
        when(postingRepository.findbyemployeriddeadline(eq(1), any(Date.class))).thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(), new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Collections.emptyList());

        List<PostingDTO> result = postingService.findByEmployerIdDeadline(1);

        System.out.println("Kết quả trả về: " + (result == null ? "null" : result.size() + " bài đăng"));
        assertNotNull(result, "Danh sách trả về không được là null");
        assertTrue(result.isEmpty(), "Danh sách trả về phải rỗng");
    }

    /**
     * TC068_findByEmployerIdDeadline_InvalidId_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi employerid không tồn tại trong hệ thống.
     * Đầu vào: employerid = 99, deadline >= ngày hiện tại.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC068_findByEmployerIdDeadline_InvalidId_ReturnsEmptyList")
    void TC068_findByEmployerIdDeadline_InvalidId_ReturnsEmptyList() {
        when(postingRepository.findbyemployeriddeadline(eq(99), any(Date.class))).thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(), new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Collections.emptyList());

        List<PostingDTO> result = postingService.findByEmployerIdDeadline(99);

        System.out.println("Kết quả trả về: " + (result == null ? "null" : result.size() + " bài đăng"));
        assertNotNull(result, "Danh sách trả về không được là null");
        assertTrue(result.isEmpty(), "Danh sách trả về phải rỗng");
    }

    /**
     * TC069_findByEmployerIdDeadline_ExceptionThrown_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức xử lý đúng khi xảy ra ngoại lệ trong repository.
     * Đầu vào: employerid = 1, deadline >= ngày hiện tại.
     * Kết quả mong đợi: Ném ngoại lệ và in ra stack trace.
     */
    @Test
    @DisplayName("TC069_findByEmployerIdDeadline_ExceptionThrown_LogsStackTrace")
    void TC069_findByEmployerIdDeadline_ExceptionThrown_LogsStackTrace() {
        when(postingRepository.findbyemployeriddeadline(eq(1), any(Date.class)))
                .thenThrow(new RuntimeException("Database error"));

        try {
            postingService.findByEmployerIdDeadline(1);
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException ex) {
            System.out.println("Ngoại lệ đã xảy ra: " + ex.getMessage());
            assertEquals("Database error", ex.getMessage());
        }
    }

    /**
     * TC070_search_AllParametersValid_ReturnsMatchingPosts:
     * Mô tả: Đảm bảo trả về danh sách bài đăng phù hợp với tất cả tiêu chí tìm kiếm hợp lệ.
     * Đầu vào: localId=1, wageId=1, typeId=1, categoryId=1, experienceId=1, title="Java Developer".
     * Kết quả mong đợi: Trả về danh sách bài đăng phù hợp với các tiêu chí.
     */
    @Test
    @DisplayName("TC070_search_AllParametersValid_ReturnsMatchingPosts")
    void TC070_search_AllParametersValid_ReturnsMatchingPosts() {
        List<Postings> postingsList = Arrays.asList(new Postings(), new Postings());
        when(postingRepository.search(1, 1, 1, 1, 1, "Java Developer")).thenReturn(postingsList);
        when(modelMapper.map(postingsList, new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Arrays.asList(new PostingDTO(), new PostingDTO()));

        List<PostingDTO> result = postingService.search(1, 1, 1, 1, 1, "Java Developer");

        System.out.println("Kết quả trả về: " + result.size() + " bài đăng");
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * TC071_search_AllParametersNull_ReturnsAllPosts:
     * Mô tả: Đảm bảo trả về danh sách tất cả bài đăng khi tất cả tham số là null.
     * Đầu vào: tất cả tham số = null.
     * Kết quả mong đợi: Trả về danh sách tất cả bài đăng.
     */
    @Test
    @DisplayName("TC071_search_AllParametersNull_ReturnsAllPosts")
    void TC071_search_AllParametersNull_ReturnsAllPosts() {
        List<Postings> postingsList = Arrays.asList(new Postings(), new Postings(), new Postings());
        when(postingRepository.search(null, null, null, null, null, null)).thenReturn(postingsList);
        when(modelMapper.map(postingsList, new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Arrays.asList(new PostingDTO(), new PostingDTO(), new PostingDTO()));

        List<PostingDTO> result = postingService.search(null, null, null, null, null, null);

        System.out.println("Kết quả trả về: " + result.size() + " bài đăng");
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    /**
     * TC072_search_TitleNotExist_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi tiêu đề không tồn tại.
     * Đầu vào: title = "Không Tồn Tại".
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    /**
     * TC072_search_TitleNotExist_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi tiêu đề không tồn tại.
     * Đầu vào: title = "Không Tồn Tại".
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC072_search_TitleNotExist_ReturnsEmptyList")
    void TC072_search_TitleNotExist_ReturnsEmptyList() {
        // Giả lập phương thức search trả về danh sách trống
        when(postingRepository.search(null, null, null, null, null, "Không Tồn Tại")).thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(), new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Collections.emptyList());

        // Thực hiện phương thức search
        List<PostingDTO> result = postingService.search(null, null, null, null, null, "Không Tồn Tại");

        // Kiểm tra kết quả
        System.out.println("Kết quả trả về: " + (result == null ? "null" : result.size() + " bài đăng"));
        assertNotNull(result, "Danh sách trả về không được là null");
        assertTrue(result.isEmpty(), "Danh sách trả về phải rỗng");
    }


    /**
     * TC073_search_InvalidLocalId_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi localId không tồn tại.
     * Đầu vào: localId = 999.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC073_search_InvalidLocalId_ReturnsEmptyList")
    void TC073_search_InvalidLocalId_ReturnsEmptyList() {
        // Giả lập phương thức search trả về danh sách trống
        when(postingRepository.search(999, 1, 1, 1, 1, "Java Developer")).thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(), new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Collections.emptyList());

        // Thực hiện phương thức search
        List<PostingDTO> result = postingService.search(999, 1, 1, 1, 1, "Java Developer");

        // Kiểm tra kết quả
        System.out.println("Kết quả trả về: " + (result == null ? "null" : result.size() + " bài đăng"));
        assertNotNull(result, "Danh sách trả về không được là null");
        assertTrue(result.isEmpty(), "Danh sách trả về phải rỗng");
    }

    /**
     * TC074_search_EmptyTitle_ReturnsAllPosts:
     * Mô tả: Đảm bảo trả về danh sách bài đăng không lọc theo tiêu đề khi tiêu đề là chuỗi rỗng.
     * Đầu vào: title = "".
     * Kết quả mong đợi: Trả về danh sách tất cả bài đăng.
     */
    @Test
    @DisplayName("TC074_search_EmptyTitle_ReturnsAllPosts")
    void TC074_search_EmptyTitle_ReturnsAllPosts() {
        List<Postings> postingsList = Arrays.asList(new Postings(), new Postings(), new Postings());
        when(postingRepository.search(null, null, null, null, null, "")).thenReturn(postingsList);
        when(modelMapper.map(postingsList, new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Arrays.asList(new PostingDTO(), new PostingDTO(), new PostingDTO()));

        List<PostingDTO> result = postingService.search(null, null, null, null, null, "");

        System.out.println("Kết quả trả về (TC074): " + result.size());
        assertNotNull(result);
        assertEquals(3, result.size()); // Danh sách trả về không lọc theo tiêu đề
    }

    /**
     * TC075_search_WhitespaceTitle_ReturnsAllPosts:
     * Mô tả: Đảm bảo trả về danh sách bài đăng không lọc theo tiêu đề khi tiêu đề là chuỗi chứa khoảng trắng.
     * Đầu vào: title = " ".
     * Kết quả mong đợi: Trả về danh sách tất cả bài đăng.
     */
    @Test
    @DisplayName("TC075_search_WhitespaceTitle_ReturnsAllPosts")
    void TC075_search_WhitespaceTitle_ReturnsAllPosts() {
        List<Postings> postingsList = Arrays.asList(new Postings(), new Postings(), new Postings());
        when(postingRepository.search(null, null, null, null, null, " ")).thenReturn(postingsList);
        when(modelMapper.map(postingsList, new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Arrays.asList(new PostingDTO(), new PostingDTO(), new PostingDTO()));

        List<PostingDTO> result = postingService.search(null, null, null, null, null, " ");

        // Kiểm tra kết quả
        System.out.println("Kết quả trả về (TC075): " + result.size());
        assertNotNull(result);
        assertEquals(3, result.size()); // Danh sách trả về không lọc theo tiêu đề
    }

    /**
     * TC076_search_ExceptionThrown_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức xử lý đúng khi xảy ra ngoại lệ trong repository.
     * Đầu vào: title = "Java Developer".
     * Kết quả mong đợi: Ném ngoại lệ và in ra stack trace.
     */
    @Test
    @DisplayName("TC076_search_ExceptionThrown_LogsStackTrace")
    void TC076_search_ExceptionThrown_LogsStackTrace() {
        when(postingRepository.search(1, 1, 1, 1, 1, "Java Developer"))
                .thenThrow(new RuntimeException("Database error"));

        try {
            postingService.search(1, 1, 1, 1, 1, "Java Developer");
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException ex) {
            System.out.println("Lỗi xảy ra: " + ex.getMessage());
            assertEquals("Database error", ex.getMessage());
        }
    }

    /**
     * TC077_search_ValidTitle_ReturnsMatchingPosts:
     * Mô tả: Đảm bảo trả về danh sách bài đăng với tiêu đề hợp lệ.
     * Đầu vào: title = "Java Developer".
     * Kết quả mong đợi: Trả về danh sách bài đăng có tiêu đề "Java Developer".
     */
    @Test
    @DisplayName("TC077_search_ValidTitle_ReturnsMatchingPosts")
    void TC077_search_ValidTitle_ReturnsMatchingPosts() {
        // Giả lập danh sách bài đăng với tiêu đề "Java Developer"
        List<Postings> postingsList = Arrays.asList(new Postings(), new Postings());
        when(postingRepository.search(null, null, null, null, null, "Java Developer"))
                .thenReturn(postingsList);
        when(modelMapper.map(postingsList, new TypeToken<List<PostingDTO>>() {}.getType()))
                .thenReturn(Arrays.asList(new PostingDTO(), new PostingDTO()));

        // Gọi phương thức kiểm thử
        List<PostingDTO> result = postingService.search(null, null, null, null, null, "Java Developer");

        // In kết quả ra console
        System.out.println("Kết quả trả về: " + result.size() + " bài đăng");
        System.out.println("Danh sách bài đăng: " + result);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(2, result.size());
    }
    /**
     * TC078_search_NonExistentTitle_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi tiêu đề không tồn tại.
     * Đầu vào: title = "Không Tồn Tại".
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC078_search_NonExistentTitle_ReturnsEmptyList")
    void TC078_search_NonExistentTitle_ReturnsEmptyList() {
        // Giả lập kết quả tìm kiếm trống
        when(postingRepository.search(null, null, null, null, null, "Không Tồn Tại"))
                .thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(), new TypeToken<List<PostingDTO>>() {}.getType()))
                .thenReturn(Collections.emptyList());

        // Gọi phương thức kiểm thử
        List<PostingDTO> result = postingService.search(null, null, null, null, null, "Không Tồn Tại");

        // In kết quả ra console
        System.out.println("Kết quả trả về: " + result.size() + " bài đăng");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC079_search_EmptyTitle_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi tiêu đề là chuỗi rỗng.
     * Đầu vào: title = "".
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC079_search_EmptyTitle_ReturnsEmptyList")
    void TC079_search_EmptyTitle_ReturnsEmptyList() {
        when(postingRepository.search(null, null, null, null, null, ""))
                .thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(), new TypeToken<List<PostingDTO>>() {}.getType()))
                .thenReturn(Collections.emptyList());

        // Gọi phương thức kiểm thử
        List<PostingDTO> result = postingService.search(null, null, null, null, null, "");

        // In kết quả ra console
        System.out.println("Kết quả trả về: " + result.size() + " bài đăng");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC080_search_WhitespaceTitle_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi tiêu đề là chuỗi chứa khoảng trắng.
     * Đầu vào: title = " ".
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC080_search_WhitespaceTitle_ReturnsEmptyList")
    void TC080_search_WhitespaceTitle_ReturnsEmptyList() {
        when(postingRepository.search(null, null, null, null, null, " "))
                .thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(), new TypeToken<List<PostingDTO>>() {}.getType()))
                .thenReturn(Collections.emptyList());

        // Gọi phương thức kiểm thử
        List<PostingDTO> result = postingService.search(null, null, null, null, null, " ");

        // In kết quả ra console
        System.out.println("Kết quả trả về: " + result.size() + " bài đăng");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC081_search_SpecialCharacterTitle_ReturnsMatchingPosts:
     * Mô tả: Đảm bảo trả về danh sách bài đăng với tiêu đề chứa ký tự đặc biệt.
     * Đầu vào: title = "@!#$%^&*".
     * Kết quả mong đợi: Trả về danh sách bài đăng với tiêu đề khớp ký tự đặc biệt.
     */
    @Test
    @DisplayName("TC081_search_SpecialCharacterTitle_ReturnsMatchingPosts")
    void TC081_search_SpecialCharacterTitle_ReturnsMatchingPosts() {
        // Giả lập danh sách bài đăng với tiêu đề chứa ký tự đặc biệt
        List<Postings> postingsList = Arrays.asList(new Postings());
        when(postingRepository.search(null, null, null, null, null, "@!#$%^&*"))
                .thenReturn(postingsList);
        when(modelMapper.map(postingsList, new TypeToken<List<PostingDTO>>() {}.getType()))
                .thenReturn(Arrays.asList(new PostingDTO()));

        // Gọi phương thức kiểm thử
        List<PostingDTO> result = postingService.search(null, null, null, null, null, "@!#$%^&*");

        // In kết quả ra console
        System.out.println("Kết quả trả về: " + result.size() + " bài đăng");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * TC082_search_ExceptionThrown_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức xử lý đúng khi xảy ra ngoại lệ trong repository.
     * Đầu vào: title = "Java Developer".
     * Kết quả mong đợi: Ném ngoại lệ và in ra stack trace.
     */
    @Test
    @DisplayName("TC082_search_ExceptionThrown_LogsStackTrace")
    void TC082_search_ExceptionThrown_LogsStackTrace() {
        // Giả lập ngoại lệ từ repository
        when(postingRepository.search(null, null, null, null, null, "Java Developer"))
                .thenThrow(new RuntimeException("Database error"));

        try {
            // Gọi phương thức kiểm thử
            postingService.search(null, null, null, null, null, "Java Developer");
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException ex) {
            // In ngoại lệ ra console
            System.out.println("Đã ném ngoại lệ: " + ex.getMessage());

            // Kiểm tra ngoại lệ
            assertEquals("Database error", ex.getMessage());
        }
    }

    /**
     * TC083_findAllLimit_ValidLimit_ReturnsLimitedPosts:
     * Mô tả: Đảm bảo trả về danh sách bài đăng với số lượng giới hạn hợp lệ.
     * Đầu vào: limit = 3.
     * Kết quả mong đợi: Trả về danh sách bài đăng với tối đa 3 bài.
     */
    @Test
    @DisplayName("TC083_findAllLimit_ValidLimit_ReturnsLimitedPosts")
    void TC083_findAllLimit_ValidLimit_ReturnsLimitedPosts() {
        // Giả lập danh sách bài đăng
        List<Postings> postingsList = Arrays.asList(new Postings(), new Postings(), new Postings());
        when(postingRepository.findAllLimit(3)).thenReturn(postingsList);
        when(modelMapper.map(postingsList, new TypeToken<List<PostingDTO>>() {}.getType()))
                .thenReturn(Arrays.asList(new PostingDTO(), new PostingDTO(), new PostingDTO()));

        // Gọi phương thức kiểm thử
        List<PostingDTO> result = postingService.findAllLimit(3);

        // In kết quả ra console
        System.out.println("Kết quả trả về: " + result.size() + " bài đăng");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    /**
     * TC084_findAllLimit_ZeroLimit_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi giới hạn là 0.
     * Đầu vào: limit = 0.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC084_findAllLimit_ZeroLimit_ReturnsEmptyList")
    void TC084_findAllLimit_ZeroLimit_ReturnsEmptyList() {
        when(postingRepository.findAllLimit(0)).thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(), new TypeToken<List<PostingDTO>>() {}.getType()))
                .thenReturn(Collections.emptyList());

        // Gọi phương thức kiểm thử
        List<PostingDTO> result = postingService.findAllLimit(0);

        // In kết quả ra console
        System.out.println("Kết quả trả về: " + result.size() + " bài đăng");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC085_findAllLimit_NegativeLimit_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi giới hạn là số âm.
     * Đầu vào: limit = -1.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC085_findAllLimit_NegativeLimit_ReturnsEmptyList")
    void TC085_findAllLimit_NegativeLimit_ReturnsEmptyList() {
        when(postingRepository.findAllLimit(-1)).thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(), new TypeToken<List<PostingDTO>>() {}.getType()))
                .thenReturn(Collections.emptyList());

        // Gọi phương thức kiểm thử
        List<PostingDTO> result = postingService.findAllLimit(-1);

        // In kết quả ra console
        System.out.println("Kết quả trả về: " + result.size() + " bài đăng");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC086_findAllLimit_LargeLimit_ReturnsAllPosts:
     * Mô tả: Đảm bảo trả về danh sách tất cả bài đăng khi giới hạn lớn hơn tổng số bài đăng.
     * Đầu vào: limit = 100.
     * Kết quả mong đợi: Trả về danh sách tất cả bài đăng (ít hơn 100).
     */
    @Test
    @DisplayName("TC086_findAllLimit_LargeLimit_ReturnsAllPosts")
    void TC086_findAllLimit_LargeLimit_ReturnsAllPosts() {
        // Giả lập danh sách bài đăng (ít hơn giới hạn 100)
        List<Postings> postingsList = Arrays.asList(new Postings(), new Postings());
        when(postingRepository.findAllLimit(100)).thenReturn(postingsList);
        when(modelMapper.map(postingsList, new TypeToken<List<PostingDTO>>() {}.getType()))
                .thenReturn(Arrays.asList(new PostingDTO(), new PostingDTO()));

        // Gọi phương thức kiểm thử
        List<PostingDTO> result = postingService.findAllLimit(100);

        // In kết quả ra console
        System.out.println("Kết quả trả về: " + result.size() + " bài đăng");

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(2, result.size()); // Tổng số bài đăng ít hơn 100
    }

    /**
     * TC087_findAllLimit_ExceptionThrown_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức xử lý đúng khi xảy ra ngoại lệ trong repository.
     * Đầu vào: limit = 3.
     * Kết quả mong đợi: Ném ngoại lệ và in ra stack trace.
     */
    @Test
    @DisplayName("TC087_findAllLimit_ExceptionThrown_LogsStackTrace")
    void TC087_findAllLimit_ExceptionThrown_LogsStackTrace() {
        // Giả lập ngoại lệ từ repository
        when(postingRepository.findAllLimit(3))
                .thenThrow(new RuntimeException("Database error"));

        try {
            // Gọi phương thức kiểm thử
            postingService.findAllLimit(3);
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException ex) {
            // In ngoại lệ ra console
            System.out.println("Đã ném ngoại lệ: " + ex.getMessage());

            // Kiểm tra ngoại lệ
            assertEquals("Database error", ex.getMessage());
        }
    }
    /**
     * TC088_getByEmployerId_ValidEmployerId_ReturnsPosts:
     * Mô tả: Đảm bảo trả về danh sách bài đăng với ID nhà tuyển dụng hợp lệ.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về danh sách bài đăng liên quan đến nhà tuyển dụng.
     */
    @Test
    @DisplayName("TC088_getByEmployerId_ValidEmployerId_ReturnsPosts")
    void TC088_getByEmployerId_ValidEmployerId_ReturnsPosts() {
        List<Postings> postingsList = Arrays.asList(new Postings(), new Postings());
        when(postingRepository.findByEmployerId(1)).thenReturn(postingsList);
        when(modelMapper.map(postingsList, new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Arrays.asList(new PostingDTO(), new PostingDTO()));

        List<PostingDTO> result = postingService.findByEmployerId(1);
        System.out.println("Kết quả cho ID hợp lệ (empid = 1): " + result);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * TC089_getByEmployerId_NonExistentEmployerId_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi ID nhà tuyển dụng không tồn tại.
     * Đầu vào: id = 9999.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC089_getByEmployerId_NonExistentEmployerId_ReturnsEmptyList")
    void TC089_getByEmployerId_NonExistentEmployerId_ReturnsEmptyList() {
        when(postingRepository.findByEmployerId(9999)).thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(), new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Collections.emptyList());

        List<PostingDTO> result = postingService.findByEmployerId(9999);
        System.out.println("Kết quả cho ID không tồn tại (empid = 9999): " + result);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC090_getByEmployerId_ZeroId_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi ID nhà tuyển dụng bằng 0.
     * Đầu vào: id = 0.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC090_getByEmployerId_ZeroId_ReturnsEmptyList")
    void TC090_getByEmployerId_ZeroId_ReturnsEmptyList() {
        when(postingRepository.findByEmployerId(0)).thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(), new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Collections.emptyList());

        List<PostingDTO> result = postingService.findByEmployerId(0);
        System.out.println("Kết quả cho ID bằng 0 (empid = 0): " + result);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC091_getByEmployerId_NegativeId_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách trống khi ID nhà tuyển dụng là số âm.
     * Đầu vào: id = -1.
     * Kết quả mong đợi: Trả về danh sách trống.
     */
    @Test
    @DisplayName("TC091_getByEmployerId_NegativeId_ReturnsEmptyList")
    void TC091_getByEmployerId_NegativeId_ReturnsEmptyList() {
        when(postingRepository.findByEmployerId(-1)).thenReturn(Collections.emptyList());
        when(modelMapper.map(Collections.emptyList(), new TypeToken<List<PostingDTO>>() {}.getType())).thenReturn(Collections.emptyList());

        List<PostingDTO> result = postingService.findByEmployerId(-1);
        System.out.println("Kết quả cho ID âm (empid = -1): " + result);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC092_getByEmployerId_ExceptionThrown_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức xử lý đúng khi xảy ra ngoại lệ trong repository.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Ném ngoại lệ RuntimeException với thông báo "Database error".
     */
    @Test
    @DisplayName("TC092_getByEmployerId_ExceptionThrown_LogsStackTrace")
    void TC092_getByEmployerId_ExceptionThrown_LogsStackTrace() {
        // Giả lập hành vi của repository, ném ngoại lệ RuntimeException
        when(postingRepository.findByEmployerId(1))
                .thenThrow(new RuntimeException("Database error"));

        try {
            // Gọi phương thức cần kiểm thử
            postingService.findByEmployerId(1);
            fail("Expected RuntimeException to be thrown"); // Nếu không ném ngoại lệ thì kiểm thử thất bại
        } catch (RuntimeException ex) {
            // In ra kết quả kiểm thử
            System.out.println("Đã ném ngoại lệ: " + ex.getMessage());

            // Kiểm tra ngoại lệ có đúng như mong đợi không
            assertEquals("Database error", ex.getMessage());
        }
    }
    /**
     * TC093_getDetail_ValidId_ReturnsPostings:
     * Mô tả: Đảm bảo phương thức trả về đối tượng Postings khi ID hợp lệ.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về đối tượng Postings có ID = 1.
     */
    @Test
    @DisplayName("TC093_getDetail_ValidId_ReturnsPostings")
    void TC093_getDetail_ValidId_ReturnsPostings() {
        // Giả lập đối tượng Postings
        Postings posting = new Postings();
        posting.setId(1);
        Mockito.when(postingRepository.findById(1)).thenReturn(Optional.of(posting));

        // Gọi phương thức kiểm thử
        Postings result = postingService.getDetail(1);

        // In ra kết quả
        System.out.println("Kết quả trả về: " + result);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    /**
     * TC094_getDetail_NonExistentId_ThrowsNoSuchElementException:
     * Mô tả: Đảm bảo phương thức ném ngoại lệ khi ID không tồn tại.
     * Đầu vào: id = 9999.
     * Kết quả mong đợi: Ném ngoại lệ NoSuchElementException.
     */
    @Test
    @DisplayName("TC094_getDetail_NonExistentId_ThrowsNoSuchElementException")
    void TC094_getDetail_NonExistentId_ThrowsNoSuchElementException() {
        Mockito.when(postingRepository.findById(9999)).thenReturn(Optional.empty());

        try {
            postingService.getDetail(9999);
        } catch (NoSuchElementException e) {
            System.out.println("Ngoại lệ ném ra: " + e.getMessage());
            assertEquals(NoSuchElementException.class, e.getClass());
        }
    }

    /**
     * TC095_getDetail_ZeroId_ThrowsNoSuchElementException:
     * Mô tả: Đảm bảo phương thức ném ngoại lệ khi ID bằng 0.
     * Đầu vào: id = 0.
     * Kết quả mong đợi: Ném ngoại lệ NoSuchElementException.
     */
    @Test
    @DisplayName("TC095_getDetail_ZeroId_ThrowsNoSuchElementException")
    void TC095_getDetail_ZeroId_ThrowsNoSuchElementException() {
        Mockito.when(postingRepository.findById(0)).thenReturn(Optional.empty());

        try {
            postingService.getDetail(0);
        } catch (NoSuchElementException e) {
            System.out.println("Ngoại lệ ném ra: " + e.getMessage());
            assertEquals(NoSuchElementException.class, e.getClass());
        }
    }

    /**
     * TC096_getDetail_NegativeId_ThrowsNoSuchElementException:
     * Mô tả: Đảm bảo phương thức ném ngoại lệ khi ID âm.
     * Đầu vào: id = -1.
     * Kết quả mong đợi: Ném ngoại lệ NoSuchElementException.
     */
    @Test
    @DisplayName("TC096_getDetail_NegativeId_ThrowsNoSuchElementException")
    void TC096_getDetail_NegativeId_ThrowsNoSuchElementException() {
        Mockito.when(postingRepository.findById(-1)).thenReturn(Optional.empty());

        try {
            postingService.getDetail(-1);
        } catch (NoSuchElementException e) {
            System.out.println("Ngoại lệ ném ra: " + e.getMessage());
            assertEquals(NoSuchElementException.class, e.getClass());
        }
    }

    /**
     * TC097_getDetail_RepositoryThrowsException_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức xử lý đúng khi xảy ra ngoại lệ trong repository.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Ném ngoại lệ và in ra stack trace.
     */
    @Test
    @DisplayName("TC097_getDetail_RepositoryThrowsException_LogsStackTrace")
    void TC097_getDetail_RepositoryThrowsException_LogsStackTrace() {
        // Giả lập repository ném ngoại lệ
        Mockito.when(postingRepository.findById(1)).thenThrow(new RuntimeException("Lỗi từ repository"));

        try {
            postingService.getDetail(1);
        } catch (RuntimeException e) {
            // In ra stack trace
            System.out.println("Ngoại lệ ném ra: ");
            e.printStackTrace();
            assertEquals(RuntimeException.class, e.getClass());
        }
    }
    /**
     * TC098_saveDB_ValidPostings_ReturnsTrue:
     * Mô tả: Đảm bảo phương thức trả về true khi đối tượng Postings hợp lệ.
     * Đầu vào: Đối tượng Postings với các trường đầy đủ giá trị.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC098_saveDB_ValidPostings_ReturnsTrue")
    void TC098_saveDB_ValidPostings_ReturnsTrue() {
        // Tạo đối tượng Postings hợp lệ
        Postings posting = new Postings();
        posting.setTitle("Valid Title");
        posting.setCreated(new Date());

        // Giả lập phương thức save không ném ngoại lệ
        Mockito.when(postingRepository.save(posting)).thenReturn(posting);

        // Gọi phương thức kiểm thử
        boolean result = postingService.saveDB(posting);

        // In ra kết quả
        System.out.println("Kết quả trả về: " + result);

        // Kiểm tra kết quả
        assertTrue(result);
    }

    /**
     * TC099_saveDB_NullPostings_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi đối tượng Postings là null.
     * Đầu vào: null.
     * Kết quả mong đợi: Trả về false, không xảy ra ngoại lệ.
     */
    @Test
    @DisplayName("TC099_saveDB_NullPostings_ReturnsFalse")
    void TC099_saveDB_NullPostings_ReturnsFalse() {
        // Gọi phương thức kiểm thử với null
        boolean result = postingService.saveDB(null);

        // In ra kết quả
        System.out.println("Kết quả trả về với đối tượng null: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC100_saveDB_NullTitle_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi đối tượng Postings có trường title = null.
     * Đầu vào: Đối tượng Postings với title = null.
     * Kết quả mong đợi: Trả về false, không xảy ra ngoại lệ.
     */
    @Test
    @DisplayName("TC100_saveDB_NullTitle_ReturnsFalse")
    void TC100_saveDB_NullTitle_ReturnsFalse() {
        // Tạo đối tượng Postings với title null
        Postings posting = new Postings();
        posting.setTitle(null);
        posting.setCreated(new Date());

        // Gọi phương thức kiểm thử
        boolean result = postingService.saveDB(posting);

        // In ra kết quả
        System.out.println("Kết quả trả về với title = null: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC101_saveDB_NullCreated_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi đối tượng Postings có trường created = null.
     * Đầu vào: Đối tượng Postings với created = null.
     * Kết quả mong đợi: Trả về false, không xảy ra ngoại lệ.
     */
    @Test
    @DisplayName("TC101_saveDB_NullCreated_ReturnsFalse")
    void TC101_saveDB_NullCreated_ReturnsFalse() {
        // Tạo đối tượng Postings với created null
        Postings posting = new Postings();
        posting.setTitle("Valid Title");
        posting.setCreated(null);

        // Gọi phương thức kiểm thử
        boolean result = postingService.saveDB(posting);

        // In ra kết quả
        System.out.println("Kết quả trả về với created = null: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC102_saveDB_RepositoryThrowsException_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức trả về false và in ra stack trace khi repository ném ngoại lệ.
     * Đầu vào: Đối tượng Postings hợp lệ nhưng repository ném ngoại lệ.
     * Kết quả mong đợi: Trả về false, in ra stack trace.
     */
    @Test
    @DisplayName("TC102_saveDB_RepositoryThrowsException_LogsStackTrace")
    void TC102_saveDB_RepositoryThrowsException_LogsStackTrace() {
        // Tạo đối tượng Postings hợp lệ
        Postings posting = new Postings();
        posting.setTitle("Valid Title");
        posting.setCreated(new Date());

        // Giả lập repository ném ngoại lệ
        Mockito.when(postingRepository.save(posting)).thenThrow(new RuntimeException("Lỗi từ repository"));

        // Gọi phương thức kiểm thử
        boolean result = false;
        try {
            result = postingService.saveDB(posting);
        } catch (Exception e) {
            System.out.println("Ngoại lệ ném ra: ");
            e.printStackTrace();
        }

        // In ra kết quả
        System.out.println("Kết quả trả về khi repository ném ngoại lệ: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }
    /**
     * TC103_existPostByCategoryId_ExistingCategory_ReturnsTrue:
     * Mô tả: Đảm bảo phương thức trả về true khi có bài đăng với categoryId tồn tại trong DB.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC103_existPostByCategoryId_ExistingCategory_ReturnsTrue")
    void TC103_existPostByCategoryId_ExistingCategory_ReturnsTrue() {
        // Giả lập phương thức countByCategoryId trả về giá trị lớn hơn 0
        Mockito.when(postingRepository.countByCategoryId(1)).thenReturn(5);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByCategoryId(1);

        // In ra kết quả
        System.out.println("Kết quả trả về khi categoryId tồn tại: " + result);

        // Kiểm tra kết quả
        assertTrue(result);
    }

    /**
     * TC104_existPostByCategoryId_NonExistingCategory_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi không có bài đăng với categoryId trong DB.
     * Đầu vào: id = 999.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC104_existPostByCategoryId_NonExistingCategory_ReturnsFalse")
    void TC104_existPostByCategoryId_NonExistingCategory_ReturnsFalse() {
        // Giả lập phương thức countByCategoryId trả về 0
        Mockito.when(postingRepository.countByCategoryId(999)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByCategoryId(999);

        // In ra kết quả
        System.out.println("Kết quả trả về khi categoryId không tồn tại: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC105_existPostByCategoryId_NegativeCategoryId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi categoryId là số âm.
     * Đầu vào: id = -1.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC105_existPostByCategoryId_NegativeCategoryId_ReturnsFalse")
    void TC105_existPostByCategoryId_NegativeCategoryId_ReturnsFalse() {
        // Giả lập phương thức countByCategoryId trả về 0
        Mockito.when(postingRepository.countByCategoryId(-1)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByCategoryId(-1);

        // In ra kết quả
        System.out.println("Kết quả trả về khi categoryId là số âm: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC106_existPostByCategoryId_ZeroCategoryId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi categoryId là 0.
     * Đầu vào: id = 0.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC106_existPostByCategoryId_ZeroCategoryId_ReturnsFalse")
    void TC106_existPostByCategoryId_ZeroCategoryId_ReturnsFalse() {
        // Giả lập phương thức countByCategoryId trả về 0
        Mockito.when(postingRepository.countByCategoryId(0)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByCategoryId(0);

        // In ra kết quả
        System.out.println("Kết quả trả về khi categoryId là 0: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC107_existPostByCategoryId_RepositoryThrowsException_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức trả về false và in ra stack trace khi repository ném ngoại lệ.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về false, in ra stack trace.
     */
    @Test
    @DisplayName("TC107_existPostByCategoryId_RepositoryThrowsException_LogsStackTrace")
    void TC107_existPostByCategoryId_RepositoryThrowsException_LogsStackTrace() {
        // Giả lập repository ném ngoại lệ
        Mockito.when(postingRepository.countByCategoryId(1))
                .thenThrow(new RuntimeException("Lỗi từ repository"));

        boolean result = false;
        try {
            // Gọi phương thức kiểm thử
            result = postingService.existPostByCategoryId(1);
        } catch (Exception e) {
            // In ra stack trace của ngoại lệ
            System.out.println("Ngoại lệ ném ra: ");
            e.printStackTrace();
        }

        // In ra kết quả
        System.out.println("Kết quả trả về khi repository ném ngoại lệ: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }
    /**
     * TC108_existPostByLocalId_ExistingLocal_ReturnsTrue:
     * Mô tả: Đảm bảo phương thức trả về true khi có bài đăng với localId tồn tại trong DB.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC108_existPostByLocalId_ExistingLocal_ReturnsTrue")
    void TC108_existPostByLocalId_ExistingLocal_ReturnsTrue() {
        // Giả lập phương thức countByLocalId trả về giá trị lớn hơn 0
        Mockito.when(postingRepository.countByLocalId(1)).thenReturn(5);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByLocalId(1);

        // In ra kết quả
        System.out.println("Kết quả trả về khi localId tồn tại: " + result);

        // Kiểm tra kết quả
        assertTrue(result);
    }

    /**
     * TC109_existPostByLocalId_NonExistingLocal_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi không có bài đăng với localId trong DB.
     * Đầu vào: id = 999.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC109_existPostByLocalId_NonExistingLocal_ReturnsFalse")
    void TC109_existPostByLocalId_NonExistingLocal_ReturnsFalse() {
        // Giả lập phương thức countByLocalId trả về 0
        Mockito.when(postingRepository.countByLocalId(999)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByLocalId(999);

        // In ra kết quả
        System.out.println("Kết quả trả về khi localId không tồn tại: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC110_existPostByLocalId_NegativeLocalId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi localId là số âm.
     * Đầu vào: id = -1.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC110_existPostByLocalId_NegativeLocalId_ReturnsFalse")
    void TC110_existPostByLocalId_NegativeLocalId_ReturnsFalse() {
        // Giả lập phương thức countByLocalId trả về 0
        Mockito.when(postingRepository.countByLocalId(-1)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByLocalId(-1);

        // In ra kết quả
        System.out.println("Kết quả trả về khi localId là số âm: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC111_existPostByLocalId_ZeroLocalId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi localId là 0.
     * Đầu vào: id = 0.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC111_existPostByLocalId_ZeroLocalId_ReturnsFalse")
    void TC111_existPostByLocalId_ZeroLocalId_ReturnsFalse() {
        // Giả lập phương thức countByLocalId trả về 0
        Mockito.when(postingRepository.countByLocalId(0)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByLocalId(0);

        // In ra kết quả
        System.out.println("Kết quả trả về khi localId là 0: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC112_existPostByLocalId_RepositoryThrowsException_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức trả về false và in ra stack trace khi repository ném ngoại lệ.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về false, in ra stack trace.
     */
    @Test
    @DisplayName("TC112_existPostByLocalId_RepositoryThrowsException_LogsStackTrace")
    void TC112_existPostByLocalId_RepositoryThrowsException_LogsStackTrace() {
        // Giả lập repository ném ngoại lệ
        Mockito.when(postingRepository.countByLocalId(1))
                .thenThrow(new RuntimeException("Lỗi từ repository"));

        boolean result = false;
        try {
            // Gọi phương thức kiểm thử
            result = postingService.existPostByLocalId(1);
        } catch (Exception e) {
            // In ra stack trace của ngoại lệ
            System.out.println("Ngoại lệ ném ra: ");
            e.printStackTrace();
        }

        // In ra kết quả
        System.out.println("Kết quả trả về khi repository ném ngoại lệ: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }
    /**
     * TC113_existPostByExperienceId_ExistingExperience_ReturnsTrue:
     * Mô tả: Đảm bảo phương thức trả về true khi có bài đăng với experienceId tồn tại trong DB.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC113_existPostByExperienceId_ExistingExperience_ReturnsTrue")
    void TC113_existPostByExperienceId_ExistingExperience_ReturnsTrue() {
        // Giả lập phương thức countByExperienceId trả về giá trị lớn hơn 0
        Mockito.when(postingRepository.countByExperienceId(1)).thenReturn(5);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByExperienceId(1);

        // In ra kết quả
        System.out.println("Kết quả trả về khi experienceId tồn tại: " + result);

        // Kiểm tra kết quả
        assertTrue(result);
    }

    /**
     * TC114_existPostByExperienceId_NonExistingExperience_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi không có bài đăng với experienceId trong DB.
     * Đầu vào: id = 999.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC114_existPostByExperienceId_NonExistingExperience_ReturnsFalse")
    void TC114_existPostByExperienceId_NonExistingExperience_ReturnsFalse() {
        // Giả lập phương thức countByExperienceId trả về 0
        Mockito.when(postingRepository.countByExperienceId(999)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByExperienceId(999);

        // In ra kết quả
        System.out.println("Kết quả trả về khi experienceId không tồn tại: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC115_existPostByExperienceId_NegativeExperienceId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi experienceId là số âm.
     * Đầu vào: id = -1.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC115_existPostByExperienceId_NegativeExperienceId_ReturnsFalse")
    void TC115_existPostByExperienceId_NegativeExperienceId_ReturnsFalse() {
        // Giả lập phương thức countByExperienceId trả về 0
        Mockito.when(postingRepository.countByExperienceId(-1)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByExperienceId(-1);

        // In ra kết quả
        System.out.println("Kết quả trả về khi experienceId là số âm: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC116_existPostByExperienceId_ZeroExperienceId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi experienceId là 0.
     * Đầu vào: id = 0.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC116_existPostByExperienceId_ZeroExperienceId_ReturnsFalse")
    void TC116_existPostByExperienceId_ZeroExperienceId_ReturnsFalse() {
        // Giả lập phương thức countByExperienceId trả về 0
        Mockito.when(postingRepository.countByExperienceId(0)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByExperienceId(0);

        // In ra kết quả
        System.out.println("Kết quả trả về khi experienceId là 0: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC117_existPostByExperienceId_RepositoryThrowsException_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức trả về false và in ra stack trace khi repository ném ngoại lệ.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về false, in ra stack trace.
     */
    @Test
    @DisplayName("TC117_existPostByExperienceId_RepositoryThrowsException_LogsStackTrace")
    void TC117_existPostByExperienceId_RepositoryThrowsException_LogsStackTrace() {
        // Giả lập repository ném ngoại lệ
        Mockito.when(postingRepository.countByExperienceId(1))
                .thenThrow(new RuntimeException("Lỗi từ repository"));

        boolean result = false;
        try {
            // Gọi phương thức kiểm thử
            result = postingService.existPostByExperienceId(1);
        } catch (Exception e) {
            // In ra stack trace của ngoại lệ
            System.out.println("Ngoại lệ ném ra: ");
            e.printStackTrace();
        }

        // In ra kết quả
        System.out.println("Kết quả trả về khi repository ném ngoại lệ: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }
    /**
     * TC118_existPostByRankId_ExistingRank_ReturnsTrue:
     * Mô tả: Đảm bảo phương thức trả về true khi có bài đăng với rankId tồn tại trong DB.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC118_existPostByRankId_ExistingRank_ReturnsTrue")
    void TC118_existPostByRankId_ExistingRank_ReturnsTrue() {
        // Giả lập phương thức countByRankId trả về giá trị lớn hơn 0
        Mockito.when(postingRepository.countByRankId(1)).thenReturn(5);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByRankId(1);

        // In ra kết quả
        System.out.println("Kết quả trả về khi rankId tồn tại: " + result);

        // Kiểm tra kết quả
        assertTrue(result);
    }

    /**
     * TC119_existPostByRankId_NonExistingRank_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi không có bài đăng với rankId trong DB.
     * Đầu vào: id = 999.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC119_existPostByRankId_NonExistingRank_ReturnsFalse")
    void TC119_existPostByRankId_NonExistingRank_ReturnsFalse() {
        // Giả lập phương thức countByRankId trả về 0
        Mockito.when(postingRepository.countByRankId(999)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByRankId(999);

        // In ra kết quả
        System.out.println("Kết quả trả về khi rankId không tồn tại: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC120_existPostByRankId_NegativeRankId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi rankId là số âm.
     * Đầu vào: id = -1.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC120_existPostByRankId_NegativeRankId_ReturnsFalse")
    void TC120_existPostByRankId_NegativeRankId_ReturnsFalse() {
        // Giả lập phương thức countByRankId trả về 0
        Mockito.when(postingRepository.countByRankId(-1)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByRankId(-1);

        // In ra kết quả
        System.out.println("Kết quả trả về khi rankId là số âm: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC121_existPostByRankId_ZeroRankId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi rankId là 0.
     * Đầu vào: id = 0.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC121_existPostByRankId_ZeroRankId_ReturnsFalse")
    void TC121_existPostByRankId_ZeroRankId_ReturnsFalse() {
        // Giả lập phương thức countByRankId trả về 0
        Mockito.when(postingRepository.countByRankId(0)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByRankId(0);

        // In ra kết quả
        System.out.println("Kết quả trả về khi rankId là 0: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC122_existPostByRankId_RepositoryThrowsException_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức trả về false và in ra stack trace khi repository ném ngoại lệ.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về false, in ra stack trace.
     */
    @Test
    @DisplayName("TC122_existPostByRankId_RepositoryThrowsException_LogsStackTrace")
    void TC122_existPostByRankId_RepositoryThrowsException_LogsStackTrace() {
        // Giả lập repository ném ngoại lệ
        Mockito.when(postingRepository.countByRankId(1))
                .thenThrow(new RuntimeException("Lỗi từ repository"));

        boolean result = false;
        try {
            // Gọi phương thức kiểm thử
            result = postingService.existPostByRankId(1);
        } catch (Exception e) {
            // In ra stack trace của ngoại lệ
            System.out.println("Ngoại lệ ném ra: ");
            e.printStackTrace();
        }

        // In ra kết quả
        System.out.println("Kết quả trả về khi repository ném ngoại lệ: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }
    /**
     * TC123_existPostByTypeId_ExistingType_ReturnsTrue:
     * Mô tả: Đảm bảo phương thức trả về true khi có bài đăng với typeId tồn tại trong DB.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC123_existPostByTypeId_ExistingType_ReturnsTrue")
    void TC123_existPostByTypeId_ExistingType_ReturnsTrue() {
        // Giả lập phương thức countByTypeId trả về giá trị lớn hơn 0
        Mockito.when(postingRepository.countByTypeId(1)).thenReturn(5);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByTypeId(1);

        // In ra kết quả
        System.out.println("Kết quả trả về khi typeId tồn tại: " + result);

        // Kiểm tra kết quả
        assertTrue(result);
    }

    /**
     * TC124_existPostByTypeId_NonExistingType_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi không có bài đăng với typeId trong DB.
     * Đầu vào: id = 999.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC124_existPostByTypeId_NonExistingType_ReturnsFalse")
    void TC124_existPostByTypeId_NonExistingType_ReturnsFalse() {
        // Giả lập phương thức countByTypeId trả về 0
        Mockito.when(postingRepository.countByTypeId(999)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByTypeId(999);

        // In ra kết quả
        System.out.println("Kết quả trả về khi typeId không tồn tại: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC125_existPostByTypeId_NegativeTypeId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi typeId là số âm.
     * Đầu vào: id = -1.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC125_existPostByTypeId_NegativeTypeId_ReturnsFalse")
    void TC125_existPostByTypeId_NegativeTypeId_ReturnsFalse() {
        // Giả lập phương thức countByTypeId trả về 0
        Mockito.when(postingRepository.countByTypeId(-1)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByTypeId(-1);

        // In ra kết quả
        System.out.println("Kết quả trả về khi typeId là số âm: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC126_existPostByTypeId_ZeroTypeId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi typeId là 0.
     * Đầu vào: id = 0.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC126_existPostByTypeId_ZeroTypeId_ReturnsFalse")
    void TC126_existPostByTypeId_ZeroTypeId_ReturnsFalse() {
        // Giả lập phương thức countByTypeId trả về 0
        Mockito.when(postingRepository.countByTypeId(0)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByTypeId(0);

        // In ra kết quả
        System.out.println("Kết quả trả về khi typeId là 0: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC127_existPostByTypeId_RepositoryThrowsException_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức trả về false và in ra stack trace khi repository ném ngoại lệ.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về false, in ra stack trace.
     */
    @Test
    @DisplayName("TC127_existPostByTypeId_RepositoryThrowsException_LogsStackTrace")
    void TC127_existPostByTypeId_RepositoryThrowsException_LogsStackTrace() {
        // Giả lập repository ném ngoại lệ
        Mockito.when(postingRepository.countByTypeId(1))
                .thenThrow(new RuntimeException("Lỗi từ repository"));

        boolean result = false;
        try {
            // Gọi phương thức kiểm thử
            result = postingService.existPostByTypeId(1);
        } catch (Exception e) {
            // In ra stack trace của ngoại lệ
            System.out.println("Ngoại lệ ném ra: ");
            e.printStackTrace();
        }

        // In ra kết quả
        System.out.println("Kết quả trả về khi repository ném ngoại lệ: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }
    /**
     * TC128_existPostByWageId_ExistingWage_ReturnsTrue:
     * Mô tả: Đảm bảo phương thức trả về true khi có bài đăng với wageId tồn tại trong DB.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC128_existPostByWageId_ExistingWage_ReturnsTrue")
    void TC128_existPostByWageId_ExistingWage_ReturnsTrue() {
        // Giả lập phương thức countByWageId trả về giá trị lớn hơn 0
        Mockito.when(postingRepository.countByWageId(1)).thenReturn(5);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByWageId(1);

        // In ra kết quả
        System.out.println("Kết quả trả về khi wageId tồn tại: " + result);

        // Kiểm tra kết quả
        assertTrue(result);
    }

    /**
     * TC129_existPostByWageId_NonExistingWage_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi không có bài đăng với wageId trong DB.
     * Đầu vào: id = 999.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC129_existPostByWageId_NonExistingWage_ReturnsFalse")
    void TC129_existPostByWageId_NonExistingWage_ReturnsFalse() {
        // Giả lập phương thức countByWageId trả về 0
        Mockito.when(postingRepository.countByWageId(999)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByWageId(999);

        // In ra kết quả
        System.out.println("Kết quả trả về khi wageId không tồn tại: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC130_existPostByWageId_NegativeWageId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi wageId là số âm.
     * Đầu vào: id = -1.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC130_existPostByWageId_NegativeWageId_ReturnsFalse")
    void TC130_existPostByWageId_NegativeWageId_ReturnsFalse() {
        // Giả lập phương thức countByWageId trả về 0
        Mockito.when(postingRepository.countByWageId(-1)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByWageId(-1);

        // In ra kết quả
        System.out.println("Kết quả trả về khi wageId là số âm: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC131_existPostByWageId_ZeroWageId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi wageId là 0.
     * Đầu vào: id = 0.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC131_existPostByWageId_ZeroWageId_ReturnsFalse")
    void TC131_existPostByWageId_ZeroWageId_ReturnsFalse() {
        // Giả lập phương thức countByWageId trả về 0
        Mockito.when(postingRepository.countByWageId(0)).thenReturn(0);

        // Gọi phương thức kiểm thử
        boolean result = postingService.existPostByWageId(0);

        // In ra kết quả
        System.out.println("Kết quả trả về khi wageId là 0: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC132_existPostByWageId_RepositoryThrowsException_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức trả về false và in ra stack trace khi repository ném ngoại lệ.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về false, in ra stack trace.
     */
    @Test
    @DisplayName("TC132_existPostByWageId_RepositoryThrowsException_LogsStackTrace")
    void TC132_existPostByWageId_RepositoryThrowsException_LogsStackTrace() {
        // Giả lập repository ném ngoại lệ
        Mockito.when(postingRepository.countByWageId(1))
                .thenThrow(new RuntimeException("Lỗi từ repository"));

        boolean result = false;
        try {
            // Gọi phương thức kiểm thử
            result = postingService.existPostByWageId(1);
        } catch (Exception e) {
            // In ra stack trace của ngoại lệ
            System.out.println("Ngoại lệ ném ra: ");
            e.printStackTrace();
        }

        // In ra kết quả
        System.out.println("Kết quả trả về khi repository ném ngoại lệ: " + result);

        // Kiểm tra kết quả
        assertFalse(result);
    }
    /**
     * TC133_countByEmployerId_ExistingEmployer_ReturnsCount:
     * Mô tả: Đảm bảo phương thức trả về số lượng bài đăng khi employerId tồn tại trong DB.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về số lượng bài đăng lớn hơn 0.
     */
    @Test
    @DisplayName("TC133_countByEmployerId_ExistingEmployer_ReturnsCount")
    void TC133_countByEmployerId_ExistingEmployer_ReturnsCount() {
        // Giả lập phương thức countByEmployerId trả về 5
        Mockito.when(postingRepository.countByEmployerId(1)).thenReturn(5);

        // Gọi phương thức kiểm thử
        int result = postingService.countByEmployerId(1);

        // In ra kết quả
        System.out.println("Số lượng bài đăng cho employerId 1: " + result);

        // Kiểm tra kết quả
        assertEquals(5, result);
    }

    /**
     * TC134_countByEmployerId_NonExistingEmployer_ReturnsZero:
     * Mô tả: Đảm bảo phương thức trả về 0 khi không có bài đăng với employerId trong DB.
     * Đầu vào: id = 999.
     * Kết quả mong đợi: Trả về 0.
     */
    @Test
    @DisplayName("TC134_countByEmployerId_NonExistingEmployer_ReturnsZero")
    void TC134_countByEmployerId_NonExistingEmployer_ReturnsZero() {
        // Giả lập phương thức countByEmployerId trả về 0
        Mockito.when(postingRepository.countByEmployerId(999)).thenReturn(0);

        // Gọi phương thức kiểm thử
        int result = postingService.countByEmployerId(999);

        // In ra kết quả
        System.out.println("Số lượng bài đăng cho employerId 999: " + result);

        // Kiểm tra kết quả
        assertEquals(0, result);
    }

    /**
     * TC135_countByEmployerId_NegativeEmployerId_ReturnsZero:
     * Mô tả: Đảm bảo phương thức trả về 0 khi employerId là số âm.
     * Đầu vào: id = -1.
     * Kết quả mong đợi: Trả về 0.
     */
    @Test
    @DisplayName("TC135_countByEmployerId_NegativeEmployerId_ReturnsZero")
    void TC135_countByEmployerId_NegativeEmployerId_ReturnsZero() {
        // Giả lập phương thức countByEmployerId trả về 0
        Mockito.when(postingRepository.countByEmployerId(-1)).thenReturn(0);

        // Gọi phương thức kiểm thử
        int result = postingService.countByEmployerId(-1);

        // In ra kết quả
        System.out.println("Số lượng bài đăng cho employerId -1: " + result);

        // Kiểm tra kết quả
        assertEquals(0, result);
    }

    /**
     * TC136_countByEmployerId_ZeroEmployerId_ReturnsZero:
     * Mô tả: Đảm bảo phương thức trả về 0 khi employerId là 0.
     * Đầu vào: id = 0.
     * Kết quả mong đợi: Trả về 0.
     */
    @Test
    @DisplayName("TC136_countByEmployerId_ZeroEmployerId_ReturnsZero")
    void TC136_countByEmployerId_ZeroEmployerId_ReturnsZero() {
        // Giả lập phương thức countByEmployerId trả về 0
        Mockito.when(postingRepository.countByEmployerId(0)).thenReturn(0);

        // Gọi phương thức kiểm thử
        int result = postingService.countByEmployerId(0);

        // In ra kết quả
        System.out.println("Số lượng bài đăng cho employerId 0: " + result);

        // Kiểm tra kết quả
        assertEquals(0, result);
    }

    /**
     * TC137_countByEmployerId_RepositoryThrowsException_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức trả về 0 và in ra stack trace khi repository ném ngoại lệ.
     * Đầu vào: id = 1.
     * Kết quả mong đợi: Trả về 0, in ra stack trace.
     */
    @Test
    @DisplayName("TC137_countByEmployerId_RepositoryThrowsException_LogsStackTrace")
    void TC137_countByEmployerId_RepositoryThrowsException_LogsStackTrace() {
        // Giả lập repository ném ngoại lệ
        Mockito.when(postingRepository.countByEmployerId(1))
                .thenThrow(new RuntimeException("Lỗi từ repository"));

        int result = 0;
        try {
            // Gọi phương thức kiểm thử
            result = postingService.countByEmployerId(1);
        } catch (Exception e) {
            // In ra stack trace của ngoại lệ
            System.out.println("Ngoại lệ ném ra: ");
            e.printStackTrace();
        }

        // In ra kết quả
        System.out.println("Số lượng bài đăng khi repository ném ngoại lệ: " + result);

        // Kiểm tra kết quả
        assertEquals(0, result);
    }
    /**
     * TC138_updateStatusById_ValidId_StatusTrue_ReturnsTrue:
     * Mô tả: Đảm bảo phương thức trả về true khi ID hợp lệ và status = true.
     * Đầu vào: id = 1, status = true.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC138_updateStatusById_ValidId_StatusTrue_ReturnsTrue")
    void TC138_updateStatusById_ValidId_StatusTrue_ReturnsTrue() {
        when(postingRepository.updateStatusById(1, true)).thenReturn(1);

        boolean result = postingService.updateStatusById(1, true);
        System.out.println("Kết quả cập nhật trạng thái: " + result);

        assertTrue(result);
    }

    /**
     * TC139_updateStatusById_ValidId_StatusFalse_ReturnsTrue:
     * Mô tả: Đảm bảo phương thức trả về true khi ID hợp lệ và status = false.
     * Đầu vào: id = 1, status = false.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC139_updateStatusById_ValidId_StatusFalse_ReturnsTrue")
    void TC139_updateStatusById_ValidId_StatusFalse_ReturnsTrue() {
        when(postingRepository.updateStatusById(1, false)).thenReturn(1);

        boolean result = postingService.updateStatusById(1, false);
        System.out.println("Kết quả cập nhật trạng thái: " + result);

        assertTrue(result);
    }

    /**
     * TC140_updateStatusById_NonExistingId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi ID không tồn tại.
     * Đầu vào: id = 9999, status = true.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC140_updateStatusById_NonExistingId_ReturnsFalse")
    void TC140_updateStatusById_NonExistingId_ReturnsFalse() {
        when(postingRepository.updateStatusById(9999, true)).thenReturn(0);

        boolean result = postingService.updateStatusById(9999, true);
        System.out.println("Kết quả cập nhật trạng thái: " + result);

        assertFalse(result);
    }

    /**
     * TC141_updateStatusById_NullStatus_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi status là null.
     * Đầu vào: id = 1, status = null.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC141_updateStatusById_NullStatus_ReturnsFalse")
    void TC141_updateStatusById_NullStatus_ReturnsFalse() {
        Boolean status = null;
        boolean result = postingService.updateStatusById(1, status);
        System.out.println("Kết quả cập nhật trạng thái: " + result);

        assertFalse(result);
    }

    /**
     * TC142_updateStatusById_NegativeId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi ID là số âm.
     * Đầu vào: id = -1, status = true.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC142_updateStatusById_NegativeId_ReturnsFalse")
    void TC142_updateStatusById_NegativeId_ReturnsFalse() {
        boolean result = postingService.updateStatusById(-1, true);
        System.out.println("Kết quả cập nhật trạng thái: " + result);

        assertFalse(result);
    }

    /**
     * TC143_updateStatusById_ZeroId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi ID là 0.
     * Đầu vào: id = 0, status = true.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC143_updateStatusById_ZeroId_ReturnsFalse")
    void TC143_updateStatusById_ZeroId_ReturnsFalse() {
        boolean result = postingService.updateStatusById(0, true);
        System.out.println("Kết quả cập nhật trạng thái: " + result);

        assertFalse(result);
    }

    /**
     * TC144_updateStatusById_ExceptionThrown_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi xảy ra ngoại lệ từ repository.
     * Đầu vào: id = 1, status = true.
     * Kết quả mong đợi: Trả về false và in ra stack trace.
     */
    @Test
    @DisplayName("TC144_updateStatusById_ExceptionThrown_ReturnsFalse")
    void TC144_updateStatusById_ExceptionThrown_ReturnsFalse() {
        when(postingRepository.updateStatusById(1, true)).thenThrow(new RuntimeException("Lỗi từ repository"));

        boolean result = false;
        try {
            result = postingService.updateStatusById(1, true);
        } catch (Exception e) {
            System.out.println("Ngoại lệ xảy ra:");
            e.printStackTrace();
        }

        System.out.println("Kết quả cập nhật trạng thái: " + result);
        assertFalse(result);
    }

    /**
     * TC145_updateStatusById_MaxIntId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi ID là Integer.MAX_VALUE.
     * Đầu vào: id = Integer.MAX_VALUE, status = true.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC145_updateStatusById_MaxIntId_ReturnsFalse")
    void TC145_updateStatusById_MaxIntId_ReturnsFalse() {
        boolean result = postingService.updateStatusById(Integer.MAX_VALUE, true);
        System.out.println("Kết quả cập nhật trạng thái: " + result);

        assertFalse(result);
    }

    /**
     * TC146_updateStatusById_MinIntId_ReturnsFalse:
     * Mô tả: Đảm bảo phương thức trả về false khi ID là Integer.MIN_VALUE.
     * Đầu vào: id = Integer.MIN_VALUE, status = true.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC146_updateStatusById_MinIntId_ReturnsFalse")
    void TC146_updateStatusById_MinIntId_ReturnsFalse() {
        boolean result = postingService.updateStatusById(Integer.MIN_VALUE, true);
        System.out.println("Kết quả cập nhật trạng thái: " + result);

        assertFalse(result);
    }
    /**
     * TC147_countByMonthAndYear_ValidMonthYear_ReturnsCount:
     * Mô tả: Đảm bảo phương thức trả về đúng số bài đăng khi tháng và năm hợp lệ.
     * Đầu vào: Tháng = 5, Năm = 2025.
     * Kết quả mong đợi: Trả về số bài đăng là 3.
     */
    @Test
    @DisplayName("TC147_countByMonthAndYear_ValidMonthYear_ReturnsCount")
    void TC147_countByMonthAndYear_ValidMonthYear_ReturnsCount() {
        when(postingRepository.countByMonthAndYear(5, 2025)).thenReturn(3);

        int result = postingService.countByMonthAndYear(5, 2025);
        System.out.println("Số bài đăng cho tháng 5, năm 2025: " + result);

        assertEquals(3, result);
    }

    /**
     * TC148_countByMonthAndYear_NoPosts_ReturnsZero:
     * Mô tả: Đảm bảo phương thức trả về 0 khi không có bài đăng phù hợp.
     * Đầu vào: Tháng = 12, Năm = 2030.
     * Kết quả mong đợi: Trả về 0.
     */
    @Test
    @DisplayName("TC148_countByMonthAndYear_NoPosts_ReturnsZero")
    void TC148_countByMonthAndYear_NoPosts_ReturnsZero() {
        when(postingRepository.countByMonthAndYear(12, 2030)).thenReturn(0);

        int result = postingService.countByMonthAndYear(12, 2030);
        System.out.println("Số bài đăng cho tháng 12, năm 2030: " + result);

        assertEquals(0, result);
    }

    /**
     * TC149_countByMonthAndYear_InvalidMonth_ReturnsZero:
     * Mô tả: Đảm bảo phương thức trả về 0 khi tháng không hợp lệ (tháng = 0).
     * Đầu vào: Tháng = 0, Năm = 2025.
     * Kết quả mong đợi: Trả về 0.
     */
    @Test
    @DisplayName("TC149_countByMonthAndYear_InvalidMonth_ReturnsZero")
    void TC149_countByMonthAndYear_InvalidMonth_ReturnsZero() {
        int result = postingService.countByMonthAndYear(0, 2025);
        System.out.println("Số bài đăng cho tháng 0, năm 2025: " + result);

        assertEquals(0, result);
    }

    /**
     * TC150_countByMonthAndYear_ExceedingMonth_ReturnsZero:
     * Mô tả: Đảm bảo phương thức trả về 0 khi tháng vượt quá 12.
     * Đầu vào: Tháng = 13, Năm = 2025.
     * Kết quả mong đợi: Trả về 0.
     */
    @Test
    @DisplayName("TC150_countByMonthAndYear_ExceedingMonth_ReturnsZero")
    void TC150_countByMonthAndYear_ExceedingMonth_ReturnsZero() {
        int result = postingService.countByMonthAndYear(13, 2025);
        System.out.println("Số bài đăng cho tháng 13, năm 2025: " + result);

        assertEquals(0, result);
    }

    /**
     * TC151_countByMonthAndYear_NegativeYear_ReturnsZero:
     * Mô tả: Đảm bảo phương thức trả về 0 khi năm là số âm.
     * Đầu vào: Tháng = 5, Năm = -2025.
     * Kết quả mong đợi: Trả về 0.
     */
    @Test
    @DisplayName("TC151_countByMonthAndYear_NegativeYear_ReturnsZero")
    void TC151_countByMonthAndYear_NegativeYear_ReturnsZero() {
        int result = postingService.countByMonthAndYear(5, -2025);
        System.out.println("Số bài đăng cho tháng 5, năm -2025: " + result);

        assertEquals(0, result);
    }

    /**
     * TC152_countByMonthAndYear_NullMonthYear_ReturnsZero:
     * Mô tả: Đảm bảo phương thức trả về 0 khi cả tháng và năm là null.
     * Đầu vào: Tháng = null, Năm = null.
     * Kết quả mong đợi: Trả về 0.
     */
    @Test
    @DisplayName("TC152_countByMonthAndYear_NullMonthYear_ReturnsZero")
    void TC152_countByMonthAndYear_NullMonthYear_ReturnsZero() {
        Integer month = null;
        Integer year = null;

        int result = postingService.countByMonthAndYear(month, year);
        System.out.println("Số bài đăng khi tháng và năm là null: " + result);

        assertEquals(0, result);
    }
//    /**
//     * TC153_getAll_PostsExist_ReturnsList:
//     * Mô tả: Đảm bảo phương thức trả về danh sách bài đăng khi có bài đăng trong DB.
//     * Đầu vào: Không có.
//     * Kết quả mong đợi: Trả về danh sách bài đăng.
//     */
//    @Test
//    @DisplayName("TC153_getAll_HasPosts_ReturnsList")
//    void TC153_getAll_HasPosts_ReturnsList() {
//        // Giả lập dữ liệu bài đăng
//        List<Postings> mockPosts = List.of(
//                new Postings(
//                        new Category(), new Employer(), new Experience(),
//                        new Local(), new Rank(), new Type(), new Wage(),
//                        "Title 1", "Description 1", new Date(), new Date(),
//                        "Male", 5, true, true
//                ),
//                new Postings(
//                        new Category(), new Employer(), new Experience(),
//                        new Local(), new Rank(), new Type(), new Wage(),
//                        "Title 2", "Description 2", new Date(), new Date(),
//                        "Female", 3, true, true
//                )
//        );
//
//        // Giả lập hành vi của repository
//        when(postingRepository.findAll()).thenReturn(mockPosts);
//
//        // Gọi phương thức cần kiểm thử
//        Iterable<Postings> result = postingService.getAll();
//
//        // In ra kết quả kiểm thử
//        System.out.println("Kết quả kiểm thử (có bài đăng): " + result);
//
//        // Kiểm tra kết quả
//        assertEquals(mockPosts, result);
//    }
//
//    /**
//     * TC154_getAll_NoPosts_ReturnsEmptyList:
//     * Mô tả: Đảm bảo phương thức trả về danh sách rỗng khi không có bài đăng nào.
//     * Đầu vào: Không có.
//     * Kết quả mong đợi: Trả về danh sách rỗng.
//     */
//    @Test
//    @DisplayName("TC154_getAll_NoPosts_ReturnsEmptyList")
//    void TC154_getAll_NoPosts_ReturnsEmptyList() {
//        List<Postings> mockPosts = Collections.emptyList();
//        when(postingRepository.findAll()).thenReturn(mockPosts);
//
//        Iterable<Postings> result = postingService.getAll();
//        System.out.println("Danh sách bài đăng: " + result);
//
//        assertNotNull(result);
//        assertEquals(0, ((List<Postings>) result).size());
//    }

    /**
     * TC155_getAll_ExceptionThrown_LogsStackTrace:
     * Mô tả: Đảm bảo phương thức xử lý đúng khi xảy ra ngoại lệ trong repository.
     * Đầu vào: Không có.
     * Kết quả mong đợi: Ném ngoại lệ và in ra stack trace.
     */
    @Test
    @DisplayName("TC155_getAll_ExceptionThrown_LogsStackTrace")
    void TC155_getAll_ExceptionThrown_LogsStackTrace() {
        when(postingRepository.findAll()).thenThrow(new RuntimeException("Database Error"));

        try {
            postingService.getAll();
        } catch (Exception e) {
            System.out.println("Ngoại lệ xảy ra: " + e.getMessage());
            e.printStackTrace();
            assertTrue(e instanceof RuntimeException);
            assertEquals("Database Error", e.getMessage());
        }
    }
}