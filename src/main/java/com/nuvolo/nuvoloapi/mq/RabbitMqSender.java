package com.nuvolo.nuvoloapi.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuvolo.nuvoloapi.mq.message.NotificationMessage;
import com.nuvolo.nuvoloapi.mq.message.VerificationAndResetMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqSender {

    private final RabbitMqComponent rabbitMqComponent;

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    public Boolean sendEmailVerificationMessage(VerificationAndResetMessage verificationMessage) {
        try {
            Object response = rabbitTemplate.convertSendAndReceive(rabbitMqComponent.getDirectExchangeName(),
                    rabbitMqComponent.getVerificationRoutingKey(),
                    verificationMessage
            );
            if (response == null) {
                log.error("Verification response is null.");
                return Boolean.FALSE;
            }
            VerificationAndResetMessage verificationResponse = objectMapper.convertValue(response, VerificationAndResetMessage.class);
            log.debug("Received verification response.");
            return verificationResponse.getSuccess();
        } catch (Exception ex) {
            log.error("Exception occurred during sending verification message to mail service.");
            throw new AmqpException(String.format("Error occurred while sending user verification message. %s. Try again later.", ex.getMessage()));
        }
    }

    public Boolean sendPasswordResetMessage(VerificationAndResetMessage passResetMessage) {
        try {
            Object response = rabbitTemplate.convertSendAndReceive(rabbitMqComponent.getDirectExchangeName(),
                    rabbitMqComponent.getPasswordResetRoutingKey(),
                    passResetMessage
            );
            if (response == null) {
                log.error("Password reset response is null.");
                return Boolean.FALSE;
            }
            VerificationAndResetMessage passwordResetResponse = objectMapper.convertValue(response, VerificationAndResetMessage.class);
            log.debug("Received password reset request response.");
            return passwordResetResponse.getSuccess();
        } catch (Exception ex) {
            log.error("Exception occurred during sending password reset request message to mail service.");
            throw new AmqpException(String.format("Error occurred while sending password reset request message. %s. Try again later.", ex.getMessage()));
        }
    }

    public Boolean sendNotificationMessage(NotificationMessage notificationMessage) {
        try {
            Object response = rabbitTemplate.convertSendAndReceive(rabbitMqComponent.getDirectExchangeName(),
                    rabbitMqComponent.getNotificationRoutingKey(),
                    notificationMessage
            );
            if (response == null) {
                log.error("Notification request response is null.");
                return Boolean.FALSE;
            }
            NotificationMessage notificationResponse = objectMapper.convertValue(response, NotificationMessage.class);
            log.debug("Received notification request response.");
            return notificationResponse.getSuccess();
        } catch (Exception ex) {
            log.error("Exception occurred during sending notification message to mail service.");
            throw new AmqpException(String.format("Error occurred while sending notification request message. %s. Try again later.", ex.getMessage()));
        }
    }

}
