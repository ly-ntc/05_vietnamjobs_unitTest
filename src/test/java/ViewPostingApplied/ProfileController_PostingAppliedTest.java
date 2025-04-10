package ViewPostingApplied;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.demo.controllers.user.ProfileController;
import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.dtos.PostingDTO;
import com.demo.dtos.SeekerDTO;
import com.demo.dtos.AccountDTO;
import com.demo.services.ApplicationHistoryService;
import com.demo.services.PostingService;
import com.demo.services.SeekerService;
import com.demo.services.AccountService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@ExtendWith(MockitoExtension.class)
class ProfileController_PostingAppliedTest {

    @InjectMocks
    private ProfileController controller;

    @Mock
    private SeekerService seekerService;

    @Mock
    private AccountService accountService;

    @Mock
    private ApplicationHistoryService applicationHistoryService;

    @Mock
    private PostingService postingService;

    @Mock
    private Authentication authentication;

    @Mock
    private ModelMap modelMap;

    // TC49: Happy Path - posting != null
    @Test
    void TC48_testPostingApplied_ReturnsCorrectViewAndData() {
        // ✅ Setup dữ liệu test
        int accId = 1;
        int seekerId = 101;
        int postingId = 202;

        // Account và Seeker giả
        AccountDTO account = new AccountDTO();
        account.setId(accId);

        SeekerDTO seeker = new SeekerDTO();
        seeker.setId(seekerId);

        // Lịch sử ứng tuyển giả
        ApplicationHistoryDTO appHistory = new ApplicationHistoryDTO();
        appHistory.setPostingID(postingId);

        // Bài đăng giả
        PostingDTO posting = new PostingDTO();
        posting.setId(postingId);
        posting.setTitle("Java Dev");

        // Danh sách bài đăng ngẫu nhiên
        List<PostingDTO> randomPostings = List.of(new PostingDTO());

        // ✅ Mock các service
        when(authentication.getName()).thenReturn("testuser");
        when(accountService.findByUsername("testuser")).thenReturn(account);
        when(seekerService.findByAccountID(accId)).thenReturn(seeker);
        when(applicationHistoryService.findAppliedCV(seekerId)).thenReturn(List.of(appHistory));
        when(postingService.findById(postingId)).thenReturn(posting);
        when(postingService.findAllLimit(10)).thenReturn(randomPostings);

        // ✅ Tạo ModelMap để truyền vào controller
        ModelMap modelMap = new ModelMap();

        // ✅ Gọi hàm controller
        String viewName = controller.postingapplied(modelMap, authentication);

        // ✅ In kết quả mong muốn và thực tế
        System.out.println("✅ Mong muốn: view = user/profile/postingapplied");
        System.out.println("📤 Thực tế: view = " + viewName);

        System.out.println("✅ Mong muốn: seekerId = 101");
        System.out.println("📤 Thực tế: seekerId = " + modelMap.get("seekerId"));

        System.out.println("✅ Mong muốn: postings.size() = 1");
        System.out.println("📤 Thực tế: postings.size() = " + ((List<?>) modelMap.get("postings")).size());

        System.out.println("✅ Mong muốn: randomPostings.size() = 1");
        System.out.println("📤 Thực tế: randomPostings.size() = " + ((List<?>) modelMap.get("randomPostings")).size());

        // ✅ Kiểm tra kết quả
        assertEquals("user/profile/postingapplied", viewName);
        assertEquals(seekerId, modelMap.get("seekerId"));
        assertEquals(1, ((List<?>) modelMap.get("postings")).size());
        assertEquals(1, ((List<?>) modelMap.get("randomPostings")).size());
    }

    // TC49 - Có applicationHistory nhưng một bài đăng đã bị xoá
    @Test
    void TC49_postingApplied_PostingIsNull() {
        // Mock Authentication và Account
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user1");

        AccountDTO account = new AccountDTO();
        account.setId(1);
        when(accountService.findByUsername("user1")).thenReturn(account);

        // Mock seeker
        SeekerDTO seeker = new SeekerDTO();
        seeker.setId(10);
        when(seekerService.findByAccountID(1)).thenReturn(seeker);

        // Mock application histories
        ApplicationHistoryDTO history1 = new ApplicationHistoryDTO();
        history1.setPostingID(100); // bị xoá
        ApplicationHistoryDTO history2 = new ApplicationHistoryDTO();
        history2.setPostingID(200); // còn tồn tại

        when(applicationHistoryService.findAppliedCV(10)).thenReturn(Arrays.asList(history1, history2));
        when(postingService.findById(100)).thenReturn(null);
        PostingDTO validPosting = new PostingDTO();
        validPosting.setId(200);
        when(postingService.findById(200)).thenReturn(validPosting);
        when(postingService.findAllLimit(10)).thenReturn(Collections.emptyList());

        ModelMap model = new ModelMap();
        String view = controller.postingapplied(model, auth);

        // Kiểm tra kết quả
        System.out.println("✅ Mong muốn: postings.size() = 1");
        System.out.println("📤 Thực tế: postings.size() = " + ((List<?>) model.get("postings")).size());

        assertEquals("user/profile/postingapplied", view);
        assertEquals(10, model.get("seekerId"));
        assertEquals(1, ((List<?>) model.get("postings")).size());
    }

    @Test
    //"TC50 - Không có ApplicationHistory"
    void TC50_testPostingApplied_NoApplicationHistory() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user2");

        AccountDTO account = new AccountDTO();
        account.setId(2);
        when(accountService.findByUsername("user2")).thenReturn(account);

        SeekerDTO seeker = new SeekerDTO();
        seeker.setId(20);
        when(seekerService.findByAccountID(2)).thenReturn(seeker);

        when(applicationHistoryService.findAppliedCV(20)).thenReturn(Collections.emptyList());
        when(postingService.findAllLimit(10)).thenReturn(Collections.emptyList());

        ModelMap model = new ModelMap();
        String view = controller.postingapplied(model, auth);

        System.out.println("✅ Mong muốn: postings.size() = 0");
        System.out.println("📤 Thực tế: postings.size() = " + ((List<?>) model.get("postings")).size());

        assertEquals("user/profile/postingapplied", view);
        assertEquals(20, model.get("seekerId"));
        assertEquals(0, ((List<?>) model.get("postings")).size());
    }

    @Test
    //"TC53 - seekerService.findByAccountID ném ra NullPointerException"
    void TC51_testPostingApplied_SeekerServiceThrowsException() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user3");

        AccountDTO account = new AccountDTO();
        account.setId(3);
        when(accountService.findByUsername("user3")).thenReturn(account);

        // Giả lập lỗi
        when(seekerService.findByAccountID(3)).thenThrow(new NullPointerException("Seeker not found"));

        ModelMap model = new ModelMap();
        try {
            controller.postingapplied(model, auth);
            System.out.println("📤 Thực tế: không ném ra exception");
            fail("Expected NullPointerException");
        } catch (NullPointerException ex) {
            System.out.println("✅ Mong muốn: Ném ra NullPointerException");
            System.out.println("📤 Thực tế: " + ex.getMessage());
            assertEquals("Seeker not found", ex.getMessage());
        }
    }

}

