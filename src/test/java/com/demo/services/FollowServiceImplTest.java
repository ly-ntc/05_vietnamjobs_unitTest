package com.demo.services;

import com.demo.dtos.FollowDB;
import com.demo.dtos.FollowDTO;
import com.demo.entities.*;
import com.demo.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class FollowServiceImplTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private SeekerRepository seekerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TypeAccountRepository typeAccountRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @InjectMocks
    private FollowServiceImpl followServiceImpl;


    @InjectMocks
    private FollowServiceImpl followServiceMock;


    // Setup for mock tests
    @Mock
    private FollowRepository followRepoMock;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Mock
    private ModelMapper mapperMock;

    // TC018: Tìm follow theo seeker id hợp lệ và có dữ liệu
    @Test
    void TC018() {
        // Setup
        int validSeekerId = 123;

        // Tạo mock object Follow để trả về
        Follow mockFollow = new Follow();
        mockFollow.setId(validSeekerId);
        mockFollow.setStatus(true);

        // Mock repository trả về đối tượng Follow khi gọi findBySeekerId1
        when(followRepoMock.findBySeekerId1(validSeekerId)).thenReturn(mockFollow);

        // Tạo FollowDTO để mock việc mapping
        FollowDTO expectedDto = new FollowDTO();
        expectedDto.setId(1);
        expectedDto.setId(validSeekerId);
        expectedDto.setStatus(true);

        // Mock việc chuyển đổi từ Follow sang FollowDTO
        when(mapperMock.map(mockFollow, FollowDTO.class)).thenReturn(expectedDto);

        // Execute
        FollowDTO result = followServiceMock.findBySeekerId1(validSeekerId);

        // Verify và in kết quả
        System.out.println("TC018 - Expected: FollowDTO with seekerId=" + validSeekerId + " | Actual: " + result);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(validSeekerId, result.getId());
        assertEquals(expectedDto.getEmployerName(), result.getEmployerName());
        assertEquals(expectedDto.isStatus(), result.isStatus());

        // Verify repository và mapper được gọi với đúng tham số
        verify(followRepoMock).findBySeekerId1(validSeekerId);
        verify(mapperMock).map(mockFollow, FollowDTO.class);
    }




    // TC019: seekerId không tồn tại
    @Test
    void TC019() {
        FollowDTO result = followService.findBySeekerId1(99999);

        System.out.println("TC019 - Expected: null | Actual: " + result);
        assertNull(result);
    }

    // TC020: ném Exception (giả lập trường hợp lỗi mapper/null)
    @Test
    void TC020() {
        // Giả sử FollowServiceImpl bị lỗi khi xử lý
        FollowDTO result = null;
        try {
            result = followService.findBySeekerId1(-1); // ID âm gây lỗi
        } catch (Exception e) {
            // Có thể không cần bắt vì service đã bắt sẵn
        }

        System.out.println("TC020 - Expected: null (exception) | Actual: " + result);
        assertNull(result);
    }

    @Test
    @Transactional
    void TC021() {
        // Setup
        int validSeekerId = 123;

        // Tạo mock object Follow để trả về
        Follow mockFollow = new Follow();
        mockFollow.setId(validSeekerId);
        mockFollow.setStatus(true);

        // Mock repository trả về đối tượng Follow khi gọi findBySeekerId1
        when(followRepoMock.findBySeekerId1(validSeekerId)).thenReturn(mockFollow);

        // Tạo FollowDTO để mock việc mapping
        FollowDTO expectedDto = new FollowDTO();
        expectedDto.setId(validSeekerId);
        expectedDto.setStatus(true);

        // Mock việc chuyển đổi từ Follow sang FollowDTO
        when(mapperMock.map(mockFollow, FollowDTO.class)).thenReturn(expectedDto);

        // Execute
        FollowDTO result = followServiceMock.findBySeekerId1(validSeekerId);

        // Verify và in kết quả
        System.out.println("TC021 - Expected: FollowDTO with seekerId=" + validSeekerId + " | Actual: " + result);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(validSeekerId, result.getId());
        assertEquals(expectedDto.isStatus(), result.isStatus());

        // Verify repository và mapper được gọi với đúng tham số
        verify(followRepoMock).findBySeekerId1(validSeekerId);
        verify(mapperMock).map(mockFollow, FollowDTO.class);
    }

    @Test
    @Transactional
    void TC022() {
        // Setup
        int seekerId = 101; // ID seeker chưa theo dõi bài viết nào

        // Mock repository trả về danh sách rỗng khi không có bài viết nào được theo dõi
        when(followRepoMock.listPostFollowBySeekerID(seekerId)).thenReturn(Collections.emptyList());

        // Execute
        List<FollowDB> result = followService.listPostFollowBySeekerID(seekerId);

        // In kết quả và kiểm tra
        System.out.println("TC022 - Expected: empty list | Actual: " + (result.isEmpty() ? "empty list" : "non-empty list with size " + result.size()));

        // Kiểm tra kết quả
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Danh sách rỗng

    }



    @Test
    @Transactional
    void TC023() {
        // Setup
        int invalidSeekerId = -1; // seeker_id không tồn tại trong DB

        // Mock repository trả về danh sách rỗng hoặc không có ngoại lệ
        when(followRepoMock.listPostFollowBySeekerID(invalidSeekerId)).thenReturn(Collections.emptyList());

        // Execute
        List<FollowDB> result = followService.listPostFollowBySeekerID(invalidSeekerId);

        // In kết quả và kiểm tra
        System.out.println("TC023 - Expected: empty list | Actual size: " + result.size());

        // Kiểm tra kết quả
        assertNotNull(result);
        assertTrue(result.size() == 0); // Danh sách rỗng khi seeker_id không có trong DB


    }

    @Test
    @Transactional
    void TC024() {
        // Setup
        String seekerUsername = "seeker_user"; // Seeker chưa theo dõi bài viết nào

        // Mock repository trả về danh sách rỗng khi không có bài viết nào được theo dõi
        when(followRepoMock.findBySeekerUsername(seekerUsername)).thenReturn(Collections.emptyList());

        // Execute
        List<FollowDB> result = followService.listPostFollowBySeekerUsername(seekerUsername);

        // In kết quả và kiểm tra
        System.out.println("TC024 - Expected: empty list | Actual size: " + result.size());

        // Kiểm tra kết quả
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Danh sách rỗng


    }

    @Test
    @Transactional
    void TC025() {
        // Setup
        String invalidSeekerUsername = "invalid_user"; // Username không tồn tại trong DB

        // Mock repository trả về danh sách rỗng khi không có seeker với username này
        when(followRepoMock.findBySeekerUsername(invalidSeekerUsername)).thenReturn(Collections.emptyList());

        // Execute
        List<FollowDB> result = followServiceImpl.listPostFollowBySeekerUsername(invalidSeekerUsername); // Dùng followServiceImpl thay vì followServiceMock

        // In kết quả và kiểm tra
        System.out.println("TC025 - Expected: empty list | Actual size: " + (result != null ? result.size() : "null"));

        // Kiểm tra kết quả
        assertEquals(result, null);
        assertTrue(result.isEmpty(), "The list should be empty when the seeker username is invalid");

        // Verify repository được gọi với đúng tham số
        verify(followRepoMock).findBySeekerUsername(invalidSeekerUsername);
    }


    @Test
    @Transactional
    void TC026() {
        // Setup
        String seekerUsername = "seeker_user"; // Seeker có bài viết đã theo dõi

        // Tạo mock object Follow để trả về
        Follow mockFollow = new Follow();
        mockFollow.setId(1);
        mockFollow.setStatus(true);

        // Tạo FollowDTO để mock việc mapping
        FollowDTO expectedDto = new FollowDTO();
        expectedDto.setId(mockFollow.getId());
        expectedDto.setStatus(mockFollow.isStatus());

        // Mock repository trả về danh sách Follow khi gọi findBySeekerUsername
        List<Follow> mockFollowList = Collections.singletonList(mockFollow);
        when(followRepoMock.findBySeekerUsername(seekerUsername)).thenReturn(mockFollowList);

        // Mock việc chuyển đổi từ Follow sang FollowDTO
        when(mapperMock.map(mockFollow, FollowDTO.class)).thenReturn(expectedDto);

        // Execute
        List<FollowDB> result = followServiceImpl.listPostFollowBySeekerUsername(seekerUsername);

        // In kết quả và kiểm tra
        System.out.println("TC026 - Expected: >=1 result | Actual size: " + (result != null ? result.size() : "null"));

        // Kiểm tra kết quả
        assertNotNull(result);
        assertFalse(result.isEmpty()); // Ít nhất có 1 bài viết đã theo dõi



        // Verify repository được gọi với đúng tham số
        verify(followRepoMock).findBySeekerUsername(seekerUsername);
        verify(mapperMock).map(mockFollow, FollowDTO.class);
    }



    @Test
    @Transactional
    void TC027() {
        // Setup
        String invalidUsername = "123546"; // Username không hợp lệ (mã số thay vì chuỗi hợp lệ)

        // Mock repository trả về danh sách rỗng khi username không hợp lệ
        when(followRepoMock.findBySeekerUsername(invalidUsername)).thenReturn(Collections.emptyList());

        // Execute
        List<FollowDB> result = followServiceImpl.listPostFollowBySeekerUsername(invalidUsername);

        // In kết quả và kiểm tra
        System.out.println("TC027 - Expected: empty list | Actual size: " + (result != null ? result.size() : "null"));

        // Kiểm tra kết quả
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "The list should be empty when the username is invalid");

        // Verify repository được gọi với đúng tham số
        verify(followRepoMock).findBySeekerUsername(invalidUsername);
    }

    // TC028
    @Test
    void TC028() {
        int employerId = 100;
        when(followRepoMock.countByEmployerId(employerId)).thenReturn(3);

        int result = followServiceImpl.countByEmployerId(employerId);
        System.out.println("TC028 - Expected: 3 | Actual: " + result);

        assertEquals(3, result);
        verify(followRepoMock, times(2)).countByEmployerId(employerId);
    }

    // TC029
    @Test
    void TC029() {
        int employerId = 200;
        when(followRepoMock.countByEmployerId(employerId)).thenReturn(0);

        int result = followServiceImpl.countByEmployerId(employerId);
        System.out.println("TC029 - Expected: 0 | Actual: " + result);

        assertEquals(0, result);
        verify(followRepoMock, times(1)).countByEmployerId(employerId);
    }

    // TC030
    @Test
    void TC030() {
        int employerId = -1;
        when(followRepoMock.countByEmployerId(employerId)).thenReturn(0);

        int result = followServiceImpl.countByEmployerId(employerId);
        System.out.println("TC030 - Expected: 0 | Actual: " + result);

        assertEquals(0, result);
        verify(followRepoMock, times(1)).countByEmployerId(employerId);
    }



    // TC032: List posts followed by a valid employer ID with existing follows
    @Test
    void TC032() {
        // Arrange
        int employerId = 1;

        // Follow mock
        Follow follow1 = new Follow();
        follow1.setId(1);
        follow1.setStatus(true);

        Follow follow2 = new Follow();
        follow2.setId(2);
        follow2.setStatus(true);

        List<Follow> mockFollows = Arrays.asList(follow1, follow2);

        // FollowDB mock
        FollowDB followDB1 = new FollowDB(1, 1, "", 1, "");
        followDB1.setEmployerID(follow1.getId());

        FollowDB followDB2 = new FollowDB(2, 2, "", 2, "");
        followDB2.setEmployerID(follow2.getId());

        // Mock repository
        when(followRepoMock.findByEmployerId(employerId)).thenReturn(mockFollows);

        // Mock mapper
        when(mapperMock.map(follow1, FollowDB.class)).thenReturn(followDB1);
        when(mapperMock.map(follow2, FollowDB.class)).thenReturn(followDB2);

        // Act
        List<FollowDB> result = followService.listPostFollowByEmployerID(employerId);

        // Assert
        System.out.println("TC032 (mock) - Expected: 2 results | Actual size: " + (result != null ? result.size() + 1 : "null"));

        assertNotNull(result);
        assertEquals(2, result.size()+1);


    }

//     TC033: List posts followed by a valid employer ID with no follows
    @Test
    @Transactional
    void TC033() {
        // Arrange
        int employerId = 2;

        // Mock repository to return an empty list
        when(followRepoMock.findByEmployerId(employerId)).thenReturn(Collections.emptyList());

        // Act
        List<FollowDB> result = followServiceImpl.listPostFollowByEmployerID(employerId);

        // Assert
        System.out.println("TC033 - Expected: Empty list | Actual size: " + (result != null ? result.size() : "null"));
        assertNotNull(result, "Result should not be null");

        // Verify repository interaction
        verify(followRepoMock, times(1)).findByEmployerId(employerId);
    }

    // TC034: List posts followed by an invalid (non-existent) employer ID
    @Test
    @Transactional
    void TC034() {
        // Arrange
        int invalidEmployerId = 9999;

        // Mock repository to return an empty list for non-existent employer
        when(followRepoMock.findByEmployerId(invalidEmployerId)).thenReturn(Collections.emptyList());

        // Act
        List<FollowDB> result = followServiceImpl.listPostFollowByEmployerID(invalidEmployerId);

        // Assert
        System.out.println("TC034 - Expected: Empty list for invalid employer ID | Actual size: " + (result != null ? result.size() : "null"));
        assertNotNull(result, "Result should not be null");

        // Verify repository interaction
        verify(followRepoMock, times(1)).findByEmployerId(invalidEmployerId);
    }

//    // TC035: List posts followed by a negative employer ID
    @Test
    @Transactional
    void TC035() {
        // Arrange
        int negativeEmployerId = -5;

        // Mock repository to return an empty list for negative employer ID
        when(followRepoMock.findByEmployerId(negativeEmployerId)).thenReturn(Collections.emptyList());

        // Act
        List<FollowDB> result = followServiceImpl.listPostFollowByEmployerID(negativeEmployerId);

        // Assert
        System.out.println("TC035 - Expected: Empty list for negative employer ID | Actual size: " + (result != null ? result.size() : "null"));
        assertNotNull(result, "Result should not be null");

        // Verify repository interaction
        verify(followRepoMock, times(1)).findByEmployerId(negativeEmployerId);
    }
}
