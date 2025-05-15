package com.demo.services;

import com.demo.entities.Rank;
import com.demo.repositories.RankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankServiceImplTest {

    @Mock
    private RankRepository rankRepository;

    @InjectMocks
    private RankServiceImpl rankService;

    private Rank rank;

    @BeforeEach
    void setUp() {
        rank = new Rank();
        rank.setId(1);
        rank.setName("Junior");
        rank.setStatus(true);
    }

    /**
     * TC156_find_ValidId_ReturnsRank:
     * Mô tả: Đảm bảo tìm thấy Rank khi ID hợp lệ.
     * Đầu vào: ID = 1.
     * Kết quả mong đợi: Trả về đối tượng Rank.
     */
    @Test
    @DisplayName("TC156_find_ValidId_ReturnsRank")
    void TC156_find_ValidId_ReturnsRank() {
        when(rankRepository.findById(1)).thenReturn(Optional.of(rank));
        Rank result = rankService.find(1);
        System.out.println(result);
        assertNotNull(result);
        assertEquals("Junior", result.getName());
    }

    /**
     * TC157_find_NonExistentId_ReturnsNull:
     * Mô tả: Đảm bảo trả về null khi ID không tồn tại.
     * Đầu vào: ID = 999.
     * Kết quả mong đợi: Trả về null.
     */
    @Test
    @DisplayName("TC157_find_NonExistentId_ReturnsNull")
    void TC157_findById_InvalidId_ThrowsNoSuchElementException() {
        when(rankRepository.findById(9999)).thenReturn(Optional.empty());
        System.out.println("TC157 - Không tìm thấy Rank với ID 9999");
        assertThrows(NoSuchElementException.class, () -> rankService.find(9999));
    }

    /**
     * TC158_find_NegativeId_ReturnsNull:
     * Mô tả: Đảm bảo trả về null khi ID là số âm.
     * Đầu vào: ID = -1.
     * Kết quả mong đợi: Trả về null.
     */
    @Test
    @DisplayName("TC158_find_NegativeId_ReturnsNull")
    void TC158_find_NegativeId_ReturnsNull() {
        when(rankRepository.findById(-1)).thenReturn(Optional.empty());
        System.out.println("TC158 - Không tìm thấy Rank với ID -1");
        assertThrows(NoSuchElementException.class, () -> rankService.find(-1));
    }

    /**
     * TC159_find_ZeroId_ReturnsNull:
     * Mô tả: Đảm bảo trả về null khi ID = 0.
     * Đầu vào: ID = 0.
     * Kết quả mong đợi: Trả về null.
     */
    @Test
    @DisplayName("TC159_find_ZeroId_ReturnsNull")
    void TC159_find_ZeroId_ReturnsNull() {
        when(rankRepository.findById(0)).thenReturn(Optional.empty());
        System.out.println("TC159 - Không tìm thấy Rank với ID 0");
        assertThrows(NoSuchElementException.class, () -> rankService.find(0));
    }

    /**
     * TC160_find_ExceptionThrown_LogsStackTrace:
     * Mô tả: Đảm bảo xử lý đúng khi xảy ra ngoại lệ trong repository.
     * Đầu vào: ID = 1.
     * Kết quả mong đợi: Ném ngoại lệ.
     */
    @Test
    @DisplayName("TC160_find_ExceptionThrown_LogsStackTrace")
    void TC160_find_ExceptionThrown_LogsStackTrace() {
        when(rankRepository.findById(1)).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> rankService.find(1));
    }

    /**
     * TC161_findAll_HasData_ReturnsList:
     * Mô tả: Đảm bảo trả về danh sách Rank khi có dữ liệu.
     * Đầu vào: Không có.
     * Kết quả mong đợi: Danh sách chứa Rank.
     */
    @Test
    @DisplayName("TC161_findAll_HasData_ReturnsList")
    void TC161_findAll_HasData_ReturnsList() {
        when(rankRepository.findAll()).thenReturn(Arrays.asList(rank));
        Iterable<Rank> result = rankService.findAll();
        System.out.println(result);
        assertTrue(result.iterator().hasNext());
    }

    /**
     * TC162_findAll_NoData_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách rỗng khi không có dữ liệu.
     * Đầu vào: Không có.
     * Kết quả mong đợi: Danh sách rỗng.
     */
    @Test
    @DisplayName("TC162_findAll_NoData_ReturnsEmptyList")
    void TC162_findAll_NoData_ReturnsEmptyList() {
        when(rankRepository.findAll()).thenReturn(List.of());
        Iterable<Rank> result = rankService.findAll();
        System.out.println(result);
        assertFalse(result.iterator().hasNext());
    }

    /**
     * TC163_findAll_ExceptionThrown_LogsStackTrace:
     * Mô tả: Đảm bảo xử lý đúng khi xảy ra ngoại lệ trong repository.
     * Đầu vào: Không có.
     * Kết quả mong đợi: Ném ngoại lệ.
     */
    @Test
    @DisplayName("TC163_findAll_ExceptionThrown_LogsStackTrace")
    void TC163_findAll_ExceptionThrown_LogsStackTrace() {
        when(rankRepository.findAll()).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> rankService.findAll());
    }
    /**
     * TC164_save_ValidRank_ReturnsTrue:
     * Mô tả: Đảm bảo lưu Rank thành công với đối tượng Rank hợp lệ.
     * Đầu vào: Rank hợp lệ.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC164_save_ValidRank_ReturnsTrue")
    void TC164_save_ValidRank_ReturnsTrue() {
        when(rankRepository.save(any(Rank.class))).thenReturn(rank);
        boolean result = rankService.save(rank);
        System.out.println(result);
        assertTrue(result);
    }

    /**
     * TC165_save_NullRank_ReturnsFalse:
     * Mô tả: Đảm bảo trả về false khi Rank null.
     * Đầu vào: Rank = null.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC165_save_NullRank_ReturnsFalse")
    void TC165_save_NullRank_ReturnsFalse() {
        boolean result = rankService.save(null);
        System.out.println(result);
        assertFalse(result);
    }

    /**
     * TC166_save_ExceptionThrown_LogsStackTrace:
     * Mô tả: Đảm bảo xử lý đúng khi xảy ra ngoại lệ trong repository.
     * Đầu vào: Rank hợp lệ.
     * Kết quả mong đợi: Trả về false và in ra stack trace.
     */
    @Test
    @DisplayName("TC166_save_ExceptionThrown_LogsStackTrace")
    void TC166_save_ExceptionThrown_LogsStackTrace() {
        doThrow(RuntimeException.class).when(rankRepository).save(any(Rank.class));
        boolean result = rankService.save(rank);
        System.out.println(result);
        assertFalse(result);
    }

    /**
     * TC167_delete_ValidId_ReturnsTrue:
     * Mô tả: Đảm bảo xóa Rank thành công với ID hợp lệ.
     * Đầu vào: ID = 1.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC167_delete_ValidId_ReturnsTrue")
    void TC167_delete_ValidId_ReturnsTrue() {
        when(rankRepository.findById(1)).thenReturn(Optional.of(rank));
        boolean result = rankService.delete(1);
        System.out.println(result);
        assertTrue(result);
    }

    /**
     * TC168_delete_NonExistentId_ReturnsFalse:
     * Mô tả: Đảm bảo trả về false khi xóa Rank không tồn tại.
     * Đầu vào: ID = 999.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC168_delete_NonExistentId_ReturnsFalse")
    void TC168_delete_NonExistentId_ReturnsFalse() {
        when(rankRepository.findById(999)).thenReturn(Optional.empty());
        boolean result = rankService.delete(999);
        System.out.println(result);
        assertFalse(result);
    }

    /**
     * TC169_delete_NegativeId_ReturnsFalse:
     * Mô tả: Đảm bảo trả về false khi ID là số âm.
     * Đầu vào: ID = -1.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC169_delete_NegativeId_ReturnsFalse")
    void TC169_delete_NegativeId_ReturnsFalse() {
        boolean result = rankService.delete(-1);
        System.out.println(result);
        assertFalse(result);
    }

    /**
     * TC170_delete_ZeroId_ReturnsFalse:
     * Mô tả: Đảm bảo trả về false khi ID = 0.
     * Đầu vào: ID = 0.
     * Kết quả mong đợi: Trả về false.
     */
    @Test
    @DisplayName("TC170_delete_ZeroId_ReturnsFalse")
    void TC170_delete_ZeroId_ReturnsFalse() {
        boolean result = rankService.delete(0);
        System.out.println(result);
        assertFalse(result);
    }

    /**
     * TC171_delete_ExceptionThrown_LogsStackTrace:
     * Mô tả: Đảm bảo xử lý đúng khi xảy ra ngoại lệ trong repository.
     * Đầu vào: ID = 1.
     * Kết quả mong đợi: Trả về false và in ra stack trace.
     */
    @Test
    @DisplayName("TC171_delete_ExceptionThrown_LogsStackTrace")
    void TC171_delete_ExceptionThrown_LogsStackTrace() {
        doThrow(RuntimeException.class).when(rankRepository).delete(any(Rank.class));
        boolean result = rankService.delete(1);
        System.out.println(result);
        assertFalse(result);
    }

    /**
     * TC172_exists_ValidNameAndId_ReturnsTrue:
     * Mô tả: Đảm bảo kiểm tra tồn tại với tên và ID hợp lệ.
     * Đầu vào: name = "Junior", ID = 1.
     * Kết quả mong đợi: Trả về true.
     */
    @Test
    @DisplayName("TC172_exists_ValidNameAndId_ReturnsTrue")
    void TC172_exists_ValidNameAndId_ReturnsTrue() {
        when(rankRepository.exists("Junior", 1)).thenReturn(1);
        boolean result = rankService.exists("Junior", 1);
        System.out.println(result);
        assertTrue(result);
    }

    /**
     * TC177_exists_ExceptionThrown_LogsStackTrace:
     * Mô tả: Đảm bảo xử lý đúng khi xảy ra ngoại lệ trong repository.
     * Đầu vào: name = "Junior", ID = 1.
     * Kết quả mong đợi: Ném ngoại lệ.
     */
    @Test
    @DisplayName("TC177_exists_ExceptionThrown_LogsStackTrace")
    void TC177_exists_ExceptionThrown_LogsStackTrace() {
        when(rankRepository.exists(anyString(), anyInt())).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> rankService.exists("Junior", 1));
    }

    /**
     * TC178_findAllByStatus_True_ReturnsList:
     * Mô tả: Đảm bảo trả về danh sách Rank với status = true.
     * Đầu vào: status = true.
     * Kết quả mong đợi: Danh sách Rank.
     */
    @Test
    @DisplayName("TC178_findAllByStatus_True_ReturnsList")
    void TC178_findAllByStatus_True_ReturnsList() {
        when(rankRepository.findAllByStatus(true)).thenReturn(Arrays.asList(rank));
        List<Rank> result = rankService.findAllByStatus(true);
        System.out.println(result);
        assertFalse(result.isEmpty());
    }
    /**
     * TC179_findAllByStatus_False_ReturnsList:
     * Mô tả: Đảm bảo trả về danh sách Rank với status = false.
     * Đầu vào: status = false.
     * Kết quả mong đợi: Danh sách Rank.
     */
    @Test
    @DisplayName("TC179_findAllByStatus_False_ReturnsList")
    void TC179_findAllByStatus_False_ReturnsList() {
        when(rankRepository.findAllByStatus(false)).thenReturn(Arrays.asList(rank));
        List<Rank> result = rankService.findAllByStatus(false);
        System.out.println(result);
        assertFalse(result.isEmpty());
    }

    /**
     * TC180_findAllByStatus_Null_ReturnsEmptyList:
     * Mô tả: Đảm bảo trả về danh sách rỗng khi status là null.
     * Đầu vào: status = null.
     * Kết quả mong đợi: Danh sách rỗng.
     */
    @Test
    @DisplayName("TC180_findAllByStatus_Null_ReturnsEmptyList")
    void TC180_findAllByStatus_Null_ReturnsEmptyList() {
        List<Rank> result = rankService.findAllByStatus(null);
        System.out.println(result);
        assertTrue(result.isEmpty());
    }

    /**
     * TC181_findAllByStatus_ExceptionThrown_LogsStackTrace:
     * Mô tả: Đảm bảo xử lý đúng khi xảy ra ngoại lệ trong repository.
     * Đầu vào: status = true.
     * Kết quả mong đợi: Ném ngoại lệ và in ra stack trace.
     */
    @Test
    @DisplayName("TC181_findAllByStatus_ExceptionThrown_LogsStackTrace")
    void TC181_findAllByStatus_ExceptionThrown_LogsStackTrace() {
        when(rankRepository.findAllByStatus(true)).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> rankService.findAllByStatus(true));
    }
}
