package com.nuvolo.nuvoloapi.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuvolo.nuvoloapi.mq.message.NotificationMessage;
import com.nuvolo.nuvoloapi.mq.message.VerificationAndResetMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMqSenderTest {

    private static final String TEST_DIRECT_EXCHANGE = "direct.exchange";
    private static final String TEST_ROUTING = "routing.key";

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RabbitMqComponent rabbitMqComponent;

    @InjectMocks
    private RabbitMqSender rabbitMqSender;


    @Test
    void testSendEmailVerificationMessage_success() {
        VerificationAndResetMessage verificationMessage = this.createVerificationAndResetMessage();
        VerificationAndResetMessage responseMessage = this.createSuccessResponseVerificationAndReset();

        when(rabbitMqComponent.getDirectExchangeName()).thenReturn(TEST_DIRECT_EXCHANGE);
        when(rabbitMqComponent.getVerificationRoutingKey()).thenReturn(TEST_ROUTING);
        when(rabbitTemplate.convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, verificationMessage))
                .thenReturn(responseMessage);
        when(objectMapper.convertValue(responseMessage, VerificationAndResetMessage.class)).thenReturn(responseMessage);

        Boolean result = rabbitMqSender.sendEmailVerificationMessage(verificationMessage);

        assertEquals(Boolean.TRUE, result);
        verify(rabbitTemplate, times(1))
                .convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, verificationMessage);
        verify(objectMapper, times(1))
                .convertValue(responseMessage, VerificationAndResetMessage.class);
    }

    @Test
    void testSendEmailVerificationMessage_fail() {
        VerificationAndResetMessage verificationMessage = this.createVerificationAndResetMessage();
        VerificationAndResetMessage responseMessage = this.createFailResponseVerificationAndResetAndReset();

        when(rabbitMqComponent.getDirectExchangeName()).thenReturn(TEST_DIRECT_EXCHANGE);
        when(rabbitMqComponent.getVerificationRoutingKey()).thenReturn(TEST_ROUTING);
        when(rabbitTemplate.convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, verificationMessage))
                .thenReturn(responseMessage);
        when(objectMapper.convertValue(responseMessage, VerificationAndResetMessage.class)).thenReturn(responseMessage);

        Boolean result = rabbitMqSender.sendEmailVerificationMessage(verificationMessage);

        assertEquals(Boolean.FALSE, result);
        verify(rabbitTemplate, times(1))
                .convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, verificationMessage);
        verify(objectMapper, times(1))
                .convertValue(responseMessage, VerificationAndResetMessage.class);
    }

    @Test
    void testSendEmailVerificationMessage_responseNullFail() {
        VerificationAndResetMessage verificationMessage = this.createVerificationAndResetMessage();

        when(rabbitMqComponent.getDirectExchangeName()).thenReturn(TEST_DIRECT_EXCHANGE);
        when(rabbitMqComponent.getVerificationRoutingKey()).thenReturn(TEST_ROUTING);
        when(rabbitTemplate.convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, verificationMessage))
                .thenReturn(null);

        Boolean result = rabbitMqSender.sendEmailVerificationMessage(verificationMessage);

        assertEquals(Boolean.FALSE, result);
        verify(rabbitTemplate, times(1))
                .convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, verificationMessage);
        verify(objectMapper, never()).convertValue(any(), eq(VerificationAndResetMessage.class));
    }

    @Test
    void testSendEmailVerificationMessage_amqpException() {
        VerificationAndResetMessage verificationMessage = this.createVerificationAndResetMessage();

        when(rabbitMqComponent.getDirectExchangeName()).thenReturn(TEST_DIRECT_EXCHANGE);
        when(rabbitMqComponent.getVerificationRoutingKey()).thenReturn(TEST_ROUTING);
        when(rabbitTemplate.convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, verificationMessage))
                .thenThrow(new RuntimeException("Mocked exception"));

        var exception = assertThrows(AmqpException.class, () -> {
            rabbitMqSender.sendEmailVerificationMessage(verificationMessage);
        });

        assertEquals("Error occurred while sending user verification message. Mocked exception. Try again later.", exception.getMessage());
        verify(rabbitTemplate, times(1)).convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, verificationMessage);
        verify(objectMapper, never()).convertValue(any(), eq(VerificationAndResetMessage.class));
    }

    @Test
    void testSendPasswordResetMessage_success() {
        VerificationAndResetMessage passwordResetMessage = this.createVerificationAndResetMessage();
        VerificationAndResetMessage responseMessage = this.createSuccessResponseVerificationAndReset();

        when(rabbitMqComponent.getDirectExchangeName()).thenReturn(TEST_DIRECT_EXCHANGE);
        when(rabbitMqComponent.getPasswordResetRoutingKey()).thenReturn(TEST_ROUTING);
        when(rabbitTemplate.convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, passwordResetMessage))
                .thenReturn(responseMessage);
        when(objectMapper.convertValue(responseMessage, VerificationAndResetMessage.class)).thenReturn(responseMessage);

        Boolean result = rabbitMqSender.sendPasswordResetMessage(passwordResetMessage);

        assertEquals(Boolean.TRUE, result);
        verify(rabbitTemplate, times(1))
                .convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, passwordResetMessage);
        verify(objectMapper, times(1))
                .convertValue(responseMessage, VerificationAndResetMessage.class);
    }

    @Test
    void testSendPasswordResetMessage_fail() {
        VerificationAndResetMessage passwordResetMessage = this.createVerificationAndResetMessage();
        VerificationAndResetMessage responseMessage = this.createFailResponseVerificationAndResetAndReset();

        when(rabbitMqComponent.getDirectExchangeName()).thenReturn(TEST_DIRECT_EXCHANGE);
        when(rabbitMqComponent.getPasswordResetRoutingKey()).thenReturn(TEST_ROUTING);
        when(rabbitTemplate.convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, passwordResetMessage))
                .thenReturn(responseMessage);
        when(objectMapper.convertValue(responseMessage, VerificationAndResetMessage.class)).thenReturn(responseMessage);

        Boolean result = rabbitMqSender.sendPasswordResetMessage(passwordResetMessage);

        assertEquals(Boolean.FALSE, result);
        verify(rabbitTemplate, times(1))
                .convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, passwordResetMessage);
        verify(objectMapper, times(1))
                .convertValue(responseMessage, VerificationAndResetMessage.class);
    }

    @Test
    void testSendPasswordResetMessage_responseNullFail() {
        VerificationAndResetMessage passwordResetMessage = this.createVerificationAndResetMessage();

        when(rabbitMqComponent.getDirectExchangeName()).thenReturn(TEST_DIRECT_EXCHANGE);
        when(rabbitMqComponent.getPasswordResetRoutingKey()).thenReturn(TEST_ROUTING);
        when(rabbitTemplate.convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, passwordResetMessage))
                .thenReturn(null);

        Boolean result = rabbitMqSender.sendPasswordResetMessage(passwordResetMessage);

        assertEquals(Boolean.FALSE, result);
        verify(rabbitTemplate, times(1))
                .convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, passwordResetMessage);
        verify(objectMapper, never()).convertValue(any(), eq(VerificationAndResetMessage.class));
    }

    @Test
    void testSendPasswordResetMessage_amqpException() {
        VerificationAndResetMessage passwordResetMessage = this.createVerificationAndResetMessage();

        when(rabbitMqComponent.getDirectExchangeName()).thenReturn(TEST_DIRECT_EXCHANGE);
        when(rabbitMqComponent.getPasswordResetRoutingKey()).thenReturn(TEST_ROUTING);
        when(rabbitTemplate.convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, passwordResetMessage))
                .thenThrow(new RuntimeException("Mocked exception"));

        var exception = assertThrows(AmqpException.class, () -> {
            rabbitMqSender.sendPasswordResetMessage(passwordResetMessage);
        });

        assertEquals("Error occurred while sending password reset request message. Mocked exception. Try again later.", exception.getMessage());
        verify(rabbitTemplate, times(1)).convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, passwordResetMessage);
        verify(objectMapper, never()).convertValue(any(), eq(VerificationAndResetMessage.class));
    }

    @Test
    void testSendNotificationMessage_success() {
        NotificationMessage notificationMessage = this.createNotificationMessage();
        NotificationMessage responseMessage = this.createSuccessNotificationResponse();

        when(rabbitMqComponent.getDirectExchangeName()).thenReturn(TEST_DIRECT_EXCHANGE);
        when(rabbitMqComponent.getNotificationRoutingKey()).thenReturn(TEST_ROUTING);
        when(rabbitTemplate.convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, notificationMessage))
                .thenReturn(responseMessage);
        when(objectMapper.convertValue(responseMessage, NotificationMessage.class)).thenReturn(responseMessage);

        Boolean result = rabbitMqSender.sendNotificationMessage(notificationMessage);

        assertEquals(Boolean.TRUE, result);
        verify(rabbitTemplate, times(1))
                .convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, notificationMessage);
        verify(objectMapper, times(1))
                .convertValue(responseMessage, NotificationMessage.class);
    }

    @Test
    void testSendNotificationMessage_fail() {
        NotificationMessage notificationMessage = this.createNotificationMessage();
        NotificationMessage responseMessage = this.createFailNotificationResponse();

        when(rabbitMqComponent.getDirectExchangeName()).thenReturn(TEST_DIRECT_EXCHANGE);
        when(rabbitMqComponent.getNotificationRoutingKey()).thenReturn(TEST_ROUTING);
        when(rabbitTemplate.convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, notificationMessage))
                .thenReturn(responseMessage);
        when(objectMapper.convertValue(responseMessage, NotificationMessage.class)).thenReturn(responseMessage);

        Boolean result = rabbitMqSender.sendNotificationMessage(notificationMessage);

        assertEquals(Boolean.FALSE, result);
        verify(rabbitTemplate, times(1))
                .convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, notificationMessage);
        verify(objectMapper, times(1))
                .convertValue(responseMessage, NotificationMessage.class);
    }

    @Test
    void testSendNotificationMessage_responseNullFail() {
        NotificationMessage notificationMessage = this.createNotificationMessage();

        when(rabbitMqComponent.getDirectExchangeName()).thenReturn(TEST_DIRECT_EXCHANGE);
        when(rabbitMqComponent.getNotificationRoutingKey()).thenReturn(TEST_ROUTING);
        when(rabbitTemplate.convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, notificationMessage))
                .thenReturn(null);

        Boolean result = rabbitMqSender.sendNotificationMessage(notificationMessage);

        assertEquals(Boolean.FALSE, result);
        verify(rabbitTemplate, times(1))
                .convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, notificationMessage);
        verify(objectMapper, never()).convertValue(any(), eq(NotificationMessage.class));
    }

    @Test
    void testSendNotificationMessage_amqpException() {
        NotificationMessage notificationMessage = this.createNotificationMessage();

        when(rabbitMqComponent.getDirectExchangeName()).thenReturn(TEST_DIRECT_EXCHANGE);
        when(rabbitMqComponent.getNotificationRoutingKey()).thenReturn(TEST_ROUTING);
        when(rabbitTemplate.convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, notificationMessage))
                .thenThrow(new RuntimeException("Mocked exception"));

        var exception = assertThrows(AmqpException.class, () -> {
            rabbitMqSender.sendNotificationMessage(notificationMessage);
        });

        assertEquals("Error occurred while sending notification request message. Mocked exception. Try again later.", exception.getMessage());
        verify(rabbitTemplate, times(1)).convertSendAndReceive(TEST_DIRECT_EXCHANGE, TEST_ROUTING, notificationMessage);
        verify(objectMapper, never()).convertValue(any(), eq(NotificationMessage.class));
    }

    private VerificationAndResetMessage createVerificationAndResetMessage() {
        VerificationAndResetMessage verificationAndResetMessage = new VerificationAndResetMessage();
        verificationAndResetMessage.setEmail("nuvolo@mail.com");
        verificationAndResetMessage.setFirstName("nuvolo");
        verificationAndResetMessage.setLastName("nuvolo");
        verificationAndResetMessage.setToken("token");
        return verificationAndResetMessage;
    }

    private VerificationAndResetMessage createSuccessResponseVerificationAndReset() {
        VerificationAndResetMessage verificationAndResetMessage = new VerificationAndResetMessage();
        verificationAndResetMessage.setSuccess(Boolean.TRUE);
        return verificationAndResetMessage;
    }

    private VerificationAndResetMessage createFailResponseVerificationAndResetAndReset() {
        VerificationAndResetMessage verificationAndResetResponse = new VerificationAndResetMessage();
        verificationAndResetResponse.setSuccess(Boolean.FALSE);
        return verificationAndResetResponse;
    }

    private NotificationMessage createNotificationMessage() {
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setEmail("nuvolo@mail.com");
        notificationMessage.setFirstName("nuvolo");
        notificationMessage.setLastName("nuvolo");
        notificationMessage.setMessage("Message");
        return notificationMessage;
    }

    private NotificationMessage createSuccessNotificationResponse() {
        NotificationMessage notificationResponse = new NotificationMessage();
        notificationResponse.setSuccess(Boolean.TRUE);
        return notificationResponse;
    }

    private NotificationMessage createFailNotificationResponse() {
        NotificationMessage notificationResponse = new NotificationMessage();
        notificationResponse.setSuccess(Boolean.FALSE);
        return notificationResponse;
    }

}
