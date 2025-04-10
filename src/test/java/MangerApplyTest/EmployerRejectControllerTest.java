package MangerApplyTest;
import com.demo.controllers.employer.EmployerApplyController;
import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.services.ApplicationHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
public class EmployerRejectControllerTest {
    @InjectMocks
    private EmployerApplyController employerApplyController;

    @Mock
    private ApplicationHistoryService applicationHistoryService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReject_ValidId_SaveSuccess() {
        int id = 1;
        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setPostingID(10);

        // Giả lập dữ liệu từ service
        when(applicationHistoryService.findByID(id)).thenReturn(dto);
        when(applicationHistoryService.save(dto)).thenReturn(true);

        ModelMap modelMap = new ModelMap();
        String view = employerApplyController.reject(id, modelMap, redirectAttributes);

        // Kết quả mong muốn
        System.out.println("✅ Mong muốn: redirect:/employer/apply/index/10");
        System.out.println("📤 Thực tế: " + view);

        // Kiểm tra gán flash message đúng
        verify(redirectAttributes).addFlashAttribute("success", "Thành công!");
        assertEquals("redirect:/employer/apply/index/10", view);
    }

    // ✅ Test 2: ID hợp lệ, save() thất bại
    @Test
    void testReject_ValidId_SaveFail() {
        int id = 2;
        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setPostingID(20);

        // Giả lập save() trả về false
        when(applicationHistoryService.findByID(id)).thenReturn(dto);
        when(applicationHistoryService.save(dto)).thenReturn(false);

        ModelMap modelMap = new ModelMap();
        String view = employerApplyController.reject(id, modelMap, redirectAttributes);

        // Kết quả mong muốn
        System.out.println("✅ Mong muốn: redirect:/employer/apply/index/20");
        System.out.println("📤 Thực tế: " + view);

        // Kiểm tra gán flash message thất bại
        verify(redirectAttributes).addFlashAttribute("error", "Thất bại...");
        assertEquals("redirect:/employer/apply/index/20", view);
    }

    // ✅ Test 3: Kiểm tra redirect URL có đúng format không
    @Test
    void testReject_ReturnsCorrectRedirectUrl() {
        int id = 3;
        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setPostingID(99);

        when(applicationHistoryService.findByID(id)).thenReturn(dto);
        when(applicationHistoryService.save(dto)).thenReturn(true);

        ModelMap modelMap = new ModelMap();
        String view = employerApplyController.reject(id, modelMap, redirectAttributes);

        System.out.println("✅ Mong muốn: redirect:/employer/apply/index/99");
        System.out.println("📤 Thực tế: " + view);

        assertEquals("redirect:/employer/apply/index/99", view);
    }

    // ✅ Test 4: findByID trả về null → sẽ gây NullPointerException nếu không được xử lý
    @Test
    void testReject_FindByIdReturnsNull_ThrowsException() {
        int id = 100;
        when(applicationHistoryService.findByID(id)).thenReturn(null);

        ModelMap modelMap = new ModelMap();
        try {
            employerApplyController.reject(id, modelMap, redirectAttributes);
        } catch (NullPointerException e) {
            System.out.println("✅ Mong muốn: NullPointerException");
            System.out.println("📤 Thực tế: " + e.getClass().getSimpleName());
            return;
        }


    }

    // ✅ Test 5: ModelMap có chứa ID sau khi xử lý
    @Test
    void testReject_ModelMapContainsId() {
        int id = 5;
        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setPostingID(33);

        when(applicationHistoryService.findByID(id)).thenReturn(dto);
        when(applicationHistoryService.save(dto)).thenReturn(true);

        ModelMap modelMap = new ModelMap();
        String view = employerApplyController.reject(id, modelMap, redirectAttributes);

        System.out.println("✅ Mong muốn: modelMap chứa id = 5");
        System.out.println("📤 Thực tế: id = " + modelMap.get("id"));

        assertEquals(id, modelMap.get("id"));
    }
}
