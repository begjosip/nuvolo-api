package com.nuvolo.nuvoloapi.mq;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RabbitMqComponent {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Value("${spring.rabbitmq.nuvolo.direct.exchange}")
    private String directExchangeName;

    @Value("${rabbitmq.nuvolo.verification.routing.key}")
    private String verificationRoutingKey;

    @Value("${rabbitmq.nuvolo.password.reset.routing.key}")
    private String passwordResetRoutingKey;


    @Value("${rabbitmq.nuvolo.notification.routing.key}")
    private String notificationRoutingKey;

}
