package MangerApplyTest;
import com.demo.controllers.employer.AjaxEmployerController;
import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.services.ApplicationHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.util.MimeTypeUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class AjaxEmployerController_ViewCVTest {
    @InjectMocks
    private AjaxEmployerController controller;

    @Mock
    private ApplicationHistoryService applicationHistoryService;

    @Captor
    private ArgumentCaptor<ApplicationHistoryDTO> dtoCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * ✅ Test Case 1:
     * Mục tiêu: Kiểm tra khi appID hợp lệ, DTO được cập nhật status = 1 và trả về đúng.
     */
    @Test
    void TC45_testViewCV_WithValidInput_ShouldUpdateStatusAndReturnDTO() {
        int seekerID = 10;
        int appID = 20;

        // Giả lập dữ liệu trả về từ service
        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setId(appID);
        dto.setStatus(0);

        when(applicationHistoryService.findByID(appID)).thenReturn(dto);

        // Gọi hàm
        ApplicationHistoryDTO result = controller.viewCV(seekerID, appID);

        // In kết quả mong muốn và thực tế
        System.out.println("✅ Mong muốn: ID = " + appID + ", Status = 1");
        System.out.println("📤 Thực tế: ID = " + result.getId() + ", Status = " + result.getStatus());

        // Kiểm tra
        assertNotNull(result);
        assertEquals(appID, result.getId());
        assertEquals(1, result.getStatus());

        // Kiểm tra phương thức save đã gọi với DTO có status = 1
        verify(applicationHistoryService).save(dtoCaptor.capture());
        assertEquals(1, dtoCaptor.getValue().getStatus());
    }

    /**
     * ✅ Test Case 2:
     * Mục tiêu: Kiểm tra trường hợp DTO ban đầu đã có status = 1 vẫn giữ nguyên sau khi gọi.
     */
    @Test
    void TC_46testViewCV_WithAlreadyStatus1_ShouldRemain1() {
        int seekerID = 30;
        int appID = 40;

        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setId(appID);
        dto.setStatus(1);

        when(applicationHistoryService.findByID(appID)).thenReturn(dto);

        ApplicationHistoryDTO result = controller.viewCV(seekerID, appID);

        System.out.println("✅ Mong muốn: Status giữ nguyên là 1");
        System.out.println("📤 Thực tế: Status = " + result.getStatus());

        assertEquals(1, result.getStatus());
        verify(applicationHistoryService).save(dtoCaptor.capture());
        assertEquals(1, dtoCaptor.getValue().getStatus());
    }

    /**
     * ✅ Test Case 3:
     * Mục tiêu: Kiểm tra controller ném ra exception nếu findByID trả về null.
     */
    @Test
    void TC47_testViewCV_WhenFindByIdReturnsNull_ShouldThrowException() {
        int seekerID = 50;
        int appID = 60;

        when(applicationHistoryService.findByID(appID)).thenReturn(null);

        System.out.println("✅ Mong muốn: Ném ra NullPointerException");

        assertThrows(NullPointerException.class, () -> {
            controller.viewCV(seekerID, appID);
        });

        verify(applicationHistoryService, never()).save(any());
    }
}
