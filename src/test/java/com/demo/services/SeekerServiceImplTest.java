package com.demo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.demo.entities.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.demo.dtos.SeekerDTO;
import com.demo.entities.Seeker;
import com.demo.repositories.SeekerRepository;

class SeekerServiceImplTest {

    @Mock
    private SeekerRepository seekerRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private SeekerServiceImpl seekerService;

    private Seeker seeker;
    private SeekerDTO seekerDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seeker = new Seeker();
        seeker.setId(1);
        seeker.setFullname("John Doe");
        seeker.setPhone("0123456789");
        seeker.setDescription("Experienced software engineer.");
        seeker.setCvInformation("CV_Information.pdf");
        seeker.setStatus(true);
        seeker.setAvatar("avatar.png");

        // Giả lập đối tượng Account cho seeker
        Account account = new Account();
        account.setId(1);
        seeker.setAccount(account);

        seekerDTO = new SeekerDTO();
        seekerDTO.setId(1);
        seekerDTO.setFullName("John Doe");
        seekerDTO.setPhone("0123456789");
        seekerDTO.setDescription("Experienced software engineer.");
        seekerDTO.setCvInformation("CV_Information.pdf");
        seekerDTO.setStatus(true);
        seekerDTO.setAvatar("avatar.png");
        seekerDTO.setAccountID(1);
    }

    /**
     * TC182_findByAccountID_ValidID_ReturnsSeekerDTO:
     * Mô tả: Đảm bảo trả về đối tượng SeekerDTO khi account ID hợp lệ.
     * Đầu vào: account ID hợp lệ (1).
     * Kết quả mong đợi: Đối tượng SeekerDTO không null.
     */
    @Test
    @DisplayName("TC182_findByAccountID_ValidID_ReturnsSeekerDTO")
    void TC182_findByAccountID_ValidID_ReturnsSeekerDTO() {
        // Dữ liệu giả lập
        Seeker mockSeeker = new Seeker();

        // Định nghĩa hành vi của seekerRepository
        when(seekerRepository.findByAccountID(1)).thenReturn(mockSeeker);
        when(mapper.map(mockSeeker, SeekerDTO.class)).thenReturn(seekerDTO);

        // Thực thi phương thức
        SeekerDTO result = seekerService.findByAccountID(1);
        System.out.println(result);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(seekerDTO.getFullName(), result.getFullName());
        assertEquals(seekerDTO.getPhone(), result.getPhone());
    }

    /**
     * TC183_findByAccountID_InvalidID_ReturnsNull:
     * Mô tả: Đảm bảo trả về null khi account ID không hợp lệ.
     * Đầu vào: account ID không hợp lệ (999).
     * Kết quả mong đợi: null.
     */
    @Test
    @DisplayName("TC183_findByAccountID_InvalidID_ReturnsNull")
    void TC183_findByAccountID_InvalidID_ReturnsNull() {
        // Định nghĩa hành vi của seekerRepository
        when(seekerRepository.findByAccountID(999)).thenReturn(null);

        // Thực thi phương thức
        SeekerDTO result = seekerService.findByAccountID(999);
        System.out.println(result);

        // Kiểm tra kết quả
        assertNull(result);
    }

    /**
     * TC184_save_ValidSeekerDTO_ReturnsTrue:
     * Mô tả: Đảm bảo trả về true khi lưu đối tượng SeekerDTO hợp lệ.
     * Đầu vào: Đối tượng SeekerDTO hợp lệ.
     * Kết quả mong đợi: true.
     */
    @Test
    @DisplayName("TC184_save_ValidSeekerDTO_ReturnsTrue")
    void TC184_save_ValidSeekerDTO_ReturnsTrue() {
        // Dữ liệu giả lập
        SeekerDTO validSeekerDTO = new SeekerDTO();
        validSeekerDTO.setId(1);
        validSeekerDTO.setFullName("John Doe");
        validSeekerDTO.setPhone("0123456789");
        validSeekerDTO.setDescription("Experienced software engineer.");
        validSeekerDTO.setCvInformation("CV_Information.pdf");
        validSeekerDTO.setStatus(true);
        validSeekerDTO.setAvatar("avatar.png");
        validSeekerDTO.setAccountID(1);

        Seeker mockSeeker = new Seeker();
        when(mapper.map(validSeekerDTO, Seeker.class)).thenReturn(mockSeeker);
        when(seekerRepository.save(mockSeeker)).thenReturn(mockSeeker);

        // Thực thi phương thức
        boolean result = seekerService.save(validSeekerDTO);
        System.out.println(result);

        // Kiểm tra kết quả
        assertTrue(result);
    }

    /**
     * TC185_save_NullSeekerDTO_ReturnsFalse:
     * Mô tả: Đảm bảo trả về false khi lưu đối tượng SeekerDTO là null.
     * Đầu vào: Đối tượng SeekerDTO null.
     * Kết quả mong đợi: false.
     */
    @Test
    @DisplayName("TC185_save_NullSeekerDTO_ReturnsFalse")
    void TC185_save_NullSeekerDTO_ReturnsFalse() {
        // Thực thi phương thức với SeekerDTO null
        boolean result = seekerService.save(null);
        System.out.println(result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC186_save_InvalidSeekerDTO_ReturnsFalse:
     * Mô tả: Đảm bảo trả về false khi lưu đối tượng SeekerDTO không hợp lệ.
     * Đầu vào: Đối tượng SeekerDTO không hợp lệ.
     * Kết quả mong đợi: false.
     */
    @Test
    @DisplayName("TC186_save_InvalidSeekerDTO_ReturnsFalse")
    void TC186_save_InvalidSeekerDTO_ReturnsFalse() {
        // Dữ liệu giả lập với thông tin không hợp lệ (phone rỗng)
        SeekerDTO invalidSeekerDTO = new SeekerDTO();
        invalidSeekerDTO.setId(1);
        invalidSeekerDTO.setFullName("John Doe");
        invalidSeekerDTO.setPhone(""); // Số điện thoại không hợp lệ
        invalidSeekerDTO.setDescription("Experienced software engineer.");
        invalidSeekerDTO.setCvInformation("CV_Information.pdf");
        invalidSeekerDTO.setStatus(true);
        invalidSeekerDTO.setAvatar("avatar.png");
        invalidSeekerDTO.setAccountID(1);

        // Giả lập mapper và save bị lỗi
        Seeker mockSeeker = new Seeker();
        when(mapper.map(invalidSeekerDTO, Seeker.class)).thenReturn(mockSeeker);
        when(seekerRepository.save(mockSeeker)).thenThrow(new IllegalArgumentException("Invalid data"));

        // Thực thi phương thức
        boolean result = seekerService.save(invalidSeekerDTO);
        System.out.println(result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC187_delete_ValidID_ReturnsTrue:
     * Mô tả: Đảm bảo trả về true khi xóa Seeker với ID hợp lệ.
     * Đầu vào: ID hợp lệ (1).
     * Kết quả mong đợi: true.
     */
    @Test
    @DisplayName("TC187_delete_ValidID_ReturnsTrue")
    void TC187_delete_ValidID_ReturnsTrue() {
        // Dữ liệu giả lập
        int validId = 1;
        Seeker mockSeeker = new Seeker();
        mockSeeker.setId(validId);

        // Giả lập hành vi tìm kiếm và xóa
        when(seekerRepository.findById(validId)).thenReturn(Optional.of(mockSeeker));
        doNothing().when(seekerRepository).delete(mockSeeker);

        // Thực thi phương thức
        boolean result = seekerService.delete(validId);
        System.out.println(result);

        // Kiểm tra kết quả
        assertTrue(result);
    }

    /**
     * TC188_delete_NonExistingID_ReturnsFalse:
     * Mô tả: Đảm bảo trả về false khi xóa Seeker với ID không tồn tại.
     * Đầu vào: ID không tồn tại (999).
     * Kết quả mong đợi: false.
     */
    @Test
    @DisplayName("TC188_delete_NonExistingID_ReturnsFalse")
    void TC188_delete_NonExistingID_ReturnsFalse() {
        // Dữ liệu giả lập
        when(seekerRepository.findById(999)).thenReturn(Optional.empty());

        // Thực thi phương thức
        boolean result = seekerService.delete(999);
        System.out.println(result);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    /**
     * TC189_delete_InvalidID_ReturnsFalse:
     * Mô tả: Đảm bảo trả về false khi xóa Seeker với ID là số âm hoặc 0.
     * Đầu vào: ID không hợp lệ (-1).
     * Kết quả mong đợi: false.
     */
    @Test
    @DisplayName("TC189_delete_InvalidID_ReturnsFalse")
    void TC189_delete_InvalidID_ReturnsFalse() {
        // Thực thi phương thức với ID không hợp lệ
        boolean resultNegative = seekerService.delete(-1);
        boolean resultZero = seekerService.delete(0);
        System.out.println("Result with -1: " + resultNegative);
        System.out.println("Result with 0: " + resultZero);

        // Kiểm tra kết quả
        assertFalse(resultNegative);
        assertFalse(resultZero);
    }

    /**
     * TC190_findAll_HasData_ReturnsList:
     * Mô tả: Đảm bảo trả về danh sách SeekerDTO khi có dữ liệu trong DB.
     * Đầu vào: Không có.
     * Kết quả mong đợi: Danh sách SeekerDTO không rỗng.
     */
    @Test
    @DisplayName("TC190_findAll_HasData_ReturnsList")
    void TC190_findAll_HasData_ReturnsList() {
        // Dữ liệu giả lập
        List<Seeker> mockSeekers = new ArrayList<>();
        Seeker mockSeeker = new Seeker();
        mockSeeker.setId(1);
        mockSeeker.setFullname("John Doe");
        mockSeekers.add(mockSeeker);

        List<SeekerDTO> mockSeekerDTOs = new ArrayList<>();
        SeekerDTO mockSeekerDTO = new SeekerDTO();
        mockSeekerDTO.setId(1);
        mockSeekerDTO.setFullName("John Doe");
        mockSeekerDTOs.add(mockSeekerDTO);

        // Giả lập hành vi của repository và mapper
        when(seekerRepository.findAll()).thenReturn(mockSeekers);
        when(mapper.map(mockSeekers, new TypeToken<List<SeekerDTO>>() {}.getType())).thenReturn(mockSeekerDTOs);

        // Thực thi phương thức
        List<SeekerDTO> result = seekerService.findAll();
        System.out.println(result);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
    }

    /**
     * TC191_findAll_NoData_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách rỗng khi không có dữ liệu trong DB.
     * Đầu vào: Không có.
     * Kết quả mong đợi: Danh sách rỗng.
     */
    @Test
    @DisplayName("TC191_findAll_NoData_ReturnsEmptyList")
    void TC191_findAll_NoData_ReturnsEmptyList() {
        // Dữ liệu giả lập (danh sách rỗng)
        List<Seeker> mockSeekers = new ArrayList<>();
        List<SeekerDTO> mockSeekerDTOs = new ArrayList<>();

        // Giả lập hành vi của repository và mapper
        when(seekerRepository.findAll()).thenReturn(mockSeekers);
        when(mapper.map(mockSeekers, new TypeToken<List<SeekerDTO>>() {}.getType())).thenReturn(mockSeekerDTOs);

        // Thực thi phương thức
        List<SeekerDTO> result = seekerService.findAll();
        System.out.println(result);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC192_findbyusername_ValidUsername_ReturnsSeekerDTO:
     * Mô tả: Đảm bảo trả về đối tượng SeekerDTO khi username hợp lệ.
     * Đầu vào: username = "john_doe".
     * Kết quả mong đợi: Đối tượng SeekerDTO không null.
     */
    @Test
    @DisplayName("TC192_findbyusername_ValidUsername_ReturnsSeekerDTO")
    void TC192_findbyusername_ValidUsername_ReturnsSeekerDTO() {
        when(seekerRepository.findByusername("johndoe")).thenReturn(seeker);
        when(mapper.map(seeker, SeekerDTO.class)).thenReturn(seekerDTO);

        SeekerDTO result = seekerService.findbyusername("johndoe");

        System.out.println("Kết quả: " + result);
        assertNotNull(result);
    }

    /**
     * TC193_findbyusername_NonExistingUsername_ReturnsNull:
     * Mô tả: Đảm bảo trả về null khi username không tồn tại.
     * Đầu vào: username = "unknown_user".
     * Kết quả mong đợi: null.
     */
    @Test
    @DisplayName("TC193_findbyusername_NonExistingUsername_ReturnsNull")
    void TC193_findbyusername_NonExistingUsername_ReturnsNull() {
        when(seekerRepository.findByusername("notexist")).thenReturn(null);

        SeekerDTO result = seekerService.findbyusername("notexist");

        System.out.println("Kết quả: " + result);
        assertNull(result);
    }

    /**
     * TC194_findbyusername_EmptyUsername_ReturnsNull:
     * Mô tả: Đảm bảo trả về null khi username là chuỗi rỗng.
     * Đầu vào: username = "".
     * Kết quả mong đợi: null.
     */
    @Test
    @DisplayName("TC194_findbyusername_EmptyUsername_ReturnsNull")
    void TC194_findbyusername_EmptyUsername_ReturnsNull() {
        SeekerDTO result = seekerService.findbyusername("");

        System.out.println("Kết quả: " + result);
        assertNull(result);
    }

    /**
     * TC195_findbyusername_NullUsername_ReturnsNull:
     * Mô tả: Đảm bảo trả về null khi username là null.
     * Đầu vào: username = null.
     * Kết quả mong đợi: null.
     */
    @Test
    @DisplayName("TC195_findbyusername_NullUsername_ReturnsNull")
    void TC195_findbyusername_NullUsername_ReturnsNull() {
        SeekerDTO result = seekerService.findbyusername(null);

        System.out.println("Kết quả: " + result);
        assertNull(result);
    }

    // Test tìm seeker với id hợp lệ
    @Test
    @DisplayName("TC196 - Tìm Seeker với id hợp lệ")
    void TC196_findById_ValidId_ReturnsSeeker() {
        // Mô tả: Tìm Seeker với id hợp lệ
        int validId = 1;
        Seeker mockSeeker = new Seeker();
        mockSeeker.setId(validId);
        mockSeeker.setFullname("John Doe");

        // Giả lập hành vi của seekerRepository
        Mockito.when(seekerRepository.findById(validId)).thenReturn(Optional.of(mockSeeker));

        // Gọi phương thức cần kiểm thử
        SeekerDTO result = seekerService.findbyid(validId);

        // Xác minh kết quả trả về
        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
    }

    // Test tìm seeker với id không tồn tại
    @Test
    @DisplayName("TC197 - Tìm Seeker với id không tồn tại")
    void TC197_findById_NonExistentId_ReturnsNull() {
        when(seekerRepository.findById(99)).thenReturn(Optional.empty());

        SeekerDTO result = seekerService.findbyid(99);

        System.out.println("Kết quả: " + result);
        assertNull(result);
    }

    // Test tìm seeker với id là số âm hoặc 0
    @Test
    @DisplayName("TC198 - Tìm Seeker với id là số âm hoặc 0")
    void TC198_findById_NegativeOrZeroId_ReturnsNull() {
        SeekerDTO resultNegative = seekerService.findbyid(-1);
        SeekerDTO resultZero = seekerService.findbyid(0);

        System.out.println("Kết quả với id -1: " + resultNegative);
        System.out.println("Kết quả với id 0: " + resultZero);

        assertNull(resultNegative);
        assertNull(resultZero);
    }

    // Phương thức kiểm thử findbyFullname trong SeekerServiceImplTest
    @Test
    void TC199_findbyFullname_ValidFullname_ReturnsSeekerDTOList() {
        // Mô tả: Tìm Seeker với fullname hợp lệ
        System.out.println("TC199_findbyFullname_ValidFullname_ReturnsSeekerDTOList");

        String validFullname = "John Doe";
        List<Seeker> mockSeekers = new ArrayList<>();
        Seeker seeker1 = new Seeker();
        seeker1.setFullname("John Doe");
        mockSeekers.add(seeker1);

        // Giả lập hành vi của seekerRepository
        Mockito.when(seekerRepository.findByfullname(validFullname)).thenReturn(mockSeekers);

        // Gọi phương thức cần kiểm thử
        List<SeekerDTO> result = seekerService.findbyFullname(validFullname);

        // In ra kết quả kiểm thử
        System.out.println("Kết quả nhận được: " + result);

        // Xác minh kết quả trả về
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("John Doe", result.get(0).getFullName());
    }

    @Test
    void TC200_findbyFullname_NonExistentFullname_ReturnsNull() {
        // Mô tả: Tìm Seeker với fullname không tồn tại
        String nonExistentFullname = "Non Existent";

        // Giả lập hành vi của seekerRepository
        Mockito.when(seekerRepository.findByfullname(nonExistentFullname)).thenReturn(null);

        // Gọi phương thức cần kiểm thử
        List<SeekerDTO> result = seekerService.findbyFullname(nonExistentFullname);

        // In ra kết quả kiểm thử
        System.out.println("Kết quả nhận được: " + result);

        // Xác minh kết quả trả về
        assertNull(result);
    }

    @Test
    void TC201_findbyFullname_EmptyString_ReturnsNull() {
        // Mô tả: Tìm Seeker với fullname là chuỗi rỗng
        System.out.println("TC201_findbyFullname_EmptyString_ReturnsNull");

        String emptyFullname = "";

        // Giả lập hành vi của seekerRepository
        Mockito.when(seekerRepository.findByfullname(emptyFullname)).thenReturn(null);

        // Gọi phương thức cần kiểm thử
        List<SeekerDTO> result = seekerService.findbyFullname(emptyFullname);

        // In ra kết quả kiểm thử
        System.out.println("Kết quả nhận được: " + result);

        // Xác minh kết quả trả về
        assertNull(result);
    }

    @Test
    void TC202_findbyFullname_NullFullname_ReturnsNull() {
        // Mô tả: Tìm Seeker với fullname là null
        System.out.println("TC202_findbyFullname_NullFullname_ReturnsNull");

        String nullFullname = null;

        // Giả lập hành vi của seekerRepository
        Mockito.when(seekerRepository.findByfullname(nullFullname)).thenReturn(null);

        // Gọi phương thức cần kiểm thử
        List<SeekerDTO> result = seekerService.findbyFullname(nullFullname);

        // In ra kết quả kiểm thử
        System.out.println("Kết quả nhận được: " + result);

        // Xác minh kết quả trả về
        assertNull(result);
    }

}
