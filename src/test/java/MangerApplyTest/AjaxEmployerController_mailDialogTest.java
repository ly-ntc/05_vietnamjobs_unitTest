package MangerApplyTest;

import com.demo.controllers.employer.AjaxEmployerController;
import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.services.ApplicationHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AjaxEmployerController_MailDialogTest {

    @Mock
    private ApplicationHistoryService applicationHistoryService;

    @InjectMocks
    private AjaxEmployerController controller;

    // ✅ Test 1: Với appID hợp lệ
    @Test
    void mailDialog_WithValidAppID_ReturnsDTO() {
        int appID = 1;

        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setId(appID);
        dto.setResult(1);
        dto.setStatus(2);

        // Mock hành vi service
        when(applicationHistoryService.findByID(appID)).thenReturn(dto);

        // Gọi hàm controller
        ApplicationHistoryDTO result = controller.mailDialog(appID);

        // In ra kết quả
        System.out.println("✅ Mong đợi: ID = 1, Result = 1, Status = 2");
        System.out.println("📤 Thực tế: ID = " + result.getId() + ", Result = " + result.getResult() + ", Status = " + result.getStatus());

        // Kiểm tra
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(1, result.getResult());
        assertEquals(2, result.getStatus());
    }

    // ✅ Test 2: Với appID không tồn tại → ném exception
    @Test
    void mailDialog_WithInvalidAppID_ThrowsException() {
        int invalidAppID = 999;

        // Giả lập service ném NoSuchElementException
        when(applicationHistoryService.findByID(invalidAppID)).thenThrow(new NoSuchElementException("Không tìm thấy"));

        // Kiểm tra ngoại lệ
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            controller.mailDialog(invalidAppID);
        });

        // In ra
        System.out.println("❌ Mong đợi: Ném NoSuchElementException với thông báo 'Không tìm thấy'");
        System.out.println("📤 Thực tế: " + exception.getMessage());

        assertEquals("Không tìm thấy", exception.getMessage());
    }

    // ✅ Test 3: Kiểm tra từng field được map đúng
    @Test
    void mailDialog_FieldsAreCorrectlyMapped() {
        int appID = 2;

        ApplicationHistoryDTO mockDTO = new ApplicationHistoryDTO();
        mockDTO.setId(2);
        mockDTO.setStatus(3);
        mockDTO.setResult(0);

        when(applicationHistoryService.findByID(appID)).thenReturn(mockDTO);

        ApplicationHistoryDTO result = controller.mailDialog(appID);

        // In kết quả
        System.out.println("✅ Mong đợi: ID = 2, Status = 3, Result = 0");
        System.out.println("📤 Thực tế: ID = " + result.getId() + ", Status = " + result.getStatus() + ", Result = " + result.getResult());

        // Kiểm tra từng field
        assertEquals(2, result.getId());
        assertEquals(3, result.getStatus());
        assertEquals(0, result.getResult());
    }
}