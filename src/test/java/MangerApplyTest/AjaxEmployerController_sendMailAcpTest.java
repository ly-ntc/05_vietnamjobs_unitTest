package MangerApplyTest;

import com.demo.controllers.employer.AjaxEmployerController;
import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.services.ApplicationHistoryService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class AjaxEmployerController_sendMailAcpTest {
    @InjectMocks
    private AjaxEmployerController controller;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ApplicationHistoryService applicationHistoryService;

    @Mock
    private MimeMessage mimeMessage;

    @Captor
    ArgumentCaptor<ApplicationHistoryDTO> dtoCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    // ✅ Test case 1: Gửi mail thành công
    @Test
    void TC42_testSendMailAcp_WithValidInput_ReturnsTrue() throws Exception {
        int appID = 1;
        String mailBody = "<b>Hello</b>";
        String mailSubject = "Test Subject";
        String mailTo = "test@example.com";
        String mailFrom = "sender@example.com";

        // Mock DTO
        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setId(appID);
        dto.setResult(0);

        when(applicationHistoryService.findByID(appID)).thenReturn(dto);

        // Fake MimeMessageHelper behavior
        MimeMessageHelper helper = spy(new MimeMessageHelper(mimeMessage));
        try (MockedConstruction<MimeMessageHelper> mocked = mockConstruction(MimeMessageHelper.class,
                (mock, context) -> {
                    doNothing().when(mock).setFrom(anyString());
                    doNothing().when(mock).setTo(anyString());
                    doNothing().when(mock).setSubject(anyString());
                    doNothing().when(mock).setText(anyString(), eq(true));
                })) {

            boolean result = controller.sendMailAcp(appID, mailBody, mailSubject, mailTo, mailFrom);

            // ✅ In kết quả
            System.out.println("✅ Mong muốn: true");
            System.out.println("📤 Thực tế: " + result);

            assertTrue(result);
            verify(applicationHistoryService).save(dtoCaptor.capture());
            assertEquals(1, dtoCaptor.getValue().getResult());
            verify(mailSender).send(mimeMessage);
        }
    }

    // ✅ Test case 2: Xảy ra MessagingException nhưng vẫn trả về true
    @Test
    void TC43_testSendMailAcp_WhenMessagingExceptionThrown_ReturnsTrue() throws Exception {
        int appID = 2;
        String mailBody = "error";
        String mailSubject = "Subject";
        String mailTo = "a@b.com";
        String mailFrom = "b@a.com";

        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setId(appID);
        dto.setResult(0);

        when(applicationHistoryService.findByID(appID)).thenReturn(dto);

        try (MockedConstruction<MimeMessageHelper> mocked = mockConstruction(MimeMessageHelper.class,
                (mock, context) -> {
                    doThrow(new MessagingException("Lỗi giả lập")).when(mock).setFrom(anyString());
                })) {

            boolean result = controller.sendMailAcp(appID, mailBody, mailSubject, mailTo, mailFrom);

            System.out.println("✅ Mong muốn: true (dù lỗi)");
            System.out.println("📤 Thực tế: " + result);

            assertTrue(result);
            verify(applicationHistoryService).save(any());
            verify(mailSender).send(mimeMessage);
        }
    }

    // ✅ Test case 3: Kiểm tra result = 1 trong DTO lưu lại
    @Test
    void TC4_4testSendMailAcp_ShouldUpdateDTOResultTo1() throws Exception {
        int appID = 99;
        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setId(appID);
        dto.setResult(0);

        when(applicationHistoryService.findByID(appID)).thenReturn(dto);

        try (MockedConstruction<MimeMessageHelper> mocked = mockConstruction(MimeMessageHelper.class,
                (mock, context) -> {
                    doNothing().when(mock).setFrom(anyString());
                    doNothing().when(mock).setTo(anyString());
                    doNothing().when(mock).setSubject(anyString());
                    doNothing().when(mock).setText(anyString(), eq(true));
                })) {

            controller.sendMailAcp(appID, "body", "subject", "to@example.com", "from@example.com");

            verify(applicationHistoryService).save(dtoCaptor.capture());
            System.out.println("✅ Mong muốn: result = 1");
            System.out.println("📤 Thực tế: result = " + dtoCaptor.getValue().getResult());

            assertEquals(1, dtoCaptor.getValue().getResult());
        }
    }
}
