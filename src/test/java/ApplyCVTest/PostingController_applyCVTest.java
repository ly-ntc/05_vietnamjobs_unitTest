package ApplyCVTest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.demo.controllers.user.PostingController;
import com.demo.dtos.AccountDTO;
import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.dtos.PostingDTO;
import com.demo.dtos.SeekerDTO;
import com.demo.services.AccountService;
import com.demo.services.ApplicationHistoryService;
import com.demo.services.PostingService;
import com.demo.services.SeekerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class PostingController_applyCVTest {
    @Mock
    private PostingService postingService;

    @Mock
    private SeekerService seekerService;

    @Mock
    private AccountService accountService;

    @Mock
    private ApplicationHistoryService applicationHistoryService;

    @Mock
    private Authentication authentication;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private PostingController postingController;  // Thay 'YourController' bằng tên controller thực tế của bạn

    @BeforeEach
    public void setUp() {

    }
    //test truong hop thanh cong
    @Test
    public void TC1_testApplyCVSuccess() {
        int id = 1;
        String username = "testUser";

        // Tạo đối tượng PostingDTO và SeekerDTO mock
        PostingDTO posting = new PostingDTO();
        posting.setId(id);

        SeekerDTO seeker = new SeekerDTO();
        seeker.setId(2);

        // Mock đối tượng AccountDTO với ID hợp lệ
        AccountDTO account = new AccountDTO();
        account.setId(3);  // Cấp ID hợp lệ cho AccountDTO

        // Mock hành vi của các phương thức dịch vụ
        when(authentication.getName()).thenReturn(username);
        when(accountService.findByUsername(username)).thenReturn(account);  // Mock account với ID
        when(postingService.findById(id)).thenReturn(posting);
        when(seekerService.findByAccountID(anyInt())).thenReturn(seeker);
        when(applicationHistoryService.save(any(ApplicationHistoryDTO.class))).thenReturn(true);

        // Kết quả mong muốn
        String expectedViewName = "redirect:/home/posting/" + id;
        String expectedMessage = "Completed";
        System.out.println("Kết quả mong muốn:");
        System.out.println("Expected ViewName: " + expectedViewName);

        // Gọi phương thức trong controller
        String viewName = postingController.applyCV(new ModelMap(), id, authentication, redirectAttributes);

        // Kiểm tra kết quả thực tế
        System.out.println("Kết quả thực tế:");
        System.out.println("Actual ViewName: " + viewName);
        verify(redirectAttributes).addFlashAttribute("success", expectedMessage);

        // Kiểm tra kết quả cuối cùng
        assertEquals(expectedViewName, viewName);
        verify(redirectAttributes).addFlashAttribute("success", expectedMessage);
    }



    @Test
    public void TC2_testApplyCVPostingNotFound() {
        int id = 1;
        String username = "testUser";

        // Tạo đối tượng AccountDTO mock
        AccountDTO account = new AccountDTO();
        account.setId(3);  // Cấp ID hợp lệ cho AccountDTO

        // Mock hành vi của các phương thức dịch vụ với lenient
        lenient().when(authentication.getName()).thenReturn(username);
        lenient().when(accountService.findByUsername(username)).thenReturn(account);  // Mock account với ID
        lenient().when(postingService.findById(id)).thenReturn(null);  // Không tìm thấy bài đăng

        String expectedMessage = "Cannot invoke \"com.demo.dtos.PostingDTO.getId()\" because \"posting\" is null";
        System.out.println("Kết quả mong muốn:");
        System.out.println("Expected Message: " + expectedMessage);

        String actualViewName = null;
        String actualMessage = null;

        try {
            actualViewName = postingController.applyCV(new ModelMap(), id, authentication, redirectAttributes);
        } catch (Exception e) {
            // Catching the exception to print actual result for debugging
            actualMessage = e.getMessage();
        }
        System.out.println("Kết quả thực tế:");
        if (actualViewName != null) {
            System.out.println("Actual ViewName: " + actualViewName);
        } else {
            System.out.println("Actual Message: " + actualMessage);
        }

        // Kiểm tra kết quả cuối cùng
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void TC3_testApplyCVSeekerNotFound() {
        int id = 1;
        String username = "testUser";

        // Tạo đối tượng AccountDTO mock
        AccountDTO account = new AccountDTO();
        account.setId(3);  // Cấp ID hợp lệ cho AccountDTO

        // Tạo đối tượng PostingDTO mock
        PostingDTO posting = new PostingDTO();
        posting.setId(id);

        // Mock hành vi của các phương thức dịch vụ
        when(authentication.getName()).thenReturn(username);
        when(accountService.findByUsername(username)).thenReturn(account);  // Mock account với ID
        when(postingService.findById(id)).thenReturn(posting);  // Tìm thấy bài đăng hợp lệ
        when(seekerService.findByAccountID(account.getId())).thenReturn(null);  // Không tìm thấy seeker

        String expectedMessage = "Cannot invoke \"com.demo.dtos.SeekerDTO.getId()\" because \"seeker\" is null";
        System.out.println("Kết quả mong muốn:");
        System.out.println("Expected Message: " + expectedMessage);

        // Gọi phương thức trong controller và kiểm tra kết quả thực tế
        String actualViewName = null;
        String actualMessage = null;

        try {
            actualViewName = postingController.applyCV(new ModelMap(), id, authentication, redirectAttributes);
        } catch (Exception e) {
            // Catching the exception to print actual result for debugging
            actualMessage = e.getMessage();

        }

        // In kết quả thực tế
        System.out.println("Kết quả thực tế:");
        if (actualViewName != null) {
            System.out.println("Actual ViewName: " + actualViewName);
        } else {
            System.out.println("Actual Message: " + actualMessage);
        }

        // Kiểm tra kết quả cuối cùng
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void TC4_testApplyCVAuthenticationNotValid() {
        int id = 1;
        String invalidUsername = "invalidUser";

        // Tạo đối tượng AccountDTO mock cho một người dùng không hợp lệ
        AccountDTO invalidAccount = new AccountDTO();
        invalidAccount.setId(0);  // Không có ID hợp lệ

        // Mock hành vi của các phương thức dịch vụ
        lenient().when(authentication.getName()).thenReturn(invalidUsername);
        lenient().when(accountService.findByUsername(invalidUsername)).thenReturn(null);  // Tài khoản không tồn tại

        // Mock bài đăng hợp lệ
        PostingDTO posting = new PostingDTO();
        posting.setId(id);
        lenient().when(postingService.findById(id)).thenReturn(posting);  // Tìm thấy bài đăng hợp lệ

        // In kết quả mong muốn
        String expectedMessage = "Cannot invoke \"com.demo.dtos.AccountDTO.getId()\" because the return value of \"com.demo.services.AccountService.findByUsername(String)\" is null";
        System.out.println("Kết quả mong muốn:");
        System.out.println("Expected Message: " + expectedMessage);

        // Gọi phương thức trong controller và kiểm tra kết quả thực tế
        String actualViewName = null;
        String actualMessage = null;

        try {
            actualViewName = postingController.applyCV(new ModelMap(), id, authentication, redirectAttributes);
        } catch (Exception e) {
            // Catching the exception to print actual result for debugging
            actualMessage = e.getMessage();
        }

        // In kết quả thực tế
        System.out.println("Kết quả thực tế:");
        if (actualViewName != null) {
            System.out.println("Actual ViewName: " + actualViewName);
        } else {
            System.out.println("Actual Message: " + actualMessage);
        }

        // Kiểm tra kết quả cuối cùng
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void TC5_testApplyCVSaveFailed() {
        int id = 1;
        String username = "testUser";

        // Tạo đối tượng PostingDTO và SeekerDTO mock
        PostingDTO posting = new PostingDTO();
        posting.setId(id);

        SeekerDTO seeker = new SeekerDTO();
        seeker.setId(2);

        // Mock đối tượng AccountDTO với ID hợp lệ
        AccountDTO account = new AccountDTO();
        account.setId(3);  // Cấp ID hợp lệ cho AccountDTO

        // Mock hành vi của các phương thức dịch vụ
        when(authentication.getName()).thenReturn(username);
        when(accountService.findByUsername(username)).thenReturn(account);  // Mock account với ID
        when(postingService.findById(id)).thenReturn(posting);
        when(seekerService.findByAccountID(anyInt())).thenReturn(seeker);
        when(applicationHistoryService.save(any(ApplicationHistoryDTO.class))).thenReturn(false);  // Giả lập lưu thất bại

        // Kết quả mong muốn
        String expectedViewName = "redirect:/home/posting/" + id;
        String expectedMessage = "Thất bại...";

        // Gọi phương thức trong controller
        String viewName = postingController.applyCV(new ModelMap(), id, authentication, redirectAttributes);

        // Kiểm tra kết quả thực tế
        System.out.println("Kết quả mong muốn:");
        System.out.println("Expected ViewName: " + expectedViewName);
        System.out.println("Expected Message: " + expectedMessage);

        // Sử dụng ArgumentCaptor để bắt giá trị được thêm vào redirectAttributes
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> flashCaptor = ArgumentCaptor.forClass(String.class);

        // Kiểm tra xem addFlashAttribute đã được gọi đúng
        verify(redirectAttributes).addFlashAttribute(flashCaptor.capture(), messageCaptor.capture());

        // In kết quả thực tế
        System.out.println("Kết quả thực tế:");
        System.out.println("Actual ViewName: " + viewName);
        System.out.println("Actual Error Message: " + messageCaptor.getValue());

        // Kiểm tra kết quả cuối cùng
        assertEquals(expectedViewName, viewName);
        assertEquals("error", flashCaptor.getValue());  // Kiểm tra tên attribute là "error"
        assertEquals(expectedMessage, messageCaptor.getValue());  // Kiểm tra thông báo là "Thất bại..."

    }
}
