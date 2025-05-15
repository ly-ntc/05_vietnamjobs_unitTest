package com.demo.services;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MailServiceImplTest {

    private JavaMailSender mailSenderMock;
    private MailServiceImpl mailService;

    @BeforeEach
    void setUp() {
        mailSenderMock = mock(JavaMailSender.class);
        mailService = new MailServiceImpl();
        ReflectionTestUtils.setField(mailService, "sender", mailSenderMock); // inject mock
    }

    /**
     * TC052 - Gửi mail thành công
     */
    @Test
    void TC052() throws Exception {
        // Giả lập MimeMessage
        MimeMessage mimeMessageMock = mock(MimeMessage.class);
        when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessageMock);

        // Không ném exception khi gửi
        doNothing().when(mailSenderMock).send(any(MimeMessage.class));

        boolean result = mailService.sendMailAccuracy(
                "test@demo.com",
                "receiver@demo.com",
                "<p>Hello!</p>"
        );

        System.out.println("TC052 - Expected: true | Actual: " + result);
        assertTrue(result);
    }

    /**
     * TC053 - Gửi mail thất bại do ném exception
     */
    @Test
    void TC053() {
        // Giả lập lỗi khi tạo MimeMessage
        when(mailSenderMock.createMimeMessage()).thenThrow(new RuntimeException("Mail error"));

        boolean result = mailService.sendMailAccuracy(
                "test@demo.com",
                "receiver@demo.com",
                "<p>Hello!</p>"
        );

        System.out.println("TC053 - Expected: false | Actual: " + result);
        assertFalse(result);
    }
}
